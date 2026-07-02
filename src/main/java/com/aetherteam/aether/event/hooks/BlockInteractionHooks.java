package com.aetherteam.aether.event.hooks;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

public final class BlockInteractionHooks {
    private BlockInteractionHooks() {
    }

    public static InteractionResult useBlock(Player player, Level level, InteractionHand hand, BlockHitResult hitResult) {
        if (player == null || hitResult == null) {
            return InteractionResult.PASS;
        }

        if (InteractionRecipeHooks.isBlockedInteraction(player, level, hand, hitResult.getBlockPos(), hitResult.getDirection())) {
            return InteractionResult.FAIL;
        }

        return DimensionPortalHooks.createPortal(player, level, hitResult.getBlockPos(), hitResult.getDirection(), player.getItemInHand(hand), hand)
                ? InteractionResult.SUCCESS
                : InteractionResult.PASS;
    }
}
