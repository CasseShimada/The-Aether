package com.aetherteam.aether.network.packet.serverbound;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

/**
 * Packet used by the Moa skin refresh button path.
 */
public record TriggerUpdateInfoPacket(int playerID) implements CustomPacketPayload {
    public static final Type<TriggerUpdateInfoPacket> TYPE = new Type<>(Identifier.fromNamespaceAndPath("nitrogen_internals", "trigger_patreon_info_update"));

    public static final StreamCodec<RegistryFriendlyByteBuf, TriggerUpdateInfoPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            TriggerUpdateInfoPacket::playerID,
            TriggerUpdateInfoPacket::new
    );

    @Override
    public Type<TriggerUpdateInfoPacket> type() {
        return TYPE;
    }

    public static void execute(TriggerUpdateInfoPacket payload, ServerPlayer player) {
        // Intentionally no-op because no external Patreon sync service is bundled.
    }
}
