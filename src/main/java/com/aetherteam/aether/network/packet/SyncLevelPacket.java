package com.aetherteam.aether.network.packet;

import com.aetherteam.aether.attachment.AttachmentSyncable;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;

public abstract class SyncLevelPacket<T extends AttachmentSyncable> extends SyncPacket<T> {
    protected SyncLevelPacket(String key, AttachmentSyncable.Type valueType, Object value) {
        super(key, valueType, value);
    }

    protected SyncLevelPacket(SyncValues values) {
        super(values);
    }

    public static SyncValues decodeValues(RegistryFriendlyByteBuf buf) {
        return SyncPacket.decodeValues(buf);
    }

    public static <T extends AttachmentSyncable> void execute(SyncLevelPacket<T> payload, @Nullable Player player) {
        if (player == null) {
            return;
        }

        T attachment = player.level().getAttachedOrCreate(payload.getAttachment());
        payload.applySync(attachment);
    }
}
