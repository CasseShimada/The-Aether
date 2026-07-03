package com.aetherteam.aether.recipe.recipes.ban;

import com.aetherteam.aether.event.hooks.PlacementRecipeHooks;
import com.aetherteam.aether.recipe.AetherRecipeSerializers;
import com.aetherteam.aether.recipe.AetherRecipeTypes;
import com.aetherteam.aether.recipe.serializer.PlacementBanRecipeSerializer;
import com.aetherteam.aether.recipe.blockstate.BlockStateIngredient;
import com.aetherteam.aether.recipe.blockstate.BlockStateRecipeUtil;
import com.aetherteam.aether.recipe.blockstate.input.BlockStateRecipeInput;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public class BlockBanRecipe extends AbstractPlacementBanRecipe<BlockState, BlockStateIngredient, BlockStateRecipeInput> {
    public BlockBanRecipe(Either<ResourceKey<Biome>, TagKey<Biome>> biome, Optional<BlockStateIngredient> bypassBlock, BlockStateIngredient ingredient) {
        super(AetherRecipeTypes.BLOCK_PLACEMENT_BAN, biome, bypassBlock, ingredient);
    }

    public BlockBanRecipe(Either<ResourceKey<Biome>, TagKey<Biome>> biome, Optional<BlockStateIngredient> bypassBlock) {
        this(biome, bypassBlock, BlockStateIngredient.EMPTY);
    }

    /**
     * Checks if the recipe matches the given parameters using {@link AbstractPlacementBanRecipe#matches(Level, BlockPos, Object)}.<br><br>
     * Then calls {@link PlacementRecipeHooks#banOrConvert} to spawn particles on block ban.
     *
     * @param level The {@link Level} the recipe is performed in.
     * @param pos   The {@link BlockPos} the recipe is performed at.
     * @param state The {@link BlockState} being used that is being checked.
     * @return Whether the given {@link BlockState} is banned from placement.
     */
    public boolean banBlock(Level level, BlockPos pos, BlockState state) {
        if (this.matches(level, pos.below(), state)) {
            PlacementRecipeHooks.banOrConvert(level, pos);
            return true;
        }
        return false;
    }

    @Override
    public RecipeSerializer<BlockBanRecipe> getSerializer() {
        return AetherRecipeSerializers.BLOCK_PLACEMENT_BAN;
    }

    public static final class Serializer {
        private static final MapCodec<BlockBanRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                BlockStateRecipeUtil.KEY_CODEC.fieldOf("biome").forGetter(BlockBanRecipe::getBiome),
                BlockStateIngredient.CODEC.optionalFieldOf("bypass").forGetter(BlockBanRecipe::getBypassBlock),
                BlockStateIngredient.CODEC.fieldOf("ingredient").forGetter(BlockBanRecipe::getIngredient)
        ).apply(inst, BlockBanRecipe::new));

        private Serializer() {
        }

        public static RecipeSerializer<BlockBanRecipe> create() {
            return PlacementBanRecipeSerializer.create(CODEC, StreamCodec.of(Serializer::toNetwork, Serializer::fromNetwork));
        }

        private static BlockBanRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            Either<ResourceKey<Biome>, TagKey<Biome>> biome = PlacementBanRecipeSerializer.readBiome(buffer);
            Optional<BlockStateIngredient> bypassBlock = PlacementBanRecipeSerializer.readBypass(buffer);
            BlockStateIngredient ingredient = BlockStateIngredient.CONTENTS_STREAM_CODEC.decode(buffer);
            return new BlockBanRecipe(biome, bypassBlock, ingredient);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buffer, BlockBanRecipe recipe) {
            PlacementBanRecipeSerializer.writeBase(buffer, recipe);
            BlockStateIngredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.getIngredient());
        }
    }
}
