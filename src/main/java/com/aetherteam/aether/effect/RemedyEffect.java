package com.aetherteam.aether.effect;

import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.attachment.AetherPlayerAttachment;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class RemedyEffect extends MobEffect {
    private int effectDuration;

    public RemedyEffect() {
        super(MobEffectCategory.BENEFICIAL, 5031241);
    }

    /**
     * Tracks the starting effect duration through {@link AetherPlayerAttachment} and removes Inebriation if the entity has it.
     *
     * @param livingEntity The affected {@link LivingEntity}.
     * @param amplifier    The {@link Integer} amplifier for the effect.
     */
    @Override
    public boolean applyEffectTick(ServerLevel serverLevel, LivingEntity livingEntity, int amplifier) {
        if (livingEntity instanceof Player player) {
            var data = player.getAttachedOrCreate(AetherDataAttachments.AETHER_PLAYER);
            if (data.getRemedyStartDuration() <= 0) {
                data.setSyncedToServer(player.getId(), AetherPlayerAttachment.REMEDY_START_DURATION_SYNC_KEY, this.effectDuration);
            }
        }
        var inebriation = BuiltInRegistries.MOB_EFFECT.wrapAsHolder(AetherEffects.INEBRIATION);
        if (livingEntity.hasEffect(inebriation)) {
            livingEntity.removeEffect(inebriation);
        }
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        this.effectDuration = duration;
        return true;
    }
}
