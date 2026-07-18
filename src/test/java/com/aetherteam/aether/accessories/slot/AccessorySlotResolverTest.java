package com.aetherteam.aether.accessories.slot;

import org.junit.jupiter.api.Test;

import static com.aetherteam.aether.accessories.slot.AccessorySlotResolver.SlotKind;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AccessorySlotResolverTest {
    @Test
    void deathProtectionOnlyMatchesGeneralAccessorySlot() {
        assertEquals(SlotKind.ACCESSORY, AccessorySlotResolver.classify(evidence(true, false, false, false, false, false, false, false, false, false)));
    }

    @Test
    void elytraOnlyMatchesBackSlot() {
        assertEquals(SlotKind.BACK, AccessorySlotResolver.classify(evidence(false, true, false, false, false, false, false, false, false, false)));
    }

    @Test
    void vanillaShieldOnlyMatchesShieldSlot() {
        assertEquals(SlotKind.SHIELD, AccessorySlotResolver.classify(evidence(false, false, true, false, false, false, false, false, false, false)));
    }

    @Test
    void curiosCharmAndHeadMapToGeneralAccessorySlot() {
        assertEquals(SlotKind.ACCESSORY, AccessorySlotResolver.classify(evidence(false, false, false, false, false, false, false, false, true, false)));
        assertEquals(SlotKind.ACCESSORY, AccessorySlotResolver.classify(evidence(false, false, false, false, false, false, false, false, false, true)));
    }

    @Test
    void multiTagPriorityIsStable() {
        assertEquals(SlotKind.BACK, AccessorySlotResolver.classify(evidence(false, true, true, true, true, true, true, true, true, true)));
        assertEquals(SlotKind.SHIELD, AccessorySlotResolver.classify(evidence(false, false, true, true, true, true, true, true, true, true)));
        assertEquals(SlotKind.RING, AccessorySlotResolver.classify(evidence(false, false, false, false, true, true, true, true, true, true)));
        assertEquals(SlotKind.ACCESSORY, AccessorySlotResolver.classify(evidence(true, true, true, true, true, true, true, true, true, true)));
    }

    @Test
    void ordinaryArmorIsNotAnAccessory() {
        assertEquals(SlotKind.NONE, AccessorySlotResolver.classify(evidence(false, false, false, false, false, false, false, false, false, false)));
    }

    private static AccessorySlotResolver.SlotEvidence evidence(boolean death, boolean elytra, boolean shield,
                                                               boolean gloves, boolean ring, boolean pendant,
                                                               boolean cape, boolean back, boolean charm, boolean head) {
        return new AccessorySlotResolver.SlotEvidence(death, elytra, shield, gloves, ring, pendant, cape, back, charm, head);
    }
}
