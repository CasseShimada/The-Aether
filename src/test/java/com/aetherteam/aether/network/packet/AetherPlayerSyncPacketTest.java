package com.aetherteam.aether.network.packet;

import com.aetherteam.aether.attachment.AetherPlayerAttachment;
import com.aetherteam.aether.attachment.AttachmentSyncable;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AetherPlayerSyncPacketTest {
    @Test
    void acceptsOnlyAllowlistedUpdatesForTheSender() {
        int senderEntityId = 42;
        AetherPlayerSyncPacket allowed = packet(senderEntityId, AetherPlayerAttachment.HITTING_SYNC_KEY);
        AetherPlayerSyncPacket serverOwned = packet(senderEntityId, AetherPlayerAttachment.LIFE_SHARD_COUNT_SYNC_KEY);
        AetherPlayerSyncPacket otherPlayer = packet(senderEntityId + 1, AetherPlayerAttachment.HITTING_SYNC_KEY);

        assertTrue(AetherPlayerSyncPacket.isValidServerboundUpdate(allowed, senderEntityId));
        assertFalse(AetherPlayerSyncPacket.isValidServerboundUpdate(serverOwned, senderEntityId));
        assertFalse(AetherPlayerSyncPacket.isValidServerboundUpdate(otherPlayer, senderEntityId));
    }

    private static AetherPlayerSyncPacket packet(int entityId, String key) {
        return new AetherPlayerSyncPacket(entityId, key, AttachmentSyncable.ValueType.BOOLEAN, false);
    }
}
