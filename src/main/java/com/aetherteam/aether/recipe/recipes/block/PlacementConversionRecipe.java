package com.aetherteam.aether.recipe.recipes.block;

import com.aetherteam.aether.event.hooks.PlacementRecipeHooks;
import com.aetherteam.aether.recipe.AetherRecipeSerializers;
import com.aetherteam.aether.recipe.AetherRecipeTypes;
import com.aetherteam.aether.recipe.serializer.BiomeParameterRecipeSerializer;
import com.aetherteam.aether.recipe.blockstate.BlockPropertyPair;
import com.aetherteam.aether.recipe.blockstate.BlockStateIngredient;
import com.mojang.datafixers.util.Either;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public class PlacementConversionRecipe extends AbstractBiomeParameterRecipe {
    public PlacementConversionRecipe(Optional<Either<ResourceKey<Biome>, TagKey<Biome>>> biome, BlockStateIngredient ingredient, BlockPropertyPair result, Optional<Identifier> function) {
        super(AetherRecipeTypes.PLACEMENT_CONVERSION, biome, ingredient, result, function);
    }

    public PlacementConversionRecipe(BlockStateIngredient ingredient, BlockPropertyPair result, Optional<Identifier> function) {
        this(Optional.empty(), ingredient, result, function);
    }

    /**
     * Replaces an old {@link BlockState} with a new one from {@link com.aetherteam.aether.recipe.blockstate.recipes.AbstractBlockStateRecipe#getResultState(BlockState)}.
     *
     * @param level    The {@link Level} the recipe is performed in.
     * @param pos      The {@link BlockPos} the recipe is performed at.
     * @param oldState The original {@link BlockState} being used that is being checked.
     * @return Whether the new {@link BlockState} was set.
     */
    public boolean convert(Level level, BlockPos pos, BlockState oldState) {
        if (this.matches(level, pos, oldState)) {
            BlockState newState = this.getResultState(oldState);
            PlacementRecipeHooks.banOrConvert(level, pos);
            level.setBlockAndUpdate(pos, newState);
            return true;
        }
        return false;
    }

    @Override
    public RecipeSerializer<PlacementConversionRecipe> getSerializer() {
        return AetherRecipeSerializers.PLACEMENT_CONVERSION;
    }

    public static final class Serializer {
        private Serializer() {
        }

        public static RecipeSerializer<PlacementConversionRecipe> create() {
            return BiomeParameterRecipeSerializer.create(PlacementConversionRecipe::new);
        }
    }
}
