package com.aetherteam.aether.client.gui.component.inventory;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.mixin.mixins.client.accessor.ButtonBuilderAccessor;
import com.aetherteam.aether.accessories.client.gui.ButtonEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public class LorePageButton extends Button {
    private static final WidgetSprites BUTTON_TEXTURES = new WidgetSprites(Identifier.fromNamespaceAndPath(Aether.MODID, "menu/lore_button"), Identifier.fromNamespaceAndPath(Aether.MODID, "menu/lore_button_disabled"), Identifier.fromNamespaceAndPath(Aether.MODID, "menu/lore_button_highlighted"));
    private final Event<ButtonEvents.AdjustRendering> adjustRenderingEvent = EventFactory.createArrayBacked(ButtonEvents.AdjustRendering.class, listeners -> (button, guiGraphics, texture, x, y, width, height) -> {
        for (ButtonEvents.AdjustRendering listener : listeners) {
            if (listener.render(button, guiGraphics, texture, x, y, width, height)) {
                return true;
            }
        }
        return false;
    });

    public LorePageButton(Builder builder) {
        super(((ButtonBuilderAccessor) builder).aether$getX(), ((ButtonBuilderAccessor) builder).aether$getY(), ((ButtonBuilderAccessor) builder).aether$getWidth(), ((ButtonBuilderAccessor) builder).aether$getHeight(), ((ButtonBuilderAccessor) builder).aether$getMessage(), ((ButtonBuilderAccessor) builder).aether$getOnPress(), DEFAULT_NARRATION);
        this.setTooltip(((ButtonBuilderAccessor) builder).aether$getTooltip());
        this.active = false;
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
        Identifier location = BUTTON_TEXTURES.get(this.isActive(), this.isHoveredOrFocused());
        Minecraft minecraft = Minecraft.getInstance();
        Font fontRenderer = minecraft.font;
        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, location, this.getX(), this.getY(), this.width, this.height);
        int color = this.active ? 0xFFFFFF : 0xA0A0A0;
        guiGraphics.centeredText(fontRenderer, this.getMessage(), this.getX() + this.width / 2, this.getY() + (this.height - 8) / 2, color | 255 << 24);
    }

    public Event<ButtonEvents.AdjustRendering> getRenderingEvent() {
        return this.adjustRenderingEvent;
    }
}
