package com.aetherteam.aether.recipe.serializer;

import com.aetherteam.aether.recipe.recipes.ban.AbstractPlacementBanRecipe;
import com.aetherteam.nitrogen.recipe.BlockStateIngredient;
import com.aetherteam.nitrogen.recipe.BlockStateRecipeUtil;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Function3;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.biome.Biome;

import java.util.Optional;
import java.util.function.Predicate;

public final class PlacementBanRecipeSerializer {
    private PlacementBanRecipeSerializer() {
    }

    public static <F extends AbstractPlacementBanRecipe<?, ?, ?>> RecipeSerializer<F> create(MapCodec<F> codec, StreamCodec<RegistryFriendlyByteBuf, F> streamCodec) {
        return new RecipeSerializer<>(codec, streamCodec);
    }

    public static <F extends AbstractPlacementBanRecipe<?, ?, ?>> void writeBase(RegistryFriendlyByteBuf buffer, F recipe) {
        BlockStateRecipeUtil.STREAM_CODEC.encode(buffer, recipe.getBiome());
        buffer.writeOptional(recipe.getBypassBlock(), (buf, blockStateIngredient) -> BlockStateIngredient.CONTENTS_STREAM_CODEC.encode((RegistryFriendlyByteBuf) buf, blockStateIngredient));
    }

    public static Either<ResourceKey<Biome>, TagKey<Biome>> readBiome(RegistryFriendlyByteBuf buffer) {
        return BlockStateRecipeUtil.STREAM_CODEC.decode(buffer);
    }

    public static Optional<BlockStateIngredient> readBypass(RegistryFriendlyByteBuf buffer) {
        return buffer.readOptional(buf -> BlockStateIngredient.CONTENTS_STREAM_CODEC.decode((RegistryFriendlyByteBuf) buf));
    }

    public interface CookieBaker<T, S extends Predicate<T>, R extends RecipeInput, F extends AbstractPlacementBanRecipe<T, S, R>> extends Function3<Either<ResourceKey<Biome>, TagKey<Biome>>, Optional<BlockStateIngredient>, S, F> {
        @Override
        F apply(Either<ResourceKey<Biome>, TagKey<Biome>> biome, Optional<BlockStateIngredient> bypassBlock, S ingredient);
    }
}
