package com.aetherteam.aether.accessories.api.slot;

@FunctionalInterface
public interface SlotTypeReference {
    String slotName();

    static SlotTypeReference of(String slotName) {
        return new Simple(slotName);
    }

    record Simple(String slotName) implements SlotTypeReference {
    }
}
