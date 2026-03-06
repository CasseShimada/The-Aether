package com.aetherteam.aether.client;

import com.aetherteam.aether.client.gui.screen.menu.AetherTitleScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;

public class AetherMenuUtil {
    /**
     * @return Whether the currently active menu is an Aether menu, as a {@link Boolean}.
     */
    public static boolean isAetherMenu() {
        return Minecraft.getInstance().screen instanceof AetherTitleScreen;
    }

    /**
     * @return Whether the currently active menu is a Minecraft menu, as a {@link Boolean}.
     */
    public static boolean isMinecraftMenu() {
        return Minecraft.getInstance().screen instanceof TitleScreen && !isAetherMenu();
    }
}
