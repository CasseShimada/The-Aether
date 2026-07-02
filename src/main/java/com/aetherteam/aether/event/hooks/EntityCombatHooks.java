package com.aetherteam.aether.event.hooks;

import com.aetherteam.aether.entity.monster.dungeon.boss.Slider;
import net.minecraft.world.damagesource.DamageSource;

public final class EntityCombatHooks {
    private EntityCombatHooks() {
    }

    /**
     * Disallows blocking the Slider with a shield.
     *
     * @param source The {@link DamageSource} to block.
     * @return Whether to disallow blocking, as a {@link Boolean}.
     */
    public static boolean preventSliderShieldBlock(DamageSource source) {
        return source.getEntity() instanceof Slider;
    }
}
