package com.aetherteam.aether.recipe.recipes.item;

import com.aetherteam.aether.block.AetherBlocks;
import com.aetherteam.aether.recipe.AetherBookCategory;
import com.aetherteam.aether.recipe.AetherRecipeSerializers;
import com.aetherteam.aether.recipe.AetherRecipeTypes;
import com.aetherteam.aether.recipe.serializer.AetherCookingSerializer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class FreezingRecipe extends AbstractAetherCookingRecipe {
    public FreezingRecipe(String group, AetherBookCategory category, Ingredient ingredient, ItemStack result, float experience, int freezingTime) {
        super(AetherRecipeTypes.FREEZING.get(), group, category, ingredient, result, experience, freezingTime);
    }

    public FreezingRecipe(String group, AetherBookCategory category, Ingredient ingredient, ItemStackTemplate result, float experience, int freezingTime) {
        super(AetherRecipeTypes.FREEZING.get(), group, category, ingredient, result, experience, freezingTime);
    }

    @Override
    protected Item furnaceIcon() {
        return AetherBlocks.FREEZER.get().asItem();
    }

    @Override
    public RecipeSerializer<FreezingRecipe> getSerializer() {
        return AetherRecipeSerializers.FREEZING.get();
    }

    public static final class Serializer {
        private Serializer() {
        }

        public static RecipeSerializer<FreezingRecipe> create() {
            return AetherCookingSerializer.create(FreezingRecipe::new, 800);
        }
    }
}
