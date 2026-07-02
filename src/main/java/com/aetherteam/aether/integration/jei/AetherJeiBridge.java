package com.aetherteam.aether.integration.jei;

import net.minecraft.client.gui.screens.Screen;

import java.util.Objects;
import java.util.function.Consumer;

public final class AetherJeiBridge {
    private static Consumer<Screen> overlayLogger = screen -> {
    };

    private AetherJeiBridge() {
    }

    public static void registerOverlayLogger(Consumer<Screen> logger) {
        overlayLogger = Objects.requireNonNull(logger);
    }

    public static void clearOverlayLogger() {
        overlayLogger = screen -> {
        };
    }

    public static void logVisibleOverlayState(Screen screen) {
        overlayLogger.accept(screen);
    }
}
