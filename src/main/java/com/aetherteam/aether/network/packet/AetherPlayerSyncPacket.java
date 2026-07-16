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
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.Set;

/**
 * Sync packet for values in the {@link AetherPlayerAttachment} class.
 */
public class AetherPlayerSyncPacket extends SyncEntityPacket<AetherPlayerAttachment> {
    public static final Type<AetherPlayerSyncPacket> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Aether.MODID, "sync_aether_player_attachment"));
    private static final Set<String> CLIENT_WRITABLE_KEYS = Set.of(
            AetherPlayerAttachment.HITTING_SYNC_KEY,
            AetherPlayerAttachment.MOVING_SYNC_KEY,
            AetherPlayerAttachment.JUMPING_SYNC_KEY,
            AetherPlayerAttachment.GRAVITITE_JUMP_ACTIVE_SYNC_KEY,
            AetherPlayerAttachment.INVISIBILITY_ENABLED_SYNC_KEY
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, AetherPlayerSyncPacket> STREAM_CODEC = CustomPacketPayload.codec(
        AetherPlayerSyncPacket::write,
        AetherPlayerSyncPacket::decode);

    public AetherPlayerSyncPacket(EntitySyncValues values) {
        super(values);
    }

    public AetherPlayerSyncPacket(int playerID, String key, AttachmentSyncable.ValueType valueType, Object value) {
        super(playerID, key, valueType, value);
    }

    @Override
    public Type<AetherPlayerSyncPacket> type() {
        return TYPE;
    }

    public static AetherPlayerSyncPacket decode(RegistryFriendlyByteBuf buf) {
        return new AetherPlayerSyncPacket(SyncEntityPacket.decodeEntityValues(buf));
    }

    @Override
    public AttachmentType<AetherPlayerAttachment> attachmentType() {
        return AetherDataAttachments.AETHER_PLAYER;
    }

    public static void executeClientbound(AetherPlayerSyncPacket payload, @Nullable Player player) {
        SyncEntityPacket.execute(payload, player);
    }

    public static void executeServerbound(AetherPlayerSyncPacket payload, ServerPlayer player) {
        if (isValidServerboundUpdate(payload, player.getId())) {
            SyncEntityPacket.execute(payload, player);
        }
    }

    public static boolean isValidServerboundUpdate(AetherPlayerSyncPacket payload, int senderEntityId) {
        return payload.entityID() == senderEntityId && CLIENT_WRITABLE_KEYS.contains(payload.key());
    }
}
