package com.aetherteam.aether.recipe.book;

import com.aetherteam.aether.Aether;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeBookCategory;

public final class AetherRecipeBookCategories {
    public static final RecipeBookCategory ENCHANTING_FOOD = Registry.register(
            BuiltInRegistries.RECIPE_BOOK_CATEGORY,
            Identifier.fromNamespaceAndPath(Aether.MODID, "enchanting_food"),
            new RecipeBookCategory());
    public static final RecipeBookCategory ENCHANTING_BLOCKS = Registry.register(
            BuiltInRegistries.RECIPE_BOOK_CATEGORY,
            Identifier.fromNamespaceAndPath(Aether.MODID, "enchanting_blocks"),
            new RecipeBookCategory());
    public static final RecipeBookCategory ENCHANTING_MISC = Registry.register(
            BuiltInRegistries.RECIPE_BOOK_CATEGORY,
            Identifier.fromNamespaceAndPath(Aether.MODID, "enchanting_misc"),
            new RecipeBookCategory());
    public static final RecipeBookCategory ENCHANTING_REPAIR = Registry.register(
            BuiltInRegistries.RECIPE_BOOK_CATEGORY,
            Identifier.fromNamespaceAndPath(Aether.MODID, "enchanting_repair"),
            new RecipeBookCategory());

    public static final RecipeBookCategory FREEZABLE_BLOCKS = Registry.register(
            BuiltInRegistries.RECIPE_BOOK_CATEGORY,
            Identifier.fromNamespaceAndPath(Aether.MODID, "freezable_blocks"),
            new RecipeBookCategory());
    public static final RecipeBookCategory FREEZABLE_MISC = Registry.register(
            BuiltInRegistries.RECIPE_BOOK_CATEGORY,
            Identifier.fromNamespaceAndPath(Aether.MODID, "freezable_misc"),
            new RecipeBookCategory());

    public static final RecipeBookCategory INCUBATION_MISC = Registry.register(
            BuiltInRegistries.RECIPE_BOOK_CATEGORY,
            Identifier.fromNamespaceAndPath(Aether.MODID, "incubation_misc"),
            new RecipeBookCategory());

    public static final RecipeBookCategory ENCHANTING_SEARCH = new RecipeBookCategory();
    public static final RecipeBookCategory FREEZABLE_SEARCH = new RecipeBookCategory();
    public static final RecipeBookCategory INCUBATION_SEARCH = new RecipeBookCategory();

    private AetherRecipeBookCategories() {
    }

    public static void bootstrap() {
    }
}
