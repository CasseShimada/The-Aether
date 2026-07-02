package com.aetherteam.aether.event.hooks;

import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.attachment.AetherPlayerAttachment;
import com.aetherteam.aether.data.resources.registries.AetherDimensions;
import com.aetherteam.nitrogen.attachment.INBTSynchable;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class AttachmentHooks {
    public static class AetherPlayerHooks {
        /**
         * @see AetherPlayerAttachment#onLogin(Player)
         * Handles the Fabric login event for Aether's player attachment.
         */
        public static void login(Player player) {
            player.getAttachedOrCreate(AetherDataAttachments.AETHER_PLAYER).onLogin(player);
        }

        /**
         * @see AetherPlayerAttachment#onLogout(Player)
         * Handles the Fabric logout event for Aether's player attachment.
         */
        public static void logout(Player player) {
            player.getAttachedOrCreate(AetherDataAttachments.AETHER_PLAYER).onLogout(player);
        }

        /**
         * @see AetherPlayerAttachment#onJoinLevel(Player)
         * Handles the Fabric entity-load event for Aether's player attachment.
         */
        public static void joinLevel(Entity entity) {
            if (entity instanceof Player player) {
                player.getAttachedOrCreate(AetherDataAttachments.AETHER_PLAYER).onJoinLevel(player);
            }
        }

        /**
         * @see AetherPlayerAttachment#onUpdate(Player)
         * Handles the Fabric player tick callback for Aether's player attachment.
         */
        public static void update(LivingEntity entity) {
            if (entity instanceof Player player) {
                player.getAttachedOrCreate(AetherDataAttachments.AETHER_PLAYER).onUpdate(player);
            }
        }

        /**
         * @see AetherPlayerAttachment#handleRespawn(boolean)
         * Handles the Fabric respawn/copy event for Aether's player attachment.
         */
        public static void clone(Player player, boolean wasDeath) {
            player.getAttachedOrCreate(AetherDataAttachments.AETHER_PLAYER).handleRespawn(wasDeath);
        }

        /**
         * Syncs attachment data to the client when the player changes dimensions.
         *
         * @param player The {@link Player}.
         * Handles the Fabric dimension-change event for Aether's player attachment.
         */
        public static void changeDimension(Player player) {
            if (!player.level().isClientSide()) {
                player.getAttachedOrCreate(AetherDataAttachments.AETHER_PLAYER).forceSync(player.getId(), INBTSynchable.Direction.CLIENT);
            }
        }
    }

    public static class AetherTimeHooks {
        /**
         * Sync the Aether's time to the player on login.
         *
         * @param player The {@link Player}.
         */
        public static void login(Player player) {
            syncAetherTime(player);
        }

        /**
         * Sync the Aether's time to the player on dimension change.
         *
         * @param player The {@link Player}.
         */
        public static void changeDimension(Player player) {
            syncAetherTime(player);
        }

        /**
         * Sync the Aether's time to the player on respawn.
         *
         * @param player The {@link Player}.
         */
        public static void respawn(Player player) {
            syncAetherTime(player);
        }

        /**
         * Sync the Aether's time to the player.
         *
         * @param player The {@link Player}.
         */
        private static void syncAetherTime(Player player) {
            if (player instanceof ServerPlayer serverPlayer) {
                if (player.level().dimension().equals(AetherDimensions.AETHER_LEVEL)) {
                    player.level().getAttachedOrCreate(AetherDataAttachments.AETHER_TIME).updateEternalDay(serverPlayer);
                }
            }
        }
    }
}
