package com.aetherteam.aether.client.gui.screen.inventory;

import com.aetherteam.aether.inventory.menu.AbstractAetherFurnaceMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.gui.screens.recipebook.FurnaceRecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.SingleRecipeInput;

public abstract class AbstractAetherFurnaceScreen<T extends AbstractAetherFurnaceMenu> extends AbstractRecipeBookScreen<T> {
    private final Identifier texture;
    private final Identifier litProgressSprite;
    private final Identifier burnProgressSprite;

    public AbstractAetherFurnaceScreen(T menu, RecipeBookComponent<AbstractAetherFurnaceMenu> recipeBook, Inventory playerInventory, Component title, Identifier texture, Identifier litProgressSprite, Identifier burnProgressSprite) {
        super(menu, recipeBook, playerInventory, title);
        this.texture = texture;
        this.litProgressSprite = litProgressSprite;
        this.burnProgressSprite = burnProgressSprite;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int x, int y) {
        int left = this.leftPos;
        int top = this.topPos;
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, this.texture, left, top, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
        if (this.getMenu().isLit()) {
            int litProgress = this.getMenu().getLitProgress() + 1;
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, this.litProgressSprite, 14, 14, 0, 14 - litProgress, left + 57, top + 36 + 13 - litProgress, 14, litProgress);
        }
        int burnProgress = this.getMenu().getBurnProgress();
        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, this.burnProgressSprite, 24, 16, 0, 0, left + 79, top + 34, burnProgress + 1, 16);
    }

    @Override
    protected ScreenPosition getRecipeBookButtonPosition() {
        return new ScreenPosition(this.leftPos + 20, this.height / 2 - 49);
    }
}
