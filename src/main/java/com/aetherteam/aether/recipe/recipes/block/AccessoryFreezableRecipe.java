package com.aetherteam.aether.recipe.recipes.block;

import com.aetherteam.aether.recipe.AetherRecipeSerializers;
import com.aetherteam.aether.recipe.AetherRecipeTypes;
import com.aetherteam.aether.recipe.blockstate.BlockPropertyPair;
import com.aetherteam.aether.recipe.blockstate.BlockStateIngredient;
import com.aetherteam.aether.recipe.blockstate.input.BlockStateRecipeInput;
import com.aetherteam.aether.recipe.blockstate.recipes.AbstractBlockStateRecipe;
import com.aetherteam.aether.recipe.blockstate.serializer.BlockStateRecipeSerializer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.Optional;

public class AccessoryFreezableRecipe extends AbstractBlockStateRecipe {
    public AccessoryFreezableRecipe(BlockStateIngredient ingredient, BlockPropertyPair result, Optional<Identifier> function) {
        super(AetherRecipeTypes.ACCESSORY_FREEZABLE, ingredient, result, function);
    }

    @Override
    public RecipeSerializer<AccessoryFreezableRecipe> getSerializer() {
        return AetherRecipeSerializers.ACCESSORY_FREEZABLE;
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    @SuppressWarnings("unchecked")
    public RecipeType<? extends Recipe<BlockStateRecipeInput>> getType() {
        return (RecipeType<? extends Recipe<BlockStateRecipeInput>>) super.getType();
    }

    public static final class Serializer {
        private Serializer() {
        }

        public static RecipeSerializer<AccessoryFreezableRecipe> create() {
            return BlockStateRecipeSerializer.create(AccessoryFreezableRecipe::new);
        }
    }
}
