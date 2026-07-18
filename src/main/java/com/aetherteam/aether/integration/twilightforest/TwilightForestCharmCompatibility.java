package com.aetherteam.aether.integration.twilightforest;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.accessories.api.AccessoriesAPI;
import com.aetherteam.aether.accessories.api.AccessoryMutationResult;
import com.aetherteam.aether.accessories.slot.AccessorySlotResolver;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.BooleanSupplier;

/** Optional Twilight Forest bridge containing no Twilight Forest class references. */
public final class TwilightForestCharmCompatibility {
    public static final String CONSUMED_CHARM_TAG = "CharmStack";

    private TwilightForestCharmCompatibility() {
    }

    public static boolean consumeAccessoryCharm(Player player, Item item, CompoundTag persistentTag, boolean saveItemToTag) {
        if (player.level().isClientSide()) {
            return false;
        }
        AccessoryMutationResult result = AccessoriesAPI.consumeFirst(player,
            stack -> stack.is(item) && AccessorySlotResolver.isCharmTag(stack));
        if (result == null) {
            return false;
        }
        if (saveItemToTag) {
            saveConsumedStack(persistentTag, true, encodeStack(player, result.previousStack()));
        }
        return true;
    }

    public static boolean afterInventoryResult(boolean inventoryResult, BooleanSupplier accessoryFallback) {
        return inventoryResult || accessoryFallback.getAsBoolean();
    }

    public static void saveConsumedStack(CompoundTag persistentTag, boolean saveItemToTag, CompoundTag encodedStack) {
        if (saveItemToTag) {
            persistentTag.put(CONSUMED_CHARM_TAG, encodedStack.copy());
        }
    }

    static CompoundTag encodeStack(Player player, ItemStack stack) {
        RegistryOps<Tag> ops = RegistryOps.create(NbtOps.INSTANCE, player.registryAccess());
        return ItemStack.CODEC.encodeStart(ops, stack)
            .resultOrPartial(message -> Aether.LOGGER.error("Unable to encode consumed Twilight Forest charm: {}", message))
            .filter(CompoundTag.class::isInstance)
            .map(CompoundTag.class::cast)
            .orElseGet(CompoundTag::new);
    }
}
