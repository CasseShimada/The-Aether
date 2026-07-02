package com.aetherteam.aether.client.gui.screen.inventory;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.block.AetherBlocks;
import com.aetherteam.aether.client.gui.screen.inventory.recipebook.FreezerRecipeBookComponent;
import com.aetherteam.aether.inventory.menu.FreezerMenu;
import com.aetherteam.aether.item.AetherItems;
import com.aetherteam.aether.recipe.book.AetherRecipeBookCategories;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Items;

import java.util.List;

public class FreezerScreen extends AbstractAetherFurnaceScreen<FreezerMenu> {
    private static final Identifier FREEZER_GUI_TEXTURES = Identifier.fromNamespaceAndPath(Aether.MODID, "textures/gui/menu/freezer.png");
    private static final Identifier LIT_PROGRESS_TEXTURE = Identifier.fromNamespaceAndPath(Aether.MODID, "menu/lit_progress");
    private static final Identifier BURN_PROGRESS_TEXTURE = Identifier.fromNamespaceAndPath(Aether.MODID, "menu/burn_progress");
    private static final List<RecipeBookComponent.TabInfo> TABS = List.of(
        new RecipeBookComponent.TabInfo(Items.COMPASS, AetherRecipeBookCategories.FREEZABLE_SEARCH),
        new RecipeBookComponent.TabInfo(AetherBlocks.BLUE_AERCLOUD.asItem(), AetherRecipeBookCategories.FREEZABLE_BLOCKS),
        new RecipeBookComponent.TabInfo(AetherItems.ICE_RING, AetherRecipeBookCategories.FREEZABLE_MISC));

    public FreezerScreen(FreezerMenu menu, Inventory inventory, Component title) {
        super(menu, new FreezerRecipeBookComponent(menu, TABS), inventory, title, FREEZER_GUI_TEXTURES, LIT_PROGRESS_TEXTURE, BURN_PROGRESS_TEXTURE);
    }
}
