package com.aetherteam.aether.accessories.api;

import com.aetherteam.aether.accessories.api.equip.AccessoryEquipResult;
import com.aetherteam.aether.accessories.api.slot.SlotEntryReference;
import com.aetherteam.aether.accessories.api.slot.SlotReference;
import com.aetherteam.aether.accessories.api.slot.SlotTypeReference;
import com.aetherteam.aether.network.packet.clientbound.AccessorySyncPacket;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Current per-entity Aether accessory storage exposed through {@link AccessoriesAPI}.
 */
public interface AccessoriesStorage {
    @Nullable
    AccessoriesContainer getContainer(SlotTypeReference slotTypeReference);

    @Nullable
    AccessoryEquipResult canEquipAccessory(ItemStack stack, boolean requireEmptySlot);

    @Nullable
    AccessoryEquipResult canEquipAccessory(ItemStack stack, boolean requireEmptySlot, Predicate<SlotReference> slotFilter);

    List<SlotEntryReference> getEquipped(Item item);

    @Nullable
    SlotEntryReference getFirstEquipped(Predicate<ItemStack> predicate);

    List<SlotEntryReference> getAllEquipped(Predicate<ItemStack> predicate);

    List<SlotEntryReference> getAllEquipped();

    @Nullable
    VisibleAccessory getFirstVisible(Predicate<ItemStack> predicate);

    List<VisibleAccessory> getAllVisible(Predicate<ItemStack> predicate);

    @Nullable
    AccessoryMutationResult consumeFirst(Predicate<ItemStack> predicate);

    @Nullable
    AccessoryMutationResult consumeFirst(Predicate<ItemStack> predicate, Predicate<SlotReference> slotFilter);

    @Nullable
    AccessoryMutationResult consumeOne(SlotReference reference);

    @Nullable
    AccessoryMutationResult replaceAccessory(SlotReference reference, ItemStack replacement);

    @Nullable
    AccessoryMutationResult mutateAccessory(SlotReference reference, Consumer<ItemStack> mutation);

    @Nullable
    AccessoryMutationResult commitAccessoryMutation(SlotReference reference, ItemStack previousStack);

    void clearAccessories(boolean clearCosmeticAccessories);

    void process(boolean runAccessoryTick);

    boolean consumeSyncDirty();

    AccessorySyncPacket createSyncPacket();

    void applyClientSync(AccessorySyncPacket packet);

    void clearRuntimeState(boolean invokeUnequipCallbacks);

    void handleImmediateUnequip(SlotReference reference);

    void handleImmediateStackMutation(SlotReference reference);
}
