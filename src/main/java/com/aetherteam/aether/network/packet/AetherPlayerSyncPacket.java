package com.aetherteam.aether.network.packet;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.attachment.AetherPlayerAttachment;
import com.aetherteam.aether.attachment.AttachmentSyncable;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;

/**
 * Sync packet for values in the {@link AetherPlayerAttachment} class.
 */
public class AetherPlayerSyncPacket extends SyncEntityPacket<AetherPlayerAttachment> {
    public static final Type<AetherPlayerSyncPacket> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Aether.MODID, "sync_aether_player_attachment"));

    public static final StreamCodec<RegistryFriendlyByteBuf, AetherPlayerSyncPacket> STREAM_CODEC = CustomPacketPayload.codec(
        AetherPlayerSyncPacket::write,
        AetherPlayerSyncPacket::decode);

    public AetherPlayerSyncPacket(EntitySyncValues values) {
        super(values);
    }

    public AetherPlayerSyncPacket(int playerID, String key, AttachmentSyncable.Type type, Object value) {
        super(playerID, key, type, value);
    }

    @Override
    public Type<AetherPlayerSyncPacket> type() {
        return TYPE;
    }

    public static AetherPlayerSyncPacket decode(RegistryFriendlyByteBuf buf) {
        return new AetherPlayerSyncPacket(SyncEntityPacket.decodeEntityValues(buf));
    }

    @Override
    public AttachmentType<AetherPlayerAttachment> getAttachment() {
        return AetherDataAttachments.AETHER_PLAYER;
    }

    public static void execute(AetherPlayerSyncPacket payload, @Nullable Player player) {
        SyncEntityPacket.execute(payload, player);
    }
}
