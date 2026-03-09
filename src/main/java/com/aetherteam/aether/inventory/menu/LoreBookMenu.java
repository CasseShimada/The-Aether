package com.aetherteam.aether.inventory.menu;

import com.aetherteam.aether.inventory.container.LoreInventory;
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
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

public class LoreBookMenu extends AbstractContainerMenu {
    private static final Map<Function<RegistryAccess, Predicate<ItemStack>>, String> LORE_ENTRY_OVERRIDES = new HashMap<>();
    private final LoreInventory loreInventory;
    private boolean loreEntryExists;

    public LoreBookMenu(int id, Inventory playerInventory) {
        this(id, playerInventory, new LoreInventory(playerInventory.player));
    }

    public LoreBookMenu(int id, Inventory playerInventory, LoreInventory loreInventory) {
        super(AetherMenuTypes.BOOK_OF_LORE.get(), id);
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

    @Environment(EnvType.CLIENT)
    public static void addLoreEntryOverride(Function<RegistryAccess, Predicate<ItemStack>> predicate, String entry) {
        LORE_ENTRY_OVERRIDES.putIfAbsent(predicate, entry);
    }

    @Environment(EnvType.CLIENT)
    public String getLoreEntryKey(ItemStack stack) {
        Optional<String> key = LORE_ENTRY_OVERRIDES.entrySet().stream().filter(e -> e.getKey().apply(this.loreInventory.player.registryAccess()).test(stack)).findAny().map(Map.Entry::getValue);
        if (key.isPresent()) {
            return key.get();
        }

        List<String> candidates = this.getLoreEntryCandidates(stack);
        for (String candidate : candidates) {
            if (this.hasLoreEntryTranslation(candidate)) {
                return candidate;
            }
        }
        return candidates.isEmpty() ? "lore.item.minecraft.air" : candidates.getFirst();
    }

    @Environment(EnvType.CLIENT)
    public boolean loreEntryKeyExists(ItemStack stack) {
        return this.hasLoreEntryTranslation(this.getLoreEntryKey(stack));
    }

    @Environment(EnvType.CLIENT)
    private List<String> getLoreEntryCandidates(ItemStack stack) {
        Set<String> candidates = new LinkedHashSet<>();

        if (stack.getHoverName().getContents() instanceof TranslatableContents translatableContents) {
            candidates.add("lore." + translatableContents.getKey());
        }
        if (stack.getItemName().getContents() instanceof TranslatableContents translatableContents) {
            candidates.add("lore." + translatableContents.getKey());
        }
        candidates.add("lore." + stack.getItem().getDescriptionId());

        Identifier itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (itemId != null) {
            candidates.add("lore.item." + itemId.getNamespace() + "." + itemId.getPath());
        }

        if (stack.getItem() instanceof BlockItem blockItem) {
            Identifier blockId = BuiltInRegistries.BLOCK.getKey(blockItem.getBlock());
            if (blockId != null) {
                candidates.add("lore.block." + blockId.getNamespace() + "." + blockId.getPath());
            }
        }

        return new ArrayList<>(candidates);
    }

    @Environment(EnvType.CLIENT)
    private boolean hasLoreEntryTranslation(String key) {
        return !I18n.get(key).equals(key);
    }
}
