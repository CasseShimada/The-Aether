package com.aetherteam.aether.client.gui.screen.inventory;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.block.AetherBlocks;
import com.aetherteam.aether.client.gui.screen.inventory.recipebook.AltarRecipeBookComponent;
import com.aetherteam.aether.inventory.menu.AltarMenu;
import com.aetherteam.aether.item.AetherItems;
import com.aetherteam.aether.recipe.book.AetherRecipeBookCategories;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.SearchRecipeBookCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeBookCategories;

import java.util.List;

public class AltarScreen extends AbstractAetherFurnaceScreen<AltarMenu> {
    private static final Identifier ALTAR_GUI_TEXTURES = Identifier.fromNamespaceAndPath(Aether.MODID, "textures/gui/menu/altar.png");
    private static final Identifier LIT_PROGRESS_TEXTURE = Identifier.fromNamespaceAndPath(Aether.MODID, "menu/lit_progress");
    private static final Identifier BURN_PROGRESS_TEXTURE = Identifier.fromNamespaceAndPath(Aether.MODID, "menu/burn_progress");
    private static final List<RecipeBookComponent.TabInfo> TABS = List.of(
        new RecipeBookComponent.TabInfo(Items.COMPASS, AetherRecipeBookCategories.ENCHANTING_SEARCH),
        new RecipeBookComponent.TabInfo(AetherItems.ENCHANTED_BERRY.get(), AetherRecipeBookCategories.ENCHANTING_FOOD),
        new RecipeBookComponent.TabInfo(AetherBlocks.ENCHANTED_GRAVITITE.asItem(), AetherRecipeBookCategories.ENCHANTING_BLOCKS),
        new RecipeBookComponent.TabInfo(AetherItems.SKYROOT_REMEDY_BUCKET.get(), AetherRecipeBookCategories.ENCHANTING_MISC),
        new RecipeBookComponent.TabInfo(AetherItems.ZANITE_PICKAXE.get(), AetherRecipeBookCategories.ENCHANTING_REPAIR));

    public AltarScreen(AltarMenu menu, Inventory inventory, Component title) {
        super(menu, new AltarRecipeBookComponent(menu, TABS), inventory, title, ALTAR_GUI_TEXTURES, LIT_PROGRESS_TEXTURE, BURN_PROGRESS_TEXTURE);
    }
}
