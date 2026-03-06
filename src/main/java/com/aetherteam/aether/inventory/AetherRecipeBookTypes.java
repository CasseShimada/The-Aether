package com.aetherteam.aether.inventory;

import net.minecraft.world.inventory.RecipeBookType;

public class AetherRecipeBookTypes {
    public static final RecipeBookType ALTAR = resolve("AETHER_ALTAR", RecipeBookType.FURNACE);
    public static final RecipeBookType FREEZER = resolve("AETHER_FREEZER", RecipeBookType.BLAST_FURNACE);
    public static final RecipeBookType INCUBATOR = resolve("AETHER_INCUBATOR", RecipeBookType.SMOKER);

    private static RecipeBookType resolve(String name, RecipeBookType fallback) {
        try {
            return RecipeBookType.valueOf(name);
        } catch (IllegalArgumentException exception) {
            return fallback;
        }
    }
}
