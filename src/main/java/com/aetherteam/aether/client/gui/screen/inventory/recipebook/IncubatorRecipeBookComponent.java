package com.aetherteam.aether.client.gui.screen.inventory.recipebook;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.inventory.menu.IncubatorMenu;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.recipebook.GhostSlots;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.crafting.display.RecipeDisplay;

import java.util.List;

public class IncubatorRecipeBookComponent extends RecipeBookComponent<IncubatorMenu> { //todo
    private static final WidgetSprites FILTER_SPRITES = new WidgetSprites(
            Identifier.fromNamespaceAndPath(Aether.MODID, "recipe_book/incubator_filter_enabled"),
            Identifier.fromNamespaceAndPath(Aether.MODID, "recipe_book/incubator_filter_disabled"),
            Identifier.fromNamespaceAndPath(Aether.MODID, "recipe_book/incubator_filter_enabled_highlighted"),
            Identifier.fromNamespaceAndPath(Aether.MODID, "recipe_book/incubator_filter_disabled_highlighted")
    );
    private static final Component FILTER_NAME = Component.translatable("gui.aether.recipebook.toggleRecipes.incubatable");

    public IncubatorRecipeBookComponent(IncubatorMenu menu, List<TabInfo> tabInfos) {
        super(menu, tabInfos);
    }

    @Override
    protected boolean isCraftingSlot(Slot slot) {
        return false;
    }

    @Override
    protected void selectMatchingRecipes(RecipeCollection recipeCollection, StackedItemContents stackedItemContents) {
        recipeCollection.selectRecipes(stackedItemContents, (display) -> true);
    }

//    @Override
//    protected boolean isCraftingSlot(Slot slot) {
//        return false;
//    }
//
////    @Override
////    public void slotClicked(@Nullable Slot slot) {
////        super.slotClicked(slot);
////        if (slot != null && slot.index < this.menu.getSize()) {
////            this.ghostRecipe.clear();
////        }
////    }
//
//    @Override
//    protected void selectMatchingRecipes(RecipeCollection recipeCollection, StackedItemContents stackedItemContents) {
//
//    }
//
//    @Override
//    public void setupGhostRecipe(RecipeHolder<?> recipe, List<Slot> slots) {
//        this.ghostRecipe.setRecipe(recipe);
//        Slot fuelSlot = slots.get(1);
//        if (fuelSlot.getItem().isEmpty()) {
//            if (this.fuels == null) {
//                this.fuels = Ingredient.of(this.getFuelItems().stream().map(ItemStack::new));
//            }
//            this.ghostRecipe.addIngredient(this.fuels, fuelSlot.x, fuelSlot.y);
//        }
//
//        Ingredient ingredient = recipe.value().getIngredients().getFirst();
//        if (!ingredient.isEmpty()) {
//            Slot eggSlot = slots.getFirst();
//            this.ghostRecipe.addIngredient(ingredient, eggSlot.x, eggSlot.y);
//        }
//    }
//
    @Override
    protected Component getRecipeFilterName() {
        return FILTER_NAME;
    }

    @Override
    protected WidgetSprites getFilterButtonTextures() {
        return FILTER_SPRITES;
    }

    @Override
    protected void fillGhostRecipe(GhostSlots ghostSlots, RecipeDisplay recipeDisplay, ContextMap contextMap) {

    }
//
//    @Override
//    protected void fillGhostRecipe(GhostSlots ghostSlots, RecipeDisplay recipeDisplay, ContextMap contextMap) {
//
//    }
}
