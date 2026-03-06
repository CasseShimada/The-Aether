package com.aetherteam.aether.client.gui.screen.inventory.recipebook;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.inventory.menu.AbstractAetherFurnaceMenu;
import com.aetherteam.aether.mixin.mixins.client.accessor.GhostSlotsAccessor;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.recipebook.GhostSlots;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.crafting.display.FurnaceRecipeDisplay;
import net.minecraft.world.item.crafting.display.RecipeDisplay;

import java.util.List;

public class FreezerRecipeBookComponent extends RecipeBookComponent<AbstractAetherFurnaceMenu> {
    private static final WidgetSprites FILTER_SPRITES = new WidgetSprites(
        Identifier.fromNamespaceAndPath(Aether.MODID, "recipe_book/freezer_filter_enabled"),
        Identifier.fromNamespaceAndPath(Aether.MODID, "recipe_book/freezer_filter_disabled"),
        Identifier.fromNamespaceAndPath(Aether.MODID, "recipe_book/freezer_filter_enabled_highlighted"),
        Identifier.fromNamespaceAndPath(Aether.MODID, "recipe_book/freezer_filter_disabled_highlighted")
    );
    private static final Component FILTER_NAME = Component.translatable("gui.aether.recipebook.toggleRecipes.freezable");

    public FreezerRecipeBookComponent(AbstractAetherFurnaceMenu menu, List<TabInfo> tabInfos) {
        super(menu, tabInfos);
    }

    @Override
    protected boolean isCraftingSlot(Slot slot) {
        return switch (slot.index) {
            case 0, 1, 2 -> true;
            default -> false;
        };
    }

    @Override
    protected void fillGhostRecipe(GhostSlots ghostSlots, RecipeDisplay recipeDisplay, ContextMap contextMap) {
        GhostSlotsAccessor accessor = (GhostSlotsAccessor) ghostSlots;
        accessor.aether$setResult(this.menu.getResultSlot(), contextMap, recipeDisplay.result());
        if (recipeDisplay instanceof FurnaceRecipeDisplay furnacerecipedisplay) {
            accessor.aether$setInput(this.menu.slots.get(0), contextMap, furnacerecipedisplay.ingredient());
            Slot slot = this.menu.slots.get(1);
            if (slot.getItem().isEmpty()) {
                accessor.aether$setInput(slot, contextMap, furnacerecipedisplay.fuel());
            }
        }
    }

    @Override
    protected void selectMatchingRecipes(RecipeCollection possibleRecipes, StackedItemContents stackedItemContents) {
        possibleRecipes.selectRecipes(stackedItemContents, (display) -> display instanceof FurnaceRecipeDisplay);
    }

    @Override
    protected WidgetSprites getFilterButtonTextures() {
        return FILTER_SPRITES;
    }

    @Override
    protected Component getRecipeFilterName() {
        return FILTER_NAME;
    }
}
