package com.aetherteam.aether.client.event.hooks;

import com.aetherteam.aether.AetherConfig;
import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.attachment.AetherPlayerAttachment;
import com.aetherteam.aether.client.AetherKeys;
import com.aetherteam.aether.client.ClientAccess;
import com.aetherteam.aether.event.hooks.EntityMountHooks;
import com.aetherteam.aether.attachment.AttachmentSyncable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.entity.player.Input;

public final class ClientTickHooks {
    private ClientTickHooks() {
    }

    public static void endClientTick(Minecraft client) {
        ClientMusicHooks.tick();
        ClientDimensionTimeHooks.tickTime();
        tickPlayerState(client);
        handleAccessoryHotkey(client);
        GuiAccessoryMenuHooks.openAccessoryMenu();
    }

    private static void tickPlayerState(Minecraft client) {
        if (client.player == null) {
            return;
        }

        syncPlayerInput(client);
        EntityMountHooks.launchMount(client.player);
    }

    private static void syncPlayerInput(Minecraft client) {
        var player = client.player;
        var aetherPlayer = player.getAttachedOrCreate(AetherDataAttachments.AETHER_PLAYER);
        Input keys = player.input.keyPresses;

        boolean isJumping = keys.jump();
        if (isJumping != aetherPlayer.isJumping()) {
            aetherPlayer.setSynced(player.getId(), AttachmentSyncable.Direction.SERVER, AetherPlayerAttachment.JUMPING_SYNC_KEY, isJumping);
        }

        boolean isMoving = isJumping || keys.forward() || keys.backward() || keys.left() || keys.right() || player.isFallFlying();
        if (isMoving != aetherPlayer.isMoving()) {
            aetherPlayer.setSynced(player.getId(), AttachmentSyncable.Direction.SERVER, AetherPlayerAttachment.MOVING_SYNC_KEY, isMoving);
        }

        boolean isHitting = client.options.keyAttack.isDown();
        if (isHitting != aetherPlayer.isHitting()) {
            aetherPlayer.setSynced(player.getId(), AttachmentSyncable.Direction.SERVER, AetherPlayerAttachment.HITTING_SYNC_KEY, isHitting);
        }

        boolean gravititeJumpActive = AetherKeys.GRAVITITE_JUMP_ABILITY.isDown();
        if (gravititeJumpActive != aetherPlayer.isGravititeJumpActive()) {
            aetherPlayer.setSynced(player.getId(), AttachmentSyncable.Direction.SERVER, AetherPlayerAttachment.GRAVITITE_JUMP_ACTIVE_SYNC_KEY, gravititeJumpActive);
        }
    }

    private static void handleAccessoryHotkey(Minecraft client) {
        if (!(ClientAccess.screen(client) instanceof AbstractContainerScreen<?> containerScreen)) {
            return;
        }
        if (AetherConfig.CLIENT.disable_accessory_button.get() || !AetherKeys.OPEN_ACCESSORY_INVENTORY.consumeClick()) {
            return;
        }
        containerScreen.onClose();
    }
}
