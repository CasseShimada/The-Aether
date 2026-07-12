package com.aetherteam.aether.event.hooks;

import com.aetherteam.aether.accessories.impl.AccessoryRuntime;
import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.attachment.AttachmentSyncable;
import com.aetherteam.aether.network.AetherPacketSender;
import com.aetherteam.aether.network.packet.clientbound.RegisterMoaSkinsPacket;
import com.aetherteam.aether.perk.data.ServerPerkData;
import com.aetherteam.aether.perk.data.UserData;
import com.aetherteam.aether.perk.types.MoaSkins;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public final class PlayerLifecycleHooks {
    private PlayerLifecycleHooks() {
    }

    public static void login(ServerPlayer player) {
        player.getAttachedOrCreate(AetherDataAttachments.AETHER_PLAYER).onLogin(player);
        DimensionTimeHooks.syncAetherTime(player);
        var playerId = player.getGameProfile().id();
        if (!UserData.Server.getStoredUsers().containsKey(playerId)) {
            var server = player.level().getServer();
            ServerPerkData.MOA_SKIN_INSTANCE.removePerk(server, playerId);
            ServerPerkData.HALO_INSTANCE.removePerk(server, playerId);
            ServerPerkData.DEVELOPER_GLOW_INSTANCE.removePerk(server, playerId);
        }
        ToolAbilityHooks.setDebuffToolsState(player);
        MoaSkins.registerMoaSkins(player.level());
        AetherPacketSender.sendToPlayer(player, new RegisterMoaSkinsPacket());
        DimensionSpawnHooks.startInAether(player);
        AccessoryRuntime.forceSync(player);
    }

    public static void logout(ServerPlayer player) {
        player.getAttachedOrCreate(AetherDataAttachments.AETHER_PLAYER).onLogout(player);
        AccessoryRuntime.clear(player);
    }

    public static void afterRespawn(ServerPlayer oldPlayer, ServerPlayer newPlayer, boolean alive) {
        DimensionTimeHooks.syncAetherTime(newPlayer);
        AccessoryRuntime.forceSync(newPlayer);
    }

    public static void changeLevel(ServerPlayer player, ServerLevel origin, ServerLevel destination) {
        player.getAttachedOrCreate(AetherDataAttachments.AETHER_PLAYER).remountAerbunny(player);
        if (!player.level().isClientSide()) {
            player.getAttachedOrCreate(AetherDataAttachments.AETHER_PLAYER).forceSync(player.getId(), AttachmentSyncable.Direction.CLIENT);
        }
        DimensionTimeHooks.syncAetherTime(player);
        AccessoryRuntime.forceSync(player);
    }
}
