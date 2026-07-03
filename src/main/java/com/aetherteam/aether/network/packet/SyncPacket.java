package com.aetherteam.aether.network.packet;

import com.aetherteam.aether.attachment.AttachmentSyncable;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.apache.commons.lang3.tuple.Triple;

import java.util.UUID;
import java.util.function.Supplier;

public abstract class SyncPacket<T extends AttachmentSyncable> implements CustomPacketPayload {
    private final String key;
    private final AttachmentSyncable.Type valueType;
    private final Object value;

    protected SyncPacket(String key, AttachmentSyncable.Type valueType, Object value) {
        this.key = key;
        this.valueType = valueType;
        this.value = value;
    }

    protected SyncPacket(Triple<String, AttachmentSyncable.Type, Object> values) {
        this(values.getLeft(), values.getMiddle(), values.getRight());
    }

    public abstract Supplier<AttachmentType<T>> getAttachment();

    public String key() {
        return this.key;
    }

    public AttachmentSyncable.Type valueType() {
        return this.valueType;
    }

    public Object value() {
        return this.value;
    }

    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeUtf(this.key);
        buf.writeEnum(this.valueType);
        this.writeValue(buf, this.valueType, this.value);
    }

    protected void applySync(T attachment) {
        attachment.executeSynched(this.key, this.valueType, this.value);
    }

    protected static Triple<String, AttachmentSyncable.Type, Object> decodeValues(RegistryFriendlyByteBuf buf) {
        String key = buf.readUtf();
        AttachmentSyncable.Type type = buf.readEnum(AttachmentSyncable.Type.class);
        Object value = readValue(buf, type);
        return Triple.of(key, type, value);
    }

    private static void writeValue(RegistryFriendlyByteBuf buf, AttachmentSyncable.Type type, Object value) {
        switch (type) {
            case BOOLEAN -> buf.writeBoolean((Boolean) value);
            case INT -> buf.writeInt((Integer) value);
            case LONG -> buf.writeLong((Long) value);
            case FLOAT -> buf.writeFloat((Float) value);
            case DOUBLE -> buf.writeDouble((Double) value);
            case STRING -> {
                String text = (String) value;
                if (text == null) {
                    buf.writeBoolean(false);
                } else {
                    buf.writeBoolean(true);
                    buf.writeUtf(text);
                }
            }
            case UUID -> {
                UUID uuid = (UUID) value;
                if (uuid == null) {
                    buf.writeBoolean(false);
                } else {
                    buf.writeBoolean(true);
                    buf.writeUUID(uuid);
                }
            }
        }
    }

    private static Object readValue(RegistryFriendlyByteBuf buf, AttachmentSyncable.Type type) {
        return switch (type) {
            case BOOLEAN -> buf.readBoolean();
            case INT -> buf.readInt();
            case LONG -> buf.readLong();
            case FLOAT -> buf.readFloat();
            case DOUBLE -> buf.readDouble();
            case STRING -> buf.readBoolean() ? buf.readUtf() : null;
            case UUID -> buf.readBoolean() ? buf.readUUID() : null;
        };
    }
}
