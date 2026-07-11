package com.aetherteam.aether.network.packet.clientbound;

import com.aetherteam.aether.Aether;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;

/**
 * Swings the player's hand when feeding a Moa.
 */
public record MoaInteractPacket(int playerID, boolean mainHand) implements CustomPacketPayload {
    public static final Type<MoaInteractPacket> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Aether.MODID, "interact_with_moa"));

    public static final StreamCodec<RegistryFriendlyByteBuf, MoaInteractPacket> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.INT,
        MoaInteractPacket::playerID,
        ByteBufCodecs.BOOL,
        MoaInteractPacket::mainHand,
        MoaInteractPacket::new);

    @Override
    public Type<MoaInteractPacket> type() {
        return TYPE;
    }

    public static void execute(MoaInteractPacket payload, @Nullable Player contextPlayer) {
        if (contextPlayer != null) {
            Entity entity = contextPlayer.level().getEntity(payload.playerID());
            if (entity instanceof Player player) {
                player.swing(payload.mainHand() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND);
            }
        }
    }
}
