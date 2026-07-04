package com.aetherteam.aether.item.accessories;

import com.aetherteam.aether.accessories.api.slot.SlotTypeReference;

/**
 * Functional interface that defines an accessory slot type for an item.
 * <p>
 * Should be used to avoid hard-coding specific slot types
 * and enable easy changing and addition of new accessory slots.
 *
 * @author Alexandra
 */
@FunctionalInterface
public interface AccessorySlotProvider {
    /**
     * @implNote May be best to pair with a static method to get a slot type if no instance methods are needed.
     * @return The {@link SlotTypeReference} used for an accessory slot.
     */
    SlotTypeReference getSlotType();
}
