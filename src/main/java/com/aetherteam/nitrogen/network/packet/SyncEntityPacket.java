package com.aetherteam.nitrogen.network.packet;

import com.aetherteam.nitrogen.attachment.INBTSynchable;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import oshi.util.tuples.Quartet;
import org.apache.commons.lang3.tuple.Triple;

import javax.annotation.Nullable;

public abstract class SyncEntityPacket<T extends INBTSynchable> extends SyncPacket<T> {
    private final int entityID;

    protected SyncEntityPacket(int entityID, String key, INBTSynchable.Type valueType, Object value) {
        super(key, valueType, value);
        this.entityID = entityID;
    }

    protected SyncEntityPacket(Quartet<Integer, String, INBTSynchable.Type, Object> values) {
        this(values.getA(), values.getB(), values.getC(), values.getD());
    }

    public int entityID() {
        return this.entityID;
    }

    @Override
    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeVarInt(this.entityID);
        super.write(buf);
    }

    public static Quartet<Integer, String, INBTSynchable.Type, Object> decodeEntityValues(RegistryFriendlyByteBuf buf) {
        int entityID = buf.readVarInt();
        Triple<String, INBTSynchable.Type, Object> values = decodeValues(buf);
        return new Quartet<>(entityID, values.getLeft(), values.getMiddle(), values.getRight());
    }

    public static <T extends INBTSynchable> void execute(SyncEntityPacket<T> payload, @Nullable Player player) {
        if (player == null) {
            return;
        }

        Entity entity = player.level().getEntity(payload.entityID());
        if (entity == null) {
            return;
        }

        T attachment = entity.getAttachedOrCreate(payload.getAttachment().get());
        payload.applySync(attachment);
    }
}
