package com.aetherteam.aether.event.hooks;

import com.aetherteam.aether.effect.AetherEffects;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public final class EntityEffectHooks {
    private EntityEffectHooks() {
    }

    /**
     * Prevents an entity from being inflicted with {@link AetherEffects#INEBRIATION} if it has {@link AetherEffects#REMEDY} applied.
     *
     * @param livingEntity    The {@link LivingEntity} that the effect is being applied to.
     * @param appliedInstance The {@link MobEffectInstance}.
     * @return Whether Inebriation application can be prevented.
     */
    public static boolean preventInebriation(LivingEntity livingEntity, MobEffectInstance appliedInstance) {
        return livingEntity.hasEffect(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(AetherEffects.REMEDY)) && appliedInstance.getEffect().value() == AetherEffects.INEBRIATION;
    }
}
