package com.aetherteam.aether.client.gui;

import com.aetherteam.aether.client.event.hooks.GuiAccessoryMenuHooks;
import com.aetherteam.aether.client.event.hooks.GuiPerkScreenHooks;
import com.aetherteam.aether.client.event.hooks.GuiTriviaHooks;
import com.aetherteam.aether.client.gui.component.inventory.AccessoryButton;
import com.aetherteam.aether.integration.jei.AetherJeiBridge;
import com.aetherteam.aether.mixin.mixins.client.accessor.SplashRendererAccessor;
import com.aetherteam.aether.mixin.mixins.client.accessor.TitleScreenAccessor;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.SplashRenderer;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;

import java.util.Calendar;

public final class AetherScreenController {
    private static final boolean JEI_LOADED = FabricLoader.getInstance().isModLoaded("jei");
    private static final boolean TIPS_MOD_LOADED = FabricLoader.getInstance().isModLoaded("tipsmod");

    private AetherScreenController() {
    }

    public static void afterInit(Minecraft client, Screen screen, int scaledWidth, int scaledHeight) {
        configureScreen(screen);
        ScreenEvents.afterExtract(screen).register((currentScreen, guiGraphics, mouseX, mouseY, tickDelta) ->
                renderScreenOverlay(currentScreen, guiGraphics));
    }

    private static void configureScreen(Screen screen) {
        if (screen instanceof TitleScreen titleScreen) {
            Calendar calendar = Calendar.getInstance();
            if (calendar.get(Calendar.MONTH) + 1 == 7 && calendar.get(Calendar.DATE) == 22) {
                TitleScreenAccessor accessor = (TitleScreenAccessor) titleScreen;
                SplashRenderer splashRenderer = accessor.aether$getSplash();
                Component splash = ((SplashRendererAccessor) splashRenderer).aether$getSplash();
                if (!"Happy anniversary to the Aether!".equals(splash.getString())) {
                    accessor.aether$setSplash(new SplashRenderer(Component.literal("Happy anniversary to the Aether!")));
                }
            }
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
        AetherJeiBridge.logVisibleOverlayState(screen);
    }
}
