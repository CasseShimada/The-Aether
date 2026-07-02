package com.aetherteam.aether.event.hooks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;

import javax.annotation.Nullable;

/**
 * Fabric-side hook helpers for call sites that need simple Minecraft defaults.
 * These methods use vanilla default behavior when no loader-specific hook exists.
 */
public final class EventHooks {
    private EventHooks() {
    }

    public static boolean onProjectileImpact(Projectile projectile, HitResult hitResult) {
        return false;
    }

    public static boolean canEntityGrief(Level level, Entity entity) {
        return !(level instanceof ServerLevel serverLevel) || serverLevel.getGameRules().get(GameRules.MOB_GRIEFING);
    }

    public static boolean doPlayerHarvestCheck(@Nullable Player player, BlockState state, Level level, BlockPos pos) {
        return player != null && player.hasCorrectToolForDrops(state);
    }

    @Nullable
    public static InteractionResult onArrowNock(ItemStack stack, Level level, Player player, InteractionHand hand, boolean hasAmmo) {
        return null;
    }

    public static int onArrowLoose(ItemStack stack, Level level, Player player, int charge, boolean hasAmmo) {
        return charge;
    }
}
