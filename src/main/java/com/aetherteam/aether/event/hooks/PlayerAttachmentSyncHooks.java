package com.aetherteam.aether.event.hooks;

import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.attachment.INBTSynchable;
import net.minecraft.world.entity.player.Player;

public final class PlayerAttachmentSyncHooks {
    private PlayerAttachmentSyncHooks() {
    }

    public static void syncPlayerAttachment(Player player) {
        if (!player.level().isClientSide()) {
            player.getAttachedOrCreate(AetherDataAttachments.AETHER_PLAYER).forceSync(player.getId(), INBTSynchable.Direction.CLIENT);
        }
    }
}
