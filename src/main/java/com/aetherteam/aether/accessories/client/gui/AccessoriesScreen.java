package com.aetherteam.aether.accessories.client.gui;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

public abstract class AccessoriesScreen extends AbstractContainerScreen<AbstractContainerMenu> {
    protected AccessoriesScreen(AbstractContainerMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }
}
