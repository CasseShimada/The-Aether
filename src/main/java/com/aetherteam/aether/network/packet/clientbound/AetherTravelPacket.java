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
 * Marks the player as being in the process of teleporting to the Aether. This is used for displaying "Ascending to the Aether" in the world loading screen.
 *
 * @see com.aetherteam.aether.client.event.hooks.GuiTriviaHooks#drawAetherTravelMessage
 */
public record AetherTravelPacket(boolean displayAetherTravel) implements CustomPacketPayload {
    public static final Type<AetherTravelPacket> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Aether.MODID, "travel_across_dimensions"));

    public static final StreamCodec<RegistryFriendlyByteBuf, AetherTravelPacket> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.BOOL,
        AetherTravelPacket::displayAetherTravel,
        AetherTravelPacket::new);

    @Override
    public Type<AetherTravelPacket> type() {
        return TYPE;
    }

    public static void execute(AetherTravelPacket payload, AetherPayloadContext context) {
        if (context.player() != null) {
            DimensionTravelState.displayAetherTravel = payload.displayAetherTravel();
        }
    }
}
