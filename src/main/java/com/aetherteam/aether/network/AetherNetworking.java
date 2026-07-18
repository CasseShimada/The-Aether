package com.aetherteam.aether.network;

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
import com.aetherteam.aether.network.packet.clientbound.ServerConfigSyncPacket;
import com.aetherteam.aether.network.packet.clientbound.ToolDebuffPacket;
import com.aetherteam.aether.network.packet.clientbound.ZephyrSnowballHitPacket;
import com.aetherteam.aether.network.packet.serverbound.AerbunnyPuffPacket;
import com.aetherteam.aether.network.packet.serverbound.ClearItemPacket;
import com.aetherteam.aether.network.packet.serverbound.LoreExistsPacket;
import com.aetherteam.aether.network.packet.serverbound.NpcPlayerInteractPacket;
import com.aetherteam.aether.network.packet.serverbound.NukeAccessoriesPacket;
import com.aetherteam.aether.network.packet.serverbound.OpenAccessoriesPacket;
import com.aetherteam.aether.network.packet.serverbound.OpenInventoryPacket;
import com.aetherteam.aether.network.packet.serverbound.ServerDeveloperGlowPacket;
import com.aetherteam.aether.network.packet.serverbound.ServerHaloPacket;
import com.aetherteam.aether.network.packet.serverbound.ServerMoaSkinPacket;
import com.aetherteam.aether.network.packet.serverbound.StepHeightPacket;
import com.aetherteam.aether.network.packet.serverbound.SunAltarUpdatePacket;
import com.aetherteam.aether.network.packet.serverbound.ToggleAccessoryRenderPacket;
import com.aetherteam.aether.network.packet.serverbound.UseAccessoryPacket;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.BiConsumer;

public final class AetherNetworking {
    private static boolean commonRegistered;

    private AetherNetworking() {
    }

    public static void registerCommon() {
        if (commonRegistered) {
            return;
        }
        commonRegistered = true;

        registerPayloadTypes();
        registerServerReceivers();
        AetherPacketSender.init();
    }

    private static void registerPayloadTypes() {
        // CLIENTBOUND
        registerClientbound(AetherTravelPacket.TYPE, AetherTravelPacket.STREAM_CODEC);
        registerClientbound(AccessorySyncPacket.TYPE, AccessorySyncPacket.STREAM_CODEC);
        registerClientbound(BossInfoPacket.Display.TYPE, BossInfoPacket.Display.STREAM_CODEC);
        registerClientbound(BossInfoPacket.Remove.TYPE, BossInfoPacket.Remove.STREAM_CODEC);
        registerClientbound(ClientDeveloperGlowPacket.Apply.TYPE, ClientDeveloperGlowPacket.Apply.STREAM_CODEC);
        registerClientbound(ClientDeveloperGlowPacket.Remove.TYPE, ClientDeveloperGlowPacket.Remove.STREAM_CODEC);
        registerClientbound(ClientDeveloperGlowPacket.Sync.TYPE, ClientDeveloperGlowPacket.Sync.STREAM_CODEC);
        registerClientbound(ClientGrabItemPacket.TYPE, ClientGrabItemPacket.STREAM_CODEC);
        registerClientbound(ClientHaloPacket.Apply.TYPE, ClientHaloPacket.Apply.STREAM_CODEC);
        registerClientbound(ClientHaloPacket.Remove.TYPE, ClientHaloPacket.Remove.STREAM_CODEC);
        registerClientbound(ClientHaloPacket.Sync.TYPE, ClientHaloPacket.Sync.STREAM_CODEC);
        registerClientbound(ClientMoaSkinPacket.Apply.TYPE, ClientMoaSkinPacket.Apply.STREAM_CODEC);
        registerClientbound(ClientMoaSkinPacket.Remove.TYPE, ClientMoaSkinPacket.Remove.STREAM_CODEC);
        registerClientbound(ClientMoaSkinPacket.Sync.TYPE, ClientMoaSkinPacket.Sync.STREAM_CODEC);
        registerClientbound(CloudMinionPacket.TYPE, CloudMinionPacket.STREAM_CODEC);
        registerClientbound(HealthResetPacket.TYPE, HealthResetPacket.STREAM_CODEC);
        registerClientbound(LeavingAetherPacket.TYPE, LeavingAetherPacket.STREAM_CODEC);
        registerClientbound(MoaInteractPacket.TYPE, MoaInteractPacket.STREAM_CODEC);
        registerClientbound(OpenSunAltarPacket.TYPE, OpenSunAltarPacket.STREAM_CODEC);
        registerClientbound(PortalInteractPacket.TYPE, PortalInteractPacket.STREAM_CODEC);
        registerClientbound(PortalTravelSoundPacket.TYPE, PortalTravelSoundPacket.STREAM_CODEC);
        registerClientbound(QueenDialoguePacket.TYPE, QueenDialoguePacket.STREAM_CODEC);
        registerClientbound(RegisterMoaSkinsPacket.TYPE, RegisterMoaSkinsPacket.STREAM_CODEC);
        registerClientbound(RemountAerbunnyPacket.TYPE, RemountAerbunnyPacket.STREAM_CODEC);
        registerClientbound(SetInvisibilityPacket.TYPE, SetInvisibilityPacket.STREAM_CODEC);
        registerClientbound(ServerConfigSyncPacket.TYPE, ServerConfigSyncPacket.STREAM_CODEC);
        registerClientbound(ToolDebuffPacket.TYPE, ToolDebuffPacket.STREAM_CODEC);
        registerClientbound(ZephyrSnowballHitPacket.TYPE, ZephyrSnowballHitPacket.STREAM_CODEC);

        // SERVERBOUND
        registerServerbound(AerbunnyPuffPacket.TYPE, AerbunnyPuffPacket.STREAM_CODEC);
        registerServerbound(ClearItemPacket.TYPE, ClearItemPacket.STREAM_CODEC);
        registerServerbound(LoreExistsPacket.TYPE, LoreExistsPacket.STREAM_CODEC);
        registerServerbound(NukeAccessoriesPacket.TYPE, NukeAccessoriesPacket.STREAM_CODEC);
        registerServerbound(NpcPlayerInteractPacket.TYPE, NpcPlayerInteractPacket.STREAM_CODEC);
        registerServerbound(OpenAccessoriesPacket.TYPE, OpenAccessoriesPacket.STREAM_CODEC);
        registerServerbound(OpenInventoryPacket.TYPE, OpenInventoryPacket.STREAM_CODEC);
        registerServerbound(ServerDeveloperGlowPacket.Apply.TYPE, ServerDeveloperGlowPacket.Apply.STREAM_CODEC);
        registerServerbound(ServerDeveloperGlowPacket.Remove.TYPE, ServerDeveloperGlowPacket.Remove.STREAM_CODEC);
        registerServerbound(ServerHaloPacket.Apply.TYPE, ServerHaloPacket.Apply.STREAM_CODEC);
        registerServerbound(ServerHaloPacket.Remove.TYPE, ServerHaloPacket.Remove.STREAM_CODEC);
        registerServerbound(ServerMoaSkinPacket.Apply.TYPE, ServerMoaSkinPacket.Apply.STREAM_CODEC);
        registerServerbound(ServerMoaSkinPacket.Remove.TYPE, ServerMoaSkinPacket.Remove.STREAM_CODEC);
        registerServerbound(StepHeightPacket.TYPE, StepHeightPacket.STREAM_CODEC);
        registerServerbound(SunAltarUpdatePacket.TYPE, SunAltarUpdatePacket.STREAM_CODEC);
        registerServerbound(ToggleAccessoryRenderPacket.TYPE, ToggleAccessoryRenderPacket.STREAM_CODEC);
        registerServerbound(UseAccessoryPacket.TYPE, UseAccessoryPacket.STREAM_CODEC);

        // ATTACHMENT SYNC
        registerClientbound(AetherPlayerSyncPacket.TYPE, AetherPlayerSyncPacket.STREAM_CODEC);
        registerServerbound(AetherPlayerSyncPacket.TYPE, AetherPlayerSyncPacket.STREAM_CODEC);

        registerClientbound(AetherTimeSyncPacket.TYPE, AetherTimeSyncPacket.STREAM_CODEC);
        registerClientbound(PhoenixArrowSyncPacket.TYPE, PhoenixArrowSyncPacket.STREAM_CODEC);
    }

    private static void registerServerReceivers() {
        registerServerReceiver(AerbunnyPuffPacket.TYPE, AerbunnyPuffPacket::execute);
        registerServerReceiver(ClearItemPacket.TYPE, ClearItemPacket::execute);
        registerServerReceiver(LoreExistsPacket.TYPE, LoreExistsPacket::execute);
        registerServerReceiver(NukeAccessoriesPacket.TYPE, NukeAccessoriesPacket::execute);
        registerServerReceiver(NpcPlayerInteractPacket.TYPE, NpcPlayerInteractPacket::execute);
        registerServerReceiver(OpenAccessoriesPacket.TYPE, OpenAccessoriesPacket::execute);
        registerServerReceiver(OpenInventoryPacket.TYPE, OpenInventoryPacket::execute);
        registerServerReceiver(ServerDeveloperGlowPacket.Apply.TYPE, ServerDeveloperGlowPacket.Apply::execute);
        registerServerReceiver(ServerDeveloperGlowPacket.Remove.TYPE, ServerDeveloperGlowPacket.Remove::execute);
        registerServerReceiver(ServerHaloPacket.Apply.TYPE, ServerHaloPacket.Apply::execute);
        registerServerReceiver(ServerHaloPacket.Remove.TYPE, ServerHaloPacket.Remove::execute);
        registerServerReceiver(ServerMoaSkinPacket.Apply.TYPE, ServerMoaSkinPacket.Apply::execute);
        registerServerReceiver(ServerMoaSkinPacket.Remove.TYPE, ServerMoaSkinPacket.Remove::execute);
        registerServerReceiver(StepHeightPacket.TYPE, StepHeightPacket::execute);
        registerServerReceiver(SunAltarUpdatePacket.TYPE, SunAltarUpdatePacket::execute);
        registerServerReceiver(ToggleAccessoryRenderPacket.TYPE, ToggleAccessoryRenderPacket::execute);
        registerServerReceiver(UseAccessoryPacket.TYPE, UseAccessoryPacket::execute);

        registerServerReceiver(AetherPlayerSyncPacket.TYPE, AetherPlayerSyncPacket::executeServerbound);
    }

    private static <T extends CustomPacketPayload> void registerClientbound(CustomPacketPayload.Type<T> type, StreamCodec<RegistryFriendlyByteBuf, T> codec) {
        PayloadTypeRegistry.clientboundPlay().register(type, codec);
    }

    private static <T extends CustomPacketPayload> void registerServerbound(CustomPacketPayload.Type<T> type, StreamCodec<RegistryFriendlyByteBuf, T> codec) {
        PayloadTypeRegistry.serverboundPlay().register(type, codec);
    }

    private static <T extends CustomPacketPayload> void registerServerReceiver(CustomPacketPayload.Type<T> type, BiConsumer<T, ServerPlayer> handler) {
        ServerPlayNetworking.registerGlobalReceiver(type, (payload, context) ->
                context.player().level().getServer().execute(() -> handler.accept(payload, context.player())));
    }
}
