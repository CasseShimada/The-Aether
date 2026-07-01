package com.aetherteam.aether.recipe.book;

import com.aetherteam.aether.Aether;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeBookCategory;

public class AetherRecipeBookCategories {
    public static final RecipeBookCategory ENCHANTING_FOOD = register("enchanting_food");
    public static final RecipeBookCategory ENCHANTING_BLOCKS = register("enchanting_blocks");
    public static final RecipeBookCategory ENCHANTING_MISC = register("enchanting_misc");
    public static final RecipeBookCategory ENCHANTING_REPAIR = register("enchanting_repair");

    public static final RecipeBookCategory FREEZABLE_BLOCKS = register("freezable_blocks");
    public static final RecipeBookCategory FREEZABLE_MISC = register("freezable_misc");

    public static final RecipeBookCategory INCUBATION_MISC = register("incubation_misc");

    public static final RecipeBookCategory ENCHANTING_SEARCH = new RecipeBookCategory();
    public static final RecipeBookCategory FREEZABLE_SEARCH = new RecipeBookCategory();
    public static final RecipeBookCategory INCUBATION_SEARCH = new RecipeBookCategory();

    private static RecipeBookCategory register(String name) {
        return Registry.register(BuiltInRegistries.RECIPE_BOOK_CATEGORY, Identifier.fromNamespaceAndPath(Aether.MODID, name), new RecipeBookCategory());
    }
}
