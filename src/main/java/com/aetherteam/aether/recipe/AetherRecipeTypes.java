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

public class AetherRecipeTypes {
    public static final RecipeType<? extends AbstractAetherCookingRecipe> ENCHANTING = register("enchanting");
    public static final RecipeType<FreezingRecipe> FREEZING = register("freezing");
    public static final RecipeType<IncubationRecipe> INCUBATION = register("incubation");
    public static final RecipeType<AmbrosiumRecipe> AMBROSIUM_ENCHANTING = register("ambrosium_enchanting");
    public static final RecipeType<SwetBallRecipe> SWET_BALL_CONVERSION = register("swet_ball_conversion");
    public static final RecipeType<IcestoneFreezableRecipe> ICESTONE_FREEZABLE = register("icestone_freezable");
    public static final RecipeType<AccessoryFreezableRecipe> ACCESSORY_FREEZABLE = register("accessory_freezable");
    public static final RecipeType<PlacementConversionRecipe> PLACEMENT_CONVERSION = register("placement_conversion");
    public static final RecipeType<ItemBanRecipe> ITEM_PLACEMENT_BAN = register("item_placement_ban");
    public static final RecipeType<BlockBanRecipe> BLOCK_PLACEMENT_BAN = register("block_placement_ban");

    private static <T extends Recipe<?>> RecipeType<T> register(String name) {
        return Registry.register(BuiltInRegistries.RECIPE_TYPE, Identifier.fromNamespaceAndPath(Aether.MODID, name), createType(name));
    }

    private static <T extends Recipe<?>> RecipeType<T> createType(String name) {
        return new RecipeType<>() {
            @Override
            public String toString() {
                return Aether.MODID + ":" + name;
            }
        };
    }
}
