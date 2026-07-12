package com.aetherteam.aether.network.packet;

import com.aetherteam.aether.attachment.AttachmentSyncable;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;

public abstract class SyncEntityPacket<T extends AttachmentSyncable> extends SyncPacket<T> {
    private final int entityID;

    protected SyncEntityPacket(int entityID, String key, AttachmentSyncable.ValueType valueType, Object value) {
        super(key, valueType, value);
        this.entityID = entityID;
    }

    protected SyncEntityPacket(EntitySyncValues values) {
        this(values.entityID(), values.key(), values.valueType(), values.value());
    }

    public int entityID() {
        return this.entityID;
    }

    @Override
    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeVarInt(this.entityID);
        super.write(buf);
    }

    public static EntitySyncValues decodeEntityValues(RegistryFriendlyByteBuf buf) {
        int entityID = buf.readVarInt();
        SyncValues values = decodeValues(buf);
        return new EntitySyncValues(entityID, values.key(), values.valueType(), values.value());
    }

    public record EntitySyncValues(int entityID, String key, AttachmentSyncable.ValueType valueType, Object value) { }

    public static <T extends AttachmentSyncable> void execute(SyncEntityPacket<T> payload, @Nullable Player player) {
        if (player == null) {
            return;
        }

        Entity entity = player.level().getEntity(payload.entityID());
        if (entity == null) {
            return;
        }

        T attachment = entity.getAttachedOrCreate(payload.attachmentType());
        payload.applySync(attachment);
    }
}
