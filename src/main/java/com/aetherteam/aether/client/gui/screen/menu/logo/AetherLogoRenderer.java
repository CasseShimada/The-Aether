package com.aetherteam.aether.client.gui.screen.menu.logo;

import com.aetherteam.aether.Aether;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.LogoRenderer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

public class AetherLogoRenderer extends LogoRenderer {
    private static final Identifier AETHER_LOGO = Identifier.fromNamespaceAndPath(Aether.MODID, "textures/gui/title/aether.png");
    private final boolean keepLogoThroughFade;
    private final boolean alignedLeft;

    public AetherLogoRenderer(boolean keepLogoThroughFade, boolean alignedLeft) {
        super(keepLogoThroughFade);
        this.keepLogoThroughFade = keepLogoThroughFade;
        this.alignedLeft = alignedLeft;
    }

    public void renderLogo(GuiGraphics guiGraphics, int screenWidth, float transparency) {
        this.renderLogo(guiGraphics, screenWidth, transparency, 30);
    }

    public void renderLogo(GuiGraphics guiGraphics, int screenWidth, float transparency, int height) {
        int logoX = this.alignedLeft ? 28 : (int) ((screenWidth / 2.0F - (190.0F / 2.0F)));
        int logoY = this.alignedLeft ? 25 : 36;
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, AETHER_LOGO, logoX, logoY, 0, 0, 190, 38, 190, 38);
    }
}
