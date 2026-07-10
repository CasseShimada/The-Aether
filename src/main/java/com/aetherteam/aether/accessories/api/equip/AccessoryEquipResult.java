package com.aetherteam.aether.accessories.api.equip;

import com.aetherteam.aether.accessories.api.slot.SlotReference;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

public record AccessoryEquipResult(SlotReference reference, Consumer<ItemStack> action) {
}
