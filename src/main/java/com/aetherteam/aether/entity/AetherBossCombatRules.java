package com.aetherteam.aether.entity;

import com.aetherteam.aether.entity.monster.dungeon.boss.Slider;
import com.aetherteam.aether.entity.monster.dungeon.boss.ValkyrieQueen;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;

/**
 * Vanilla combat behavior overrides used by Aether dungeon bosses.
 */
public final class AetherBossCombatRules {
    private AetherBossCombatRules() {
    }

    public static boolean shouldApplyKnockback(LivingEntity target, DamageSource source) {
        return !(target instanceof ValkyrieQueen) || !(source.getDirectEntity() instanceof Projectile);
    }

    public static boolean bypassesShieldBlocking(DamageSource source) {
        return source.getEntity() instanceof Slider;
    }
}
