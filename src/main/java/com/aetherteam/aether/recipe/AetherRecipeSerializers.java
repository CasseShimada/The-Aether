package com.aetherteam.aether.recipe;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.recipe.recipes.ban.BlockBanRecipe;
import com.aetherteam.aether.recipe.recipes.ban.ItemBanRecipe;
import com.aetherteam.aether.recipe.recipes.block.*;
import com.aetherteam.aether.recipe.recipes.item.AltarRepairRecipe;
import com.aetherteam.aether.recipe.recipes.item.EnchantingRecipe;
import com.aetherteam.aether.recipe.recipes.item.FreezingRecipe;
import com.aetherteam.aether.recipe.recipes.item.IncubationRecipe;
import com.aetherteam.nitrogen.recipe.BlockStateIngredient;
import com.aetherteam.nitrogen.recipe.input.BlockStateRecipeInput;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.state.BlockState;
import com.aetherteam.aether.registry.DeferredHolder;
import com.aetherteam.aether.registry.DeferredRegister;

public class AetherRecipeSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, Aether.MODID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<AltarRepairRecipe>> REPAIRING = RECIPE_SERIALIZERS.register("repairing", AltarRepairRecipe.Serializer::create);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<EnchantingRecipe>> ENCHANTING = RECIPE_SERIALIZERS.register("enchanting", EnchantingRecipe.Serializer::create);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<FreezingRecipe>> FREEZING = RECIPE_SERIALIZERS.register("freezing", FreezingRecipe.Serializer::create);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<IncubationRecipe>> INCUBATION = RECIPE_SERIALIZERS.register("incubation", IncubationRecipe.Serializer::create);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<AmbrosiumRecipe>> AMBROSIUM_ENCHANTING = RECIPE_SERIALIZERS.register("ambrosium_enchanting", AmbrosiumRecipe.Serializer::create);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<SwetBallRecipe>> SWET_BALL_CONVERSION = RECIPE_SERIALIZERS.register("swet_ball_conversion", SwetBallRecipe.Serializer::create);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<IcestoneFreezableRecipe>> ICESTONE_FREEZABLE = RECIPE_SERIALIZERS.register("icestone_freezable", IcestoneFreezableRecipe.Serializer::create);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<AccessoryFreezableRecipe>> ACCESSORY_FREEZABLE = RECIPE_SERIALIZERS.register("accessory_freezable", AccessoryFreezableRecipe.Serializer::create);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<PlacementConversionRecipe>> PLACEMENT_CONVERSION = RECIPE_SERIALIZERS.register("placement_conversion", PlacementConversionRecipe.Serializer::create);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ItemBanRecipe>> ITEM_PLACEMENT_BAN = RECIPE_SERIALIZERS.register("item_placement_ban", ItemBanRecipe.Serializer::create);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<BlockBanRecipe>> BLOCK_PLACEMENT_BAN = RECIPE_SERIALIZERS.register("block_placement_ban", BlockBanRecipe.Serializer::create);
}
