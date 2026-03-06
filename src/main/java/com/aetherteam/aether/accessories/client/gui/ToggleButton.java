package com.aetherteam.aether.accessories.client.gui;

import com.aetherteam.aether.accessories.api.menu.AccessoriesBasedSlot;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class ToggleButton extends Button {
    private final AccessoriesBasedSlot slot;

    private ToggleButton(int x, int y, AccessoriesBasedSlot slot) {
        super(x, y, 12, 12, Component.empty(), (button) -> slot.toggleRender(), DEFAULT_NARRATION);
        this.slot = slot;
    }

    public static ToggleButton ofSlot(int x, int y, int z, AccessoriesBasedSlot slot) {
        return new ToggleButton(x, y, slot);
    }

    public AccessoriesBasedSlot slot() {
        return this.slot;
    }

    @Override
    protected void renderContents(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
    }
}
