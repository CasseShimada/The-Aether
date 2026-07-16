package com.aetherteam.aether.data.migration;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.accessories.api.AccessoriesAPI;
import com.aetherteam.aether.attachment.AccessoryInventoryAttachment;
import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.attachment.AetherPlayerAttachment;
import com.aetherteam.aether.attachment.LegacyDataArchiveAttachment;
import com.mojang.serialization.Dynamic;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.NameAndId;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Imports released Forge/NeoForge player formats without depending on either loader at runtime.
 */
public final class LegacyPlayerDataMigration {
    static final int MIGRATION_VERSION = 1;
    static final String FORGE_CAPS = "ForgeCaps";
    static final String NEOFORGE_ATTACHMENTS = "neoforge:attachments";
    static final String FABRIC_ATTACHMENTS = "fabric:attachments";
    private static final String CURIOS_INVENTORY = "curios:inventory";
    private static final String ACCESSORIES_INVENTORY_HOLDER = "accessories:inventory_holder";
    private static final String CURRENT_PLAYER_ATTACHMENT = "aether:aether_player";
    private static final String CURRENT_ACCESSORY_ATTACHMENT = "aether:accessory_inventory";

    private static final Map<String, SlotTarget> LEGACY_SLOT_TARGETS = Map.ofEntries(
            Map.entry("aether_accessory", new SlotTarget("aether:accessory_slot", 2)),
            Map.entry("aether_cape", new SlotTarget("aether:cape_slot", 1)),
            Map.entry("aether_gloves", new SlotTarget("aether:gloves_slot", 1)),
            Map.entry("aether_pendant", new SlotTarget("aether:pendant_slot", 1)),
            Map.entry("aether_ring", new SlotTarget("aether:ring_slot", 2)),
            Map.entry("aether_shield", new SlotTarget("aether:shield_slot", 1)),
            Map.entry("back", new SlotTarget("aether:back_slot", 1)),
            Map.entry("cape", new SlotTarget("aether:cape_slot", 1)),
            Map.entry("charm", new SlotTarget("aether:accessory_slot", 2)),
            Map.entry("hand", new SlotTarget("aether:gloves_slot", 1)),
            Map.entry("hands", new SlotTarget("aether:gloves_slot", 1)),
            Map.entry("head", new SlotTarget("aether:accessory_slot", 2)),
            Map.entry("necklace", new SlotTarget("aether:pendant_slot", 1)),
            Map.entry("ring", new SlotTarget("aether:ring_slot", 2)),
            Map.entry("shield", new SlotTarget("aether:shield_slot", 1))
    );

    private LegacyPlayerDataMigration() {
    }

    public static void migrate(ServerPlayer player) {
        CompoundTag playerTag = player.level().getServer().getPlayerList()
                .loadPlayerData(new NameAndId(player.getGameProfile()))
                .orElse(null);
        if (playerTag == null) {
            return;
        }

        Map<String, CompoundTag> legacyRoots = legacyRoots(playerTag);
        if (legacyRoots.isEmpty()) {
            return;
        }

        LegacyDataArchiveAttachment archive = player.getAttachedOrCreate(AetherDataAttachments.LEGACY_DATA_ARCHIVE);
        legacyRoots.forEach(archive::archive);
        if (archive.migrationVersion() >= MIGRATION_VERSION) {
            return;
        }

        boolean hasCurrentPlayerData = hasCurrentAttachment(playerTag, CURRENT_PLAYER_ATTACHMENT);
        boolean hasCurrentAccessoryData = hasCurrentAttachment(playerTag, CURRENT_ACCESSORY_ATTACHMENT);
        int migratedAccessories = 0;
        if (!hasCurrentPlayerData) {
            findLegacyPlayerState(legacyRoots).ifPresent(state ->
                    player.getAttachedOrCreate(AetherDataAttachments.AETHER_PLAYER).copyPersistentStateFrom(state));
        }
        if (!hasCurrentAccessoryData) {
            List<LegacyAccessoriesSlotData> accessoriesSlots = readAccessoriesSlots(legacyRoots);
            migratedAccessories = accessoriesSlots.isEmpty()
                    ? migrateCuriosInventory(player, playerTag, legacyRoots)
                    : migrateAccessoriesInventory(player, playerTag, accessoriesSlots);
        }

        archive.setMigrationVersion(MIGRATION_VERSION);
        List<String> currentData = new ArrayList<>();
        if (hasCurrentPlayerData) {
            currentData.add("player state");
        }
        if (hasCurrentAccessoryData) {
            currentData.add("accessory inventory");
        }
        Aether.LOGGER.info("Archived legacy player data for {} and migrated {} accessory stack(s){}",
                player.getGameProfile().name(), migratedAccessories,
                currentData.isEmpty() ? "" : "; current Fabric " + String.join(" and ", currentData) + " took precedence");
    }

    static Map<String, CompoundTag> legacyRoots(CompoundTag playerTag) {
        Map<String, CompoundTag> roots = new LinkedHashMap<>();
        playerTag.getCompound(FORGE_CAPS).ifPresent(tag -> roots.put(FORGE_CAPS, tag.copy()));
        playerTag.getCompound(NEOFORGE_ATTACHMENTS).ifPresent(tag -> roots.put(NEOFORGE_ATTACHMENTS, tag.copy()));
        playerTag.getCompound(FABRIC_ATTACHMENTS)
                .filter(tag -> tag.contains(ACCESSORIES_INVENTORY_HOLDER))
                .ifPresent(tag -> roots.put(FABRIC_ATTACHMENTS, tag.copy()));
        return roots;
    }

    static List<LegacySlotData> readCuriosSlots(Map<String, CompoundTag> roots) {
        List<LegacySlotData> slots = new ArrayList<>();
        for (Map.Entry<String, CompoundTag> root : roots.entrySet()) {
            CompoundTag curios = root.getValue().getCompound(CURIOS_INVENTORY).orElse(null);
            if (curios == null || curios.getBooleanOr("AccessoriesEncoded", false)) {
                continue;
            }
            ListTag curiosSlots = curios.getList("Curios").orElse(null);
            if (curiosSlots == null) {
                continue;
            }
            for (Tag entry : curiosSlots) {
                if (!(entry instanceof CompoundTag slotTag)) {
                    continue;
                }
                String identifier = slotTag.getStringOr("Identifier", "");
                SlotTarget target = LEGACY_SLOT_TARGETS.get(identifier);
                CompoundTag handler = slotTag.getCompound("StacksHandler").orElse(null);
                if (target == null || handler == null) {
                    continue;
                }
                slots.add(new LegacySlotData(root.getKey(), identifier, target, handler.copy()));
            }
        }
        return slots;
    }

    static List<LegacyAccessoriesSlotData> readAccessoriesSlots(Map<String, CompoundTag> roots) {
        List<LegacyAccessoriesSlotData> slots = new ArrayList<>();
        for (Map.Entry<String, CompoundTag> root : roots.entrySet()) {
            CompoundTag holder = root.getValue().getCompound(ACCESSORIES_INVENTORY_HOLDER).orElse(null);
            CompoundTag containers = holder == null ? null : getCompound(holder, "accessories_containers", "AccessoriesContainers");
            if (containers == null) {
                continue;
            }
            for (String containerKey : containers.keySet()) {
                CompoundTag container = containers.getCompound(containerKey).orElse(null);
                if (container == null) {
                    continue;
                }
                String slotName = getString(container, "slot_name", "SlotName");
                if (slotName.isBlank() || slotName.equals("UNKNOWN")) {
                    slotName = containerKey;
                }

                ListTag equipped = getList(container, "items", "Items");
                ListTag cosmetic = getList(container, "cosmetics", "Cosmetics");
                int size = Math.max(1, Math.max(
                        getInt(container, "current_size", "CurrentSize"),
                        getInt(container, "base_size", "BaseSize")));
                size = Math.max(size, Math.max(requiredSize(equipped), requiredSize(cosmetic)));
                size = Math.max(size, renderOptionCount(container));

                Set<Integer> hidden = new HashSet<>();
                readHiddenRenderSlots(container, size, hidden, "render_options", "RenderOptions");
                slots.add(new LegacyAccessoriesSlotData(
                        root.getKey(), slotName, size, equipped.copy(), cosmetic.copy(), Set.copyOf(hidden)));
            }
        }
        return slots;
    }

    static Optional<CompoundTag> updateItemStackTag(CompoundTag itemTag, int dataVersion) {
        if (dataVersion <= 0) {
            return Optional.empty();
        }
        CompoundTag syntheticPlayer = new CompoundTag();
        ListTag inventory = new ListTag();
        CompoundTag stack = itemTag.copy();
        stack.putByte("Slot", (byte) 0);
        inventory.add(stack);
        syntheticPlayer.put("Inventory", inventory);

        CompoundTag updated = DataFixTypes.PLAYER.updateToCurrentVersion(DataFixers.getDataFixer(), syntheticPlayer, dataVersion);
        ListTag updatedInventory = updated.getList("Inventory").orElse(null);
        if (updatedInventory == null || updatedInventory.isEmpty()) {
            return Optional.empty();
        }
        return updatedInventory.getCompound(0).map(CompoundTag::copy);
    }

    static boolean hasCurrentAttachment(CompoundTag playerTag, String attachmentId) {
        CompoundTag attachments = playerTag.getCompound(FABRIC_ATTACHMENTS).orElse(null);
        return attachments != null && attachments.contains(attachmentId);
    }

    static Optional<AetherPlayerAttachment> findLegacyPlayerState(Map<String, CompoundTag> roots) {
        CompoundTag neoForge = roots.get(NEOFORGE_ATTACHMENTS);
        if (neoForge != null) {
            CompoundTag state = neoForge.getCompound(CURRENT_PLAYER_ATTACHMENT).orElse(null);
            if (state != null) {
                Optional<AetherPlayerAttachment> decoded = AetherPlayerAttachment.CODEC.parse(NbtOps.INSTANCE, state)
                        .resultOrPartial(error -> Aether.LOGGER.warn("Unable to decode legacy NeoForge Aether player attachment: {}", error));
                if (decoded.isPresent()) {
                    return decoded;
                }
            }
        }

        CompoundTag forge = roots.get(FORGE_CAPS);
        if (forge == null) {
            return Optional.empty();
        }
        CompoundTag state = forge.getCompound(CURRENT_PLAYER_ATTACHMENT).orElse(null);
        if (state == null) {
            return Optional.empty();
        }
        Optional<CompoundTag> mountedAerbunny = state.getCompound("MountedAerbunnyTag").map(CompoundTag::copy);
        Optional<java.util.UUID> lastRiddenMoa = Optional.ofNullable(state.get("LastRiddenMoa"))
                .flatMap(tag -> UUIDUtil.CODEC.parse(NbtOps.INSTANCE, tag).result());
        return Optional.of(new AetherPlayerAttachment(
                state.getBooleanOr("CanGetPortal", true),
                state.getBooleanOr("CanSpawnInAether", true),
                state.getFloatOr("SavedHealth", 0.0F),
                state.getIntOr("LifeShardCount", 0),
                state.getBooleanOr("HasSeenSunSpirit", false),
                state.getIntOr("RemedyStartDuration", 0),
                mountedAerbunny,
                lastRiddenMoa,
                state.getBooleanOr("CanShowPatreonMessage", true),
                state.getIntOr("LoginsUntilPatreonMessage", -1)
        ));
    }

    private static int migrateCuriosInventory(ServerPlayer player, CompoundTag playerTag, Map<String, CompoundTag> roots) {
        List<LegacySlotData> legacySlots = readCuriosSlots(roots);
        if (legacySlots.isEmpty()) {
            return 0;
        }

        int dataVersion = playerTag.getIntOr("DataVersion", 0);
        AccessoryInventoryAttachment inventory = player.getAttachedOrCreate(AetherDataAttachments.ACCESSORY_INVENTORY);
        int migrated = 0;
        for (LegacySlotData legacySlot : legacySlots) {
            AccessoryInventoryAttachment.SlotStorage existing = inventory.getOrCreateSlot(legacySlot.target().slotName(), legacySlot.target().size());
            Map<Integer, ItemStack> equipped = new HashMap<>(existing.equipped());
            Map<Integer, ItemStack> cosmetic = new HashMap<>(existing.cosmetic());
            Set<Integer> hidden = new HashSet<>(existing.hiddenRenderSlots());

            migrated += migrateStackHandler(player, dataVersion, legacySlot, "Stacks", equipped);
            migrated += migrateStackHandler(player, dataVersion, legacySlot, "CosmeticStacks", cosmetic);
            readHiddenRenderSlots(legacySlot.handler(), legacySlot.target().size(), hidden);

            inventory.putSlot(legacySlot.target().slotName(), new AccessoryInventoryAttachment.SlotStorage(
                    legacySlot.target().size(), equipped, cosmetic, hidden));
        }
        AccessoriesAPI.evictAccessories(player);
        return migrated;
    }

    private static int migrateAccessoriesInventory(ServerPlayer player, CompoundTag playerTag, List<LegacyAccessoriesSlotData> legacySlots) {
        int dataVersion = playerTag.getIntOr("DataVersion", 0);
        AccessoryInventoryAttachment inventory = player.getAttachedOrCreate(AetherDataAttachments.ACCESSORY_INVENTORY);
        int migrated = 0;
        for (LegacyAccessoriesSlotData legacySlot : legacySlots) {
            AccessoryInventoryAttachment.SlotStorage existing = inventory.getOrCreateSlot(legacySlot.slotName(), legacySlot.size());
            int targetSize = Math.max(existing.size(), legacySlot.size());
            Map<Integer, ItemStack> equipped = new HashMap<>(existing.equipped());
            Map<Integer, ItemStack> cosmetic = new HashMap<>(existing.cosmetic());
            Set<Integer> hidden = new HashSet<>(existing.hiddenRenderSlots());
            hidden.addAll(legacySlot.hiddenRenderSlots());

            migrated += migrateItemList(player, dataVersion, legacySlot.sourceRoot(), legacySlot.slotName(),
                    legacySlot.equipped(), targetSize, equipped);
            migrated += migrateItemList(player, dataVersion, legacySlot.sourceRoot(), legacySlot.slotName(),
                    legacySlot.cosmetic(), targetSize, cosmetic);

            inventory.putSlot(legacySlot.slotName(), new AccessoryInventoryAttachment.SlotStorage(
                    targetSize, equipped, cosmetic, hidden));
        }
        AccessoriesAPI.evictAccessories(player);
        return migrated;
    }

    private static int migrateStackHandler(ServerPlayer player, int dataVersion, LegacySlotData legacySlot, String key, Map<Integer, ItemStack> destination) {
        CompoundTag stackHandler = legacySlot.handler().getCompound(key).orElse(null);
        ListTag items = stackHandler == null ? null : stackHandler.getList("Items").orElse(null);
        if (items == null) {
            return 0;
        }

        return migrateItemList(player, dataVersion, legacySlot.sourceRoot(), legacySlot.identifier(),
                items, legacySlot.target().size(), destination);
    }

    private static int migrateItemList(ServerPlayer player, int dataVersion, String sourceRoot, String slotName,
                                       ListTag items, int targetSize, Map<Integer, ItemStack> destination) {
        int migrated = 0;
        for (Tag raw : items) {
            if (!(raw instanceof CompoundTag itemTag)) {
                continue;
            }
            int slot = readSlotIndex(itemTag);
            if (slot < 0 || slot >= targetSize || destination.containsKey(slot)) {
                Aether.LOGGER.warn("Retained but did not import legacy accessory item from {} slot {} index {} due to an unsupported or occupied target",
                        sourceRoot, slotName, slot);
                continue;
            }

            Optional<ItemStack> decoded = updateItemStackTag(itemTag, dataVersion)
                    .flatMap(updated -> ItemStack.CODEC.parse(RegistryOps.create(NbtOps.INSTANCE, player.registryAccess()), updated)
                            .resultOrPartial(error -> Aether.LOGGER.warn(
                                    "Retained but could not decode legacy accessory item from {} slot {} index {}: {}",
                                    sourceRoot, slotName, slot, error)));
            if (decoded.isPresent() && !decoded.get().isEmpty()) {
                destination.put(slot, decoded.get().copy());
                migrated++;
            }
        }
        return migrated;
    }

    private static int readSlotIndex(CompoundTag itemTag) {
        Optional<Byte> byteSlot = itemTag.getByte("Slot");
        if (byteSlot.isPresent()) {
            return Byte.toUnsignedInt(byteSlot.get());
        }
        return itemTag.getIntOr("Slot", -1);
    }

    private static void readHiddenRenderSlots(CompoundTag handler, int size, Set<Integer> hidden) {
        readHiddenRenderSlots(handler, size, hidden, "Renders");
    }

    private static void readHiddenRenderSlots(CompoundTag holder, int size, Set<Integer> hidden, String... keys) {
        for (String key : keys) {
            byte[] renderBytes = holder.getByteArray(key).orElse(null);
            if (renderBytes != null) {
                for (int i = 0; i < Math.min(size, renderBytes.length); i++) {
                    if (renderBytes[i] == 0) {
                        hidden.add(i);
                    }
                }
                return;
            }

            ListTag renders = holder.getList(key).orElse(null);
            if (renders == null) {
                continue;
            }
            for (int i = 0; i < Math.min(size, renders.size()); i++) {
                Tag render = renders.get(i);
                if (render instanceof NumericTag numeric && numeric.byteValue() == 0) {
                    hidden.add(i);
                }
            }
            return;
        }
    }

    private static CompoundTag getCompound(CompoundTag holder, String... keys) {
        for (String key : keys) {
            CompoundTag value = holder.getCompound(key).orElse(null);
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    private static ListTag getList(CompoundTag holder, String... keys) {
        for (String key : keys) {
            ListTag value = holder.getList(key).orElse(null);
            if (value != null) {
                return value;
            }
        }
        return new ListTag();
    }

    private static String getString(CompoundTag holder, String... keys) {
        for (String key : keys) {
            Optional<String> value = holder.getString(key);
            if (value.isPresent()) {
                return value.get();
            }
        }
        return "";
    }

    private static int getInt(CompoundTag holder, String... keys) {
        for (String key : keys) {
            Optional<Integer> value = holder.getInt(key);
            if (value.isPresent()) {
                return value.get();
            }
        }
        return 0;
    }

    private static int requiredSize(ListTag items) {
        int size = 0;
        for (Tag raw : items) {
            if (raw instanceof CompoundTag itemTag) {
                size = Math.max(size, readSlotIndex(itemTag) + 1);
            }
        }
        return size;
    }

    private static int renderOptionCount(CompoundTag holder) {
        for (String key : List.of("render_options", "RenderOptions")) {
            byte[] bytes = holder.getByteArray(key).orElse(null);
            if (bytes != null) {
                return bytes.length;
            }
            ListTag list = holder.getList(key).orElse(null);
            if (list != null) {
                return list.size();
            }
        }
        return 0;
    }

    record SlotTarget(String slotName, int size) {
    }

    record LegacySlotData(String sourceRoot, String identifier, SlotTarget target, CompoundTag handler) {
    }

    record LegacyAccessoriesSlotData(String sourceRoot, String slotName, int size, ListTag equipped,
                                     ListTag cosmetic, Set<Integer> hiddenRenderSlots) {
    }
}
