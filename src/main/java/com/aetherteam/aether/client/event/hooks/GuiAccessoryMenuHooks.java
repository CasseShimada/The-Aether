package com.aetherteam.aether.client.event.hooks;

import com.aetherteam.aether.AetherConfig;
import com.aetherteam.aether.accessories.client.gui.AccessoriesScreen;
import com.aetherteam.aether.client.ClientCompat;
import com.aetherteam.aether.client.AetherKeys;
import com.aetherteam.aether.client.gui.component.inventory.AccessoryButton;
import com.aetherteam.aether.client.gui.component.inventory.ScreenOffset;
import com.aetherteam.aether.client.gui.screen.inventory.AetherAccessoriesScreen;
import com.aetherteam.aether.inventory.menu.AetherAccessoriesMenu;
import com.aetherteam.aether.mixin.mixins.client.accessor.AbstractContainerScreenAccessor;
import com.aetherteam.aether.network.PacketDistributor;
import com.aetherteam.aether.network.packet.serverbound.OpenAccessoriesPacket;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

final class GuiAccessoryMenuHooks {
    private static boolean shouldAddButton = true;

    private GuiAccessoryMenuHooks() {
    }

    static AccessoryButton setupAccessoryButton(Screen screen, ScreenOffset offsets) {
        AbstractContainerScreen<?> containerScreen = canCreateAccessoryButtonForScreen(screen);
        if (containerScreen == null) {
            return null;
        }

        AbstractContainerScreenAccessor accessor = (AbstractContainerScreenAccessor) containerScreen;
        return new AccessoryButton(containerScreen, accessor.aether$getLeftPos() + offsets.x(), accessor.aether$getTopPos() + offsets.y(), AetherAccessoriesScreen.ACCESSORIES_BUTTON);
    }

    static void openAccessoryMenu() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || ClientCompat.overlay(minecraft) != null || ClientCompat.screen(minecraft) != null) {
            return;
        }

        if (AetherConfig.CLIENT.disable_accessory_button.get() || !AetherKeys.OPEN_ACCESSORY_INVENTORY.consumeClick()) {
            return;
        }

        if (minecraft.gameMode != null && minecraft.gameMode.isServerControlledInventory()) {
            minecraft.player.sendOpenInventory();
            return;
        }

        PacketDistributor.sendToServer(new OpenAccessoriesPacket(ItemStack.EMPTY));
        shouldAddButton = false;
    }

    static void closeContainerMenu(int key, int action) {
        Minecraft minecraft = Minecraft.getInstance();
        if (!(ClientCompat.screen(minecraft) instanceof AbstractContainerScreen<?> abstractContainerScreen)) {
            return;
        }

        if (!AetherConfig.CLIENT.disable_accessory_button.get()
                && AetherKeys.OPEN_ACCESSORY_INVENTORY.matches(new KeyEvent(key, 0, 0))
                && (action == InputConstants.PRESS || action == InputConstants.REPEAT)) {
            abstractContainerScreen.onClose();
        }
    }

    @Nullable
    private static AbstractContainerScreen<?> canCreateAccessoryButtonForScreen(Screen screen) {
        if (screen instanceof InventoryScreen
                || screen instanceof AccessoriesScreen
                || screen instanceof CreativeModeInventoryScreen
                || (screen instanceof AetherAccessoriesScreen && shouldAddButton)) {
            return (AbstractContainerScreen<?>) screen;
        } else if (screen instanceof AetherAccessoriesScreen) {
            shouldAddButton = true;
        }
        return null;
    }
}
