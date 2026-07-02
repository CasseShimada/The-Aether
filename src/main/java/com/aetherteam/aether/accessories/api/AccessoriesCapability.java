package com.aetherteam.aether.accessories.api;

import com.aetherteam.aether.accessories.api.attributes.AccessoryAttributeBuilder;
import com.aetherteam.aether.accessories.compat.AccessoryEffectBridge;
import com.aetherteam.aether.accessories.api.core.Accessory;
import com.aetherteam.aether.accessories.api.equip.EquipAction;
import com.aetherteam.aether.accessories.api.slot.SlotEntryReference;
import com.aetherteam.aether.accessories.api.slot.SlotReference;
import com.aetherteam.aether.accessories.api.slot.SlotType;
import com.aetherteam.aether.accessories.api.slot.SlotTypeReference;
import com.aetherteam.aether.accessories.impl.AccessoriesState;
import com.aetherteam.aether.attachment.AccessoryInventoryAttachment;
import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.network.packet.clientbound.AccessorySyncPacket;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerLevel;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

public class AccessoriesCapability implements AccessoriesContainerOwner {
    private final LivingEntity entity;
    private final Map<String, AccessoriesContainer> containers = new LinkedHashMap<>();
    private final Map<String, ItemStack> previousEquipped = new HashMap<>();
    private final Map<String, List<ScopedModifier>> appliedModifiers = new HashMap<>();

    private boolean initializedFromAttachment;
    private boolean syncDirty;
    private boolean processing;

    AccessoriesCapability(LivingEntity entity) {
        this.entity = entity;
    }

    /**
     * @deprecated Use {@link AccessoriesAPI#getAccessories(LivingEntity)} so callers do not depend on the legacy capability-shaped type name.
     */
    @Nullable
    @Deprecated(forRemoval = false)
    public static AccessoriesCapability get(LivingEntity entity) {
        return EntityAccessories.getCapability(entity);
    }

    /**
     * @deprecated Use {@link AccessoriesAPI#evictAccessories(LivingEntity)} so lifecycle code goes through the accessory API boundary.
     */
    @Deprecated(forRemoval = false)
    public static void evict(LivingEntity entity) {
        EntityAccessories.evict(entity);
    }

    synchronized void ensureReady() {
        this.ensureContainers();
    }

    public synchronized AccessoriesContainer getContainer(SlotTypeReference slotTypeReference) {
        if (slotTypeReference == null) {
            return null;
        }
        this.ensureContainers();
        return this.getOrCreateContainer(slotTypeReference.slotName(), 1);
    }

    @Nullable
    public synchronized Pair<SlotReference, EquipAction> canEquipAccessory(ItemStack stack, boolean requireEmptySlot) {
        return this.canEquipAccessory(stack, requireEmptySlot, reference -> true);
    }

    @Nullable
    public synchronized Pair<SlotReference, EquipAction> canEquipAccessory(ItemStack stack, boolean requireEmptySlot, Predicate<SlotReference> slotFilter) {
        List<SlotType> validSlots = AccessoriesAPI.getValidSlotTypes(this.entity, stack);
        for (SlotType slotType : validSlots) {
            AccessoriesContainer container = this.getOrCreateContainer(slotType.name(), slotType.size());
            for (int slotIndex = 0; slotIndex < container.getAccessories().getContainerSize(); slotIndex++) {
                ItemStack existing = container.getAccessories().getItem(slotIndex);
                if (!requireEmptySlot || existing.isEmpty()) {
                    int index = slotIndex;
                    SlotReference reference = SlotReference.of(this.entity, slotType.name(), index);
                    if (!slotFilter.test(reference)) {
                        continue;
                    }
                    EquipAction action = EquipAction.of(equippedStack -> container.getAccessories().setItem(index, equippedStack));
                    return Pair.of(reference, action);
                }
            }
        }
        return null;
    }

    public synchronized List<SlotEntryReference> getEquipped(Item item) {
        List<SlotEntryReference> equipped = new ArrayList<>();
        for (SlotEntryReference reference : this.getAllEquipped()) {
            if (reference.stack().is(item)) {
                equipped.add(reference);
            }
        }
        return equipped;
    }

    public synchronized SlotEntryReference getFirstEquipped(Predicate<ItemStack> predicate) {
        for (SlotEntryReference reference : this.getAllEquipped()) {
            if (predicate.test(reference.stack())) {
                return reference;
            }
        }
        return null;
    }

    public synchronized List<SlotEntryReference> getAllEquipped() {
        this.ensureContainers();
        List<SlotEntryReference> references = new ArrayList<>();
        for (Map.Entry<String, AccessoriesContainer> entry : this.containers.entrySet()) {
            String slotName = entry.getKey();
            AccessoriesContainer container = entry.getValue();
            for (int slotIndex = 0; slotIndex < container.getAccessories().getContainerSize(); slotIndex++) {
                if (!container.getAccessories().getItem(slotIndex).isEmpty()) {
                    references.add(new SlotEntryReference(SlotReference.of(this.entity, slotName, slotIndex)));
                }
            }
        }
        return references;
    }

    public synchronized void clearAccessories(boolean clearCosmeticAccessories) {
        for (SlotEntryReference reference : List.copyOf(this.getAllEquipped())) {
            AccessoriesAPI.breakStack(reference.reference());
        }

        if (!clearCosmeticAccessories) {
            return;
        }

        this.ensureContainers();
        for (AccessoriesState.SlotDefinition definition : AccessoriesState.slots()) {
            AccessoriesContainer container = this.containers.get(definition.type().name());
            if (container == null) {
                continue;
            }

            for (int slotIndex = 0; slotIndex < container.getCosmeticAccessories().getContainerSize(); slotIndex++) {
                if (!container.getCosmeticAccessories().getItem(slotIndex).isEmpty()) {
                    container.getCosmeticAccessories().setItem(slotIndex, ItemStack.EMPTY);
                }
            }
        }
    }

    public synchronized void process(boolean runAccessoryTick) {
        if (this.processing) {
            return;
        }
        this.processing = true;
        try {
            this.ensureContainers();

            Map<String, SlotReference> references = new HashMap<>();
            Map<String, ItemStack> currentEquipped = this.captureEquippedCopies(references);
            Set<String> allKeys = new HashSet<>(this.previousEquipped.keySet());
            allKeys.addAll(currentEquipped.keySet());

            for (String key : allKeys) {
                ItemStack previous = this.previousEquipped.getOrDefault(key, ItemStack.EMPTY);
                ItemStack current = currentEquipped.getOrDefault(key, ItemStack.EMPTY);
                if (!sameStack(previous, current)) {
                    if (!previous.isEmpty()) {
                        AccessoriesAPI.getOrDefaultAccessory(previous).onUnequip(previous.copy(), references.computeIfAbsent(key, this::slotReferenceFromKey));
                    }
                }
            }

            if (this.isServerSide()) {
                this.clearDynamicModifiers();
            }

            for (Map.Entry<String, ItemStack> entry : currentEquipped.entrySet()) {
                String key = entry.getKey();
                SlotReference reference = references.computeIfAbsent(key, this::slotReferenceFromKey);
                ItemStack liveStack = reference.getStack();
                Accessory accessory = AccessoriesAPI.getOrDefaultAccessory(liveStack);
                if (runAccessoryTick) {
                    accessory.tick(liveStack, reference);
                }
                if (this.isServerSide()) {
                    this.tickHeldEquippedItem(liveStack, reference);
                    this.applyDynamicModifiers(key, liveStack, reference, accessory);
                }
            }

            Map<String, ItemStack> currentAfterTick = this.captureEquippedCopies(null);
            boolean stackChanged = !sameEquippedState(this.previousEquipped, currentAfterTick);
            this.previousEquipped.clear();
            this.previousEquipped.putAll(currentAfterTick);

            if (stackChanged) {
                this.persistToAttachment();
                if (this.isServerSide()) {
                    this.syncDirty = true;
                }
            }
        } finally {
            this.processing = false;
        }
    }

    public synchronized void onContainerChanged(String slotName) {
        this.ensureContainers();
        this.persistToAttachment();
        if (this.isServerSide()) {
            this.syncDirty = true;
        }
    }

    public synchronized boolean consumeSyncDirty() {
        boolean dirty = this.syncDirty;
        this.syncDirty = false;
        return dirty;
    }

    public synchronized AccessorySyncPacket createSyncPacket() {
        this.ensureContainers();
        List<AccessorySyncPacket.SlotData> data = new ArrayList<>();
        for (Map.Entry<String, AccessoriesContainer> entry : this.containers.entrySet()) {
            AccessoriesContainer container = entry.getValue();
            data.add(new AccessorySyncPacket.SlotData(
                    entry.getKey(),
                    container.equippedCopies(),
                    container.cosmeticCopies(),
                    container.renderFlagsCopy()
            ));
        }
        return new AccessorySyncPacket(this.entity.getId(), data);
    }

    public synchronized void applyClientSync(AccessorySyncPacket packet) {
        this.processing = true;
        try {
            for (AccessorySyncPacket.SlotData slotData : packet.slots()) {
                AccessoriesContainer container = this.getOrCreateContainer(slotData.slotName(), Math.max(1, slotData.equipped().size()));
                container.load(slotData.equipped(), slotData.cosmetic(), slotData.renderFlags());
            }
            this.previousEquipped.clear();
            this.previousEquipped.putAll(this.captureEquippedCopies(null));
            this.initializedFromAttachment = true;
        } finally {
            this.processing = false;
        }
    }

    public synchronized void clearRuntimeState(boolean invokeUnequipCallbacks) {
        if (invokeUnequipCallbacks) {
            for (Map.Entry<String, ItemStack> entry : this.previousEquipped.entrySet()) {
                ItemStack stack = entry.getValue();
                if (!stack.isEmpty()) {
                    AccessoriesAPI.getOrDefaultAccessory(stack).onUnequip(stack.copy(), this.slotReferenceFromKey(entry.getKey()));
                }
            }
        }
        this.clearDynamicModifiers();
        this.previousEquipped.clear();
        this.syncDirty = false;
    }

    public synchronized void handleImmediateUnequip(SlotReference reference) {
        String key = slotKey(reference.slotName(), reference.slot());
        this.previousEquipped.remove(key);
        this.removeDynamicModifiersForSlot(key);
        this.persistToAttachment();
        if (this.isServerSide()) {
            this.syncDirty = true;
        }
    }

    public synchronized void handleImmediateStackMutation(SlotReference reference) {
        String key = slotKey(reference.slotName(), reference.slot());
        ItemStack current = reference.getStack();
        if (current.isEmpty()) {
            this.previousEquipped.remove(key);
        } else {
            this.previousEquipped.put(key, current.copy());
        }
        this.persistToAttachment();
        if (this.isServerSide()) {
            this.syncDirty = true;
        }
    }

    private synchronized void ensureContainers() {
        for (AccessoriesState.SlotDefinition definition : AccessoriesState.slots()) {
            this.containers.computeIfAbsent(definition.type().name(), key -> AccessoriesContainer.create(this, definition.type()));
        }

        if (!this.initializedFromAttachment) {
            AccessoryInventoryAttachment attachment = this.entity.getAttachedOrCreate(AetherDataAttachments.ACCESSORY_INVENTORY);
            for (String slotName : attachment.slotNames()) {
                AccessoryInventoryAttachment.SlotStorage storage = attachment.getOrCreateSlot(slotName, 1);
                this.getOrCreateContainer(slotName, storage.size());
            }

            for (Map.Entry<String, AccessoriesContainer> entry : this.containers.entrySet()) {
                this.loadContainerFromAttachment(entry.getKey(), entry.getValue(), attachment);
            }

            this.initializedFromAttachment = true;
        }
    }

    private synchronized AccessoriesContainer getOrCreateContainer(String slotName, int fallbackSize) {
        AccessoriesContainer container = this.containers.get(slotName);
        if (container != null) {
            return container;
        }

        AccessoriesState.SlotDefinition definition = AccessoriesState.getSlot(slotName);
        SlotType slotType;
        if (definition != null) {
            slotType = definition.type();
        } else {
            String normalized = slotName.replace(':', '.');
            slotType = new SlotType(slotName, Math.max(1, fallbackSize), "slot." + normalized);
        }

        AccessoriesContainer created = AccessoriesContainer.create(this, slotType);
        this.containers.put(slotName, created);
        return created;
    }

    private void loadContainerFromAttachment(String slotName, AccessoriesContainer container, AccessoryInventoryAttachment attachment) {
        AccessoryInventoryAttachment.SlotStorage storage = attachment.getOrCreateSlot(slotName, container.getAccessories().getContainerSize())
                .normalizedSize(container.getAccessories().getContainerSize());
        container.load(storage.equippedList(), storage.cosmeticList(), storage.renderFlags());
    }

    private void persistToAttachment() {
        AccessoryInventoryAttachment attachment = this.entity.getAttachedOrCreate(AetherDataAttachments.ACCESSORY_INVENTORY);
        for (Map.Entry<String, AccessoriesContainer> entry : this.containers.entrySet()) {
            AccessoriesContainer container = entry.getValue();
            attachment.putSlot(entry.getKey(), AccessoryInventoryAttachment.SlotStorage.fromContainers(
                    container.equippedCopies(),
                    container.cosmeticCopies(),
                    container.renderFlagsCopy()
            ));
        }
        attachment.retainSlots(this.containers.keySet());
    }

    private Map<String, ItemStack> captureEquippedCopies(@Nullable Map<String, SlotReference> references) {
        Map<String, ItemStack> equipped = new HashMap<>();
        for (Map.Entry<String, AccessoriesContainer> entry : this.containers.entrySet()) {
            String slotName = entry.getKey();
            AccessoriesContainer container = entry.getValue();
            for (int i = 0; i < container.getAccessories().getContainerSize(); i++) {
                ItemStack stack = container.getAccessories().getItem(i);
                if (!stack.isEmpty()) {
                    String key = slotKey(slotName, i);
                    equipped.put(key, stack.copy());
                    if (references != null) {
                        references.put(key, SlotReference.of(this.entity, slotName, i));
                    }
                }
            }
        }
        return equipped;
    }

    private void applyDynamicModifiers(String slotKey, ItemStack stack, SlotReference reference, Accessory accessory) {
        AccessoryAttributeBuilder builder = new AccessoryAttributeBuilder();
        accessory.getDynamicModifiers(stack, reference, builder);

        if (builder.entries().isEmpty()) {
            return;
        }

        List<ScopedModifier> scoped = new ArrayList<>();
        for (AccessoryAttributeBuilder.Entry entry : builder.entries()) {
            Holder<Attribute> attribute = entry.attribute();
            AttributeInstance instance = this.entity.getAttribute(attribute);
            if (instance == null) {
                continue;
            }

            AttributeModifier modifier = entry.modifier();
            Identifier scopedId = scopedModifierId(modifier.id(), slotKey);
            AttributeModifier scopedModifier = new AttributeModifier(scopedId, modifier.amount(), modifier.operation());
            instance.removeModifier(scopedModifier.id());
            instance.addOrUpdateTransientModifier(scopedModifier);
            scoped.add(new ScopedModifier(attribute, scopedModifier));
        }

        if (!scoped.isEmpty()) {
            this.appliedModifiers.put(slotKey, scoped);
        }
    }

    private void tickHeldEquippedItem(ItemStack stack, SlotReference reference) {
        if (!(this.entity.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        EquipmentSlot virtualSlot = AccessoryEffectBridge.resolveVirtualSlot(this.entity, reference.slotName(), stack);
        stack.getItem().inventoryTick(stack, serverLevel, this.entity, virtualSlot);
    }

    private void clearDynamicModifiers() {
        for (String slotKey : new ArrayList<>(this.appliedModifiers.keySet())) {
            this.removeDynamicModifiersForSlot(slotKey);
        }
    }

    private void removeDynamicModifiersForSlot(String slotKey) {
        List<ScopedModifier> modifiers = this.appliedModifiers.remove(slotKey);
        if (modifiers == null) {
            return;
        }

        for (ScopedModifier scoped : modifiers) {
            AttributeInstance instance = this.entity.getAttribute(scoped.attribute());
            if (instance != null) {
                instance.removeModifier(scoped.modifier().id());
            }
        }
    }

    private SlotReference slotReferenceFromKey(String slotKey) {
        int separator = slotKey.lastIndexOf('#');
        if (separator <= 0 || separator == slotKey.length() - 1) {
            return SlotReference.of(this.entity, slotKey, 0);
        }
        String slotName = slotKey.substring(0, separator);
        int slot = Integer.parseInt(slotKey.substring(separator + 1));
        return SlotReference.of(this.entity, slotName, slot);
    }

    private boolean isServerSide() {
        return !this.entity.level().isClientSide();
    }

    private static Identifier scopedModifierId(Identifier baseId, String slotKey) {
        String suffix = Integer.toHexString(slotKey.hashCode());
        return Identifier.fromNamespaceAndPath(baseId.getNamespace(), baseId.getPath() + "_aether_slot_" + suffix);
    }

    private static String slotKey(String slotName, int slot) {
        return slotName + "#" + slot;
    }

    private static boolean sameStack(ItemStack first, ItemStack second) {
        return ItemStack.isSameItemSameComponents(first, second) && first.getCount() == second.getCount();
    }

    private static boolean sameEquippedState(Map<String, ItemStack> previous, Map<String, ItemStack> current) {
        if (previous.size() != current.size()) {
            return false;
        }

        for (Map.Entry<String, ItemStack> entry : previous.entrySet()) {
            ItemStack stack = current.get(entry.getKey());
            if (stack == null || !sameStack(entry.getValue(), stack)) {
                return false;
            }
        }

        return true;
    }

    private record ScopedModifier(Holder<Attribute> attribute, AttributeModifier modifier) {
    }
}
