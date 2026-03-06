package com.aetherteam.aether.recipe.recipes.item;

import com.aetherteam.aether.recipe.AetherBookCategory;
import com.aetherteam.aether.recipe.book.AetherRecipeBookCategories;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeType;

public abstract class AbstractAetherCookingRecipe extends AbstractCookingRecipe {
    private final RecipeType<? extends AbstractAetherCookingRecipe> recipeType;
    private final AetherBookCategory category;

    public AbstractAetherCookingRecipe(RecipeType<? extends AbstractAetherCookingRecipe> recipeType, String group, AetherBookCategory category, Ingredient ingredient, ItemStack result, float experience, int cookingTime) {
        super(group, getBookCategory(category), ingredient, result, experience, cookingTime);
        this.recipeType = recipeType;
        this.category = category;
    }

    private static CookingBookCategory getBookCategory(AetherBookCategory category) {
        return switch (category) {
            case ENCHANTING_FOOD -> CookingBookCategory.FOOD;
            case ENCHANTING_BLOCKS, FREEZABLE_BLOCKS -> CookingBookCategory.BLOCKS;
            default -> CookingBookCategory.MISC;
        };
    }

    @Override
    public RecipeType<? extends AbstractAetherCookingRecipe> getType() {
        return this.recipeType;
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return switch (this.category) {
            case ENCHANTING_FOOD -> AetherRecipeBookCategories.ENCHANTING_FOOD.get();
            case ENCHANTING_BLOCKS -> AetherRecipeBookCategories.ENCHANTING_BLOCKS.get();
            case ENCHANTING_MISC -> AetherRecipeBookCategories.ENCHANTING_MISC.get();
            case ENCHANTING_REPAIR -> AetherRecipeBookCategories.ENCHANTING_REPAIR.get();
            case FREEZABLE_BLOCKS -> AetherRecipeBookCategories.FREEZABLE_BLOCKS.get();
            case FREEZABLE_MISC -> AetherRecipeBookCategories.FREEZABLE_MISC.get();
            default -> RecipeBookCategories.CRAFTING_MISC;
        };
    }

    public ItemStack getResult() {
        return this.result();
    }

    public AetherBookCategory aetherCategory() {
        return this.category;
    }
}
