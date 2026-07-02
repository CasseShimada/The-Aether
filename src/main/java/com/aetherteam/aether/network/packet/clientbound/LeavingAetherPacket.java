package com.aetherteam.aether.network.packet.clientbound;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.event.hooks.DimensionTravelState;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import com.aetherteam.aether.network.AetherPayloadContext;

/**
 * Marks the player as being in the process of leaving the Aether. This is used for displaying "Descending from the Aether" in the world loading screen.
 *
 * @see com.aetherteam.aether.client.event.hooks.GuiTriviaHooks#drawAetherTravelMessage
 */
public record LeavingAetherPacket(boolean playerLeavingAether) implements CustomPacketPayload {
    public static final Type<LeavingAetherPacket> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Aether.MODID, "leave_aether"));

    public static final StreamCodec<RegistryFriendlyByteBuf, LeavingAetherPacket> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.BOOL,
        LeavingAetherPacket::playerLeavingAether,
        LeavingAetherPacket::new);

    @Override
    public Type<LeavingAetherPacket> type() {
        return TYPE;
    }

    public static void execute(LeavingAetherPacket payload, AetherPayloadContext context) {
        if (context.player() != null) {
            DimensionTravelState.playerLeavingAether = payload.playerLeavingAether();
        }
    }
}
