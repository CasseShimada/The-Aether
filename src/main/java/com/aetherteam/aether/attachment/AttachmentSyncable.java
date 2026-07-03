package com.aetherteam.aether.attachment;

import com.aetherteam.aether.network.PacketDistributor;
import com.aetherteam.aether.network.packet.SyncPacket;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.tuple.Triple;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface AttachmentSyncable {
    Map<String, Triple<Type, Consumer<Object>, Supplier<Object>>> getSynchableFunctions();

    SyncPacket getSyncPacket(int entityID, String key, Type type, Object value);

    default void forceSync(int entityID, Direction direction) {
        for (Map.Entry<String, Triple<Type, Consumer<Object>, Supplier<Object>>> entry : this.getSynchableFunctions().entrySet()) {
            Triple<Type, Consumer<Object>, Supplier<Object>> value = entry.getValue();
            this.setSynched(entityID, direction, entry.getKey(), value.getLeft(), value.getRight().get());
        }
    }

    default void setSynched(int entityID, Direction direction, String key, @Nullable Object value, Object... context) {
        Triple<Type, Consumer<Object>, Supplier<Object>> data = this.getSynchableFunctions().get(key);
        if (data == null) {
            return;
        }
        this.setSynched(entityID, direction, key, data.getLeft(), value, context);
    }

    default void setSynched(int entityID, Direction direction, String key, Type type, @Nullable Object value, Object... context) {
        this.sendPacket(this.getSyncPacket(entityID, key, type, value), direction, context);
    }

    default void executeSynched(String key, Type type, @Nullable Object value) {
        Triple<Type, Consumer<Object>, Supplier<Object>> data = this.getSynchableFunctions().get(key);
        if (data == null || data.getLeft() != type) {
            return;
        }
        data.getMiddle().accept(value);
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
