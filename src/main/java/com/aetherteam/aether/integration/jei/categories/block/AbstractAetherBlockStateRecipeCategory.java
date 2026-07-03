package com.aetherteam.aether.integration.jei.categories.block;

import com.aetherteam.aether.integration.jei.categories.block.AbstractBlockStateRecipeCategory;
import com.aetherteam.nitrogen.recipe.recipes.AbstractBlockStateRecipe;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IPlatformFluidHelper;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public abstract class AbstractAetherBlockStateRecipeCategory<T extends AbstractBlockStateRecipe> extends AbstractBlockStateRecipeCategory<T> {
    public AbstractAetherBlockStateRecipeCategory(String id, Identifier uid, IDrawable background, IDrawable icon, IRecipeType<T> recipeType, IPlatformFluidHelper<?> fluidHelper) {
        super(id, uid, background, icon, recipeType, fluidHelper);
    }

    @Override
    public Component getTitle() {
        return Component.translatable("gui.aether.jei." + this.id);
    }

    @Override
    public int getWidth() {
        return this.background.getWidth();
    }

    @Override
    public int getHeight() {
        return this.background.getHeight();
    }
}
