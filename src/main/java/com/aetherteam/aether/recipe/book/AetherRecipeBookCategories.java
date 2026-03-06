package com.aetherteam.aether.recipe.book;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.registry.DeferredHolder;
import com.aetherteam.aether.registry.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeBookCategory;

public class AetherRecipeBookCategories {
    public static final DeferredRegister<RecipeBookCategory> RECIPE_BOOK_CATEGORIES = DeferredRegister.create(BuiltInRegistries.RECIPE_BOOK_CATEGORY, Aether.MODID);

    public static final DeferredHolder<RecipeBookCategory, RecipeBookCategory> ENCHANTING_FOOD = RECIPE_BOOK_CATEGORIES.register("enchanting_food", RecipeBookCategory::new);
    public static final DeferredHolder<RecipeBookCategory, RecipeBookCategory> ENCHANTING_BLOCKS = RECIPE_BOOK_CATEGORIES.register("enchanting_blocks", RecipeBookCategory::new);
    public static final DeferredHolder<RecipeBookCategory, RecipeBookCategory> ENCHANTING_MISC = RECIPE_BOOK_CATEGORIES.register("enchanting_misc", RecipeBookCategory::new);
    public static final DeferredHolder<RecipeBookCategory, RecipeBookCategory> ENCHANTING_REPAIR = RECIPE_BOOK_CATEGORIES.register("enchanting_repair", RecipeBookCategory::new);

    public static final DeferredHolder<RecipeBookCategory, RecipeBookCategory> FREEZABLE_BLOCKS = RECIPE_BOOK_CATEGORIES.register("freezable_blocks", RecipeBookCategory::new);
    public static final DeferredHolder<RecipeBookCategory, RecipeBookCategory> FREEZABLE_MISC = RECIPE_BOOK_CATEGORIES.register("freezable_misc", RecipeBookCategory::new);

    public static final DeferredHolder<RecipeBookCategory, RecipeBookCategory> INCUBATION_MISC = RECIPE_BOOK_CATEGORIES.register("incubation_misc", RecipeBookCategory::new);

    public static final RecipeBookCategory ENCHANTING_SEARCH = new RecipeBookCategory();
    public static final RecipeBookCategory FREEZABLE_SEARCH = new RecipeBookCategory();
    public static final RecipeBookCategory INCUBATION_SEARCH = new RecipeBookCategory();

}
