package com.aetherteam.aether.client.gui.component.inventory;

import com.aetherteam.aether.client.gui.screen.inventory.AetherAccessoriesScreen;
import com.aetherteam.aether.mixin.mixins.client.accessor.AbstractContainerScreenAccessor;
import com.aetherteam.aether.network.packet.serverbound.OpenAccessoriesPacket;
import com.aetherteam.aether.network.packet.serverbound.OpenInventoryPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import com.aetherteam.aether.network.PacketDistributor;

/**
 * Opens the {@link AetherAccessoriesScreen} instead.
 */
public class AccessoryButton extends ImageButton {
    private final AbstractContainerScreen<?> parentScreen;

    public AccessoryButton(AbstractContainerScreen<?> parentScreen, int x, int y, WidgetSprites sprites) {
        super(x, y, 12, 8, sprites,
                (button) -> {
                    Minecraft minecraft = Minecraft.getInstance();
                    Player player = minecraft.player;
                    if (player != null) {
                        ItemStack stack = player.containerMenu.getCarried();
                        player.containerMenu.setCarried(ItemStack.EMPTY);

                        if (parentScreen instanceof AetherAccessoriesScreen) {
                            InventoryScreen inventory = new InventoryScreen(player);
                            minecraft.setScreen(inventory);
                            player.inventoryMenu.setCarried(stack);
                            PacketDistributor.sendToServer(new OpenInventoryPacket(stack));
                        } else {
                            PacketDistributor.sendToServer(new OpenAccessoriesPacket(stack));
                        }
                    }
                });
        this.parentScreen = parentScreen;
    }

    public void updateButtonState() {
        AbstractContainerScreenAccessor accessor = (AbstractContainerScreenAccessor) this.parentScreen;
        Tuple<Integer, Integer> offsets = AetherAccessoriesScreen.getButtonOffset(this.parentScreen);
        this.setX(accessor.aether$getLeftPos() + offsets.getA());
        this.setY(accessor.aether$getTopPos() + offsets.getB());
        if (this.parentScreen instanceof CreativeModeInventoryScreen screen) {
            boolean isInventoryTab = screen.isInventoryOpen();
            this.active = isInventoryTab;
            this.visible = isInventoryTab;
        } else if (this.parentScreen instanceof AetherAccessoriesScreen screen) {
            boolean hasButton = screen.getMenu().hasButton;
            this.active = hasButton;
            this.visible = hasButton;
        } else {
            this.active = true;
            this.visible = true;
        }
    }
}
