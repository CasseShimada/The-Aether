package com.aetherteam.aether.accessories.api.slot;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

@FunctionalInterface
public interface SlotBasedPredicate {
    boolean test(ItemStack stack);

    default boolean test(Item item) {
        return this.test(item.getDefaultInstance());
    }

    static SlotBasedPredicate ofItem(Predicate<Item> predicate) {
        return stack -> predicate.test(stack.getItem());
    }

    static SlotBasedPredicate ofStack(Predicate<ItemStack> predicate) {
        return predicate::test;
    }
}
