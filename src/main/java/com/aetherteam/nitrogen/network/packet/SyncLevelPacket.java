package com.aetherteam.nitrogen.network.packet;

import com.aetherteam.nitrogen.attachment.INBTSynchable;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import org.apache.commons.lang3.tuple.Triple;

import javax.annotation.Nullable;

public abstract class SyncLevelPacket<T extends INBTSynchable> extends SyncPacket<T> {
    protected SyncLevelPacket(String key, INBTSynchable.Type valueType, Object value) {
        super(key, valueType, value);
    }

    protected SyncLevelPacket(Triple<String, INBTSynchable.Type, Object> values) {
        super(values);
    }

    public static Triple<String, INBTSynchable.Type, Object> decodeValues(RegistryFriendlyByteBuf buf) {
        return SyncPacket.decodeValues(buf);
    }

    public static <T extends INBTSynchable> void execute(SyncLevelPacket<T> payload, @Nullable Player player) {
        if (player == null) {
            return;
        }

        T attachment = player.level().getAttachedOrCreate(payload.getAttachment().get());
        payload.applySync(attachment);
    }
}
