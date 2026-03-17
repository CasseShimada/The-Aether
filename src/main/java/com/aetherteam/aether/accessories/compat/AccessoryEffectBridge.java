package com.aetherteam.aether.accessories.compat;

import com.aetherteam.aether.accessories.api.AccessoriesCapability;
import com.aetherteam.aether.accessories.api.AccessoriesAPI;
import com.aetherteam.aether.accessories.api.events.extra.AllowWalkingOnSnow;
import com.aetherteam.aether.accessories.api.events.extra.PiglinNeutralInducer;
import com.aetherteam.aether.accessories.api.slot.SlotEntryReference;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.component.BlockItemStateProperties;

import java.util.List;
import java.util.function.Predicate;

/**
 * Shared bridge for treating Aether accessory slots as held/equipped equivalents.
 */
public final class AccessoryEffectBridge {
    private static final String TWILIGHT_CHARM_STACK_TAG = "CharmStack";
    private static final String TWILIGHT_CASKET_DAMAGE_TAG = "CasketDamage";

    private AccessoryEffectBridge() {
    }

    public static boolean isHoldingEquivalent(LivingEntity entity, Predicate<ItemStack> predicate) {
        AccessoriesCapability capability = AccessoriesCapability.get(entity);
        if (capability == null) {
            return false;
        }

        for (SlotEntryReference reference : capability.getAllEquipped()) {
            if (predicate.test(reference.stack())) {
                return true;
            }
        }
        return false;
    }

    public static EquipmentSlot resolveVirtualSlot(LivingEntity entity, String slotName, ItemStack stack) {
        EquipmentSlot preferred = resolveEquipmentSlot(entity, stack);
        if (preferred.getType() == EquipmentSlot.Type.HUMANOID_ARMOR || preferred == EquipmentSlot.OFFHAND || preferred == EquipmentSlot.MAINHAND) {
            return preferred;
        }

        if (slotName.endsWith("shield_slot")) {
            return EquipmentSlot.OFFHAND;
        }

        return EquipmentSlot.MAINHAND;
    }

    public static ItemStack findFirstByEquipmentSlot(LivingEntity entity, EquipmentSlot slot) {
        AccessoriesCapability capability = AccessoriesCapability.get(entity);
        if (capability == null) {
            return ItemStack.EMPTY;
        }

        for (SlotEntryReference reference : capability.getAllEquipped()) {
            ItemStack stack = reference.stack();
            if (resolveEquipmentSlot(entity, stack) == slot) {
                return stack;
            }
        }

        return ItemStack.EMPTY;
    }

    public static TriState shouldAllowWalkingOnSnow(LivingEntity entity) {
        return evaluateAccessoryTriState(entity, AllowWalkingOnSnow.class, (effect, stack, reference) -> effect.allowWalkingOnSnow(stack, reference));
    }

    public static TriState shouldMakePiglinsNeutral(LivingEntity entity) {
        return evaluateAccessoryTriState(entity, PiglinNeutralInducer.class, (effect, stack, reference) -> effect.makePiglinsNeutral(stack, reference));
    }

    /**
     * Optional Twilight Forest compatibility path for equipment-slot consumption checks.
     */
    public static boolean consumeAccessoryItem(Player player, EquipmentSlot requestedSlot, ItemLike item, CompoundTag persistentTag, boolean saveItemToTag) {
        AccessoriesCapability capability = AccessoriesCapability.get(player);
        if (capability == null) {
            return false;
        }

        List<SlotEntryReference> equipped = capability.getAllEquipped();
        for (SlotEntryReference reference : equipped) {
            ItemStack stack = reference.stack();
            if (!stack.is(item.asItem())) {
                continue;
            }
            EquipmentSlot resolvedSlot = resolveEquipmentSlot(player, stack);
            if (resolvedSlot != requestedSlot && !(requestedSlot == EquipmentSlot.OFFHAND && resolvedSlot == EquipmentSlot.MAINHAND)) {
                continue;
            }

            if (saveItemToTag) {
                persistentTag.put(TWILIGHT_CHARM_STACK_TAG, saveItem(player, stack));
            }

            BlockItemStateProperties stateProperties = stack.get(DataComponents.BLOCK_STATE);
            if (stateProperties != null && stateProperties.properties().containsKey("breakage")) {
                String value = stateProperties.properties().get("breakage");
                persistentTag.putInt(TWILIGHT_CASKET_DAMAGE_TAG, parseIntOrZero(value));
            }

            stack.shrink(1);
            reference.reference().setStack(stack.isEmpty() ? ItemStack.EMPTY : stack);
            return true;
        }

        return false;
    }

    private static CompoundTag saveItem(Player player, ItemStack stack) {
        RegistryOps<Tag> ops = RegistryOps.create(NbtOps.INSTANCE, player.registryAccess());
        return ItemStack.CODEC.encodeStart(ops, stack)
                .result()
                .filter(CompoundTag.class::isInstance)
                .map(CompoundTag.class::cast)
                .orElseGet(CompoundTag::new);
    }

    private static int parseIntOrZero(String value) {
        if (value == null || value.isEmpty()) {
            return 0;
        }
        for (int i = 0; i < value.length(); i++) {
            if (!Character.isDigit(value.charAt(i))) {
                return 0;
            }
        }
        return Integer.parseInt(value);
    }

    private static EquipmentSlot resolveEquipmentSlot(LivingEntity entity, ItemStack stack) {
        return entity.getEquipmentSlotForItem(stack);
    }

    private static <T> TriState evaluateAccessoryTriState(LivingEntity entity, Class<T> effectClass, TriStateEvaluator<T> evaluator) {
        AccessoriesCapability capability = AccessoriesCapability.get(entity);
        if (capability == null) {
            return TriState.DEFAULT;
        }

        TriState fallback = TriState.DEFAULT;
        for (SlotEntryReference reference : capability.getAllEquipped()) {
            ItemStack stack = reference.stack();
            T effect = effectClass.isInstance(stack.getItem()) ? effectClass.cast(stack.getItem()) : null;
            if (effect == null) {
                var accessory = AccessoriesAPI.getOrDefaultAccessory(stack);
                if (effectClass.isInstance(accessory)) {
                    effect = effectClass.cast(accessory);
                }
            }
            if (effect == null) {
                continue;
            }

            TriState state = evaluator.evaluate(effect, stack, reference.reference());
            if (state == TriState.TRUE) {
                return TriState.TRUE;
            }
            if (state == TriState.FALSE) {
                fallback = TriState.FALSE;
            }
        }

        return fallback;
    }

    @FunctionalInterface
    private interface TriStateEvaluator<T> {
        TriState evaluate(T effect, ItemStack stack, com.aetherteam.aether.accessories.api.slot.SlotReference reference);
    }
}
