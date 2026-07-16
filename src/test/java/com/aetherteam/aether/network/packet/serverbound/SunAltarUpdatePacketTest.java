package com.aetherteam.aether.network.packet.serverbound;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SunAltarUpdatePacketTest {
    @Test
    void validatesServerTimeScaleAndSelectionBounds() {
        assertTrue(SunAltarUpdatePacket.isValidTimeSelection(0, 24_000, 24_000));
        assertTrue(SunAltarUpdatePacket.isValidTimeSelection(24_000, 24_000, 24_000));

        assertFalse(SunAltarUpdatePacket.isValidTimeSelection(-1, 24_000, 24_000));
        assertFalse(SunAltarUpdatePacket.isValidTimeSelection(24_001, 24_000, 24_000));
        assertFalse(SunAltarUpdatePacket.isValidTimeSelection(12_000, 0, 24_000));
        assertFalse(SunAltarUpdatePacket.isValidTimeSelection(12_000, 72_000, 24_000));
        assertFalse(SunAltarUpdatePacket.isValidTimeSelection(0, 0, 0));
    }
}
