package com.aetherteam.aether.network.packet;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.attachment.AetherTimeAttachment;
import com.aetherteam.aether.attachment.AttachmentSyncable;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;

/**
 * Sync packet for values in the {@link AetherTimeAttachment} class.
 */
public class AetherTimeSyncPacket extends SyncLevelPacket<AetherTimeAttachment> {
    public static final Type<AetherTimeSyncPacket> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Aether.MODID, "sync_aether_time_attachment"));

    public static final StreamCodec<RegistryFriendlyByteBuf, AetherTimeSyncPacket> STREAM_CODEC = CustomPacketPayload.codec(
        AetherTimeSyncPacket::write,
        AetherTimeSyncPacket::decode);

    public AetherTimeSyncPacket(SyncValues values) {
        super(values);
    }

    public AetherTimeSyncPacket(String key, AttachmentSyncable.ValueType valueType, Object value) {
        super(key, valueType, value);
    }

    @Override
    public Type<AetherTimeSyncPacket> type() {
        return TYPE;
    }

    public static AetherTimeSyncPacket decode(RegistryFriendlyByteBuf buf) {
        return new AetherTimeSyncPacket(SyncLevelPacket.decodeValues(buf));
    }

    @Override
    public AttachmentType<AetherTimeAttachment> attachmentType() {
        return AetherDataAttachments.AETHER_TIME;
    }

    public static void execute(AetherTimeSyncPacket payload, @Nullable Player player) {
        SyncLevelPacket.execute(payload, player);
    }
}
