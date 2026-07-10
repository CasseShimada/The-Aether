package com.aetherteam.aether.client.event.hooks;

import com.aetherteam.aether.AetherConfig;
import com.aetherteam.aether.client.ClientAccess;
import com.aetherteam.aether.client.AetherKeys;
import com.aetherteam.aether.client.gui.component.inventory.AccessoryButton;
import com.aetherteam.aether.client.gui.component.inventory.ScreenOffset;
import com.aetherteam.aether.client.gui.screen.inventory.AetherAccessoriesScreen;
import com.aetherteam.aether.inventory.menu.AetherAccessoriesMenu;
import com.aetherteam.aether.mixin.mixins.client.accessor.AbstractContainerScreenAccessor;
import com.aetherteam.aether.network.AetherPacketSender;
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

public final class GuiAccessoryMenuHooks {
    private static boolean shouldAddButton = true;

    private GuiAccessoryMenuHooks() {
    }

    /**
     * Checks whether the accessory button isn't disabled by {@link AetherConfig.Client#disable_accessory_button} or accessory tags being empty.
     *
     * @return The {@link Boolean} value.
     */
    public static boolean isAccessoryButtonEnabled() {
        return !AetherConfig.CLIENT.disable_accessory_button.get() && !AetherConfig.COMMON.use_default_accessories_menu.get();
    }

    public static AccessoryButton setupAccessoryButton(Screen screen, ScreenOffset offsets) {
        AbstractContainerScreen<?> containerScreen = canCreateAccessoryButtonForScreen(screen);
        if (containerScreen == null) {
            return null;
        }

        AbstractContainerScreenAccessor accessor = (AbstractContainerScreenAccessor) containerScreen;
        return new AccessoryButton(containerScreen, accessor.aether$getLeftPos() + offsets.x(), accessor.aether$getTopPos() + offsets.y(), AetherAccessoriesScreen.ACCESSORIES_BUTTON);
    }

    public static void openAccessoryMenu() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || ClientAccess.overlay(minecraft) != null || ClientAccess.screen(minecraft) != null) {
            return;
        }

        if (AetherConfig.COMMON.use_default_accessories_menu.get()
                || AetherConfig.CLIENT.disable_accessory_button.get()
                || !AetherKeys.OPEN_ACCESSORY_INVENTORY.consumeClick()) {
            return;
        }

        if (minecraft.gameMode != null && minecraft.gameMode.isServerControlledInventory()) {
            minecraft.player.sendOpenInventory();
            return;
        }

        AetherPacketSender.sendToServer(new OpenAccessoriesPacket(ItemStack.EMPTY));
        shouldAddButton = false;
    }

    public static void closeContainerMenu(int key, int action) {
        Minecraft minecraft = Minecraft.getInstance();
        if (!(ClientAccess.screen(minecraft) instanceof AbstractContainerScreen<?> abstractContainerScreen)) {
            return;
        }

        if (!AetherConfig.COMMON.use_default_accessories_menu.get()
                && !AetherConfig.CLIENT.disable_accessory_button.get()
                && AetherKeys.OPEN_ACCESSORY_INVENTORY.matches(new KeyEvent(key, 0, 0))
                && (action == InputConstants.PRESS || action == InputConstants.REPEAT)) {
            abstractContainerScreen.onClose();
        }
    }

    @Nullable
    private static AbstractContainerScreen<?> canCreateAccessoryButtonForScreen(Screen screen) {
        if (screen instanceof InventoryScreen
                || screen instanceof CreativeModeInventoryScreen
                || (screen instanceof AetherAccessoriesScreen && shouldAddButton)) {
            return (AbstractContainerScreen<?>) screen;
        } else if (screen instanceof AetherAccessoriesScreen) {
            shouldAddButton = true;
        }
        return null;
    }
}
