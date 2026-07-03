package com.aetherteam.aether.inventory.menu;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.client.ClientAccess;
import com.aetherteam.aether.inventory.container.LoreInventory;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.Identifier;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class LoreBookMenu extends AbstractContainerMenu {
    private static final Map<Function<RegistryAccess, Predicate<ItemStack>>, String> LORE_ENTRY_OVERRIDES = new HashMap<>();
    private static Set<String> CACHED_LORE_KEYS = null;
    private static Map<String, String> CACHED_LORE_ENTRY_TEXTS = null;
    private static boolean loggedLoreCache;
    private final LoreInventory loreInventory;
    private boolean loreEntryExists;

    public LoreBookMenu(int id, Inventory playerInventory) {
        this(id, playerInventory, new LoreInventory(playerInventory.player));
    }

    public LoreBookMenu(int id, Inventory playerInventory, LoreInventory loreInventory) {
        super(AetherMenuTypes.BOOK_OF_LORE, id);
        checkContainerSize(loreInventory, 1);
        this.loreInventory = loreInventory;
        loreInventory.setMenu(this); // Provide this menu to the LoreInventory.
        loreInventory.startOpen(playerInventory.player);
        this.addSlot(new Slot(loreInventory, 0, 83, 63));
        for (int k = 0; k < 3; ++k) {
            for (int i1 = 0; i1 < 9; ++i1) {
                this.addSlot(new Slot(playerInventory, i1 + k * 9 + 9, 48 + i1 * 18, 113 + k * 18));
            }
        }
        for (int l = 0; l < 9; ++l) {
            this.addSlot(new Slot(playerInventory, l, 48 + l * 18, 171));
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return this.loreInventory.stillValid(player);
    }

    /**
     * Warning for "ConstantConditions" is suppressed because of being based on vanilla code.
     */
    @SuppressWarnings("ConstantConditions")
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemStack1 = slot.getItem();
            itemStack = itemStack1.copy();
            if (index < 1) {
                if (!this.moveItemStackTo(itemStack1, 1, 37, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemStack1, 0, 1, false)) {
                return ItemStack.EMPTY;
            }
            if (itemStack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
            if (itemStack1.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(player, itemStack1);
        }
        return itemStack;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        if (player instanceof ServerPlayer serverPlayer) {
            ItemStack stack = this.loreInventory.getItem(0);
            if (!stack.isEmpty()) {
                if (player.isAlive() && !serverPlayer.hasDisconnected()) {
                    player.getInventory().placeItemBackInInventory(stack);
                } else {
                    player.drop(stack, false);
                }
                this.loreInventory.setItem(0, ItemStack.EMPTY);
            }
        }
        this.loreInventory.stopOpen(player);
    }

    public boolean getLoreEntryExists() {
        return this.loreEntryExists;
    }

    public void setLoreEntryExists(boolean loreEntryExists) {
        this.loreEntryExists = loreEntryExists;
    }

    public static String describeStack(ItemStack stack) {
        if (stack.isEmpty()) {
            return "<empty>";
        }
        Identifier itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        String itemName = itemId != null ? itemId.toString() : stack.getItem().toString();
        return itemName + " x" + stack.getCount() + " [" + stack.getHoverName().getString() + "]";
    }

    @Environment(EnvType.CLIENT)
    public static void addLoreEntryOverride(Function<RegistryAccess, Predicate<ItemStack>> predicate, String entry) {
        LORE_ENTRY_OVERRIDES.putIfAbsent(predicate, entry);
    }

    @Environment(EnvType.CLIENT)
    public String getLoreEntryKey(ItemStack stack) {
        Optional<String> key = LORE_ENTRY_OVERRIDES.entrySet().stream().filter(e -> e.getKey().apply(this.loreInventory.player.registryAccess()).test(stack)).findAny().map(Map.Entry::getValue);
        if (key.isPresent() && this.hasLoreEntryTranslation(key.get())) {
            Aether.LOGGER.info("Book of Lore resolved override key '{}' for {}", key.get(), describeStack(stack));
            return key.get();
        }

        String defaultKey = "lore." + stack.getItem().getDescriptionId();
        if (this.hasLoreEntryTranslation(defaultKey)) {
            Aether.LOGGER.info("Book of Lore resolved default key '{}' for {}", defaultKey, describeStack(stack));
            return defaultKey;
        }

        Identifier itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (itemId != null) {
            String itemKey = "lore.item." + itemId.getNamespace() + "." + itemId.getPath();
            if (this.hasLoreEntryTranslation(itemKey)) {
                Aether.LOGGER.info("Book of Lore resolved registry item key '{}' for {}", itemKey, describeStack(stack));
                return itemKey;
            }
        }

        if (stack.getItem() instanceof BlockItem blockItem) {
            Identifier blockId = BuiltInRegistries.BLOCK.getKey(blockItem.getBlock());
            if (blockId != null) {
                String blockKey = "lore.block." + blockId.getNamespace() + "." + blockId.getPath();
                if (this.hasLoreEntryTranslation(blockKey)) {
                    Aether.LOGGER.info("Book of Lore resolved registry block key '{}' for {}", blockKey, describeStack(stack));
                    return blockKey;
                }
            }
        }
        Aether.LOGGER.info("Book of Lore did not find a direct lore key for {}; falling back to '{}'", describeStack(stack), defaultKey);
        return defaultKey;
    }

    @Environment(EnvType.CLIENT)
    public boolean loreEntryKeyExists(ItemStack stack) {
        String key = this.getLoreEntryKey(stack);
        boolean exists = this.hasLoreEntryTranslation(key);
        Aether.LOGGER.info("Book of Lore key existence check for {} -> key='{}', exists={}", describeStack(stack), key, exists);
        return exists;
    }

    @Environment(EnvType.CLIENT)
    private boolean hasLoreEntryTranslation(String key) {
        return ClientAccess.hasTranslation(key) || getKnownLoreKeys().contains(key);
    }

    @Environment(EnvType.CLIENT)
    private static Set<String> getKnownLoreKeys() {
        if (CACHED_LORE_KEYS != null) {
            return CACHED_LORE_KEYS;
        }

        Map<String, String> entries = getKnownLoreEntryTexts();
        Set<String> keys = new LinkedHashSet<>(entries.keySet());

        CACHED_LORE_KEYS = Collections.unmodifiableSet(keys);
        return CACHED_LORE_KEYS;
    }

    @Environment(EnvType.CLIENT)
    public String resolveLoreEntryText(String key) {
        if (ClientAccess.hasTranslation(key)) {
            return I18n.get(key);
        }
        return getKnownLoreEntryTexts().getOrDefault(key, key);
    }

    @Environment(EnvType.CLIENT)
    private static Map<String, String> getKnownLoreEntryTexts() {
        if (CACHED_LORE_ENTRY_TEXTS != null) {
            return CACHED_LORE_ENTRY_TEXTS;
        }

        Map<String, String> entries = new HashMap<>();
        loadLoreEntriesFromResourceManager(entries);
        if (entries.isEmpty()) {
            loadLoreEntriesFromClasspath(entries);
        }

        if (!loggedLoreCache) {
            loggedLoreCache = true;
            Aether.LOGGER.info("Book of Lore cached {} lore entries", entries.size());
        }
        CACHED_LORE_ENTRY_TEXTS = Collections.unmodifiableMap(entries);
        return CACHED_LORE_ENTRY_TEXTS;
    }

    @Environment(EnvType.CLIENT)
    private static void loadLoreEntriesFromResourceManager(Map<String, String> entries) {
        Identifier languageFile = Identifier.fromNamespaceAndPath("aether", "lang/en_us.json");
        try (var reader = Minecraft.getInstance().getResourceManager().openAsReader(languageFile)) {
            loadLoreEntriesFromJson(reader, entries);
            Aether.LOGGER.info("Book of Lore loaded {} entries from resource manager file {}", entries.size(), languageFile);
        } catch (Exception exception) {
            Aether.LOGGER.warn("Book of Lore failed to load {} from the resource manager", languageFile, exception);
        }
    }

    @Environment(EnvType.CLIENT)
    private static void loadLoreEntriesFromClasspath(Map<String, String> entries) {
        try (var stream = LoreBookMenu.class.getResourceAsStream("/assets/aether/lang/en_us.json")) {
            if (stream != null) {
                try (var reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
                    loadLoreEntriesFromJson(reader, entries);
                    Aether.LOGGER.info("Book of Lore loaded {} entries from the classpath fallback", entries.size());
                }
            } else {
                Aether.LOGGER.warn("Book of Lore classpath fallback '/assets/aether/lang/en_us.json' was missing");
            }
        } catch (Exception exception) {
            Aether.LOGGER.warn("Book of Lore failed to load lore entries from the classpath fallback", exception);
        }
    }

    @Environment(EnvType.CLIENT)
    private static void loadLoreEntriesFromJson(java.io.Reader reader, Map<String, String> entries) {
        JsonElement root = JsonParser.parseReader(reader);
        if (root.isJsonObject()) {
            JsonObject object = root.getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
                if (entry.getKey().startsWith("lore.") && entry.getValue().isJsonPrimitive() && entry.getValue().getAsJsonPrimitive().isString()) {
                    entries.put(entry.getKey(), entry.getValue().getAsString());
                }
            }
        }
    }
}
