package com.aetherteam.aether.event.hooks;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

import javax.annotation.Nullable;

public final class EntityInteractionHooks {
    private EntityInteractionHooks() {
    }

    public static InteractionResult useEntity(Player player, Level level, InteractionHand hand, Entity entity, @Nullable EntityHitResult hitResult) {
        if (level.isClientSide()) {
            return InteractionResult.PASS;
        }

        EntityBucketHooks.skyrootBucketMilking(entity, player, hand);
        var result = EntityBucketHooks.pickupBucketable(entity, player, hand);
        if (result.isPresent()) {
            return result.get();
        }

        if (hitResult != null) {
            result = EntityArmorStandHooks.interactWithArmorStand(entity, player, player.getItemInHand(hand), hitResult.getLocation(), hand);
            if (result.isPresent()) {
                return result.get();
            }
        }

        return InteractionResult.PASS;
    }
}
