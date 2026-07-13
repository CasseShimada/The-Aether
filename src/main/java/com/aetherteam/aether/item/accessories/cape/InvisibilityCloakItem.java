package com.aetherteam.aether.item.accessories.cape;

import com.aetherteam.aether.AetherConfig;
import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.attachment.AetherPlayerAttachment;
import com.aetherteam.aether.client.AetherKeys;
import com.aetherteam.aether.item.accessories.AccessoryItem;
import com.aetherteam.aether.mixin.mixins.common.accessor.LivingEntityAccessor;
import com.aetherteam.aether.network.packet.clientbound.SetInvisibilityPacket;
import com.aetherteam.aether.accessories.api.slot.SlotReference;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import com.aetherteam.aether.network.AetherPacketSender;

/**
 * Additional invisibility behavior is handled by the client and ability event listeners.
 * The wearer is also hidden from other entities' targeting by accessory ability hooks.
 */
public class InvisibilityCloakItem extends AccessoryItem {
    public InvisibilityCloakItem(Properties properties) {
        super(properties);
    }

    @Override
    public void tick(ItemStack stack, SlotReference reference) {
        LivingEntity livingEntity = reference.entity();
        if (livingEntity.level().isClientSide() && livingEntity instanceof Player player) {
            if (AetherKeys.INVISIBILITY_TOGGLE.consumeClick()) {
                var data = player.getAttachedOrCreate(AetherDataAttachments.AETHER_PLAYER);
                data.setSyncedToServer(player.getId(), AetherPlayerAttachment.INVISIBILITY_ENABLED_SYNC_KEY, !data.isInvisibilityEnabled());
            }
        }
        if (!livingEntity.level().isClientSide() && livingEntity instanceof Player player) {
            var data = player.getAttachedOrCreate(AetherDataAttachments.AETHER_PLAYER);
            if (data.isInvisibilityEnabled()) {
                if (!AetherConfig.SERVER.balance_invisibility_cloak.get()) {
                    data.setSyncedToClients(player.getId(), AetherPlayerAttachment.WEARING_INVISIBILITY_CLOAK_SYNC_KEY, true);
                } else {
                    if (!data.attackedWithInvisibility() && !data.isWearingInvisibilityCloak()) {
                        data.setSyncedToClients(player.getId(), AetherPlayerAttachment.WEARING_INVISIBILITY_CLOAK_SYNC_KEY, true);
                    } else if (data.attackedWithInvisibility() && data.isWearingInvisibilityCloak()) {
                        data.setSyncedToClients(player.getId(), AetherPlayerAttachment.WEARING_INVISIBILITY_CLOAK_SYNC_KEY, false);
                    }
                }
            } else {
                data.setSyncedToClients(player.getId(), AetherPlayerAttachment.WEARING_INVISIBILITY_CLOAK_SYNC_KEY, false);
            }
        }
        if (!livingEntity.level().isClientSide()) {
            if (!livingEntity.isInvisible()) {
                if (livingEntity instanceof Player player) {
                    var data = player.getAttachedOrCreate(AetherDataAttachments.AETHER_PLAYER);
                    if (data.isWearingInvisibilityCloak()) {
                        player.setInvisible(true);
                        AetherPacketSender.sendToAllPlayers(new SetInvisibilityPacket(player.getId(), true));
                    }
                } else {
                    livingEntity.setInvisible(true);
                }
            } else {
                if (livingEntity instanceof Player player) {
                    var data = player.getAttachedOrCreate(AetherDataAttachments.AETHER_PLAYER);
                    if (!data.isWearingInvisibilityCloak()) {
                        player.setInvisible(false);
                        AetherPacketSender.sendToAllPlayers(new SetInvisibilityPacket(player.getId(), false));
                    }
                }
            }
        }
    }

    @Override
    public void onUnequip(ItemStack stack, SlotReference reference) {
        LivingEntity livingEntity = reference.entity();
        if (!livingEntity.level().isClientSide() && livingEntity instanceof Player player) {
            player.getAttachedOrCreate(AetherDataAttachments.AETHER_PLAYER).setSyncedToClients(player.getId(), AetherPlayerAttachment.WEARING_INVISIBILITY_CLOAK_SYNC_KEY, false);
        }
        livingEntity.setInvisible(false);
        ((LivingEntityAccessor) livingEntity).callUpdateEffectVisibility();
    }
}
