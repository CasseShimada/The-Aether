package com.aetherteam.aether.recipe.blockstate.serializer;

import com.aetherteam.aether.recipe.blockstate.BlockPropertyPair;
import com.aetherteam.aether.recipe.blockstate.BlockStateIngredient;
import com.aetherteam.aether.recipe.blockstate.BlockStateRecipeUtil;
import com.aetherteam.aether.recipe.blockstate.recipes.AbstractBlockStateRecipe;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.Optional;

public final class BlockStateRecipeSerializer {
    private BlockStateRecipeSerializer() {
    }

    public static <T extends AbstractBlockStateRecipe> RecipeSerializer<T> create(AbstractBlockStateRecipe.Factory<T> factory) {
        MapCodec<T> codec = RecordCodecBuilder.mapCodec(instance -> instance.group(
                BlockStateIngredient.CODEC.fieldOf("ingredient").forGetter(AbstractBlockStateRecipe::getIngredient),
                BlockPropertyPair.CODEC.fieldOf("result").forGetter(AbstractBlockStateRecipe::getResult),
                Identifier.CODEC.optionalFieldOf("mcfunction").forGetter(AbstractBlockStateRecipe::getFunctionId)
        ).apply(instance, factory::create));
        StreamCodec<RegistryFriendlyByteBuf, T> streamCodec = StreamCodec.of(
                BlockStateRecipeSerializer::toNetwork,
                buffer -> fromNetwork(buffer, factory)
        );
        return new RecipeSerializer<>(codec, streamCodec);
    }

    public static <T extends AbstractBlockStateRecipe> T fromNetwork(RegistryFriendlyByteBuf buffer, AbstractBlockStateRecipe.Factory<T> factory) {
        BlockStateIngredient ingredient = BlockStateIngredient.CONTENTS_STREAM_CODEC.decode(buffer);
        BlockPropertyPair result = BlockStateRecipeUtil.readPair(buffer);
        Optional<Identifier> function = buffer.readOptional(FriendlyByteBuf::readIdentifier);
        return factory.create(ingredient, result, function);
    }

    public static <T extends AbstractBlockStateRecipe> void toNetwork(RegistryFriendlyByteBuf buffer, T recipe) {
        BlockStateIngredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.getIngredient());
        BlockStateRecipeUtil.writePair(buffer, recipe.getResult());
        buffer.writeOptional(recipe.getFunctionId(), (buf, id) -> buf.writeIdentifier(id));
    }
}
