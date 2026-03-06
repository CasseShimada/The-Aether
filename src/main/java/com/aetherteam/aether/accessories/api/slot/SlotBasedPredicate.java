package com.aetherteam.aether.accessories.api.slot;

import net.minecraft.world.item.Item;

import java.util.function.Predicate;

@FunctionalInterface
public interface SlotBasedPredicate {
    boolean test(Item item);

    static SlotBasedPredicate ofItem(Predicate<Item> predicate) {
        return predicate::test;
    }
}
