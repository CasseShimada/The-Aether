package com.aetherteam.aether.accessories.effect;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AccessoryDeathProtectionTest {
    @Test
    void heldDeathProtectionAlwaysWinsBeforeAccessoryFallback() {
        assertFalse(AccessoryEffectBridge.shouldTryAccessoryDeathProtection(true, false));
        assertTrue(AccessoryEffectBridge.shouldTryAccessoryDeathProtection(false, false));
        assertFalse(AccessoryEffectBridge.shouldTryAccessoryDeathProtection(false, true));
    }
}
