package com.aetherteam.aether.recipe;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.recipe.recipes.ban.BlockBanRecipe;
import com.aetherteam.aether.recipe.recipes.ban.ItemBanRecipe;
import com.aetherteam.aether.recipe.recipes.block.*;
import com.aetherteam.aether.recipe.recipes.item.AbstractAetherCookingRecipe;
import com.aetherteam.aether.recipe.recipes.item.FreezingRecipe;
import com.aetherteam.aether.recipe.recipes.item.IncubationRecipe;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import com.aetherteam.aether.registry.DeferredHolder;
import com.aetherteam.aether.registry.DeferredRegister;

public class AetherRecipeTypes {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, Aether.MODID);

    public static final DeferredHolder<RecipeType<?>, RecipeType<? extends AbstractAetherCookingRecipe>> ENCHANTING = RECIPE_TYPES.register("enchanting", () -> createType("enchanting"));
    public static final DeferredHolder<RecipeType<?>, RecipeType<FreezingRecipe>> FREEZING = RECIPE_TYPES.register("freezing", () -> createType("freezing"));
    public static final DeferredHolder<RecipeType<?>, RecipeType<IncubationRecipe>> INCUBATION = RECIPE_TYPES.register("incubation", () -> createType("incubation"));
    public static final DeferredHolder<RecipeType<?>, RecipeType<AmbrosiumRecipe>> AMBROSIUM_ENCHANTING = RECIPE_TYPES.register("ambrosium_enchanting", () -> createType("ambrosium_enchanting"));
    public static final DeferredHolder<RecipeType<?>, RecipeType<SwetBallRecipe>> SWET_BALL_CONVERSION = RECIPE_TYPES.register("swet_ball_conversion", () -> createType("swet_ball_conversion"));
    public static final DeferredHolder<RecipeType<?>, RecipeType<IcestoneFreezableRecipe>> ICESTONE_FREEZABLE = RECIPE_TYPES.register("icestone_freezable", () -> createType("icestone_freezable"));
    public static final DeferredHolder<RecipeType<?>, RecipeType<AccessoryFreezableRecipe>> ACCESSORY_FREEZABLE = RECIPE_TYPES.register("accessory_freezable", () -> createType("accessory_freezable"));
    public static final DeferredHolder<RecipeType<?>, RecipeType<PlacementConversionRecipe>> PLACEMENT_CONVERSION = RECIPE_TYPES.register("placement_conversion", () -> createType("placement_conversion"));
    public static final DeferredHolder<RecipeType<?>, RecipeType<ItemBanRecipe>> ITEM_PLACEMENT_BAN = RECIPE_TYPES.register("item_placement_ban", () -> createType("item_placement_ban"));
    public static final DeferredHolder<RecipeType<?>, RecipeType<BlockBanRecipe>> BLOCK_PLACEMENT_BAN = RECIPE_TYPES.register("block_placement_ban", () -> createType("block_placement_ban"));

    private static <T extends Recipe<?>> RecipeType<T> createType(String name) {
        return new RecipeType<>() {
            @Override
            public String toString() {
                return Aether.MODID + ":" + name;
            }
        };
    }
}
