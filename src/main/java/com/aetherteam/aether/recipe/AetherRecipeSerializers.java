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
import net.minecraft.world.item.crafting.RecipeSerializer;

public final class AetherRecipeSerializers {
    public static final RecipeSerializer<AltarRepairRecipe> REPAIRING = Registry.register(
            BuiltInRegistries.RECIPE_SERIALIZER,
            Identifier.fromNamespaceAndPath(Aether.MODID, "repairing"),
            AltarRepairRecipe.Serializer.create());
    public static final RecipeSerializer<EnchantingRecipe> ENCHANTING = Registry.register(
            BuiltInRegistries.RECIPE_SERIALIZER,
            Identifier.fromNamespaceAndPath(Aether.MODID, "enchanting"),
            EnchantingRecipe.Serializer.create());
    public static final RecipeSerializer<FreezingRecipe> FREEZING = Registry.register(
            BuiltInRegistries.RECIPE_SERIALIZER,
            Identifier.fromNamespaceAndPath(Aether.MODID, "freezing"),
            FreezingRecipe.Serializer.create());
    public static final RecipeSerializer<IncubationRecipe> INCUBATION = Registry.register(
            BuiltInRegistries.RECIPE_SERIALIZER,
            Identifier.fromNamespaceAndPath(Aether.MODID, "incubation"),
            IncubationRecipe.Serializer.create());
    public static final RecipeSerializer<AmbrosiumRecipe> AMBROSIUM_ENCHANTING = Registry.register(
            BuiltInRegistries.RECIPE_SERIALIZER,
            Identifier.fromNamespaceAndPath(Aether.MODID, "ambrosium_enchanting"),
            AmbrosiumRecipe.Serializer.create());
    public static final RecipeSerializer<SwetBallRecipe> SWET_BALL_CONVERSION = Registry.register(
            BuiltInRegistries.RECIPE_SERIALIZER,
            Identifier.fromNamespaceAndPath(Aether.MODID, "swet_ball_conversion"),
            SwetBallRecipe.Serializer.create());
    public static final RecipeSerializer<IcestoneFreezableRecipe> ICESTONE_FREEZABLE = Registry.register(
            BuiltInRegistries.RECIPE_SERIALIZER,
            Identifier.fromNamespaceAndPath(Aether.MODID, "icestone_freezable"),
            IcestoneFreezableRecipe.Serializer.create());
    public static final RecipeSerializer<AccessoryFreezableRecipe> ACCESSORY_FREEZABLE = Registry.register(
            BuiltInRegistries.RECIPE_SERIALIZER,
            Identifier.fromNamespaceAndPath(Aether.MODID, "accessory_freezable"),
            AccessoryFreezableRecipe.Serializer.create());
    public static final RecipeSerializer<PlacementConversionRecipe> PLACEMENT_CONVERSION = Registry.register(
            BuiltInRegistries.RECIPE_SERIALIZER,
            Identifier.fromNamespaceAndPath(Aether.MODID, "placement_conversion"),
            PlacementConversionRecipe.Serializer.create());
    public static final RecipeSerializer<ItemBanRecipe> ITEM_PLACEMENT_BAN = Registry.register(
            BuiltInRegistries.RECIPE_SERIALIZER,
            Identifier.fromNamespaceAndPath(Aether.MODID, "item_placement_ban"),
            ItemBanRecipe.Serializer.create());
    public static final RecipeSerializer<BlockBanRecipe> BLOCK_PLACEMENT_BAN = Registry.register(
            BuiltInRegistries.RECIPE_SERIALIZER,
            Identifier.fromNamespaceAndPath(Aether.MODID, "block_placement_ban"),
            BlockBanRecipe.Serializer.create());

    private AetherRecipeSerializers() {
    }

    public static void bootstrap() {
    }
}
