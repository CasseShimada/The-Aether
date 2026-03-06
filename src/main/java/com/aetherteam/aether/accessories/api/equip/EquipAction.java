package com.aetherteam.aether.accessories.api.equip;

import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

@FunctionalInterface
public interface EquipAction {
    void equipStack(ItemStack stack);

    static EquipAction of(Consumer<ItemStack> consumer) {
        return consumer::accept;
    }
}
