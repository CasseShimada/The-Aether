package com.aetherteam.aether.client.event.hooks;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.client.gui.component.inventory.AccessoryButton;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;

import java.lang.reflect.Method;

public final class ClientScreenHooks {
    private static final boolean JEI_LOADED = FabricLoader.getInstance().isModLoaded("jei");
    private static final boolean TIPS_MOD_LOADED = FabricLoader.getInstance().isModLoaded("tipsmod");
    private static boolean jeiOverlayLoggerResolved;
    private static Method jeiOverlayLogger;

    private ClientScreenHooks() {
    }

    public static void afterInit(Minecraft client, Screen screen, int scaledWidth, int scaledHeight) {
        configureScreen(screen);
        ScreenEvents.afterExtract(screen).register(ClientScreenHooks::afterExtract);
    }

    private static void afterExtract(Screen currentScreen, GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float tickDelta) {
        renderScreenOverlay(currentScreen, guiGraphics);
    }

    private static void configureScreen(Screen screen) {
        if (screen instanceof TitleScreen titleScreen) {
            TitleScreenHooks.setCustomSplashText(titleScreen);
        }

        var offsets = com.aetherteam.aether.client.gui.screen.inventory.AetherAccessoriesScreen.getButtonOffset(screen);
        var inventoryAccessoryButton = GuiAccessoryMenuHooks.setupAccessoryButton(screen, offsets);
        if (inventoryAccessoryButton != null && GuiAccessoryMenuHooks.isAccessoryButtonEnabled()) {
            Screens.getWidgets(screen).add(inventoryAccessoryButton);
        }

        GridLayout layout = GuiPerkScreenHooks.setupPerksButtons(screen);
        if (layout != null && !GuiAccessoryMenuHooks.isAccessoryButtonEnabled()) {
            addPerkWidgets(screen, layout);
        }
    }

    private static void addPerkWidgets(Screen screen, GridLayout layout) {
        layout.visitWidgets(widget -> {
            if (widget instanceof AbstractWidget abstractWidget) {
                Screens.getWidgets(screen).add(abstractWidget);
            }
        });
    }

    private static void renderScreenOverlay(Screen currentScreen, GuiGraphicsExtractor guiGraphics) {
        updateAccessoryButtons(currentScreen);
        logJeiOverlayState(currentScreen);
        if (!TIPS_MOD_LOADED) {
            GuiTriviaHooks.drawTrivia(currentScreen, guiGraphics);
        }
        GuiTriviaHooks.drawAetherTravelMessage(currentScreen, guiGraphics);
    }

    private static void updateAccessoryButtons(Screen currentScreen) {
        Screens.getWidgets(currentScreen).forEach(widget -> {
            if (widget instanceof AccessoryButton accessoryButton) {
                accessoryButton.updateButtonState();
            }
        });
    }

    private static void logJeiOverlayState(Screen screen) {
        if (!JEI_LOADED) {
            return;
        }

        if (!jeiOverlayLoggerResolved) {
            jeiOverlayLoggerResolved = true;
            try {
                Class<?> pluginClass = Class.forName("com.aetherteam.aether.integration.jei.AetherJEIPlugin");
                jeiOverlayLogger = pluginClass.getMethod("logVisibleOverlayState", Screen.class);
            } catch (ReflectiveOperationException | LinkageError exception) {
                Aether.LOGGER.debug("Failed to resolve JEI overlay logger", exception);
                jeiOverlayLogger = null;
            }
        }

        if (jeiOverlayLogger == null) {
            return;
        }

        try {
            jeiOverlayLogger.invoke(null, screen);
        } catch (ReflectiveOperationException exception) {
            Aether.LOGGER.debug("Failed to query JEI overlay state", exception);
        }
    }
}
