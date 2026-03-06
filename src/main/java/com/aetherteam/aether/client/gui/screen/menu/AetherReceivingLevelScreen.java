package com.aetherteam.aether.client.gui.screen.menu;

import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.block.AetherBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import net.minecraft.client.multiplayer.LevelLoadTracker;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class AetherReceivingLevelScreen extends LevelLoadingScreen {
    private boolean isInAetherPortal;
    private float portalIntensity;
    private float oPortalIntensity;

    public AetherReceivingLevelScreen(LevelLoadTracker levelLoadTracker, Reason reason) {
        super(levelLoadTracker, reason);
        if (Minecraft.getInstance().player != null && Minecraft.getInstance().player.portalProcess != null && Minecraft.getInstance().player.portalProcess.isSamePortal(AetherBlocks.AETHER_PORTAL.get())) {
            var data = Minecraft.getInstance().player.getAttachedOrCreate(AetherDataAttachments.AETHER_PLAYER);
            this.isInAetherPortal = true;
            this.portalIntensity = data.getPortalIntensity();
            this.oPortalIntensity = data.getOldPortalIntensity();
        }
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (this.isInAetherPortal) {
            TextureAtlasSprite sprite = Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getParticleIcon(AetherBlocks.AETHER_PORTAL.get().defaultBlockState());
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, 0, 0, guiGraphics.guiWidth(), guiGraphics.guiHeight());
        }
    }

    @Override
    public void onClose() {
        if (Minecraft.getInstance().player != null && this.isInAetherPortal) {
            var data = Minecraft.getInstance().player.getAttachedOrCreate(AetherDataAttachments.AETHER_PLAYER);
            data.portalIntensity = this.portalIntensity;
            data.oPortalIntensity = this.oPortalIntensity;
        }
        super.onClose();
    }
}
