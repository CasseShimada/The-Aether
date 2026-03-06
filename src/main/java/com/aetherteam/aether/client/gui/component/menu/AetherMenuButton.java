package com.aetherteam.aether.client.gui.component.menu;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.client.gui.screen.menu.AetherTitleScreen;
import com.aetherteam.aether.mixin.mixins.client.accessor.ButtonBuilderAccessor;
import com.aetherteam.aether.mixin.mixins.client.accessor.ButtonAccessor;
import com.aetherteam.aether.accessories.client.gui.ButtonEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public class AetherMenuButton extends Button {
    private static final WidgetSprites AETHER_WIDGETS = new WidgetSprites(Identifier.fromNamespaceAndPath(Aether.MODID, "title/button"), Identifier.fromNamespaceAndPath(Aether.MODID, "title/button_highlighted"));
    private static final WidgetSprites AETHER_WIDGETS_SMALL = new WidgetSprites(Identifier.fromNamespaceAndPath(Aether.MODID, "title/button"), Identifier.fromNamespaceAndPath(Aether.MODID, "title/button_highlighted_small"));
    public final int originalX;
    public final int originalY;
    public int hoverOffset;
    public int buttonCountOffset;
    public boolean serverButton;
    private final Event<ButtonEvents.AdjustRendering> adjustRenderingEvent = EventFactory.createArrayBacked(ButtonEvents.AdjustRendering.class, listeners -> (button, guiGraphics, texture, x, y, width, height) -> {
        for (ButtonEvents.AdjustRendering listener : listeners) {
            if (listener.render(button, guiGraphics, texture, x, y, width, height)) {
                return true;
            }
        }
        return false;
    });

    public AetherMenuButton(AetherTitleScreen screen, Builder builder) {
        this((ButtonBuilderAccessor) builder);
    }

    private AetherMenuButton(ButtonBuilderAccessor builderAccessor) {
        super(builderAccessor.aether$getX(), builderAccessor.aether$getY(), builderAccessor.aether$getWidth(), builderAccessor.aether$getHeight(), builderAccessor.aether$getMessage(), builderAccessor.aether$getOnPress(), builderAccessor.aether$getCreateNarration());
        this.setTooltip(builderAccessor.aether$getTooltip());
        this.originalX = this.getX();
        this.originalY = this.getY();
        this.hoverOffset = 0;
    }

    public AetherMenuButton(AetherTitleScreen screen, Button oldButton) {
        this(screen, new Builder(oldButton.getMessage(), ((ButtonAccessor) oldButton).aether$getOnPress()).bounds(oldButton.getX(), oldButton.getY(), oldButton.getWidth(), oldButton.getHeight()).createNarration(((ButtonAccessor) oldButton).aether$getCreateNarration()));
        oldButton.visible = false;
        oldButton.active = false;
    }

    @Override
    protected void renderContents(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;

        Identifier location = this.getWidth() < 100 ? AETHER_WIDGETS_SMALL.get(this.isActive(), this.isHoveredOrFocused()) : AETHER_WIDGETS.get(this.isActive(), this.isHoveredOrFocused());

        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, location, this.getX() + this.hoverOffset, this.getY(), this.getWidth(), this.getHeight());
        int textX = this.getX() + 35 + this.hoverOffset;
        int textY = this.getY() + (this.height - 8) / 2;
        guiGraphics.drawString(font, this.getMessage(), textX, textY, this.getTextColor(mouseX, mouseY) | Mth.ceil(this.alpha * 255.0F) << 24);
    }

    /**
     * Determines the color for the button text depending on if its hovered over.
     *
     * @param mouseX The {@link Integer} for the mouse's x-position.
     * @param mouseY The {@link Integer} for the mouse's y-position.
     * @return The decimal {@link Integer} for the color.
     */
    public int getTextColor(int mouseX, int mouseY) {
        if (!this.serverButton) {
            return this.isMouseOver(mouseX, mouseY) ? 11842776 : 13948116;
        } else {
            return this.isMouseOver(mouseX, mouseY) ? 13746759 : 15457113;
        }
    }

    public Event<ButtonEvents.AdjustRendering> getRenderingEvent() {
        return this.adjustRenderingEvent;
    }
}
