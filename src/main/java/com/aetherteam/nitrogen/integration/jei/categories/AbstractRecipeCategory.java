package com.aetherteam.nitrogen.integration.jei.categories;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.types.IRecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

public abstract class AbstractRecipeCategory<T> implements IRecipeCategory<T> {
    protected final String id;
    protected final Identifier uid;
    protected final IDrawable background;
    protected final @Nullable IDrawable icon;
    protected final IRecipeType<T> recipeType;

    protected AbstractRecipeCategory(String id, Identifier uid, IDrawable background, @Nullable IDrawable icon, IRecipeType<T> recipeType) {
        this.id = id;
        this.uid = uid;
        this.background = background;
        this.icon = icon;
        this.recipeType = recipeType;
    }

    @Override
    public IRecipeType<T> getRecipeType() {
        return this.recipeType;
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public int getWidth() {
        return this.background.getWidth();
    }

    @Override
    public int getHeight() {
        return this.background.getHeight();
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, T recipe, IFocusGroup focuses) {
        builder.addDrawable(this.background, 0, 0);
    }

    @Override
    public abstract void setRecipe(IRecipeLayoutBuilder builder, T recipe, IFocusGroup focuses);
}
