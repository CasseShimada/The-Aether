package com.aetherteam.aether.integration.compat;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.SemanticVersion;
import net.fabricmc.loader.api.metadata.version.VersionPredicate;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ThreeModApiCompatibilityTest {
    private static final Path DATA = Path.of("src/generated/resources/data");
    private static final Set<String> DUNGEON_BLOCK_TAGS = Set.of(
            "#aether:trapped_dungeon_blocks",
            "#aether:locked_dungeon_blocks",
            "#aether:boss_doorway_dungeon_blocks",
            "#aether:treasure_doorway_dungeon_blocks");
    private static final Set<String> HARD_RELOCATION_DENY = Set.of(
            "aether:aether_portal",
            "aether:chest_mimic",
            "aether:treasure_chest",
            "aether:skyroot_bed",
            "aether:sun_altar",
            "aether:skyroot_door",
            "aether:frosted_ice",
            "aether:unstable_obsidian");

    @Test
    void diggusUsesApiOneTagPathsAndKeepsOreFamiliesSeparate() throws IOException {
        Set<String> included = tagValues("diggusmaximus/tags/block/included_blocks.json");
        assertEquals(Set.of(
                "#aether:ores/ambrosium",
                "#aether:ores/zanite",
                "#aether:ores/gravitite"), included);

        assertEquals(Set.of("#aether:ores/ambrosium"),
                tagValues("diggusmaximus/tags/block/groups/aether_ambrosium_ores.json"));
        assertEquals(Set.of("#aether:ores/zanite"),
                tagValues("diggusmaximus/tags/block/groups/aether_zanite_ores.json"));
        assertEquals(Set.of("#aether:ores/gravitite"),
                tagValues("diggusmaximus/tags/block/groups/aether_gravitite_ores.json"));

        assertFalse(Files.exists(DATA.resolve("diggusmaximus/tags/blocks")));
        assertFalse(Files.exists(DATA.resolve("diggusmaximus/tags/items")));
    }

    @Test
    void diggusToolAndBlockSafetyRulesAreDisjointAndFailClosed() throws IOException {
        Set<String> includedTools = tagValues("diggusmaximus/tags/item/included_tools.json");
        Set<String> excludedTools = tagValues("diggusmaximus/tags/item/excluded_tools.json");
        Set<String> excludedBlocks = tagValues("diggusmaximus/tags/block/excluded_blocks.json");

        assertEquals(20, includedTools.size());
        assertTrue(includedTools.containsAll(Set.of(
                "aether:skyroot_pickaxe",
                "aether:holystone_axe",
                "aether:zanite_shovel",
                "aether:gravitite_hoe",
                "aether:valkyrie_pickaxe")));
        assertTrue(excludedTools.containsAll(Set.of(
                "#aether:accessories",
                "#aether:tools/hammers",
                "#aether:tools/lances",
                "aether:skyroot_sword",
                "aether:phoenix_bow",
                "aether:zanite_helmet",
                "aether:cold_parachute",
                "aether:nature_staff")));
        assertTrue(disjoint(includedTools, excludedTools));
        assertTrue(excludedBlocks.containsAll(DUNGEON_BLOCK_TAGS));
        assertTrue(excludedBlocks.containsAll(HARD_RELOCATION_DENY));
        assertTrue(excludedBlocks.containsAll(Set.of(
                "aether:altar", "aether:freezer", "aether:incubator",
                "aether:icestone", "aether:purple_flower")));
    }

    @Test
    void carryOnUsesApiOneTagsAndPreservesOnlySafeBlockEntities() throws IOException {
        Set<String> hardDeny = tagValues("c/tags/block/relocation_not_supported.json");
        Set<String> blacklist = tagValues("carryon/tags/block/block_blacklist.json");
        Set<String> whitelist = tagValues("carryon/tags/block/block_whitelist.json");

        assertEquals(blacklist, hardDeny);
        assertTrue(hardDeny.containsAll(DUNGEON_BLOCK_TAGS));
        assertTrue(hardDeny.containsAll(HARD_RELOCATION_DENY));
        assertTrue(whitelist.containsAll(Set.of(
                "aether:altar", "aether:freezer", "aether:incubator",
                "aether:purple_flower", "aether:white_flower",
                "aether:icestone", "aether:skyroot_sign")));
        assertTrue(disjoint(blacklist, whitelist));
        assertFalse(Files.exists(DATA.resolve("carryon/tags/blocks")));
        assertFalse(Files.exists(DATA.resolve("carryon/tags/entities")));
    }

    @Test
    void carryOnAllowsPassiveFarmMobsAndDeniesDungeonOrTransientEntities() throws IOException {
        Set<String> entityWhitelist = tagValues("carryon/tags/entity_type/entity_whitelist.json");
        Set<String> stackingWhitelist = tagValues("carryon/tags/entity_type/stacking_whitelist.json");
        Set<String> entityBlacklist = tagValues("carryon/tags/entity_type/entity_blacklist.json");
        Set<String> stackingBlacklist = tagValues("carryon/tags/entity_type/stacking_blacklist.json");

        Set<String> safeFarmMobs = Set.of("aether:phyg", "aether:flying_cow", "aether:sheepuff");
        assertEquals(safeFarmMobs, entityWhitelist);
        assertEquals(safeFarmMobs, stackingWhitelist);
        assertEquals(entityBlacklist, stackingBlacklist);
        assertTrue(entityBlacklist.containsAll(Set.of(
                "#aether:dungeon_entities", "aether:mimic", "aether:zephyr",
                "aether:floating_block", "aether:cloud_crystal", "aether:fire_crystal",
                "aether:ice_crystal", "aether:thunder_crystal",
                "aether:golden_dart", "aether:hammer_projectile")));
        assertTrue(disjoint(entityWhitelist, entityBlacklist));
    }

    @Test
    void optionalDependencyRangesAcceptTheTargetApiVersions() throws Exception {
        JsonObject suggests = readJson(Path.of("src/main/resources/fabric.mod.json"))
                .getAsJsonObject("suggests");

        assertVersionMatches(suggests, "diggusmaximus", "1.5.9-beta.1+26.2");
        assertVersionMatches(suggests, "carryon", "2.9.1");
        assertVersionMatches(suggests, "wthit", "20.0.0");
        assertFalse(suggests.get("wthit").getAsString().contains("21.0.0 <"));
    }

    @Test
    void wthitUsesTheModernDescriptorAndKeepsClientCodeOffTheCommonPath() throws IOException {
        JsonObject fabricEntrypoints = readJson(Path.of("src/main/resources/fabric.mod.json"))
                .getAsJsonObject("entrypoints");
        assertEquals("com.aetherteam.aether.integration.jade.AetherJadePlugin",
                fabricEntrypoints.getAsJsonArray("jade").get(0).getAsString());

        JsonObject plugin = readJson(Path.of("src/main/resources/waila_plugins.json"))
                .getAsJsonObject("aether:viewer_privacy");
        readJson(Path.of("src/gametest/resources/fabric.mod.json"));
        JsonObject entrypoints = plugin.getAsJsonObject("entrypoints");

        assertEquals("com.aetherteam.aether.integration.wthit.AetherWthitCommonPlugin",
                entrypoints.get("common").getAsString());
        assertEquals("com.aetherteam.aether.integration.wthit.AetherWthitClientPlugin",
                entrypoints.get("client").getAsString());
        assertEquals("*", plugin.get("side").getAsString());
        assertEquals(">=20.0.0 <21.0.0",
                plugin.getAsJsonObject("required").get("wthit").getAsString());
        assertTrue(plugin.get("defaultEnabled").getAsBoolean());
        assertFalse(plugin.has("initializer"));

        List<Path> adapters = List.of(
                Path.of("src/main/java/com/aetherteam/aether/integration/wthit/AetherWthitCommonPlugin.java"),
                Path.of("src/main/java/com/aetherteam/aether/integration/wthit/AetherWthitClientPlugin.java"));
        for (Path adapter : adapters) {
            for (String line : Files.readAllLines(adapter)) {
                if (line.startsWith("import mcp.mobius.waila.")) {
                    assertTrue(line.startsWith("import mcp.mobius.waila.api."),
                            () -> "WTHIT implementation import in " + adapter + ": " + line);
                }
            }
        }

        String commonBytecode = classBytes("integration/wthit/AetherWthitCommonPlugin.class");
        String fabricEntrypoint = classBytes("fabric/AetherFabric.class");
        assertFalse(commonBytecode.contains("net/minecraft/client"));
        assertFalse(commonBytecode.contains("AetherWthitClientPlugin"));
        assertFalse(fabricEntrypoint.contains("mcp/mobius/waila"));
    }

    private static void assertVersionMatches(JsonObject suggests, String modId, String version) throws Exception {
        String range = suggests.get(modId).getAsString();
        assertTrue(VersionPredicate.parse(range).test(SemanticVersion.parse(version)),
                () -> version + " must match " + modId + " range " + range);
    }

    private static boolean disjoint(Set<String> left, Set<String> right) {
        Set<String> overlap = new LinkedHashSet<>(left);
        overlap.retainAll(right);
        return overlap.isEmpty();
    }

    private static Set<String> tagValues(String relativePath) throws IOException {
        JsonObject tag = readJson(DATA.resolve(relativePath));
        assertTrue(tag.has("replace"), relativePath + " must explicitly merge");
        assertFalse(tag.get("replace").getAsBoolean(), relativePath + " must use replace=false");
        Set<String> values = new LinkedHashSet<>();
        for (JsonElement value : tag.getAsJsonArray("values")) {
            values.add(value.getAsString());
        }
        return values;
    }

    private static JsonObject readJson(Path path) throws IOException {
        return JsonParser.parseString(Files.readString(path)).getAsJsonObject();
    }

    private static String classBytes(String relativePath) throws IOException {
        return new String(Files.readAllBytes(Path.of("build/classes/java/main/com/aetherteam/aether")
                .resolve(relativePath)), StandardCharsets.ISO_8859_1);
    }
}
