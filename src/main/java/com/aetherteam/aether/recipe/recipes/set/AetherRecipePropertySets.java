package com.aetherteam.aether.recipe.recipes.set;

import com.aetherteam.aether.Aether;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipePropertySet;

public class AetherRecipePropertySets {
    public static final ResourceKey<RecipePropertySet> ALTAR_INPUT = register("altar_input");
    public static final ResourceKey<RecipePropertySet> FREEZER_INPUT = register("freezer_input");
    public static final ResourceKey<RecipePropertySet> INCUBATOR_INPUT = register("incubator_recipe");

    private static ResourceKey<RecipePropertySet> register(String id) {
        return ResourceKey.create(RecipePropertySet.TYPE_KEY, Identifier.fromNamespaceAndPath(Aether.MODID, id));
    }
}
