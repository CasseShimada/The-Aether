package com.aetherteam.aether.network.packet.clientbound;

import com.aetherteam.aether.Aether;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import java.util.UUID;

/**
 * Packets to help sync the server's Aether boss bars with the client's.
 */
public abstract class BossInfoPacket implements CustomPacketPayload {
    protected final UUID bossEvent;
    protected final int entityID;

    public BossInfoPacket(UUID bossEvent, int entityID) {
        this.bossEvent = bossEvent;
        this.entityID = entityID;
    }

    /**
     * Adds a boss bar for the client.
     */
    public static class Display extends BossInfoPacket {
        public static final Type<BossInfoPacket.Display> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Aether.MODID, "add_custom_bossbar"));

        public static final StreamCodec<RegistryFriendlyByteBuf, BossInfoPacket.Display> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC,
            BossInfoPacket.Display::getBossEvent,
            ByteBufCodecs.INT,
            BossInfoPacket.Display::getEntityID,
            BossInfoPacket.Display::new);

        public Display(UUID bossEvent, int entityID) {
            super(bossEvent, entityID);
        }

        @Override
        public Type<BossInfoPacket.Display> type() {
            return TYPE;
        }
    }

    /**
     * Removes a boss bar for the client.
     */
    public static class Remove extends BossInfoPacket {
        public static final Type<BossInfoPacket.Remove> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Aether.MODID, "remove_custom_bossbar"));

        public static final StreamCodec<RegistryFriendlyByteBuf, BossInfoPacket.Remove> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC,
            BossInfoPacket.Remove::getBossEvent,
            ByteBufCodecs.INT,
            BossInfoPacket.Remove::getEntityID,
            BossInfoPacket.Remove::new);

        public Remove(UUID bossEvent, int entityID) {
            super(bossEvent, entityID);
        }

        @Override
        public Type<BossInfoPacket.Remove> type() {
            return TYPE;
        }
    }

    public UUID getBossEvent() {
        return this.bossEvent;
    }

    public int getEntityID() {
        return this.entityID;
    }
}
