package com.aetherteam.aether.client.gui.component.skins;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.mixin.mixins.client.accessor.ButtonBuilderAccessor;
import com.aetherteam.aether.accessories.client.gui.ButtonEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public class RefreshButton extends Button {
    public static final WidgetSprites REFRESH_WIDGET = new WidgetSprites(Identifier.fromNamespaceAndPath(Aether.MODID, "skins/refresh_button"), Identifier.fromNamespaceAndPath(Aether.MODID, "skins/refresh_button_highlighted"), Identifier.fromNamespaceAndPath(Aether.MODID, "skins/refresh_button_highlighted"));

    public static final int reboundMax = 1200;
    public static int reboundTimer = 0;
    private final Event<ButtonEvents.AdjustRendering> adjustRenderingEvent = EventFactory.createArrayBacked(ButtonEvents.AdjustRendering.class, listeners -> (button, guiGraphics, texture, x, y, width, height) -> {
        for (ButtonEvents.AdjustRendering listener : listeners) {
            if (listener.render(button, guiGraphics, texture, x, y, width, height)) {
                return true;
            }
        }
        return false;
    });

    public RefreshButton(Builder builder) {
        super(((ButtonBuilderAccessor) builder).aether$getX(), ((ButtonBuilderAccessor) builder).aether$getY(), ((ButtonBuilderAccessor) builder).aether$getWidth(), ((ButtonBuilderAccessor) builder).aether$getHeight(), ((ButtonBuilderAccessor) builder).aether$getMessage(), ((ButtonBuilderAccessor) builder).aether$getOnPress(), ((ButtonBuilderAccessor) builder).aether$getCreateNarration());
        this.setTooltip(((ButtonBuilderAccessor) builder).aether$getTooltip());
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        Identifier location = REFRESH_WIDGET.get(this.isActive(), this.isHoveredOrFocused());
        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, location, this.getX(), this.getY(), this.getWidth(), this.getHeight());
        guiGraphics.centeredText(minecraft.font, this.getMessage(), this.getX() + (this.getWidth() / 2), this.getY() + (this.getHeight() / 2) - 4, 16777215);
    }

    @Override
    public boolean isActive() {
        return super.isActive() && reboundTimer <= 0;
    }

    public Event<ButtonEvents.AdjustRendering> getRenderingEvent() {
        return this.adjustRenderingEvent;
    }
}
