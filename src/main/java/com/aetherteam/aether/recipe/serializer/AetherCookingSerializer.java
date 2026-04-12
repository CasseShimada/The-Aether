package com.aetherteam.aether.recipe.serializer;

import com.aetherteam.aether.recipe.AetherBookCategory;
import com.aetherteam.aether.recipe.recipes.item.AbstractAetherCookingRecipe;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.Objects;

public final class AetherCookingSerializer {
    private AetherCookingSerializer() {
    }

    public static <T extends AbstractAetherCookingRecipe> RecipeSerializer<T> create(CookieBaker<T> factory, int defaultCookingTime) {
        MapCodec<T> codec = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.optionalFieldOf("group", "").forGetter(AbstractCookingRecipe::group),
                AetherBookCategory.CODEC.fieldOf("category").forGetter(AbstractAetherCookingRecipe::aetherCategory),
                Ingredient.CODEC.fieldOf("ingredient").forGetter(AbstractCookingRecipe::input),
                ItemStackTemplate.CODEC.fieldOf("result").forGetter(AbstractAetherCookingRecipe::resultTemplate),
                Codec.FLOAT.fieldOf("experience").orElse(0.0F).forGetter(AbstractCookingRecipe::experience),
                Codec.INT.fieldOf("cookingtime").orElse(defaultCookingTime).forGetter(AbstractCookingRecipe::cookingTime)
        ).apply(instance, factory::create));
        StreamCodec<RegistryFriendlyByteBuf, T> streamCodec = StreamCodec.of(
                (buffer, recipe) -> toNetwork(buffer, recipe),
                buffer -> fromNetwork(buffer, factory)
        );
        return new RecipeSerializer<>(codec, streamCodec);
    }

    private static <T extends AbstractAetherCookingRecipe> T fromNetwork(RegistryFriendlyByteBuf buffer, CookieBaker<T> factory) {
        String group = buffer.readUtf();
        AetherBookCategory aetherBookCategory = buffer.readEnum(AetherBookCategory.class);
        Ingredient ingredient = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
        ItemStackTemplate result = ItemStackTemplate.STREAM_CODEC.decode(buffer);
        float experience = buffer.readFloat();
        int cookingTime = buffer.readVarInt();
        return factory.create(group, aetherBookCategory, ingredient, result, experience, cookingTime);
    }

    private static <T extends AbstractAetherCookingRecipe> void toNetwork(RegistryFriendlyByteBuf buffer, T recipe) {
        buffer.writeUtf(recipe.group());
        buffer.writeEnum(Objects.requireNonNullElse(recipe.aetherCategory(), AetherBookCategory.UNKNOWN));
        Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.input());
        ItemStackTemplate.STREAM_CODEC.encode(buffer, recipe.resultTemplate());
        buffer.writeFloat(recipe.experience());
        buffer.writeVarInt(recipe.cookingTime());
    }

    public interface CookieBaker<T extends AbstractAetherCookingRecipe> {
        T create(String group, AetherBookCategory category, Ingredient ingredient, ItemStackTemplate result, float experience, int cookingTime);
    }
}
