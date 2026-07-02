package com.aetherteam.aether.recipe.recipes.block;

import com.aetherteam.nitrogen.recipe.BlockStateRecipeUtil;
import net.minecraft.commands.CacheableFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.Optional;

public interface MatchEventRecipe {
    /**
     * Replaces an old {@link BlockState} with a new one. Also executes a mcfunction if the recipe has one.
     *
     * @param level    The {@link Level} the recipe is performed in.
     * @param pos      The {@link BlockPos} the recipe is performed at.
     * @param newState The resulting {@link BlockState} from the recipe.
     * @param function The {@link CacheableFunction} to run when the recipe is performed.
     * @return Whether the new {@link BlockState} was set.
     */
    default boolean convert(Level level, BlockPos pos, BlockState newState, Optional<CacheableFunction> function) {
        level.setBlockAndUpdate(pos, newState);
        BlockStateRecipeUtil.executeFunction(level, pos, function);
        return true;
    }

    /**
     * Allows shared item-use conversion recipes to keep a common match hook.
     *
     * @param player   The {@link Player} performing the recipe.
     * @param level    The {@link Level} the recipe is performed in.
     * @param pos      The {@link BlockPos} the recipe is performed at.
     * @param stack    The {@link ItemStack} being used to perform the recipe.
     * @param oldState The original {@link BlockState} being interacted with.
     * @param newState The resulting {@link BlockState} from the recipe.
     * @return Whether conversion should continue.
     */
    default boolean matches(@Nullable Player player, Level level, BlockPos pos, @Nullable ItemStack stack, BlockState oldState, BlockState newState, RecipeType<?> recipeType) {
        return true;
    }
}
