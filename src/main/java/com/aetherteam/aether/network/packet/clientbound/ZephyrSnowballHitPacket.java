package com.aetherteam.aether.network.packet.clientbound;

import com.aetherteam.aether.Aether;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;

/**
 * Used to move the player on the client when they are hit by a ZephyrSnowBallEntity on the server.
 */
public record ZephyrSnowballHitPacket(int entityID, double xSpeed, double zSpeed) implements CustomPacketPayload {
    public static final Type<ZephyrSnowballHitPacket> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Aether.MODID, "zephyr_snowball_knockback_player"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ZephyrSnowballHitPacket> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.INT,
        ZephyrSnowballHitPacket::entityID,
        ByteBufCodecs.DOUBLE,
        ZephyrSnowballHitPacket::xSpeed,
        ByteBufCodecs.DOUBLE,
        ZephyrSnowballHitPacket::zSpeed,
        ZephyrSnowballHitPacket::new);

    @Override
    public Type<ZephyrSnowballHitPacket> type() {
        return TYPE;
    }

    public static void execute(ZephyrSnowballHitPacket payload, @Nullable Player contextPlayer) {
        if (contextPlayer != null && contextPlayer.level().getEntity(payload.entityID()) instanceof Player targetPlayer) {
            if (!targetPlayer.isBlocking()) {
                targetPlayer.setDeltaMovement(targetPlayer.getDeltaMovement().x(), targetPlayer.getDeltaMovement().y() + 0.5, targetPlayer.getDeltaMovement().z());
            }
            targetPlayer.setDeltaMovement(targetPlayer.getDeltaMovement().x() + (payload.xSpeed() * 1.5F), targetPlayer.getDeltaMovement().y(), targetPlayer.getDeltaMovement().z() + (payload.zSpeed() * 1.5F));
        }
    }
}
