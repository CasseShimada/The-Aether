package com.aetherteam.aether.integration.jei.categories.item;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.recipe.recipes.item.AbstractAetherCookingRecipe;
import com.aetherteam.aether.integration.jei.categories.AbstractRecipeCategory;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public abstract class AbstractAetherCookingRecipeCategory<T> extends AbstractRecipeCategory<T> {
    public static final Identifier FLAME_TEXTURE = Identifier.fromNamespaceAndPath(Aether.MODID, "textures/gui/sprites/menu/lit_progress.png");
    public static final Identifier ARROW_TEXTURE = Identifier.fromNamespaceAndPath(Aether.MODID, "textures/gui/sprites/menu/burn_progress.png");
    public static final Identifier INCUBATION_PROGRESS_TEXTURE = Identifier.fromNamespaceAndPath(Aether.MODID, "textures/gui/sprites/menu/incubation_progress.png");
    protected final IDrawable fuelIndicator;
    protected final IDrawableAnimated animatedProgressArrow;

    public AbstractAetherCookingRecipeCategory(String id, Identifier uid, IDrawable background, IDrawable icon, IDrawable fuelIndicator, IDrawableAnimated animatedProgressArrow, IRecipeType<T> recipeType) {
        super(id, uid, background, icon, recipeType);
        this.fuelIndicator = fuelIndicator;
        this.animatedProgressArrow = animatedProgressArrow;
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

    protected void drawExperience(AbstractAetherCookingRecipe recipe, GuiGraphicsExtractor guiGraphics, int y, IDrawable background) {
        float experience = recipe.experience();
        if (experience > 0) {
            Component experienceString = Component.translatable("gui.jei.category.smelting.experience", experience);
            Font fontRenderer = Minecraft.getInstance().font;
            int stringWidth = fontRenderer.width(experienceString);
            guiGraphics.text(fontRenderer, experienceString, background.getWidth() - stringWidth, y, 0xFF808080, false);
        }
    }

    protected void drawCookingTime(GuiGraphicsExtractor guiGraphics, int y, int time, IDrawable background) {
        if (time > 0) {
            int cookTimeSeconds = time / 20;
            Component timeString = Component.translatable("gui.jei.category.smelting.time.seconds", cookTimeSeconds);
            Font fontRenderer = Minecraft.getInstance().font;
            int stringWidth = fontRenderer.width(timeString);
            guiGraphics.text(fontRenderer, timeString, background.getWidth() - stringWidth, y, 0xFF808080, false);
        }
    }
}
