package com.aetherteam.aether.network.packet;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.attachment.PhoenixArrowAttachment;
import com.aetherteam.aether.attachment.AttachmentSyncable;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import com.aetherteam.aether.network.AetherPayloadContext;

import java.util.function.Supplier;

/**
 * Sync packet for values in the {@link PhoenixArrowAttachment} class.
 */
public class PhoenixArrowSyncPacket extends SyncEntityPacket<PhoenixArrowAttachment> {
    public static final Type<PhoenixArrowSyncPacket> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Aether.MODID, "sync_phoenix_arrow_attachment"));

    public static final StreamCodec<RegistryFriendlyByteBuf, PhoenixArrowSyncPacket> STREAM_CODEC = CustomPacketPayload.codec(
        PhoenixArrowSyncPacket::write,
        PhoenixArrowSyncPacket::decode);

    public PhoenixArrowSyncPacket(EntitySyncValues values) {
        super(values);
    }

    public PhoenixArrowSyncPacket(int playerID, String key, AttachmentSyncable.Type type, Object value) {
        super(playerID, key, type, value);
    }

    @Override
    public Type<PhoenixArrowSyncPacket> type() {
        return TYPE;
    }

    public static PhoenixArrowSyncPacket decode(RegistryFriendlyByteBuf buf) {
        return new PhoenixArrowSyncPacket(SyncEntityPacket.decodeEntityValues(buf));
    }

    @Override
    public Supplier<AttachmentType<PhoenixArrowAttachment>> getAttachment() {
        return () -> AetherDataAttachments.PHOENIX_ARROW;
    }

    public static void execute(PhoenixArrowSyncPacket payload, AetherPayloadContext context) {
        SyncEntityPacket.execute(payload, context.player());
    }
}
