package com.aetherteam.aether.client.gui.component.skins;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.mixin.mixins.client.accessor.ButtonBuilderAccessor;
import com.aetherteam.aether.client.gui.component.ButtonEvents;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public class ChangeSkinButton extends Button {
    public static final WidgetSprites APPLY_WIDGET = new WidgetSprites(Identifier.fromNamespaceAndPath(Aether.MODID, "skins/apply_button"), Identifier.fromNamespaceAndPath(Aether.MODID, "skins/apply_button_disabled"), Identifier.fromNamespaceAndPath(Aether.MODID, "skins/apply_button_highlighted"));
    public static final WidgetSprites REMOVE_WIDGET = new WidgetSprites(Identifier.fromNamespaceAndPath(Aether.MODID, "skins/remove_button"), Identifier.fromNamespaceAndPath(Aether.MODID, "skins/remove_button_disabled"), Identifier.fromNamespaceAndPath(Aether.MODID, "skins/remove_button_highlighted"));

    private final ButtonType buttonType;
    private final Event<ButtonEvents.AdjustRendering> adjustRenderingEvent = EventFactory.createArrayBacked(ButtonEvents.AdjustRendering.class, listeners -> (button, guiGraphics, texture, x, y, width, height) -> {
        for (ButtonEvents.AdjustRendering listener : listeners) {
            if (listener.render(button, guiGraphics, texture, x, y, width, height)) {
                return true;
            }
        }
        return false;
    });

    public ChangeSkinButton(ButtonType buttonType, Builder builder) {
        super(((ButtonBuilderAccessor) builder).aether$getX(), ((ButtonBuilderAccessor) builder).aether$getY(), ((ButtonBuilderAccessor) builder).aether$getWidth(), ((ButtonBuilderAccessor) builder).aether$getHeight(), ((ButtonBuilderAccessor) builder).aether$getMessage(), ((ButtonBuilderAccessor) builder).aether$getOnPress(), ((ButtonBuilderAccessor) builder).aether$getCreateNarration());
        this.setTooltip(((ButtonBuilderAccessor) builder).aether$getTooltip());
        this.buttonType = buttonType;
        this.active = false;
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
        Identifier location;
        if (this.buttonType == ButtonType.APPLY) {
            location = APPLY_WIDGET.get(this.isActive(), this.isHovered());
        } else {
            location = REMOVE_WIDGET.get(this.isActive(), this.isHovered());
        }
        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, location, this.getX(), this.getY(), this.getWidth(), this.getHeight());
    }

    public enum ButtonType {
        APPLY,
        REMOVE
    }

    public Event<ButtonEvents.AdjustRendering> getRenderingEvent() {
        return this.adjustRenderingEvent;
    }
}
