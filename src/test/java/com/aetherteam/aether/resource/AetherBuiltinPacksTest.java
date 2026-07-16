package com.aetherteam.aether.resource;

import net.minecraft.server.packs.PackType;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AetherBuiltinPacksTest {
    @Test
    void releasedCatalogKeepsStableIdsAndPackDirectories() {
        List<AetherBuiltinPacks.BuiltinPackDefinition> definitions = AetherBuiltinPacks.definitions();
        assertEquals(9, definitions.size());
        assertEquals(List.of(
                        "builtin/aether_125_art",
                        "builtin/aether_b173_art",
                        "builtin/aether_ctm_fix",
                        "builtin/aether_tips",
                        "builtin/aether_colorblind",
                        "builtin/aether_tooltips",
                        "builtin/aether_imm_ptl_compat",
                        "builtin/aether_temporary_freezing",
                        "builtin/aether_ruined_portal"),
                definitions.stream().map(AetherBuiltinPacks.BuiltinPackDefinition::id).toList());

        assertEquals(List.of("classic_125", "classic_base"), definitions.get(0).directories());
        assertEquals(List.of("classic_b173", "classic_base"), definitions.get(1).directories());
        for (AetherBuiltinPacks.BuiltinPackDefinition definition : definitions) {
            for (String directory : definition.directories()) {
                assertTrue(Files.isDirectory(Path.of("src/main/resources/packs", directory)), directory);
            }
        }
    }

    @Test
    void optionalModConditionsMatchReleasedBehavior() {
        AetherBuiltinPacks.PackContext noMods = context(Set.of(), true, true, false, false);
        assertEquals(Set.of(
                        "builtin/aether_125_art",
                        "builtin/aether_b173_art",
                        "builtin/aether_colorblind",
                        "builtin/aether_tooltips"),
                ids(AetherBuiltinPacks.availableDefinitions(PackType.CLIENT_RESOURCES, noMods)));
        assertEquals(Set.of("builtin/aether_temporary_freezing", "builtin/aether_ruined_portal"),
                ids(AetherBuiltinPacks.availableDefinitions(PackType.SERVER_DATA, noMods)));

        AetherBuiltinPacks.PackContext allMods = context(
                Set.of("ctm", "tipsmod", "immersive_portals_core"), true, true, false, false);
        assertEquals(6, AetherBuiltinPacks.availableDefinitions(PackType.CLIENT_RESOURCES, allMods).size());
        assertEquals(3, AetherBuiltinPacks.availableDefinitions(PackType.SERVER_DATA, allMods).size());

        AetherBuiltinPacks.PackContext disabledConfigs = context(
                Set.of("tipsmod", "immersive_portals_core"), false, false, false, false);
        assertFalse(ids(AetherBuiltinPacks.availableDefinitions(PackType.CLIENT_RESOURCES, disabledConfigs))
                .contains("builtin/aether_tips"));
        assertFalse(ids(AetherBuiltinPacks.availableDefinitions(PackType.SERVER_DATA, disabledConfigs))
                .contains("builtin/aether_imm_ptl_compat"));
    }

    @Test
    void requiredAndConfigDrivenActivationRemainDistinct() {
        AetherBuiltinPacks.PackContext defaultsOff = context(Set.of(), true, true, false, false);
        AetherBuiltinPacks.PackContext defaultsOn = context(Set.of(), true, true, true, true);

        AetherBuiltinPacks.BuiltinPackDefinition ctm = definition("builtin/aether_ctm_fix");
        AetherBuiltinPacks.BuiltinPackDefinition immersive = definition("builtin/aether_imm_ptl_compat");
        AetherBuiltinPacks.BuiltinPackDefinition freezing = definition("builtin/aether_temporary_freezing");
        AetherBuiltinPacks.BuiltinPackDefinition ruined = definition("builtin/aether_ruined_portal");

        assertTrue(ctm.isRequired());
        assertTrue(immersive.isRequired());
        assertFalse(freezing.isRequired());
        assertFalse(ruined.isRequired());
        assertFalse(freezing.shouldAddAutomatically(defaultsOff));
        assertFalse(ruined.shouldAddAutomatically(defaultsOff));
        assertTrue(freezing.shouldAddAutomatically(defaultsOn));
        assertTrue(ruined.shouldAddAutomatically(defaultsOn));
    }

    private static AetherBuiltinPacks.PackContext context(Set<String> mods, boolean trivia,
                                                           boolean immersivePortals, boolean freezing,
                                                           boolean ruinedPortals) {
        return new AetherBuiltinPacks.PackContext(mods::contains, trivia, immersivePortals, freezing, ruinedPortals);
    }

    private static Set<String> ids(List<AetherBuiltinPacks.BuiltinPackDefinition> definitions) {
        return definitions.stream().map(AetherBuiltinPacks.BuiltinPackDefinition::id).collect(java.util.stream.Collectors.toSet());
    }

    private static AetherBuiltinPacks.BuiltinPackDefinition definition(String id) {
        return AetherBuiltinPacks.definitions().stream()
                .filter(definition -> definition.id().equals(id))
                .findFirst()
                .orElseThrow();
    }
}
