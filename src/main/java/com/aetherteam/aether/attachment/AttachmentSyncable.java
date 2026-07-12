package com.aetherteam.aether.attachment;

import com.aetherteam.aether.network.AetherPacketSender;
import com.aetherteam.aether.network.packet.SyncPacket;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface AttachmentSyncable {
    Map<String, SyncField> getSyncFields();

    SyncPacket<?> getSyncPacket(int entityID, String key, ValueType valueType, Object value);

    default void forceSync(int entityID, SyncTarget target) {
        for (Map.Entry<String, SyncField> entry : this.getSyncFields().entrySet()) {
            SyncField value = entry.getValue();
            this.setSynced(entityID, target, entry.getKey(), value.valueType(), value.getter().get());
        }
    }

    default void setSynced(int entityID, SyncTarget target, String key, @Nullable Object value, Object... context) {
        SyncField data = this.getSyncFields().get(key);
        if (data == null) {
            return;
        }
        this.setSynced(entityID, target, key, data.valueType(), value, context);
    }

    default void setSynced(int entityID, SyncTarget target, String key, ValueType valueType, @Nullable Object value, Object... context) {
        this.sendPacket(this.getSyncPacket(entityID, key, valueType, value), target, context);
    }

    default void executeSynced(String key, ValueType valueType, @Nullable Object value) {
        SyncField data = this.getSyncFields().get(key);
        if (data == null || data.valueType() != valueType) {
            return;
        }
        data.setter().accept(value);
    }

    private void sendPacket(SyncPacket<?> packet, SyncTarget target, Object... context) {
        switch (target) {
            case SERVER -> AetherPacketSender.sendToServer(packet);
            case CLIENT -> this.sendToClients(packet, context);
            case PLAYER -> this.sendToPlayer(packet, context);
            case DIMENSION -> this.sendToDimension(packet, context);
        }
    }

    private void sendToClients(SyncPacket<?> packet, Object... context) {
        if (context.length > 0 && context[0] instanceof ServerPlayer player) {
            AetherPacketSender.sendToPlayer(player, packet);
            return;
        }
        AetherPacketSender.sendToAllPlayers(packet);
    }

    private void sendToPlayer(SyncPacket<?> packet, Object... context) {
        if (context.length > 0 && context[0] instanceof ServerPlayer player) {
            AetherPacketSender.sendToPlayer(player, packet);
        }
    }

    private void sendToDimension(SyncPacket<?> packet, Object... context) {
        if (context.length == 0 || !(context[0] instanceof Level level) || !(level instanceof ServerLevel serverLevel)) {
            return;
        }
        for (ServerPlayer player : PlayerLookup.level(serverLevel)) {
            AetherPacketSender.sendToPlayer(player, packet);
        }
    }

    enum SyncTarget {
        CLIENT,
        SERVER,
        PLAYER,
        DIMENSION
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
