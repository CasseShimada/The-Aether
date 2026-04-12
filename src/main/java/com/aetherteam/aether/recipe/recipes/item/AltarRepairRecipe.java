package com.aetherteam.aether.recipe.recipes.item;

import com.aetherteam.aether.block.AetherBlocks;
import com.aetherteam.aether.recipe.AetherBookCategory;
import com.aetherteam.aether.recipe.AetherRecipeSerializers;
import com.aetherteam.aether.recipe.AetherRecipeTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SingleRecipeInput;

public class AltarRepairRecipe extends AbstractAetherCookingRecipe {
    private static final ItemStackTemplate PLACEHOLDER_RESULT = new ItemStackTemplate(AetherBlocks.ALTAR.get().asItem());
    public final Ingredient ingredient;

    public AltarRepairRecipe(String group, Ingredient ingredient, int repairTime) {
        super(AetherRecipeTypes.ENCHANTING.get(), group, AetherBookCategory.ENCHANTING_REPAIR, ingredient, PLACEHOLDER_RESULT, 0.0F, repairTime);
        this.ingredient = ingredient;
    }

    private static ItemStack firstIngredientItem(Ingredient ingredient) {
        return ingredient.items().findFirst().map((holder) -> new ItemStack(holder.value())).orElse(ItemStack.EMPTY);
    }

    /**
     * @param inventory The crafting {@link SingleRecipeInput}.
     * @return The original {@link ItemStack} ingredient, because repairing always outputs the same item as the input.
     */
    @Override
    public ItemStack assemble(SingleRecipeInput inventory) {
        return firstIngredientItem(this.ingredient);
    }

    @Override
    public ItemStack getResult() {
        return firstIngredientItem(this.ingredient);
    }

    /**
     * @return The original {@link ItemStack} ingredient for Recipe Book display, because repairing always outputs the same item as the input.
     */
    public ItemStack getResultItem() {
        return firstIngredientItem(this.ingredient);
    }

    @Override
    protected Item furnaceIcon() {
        return AetherBlocks.ALTAR.get().asItem();
    }

    @Override
    public RecipeSerializer<AltarRepairRecipe> getSerializer() {
        return AetherRecipeSerializers.REPAIRING.get();
    }

    public static final class Serializer {
        private static final MapCodec<AltarRepairRecipe> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
                Codec.STRING.optionalFieldOf("group", "").forGetter(AbstractCookingRecipe::group),
                Ingredient.CODEC.fieldOf("ingredient").forGetter((recipe) -> recipe.ingredient),
                Codec.INT.fieldOf("repairTime").orElse(500).forGetter(AbstractCookingRecipe::cookingTime)
        ).apply(instance, AltarRepairRecipe::new));

        private Serializer() {
        }

        public static RecipeSerializer<AltarRepairRecipe> create() {
            return new RecipeSerializer<>(CODEC, StreamCodec.of(Serializer::toNetwork, Serializer::fromNetwork));
        }

        private static AltarRepairRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            String group = buffer.readUtf();
            Ingredient ingredient = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
            int cookingTime = buffer.readVarInt();
            return new AltarRepairRecipe(group, ingredient, cookingTime);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buffer, AltarRepairRecipe recipe) {
            buffer.writeUtf(recipe.group());
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.ingredient);
            buffer.writeVarInt(recipe.cookingTime());
        }
    }
}
