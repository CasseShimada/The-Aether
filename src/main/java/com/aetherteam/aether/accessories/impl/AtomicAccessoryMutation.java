package com.aetherteam.aether.accessories.impl;

import net.minecraft.world.item.ItemStack;

/** Ordered, testable transaction boundary used by the attachment-backed accessory storage. */
public final class AtomicAccessoryMutation {
    private AtomicAccessoryMutation() {
    }

    public static ItemStack consumeOneCopy(ItemStack stack) {
        ItemStack replacement = stack.copy();
        replacement.shrink(1);
        return replacement.isEmpty() ? ItemStack.EMPTY : replacement;
    }

    public static int consumeOneCount(int count) {
        return Math.max(0, count - 1);
    }

    public static boolean commit(ItemStack previous,
                                 ItemStack current,
                                 Runnable onUnequip,
                                 Runnable apply,
                                 Runnable persist,
                                 Runnable sync) {
        boolean changed = !sameStack(previous, current);
        boolean unequipped = !previous.isEmpty() && (current.isEmpty() || !ItemStack.isSameItem(previous, current));
        return commit(changed, unequipped, onUnequip, apply, persist, sync);
    }

    public static boolean commit(boolean changed,
                                 boolean unequipped,
                                 Runnable onUnequip,
                                 Runnable apply,
                                 Runnable persist,
                                 Runnable sync) {
        if (!changed) {
            return false;
        }
        if (unequipped) {
            onUnequip.run();
        }
        apply.run();
        persist.run();
        sync.run();
        return true;
    }

    private static boolean sameStack(ItemStack first, ItemStack second) {
        return ItemStack.isSameItemSameComponents(first, second) && first.getCount() == second.getCount();
    }
}
