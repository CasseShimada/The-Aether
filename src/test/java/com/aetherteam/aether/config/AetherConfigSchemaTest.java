package com.aetherteam.aether.config;

import com.aetherteam.aether.AetherConfig;
import com.aetherteam.aether.data.ConfigSerializationUtil;
import com.google.gson.JsonSyntaxException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AetherConfigSchemaTest {
    @Test
    void schemaContainsAllReleasedEntries() {
        assertEquals(1, AetherConfig.STARTUP_FILE.entries().size());
        assertEquals(27, AetherConfig.SERVER_FILE.entries().size());
        assertEquals(9, AetherConfig.COMMON_FILE.entries().size());
        assertEquals(27, AetherConfig.CLIENT_FILE.entries().size());
    }

    @Test
    void serializedDataPackPathsRemainStable() {
        assertEquals("[World Generation, Generate Tall Grass in the Aether]",
                ConfigSerializationUtil.serialize(AetherConfig.SERVER.generate_tall_grass));
        assertSame(AetherConfig.SERVER.generate_tall_grass,
                ConfigSerializationUtil.deserialize("[World Generation, Generate Tall Grass in the Aether]"));
        assertSame(AetherConfig.COMMON.enable_startup_loot,
                ConfigSerializationUtil.deserialize("[Gameplay, Gives starting loot on entry]"));
        assertThrows(JsonSyntaxException.class,
                () -> ConfigSerializationUtil.deserialize("[Gameplay, Maximum consumable Life Shards]"));
    }
}
