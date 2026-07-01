package com.aetherteam.aether.recipe;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.recipe.recipes.ban.BlockBanRecipe;
import com.aetherteam.aether.recipe.recipes.ban.ItemBanRecipe;
import com.aetherteam.aether.recipe.recipes.block.*;
import com.aetherteam.aether.recipe.recipes.item.AltarRepairRecipe;
import com.aetherteam.aether.recipe.recipes.item.EnchantingRecipe;
import com.aetherteam.aether.recipe.recipes.item.FreezingRecipe;
import com.aetherteam.aether.recipe.recipes.item.IncubationRecipe;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class AetherRecipeSerializers {
    public static final RecipeSerializer<AltarRepairRecipe> REPAIRING = register("repairing", AltarRepairRecipe.Serializer.create());
    public static final RecipeSerializer<EnchantingRecipe> ENCHANTING = register("enchanting", EnchantingRecipe.Serializer.create());
    public static final RecipeSerializer<FreezingRecipe> FREEZING = register("freezing", FreezingRecipe.Serializer.create());
    public static final RecipeSerializer<IncubationRecipe> INCUBATION = register("incubation", IncubationRecipe.Serializer.create());
    public static final RecipeSerializer<AmbrosiumRecipe> AMBROSIUM_ENCHANTING = register("ambrosium_enchanting", AmbrosiumRecipe.Serializer.create());
    public static final RecipeSerializer<SwetBallRecipe> SWET_BALL_CONVERSION = register("swet_ball_conversion", SwetBallRecipe.Serializer.create());
    public static final RecipeSerializer<IcestoneFreezableRecipe> ICESTONE_FREEZABLE = register("icestone_freezable", IcestoneFreezableRecipe.Serializer.create());
    public static final RecipeSerializer<AccessoryFreezableRecipe> ACCESSORY_FREEZABLE = register("accessory_freezable", AccessoryFreezableRecipe.Serializer.create());
    public static final RecipeSerializer<PlacementConversionRecipe> PLACEMENT_CONVERSION = register("placement_conversion", PlacementConversionRecipe.Serializer.create());
    public static final RecipeSerializer<ItemBanRecipe> ITEM_PLACEMENT_BAN = register("item_placement_ban", ItemBanRecipe.Serializer.create());
    public static final RecipeSerializer<BlockBanRecipe> BLOCK_PLACEMENT_BAN = register("block_placement_ban", BlockBanRecipe.Serializer.create());

    private static <T extends Recipe<?>> RecipeSerializer<T> register(String name, RecipeSerializer<T> serializer) {
        return Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, Identifier.fromNamespaceAndPath(Aether.MODID, name), serializer);
    }
}
