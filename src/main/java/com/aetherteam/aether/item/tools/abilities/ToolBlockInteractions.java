package com.aetherteam.aether.item.tools.abilities;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;

public final class ToolBlockInteractions {
    private static final ToolInteraction AXE_STRIP = new ToolInteraction(ToolAbilityHooks.ToolAction.AXE_STRIP, SoundEvents.AXE_STRIP);
    private static final ToolInteraction SHOVEL_FLATTEN = new ToolInteraction(ToolAbilityHooks.ToolAction.SHOVEL_FLATTEN, SoundEvents.SHOVEL_FLATTEN);
    private static final ToolInteraction HOE_TILL = new ToolInteraction(ToolAbilityHooks.ToolAction.HOE_TILL, SoundEvents.HOE_TILL);

    private ToolBlockInteractions() {
    }

    public static InteractionResult interact(Player player, Level level, InteractionHand hand, BlockHitResult hitResult) {
        ItemStack stack = player.getItemInHand(hand);
        ToolInteraction interaction = getInteraction(stack);
        if (interaction == null) {
            return InteractionResult.PASS;
        }

        UseOnContext context = new UseOnContext(player, hand, hitResult);
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        BlockState modified = ToolAbilityHooks.setupItemAbilities(level, pos, state, interaction.action());
        if (modified == state) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide()) {
            level.setBlock(pos, modified, 11);
            level.playSound(null, pos, interaction.sound(), SoundSource.BLOCKS, 1.0F, 1.0F);
            if (interaction.action() == ToolAbilityHooks.ToolAction.AXE_STRIP) {
                ToolAbilityHooks.stripGoldenOak(level, state, stack, interaction.action(), context);
            }
            stack.hurtAndBreak(1, player, hand);
        }

        level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, modified));
        return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
    }

    private static ToolInteraction getInteraction(ItemStack stack) {
        if (stack.getItem() instanceof AxeItem) {
            return AXE_STRIP;
        }
        if (stack.getItem() instanceof ShovelItem) {
            return SHOVEL_FLATTEN;
        }
        if (stack.getItem() instanceof HoeItem) {
            return HOE_TILL;
        }
        return null;
    }

    private record ToolInteraction(ToolAbilityHooks.ToolAction action, SoundEvent sound) {
    }
}
