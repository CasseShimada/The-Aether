package com.aetherteam.aether.network.packet.serverbound;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.entity.passive.Aerbunny;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

/**
 * Sets the {@link Aerbunny#DATA_PUFFINESS_ID} value to 11. This is needed in a packet for precise animation syncing.
 */
public record AerbunnyPuffPacket(int entityID) implements CustomPacketPayload {
    public static final Type<AerbunnyPuffPacket> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Aether.MODID, "aerbunny_puff"));

    public static final StreamCodec<RegistryFriendlyByteBuf, AerbunnyPuffPacket> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.INT,
        AerbunnyPuffPacket::entityID,
        AerbunnyPuffPacket::new);

    @Override
    public Type<AerbunnyPuffPacket> type() {
        return TYPE;
    }

    public static void execute(AerbunnyPuffPacket payload, ServerPlayer player) {
        if (player.level().getEntity(payload.entityID()) instanceof Aerbunny aerbunny && aerbunny.getVehicle() == player) {
            aerbunny.puff();
        }
    }
}
