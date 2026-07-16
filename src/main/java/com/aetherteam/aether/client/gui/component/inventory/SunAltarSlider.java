package com.aetherteam.aether.client.gui.component.inventory;

import com.aetherteam.aether.network.packet.serverbound.SunAltarUpdatePacket;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import com.aetherteam.aether.network.AetherPacketSender;

public class SunAltarSlider extends AbstractSliderButton {
    private final Component title;
    private final int timeScale;
    private final BlockPos altarPos;

    public SunAltarSlider(int x, int y, int width, int height, Component title, double value, int timeScale, BlockPos altarPos) {
        super(x, y, width, height, title, value);
        this.title = title;
        this.timeScale = timeScale;
        this.altarPos = altarPos;
        this.updateMessage();
    }

    @Override
    protected void applyValue() {
        long time = (long) (this.value * this.timeScale);
        AetherPacketSender.sendToServer(new SunAltarUpdatePacket(time, this.timeScale, this.altarPos));
    }

    @Override
    protected void updateMessage() {
        long time = Math.floorMod((long) (this.value * this.timeScale), this.timeScale);
        this.setMessage(this.title.copy().append(": ").append(Component.literal(Long.toString(time))));
    }
}
