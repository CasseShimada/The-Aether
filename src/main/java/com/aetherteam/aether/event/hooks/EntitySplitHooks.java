package com.aetherteam.aether.event.hooks;

import com.aetherteam.aether.AetherTags;
import net.minecraft.world.entity.Mob;

public final class EntitySplitHooks {
    private EntitySplitHooks() {
    }

    /**
     * Prevents Slime split behavior from carrying over to Swets.
     *
     * @param mob The splitting {@link Mob}.
     * @return Whether the {@link Mob} should split.
     */
    public static boolean preventSplit(Mob mob) {
        return mob.getType().builtInRegistryHolder().is(AetherTags.Entities.SWETS);
    }
}
