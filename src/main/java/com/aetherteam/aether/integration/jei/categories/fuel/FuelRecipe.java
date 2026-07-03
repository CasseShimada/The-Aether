package com.aetherteam.aether.integration.jei.categories.fuel;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.List;

public record FuelRecipe(List<ItemStack> ingredients, int burnTime, Block station) {
}
