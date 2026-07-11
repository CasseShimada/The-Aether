package com.aetherteam.aether.client.gui.screen.inventory;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.AetherConfig;
import com.aetherteam.aether.client.ClientAccess;
import com.aetherteam.aether.client.AetherKeys;
import com.aetherteam.aether.client.gui.component.inventory.ScreenOffset;
import com.aetherteam.aether.client.gui.screen.perks.AetherCustomizationsScreen;
import com.aetherteam.aether.client.gui.screen.perks.MoaSkinsScreen;
import com.aetherteam.aether.inventory.menu.AetherAccessoriesMenu;
import com.aetherteam.aether.mixin.mixins.client.accessor.ScreenAccessor;
import com.aetherteam.aether.network.AetherPacketSender;
import com.aetherteam.aether.network.packet.serverbound.ClearItemPacket;
import com.aetherteam.aether.perk.PerkUtil;
import com.aetherteam.aether.perk.data.User;
import com.aetherteam.aether.perk.data.UserData;
import com.aetherteam.aether.inventory.menu.slot.AccessoriesBasedSlot;
import com.aetherteam.aether.network.packet.serverbound.NukeAccessories;
import com.aetherteam.aether.network.packet.serverbound.ToggleAccessoryRenderPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.recipebook.CraftingRecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * [CODE COPY] - {@link InventoryScreen}.<br><br>
 * Modified to register slots for Aether accessories.
 */
public class AetherAccessoriesScreen extends AbstractRecipeBookScreen<AetherAccessoriesMenu> {
    public static final WidgetSprites ACCESSORIES_BUTTON = new WidgetSprites(Identifier.fromNamespaceAndPath(Aether.MODID, "inventory/accessories_button"), Identifier.fromNamespaceAndPath(Aether.MODID, "inventory/accessories_button_highlighted"));
    public static final WidgetSprites SKINS_BUTTON = new WidgetSprites(Identifier.fromNamespaceAndPath(Aether.MODID, "skins/skins_button"), Identifier.fromNamespaceAndPath(Aether.MODID, "skins/skins_button_highlighted"));
    public static final WidgetSprites CUSTOMIZATION_BUTTON = new WidgetSprites(Identifier.fromNamespaceAndPath(Aether.MODID, "customization/customization_button"), Identifier.fromNamespaceAndPath(Aether.MODID, "customization/customization_button_highlighted"));

    private static final Identifier ACCESSORIES_INVENTORY = Identifier.fromNamespaceAndPath(Aether.MODID, "textures/gui/inventory/accessories.png");
    private static final Identifier ACCESSORIES_INVENTORY_CREATIVE = Identifier.fromNamespaceAndPath(Aether.MODID, "textures/gui/inventory/accessories_creative.png");

    private static final SimpleContainer DESTROY_ITEM_CONTAINER = new SimpleContainer(1);
    private final Map<AccessoriesBasedSlot, ToggleButton> cosmeticButtons = new LinkedHashMap<>();
    private final RecipeBookComponent<?> recipeBookComponent;
    private boolean widthTooNarrow;
    private boolean isRenderButtonHovered;
    @Nullable
    private Slot destroyItemSlot;
    @Nullable
    private ImageButton skinsButton;
    @Nullable
    private ImageButton customizationButton;
    private int nukeCoolDown = 0;


    public AetherAccessoriesScreen(AetherAccessoriesMenu accessoriesMenu, Inventory playerInventory, Component title) {
        this(accessoriesMenu, new CraftingRecipeBookComponent(playerInventory.player.inventoryMenu), playerInventory, title);
    }

    public AetherAccessoriesScreen(AetherAccessoriesMenu accessoriesMenu, RecipeBookComponent<?> recipeBookComponent, Inventory playerInventory, Component title) {
        super(accessoriesMenu, recipeBookComponent, playerInventory, title);
        this.recipeBookComponent = recipeBookComponent;
    }

    @Override
    public void init() {
        super.init();
        this.widthTooNarrow = this.width < 379;
        this.getRecipeBookComponent().init(this.width, this.height, this.minecraft, this.widthTooNarrow);
        this.updateScreenPosition();
        this.addWidget(this.getRecipeBookComponent());
        this.setInitialFocus(this.getRecipeBookComponent());

        if (this.minecraft.player != null && this.getRecipeBookComponent().isVisible()) {
            this.getRecipeBookComponent().toggleVisibility();
            this.updateScreenPosition();
        }

        this.updateRenderButtons();


        // Create perk-related buttons.
        User user = UserData.Client.getClientUser();
        if (user != null) {
            if (!AetherConfig.CLIENT.disable_skins_button.get() || PerkUtil.hasAnyMoaSkins().test(user)) { // Add the skins button if the config is enabled. If not, only display for players with access.
                this.skinsButton = this.createSkinsButton();
                this.addRenderableWidget(this.skinsButton);
            }
            if (PerkUtil.hasDeveloperGlow().test(user) || PerkUtil.hasHalo().test(user)) {
                this.customizationButton = this.createCustomizationButton();
                this.addRenderableWidget(this.customizationButton);
            }
        }
    }

    @Override
    protected ScreenPosition getRecipeBookButtonPosition() {
        return new ScreenPosition(this.leftPos + 142, this.height / 2 - 22);
    }

    @Override
    public void containerTick() {
        if (this.nukeCoolDown > 0) {
            this.nukeCoolDown--;
        }
    }

    /**
     * [CODE COPY] - {@link RecipeBookComponent#updateScreenPosition(int, int)}.
     */
    private void updateScreenPosition() {
        int i;
        if (this.getRecipeBookComponent().isVisible() && !this.widthTooNarrow) {
            int offset = 200 - this.creativeXOffset();
            i = 177 + (this.width - this.backgroundWidth() - offset) / 2;
        } else {
            i = (this.width - this.backgroundWidth()) / 2;
        }
        this.leftPos = i;
        this.updateRenderButtons();
    }

    /**
     * Creates the button for the {@link MoaSkinsScreen}.
     *
     * @return The {@link ImageButton}.
     */
    private ImageButton createSkinsButton() {
        ImageButton skinsButton = new ImageButton(this.leftPos - 22, this.topPos + 2, 20, 20, SKINS_BUTTON,
                (pressed) -> ClientAccess.setScreen(this.minecraft, new MoaSkinsScreen(this)),
                Component.translatable("gui.aether.accessories.skins_button"));
        skinsButton.setTooltip(Tooltip.create(Component.translatable("gui.aether.accessories.skins_button")));
        return skinsButton;
    }

    /**
     * Creates the button for the {@link AetherCustomizationsScreen}.
     *
     * @return The {@link ImageButton}.
     */
    private ImageButton createCustomizationButton() {
        ImageButton customizationButton = new ImageButton(this.leftPos - 22, this.topPos + 24, 20, 20, CUSTOMIZATION_BUTTON,
                (pressed) -> ClientAccess.setScreen(this.minecraft, new AetherCustomizationsScreen(this)),
                Component.translatable("gui.aether.accessories.customization_button"));
        customizationButton.setTooltip(Tooltip.create(Component.translatable("gui.aether.accessories.customization_button")));
        return customizationButton;
    }

    private void updateRenderButtons() {
        ScreenAccessor screenAccessor = (ScreenAccessor) this;
        screenAccessor.aether$getNarratables().removeIf(widget -> widget instanceof ToggleButton);
        this.children().removeIf(widget -> widget instanceof ToggleButton);
        this.cosmeticButtons.clear();
        for (Slot slot : this.menu.slots) {
            if (slot instanceof AccessoriesBasedSlot accessoriesSlot) {
                ToggleButton slotButton = new ToggleButton(slot.x + this.leftPos + 13, slot.y + this.topPos - 2, accessoriesSlot);

                slotButton.visible = accessoriesSlot.isActive();
                slotButton.active = accessoriesSlot.isActive();

                this.cosmeticButtons.put(accessoriesSlot, this.addWidget(slotButton));
            }
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.updatePerkButtonPositions();
        if (this.getRecipeBookComponent().isVisible() && this.widthTooNarrow) {
            this.extractBackground(guiGraphics, mouseX, mouseY, partialTicks);
            this.getRecipeBookComponent().extractRenderState(guiGraphics, mouseX, mouseY, partialTicks);
        } else {
            this.getRecipeBookComponent().extractRenderState(guiGraphics, mouseX, mouseY, partialTicks);
            super.extractRenderState(guiGraphics, mouseX, mouseY, partialTicks);
            this.getRecipeBookComponent().extractGhostRecipe(guiGraphics, this.isBiggerResultSlot());

            for (var cosmeticButton : this.cosmeticButtons.values()) {
                cosmeticButton.extractRenderState(guiGraphics, mouseX, mouseY, partialTicks);
            }

            boolean isButtonHovered = false;
            for (GuiEventListener widget : this.children()) {
                if (widget instanceof ToggleButton renderButton) {
                    if (renderButton.isHovered()) {
                        isButtonHovered = true;
                    }
                }
            }
            this.isRenderButtonHovered = isButtonHovered;
            LocalPlayer clientPlayer = Minecraft.getInstance().player;
            if (!this.isRenderButtonHovered && clientPlayer != null && clientPlayer.inventoryMenu.getCarried().isEmpty() && this.hoveredSlot != null) {
                Slot slot = this.hoveredSlot;
                if (slot instanceof AccessoriesBasedSlot accessorySlot && !slot.hasItem()) {
                    guiGraphics.setTooltipForNextFrame(this.font, Component.translatable(accessorySlot.slotType().translation()), mouseX, mouseY);
                }
            }

            if (this.minecraft.player != null) {
                if (this.minecraft.player.isCreative() && this.destroyItemSlot == null) {
                    this.destroyItemSlot = new Slot(DESTROY_ITEM_CONTAINER, 0, 172, 142);
                    this.getMenu().slots.add(this.destroyItemSlot);
                } else if (!this.minecraft.player.isCreative() && this.destroyItemSlot != null) {
                    this.getMenu().slots.remove(this.destroyItemSlot);
                    this.destroyItemSlot = null;
                }
            }

            if (this.destroyItemSlot != null && this.isHovering(this.destroyItemSlot.x, this.destroyItemSlot.y, 16, 16, mouseX, mouseY)) {
                guiGraphics.setTooltipForNextFrame(this.font, Component.translatable("inventory.binSlot"), mouseX, mouseY);
            }
        }
        this.extractTooltip(guiGraphics, mouseX, mouseY);
        this.getRecipeBookComponent().extractTooltip(guiGraphics, mouseX, mouseY, this.hoveredSlot);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (this.minecraft.player != null) {
            int i = this.leftPos;
            int j = this.topPos;
            Identifier background = this.minecraft.player.isCreative() ? ACCESSORIES_INVENTORY_CREATIVE : ACCESSORIES_INVENTORY;
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, background, i, j, 0.0F, 0.0F, this.backgroundWidth(), this.imageHeight, 256, 256);
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, background, i + AetherAccessoriesMenu.BACK_SLOT_X, j + AetherAccessoriesMenu.BACK_SLOT_Y, AetherAccessoriesMenu.ACCESSORY_SLOT_BACKGROUND_X, AetherAccessoriesMenu.ACCESSORY_SLOT_BACKGROUND_Y, 18, 18, 256, 256);
            InventoryScreen.extractEntityInInventoryFollowsMouse(guiGraphics, i + 9, j + 8, i + 58, j + 78, 30, 0.1575F, mouseX, mouseY, this.minecraft.player);
        }
    }

    /**
     * @return The {@link Integer} y-offset for the GUI.
     */
    private int creativeXOffset() {
        return this.minecraft.player != null && this.minecraft.player.isCreative() ? 18 : 0;
    }

    private int backgroundWidth() {
        return 176 + this.creativeXOffset();
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        Minecraft minecraft = this.minecraft;
        LocalPlayer clientPlayer = minecraft.player;
        if (clientPlayer != null && clientPlayer.inventoryMenu.getCarried().isEmpty()) {
            if (!this.isRenderButtonHovered) {
                if (this.hoveredSlot != null && this.hoveredSlot.hasItem()) {
                    guiGraphics.setTooltipForNextFrame(this.font, this.hoveredSlot.getItem(), mouseX, mouseY);
                }
            }
        }
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        if (this.minecraft.player != null) {
            guiGraphics.text(this.font, this.title, 115, 6, 4210752, false);
        }
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (this.getRecipeBookComponent().isVisible() && this.widthTooNarrow) {
            this.getRecipeBookComponent().toggleVisibility();
            this.updateScreenPosition();
            return true;
        } else if (AetherKeys.OPEN_ACCESSORY_INVENTORY.matches(event)) {
            LocalPlayer playerEntity = this.minecraft.player;
            if (playerEntity != null) {
                playerEntity.closeContainer();
            }
            return true;
        } else {
            return super.keyPressed(event);
        }
    }

    @Override
    protected boolean isHovering(int rectX, int rectY, int rectWidth, int rectHeight, double pointX, double pointY) {
        if (this.isRenderButtonHovered) {
            return false;
        }
        return (!this.widthTooNarrow || !this.getRecipeBookComponent().isVisible()) && super.isHovering(rectX, rectY, rectWidth, rectHeight, pointX, pointY);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (this.getRecipeBookComponent().mouseClicked(event, doubleClick)) {
            this.setFocused(this.getRecipeBookComponent());
            return true;
        } else {
            return (!this.widthTooNarrow || !this.getRecipeBookComponent().isVisible()) && super.mouseClicked(event, doubleClick);
        }
    }

    /**
     * [CODE COPY] {@link net.minecraft.client.gui.screens.inventory.AbstractContainerScreen}.<br><br>
     * Heavily modified to only have behavior for the item trash slot.
     */
    @Override
    protected void slotClicked(@Nullable Slot slot, int slotId, int mouseButton, ContainerInput type) {
        if (this.minecraft.player != null && this.minecraft.gameMode != null) {
            boolean flag = type == ContainerInput.QUICK_MOVE;
            if (slot != null || type == ContainerInput.QUICK_CRAFT) {
                if (slot == null || slot.mayPickup(this.minecraft.player)) {
                    if (slot == this.destroyItemSlot && this.destroyItemSlot != null && flag) {
                        for (int j = 0; j < this.minecraft.player.inventoryMenu.getItems().size(); ++j) {
                            if (this.nukeCoolDown <= 0) {
                                AetherPacketSender.sendToServer(new NukeAccessories());
                                this.nukeCoolDown = 10;
                            }
                            this.minecraft.gameMode.handleCreativeModeItemAdd(ItemStack.EMPTY, j);
                        }
                    } else {
                        if (slot == this.destroyItemSlot && this.destroyItemSlot != null) {
                            this.getMenu().setCarried(ItemStack.EMPTY);
                            AetherPacketSender.sendToServer(new ClearItemPacket(this.minecraft.player.getId()));
                        }
                    }
                }
            }
            super.slotClicked(slot, slotId, mouseButton, type);
        }
    }

    public RecipeBookComponent<?> getRecipeBookComponent() {
        return this.recipeBookComponent;
    }

    private void updatePerkButtonPositions() {
        boolean recipeBookVisible = this.getRecipeBookComponent().isVisible();
        if (this.skinsButton != null) {
            if (!recipeBookVisible) {
                this.skinsButton.setPosition(this.leftPos - 22, this.topPos + 2);
            } else {
                this.skinsButton.setPosition(this.leftPos + 2, this.topPos - 22);
            }
        }
        if (this.customizationButton != null) {
            if (!recipeBookVisible) {
                this.customizationButton.setPosition(this.leftPos - 22, this.topPos + 24);
            } else {
                this.customizationButton.setPosition(this.leftPos + 24, this.topPos - 22);
            }
        }
    }

//    @Override
//    public boolean canSeeEffects() {
//        int i = this.leftPos + this.imageWidth + 2 + this.creativeXOffset();
//        int j = this.width - i;
//        return j > 13;
//    }

    /**
     * Offsets the accessories screen button based on what screen is currently open.
     *
     * @param screen The current {@link Screen}.
     * @return A {@link ScreenOffset} containing the x and y offsets.
     */
    public static ScreenOffset getButtonOffset(Screen screen) {
        int x = 0;
        int y = 0;
        if (screen instanceof InventoryScreen) {
            x = AetherConfig.CLIENT.button_inventory_x.get();
            y = AetherConfig.CLIENT.button_inventory_y.get();
        }
        if (screen instanceof CreativeModeInventoryScreen) {
            x = AetherConfig.CLIENT.button_creative_x.get();
            y = AetherConfig.CLIENT.button_creative_y.get();
        }
        if (screen instanceof AetherAccessoriesScreen) {
            x = AetherConfig.CLIENT.button_accessories_x.get();
            y = AetherConfig.CLIENT.button_accessories_y.get();
        }
        return new ScreenOffset(x, y);
    }

    private static final class ToggleButton extends Button {
        private ToggleButton(int x, int y, AccessoriesBasedSlot slot) {
            super(x, y, 12, 12, Component.empty(), button -> {
                boolean shouldRender = !slot.shouldRender();
                slot.setRender(shouldRender);
                AetherPacketSender.sendToServer(new ToggleAccessoryRenderPacket(slot.slotName(), slot.slotIndex(), shouldRender));
            }, DEFAULT_NARRATION);
        }

        @Override
        protected void extractContents(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
        }
    }
}
