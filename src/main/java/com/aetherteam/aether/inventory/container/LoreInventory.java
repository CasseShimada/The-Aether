package com.aetherteam.aether.inventory.container;

import com.aetherteam.aether.advancement.AetherAdvancementTriggers;
import com.aetherteam.aether.inventory.menu.LoreBookMenu;
import com.aetherteam.aether.network.packet.serverbound.LoreExistsPacket;
import com.aetherteam.aether.network.AetherPacketSender;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class LoreInventory extends SimpleContainer {
    public final Player player;
    public LoreBookMenu menu;

    public LoreInventory(Player player) {
        super(1);
        this.player = player;
    }

    /**
     * Ran when a player puts an item in the Book of Lore menu.<br><br>
     * On the client side, a packet {@link LoreExistsPacket} will be sent to the server with the information of whether an item has a lore entry
     * according to {@link LoreBookMenu#loreEntryKeyExists(ItemStack)}, which tells if a translation key for an entry is found for an item.
     * This will change the {@link LoreBookMenu#loreEntryExists} value on the server according to {@link LoreExistsPacket#exists()}.<br><br>
     * On the server side, if {@link LoreBookMenu#loreEntryExists} is found to be true through {@link LoreBookMenu#getLoreEntryExists()},
     * then the item must have a lore entry, so the lore advancement trigger is fired.
     *
     * @param index The {@link Integer} index of the slot.
     * @param stack the {@link ItemStack} trying to be set to the slot.
     */
    @Override
    public void setItem(int index, ItemStack stack) {
        if (!stack.isEmpty()) {
            if (this.player.level().isClientSide() && this.player instanceof LocalPlayer) {
                if (this.menu != null) {
                    AetherPacketSender.sendToServer(new LoreExistsPacket(this.player.getId(), stack, this.menu.loreEntryKeyExists(stack)));
                }
            } else if (this.player instanceof ServerPlayer serverPlayer && this.menu != null && this.menu.getLoreEntryExists()) {
                AetherAdvancementTriggers.LORE_ENTRY.trigger(serverPlayer, stack);
            }
        }
        super.setItem(index, stack);
    }

    public void setMenu(LoreBookMenu menu) {
        this.menu = menu;
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (this.menu != null) {
            this.menu.slotsChanged(this);
        }
    }
}
