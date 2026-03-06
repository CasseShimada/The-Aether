package com.aetherteam.aether.accessories.api.core;

import com.aetherteam.aether.accessories.api.SoundEventData;
import com.aetherteam.aether.accessories.api.attributes.AccessoryAttributeBuilder;
import com.aetherteam.aether.accessories.api.slot.SlotReference;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface Accessory {
    default boolean canEquipFromUse(ItemStack stack, SlotReference reference) {
        return true;
    }

    default boolean canEquip(ItemStack stack, SlotReference reference) {
        return true;
    }

    default void onEquipFromUse(ItemStack stack, SlotReference reference) {
    }

    default void tick(ItemStack stack, SlotReference reference) {
    }

    default void onUnequip(ItemStack stack, SlotReference reference) {
    }

    default void getDynamicModifiers(ItemStack stack, SlotReference reference, AccessoryAttributeBuilder builder) {
    }

    @Nullable
    default SoundEventData getEquipSound(ItemStack stack, SlotReference reference) {
        return null;
    }
}
