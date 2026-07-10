package com.aetherteam.aether.accessories.client.gui;

import com.aetherteam.aether.accessories.api.menu.AccessoriesBasedSlot;
import com.aetherteam.aether.accessories.networking.server.ToggleAccessoryRenderPacket;
import com.aetherteam.aether.network.AetherPacketSender;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class ToggleButton extends Button {
    public ToggleButton(int x, int y, AccessoriesBasedSlot slot) {
        super(x, y, 12, 12, Component.empty(), (button) -> {
            boolean shouldRender = !slot.shouldRender();
            slot.setRender(shouldRender);
            AetherPacketSender.sendToServer(new ToggleAccessoryRenderPacket(slot.slotName(), slot.slotIndex(), shouldRender));
        }, DEFAULT_NARRATION);
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
    }
}
