package com.aetherteam.aether.event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

/**
 * TriggerTrapEvent is fired when a player steps on a trapped block.
 * <br>
 * This event is cancellable.<br>
 * If the event is not canceled, the trapped block will be detected as having been stepped on.
 * <br>
 * This event is fired by Aether's local event dispatch.<br>
 * <br>
 * This event is fired on both sides.<br>
 * <br>
 * If this event is canceled, the trapped block will not trigger.
 */
public class TriggerTrapEvent {
    private boolean canceled;
    private final Player player;
    private final LevelAccessor level;
    private final BlockPos pos;
    private final BlockState state;

    /**
     * @param player The {@link Player} stepping on the trapped block.
     * @param level  The {@link LevelAccessor} that the block is in.
     * @param pos    The {@link BlockPos} of the block.
     * @param state  The {@link BlockState} of the block.
     */
    public TriggerTrapEvent(Player player, LevelAccessor level, BlockPos pos, BlockState state) {
        this.player = player;
        this.level = level;
        this.pos = pos;
        this.state = state;
    }

    /**
     * @return The {@link Player} stepping on the trapped block.
     */
    public Player getPlayer() {
        return this.player;
    }

    public LevelAccessor getLevel() {
        return this.level;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public BlockState getState() {
        return this.state;
    }

    public boolean isCanceled() {
        return this.canceled;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }
}
