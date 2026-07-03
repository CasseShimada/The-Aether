package com.aetherteam.aether.attachment;

import com.aetherteam.aether.network.PacketDistributor;
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

    SyncPacket getSyncPacket(int entityID, String key, Type type, Object value);

    default void forceSync(int entityID, Direction direction) {
        for (Map.Entry<String, SyncField> entry : this.getSyncFields().entrySet()) {
            SyncField value = entry.getValue();
            this.setSynced(entityID, direction, entry.getKey(), value.type(), value.getter().get());
        }
    }

    default void setSynced(int entityID, Direction direction, String key, @Nullable Object value, Object... context) {
        SyncField data = this.getSyncFields().get(key);
        if (data == null) {
            return;
        }
        this.setSynced(entityID, direction, key, data.type(), value, context);
    }

    default void setSynced(int entityID, Direction direction, String key, Type type, @Nullable Object value, Object... context) {
        this.sendPacket(this.getSyncPacket(entityID, key, type, value), direction, context);
    }

    default void executeSynced(String key, Type type, @Nullable Object value) {
        SyncField data = this.getSyncFields().get(key);
        if (data == null || data.type() != type) {
            return;
        }
        data.setter().accept(value);
    }

    private void sendPacket(SyncPacket packet, Direction direction, Object... context) {
        switch (direction) {
            case SERVER -> PacketDistributor.sendToServer(packet);
            case CLIENT -> this.sendToClients(packet, context);
            case PLAYER -> this.sendToPlayer(packet, context);
            case DIMENSION -> this.sendToDimension(packet, context);
        }
    }

    private void sendToClients(SyncPacket packet, Object... context) {
        if (context.length > 0 && context[0] instanceof ServerPlayer player) {
            PacketDistributor.sendToPlayer(player, packet);
            return;
        }
        PacketDistributor.sendToAllPlayers(packet);
    }

    private void sendToPlayer(SyncPacket packet, Object... context) {
        if (context.length > 0 && context[0] instanceof ServerPlayer player) {
            PacketDistributor.sendToPlayer(player, packet);
        }
    }

    private void sendToDimension(SyncPacket packet, Object... context) {
        if (context.length == 0 || !(context[0] instanceof Level level) || !(level instanceof ServerLevel serverLevel)) {
            return;
        }
        for (ServerPlayer player : PlayerLookup.level(serverLevel)) {
            PacketDistributor.sendToPlayer(player, packet);
        }
    }

    enum Direction {
        CLIENT,
        SERVER,
        PLAYER,
        DIMENSION
    }

    record SyncField(Type type, Consumer<Object> setter, Supplier<Object> getter) { }

    enum Type {
        BOOLEAN,
        INT,
        LONG,
        FLOAT,
        DOUBLE,
        STRING,
        UUID
    }
}
