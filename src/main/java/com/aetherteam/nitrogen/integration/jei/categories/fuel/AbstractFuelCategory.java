package com.aetherteam.nitrogen.integration.jei.categories.fuel;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

import java.util.List;

public abstract class AbstractFuelCategory implements IRecipeCategory<FuelRecipe> {
    private final IDrawable background;
    private final IDrawable icon;
    private final List<String> stationNames;

    protected AbstractFuelCategory(IGuiHelper helper, List<String> stationNames) {
        this.background = helper.createDrawable(this.getBackgroundTexture(), 55, 16, this.getWidth(), this.getHeight());
        this.icon = helper.drawableBuilder(this.getIconTexture(), 0, 0, 14, 14).setTextureSize(14, 14).build();
        this.stationNames = List.copyOf(stationNames);
    }

    protected abstract Identifier getBackgroundTexture();

    protected abstract Identifier getIconTexture();

    @Override
    public @Nullable IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, FuelRecipe recipe, IFocusGroup focuses) {
        builder.addDrawable(this.background, 0, 0);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, FuelRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 19)
                .addIngredients(VanillaTypes.ITEM_STACK, recipe.ingredients());
        builder.addSlot(RecipeIngredientRole.CRAFTING_STATION, 61, 19)
                .addIngredient(VanillaTypes.ITEM_STACK, new ItemStack(recipe.station()));
    }

    @Override
    public void draw(FuelRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        int burnTimeSeconds = recipe.burnTime() / 20;
        Component burnTime = Component.translatable("gui.jei.category.smelting.time.seconds", burnTimeSeconds);
        Component station = Component.literal(recipe.station().getName().getString());
        var font = Minecraft.getInstance().font;

        guiGraphics.drawString(font, station, 20, 1, 0xFF808080, false);
        guiGraphics.drawString(font, burnTime, 20, 40, 0xFF808080, false);
    }

    protected List<String> getStationNames() {
        return this.stationNames;
    }
}
