package com.aetherteam.aether.recipe;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.recipe.recipes.ban.BlockBanRecipe;
import com.aetherteam.aether.recipe.recipes.ban.ItemBanRecipe;
import com.aetherteam.aether.recipe.recipes.block.*;
import com.aetherteam.aether.recipe.recipes.item.AbstractAetherCookingRecipe;
import com.aetherteam.aether.recipe.recipes.item.FreezingRecipe;
import com.aetherteam.aether.recipe.recipes.item.IncubationRecipe;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

public final class AetherRecipeTypes {
    public static final RecipeType<? extends AbstractAetherCookingRecipe> ENCHANTING = Registry.register(
            BuiltInRegistries.RECIPE_TYPE,
            Identifier.fromNamespaceAndPath(Aether.MODID, "enchanting"),
            createType("enchanting"));
    public static final RecipeType<FreezingRecipe> FREEZING = Registry.register(
            BuiltInRegistries.RECIPE_TYPE,
            Identifier.fromNamespaceAndPath(Aether.MODID, "freezing"),
            createType("freezing"));
    public static final RecipeType<IncubationRecipe> INCUBATION = Registry.register(
            BuiltInRegistries.RECIPE_TYPE,
            Identifier.fromNamespaceAndPath(Aether.MODID, "incubation"),
            createType("incubation"));
    public static final RecipeType<AmbrosiumRecipe> AMBROSIUM_ENCHANTING = Registry.register(
            BuiltInRegistries.RECIPE_TYPE,
            Identifier.fromNamespaceAndPath(Aether.MODID, "ambrosium_enchanting"),
            createType("ambrosium_enchanting"));
    public static final RecipeType<SwetBallRecipe> SWET_BALL_CONVERSION = Registry.register(
            BuiltInRegistries.RECIPE_TYPE,
            Identifier.fromNamespaceAndPath(Aether.MODID, "swet_ball_conversion"),
            createType("swet_ball_conversion"));
    public static final RecipeType<IcestoneFreezableRecipe> ICESTONE_FREEZABLE = Registry.register(
            BuiltInRegistries.RECIPE_TYPE,
            Identifier.fromNamespaceAndPath(Aether.MODID, "icestone_freezable"),
            createType("icestone_freezable"));
    public static final RecipeType<AccessoryFreezableRecipe> ACCESSORY_FREEZABLE = Registry.register(
            BuiltInRegistries.RECIPE_TYPE,
            Identifier.fromNamespaceAndPath(Aether.MODID, "accessory_freezable"),
            createType("accessory_freezable"));
    public static final RecipeType<PlacementConversionRecipe> PLACEMENT_CONVERSION = Registry.register(
            BuiltInRegistries.RECIPE_TYPE,
            Identifier.fromNamespaceAndPath(Aether.MODID, "placement_conversion"),
            createType("placement_conversion"));
    public static final RecipeType<ItemBanRecipe> ITEM_PLACEMENT_BAN = Registry.register(
            BuiltInRegistries.RECIPE_TYPE,
            Identifier.fromNamespaceAndPath(Aether.MODID, "item_placement_ban"),
            createType("item_placement_ban"));
    public static final RecipeType<BlockBanRecipe> BLOCK_PLACEMENT_BAN = Registry.register(
            BuiltInRegistries.RECIPE_TYPE,
            Identifier.fromNamespaceAndPath(Aether.MODID, "block_placement_ban"),
            createType("block_placement_ban"));

    private static <T extends Recipe<?>> RecipeType<T> createType(String name) {
        return new RecipeType<>() {
            @Override
            public String toString() {
                return Aether.MODID + ":" + name;
            }
        };
    }

    private AetherRecipeTypes() {
    }

    public static void bootstrap() {
    }
}
