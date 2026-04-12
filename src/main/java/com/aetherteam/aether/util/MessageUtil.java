package com.aetherteam.aether.util;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public final class MessageUtil {
    private MessageUtil() {
    }

    public static void sendPlayerMessage(Player player, Component message, boolean overlay) {
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.sendSystemMessage(message, overlay);
        } else {
            player.sendSystemMessage(message);
        }
    }
}
