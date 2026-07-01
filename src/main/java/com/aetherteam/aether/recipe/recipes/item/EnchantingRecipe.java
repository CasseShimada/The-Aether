package com.aetherteam.aether.recipe.recipes.item;

import com.aetherteam.aether.block.AetherBlocks;
import com.aetherteam.aether.recipe.AetherBookCategory;
import com.aetherteam.aether.recipe.AetherRecipeSerializers;
import com.aetherteam.aether.recipe.AetherRecipeTypes;
import com.aetherteam.aether.recipe.serializer.AetherCookingSerializer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class EnchantingRecipe extends AbstractAetherCookingRecipe {
    public EnchantingRecipe(String group, AetherBookCategory category, Ingredient ingredient, ItemStack result, float experience, int enchantingTime) {
        super(AetherRecipeTypes.ENCHANTING, group, category, ingredient, result, experience, enchantingTime);
    }

    public EnchantingRecipe(String group, AetherBookCategory category, Ingredient ingredient, ItemStackTemplate result, float experience, int enchantingTime) {
        super(AetherRecipeTypes.ENCHANTING, group, category, ingredient, result, experience, enchantingTime);
    }

    @Override
    protected Item furnaceIcon() {
        return AetherBlocks.ALTAR.get().asItem();
    }

    @Override
    public RecipeSerializer<EnchantingRecipe> getSerializer() {
        return AetherRecipeSerializers.ENCHANTING;
    }

    public static final class Serializer {
        private Serializer() {
        }

        public static RecipeSerializer<EnchantingRecipe> create() {
            return AetherCookingSerializer.create(EnchantingRecipe::new, 250);
        }
    }
}
