package com.aetherteam.nitrogen.recipe.recipes;

import com.aetherteam.nitrogen.recipe.BlockPropertyPair;
import com.aetherteam.nitrogen.recipe.BlockStateIngredient;
import com.aetherteam.nitrogen.recipe.input.BlockStateRecipeInput;
import net.minecraft.commands.CacheableFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public interface BlockStateRecipe extends Recipe<BlockStateRecipeInput> {
    BlockStateIngredient getIngredient();

    BlockPropertyPair getResult();

    Optional<Identifier> getFunctionId();

    Optional<CacheableFunction> getFunction();

    boolean matches(Level level, BlockPos pos, BlockState state);

    BlockState getResultState(BlockState oldState);
}
