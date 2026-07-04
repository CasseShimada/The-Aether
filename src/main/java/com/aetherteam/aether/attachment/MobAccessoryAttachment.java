package com.aetherteam.aether.attachment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.aetherteam.aether.accessories.api.slot.SlotTypeReference;

import java.util.HashMap;
import java.util.Map;

public class MobAccessoryAttachment {
    private final Map<String, Float> accessoryDropChances;

    public static final Codec<MobAccessoryAttachment> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.unboundedMap(Codec.STRING, Codec.FLOAT).fieldOf("drop_chances").forGetter(MobAccessoryAttachment::getAccessoryDropChances)
    ).apply(instance, MobAccessoryAttachment::new));

    public MobAccessoryAttachment() {
        this.accessoryDropChances = new HashMap<>(Map.ofEntries(
                Map.entry("hand", 0.085F),
                Map.entry("necklace", 0.085F),
                Map.entry("aether:gloves_slot", 0.085F),
                Map.entry("aether:pendant_slot", 0.085F)
        ));
    }

    private MobAccessoryAttachment(Map<String, Float> dropChances) {
        this.accessoryDropChances = new HashMap<>(dropChances);
    }

    public synchronized void setGuaranteedDrop(SlotTypeReference slotType) {
        if (this.accessoryDropChances.containsKey(slotType.slotName())) {
            this.accessoryDropChances.put(slotType.slotName(), 2.0F);
        }
    }

    public synchronized float getEquipmentDropChance(SlotTypeReference slotType) {
        if (this.accessoryDropChances.containsKey(slotType.slotName())) {
            return this.accessoryDropChances.get(slotType.slotName());
        }
        return 0.0F;
    }

    public synchronized void setDropChance(SlotTypeReference slotType, float chance) {
        if (this.accessoryDropChances.containsKey(slotType.slotName())) {
            this.accessoryDropChances.put(slotType.slotName(), chance);
        }
    }

    public synchronized Map<String, Float> getAccessoryDropChances() {
        return Map.copyOf(this.accessoryDropChances);
    }
}
