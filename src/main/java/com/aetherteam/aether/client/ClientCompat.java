package com.aetherteam.aether.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.locale.Language;

import javax.annotation.Nullable;

public final class ClientCompat {
    private ClientCompat() {
    }

    @Nullable
    public static Screen screen(Minecraft minecraft) {
        return minecraft.gui.screen();
    }

    public static void setScreen(Minecraft minecraft, @Nullable Screen screen) {
        minecraft.gui.setScreen(screen);
    }

    @Nullable
    public static Overlay overlay(Minecraft minecraft) {
        return minecraft.gui.overlay();
    }

    public static boolean hasTranslation(String key) {
        return Language.getInstance().has(key);
    }
}
