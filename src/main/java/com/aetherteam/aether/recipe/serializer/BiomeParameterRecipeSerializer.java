package com.aetherteam.aether.recipe.serializer;

import com.aetherteam.aether.recipe.recipes.block.AbstractBiomeParameterRecipe;
import com.aetherteam.aether.recipe.blockstate.BlockPropertyPair;
import com.aetherteam.aether.recipe.blockstate.BlockStateIngredient;
import com.aetherteam.aether.recipe.blockstate.BlockStateRecipeUtil;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.biome.Biome;

import java.util.Optional;

public final class BiomeParameterRecipeSerializer {
    private BiomeParameterRecipeSerializer() {
    }

    public static <T extends AbstractBiomeParameterRecipe> RecipeSerializer<T> create(Factory<T> factory) {
        MapCodec<T> codec = RecordCodecBuilder.mapCodec(inst -> inst.group(
                BlockStateRecipeUtil.KEY_CODEC.optionalFieldOf("biome").forGetter(AbstractBiomeParameterRecipe::getBiome),
                BlockStateIngredient.CODEC.fieldOf("ingredient").forGetter(AbstractBiomeParameterRecipe::getIngredient),
                BlockPropertyPair.CODEC.fieldOf("result").forGetter(AbstractBiomeParameterRecipe::getResult),
                Identifier.CODEC.optionalFieldOf("mcfunction").forGetter(AbstractBiomeParameterRecipe::getFunctionId)
        ).apply(inst, factory::create));
        StreamCodec<RegistryFriendlyByteBuf, T> streamCodec = StreamCodec.of(
                (buffer, recipe) -> toNetwork(buffer, recipe),
                buffer -> fromNetwork(buffer, factory)
        );
        return new RecipeSerializer<>(codec, streamCodec);
    }

    private static <T extends AbstractBiomeParameterRecipe> T fromNetwork(RegistryFriendlyByteBuf buffer, Factory<T> factory) {
        Optional<Either<ResourceKey<Biome>, TagKey<Biome>>> biome = buffer.readOptional(buf -> BlockStateRecipeUtil.STREAM_CODEC.decode((RegistryFriendlyByteBuf) buf));
        BlockStateIngredient ingredient = BlockStateIngredient.CONTENTS_STREAM_CODEC.decode(buffer);
        BlockPropertyPair result = BlockStateRecipeUtil.readPair(buffer);
        Optional<Identifier> function = buffer.readOptional(FriendlyByteBuf::readIdentifier);
        return factory.create(biome, ingredient, result, function);
    }

    private static <T extends AbstractBiomeParameterRecipe> void toNetwork(RegistryFriendlyByteBuf buffer, T recipe) {
        buffer.writeOptional(recipe.getBiome(), (buf, either) -> BlockStateRecipeUtil.STREAM_CODEC.encode((RegistryFriendlyByteBuf) buf, either));
        BlockStateIngredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.getIngredient());
        BlockStateRecipeUtil.writePair(buffer, recipe.getResult());
        buffer.writeOptional(recipe.getFunctionId(), (buf, id) -> buf.writeIdentifier(id));
    }

    public interface Factory<T extends AbstractBiomeParameterRecipe> {
        T create(Optional<Either<ResourceKey<Biome>, TagKey<Biome>>> biome, BlockStateIngredient ingredient, BlockPropertyPair result, Optional<Identifier> function);
    }
}
