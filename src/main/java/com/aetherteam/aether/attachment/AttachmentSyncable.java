package com.aetherteam.aether.attachment;

import com.aetherteam.aether.network.AttachmentSyncPacketDispatcher;
import com.aetherteam.aether.network.packet.SyncPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface AttachmentSyncable {
    Map<String, SyncField> getSyncFields();

    SyncPacket<?> getSyncPacket(int entityID, String key, ValueType valueType, Object value);

    default void forceSyncToClients(int entityID) {
        for (Map.Entry<String, SyncField> entry : this.getSyncFields().entrySet()) {
            SyncField value = entry.getValue();
            AttachmentSyncPacketDispatcher.sendToClients(this.getSyncPacket(entityID, entry.getKey(), value.valueType(), value.getter().get()));
        }
    }

    default void setSyncedToServer(int entityID, String key, @Nullable Object value) {
        SyncPacket<?> packet = this.createSyncPacket(entityID, key, value);
        if (packet != null) {
            AttachmentSyncPacketDispatcher.sendToServer(packet);
        }
    }

    default void setSyncedToClients(int entityID, String key, @Nullable Object value) {
        SyncPacket<?> packet = this.createSyncPacket(entityID, key, value);
        if (packet != null) {
            AttachmentSyncPacketDispatcher.sendToClients(packet);
        }
    }

    default void setSyncedToPlayer(int entityID, String key, @Nullable Object value, ServerPlayer player) {
        SyncPacket<?> packet = this.createSyncPacket(entityID, key, value);
        if (packet != null) {
            AttachmentSyncPacketDispatcher.sendToPlayer(packet, player);
        }
    }

    default void setSyncedToDimension(int entityID, String key, @Nullable Object value, Level level) {
        SyncPacket<?> packet = this.createSyncPacket(entityID, key, value);
        if (packet != null) {
            AttachmentSyncPacketDispatcher.sendToDimension(packet, level);
        }
    }

    default void executeSynced(String key, ValueType valueType, @Nullable Object value) {
        SyncField data = this.getSyncFields().get(key);
        if (data == null || data.valueType() != valueType) {
            return;
        }
        data.setter().accept(value);
    }

    private @Nullable SyncPacket<?> createSyncPacket(int entityID, String key, @Nullable Object value) {
        SyncField data = this.getSyncFields().get(key);
        return data != null ? this.getSyncPacket(entityID, key, data.valueType(), value) : null;
    }

    record SyncField(ValueType valueType, Consumer<Object> setter, Supplier<Object> getter) { }

    enum ValueType {
        BOOLEAN,
        INT,
        LONG,
        FLOAT,
        DOUBLE,
        STRING,
        UUID
    }
}
