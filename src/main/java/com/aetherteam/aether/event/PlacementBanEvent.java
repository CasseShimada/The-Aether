package com.aetherteam.aether.event;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * PlacementBanEvent is fired when an event involving placement banning occurs.<br>
 * If a method utilizes this event as its parameter, the method will receive every child event of this class.<br>
 * <br>
 * All children of this event are dispatched from Aether's local event dispatch points.
 */
public abstract class PlacementBanEvent {
    /**
     * PlacementBanEvent.SpawnParticles is fired after a placement ban has occurred.
     * <br>
     * This event is cancellable.<br>
     * If the event is not canceled, the particles will spawn.
     * <br>
     * This event is fired on both sides.<br>
     * <br>
     * If this event is canceled, the particles will not be spawned.
     */
    public static class SpawnParticles extends PlacementBanEvent {
        private boolean canceled;
        private final LevelAccessor level;
        private final BlockPos pos;
        @Nullable
        private final Direction face;
        @Nullable
        private final ItemStack itemStack;
        @Nullable
        private final BlockState blockState;

        /**
         * @param level The {@link LevelAccessor} to spawn the particles in.
         * @param pos   The {@link BlockPos} to spawn the particles at.
         * @param face  The {@link Direction} of the face the particles are spawning from.
         * @param stack The {@link ItemStack} being banned.
         * @param state The {@link BlockState} being banned.
         */
        public SpawnParticles(LevelAccessor level, BlockPos pos, @Nullable Direction face, @Nullable ItemStack stack, @Nullable BlockState state) {
            this.level = Objects.requireNonNull(level, "Null world in PlacementBanEvent");
            this.pos = Objects.requireNonNull(pos, "Null position in PlacementBanEvent");
            this.face = face;
            this.itemStack = stack;
            this.blockState = state;
        }

        public boolean isCanceled() {
            return this.canceled;
        }

        public void setCanceled(boolean canceled) {
            this.canceled = canceled;
        }

        /**
         * @return The {@link LevelAccessor} to spawn the particles in.
         */
        public LevelAccessor getLevel() {
            return this.level;
        }

        /**
         * @return The {@link BlockPos} to spawn the particles at.
         */
        public BlockPos getPos() {
            return this.pos;
        }

        /**
         * This method is {@link Nullable}. It is marked null for block placement bans.
         *
         * @return The {@link Direction} of the face the particles are spawning from.
         */
        @Nullable
        public Direction getFace() {
            return this.face;
        }

        /**
         * This method is {@link Nullable}. It is marked null for block placement bans.
         *
         * @return The {@link ItemStack} being banned.
         */
        @Nullable
        public ItemStack getItemStack() {
            return this.itemStack;
        }

        /**
         * This method is {@link Nullable}. It is marked null for item placement bans.
         *
         * @return The {@link BlockState} being banned.
         */
        @Nullable
        public BlockState getBlockState() {
            return this.blockState;
        }
    }
}
