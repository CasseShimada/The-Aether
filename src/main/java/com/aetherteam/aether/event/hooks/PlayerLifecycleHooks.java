package com.aetherteam.aether.event.hooks;

import com.aetherteam.aether.accessories.impl.AccessoryRuntime;
import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.network.AetherPacketSender;
import com.aetherteam.aether.network.packet.clientbound.RegisterMoaSkinsPacket;
import com.aetherteam.aether.perk.types.MoaSkins;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;

public final class PlayerLifecycleHooks {
    private PlayerLifecycleHooks() {
    }

    public static void login(ServerPlayer player) {
        player.getAttachedOrCreate(AetherDataAttachments.AETHER_PLAYER).onLogin(player);
        DimensionTimeHooks.syncAetherTime(player);
        ServerPerkHooks.refreshPerks(player);
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

    public static void copyFrom(ServerPlayer oldPlayer, ServerPlayer newPlayer, boolean alive) {
        newPlayer.getAttachedOrCreate(AetherDataAttachments.AETHER_PLAYER).handleRespawn(!alive);
    }

    public static void afterRespawn(ServerPlayer oldPlayer, ServerPlayer newPlayer, boolean alive) {
        DimensionTimeHooks.syncAetherTime(newPlayer);
        AccessoryRuntime.forceSync(newPlayer);
    }

    public static void changeLevel(ServerPlayer player, ServerLevel origin, ServerLevel destination) {
        DimensionTravelHooks.remountPlayerAerbunny(player);
        PlayerAttachmentSyncHooks.syncPlayerAttachment(player);
        DimensionTimeHooks.syncAetherTime(player);
        AccessoryRuntime.forceSync(player);
    }

    @Nullable
    public static Player.BedSleepingProblem allowSleeping(Player player, BlockPos sleepingPos) {
        return DimensionTimeHooks.isEternalDay(player) ? Player.BedSleepingProblem.OTHER_PROBLEM : null;
    }

    public static void joinLevel(Player player) {
        player.getAttachedOrCreate(AetherDataAttachments.AETHER_PLAYER).onJoinLevel(player);
    }
}
