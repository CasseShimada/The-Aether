package com.aetherteam.aether.accessories.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.resources.Identifier;

public interface ButtonEvents {
    @FunctionalInterface
    interface AdjustRendering {
        boolean render(Button button, GuiGraphics guiGraphics, Identifier texture, int x, int y, int width, int height);
    }
}
