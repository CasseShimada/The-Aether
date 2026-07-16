package com.aetherteam.aether.attachment;

import com.aetherteam.aether.network.AetherPacketSender;
import com.aetherteam.aether.network.packet.SyncPacket;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AttachmentSyncableTest {
    @Test
    void appliesLocalSetterAfterDispatchingServerPacket() {
        List<String> events = new ArrayList<>();
        TestSyncable syncable = new TestSyncable(events);
        AetherPacketSender.registerServerPacketSender(payload -> {
            assertFalse(syncable.value);
            events.add("send");
        });

        try {
            syncable.setSyncedToServer(7, TestSyncable.VALUE_KEY, true);
        } finally {
            AetherPacketSender.registerServerPacketSender(payload -> { });
        }

        assertTrue(syncable.value);
        assertEquals(List.of("send", "apply"), events);
    }

    private static final class TestSyncable implements AttachmentSyncable {
        private static final String VALUE_KEY = "value";

        private final Map<String, SyncField> syncFields;
        private boolean value;

        private TestSyncable(List<String> events) {
            this.syncFields = Map.of(VALUE_KEY, new SyncField(ValueType.BOOLEAN, value -> {
                this.value = (boolean) value;
                events.add("apply");
            }, () -> this.value));
        }

        @Override
        public Map<String, SyncField> getSyncFields() {
            return this.syncFields;
        }

        @Override
        public SyncPacket<?> getSyncPacket(int entityID, String key, ValueType valueType, Object value) {
            return new TestSyncPacket(key, valueType, value);
        }
    }

    private static final class TestSyncPacket extends SyncPacket<TestSyncable> {
        private static final Type<TestSyncPacket> TYPE = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("aether_test", "sync"));

        private TestSyncPacket(String key, AttachmentSyncable.ValueType valueType, Object value) {
            super(key, valueType, value);
        }

        @Override
        public AttachmentType<TestSyncable> attachmentType() {
            return null;
        }

        @Override
        public Type<TestSyncPacket> type() {
            return TYPE;
        }
    }
}
