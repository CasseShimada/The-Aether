package com.aetherteam.aether.recipe.recipes.item;

import com.aetherteam.aether.block.AetherBlocks;
import com.aetherteam.aether.recipe.AetherRecipeSerializers;
import com.aetherteam.aether.recipe.AetherRecipeTypes;
import com.aetherteam.aether.recipe.book.AetherRecipeBookCategories;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.Optional;

public class IncubationRecipe implements Recipe<SingleRecipeInput> {
    protected final String group;
    protected final Ingredient ingredient;
    protected final EntityType<?> entity;
    protected final Optional<CompoundTag> tag;
    protected final int incubationTime;
    protected final PlacementInfo placementInfo;

    public IncubationRecipe(String group, Ingredient ingredient, EntityType<?> entity, Optional<CompoundTag> tag, int incubationTime) {
        this.group = group;
        this.ingredient = ingredient;
        this.entity = entity;
        this.tag = tag;
        this.incubationTime = incubationTime;
        this.placementInfo = PlacementInfo.create(ingredient);
    }

    @Override
    public boolean matches(SingleRecipeInput menu, Level level) {
        return this.ingredient.test(menu.getItem(0));
    }

    /**
     * @return An empty {@link ItemStack}, as there is no item output.
     */
    @Override
    public ItemStack assemble(SingleRecipeInput menu) {
        return ItemStack.EMPTY;
    }

    /**
     * @return The original {@link ItemStack} ingredient for Recipe Book display.
     */
    public ItemStack getResultItem() {
        return this.ingredient.items().findFirst().map(ItemStack::new).orElse(ItemStack.EMPTY);
    }

    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    public int getIncubationTime() {
        return this.incubationTime;
    }

    public EntityType<?> getEntity() {
        return this.entity;
    }

    public Optional<CompoundTag> getTag() {
        return this.tag;
    }

    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> nonNullList = NonNullList.create();
        nonNullList.add(this.ingredient);
        return nonNullList;
    }

    @Override
    public String group() {
        return this.group;
    }

    public ItemStack getToastSymbol() {
        return new ItemStack(AetherBlocks.INCUBATOR.get());
    }

    @Override
    public RecipeSerializer<IncubationRecipe> getSerializer() {
        return AetherRecipeSerializers.INCUBATION.get();
    }

    @Override
    public RecipeType<IncubationRecipe> getType() {
        return AetherRecipeTypes.INCUBATION.get();
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return AetherRecipeBookCategories.INCUBATION_MISC;
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public PlacementInfo placementInfo() {
        return this.placementInfo;
    }

    public static final class Serializer {
        private static final MapCodec<IncubationRecipe> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
                Codec.STRING.optionalFieldOf("group", "").forGetter((IncubationRecipe recipe) -> recipe.group),
                Ingredient.CODEC.fieldOf("ingredient").forGetter((IncubationRecipe recipe) -> recipe.ingredient),
                BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("entity").forGetter((IncubationRecipe recipe) -> recipe.entity),
                CompoundTag.CODEC.optionalFieldOf("tag").forGetter((IncubationRecipe recipe) -> recipe.tag),
                Codec.INT.fieldOf("incubationtime").orElse(500).forGetter((IncubationRecipe recipe) -> recipe.incubationTime)
        ).apply(instance, IncubationRecipe::new));

        private Serializer() {
        }

        public static RecipeSerializer<IncubationRecipe> create() {
            return new RecipeSerializer<>(CODEC, StreamCodec.of(Serializer::toNetwork, Serializer::fromNetwork));
        }

        @Nullable
        private static IncubationRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            String group = buffer.readUtf();
            Ingredient ingredient = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
            EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.getOptional(Identifier.parse(buffer.readUtf())).orElseThrow(() -> new JsonSyntaxException("Entity type cannot be found"));
            Optional<CompoundTag> tag = buffer.readOptional(RegistryFriendlyByteBuf::readNbt);
            int incubationTime = buffer.readVarInt();
            return new IncubationRecipe(group, ingredient, entityType, tag, incubationTime);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buffer, IncubationRecipe recipe) {
            buffer.writeUtf(recipe.group);
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.ingredient);
            buffer.writeUtf(EntityType.getKey(recipe.getEntity()).toString());
            buffer.writeOptional(recipe.tag, RegistryFriendlyByteBuf::writeNbt);
            buffer.writeVarInt(recipe.getIncubationTime());
        }
    }
}
