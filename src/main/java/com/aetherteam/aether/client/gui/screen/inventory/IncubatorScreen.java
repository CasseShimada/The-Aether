package com.aetherteam.aether.client.gui.screen.inventory;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.client.gui.screen.inventory.recipebook.IncubatorRecipeBookComponent;
import com.aetherteam.aether.inventory.menu.IncubatorMenu;
import com.aetherteam.aether.item.AetherItems;
import com.aetherteam.aether.recipe.book.AetherRecipeBookCategories;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Items;

import java.util.List;

public class IncubatorScreen extends AbstractRecipeBookScreen<IncubatorMenu> {
    private static final Identifier INCUBATOR_GUI_TEXTURES = Identifier.fromNamespaceAndPath(Aether.MODID, "textures/gui/menu/incubator.png");
    private static final Identifier LIT_PROGRESS_TEXTURE = Identifier.fromNamespaceAndPath(Aether.MODID, "menu/lit_progress");
    private static final Identifier INCUBATION_PROGRESS_TEXTURE = Identifier.fromNamespaceAndPath(Aether.MODID, "menu/incubation_progress");
    private static final List<RecipeBookComponent.TabInfo> TABS = List.of(
        new RecipeBookComponent.TabInfo(Items.COMPASS, AetherRecipeBookCategories.INCUBATION_SEARCH),
        new RecipeBookComponent.TabInfo(AetherItems.BLUE_MOA_EGG.get(), AetherRecipeBookCategories.INCUBATION_MISC));

    public IncubatorScreen(IncubatorMenu menu, Inventory playerInventory, Component title) {
        super(menu, new IncubatorRecipeBookComponent(menu, TABS), playerInventory, title);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int x, int y, float partialTicks) {
        int left = this.leftPos;
        int top = this.topPos;
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, INCUBATOR_GUI_TEXTURES, left, top, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
        if (this.getMenu().isIncubating()) {
            int incubationTimeRemaining = this.getMenu().getIncubationTimeRemaining() + 1;
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, LIT_PROGRESS_TEXTURE, 14, 14, 0, 14 - incubationTimeRemaining, left + 74, top + 36 + 13 - incubationTimeRemaining, 14, incubationTimeRemaining);
        }
        int incubationProgressScaled = this.getMenu().getIncubationProgressScaled();
        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, INCUBATION_PROGRESS_TEXTURE, 10, 54, 0, 54 - incubationProgressScaled, left + 103, top + 15 + 55 - incubationProgressScaled, 10, incubationProgressScaled);
    }

    @Override
    protected ScreenPosition getRecipeBookButtonPosition() {
        return new ScreenPosition(this.leftPos + 37, this.height / 2 - 49);
    }
}
