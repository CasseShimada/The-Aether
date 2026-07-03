package com.aetherteam.aether.integration.jei.categories.block;

import com.aetherteam.aether.integration.jei.categories.AbstractRecipeCategory;
import com.aetherteam.aether.recipe.blockstate.BlockPropertyPair;
import com.aetherteam.aether.recipe.blockstate.BlockStateIngredient;
import com.aetherteam.aether.recipe.blockstate.recipes.AbstractBlockStateRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IPlatformFluidHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.Fluid;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractBlockStateRecipeCategory<T extends AbstractBlockStateRecipe> extends AbstractRecipeCategory<T> {
    protected final IPlatformFluidHelper<?> fluidHelper;

    protected AbstractBlockStateRecipeCategory(String id, Identifier uid, IDrawable background, IDrawable icon, IRecipeType<T> recipeType, IPlatformFluidHelper<?> fluidHelper) {
        super(id, uid, background, icon, recipeType);
        this.fluidHelper = fluidHelper;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, T recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 1)
                .addIngredientsUnsafe(this.toIngredients(recipe.getIngredient()))
                .addRichTooltipCallback((recipeSlotView, tooltip) -> this.populateAdditionalInformation(recipe, tooltip));
        builder.addSlot(RecipeIngredientRole.OUTPUT, 61, 1)
                .addIngredientsUnsafe(List.of(this.toIngredient(recipe.getResult())));
    }

    protected void populateAdditionalInformation(T recipe, List<net.minecraft.network.chat.Component> tooltip) {
    }

    protected void populateAdditionalInformation(T recipe, ITooltipBuilder tooltip) {
        List<net.minecraft.network.chat.Component> lines = new ArrayList<>();
        this.populateAdditionalInformation(recipe, lines);
        tooltip.addAll(lines);
    }

    protected List<Object> toIngredients(BlockStateIngredient ingredient) {
        List<Object> ingredients = new ArrayList<>();
        for (BlockPropertyPair pair : ingredient.getPairs()) {
            ingredients.add(this.toIngredient(pair));
        }
        return ingredients;
    }

    protected Object toIngredient(BlockPropertyPair pair) {
        if (pair.block() instanceof LiquidBlock liquidBlock) {
            Fluid fluid = liquidBlock.defaultBlockState().getFluidState().getType();
            return this.fluidHelper.create(Holder.direct(fluid), this.fluidHelper.bucketVolume());
        }

        ItemStack stack = new ItemStack(pair.block());
        return stack.isEmpty() ? new ItemStack(Blocks.STONE) : stack;
    }
}
