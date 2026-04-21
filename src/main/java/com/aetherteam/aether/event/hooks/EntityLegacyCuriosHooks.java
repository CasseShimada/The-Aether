package com.aetherteam.aether.event.hooks;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.accessories.api.AccessoriesAPI;
import com.aetherteam.aether.accessories.api.AccessoriesCapability;
import com.aetherteam.aether.accessories.api.core.Accessory;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.NameAndId;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Optional;

final class EntityLegacyCuriosHooks {
    private EntityLegacyCuriosHooks() {
    }

    static void loadLegacyCuriosData(ServerPlayer player) {
        CompoundTag playerTag = player.level().getServer().getPlayerList().loadPlayerData(new NameAndId(player.getGameProfile())).orElse(null);
        if (playerTag == null) {
            return;
        }

        var capsTag = tryGetLegacyCapsTag(playerTag);
        if (capsTag.isEmpty()) {
            return;
        }

        CompoundTag curiosInventoryTag = capsTag.get().getCompound("curios:inventory").orElse(null);
        if (curiosInventoryTag == null) {
            return;
        }

        if (curiosInventoryTag.getBoolean("AccessoriesEncoded").orElse(false) || !curiosInventoryTag.contains("Curios")) {
            return;
        }

        Tag curiosTag = curiosInventoryTag.get("Curios");
        if (!(curiosTag instanceof ListTag curiosListTag)) {
            return;
        }

        AccessoriesCapability accessories = AccessoriesCapability.get(player);
        if (accessories == null) {
            return;
        }

        for (Tag tag : curiosListTag) {
            if (!(tag instanceof CompoundTag compoundTag) || !compoundTag.contains("StacksHandler")) {
                continue;
            }

            CompoundTag stacksHandlerTag = compoundTag.getCompound("StacksHandler").orElse(null);
            if (stacksHandlerTag == null || !stacksHandlerTag.contains("Stacks")) {
                continue;
            }

            CompoundTag stacksTag = stacksHandlerTag.getCompound("Stacks").orElse(null);
            if (stacksTag == null || !stacksTag.contains("Items")) {
                continue;
            }

            Tag itemsTag = stacksTag.get("Items");
            if (!(itemsTag instanceof ListTag listTag)) {
                continue;
            }

            for (Tag itemTag : listTag) {
                if (!(itemTag instanceof CompoundTag itemCompoundTag) || !itemCompoundTag.contains("id")) {
                    continue;
                }

                String itemIdString = itemCompoundTag.getString("id").orElse("");
                if (itemIdString.isEmpty()) {
                    continue;
                }

                Identifier itemId;
                try {
                    itemId = Identifier.parse(itemIdString);
                } catch (IllegalArgumentException ignored) {
                    continue;
                }

                if (!Aether.MODID.equals(itemId.getNamespace())) {
                    continue;
                }

                Item item = BuiltInRegistries.ITEM.get(itemId)
                        .map(reference -> reference.value())
                        .orElse(Items.AIR);
                if (item == Items.AIR) {
                    continue;
                }

                ItemStack stack = new ItemStack(item);
                Accessory accessory = AccessoriesAPI.getOrDefaultAccessory(stack);
                var equipReference = accessories.canEquipAccessory(stack, true);
                if (equipReference == null) {
                    continue;
                }

                if (accessory.canEquip(stack, equipReference.first())) {
                    equipReference.second().equipStack(stack.copy());
                }
            }
        }
    }

    private static Optional<CompoundTag> tryGetLegacyCapsTag(CompoundTag playerTag) {
        if (playerTag == null) {
            return Optional.empty();
        }

        CompoundTag capsTag;
        if (playerTag.contains("ForgeCaps")) {
            capsTag = playerTag.getCompound("ForgeCaps").orElse(null);
        } else if (playerTag.contains("neoforge:attachments")) {
            capsTag = playerTag.getCompound("neoforge:attachments").orElse(null);
        } else {
            return Optional.empty();
        }

        if (capsTag == null) {
            return Optional.empty();
        }

        return capsTag.contains("curios:inventory")
                ? Optional.of(capsTag)
                : Optional.empty();
    }
}
