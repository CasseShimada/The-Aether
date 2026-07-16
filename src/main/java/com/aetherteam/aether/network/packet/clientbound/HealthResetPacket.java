package com.aetherteam.aether.network.packet.clientbound;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.attachment.AetherPlayerAttachment;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;

/**
 * Sets the player's Life Shard data and refreshes the modifier and health values. This is called by {@link com.aetherteam.aether.command.PlayerAttachmentCommand}.
 */
public record HealthResetPacket(int entityID, int value) implements CustomPacketPayload {
    public static final Type<HealthResetPacket> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Aether.MODID, "reset_health"));

    public static final StreamCodec<RegistryFriendlyByteBuf, HealthResetPacket> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.INT,
        HealthResetPacket::entityID,
        ByteBufCodecs.INT,
        HealthResetPacket::value,
        HealthResetPacket::new);

    @Override
    public Type<HealthResetPacket> type() {
        return TYPE;
    }

    public static void execute(HealthResetPacket payload, @Nullable Player contextPlayer) {
        if (contextPlayer != null && contextPlayer.level().getEntity(payload.entityID()) instanceof Player player) {
            var data = player.getAttachedOrCreate(AetherDataAttachments.AETHER_PLAYER);
            data.setLifeShardCount(payload.value());
            AttributeInstance attribute = player.getAttribute(Attributes.MAX_HEALTH);
            if (attribute != null) {
                attribute.removeModifier(data.getLifeShardHealthAttributeModifier().id());
            }
            player.setHealth(player.getMaxHealth());
        }
    }
}
