package com.aetherteam.aether.client.gui.component.skins;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.mixin.mixins.client.accessor.ButtonBuilderAccessor;
import com.aetherteam.aether.accessories.client.gui.ButtonEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public class PatreonButton extends Button {
    public static final WidgetSprites LARGE_WIDGET = new WidgetSprites(Identifier.fromNamespaceAndPath(Aether.MODID, "skins/large_button"), Identifier.fromNamespaceAndPath(Aether.MODID, "skins/large_button_highlighted"), Identifier.fromNamespaceAndPath(Aether.MODID, "skins/large_button_highlighted"));
    public static final WidgetSprites SMALL_WIDGET = new WidgetSprites(Identifier.fromNamespaceAndPath(Aether.MODID, "skins/small_button"), Identifier.fromNamespaceAndPath(Aether.MODID, "skins/small_button_highlighted"), Identifier.fromNamespaceAndPath(Aether.MODID, "skins/small_button_highlighted"));

    private final boolean small;
    private final Event<ButtonEvents.AdjustRendering> adjustRenderingEvent = EventFactory.createArrayBacked(ButtonEvents.AdjustRendering.class, listeners -> (button, guiGraphics, texture, x, y, width, height) -> {
        for (ButtonEvents.AdjustRendering listener : listeners) {
            if (listener.render(button, guiGraphics, texture, x, y, width, height)) {
                return true;
            }
        }
        return false;
    });

    public PatreonButton(Builder builder) {
        this(builder, false);
    }

    public PatreonButton(Builder builder, boolean small) {
        super(((ButtonBuilderAccessor) builder).aether$getX(), ((ButtonBuilderAccessor) builder).aether$getY(), ((ButtonBuilderAccessor) builder).aether$getWidth(), ((ButtonBuilderAccessor) builder).aether$getHeight(), ((ButtonBuilderAccessor) builder).aether$getMessage(), ((ButtonBuilderAccessor) builder).aether$getOnPress(), ((ButtonBuilderAccessor) builder).aether$getCreateNarration());
        this.setTooltip(((ButtonBuilderAccessor) builder).aether$getTooltip());
        this.small = small;
    }

    @Override
    protected void renderContents(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        Identifier location = LARGE_WIDGET.get(this.isActive(), this.isHoveredOrFocused());
        if (this.small) {
            location = SMALL_WIDGET.get(this.isActive(), this.isHoveredOrFocused());
        }
        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, location, this.getX(), this.getY(), this.getWidth(), this.getHeight());
        guiGraphics.drawCenteredString(minecraft.font, this.getMessage(), this.getX() + (this.getWidth() / 2), this.getY() + (this.getHeight() / 2) - 4, 16777215);
    }

    public Event<ButtonEvents.AdjustRendering> getRenderingEvent() {
        return this.adjustRenderingEvent;
    }
}
