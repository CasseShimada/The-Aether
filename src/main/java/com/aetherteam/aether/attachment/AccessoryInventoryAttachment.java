package com.aetherteam.aether.attachment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Persistent accessory inventory payload used by the Aether self-written accessory core.
 */
public class AccessoryInventoryAttachment {
    private static final Codec<Map<Integer, ItemStack>> SLOT_STACK_MAP_CODEC = Codec.unboundedMap(Codec.STRING, ItemStack.CODEC)
        .xmap(AccessoryInventoryAttachment::decodeSlotStackMap, AccessoryInventoryAttachment::encodeSlotStackMap);

    private final Map<String, SlotStorage> slots;

    public static final Codec<AccessoryInventoryAttachment> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.unboundedMap(Codec.STRING, SlotStorage.CODEC).optionalFieldOf("slots", Map.of()).forGetter(AccessoryInventoryAttachment::rawSlots)
    ).apply(instance, AccessoryInventoryAttachment::new));

    public AccessoryInventoryAttachment() {
        this.slots = new LinkedHashMap<>();
    }

    private AccessoryInventoryAttachment(Map<String, SlotStorage> slots) {
        this.slots = new LinkedHashMap<>();
        for (Map.Entry<String, SlotStorage> entry : slots.entrySet()) {
            this.slots.put(entry.getKey(), entry.getValue().copy());
        }
    }

    public synchronized Map<String, SlotStorage> rawSlots() {
        Map<String, SlotStorage> snapshot = new LinkedHashMap<>();
        for (Map.Entry<String, SlotStorage> entry : this.slots.entrySet()) {
            snapshot.put(entry.getKey(), entry.getValue().copy());
        }
        return snapshot;
    }

    public synchronized SlotStorage getOrCreateSlot(String slotName, int fallbackSize) {
        return this.slots.computeIfAbsent(slotName, key -> SlotStorage.empty(fallbackSize));
    }

    public synchronized void putSlot(String slotName, SlotStorage storage) {
        this.slots.put(slotName, storage.copy());
    }

    public synchronized Set<String> slotNames() {
        return Set.copyOf(this.slots.keySet());
    }

    public synchronized void retainSlots(Set<String> slotNames) {
        this.slots.keySet().removeIf(slot -> !slotNames.contains(slot));
    }

    public record SlotStorage(int size,
                              Map<Integer, ItemStack> equipped,
                              Map<Integer, ItemStack> cosmetic,
                              Set<Integer> hiddenRenderSlots) {
        private static final Codec<Set<Integer>> HIDDEN_SLOT_CODEC = Codec.INT.listOf().xmap(HashSet::new, ArrayList::new);

        public SlotStorage {
            size = Math.max(1, size);
            equipped = Map.copyOf(copyStackMap(equipped));
            cosmetic = Map.copyOf(copyStackMap(cosmetic));
            hiddenRenderSlots = Set.copyOf(hiddenRenderSlots);
        }

        public static final Codec<SlotStorage> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.optionalFieldOf("size", 1).forGetter(SlotStorage::size),
                SLOT_STACK_MAP_CODEC.optionalFieldOf("equipped", Map.of()).forGetter(SlotStorage::equipped),
                SLOT_STACK_MAP_CODEC.optionalFieldOf("cosmetic", Map.of()).forGetter(SlotStorage::cosmetic),
                HIDDEN_SLOT_CODEC.optionalFieldOf("hidden_render_slots", Set.of()).forGetter(SlotStorage::hiddenRenderSlots)
        ).apply(instance, SlotStorage::new));

        public static SlotStorage empty(int size) {
            return new SlotStorage(Math.max(1, size), Map.of(), Map.of(), Set.of());
        }

        public SlotStorage copy() {
            return new SlotStorage(this.size, this.equipped, this.cosmetic, this.hiddenRenderSlots);
        }

        public SlotStorage normalizedSize(int fallbackSize) {
            int normalized = this.size > 0 ? this.size : Math.max(1, fallbackSize);
            if (normalized == this.size) {
                return this;
            }
            return new SlotStorage(normalized, this.equipped, this.cosmetic, this.hiddenRenderSlots);
        }

        public List<ItemStack> equippedList() {
            return toList(this.equipped, this.size);
        }

        public List<ItemStack> cosmeticList() {
            return toList(this.cosmetic, this.size);
        }

        public boolean[] renderFlags() {
            boolean[] flags = new boolean[Math.max(1, this.size)];
            for (int i = 0; i < flags.length; i++) {
                flags[i] = !this.hiddenRenderSlots.contains(i);
            }
            return flags;
        }

        public static SlotStorage fromContainers(List<ItemStack> equipped, List<ItemStack> cosmetic, boolean[] renderFlags) {
            int size = Math.max(1, Math.max(equipped.size(), cosmetic.size()));
            Map<Integer, ItemStack> equippedMap = new HashMap<>();
            Map<Integer, ItemStack> cosmeticMap = new HashMap<>();
            Set<Integer> hidden = new HashSet<>();

            for (int i = 0; i < equipped.size(); i++) {
                ItemStack stack = equipped.get(i);
                if (!stack.isEmpty()) {
                    equippedMap.put(i, stack.copy());
                }
            }

            for (int i = 0; i < cosmetic.size(); i++) {
                ItemStack stack = cosmetic.get(i);
                if (!stack.isEmpty()) {
                    cosmeticMap.put(i, stack.copy());
                }
            }

            for (int i = 0; i < renderFlags.length; i++) {
                if (!renderFlags[i]) {
                    hidden.add(i);
                }
            }

            return new SlotStorage(size, equippedMap, cosmeticMap, hidden);
        }

        private static Map<Integer, ItemStack> copyStackMap(Map<Integer, ItemStack> source) {
            Map<Integer, ItemStack> copy = new HashMap<>();
            for (Map.Entry<Integer, ItemStack> entry : source.entrySet()) {
                ItemStack stack = entry.getValue();
                if (!stack.isEmpty()) {
                    copy.put(entry.getKey(), stack.copy());
                }
            }
            return copy;
        }

        private static List<ItemStack> toList(Map<Integer, ItemStack> source, int size) {
            List<ItemStack> items = new ArrayList<>(Math.max(1, size));
            for (int i = 0; i < Math.max(1, size); i++) {
                ItemStack stack = source.get(i);
                items.add(stack == null ? ItemStack.EMPTY : stack.copy());
            }
            return items;
        }
    }

    private static Map<Integer, ItemStack> decodeSlotStackMap(Map<String, ItemStack> source) {
        Map<Integer, ItemStack> decoded = new HashMap<>();
        for (Map.Entry<String, ItemStack> entry : source.entrySet()) {
            try {
                int slot = Integer.parseInt(entry.getKey());
                ItemStack stack = entry.getValue();
                if (!stack.isEmpty()) {
                    decoded.put(slot, stack.copy());
                }
            } catch (NumberFormatException ignored) {
            }
        }
        return decoded;
    }

    private static Map<String, ItemStack> encodeSlotStackMap(Map<Integer, ItemStack> source) {
        Map<String, ItemStack> encoded = new HashMap<>();
        for (Map.Entry<Integer, ItemStack> entry : source.entrySet()) {
            ItemStack stack = entry.getValue();
            if (!stack.isEmpty()) {
                encoded.put(Integer.toString(entry.getKey()), stack.copy());
            }
        }
        return encoded;
    }
}
