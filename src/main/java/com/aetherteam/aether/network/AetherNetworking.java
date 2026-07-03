package com.aetherteam.aether.network;

import com.aetherteam.aether.accessories.networking.server.NukeAccessories;
import com.aetherteam.aether.accessories.networking.server.ToggleAccessoryRenderPacket;
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
import com.aetherteam.aether.network.packet.serverbound.AerbunnyPuffPacket;
import com.aetherteam.aether.network.packet.serverbound.ClearItemPacket;
import com.aetherteam.aether.network.packet.serverbound.HammerProjectileLaunchPacket;
import com.aetherteam.aether.network.packet.serverbound.LoreExistsPacket;
import com.aetherteam.aether.network.packet.serverbound.NpcPlayerInteractPacket;
import com.aetherteam.aether.network.packet.serverbound.OpenAccessoriesPacket;
import com.aetherteam.aether.network.packet.serverbound.OpenInventoryPacket;
import com.aetherteam.aether.network.packet.serverbound.ServerDeveloperGlowPacket;
import com.aetherteam.aether.network.packet.serverbound.ServerHaloPacket;
import com.aetherteam.aether.network.packet.serverbound.ServerMoaSkinPacket;
import com.aetherteam.aether.network.packet.serverbound.StepHeightPacket;
import com.aetherteam.aether.network.packet.serverbound.SunAltarUpdatePacket;
import com.aetherteam.aether.network.packet.serverbound.TriggerUpdateInfoPacket;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

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
        PacketDistributor.init();
    }

    private static void registerPayloadTypes() {
        // CLIENTBOUND
        registerS2C(AetherTravelPacket.TYPE, AetherTravelPacket.STREAM_CODEC);
        registerS2C(AccessorySyncPacket.TYPE, AccessorySyncPacket.STREAM_CODEC);
        registerS2C(BossInfoPacket.Display.TYPE, BossInfoPacket.Display.STREAM_CODEC);
        registerS2C(BossInfoPacket.Remove.TYPE, BossInfoPacket.Remove.STREAM_CODEC);
        registerS2C(ClientDeveloperGlowPacket.Apply.TYPE, ClientDeveloperGlowPacket.Apply.STREAM_CODEC);
        registerS2C(ClientDeveloperGlowPacket.Remove.TYPE, ClientDeveloperGlowPacket.Remove.STREAM_CODEC);
        registerS2C(ClientDeveloperGlowPacket.Sync.TYPE, ClientDeveloperGlowPacket.Sync.STREAM_CODEC);
        registerS2C(ClientGrabItemPacket.TYPE, ClientGrabItemPacket.STREAM_CODEC);
        registerS2C(ClientHaloPacket.Apply.TYPE, ClientHaloPacket.Apply.STREAM_CODEC);
        registerS2C(ClientHaloPacket.Remove.TYPE, ClientHaloPacket.Remove.STREAM_CODEC);
        registerS2C(ClientHaloPacket.Sync.TYPE, ClientHaloPacket.Sync.STREAM_CODEC);
        registerS2C(ClientMoaSkinPacket.Apply.TYPE, ClientMoaSkinPacket.Apply.STREAM_CODEC);
        registerS2C(ClientMoaSkinPacket.Remove.TYPE, ClientMoaSkinPacket.Remove.STREAM_CODEC);
        registerS2C(ClientMoaSkinPacket.Sync.TYPE, ClientMoaSkinPacket.Sync.STREAM_CODEC);
        registerS2C(CloudMinionPacket.TYPE, CloudMinionPacket.STREAM_CODEC);
        registerS2C(HealthResetPacket.TYPE, HealthResetPacket.STREAM_CODEC);
        registerS2C(LeavingAetherPacket.TYPE, LeavingAetherPacket.STREAM_CODEC);
        registerS2C(MoaInteractPacket.TYPE, MoaInteractPacket.STREAM_CODEC);
        registerS2C(OpenSunAltarPacket.TYPE, OpenSunAltarPacket.STREAM_CODEC);
        registerS2C(PortalInteractPacket.TYPE, PortalInteractPacket.STREAM_CODEC);
        registerS2C(PortalTravelSoundPacket.TYPE, PortalTravelSoundPacket.STREAM_CODEC);
        registerS2C(QueenDialoguePacket.TYPE, QueenDialoguePacket.STREAM_CODEC);
        registerS2C(RegisterMoaSkinsPacket.TYPE, RegisterMoaSkinsPacket.STREAM_CODEC);
        registerS2C(RemountAerbunnyPacket.TYPE, RemountAerbunnyPacket.STREAM_CODEC);
        registerS2C(SetInvisibilityPacket.TYPE, SetInvisibilityPacket.STREAM_CODEC);
        registerS2C(ToolDebuffPacket.TYPE, ToolDebuffPacket.STREAM_CODEC);
        registerS2C(ZephyrSnowballHitPacket.TYPE, ZephyrSnowballHitPacket.STREAM_CODEC);

        // SERVERBOUND
        registerC2S(AerbunnyPuffPacket.TYPE, AerbunnyPuffPacket.STREAM_CODEC);
        registerC2S(ClearItemPacket.TYPE, ClearItemPacket.STREAM_CODEC);
        registerC2S(HammerProjectileLaunchPacket.TYPE, HammerProjectileLaunchPacket.STREAM_CODEC);
        registerC2S(LoreExistsPacket.TYPE, LoreExistsPacket.STREAM_CODEC);
        registerC2S(NukeAccessories.TYPE, NukeAccessories.STREAM_CODEC);
        registerC2S(NpcPlayerInteractPacket.TYPE, NpcPlayerInteractPacket.STREAM_CODEC);
        registerC2S(OpenAccessoriesPacket.TYPE, OpenAccessoriesPacket.STREAM_CODEC);
        registerC2S(OpenInventoryPacket.TYPE, OpenInventoryPacket.STREAM_CODEC);
        registerC2S(ServerDeveloperGlowPacket.Apply.TYPE, ServerDeveloperGlowPacket.Apply.STREAM_CODEC);
        registerC2S(ServerDeveloperGlowPacket.Remove.TYPE, ServerDeveloperGlowPacket.Remove.STREAM_CODEC);
        registerC2S(ServerHaloPacket.Apply.TYPE, ServerHaloPacket.Apply.STREAM_CODEC);
        registerC2S(ServerHaloPacket.Remove.TYPE, ServerHaloPacket.Remove.STREAM_CODEC);
        registerC2S(ServerMoaSkinPacket.Apply.TYPE, ServerMoaSkinPacket.Apply.STREAM_CODEC);
        registerC2S(ServerMoaSkinPacket.Remove.TYPE, ServerMoaSkinPacket.Remove.STREAM_CODEC);
        registerC2S(StepHeightPacket.TYPE, StepHeightPacket.STREAM_CODEC);
        registerC2S(SunAltarUpdatePacket.TYPE, SunAltarUpdatePacket.STREAM_CODEC);
        registerC2S(ToggleAccessoryRenderPacket.TYPE, ToggleAccessoryRenderPacket.STREAM_CODEC);
        registerC2S(TriggerUpdateInfoPacket.TYPE, TriggerUpdateInfoPacket.STREAM_CODEC);

        // BIDIRECTIONAL
        registerS2C(AetherPlayerSyncPacket.TYPE, AetherPlayerSyncPacket.STREAM_CODEC);
        registerC2S(AetherPlayerSyncPacket.TYPE, AetherPlayerSyncPacket.STREAM_CODEC);

        registerS2C(AetherTimeSyncPacket.TYPE, AetherTimeSyncPacket.STREAM_CODEC);
        registerC2S(AetherTimeSyncPacket.TYPE, AetherTimeSyncPacket.STREAM_CODEC);

        registerS2C(PhoenixArrowSyncPacket.TYPE, PhoenixArrowSyncPacket.STREAM_CODEC);
        registerC2S(PhoenixArrowSyncPacket.TYPE, PhoenixArrowSyncPacket.STREAM_CODEC);
    }

    private static void registerServerReceivers() {
        registerServerReceiver(AerbunnyPuffPacket.TYPE, AerbunnyPuffPacket::execute);
        registerServerReceiver(ClearItemPacket.TYPE, ClearItemPacket::execute);
        registerServerReceiver(HammerProjectileLaunchPacket.TYPE, HammerProjectileLaunchPacket::execute);
        registerServerReceiver(LoreExistsPacket.TYPE, LoreExistsPacket::execute);
        registerServerReceiver(NukeAccessories.TYPE, NukeAccessories::execute);
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
        registerServerReceiver(TriggerUpdateInfoPacket.TYPE, TriggerUpdateInfoPacket::execute);

        registerServerReceiver(AetherPlayerSyncPacket.TYPE, AetherPlayerSyncPacket::execute);
        registerServerReceiver(AetherTimeSyncPacket.TYPE, AetherTimeSyncPacket::execute);
        registerServerReceiver(PhoenixArrowSyncPacket.TYPE, PhoenixArrowSyncPacket::execute);
    }

    private static <T extends CustomPacketPayload> void registerS2C(CustomPacketPayload.Type<T> type, StreamCodec<RegistryFriendlyByteBuf, T> codec) {
        PayloadTypeRegistry.clientboundPlay().register(type, codec);
    }

    private static <T extends CustomPacketPayload> void registerC2S(CustomPacketPayload.Type<T> type, StreamCodec<RegistryFriendlyByteBuf, T> codec) {
        PayloadTypeRegistry.serverboundPlay().register(type, codec);
    }

    private static <T extends CustomPacketPayload> void registerServerReceiver(CustomPacketPayload.Type<T> type, BiConsumer<T, AetherPayloadContext> handler) {
        ServerPlayNetworking.registerGlobalReceiver(type, (payload, context) ->
                context.player().level().getServer().execute(() -> handler.accept(payload, AetherPayloadContext.of(context.player()))));
    }
}
