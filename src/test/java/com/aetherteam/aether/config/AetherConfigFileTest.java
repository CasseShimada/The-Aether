package com.aetherteam.aether.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AetherConfigFileTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void loadsReleasedTypesAndKeepsDefaultsForInvalidValues() throws Exception {
        AetherConfigFile file = new AetherConfigFile("aether-server.toml");
        BooleanConfigEntry bedsExplode = file.booleanEntry("Gameplay", "Beds explode", false, "comment");
        AetherConfigEntry<Integer> lifeShards = file.integerEntry("Gameplay", "Maximum consumable Life Shards", 10, "comment");
        BooleanConfigEntry gummySwets = file.booleanEntry("Gameplay", "Gummy Swets restore health", false, "comment");
        AetherConfigEntry<List<String>> dimensions = file.stringListEntry("Multiplayer", "Configure Sun Altar dimensions", List.of("aether:the_aether"), "comment");
        AetherConfigEntry<String> destination = file.stringEntry("Modpack", "Sets portal destination dimension", "aether:the_aether", "comment");
        AetherConfigEntry<String> returning = file.stringEntry("Modpack", "Sets portal return dimension", "minecraft:overworld", "comment");
        copyFixture("released_neoforge_server.toml", this.temporaryDirectory.resolve(file.fileName()));

        file.load(this.temporaryDirectory);

        assertTrue(file.isLoaded());
        assertTrue(bedsExplode.get());
        assertEquals(1024, lifeShards.get());
        assertFalse(gummySwets.get());
        assertEquals(List.of("aether:the_aether", "thirdparty:sky"), dimensions.get());
        assertEquals("thirdparty:destination", destination.get());
        assertEquals("minecraft:the_nether", returning.get());
    }

    @Test
    void savePreservesUnknownValuesCommentsAndReleasedFormatting() throws Exception {
        AetherConfigFile file = new AetherConfigFile("aether-common.toml");
        BooleanConfigEntry portal = file.booleanEntry("Gameplay", "Gives player Aether Portal Frame item", false, "comment");
        BooleanConfigEntry startupLoot = file.booleanEntry("Gameplay", "Gives starting loot on entry", true, "comment");
        BooleanConfigEntry patreon = file.booleanEntry("Gameplay", "Show Patreon message", true, "comment");
        file.worldRestartBooleanEntry("Data Pack", "Add Temporary Freezing automatically", false, "comment");
        file.worldRestartBooleanEntry("Data Pack", "Add Ruined Portals automatically", false, "comment");
        file.booleanEntry("Modpack", "Enables Immersive Portals compatibility", true, "comment");
        Path configPath = this.temporaryDirectory.resolve(file.fileName());
        copyFixture("released_fabric_common.toml", configPath);

        file.load(this.temporaryDirectory);
        assertTrue(portal.get());
        assertFalse(startupLoot.get());
        patreon.set(false);
        patreon.save();

        String saved = Files.readString(configPath, StandardCharsets.UTF_8);
        assertTrue(saved.contains("\"Show Patreon message\" = false # keep this operator note"));
        assertTrue(saved.contains("\"Use default Accessories' menu\" = true"));
        assertTrue(saved.contains("[ThirdParty]"));
        assertTrue(saved.contains("\"Opaque integration value\" = { enabled = true, mode = \"legacy\" }"));
        try (var files = Files.list(this.temporaryDirectory)) {
            assertFalse(files.anyMatch(path -> path.getFileName().toString().endsWith(".tmp")));
        }
    }

    @Test
    void createsCanonicalConfigWithQuotedSectionAndRestartMetadata() throws Exception {
        AetherConfigFile file = new AetherConfigFile("aether-common.toml");
        BooleanConfigEntry entry = file.worldRestartBooleanEntry("Data Pack", "Add Temporary Freezing automatically", false,
                "Sets the data pack to be added to new worlds automatically");

        file.load(this.temporaryDirectory);

        String generated = Files.readString(this.temporaryDirectory.resolve(file.fileName()), StandardCharsets.UTF_8);
        assertTrue(generated.contains("[\"Data Pack\"]"));
        assertTrue(generated.contains("#Sets the data pack to be added to new worlds automatically"));
        assertTrue(generated.contains("\"Add Temporary Freezing automatically\" = false"));
        assertEquals(AetherConfigEntry.RestartRequirement.WORLD, entry.restartRequirement());
        assertEquals("[Data Pack, Add Temporary Freezing automatically]", AetherConfigFile.serializePath(entry.path()));
    }

    @Test
    void synchronizedValuesUseTypesAndCannotOverwriteLocalFiles() {
        AetherConfigFile server = new AetherConfigFile("aether-server.toml");
        BooleanConfigEntry enabled = server.booleanEntry("Gameplay", "Beds explode", false, "comment");
        AetherConfigEntry<Integer> count = server.integerEntry("Gameplay", "Maximum consumable Life Shards", 10, "comment");
        AetherConfigEntry<List<String>> dimensions = server.stringListEntry("Multiplayer", "Configure Sun Altar dimensions", List.of(), "comment");

        server.applySynchronizedValues(Map.of(
                "[Gameplay, Beds explode]", "true",
                "[Gameplay, Maximum consumable Life Shards]", "24",
                "[Multiplayer, Configure Sun Altar dimensions]", "[\"aether:the_aether\", \"thirdparty:sky\"]",
                "[Unknown, Value]", "true"));

        assertTrue(server.isLoaded());
        assertTrue(enabled.get());
        assertEquals(24, count.get());
        assertEquals(List.of("aether:the_aether", "thirdparty:sky"), dimensions.get());
        enabled.set(false);
        assertThrows(IllegalStateException.class, enabled::save);
        server.unload();
        assertFalse(server.isLoaded());
        assertFalse(enabled.get());
        assertEquals(10, count.get());
    }

    @Test
    void integratedClientSyncDoesNotReplaceWritableServerState() {
        AetherConfigFile server = new AetherConfigFile("aether-server.toml");
        BooleanConfigEntry enabled = server.booleanEntry("Gameplay", "Beds explode", false, "comment");
        server.load(this.temporaryDirectory);
        enabled.set(true);
        enabled.save();

        server.applySynchronizedValues(Map.of("[Gameplay, Beds explode]", "false"));
        server.clearSynchronizedValues();

        assertTrue(server.isLoaded());
        assertTrue(enabled.get());
        assertTrue(Files.exists(this.temporaryDirectory.resolve(server.fileName())));
    }

    @Test
    void oversizedExistingFileUsesDefaultsAndIsNotOverwritten() throws Exception {
        AetherConfigFile file = new AetherConfigFile("aether-client.toml");
        BooleanConfigEntry entry = file.booleanEntry("Rendering", "Disables Aether custom skybox", false, "comment");
        Path path = this.temporaryDirectory.resolve(file.fileName());
        Files.writeString(path, "x".repeat((int) AetherConfigFile.MAX_FILE_BYTES + 1), StandardCharsets.UTF_8);

        file.load(this.temporaryDirectory);

        assertFalse(entry.get());
        entry.set(true);
        assertThrows(IllegalStateException.class, entry::save);
        assertEquals(AetherConfigFile.MAX_FILE_BYTES + 1, Files.size(path));
    }

    private static void copyFixture(String name, Path destination) throws IOException {
        String resource = "/com/aetherteam/aether/config/" + name;
        try (InputStream stream = AetherConfigFileTest.class.getResourceAsStream(resource)) {
            if (stream == null) {
                throw new IOException("Missing test fixture " + resource);
            }
            Files.copy(stream, destination);
        }
    }
}
