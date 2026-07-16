package com.aetherteam.aether.network.packet.clientbound;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.AetherConfig;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import java.util.LinkedHashMap;
import java.util.Map;

public record ServerConfigSyncPacket(Map<String, String> values) implements CustomPacketPayload {
    private static final int MAX_ENTRIES = 128;
    private static final int MAX_PATH_LENGTH = 512;
    private static final int MAX_VALUE_LENGTH = 32_768;

    public static final Type<ServerConfigSyncPacket> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Aether.MODID, "server_config_sync"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerConfigSyncPacket> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public ServerConfigSyncPacket decode(RegistryFriendlyByteBuf buffer) {
            int size = buffer.readVarInt();
            if (size < 0 || size > MAX_ENTRIES) {
                throw new IllegalArgumentException("Invalid Aether server config entry count " + size);
            }
            Map<String, String> values = new LinkedHashMap<>();
            for (int index = 0; index < size; index++) {
                values.put(buffer.readUtf(MAX_PATH_LENGTH), buffer.readUtf(MAX_VALUE_LENGTH));
            }
            return new ServerConfigSyncPacket(values);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buffer, ServerConfigSyncPacket payload) {
            if (payload.values().size() > MAX_ENTRIES) {
                throw new IllegalArgumentException("Too many Aether server config entries to synchronize");
            }
            buffer.writeVarInt(payload.values().size());
            payload.values().forEach((path, value) -> {
                buffer.writeUtf(path, MAX_PATH_LENGTH);
                buffer.writeUtf(value, MAX_VALUE_LENGTH);
            });
        }
    };

    public ServerConfigSyncPacket {
        values = Map.copyOf(values);
    }

    public static ServerConfigSyncPacket create() {
        return new ServerConfigSyncPacket(AetherConfig.synchronizedServerValues());
    }

    public static void execute(ServerConfigSyncPacket payload) {
        AetherConfig.applySynchronizedServerValues(payload.values());
        Aether.LOGGER.info("Applied {} synchronized Aether server config values (Beds explode: {})",
                payload.values().size(), AetherConfig.SERVER.enable_bed_explosions.get());
    }

    @Override
    public Type<ServerConfigSyncPacket> type() {
        return TYPE;
    }
}
