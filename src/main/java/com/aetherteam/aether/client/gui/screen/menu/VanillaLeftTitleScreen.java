package com.aetherteam.aether.client.gui.screen.menu;

import com.aetherteam.aether.client.gui.screen.menu.logo.LeftLogoRenderer;
import com.aetherteam.aether.client.gui.screen.menu.splash.AetherSplashRenderer;
import com.aetherteam.aether.mixin.mixins.client.accessor.SplashRendererAccessor;
import com.aetherteam.aether.mixin.mixins.client.accessor.TitleScreenAccessor;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * A left-aligned variant of Minecraft's title screen.
 */
public class VanillaLeftTitleScreen extends TitleScreen implements TitleScreenBehavior, CustomBranding {
    private Map<Component, AbstractWidget> widgetsByName = new HashMap<>();

    public VanillaLeftTitleScreen() {
        super();
        TitleScreenAccessor accessor = ((TitleScreenAccessor) this);
        accessor.aether$setFading(true);
        accessor.aether$setLogoRenderer(new LeftLogoRenderer(false));
    }

    @Override
    protected void init() {
        TitleScreenAccessor accessor = (TitleScreenAccessor) this;
        if (accessor.aether$getLogoRenderer() instanceof LeftLogoRenderer leftLogoRenderer) {
            leftLogoRenderer.screenWidth = this.width;
        }
        super.init();
        if (this.minecraft != null) {
            accessor.aether$setSplash(new AetherSplashRenderer(true, ((SplashRendererAccessor) accessor.aether$getSplash()).aether$getSplash()));
        }
        this.setupButtons();
        this.widgetsByName = this.children().stream().filter(e -> e instanceof AbstractWidget).map(e -> (AbstractWidget) e)
            .collect(Collectors.toMap(AbstractWidget::getMessage, e -> e));
    }

    /**
     * Aligns all buttons to the left.
     */
    public void setupButtons() {
        int buttonCount = 0;
        for (GuiEventListener child : this.children()) {
            if (child instanceof AbstractWidget abstractWidget) {
                if (TitleScreenBehavior.isImageButton(abstractWidget.getMessage())) {
                    abstractWidget.visible = false; // The visibility handling is necessary here to avoid a bug where the buttons will render in the center of the screen before they have a specified offset.
                }
                if (abstractWidget instanceof Button button) { // Left alignment.
                    Component buttonText = button.getMessage();
                    if (TitleScreenBehavior.isMainButton(buttonText)) {
                        button.setX(47);
                        button.setY(80 + buttonCount * 25);
                        button.setWidth(200);
                        buttonCount++;
                    }
                }
            }
        }
    }

    /**
     * [CODE COPY] - {@link TitleScreen#render(GuiGraphics, int, int, float)}.<br><br>
     * Modified and abstracted using {@link TitleScreenBehavior}.
     */
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        int xOffset = 0;
        TitleScreenBehavior.super.handleImageButtons(this, xOffset);
        TitleScreenBehavior.super.handleEssentialButtonsForLeftMenu(this);
    }

    @Override
    public boolean forEachLineBranding(boolean includeMC, boolean reverse, BiConsumer<Integer, String> lineConsumer, GuiGraphics guiGraphics, int i) {
        return false;
    }

    @Override
    public boolean forEachAboveCopyrightLineBranding(BiConsumer<Integer, String> lineConsumer, GuiGraphics guiGraphics, int i) {
        return false;
    }

    @Override
    public Map<Component, AbstractWidget> getWidgetsByName() {
        return this.widgetsByName;
    }
}
