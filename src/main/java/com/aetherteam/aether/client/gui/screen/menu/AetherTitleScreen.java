package com.aetherteam.aether.client.gui.screen.menu;

import com.aetherteam.aether.AetherConfig;
import com.aetherteam.aether.client.AetherSoundEvents;
import com.aetherteam.aether.client.gui.component.menu.AetherMenuButton;
import com.aetherteam.aether.client.gui.screen.menu.logo.AetherLogoRenderer;
import com.aetherteam.aether.client.gui.screen.menu.splash.AetherSplashRenderer;
import com.aetherteam.aether.mixin.mixins.client.accessor.SplashRendererAccessor;
import com.aetherteam.aether.mixin.mixins.client.accessor.TitleScreenAccessor;
import net.minecraft.core.Holder;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.Music;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class AetherTitleScreen extends TitleScreen implements TitleScreenBehavior, CustomBranding {
    public static final Music MENU = new Music(Holder.direct(AetherSoundEvents.MUSIC_MENU.get()), 20, 600, true);
    private final boolean alignedLeft;
    private Map<Component, AbstractWidget> widgetsByName = new HashMap<>();

    public int buttonRows = 0;
    public int lastY = 0;

    public AetherTitleScreen() {
        this(false);
    }

    public AetherTitleScreen(boolean alignedLeft) {
        super();
        this.alignedLeft = alignedLeft;
        TitleScreenAccessor accessor = ((TitleScreenAccessor) this);
        accessor.aether$setFading(true);
        accessor.aether$setLogoRenderer(new AetherLogoRenderer(false, this.alignedLeft));
    }

    @Override
    protected void init() {
        TitleScreenAccessor accessor = (TitleScreenAccessor) this;
        this.buttonRows = 0;
        this.lastY = 0;
        super.init();
        if (this.minecraft != null) {
            accessor.aether$setSplash(new AetherSplashRenderer(this.alignedLeft, ((SplashRendererAccessor) accessor.aether$getSplash()).aether$getSplash()));
        }
        this.setupButtons();
        this.widgetsByName = this.children().stream().filter(e -> e instanceof AbstractWidget).map(e -> (AbstractWidget) e)
            .collect(Collectors.toMap(AbstractWidget::getMessage, e -> e));
    }

    public void setupButtons() {
        if (AetherConfig.CLIENT.enable_server_button.get()) {
            Component component = ((TitleScreenAccessor) this).callGetMultiplayerDisabledReason();
            boolean flag = component == null;
            Tooltip tooltip = component != null ? Tooltip.create(component) : null;
            Button serverButton = this.addRenderableWidget(Button.builder(Component.translatable("gui.aether.menu.server"), (button) -> {
                ServerData serverData = new ServerData("OATS", "oats.aether-mod.net", ServerData.Type.OTHER);
                ConnectScreen.startConnecting(this, this.minecraft, ServerAddress.parseString(serverData.ip), serverData, false, null);
            }).bounds(this.width / 2 - 100, (this.height / 4 + 48) + 24 * 3, 200, 20).tooltip(tooltip).build());
            serverButton.active = flag;
            Predicate<AbstractWidget> predicate = (abstractWidget) -> (abstractWidget.getMessage().equals(Component.translatable("menu.multiplayer")) || abstractWidget.getMessage().equals(Component.translatable("menu.online")));
            this.children().stream()
                .filter(button -> button instanceof AbstractWidget abstractWidget && predicate.test(abstractWidget))
                .toList()
                .forEach(this::removeWidget);
        }
        for (GuiEventListener child : this.children()) {
            if (child instanceof AbstractWidget abstractWidget) {
                Component buttonText = abstractWidget.getMessage();
                if (TitleScreenBehavior.isImageButton(buttonText)) {
                    abstractWidget.visible = false; // The visibility handling is necessary here to avoid a bug where the buttons will render in the center of the screen before they have a specified offset.
                }
            }
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.extractRenderState(guiGraphics, mouseX, mouseY, partialTicks);
        int xOffset = 0;
        for (GuiEventListener child : this.children()) {
            if (child instanceof AetherMenuButton aetherButton) { // Smoothly shifts the Aether-styled buttons to the right slightly when hovered over.
                if (aetherButton.isMouseOver(mouseX, mouseY)) {
                    if (aetherButton.hoverOffset < 15) {
                        aetherButton.hoverOffset += 2;
                    }
                } else {
                    if (aetherButton.hoverOffset > 0) {
                        aetherButton.hoverOffset -= 2;
                    }
                }
            }
        }
        TitleScreenBehavior.super.handleImageButtons(this, xOffset);
        if (this.alignedLeft) {
            TitleScreenBehavior.super.handleEssentialButtonsForLeftMenu(this);
        }
    }

    @Override
    public boolean forEachLineBranding(boolean includeMC, boolean reverse, BiConsumer<Integer, String> lineConsumer, GuiGraphicsExtractor guiGraphics, int i) {
        return false;
    }

    @Override
    public boolean forEachAboveCopyrightLineBranding(BiConsumer<Integer, String> lineConsumer, GuiGraphicsExtractor guiGraphics, int i) {
        return false;
    }

    /**
     * Changes main menu buttons into Aether-styled main menu buttons.<br><br>
     * Warning for "unchecked" is suppressed because the buttons should always be able to be cast.
     *
     * @param renderable A renderable widget.
     * @return A new renderable widget.
     */
    @Override
    @SuppressWarnings("unchecked")
    protected <T extends GuiEventListener & Renderable & NarratableEntry> T addRenderableWidget(T renderable) {
        if (renderable instanceof Button button) {
            if (TitleScreenBehavior.isMainButton(button.getMessage())) {
                AetherMenuButton aetherButton = new AetherMenuButton(this, button);
                Component buttonText = aetherButton.getMessage();

                // Sets button values that determine their positioning on the screen.
                if (this.isAlignedLeft()) {
                    this.buttonRows++;
                } else {
                    if (this.lastY < aetherButton.originalY) {
                        this.lastY = aetherButton.originalY;
                        this.buttonRows++;
                    }
                }
                if (buttonText.equals(Component.translatable("gui.aether.menu.server"))) {
                    aetherButton.serverButton = true;
                    aetherButton.buttonCountOffset = 2;
                } else {
                    aetherButton.buttonCountOffset = this.buttonRows;
                }
                if (AetherConfig.CLIENT.enable_server_button.get() && buttonText.equals(Component.translatable("menu.singleplayer"))) {
                    this.buttonRows++;
                }
                if (this.isAlignedLeft()) { // Changes button positioning dependent on whether the parent title screen is aligned left or not.
                    aetherButton.setX(16);
                    aetherButton.setY(50 + aetherButton.buttonCountOffset * 25);
                    aetherButton.setWidth(200);
                } else {
                    aetherButton.setY(this.height / 4 + 31 + 25 * (aetherButton.buttonCountOffset - 1));
                }
                return (T) super.addRenderableWidget(aetherButton);
            }
        }
        return super.addRenderableWidget(renderable);
    }

    public boolean isAlignedLeft() {
        return this.alignedLeft;
    }

    @Override
    public Map<Component, AbstractWidget> getWidgetsByName() {
        return this.widgetsByName;
    }
}
