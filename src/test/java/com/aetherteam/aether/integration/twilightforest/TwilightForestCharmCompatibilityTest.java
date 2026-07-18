package com.aetherteam.aether.integration.twilightforest;

import net.minecraft.nbt.CompoundTag;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TwilightForestCharmCompatibilityTest {
    @Test
    void accessoryFallbackOnlyRunsWhenInventoryConsumptionFailed() {
        AtomicInteger calls = new AtomicInteger();
        assertTrue(TwilightForestCharmCompatibility.afterInventoryResult(true, () -> {
            calls.incrementAndGet();
            return true;
        }));
        assertEquals(0, calls.get());

        assertTrue(TwilightForestCharmCompatibility.afterInventoryResult(false, () -> {
            calls.incrementAndGet();
            return true;
        }));
        assertEquals(1, calls.get());
    }

    @Test
    void saveFlagControlsCharmStackAndPreservesCompleteEncodedStack() {
        CompoundTag encoded = new CompoundTag();
        encoded.putString("id", "twilightforest:charm_of_keeping_3");
        encoded.putInt("count", 3);
        CompoundTag components = new CompoundTag();
        components.putString("minecraft:custom_name", "component-data");
        encoded.put("components", components);

        CompoundTag persistent = new CompoundTag();
        TwilightForestCharmCompatibility.saveConsumedStack(persistent, false, encoded);
        assertFalse(persistent.contains(TwilightForestCharmCompatibility.CONSUMED_CHARM_TAG));

        TwilightForestCharmCompatibility.saveConsumedStack(persistent, true, encoded);
        CompoundTag saved = persistent.getCompoundOrEmpty(TwilightForestCharmCompatibility.CONSUMED_CHARM_TAG);
        assertEquals(3, saved.getIntOr("count", 0));
        assertEquals("component-data", saved.getCompoundOrEmpty("components").getStringOr("minecraft:custom_name", ""));
        assertFalse(saved == encoded);
    }

    @Test
    void optionalCompatibilityClassesLoadWithoutTwilightForest() throws Exception {
        assertSame(TwilightForestCharmCompatibility.class,
            Class.forName("com.aetherteam.aether.integration.twilightforest.TwilightForestCharmCompatibility"));
        Class.forName("com.aetherteam.aether.integration.twilightforest.TwilightForestIntegration");
        Class.forName("com.aetherteam.aether.integration.twilightforest.mixin.TFItemStackUtilsMixin");
        Class.forName("com.aetherteam.aether.integration.twilightforest.mixin.TwilightForestMixinPlugin");
    }
}
