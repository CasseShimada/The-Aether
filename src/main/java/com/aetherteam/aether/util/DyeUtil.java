package com.aetherteam.aether.util;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public final class DyeUtil {
    private DyeUtil() {
    }

    public static DyeColor colorOf(DyeItem item) {
        String path = BuiltInRegistries.ITEM.getKey(item).getPath();
        return DyeColor.byName(path.replace("_dye", ""), DyeColor.WHITE);
    }

    public static Item itemOf(DyeColor color) {
        return Items.DYE.pick(color);
    }
}
