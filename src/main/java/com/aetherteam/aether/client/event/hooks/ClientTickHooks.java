package com.aetherteam.aether.client.event.hooks;

import com.aetherteam.aether.AetherConfig;
import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.client.AetherKeys;
import com.aetherteam.aether.client.ClientCompat;
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
        GuiPerkScreenHooks.handlePatreonRefreshRebound();
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
            aetherPlayer.setSynched(player.getId(), AttachmentSyncable.Direction.SERVER, "setJumping", isJumping);
        }

        boolean isMoving = isJumping || keys.forward() || keys.backward() || keys.left() || keys.right() || player.isFallFlying();
        if (isMoving != aetherPlayer.isMoving()) {
            aetherPlayer.setSynched(player.getId(), AttachmentSyncable.Direction.SERVER, "setMoving", isMoving);
        }

        boolean isHitting = client.options.keyAttack.isDown();
        if (isHitting != aetherPlayer.isHitting()) {
            aetherPlayer.setSynched(player.getId(), AttachmentSyncable.Direction.SERVER, "setHitting", isHitting);
        }

        boolean gravititeJumpActive = AetherKeys.GRAVITITE_JUMP_ABILITY.isDown();
        if (gravititeJumpActive != aetherPlayer.isGravititeJumpActive()) {
            aetherPlayer.setSynched(player.getId(), AttachmentSyncable.Direction.SERVER, "setGravititeJumpActive", gravititeJumpActive);
        }
    }

    private static void handleAccessoryHotkey(Minecraft client) {
        if (!(ClientCompat.screen(client) instanceof AbstractContainerScreen<?> containerScreen)) {
            return;
        }
        if (AetherConfig.CLIENT.disable_accessory_button.get() || !AetherKeys.OPEN_ACCESSORY_INVENTORY.consumeClick()) {
            return;
        }
        containerScreen.onClose();
    }
}
