package com.aetherteam.aether.integration.jei.categories.fuel;

import com.aetherteam.aether.block.AetherBlocks;
import com.aetherteam.aether.data.resources.registries.AetherDataMaps;
import com.aetherteam.nitrogen.integration.jei.categories.fuel.FuelRecipe;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public final class AetherFuelRecipeMaker {
    private AetherFuelRecipeMaker() {
    }

    public static List<FuelRecipe> getFuelRecipes() {
        List<FuelRecipe> fuelRecipes = new ArrayList<>();
        AetherDataMaps.forEachAltarFuel((item, burnTime) -> fuelRecipes.add(new FuelRecipe(List.of(new ItemStack(item)), burnTime, AetherBlocks.ALTAR.get())));
        AetherDataMaps.forEachFreezerFuel((item, burnTime) -> fuelRecipes.add(new FuelRecipe(List.of(new ItemStack(item)), burnTime, AetherBlocks.FREEZER.get())));
        AetherDataMaps.forEachIncubatorFuel((item, burnTime) -> fuelRecipes.add(new FuelRecipe(List.of(new ItemStack(item)), burnTime, AetherBlocks.INCUBATOR.get())));
        return fuelRecipes;
    }
}
