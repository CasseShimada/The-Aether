package com.aetherteam.aether.data.migration;

import com.aetherteam.aether.attachment.LegacyDataArchiveAttachment;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.TagParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LegacyPlayerDataMigrationTest {
    @Test
    void archivesLegacyRootsByCopy() {
        CompoundTag forgeCaps = new CompoundTag();
        forgeCaps.putString("thirdparty:data", "keep-me");
        CompoundTag player = new CompoundTag();
        player.put(LegacyPlayerDataMigration.FORGE_CAPS, forgeCaps);

        Map<String, CompoundTag> roots = LegacyPlayerDataMigration.legacyRoots(player);
        forgeCaps.putString("thirdparty:data", "changed");

        assertEquals("keep-me", roots.get(LegacyPlayerDataMigration.FORGE_CAPS).getStringOr("thirdparty:data", ""));
    }

    @Test
    void archiveCodecKeepsUnknownDataAndMigrationVersion() {
        LegacyDataArchiveAttachment archive = new LegacyDataArchiveAttachment();
        CompoundTag unknown = new CompoundTag();
        unknown.putInt("value", 42);
        archive.archive(LegacyPlayerDataMigration.NEOFORGE_ATTACHMENTS, unknown);
        archive.setMigrationVersion(LegacyPlayerDataMigration.MIGRATION_VERSION);

        var encoded = LegacyDataArchiveAttachment.CODEC.encodeStart(NbtOps.INSTANCE, archive).getOrThrow();
        LegacyDataArchiveAttachment decoded = LegacyDataArchiveAttachment.CODEC.parse(NbtOps.INSTANCE, encoded).getOrThrow();

        assertEquals(LegacyPlayerDataMigration.MIGRATION_VERSION, decoded.migrationVersion());
        assertEquals(42, decoded.roots().get(LegacyPlayerDataMigration.NEOFORGE_ATTACHMENTS).getIntOr("value", 0));
    }

    @Test
    void mapsReleasedAetherAndGenericCuriosSlots() {
        Map<String, CompoundTag> roots = Map.of(
                LegacyPlayerDataMigration.FORGE_CAPS,
                curiosRoot("aether_ring", "ring", "body")
        );

        List<LegacyPlayerDataMigration.LegacySlotData> slots = LegacyPlayerDataMigration.readCuriosSlots(roots);

        assertEquals(2, slots.size());
        assertTrue(slots.stream().allMatch(slot -> slot.target().slotName().equals("aether:ring_slot")));
        assertFalse(slots.stream().anyMatch(slot -> slot.identifier().equals("body")));
    }

    @Test
    void skipsCuriosPayloadAlreadyEncodedByAccessories() {
        CompoundTag root = curiosRoot("aether_ring");
        root.getCompoundOrEmpty("curios:inventory").putBoolean("AccessoriesEncoded", true);

        List<LegacyPlayerDataMigration.LegacySlotData> slots = LegacyPlayerDataMigration.readCuriosSlots(Map.of(
                LegacyPlayerDataMigration.NEOFORGE_ATTACHMENTS, root));

        assertTrue(slots.isEmpty());
    }

    @Test
    void readsReleasedNeoForgeAccessoriesHolderFixture() throws Exception {
        CompoundTag player = readFixture("released_neoforge_accessories_player.snbt");
        Map<String, CompoundTag> roots = LegacyPlayerDataMigration.legacyRoots(player);

        List<LegacyPlayerDataMigration.LegacyAccessoriesSlotData> slots = LegacyPlayerDataMigration.readAccessoriesSlots(roots);
        LegacyPlayerDataMigration.LegacyAccessoriesSlotData ring = slots.stream()
                .filter(slot -> slot.slotName().equals("aether:ring_slot"))
                .findFirst()
                .orElseThrow();
        LegacyPlayerDataMigration.LegacyAccessoriesSlotData unknown = slots.stream()
                .filter(slot -> slot.slotName().equals("thirdparty:unknown_slot"))
                .findFirst()
                .orElseThrow();

        assertEquals(LegacyPlayerDataMigration.NEOFORGE_ATTACHMENTS, ring.sourceRoot());
        assertEquals(2, ring.size());
        assertEquals(2, ring.equipped().getCompoundOrEmpty(0).getIntOr("count", 0));
        assertEquals(1, ring.cosmetic().getCompoundOrEmpty(0).getIntOr("count", 0));
        assertEquals(Set.of(1), ring.hiddenRenderSlots());
        assertEquals(3, unknown.size());
        assertEquals(4, unknown.equipped().getCompoundOrEmpty(0).getIntOr("count", 0));
        assertEquals("keep-me", roots.get(LegacyPlayerDataMigration.NEOFORGE_ATTACHMENTS)
                .getCompoundOrEmpty("thirdparty:attachment").getStringOr("opaque", ""));
    }

    @Test
    void recognizesReleasedFabricAccessoriesHolderAsLegacyData() {
        CompoundTag fabricAttachments = new CompoundTag();
        fabricAttachments.put("accessories:inventory_holder", new CompoundTag());
        CompoundTag player = new CompoundTag();
        player.put(LegacyPlayerDataMigration.FABRIC_ATTACHMENTS, fabricAttachments);

        Map<String, CompoundTag> roots = LegacyPlayerDataMigration.legacyRoots(player);

        assertTrue(roots.containsKey(LegacyPlayerDataMigration.FABRIC_ATTACHMENTS));
    }

    @Test
    void currentPlayerAndAccessoryAttachmentsHaveIndependentPrecedence() {
        CompoundTag fabricAttachments = new CompoundTag();
        fabricAttachments.put("aether:aether_player", new CompoundTag());
        CompoundTag player = new CompoundTag();
        player.put(LegacyPlayerDataMigration.FABRIC_ATTACHMENTS, fabricAttachments);

        assertTrue(LegacyPlayerDataMigration.hasCurrentAttachment(player, "aether:aether_player"));
        assertFalse(LegacyPlayerDataMigration.hasCurrentAttachment(player, "aether:accessory_inventory"));
    }

    @Test
    void readsReleasedForgeCapabilityPlayerFields() {
        CompoundTag state = new CompoundTag();
        state.putBoolean("CanGetPortal", false);
        state.putBoolean("CanSpawnInAether", false);
        state.putFloat("SavedHealth", 17.5F);
        state.putInt("LifeShardCount", 6);
        state.putBoolean("HasSeenSunSpirit", true);
        state.putInt("RemedyStartDuration", 120);
        CompoundTag forgeCaps = new CompoundTag();
        forgeCaps.put("aether:aether_player", state);

        var migrated = LegacyPlayerDataMigration.findLegacyPlayerState(Map.of(
                LegacyPlayerDataMigration.FORGE_CAPS, forgeCaps)).orElseThrow();

        assertFalse(migrated.canGetPortal());
        assertFalse(migrated.canSpawnInAether());
        assertEquals(17.5F, migrated.getSavedHealth());
        assertEquals(6, migrated.getLifeShardCount());
        assertTrue(migrated.hasSeenSunSpiritDialogue());
        assertEquals(120, migrated.getRemedyStartDuration());
    }

    @Test
    void dataFixesLegacyItemStackInsideSyntheticPlayerInventory() {
        SharedConstants.tryDetectVersion();
        CompoundTag stack = new CompoundTag();
        stack.putString("id", "minecraft:stone");
        stack.putByte("Count", (byte) 2);
        CompoundTag legacyTag = new CompoundTag();
        legacyTag.putInt("Damage", 3);
        stack.put("tag", legacyTag);

        CompoundTag updated = LegacyPlayerDataMigration.updateItemStackTag(stack, 3465).orElseThrow();

        assertEquals("minecraft:stone", updated.getStringOr("id", ""));
        assertEquals(2, updated.getIntOr("count", -1));
        assertEquals(3, updated.getCompoundOrEmpty("components").getIntOr("minecraft:damage", -1));
    }

    private static CompoundTag curiosRoot(String... identifiers) {
        ListTag slots = new ListTag();
        for (String identifier : identifiers) {
            CompoundTag slot = new CompoundTag();
            slot.putString("Identifier", identifier);
            slot.put("StacksHandler", new CompoundTag());
            slots.add(slot);
        }
        CompoundTag inventory = new CompoundTag();
        inventory.put("Curios", slots);
        CompoundTag root = new CompoundTag();
        root.put("curios:inventory", inventory);
        return root;
    }

    private static CompoundTag readFixture(String name) throws Exception {
        String path = "/com/aetherteam/aether/data/migration/" + name;
        try (InputStream stream = LegacyPlayerDataMigrationTest.class.getResourceAsStream(path)) {
            if (stream == null) {
                throw new IOException("Missing test fixture " + path);
            }
            return TagParser.parseCompoundFully(new String(stream.readAllBytes(), StandardCharsets.UTF_8));
        }
    }
}
