package com.aetherteam.aether.integration.jei;

import com.aetherteam.aether.recipe.AetherRecipeTypes;
import com.aetherteam.aether.recipe.recipes.item.AbstractAetherCookingRecipe;
import com.aetherteam.aether.recipe.recipes.item.AltarRepairRecipe;
import com.aetherteam.aether.recipe.recipes.item.EnchantingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.ArrayList;
import java.util.List;

final class AetherJeiRecipeCollector {
    private AetherJeiRecipeCollector() {
    }

    static List<? extends RecipeHolder<?>> getAllRecipes(RecipeManager recipeManager) {
        return recipeManager.getRecipes().stream().toList();
    }

    static List<EnchantingRecipe> getEnchantingRecipes(List<? extends RecipeHolder<?>> allRecipes) {
        List<EnchantingRecipe> enchantingRecipes = new ArrayList<>();
        getEnchantingRecipeHolders(allRecipes).stream()
                .filter(recipe -> recipe.value() instanceof EnchantingRecipe)
                .forEach(recipe -> enchantingRecipes.add((EnchantingRecipe) recipe.value()));
        return enchantingRecipes;
    }

    static List<AltarRepairRecipe> getRepairRecipes(List<? extends RecipeHolder<?>> allRecipes) {
        List<AltarRepairRecipe> repairRecipes = new ArrayList<>();
        getEnchantingRecipeHolders(allRecipes).stream()
                .filter(recipe -> recipe.value() instanceof AltarRepairRecipe)
                .forEach(recipe -> repairRecipes.add((AltarRepairRecipe) recipe.value()));
        return repairRecipes;
    }

    static <T> List<T> getRecipes(List<? extends RecipeHolder<?>> allRecipes, Class<T> recipeClass) {
        return allRecipes.stream().map(RecipeHolder::value).filter(recipeClass::isInstance).map(recipeClass::cast).toList();
    }

    @SuppressWarnings("unchecked")
    private static List<? extends RecipeHolder<? extends AbstractAetherCookingRecipe>> getEnchantingRecipeHolders(List<? extends RecipeHolder<?>> allRecipes) {
        return allRecipes.stream()
                .filter(holder -> holder.value().getType() == AetherRecipeTypes.ENCHANTING)
                .map(holder -> (RecipeHolder<? extends AbstractAetherCookingRecipe>) holder)
                .toList();
    }
}
