package com.aetherteam.aether.data.resources.registries;

import com.aetherteam.aether.block.AetherBlocks;
import com.aetherteam.aether.item.AetherItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public final class AetherDataMaps {
    private static final List<FuelEntry> ALTAR_FUEL = List.of(
            new FuelEntry(() -> AetherBlocks.AMBROSIUM_BLOCK.asItem(), 2500),
            new FuelEntry(() -> AetherItems.AMBROSIUM_SHARD.get(), 250)
    );
    private static final List<FuelEntry> FREEZER_FUEL = List.of(
            new FuelEntry(() -> AetherBlocks.ICESTONE.asItem(), 400),
            new FuelEntry(() -> AetherBlocks.ICESTONE_SLAB.asItem(), 200),
            new FuelEntry(() -> AetherBlocks.ICESTONE_STAIRS.asItem(), 400),
            new FuelEntry(() -> AetherBlocks.ICESTONE_WALL.asItem(), 400)
    );
    private static final List<FuelEntry> INCUBATOR_FUEL = List.of(
            new FuelEntry(() -> AetherBlocks.AMBROSIUM_TORCH.asItem(), 500)
    );

    private AetherDataMaps() {
    }

    public static int getAltarBurnTime(ItemStack stack) {
        return getBurnTime(ALTAR_FUEL, stack);
    }

    public static int getFreezerBurnTime(ItemStack stack) {
        return getBurnTime(FREEZER_FUEL, stack);
    }

    public static int getIncubatorBurnTime(ItemStack stack) {
        return getBurnTime(INCUBATOR_FUEL, stack);
    }

    public static boolean isAltarFuel(ItemStack stack) {
        return getAltarBurnTime(stack) > 0;
    }

    public static boolean isFreezerFuel(ItemStack stack) {
        return getFreezerBurnTime(stack) > 0;
    }

    public static boolean isIncubatorFuel(ItemStack stack) {
        return getIncubatorBurnTime(stack) > 0;
    }

    public static void forEachAltarFuel(BiConsumer<Item, Integer> consumer) {
        ALTAR_FUEL.forEach((entry) -> consumer.accept(entry.item().get(), entry.burnTime()));
    }

    public static void forEachFreezerFuel(BiConsumer<Item, Integer> consumer) {
        FREEZER_FUEL.forEach((entry) -> consumer.accept(entry.item().get(), entry.burnTime()));
    }

    public static void forEachIncubatorFuel(BiConsumer<Item, Integer> consumer) {
        INCUBATOR_FUEL.forEach((entry) -> consumer.accept(entry.item().get(), entry.burnTime()));
    }

    private static int getBurnTime(List<FuelEntry> entries, ItemStack stack) {
        if (stack.isEmpty()) {
            return 0;
        }
        for (FuelEntry entry : entries) {
            if (stack.is(entry.item().get())) {
                return entry.burnTime();
            }
        }
        return 0;
    }

    private record FuelEntry(Supplier<Item> item, int burnTime) {
    }
}
