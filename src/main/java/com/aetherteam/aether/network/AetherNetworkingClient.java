package com.aetherteam.aether.network;

import com.aetherteam.aether.client.AetherClient;
import com.aetherteam.aether.client.gui.AetherBossBarTracker;
import com.aetherteam.aether.network.packet.AetherPlayerSyncPacket;
import com.aetherteam.aether.network.packet.AetherTimeSyncPacket;
import com.aetherteam.aether.network.packet.PhoenixArrowSyncPacket;
import com.aetherteam.aether.network.packet.clientbound.AetherTravelPacket;
import com.aetherteam.aether.network.packet.clientbound.AccessorySyncPacket;
import com.aetherteam.aether.network.packet.clientbound.BossInfoPacket;
import com.aetherteam.aether.network.packet.clientbound.ClientDeveloperGlowPacket;
import com.aetherteam.aether.network.packet.clientbound.ClientGrabItemPacket;
import com.aetherteam.aether.network.packet.clientbound.ClientHaloPacket;
import com.aetherteam.aether.network.packet.clientbound.ClientMoaSkinPacket;
import com.aetherteam.aether.network.packet.clientbound.CloudMinionPacket;
import com.aetherteam.aether.network.packet.clientbound.HealthResetPacket;
import com.aetherteam.aether.network.packet.clientbound.LeavingAetherPacket;
import com.aetherteam.aether.network.packet.clientbound.MoaInteractPacket;
import com.aetherteam.aether.network.packet.clientbound.OpenSunAltarPacket;
import com.aetherteam.aether.network.packet.clientbound.PortalInteractPacket;
import com.aetherteam.aether.network.packet.clientbound.PortalTravelSoundPacket;
import com.aetherteam.aether.network.packet.clientbound.QueenDialoguePacket;
import com.aetherteam.aether.network.packet.clientbound.RegisterMoaSkinsPacket;
import com.aetherteam.aether.network.packet.clientbound.RemountAerbunnyPacket;
import com.aetherteam.aether.network.packet.clientbound.SetInvisibilityPacket;
import com.aetherteam.aether.network.packet.clientbound.ToolDebuffPacket;
import com.aetherteam.aether.network.packet.clientbound.ZephyrSnowballHitPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public final class AetherNetworkingClient {
    private static boolean registered;

    private AetherNetworkingClient() {
    }

    public static void registerClient() {
        if (registered) {
            return;
        }
        registered = true;

        registerClientReceiver(AetherTravelPacket.TYPE, AetherTravelPacket::execute);
        registerClientReceiver(AccessorySyncPacket.TYPE, AccessorySyncPacket::execute);
        registerClientReceiver(BossInfoPacket.Display.TYPE, payload -> AetherBossBarTracker.track(payload.getBossEvent(), payload.getEntityID()));
        registerClientReceiver(BossInfoPacket.Remove.TYPE, payload -> AetherBossBarTracker.untrack(payload.getBossEvent()));
        registerClientReceiver(ClientDeveloperGlowPacket.Apply.TYPE, ClientDeveloperGlowPacket.Apply::execute);
        registerClientReceiver(ClientDeveloperGlowPacket.Remove.TYPE, ClientDeveloperGlowPacket.Remove::execute);
        registerClientReceiver(ClientDeveloperGlowPacket.Sync.TYPE, ClientDeveloperGlowPacket.Sync::execute);
        registerClientReceiver(ClientGrabItemPacket.TYPE, ClientGrabItemPacket::execute);
        registerClientReceiver(ClientHaloPacket.Apply.TYPE, ClientHaloPacket.Apply::execute);
        registerClientReceiver(ClientHaloPacket.Remove.TYPE, ClientHaloPacket.Remove::execute);
        registerClientReceiver(ClientHaloPacket.Sync.TYPE, ClientHaloPacket.Sync::execute);
        registerClientReceiver(ClientMoaSkinPacket.Apply.TYPE, ClientMoaSkinPacket.Apply::execute);
        registerClientReceiver(ClientMoaSkinPacket.Remove.TYPE, ClientMoaSkinPacket.Remove::execute);
        registerClientReceiver(ClientMoaSkinPacket.Sync.TYPE, ClientMoaSkinPacket.Sync::execute);
        registerClientReceiver(CloudMinionPacket.TYPE, CloudMinionPacket::execute);
        registerClientReceiver(HealthResetPacket.TYPE, HealthResetPacket::execute);
        registerClientReceiver(LeavingAetherPacket.TYPE, LeavingAetherPacket::execute);
        registerClientReceiver(MoaInteractPacket.TYPE, MoaInteractPacket::execute);
        registerClientReceiver(OpenSunAltarPacket.TYPE, payload -> AetherClient.setToSunAltarScreen(payload.name(), payload.timeScale()));
        registerClientReceiver(PortalInteractPacket.TYPE, PortalInteractPacket::execute);
        registerClientReceiver(PortalTravelSoundPacket.TYPE, PortalTravelSoundPacket::execute);
        registerClientReceiver(QueenDialoguePacket.TYPE, QueenDialoguePacket::execute);
        registerClientReceiver(RegisterMoaSkinsPacket.TYPE, RegisterMoaSkinsPacket::execute);
        registerClientReceiver(RemountAerbunnyPacket.TYPE, RemountAerbunnyPacket::execute);
        registerClientReceiver(SetInvisibilityPacket.TYPE, SetInvisibilityPacket::execute);
        registerClientReceiver(ToolDebuffPacket.TYPE, ToolDebuffPacket::execute);
        registerClientReceiver(ZephyrSnowballHitPacket.TYPE, ZephyrSnowballHitPacket::execute);

        registerClientReceiver(AetherPlayerSyncPacket.TYPE, AetherPlayerSyncPacket::execute);
        registerClientReceiver(AetherTimeSyncPacket.TYPE, AetherTimeSyncPacket::execute);
        registerClientReceiver(PhoenixArrowSyncPacket.TYPE, PhoenixArrowSyncPacket::execute);
    }

    private static <T extends CustomPacketPayload> void registerClientReceiver(CustomPacketPayload.Type<T> type, Consumer<T> handler) {
        ClientPlayNetworking.registerGlobalReceiver(type, (payload, context) ->
                context.client().execute(() -> handler.accept(payload)));
    }

    private static <T extends CustomPacketPayload> void registerClientReceiver(CustomPacketPayload.Type<T> type, BiConsumer<T, Player> handler) {
        ClientPlayNetworking.registerGlobalReceiver(type, (payload, context) ->
                context.client().execute(() -> handler.accept(payload, context.player())));
    }
}
