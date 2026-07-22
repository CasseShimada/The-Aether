package com.aetherteam.aether.integration.viewer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AetherViewerBlockPolicyTest {

    @Test
    void doorwayNamesMapDeterministicallyAndUnknownNamesFailClosed() {
        assertEquals("locked_carved_stone",
                AetherViewerBlockPolicy.lockedDoorwayPath("boss_doorway_carved_stone"));
        assertEquals("locked_light_hellfire_stone",
                AetherViewerBlockPolicy.lockedDoorwayPath("treasure_doorway_light_hellfire_stone"));
        assertEquals("locked_carved_stone",
                AetherViewerBlockPolicy.lockedDoorwayPath("unknown_doorway"));
        assertEquals("locked_carved_stone",
                AetherViewerBlockPolicy.lockedDoorwayPath("boss_doorway_"));
    }
}
