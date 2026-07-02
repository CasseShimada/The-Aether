package com.aetherteam.aether.event.hooks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

import javax.annotation.Nullable;

public class DimensionHooks {
    public static boolean playerLeavingAether;
    public static boolean displayAetherTravel;
    public static int teleportationTimer;

    /**
     * Spawns the player in the Aether dimension if the {@link AetherConfig.Server#spawn_in_aether} config is enabled.
     *
     * @param player The {@link Player}.
     */
    public static void startInAether(Player player) {
        DimensionSpawnHooks.startInAether(player);
    }

    /**
     * Used to handle creating an Aether portal from a glowstone frame if the correct activation item is used.
     *
     * @param player    The {@link Player} creating the portal.
     * @param level     The {@link Level} to create the portal in.
     * @param pos       The {@link BlockPos} to create the portal at.
     * @param direction The {@link Direction} of where the portal is interacted at.
     * @param stack     The {@link ItemStack} used to attempt to activate the portal.
     * @param hand      The {@link InteractionHand} that the item is in.
     * @return Whether the portal should be created, as a {@link Boolean}.
     */
    public static boolean createPortal(Player player, Level level, BlockPos pos, @Nullable Direction direction, ItemStack stack, InteractionHand hand) {
        return DimensionPortalHooks.createPortal(player, level, pos, direction, stack, hand);
    }

    /**
     * Detects whether water is found in a glowstone frame.
     *
     * @param levelAccessor The {@link Level} to create the portal in.
     * @param pos           The {@link BlockPos} to create the portal at.
     * @param blockState    The water {@link BlockState}.
     * @param fluidState    The water {@link FluidState}.
     * @return Whether the portal should be created, as a {@link Boolean}.
     */
    public static boolean detectWaterInFrame(LevelAccessor levelAccessor, BlockPos pos, BlockState blockState, FluidState fluidState) {
        return DimensionPortalHooks.detectWaterInFrame(levelAccessor, pos, blockState, fluidState);
    }

    /**
     * Ticks time in dimensions with the Aether effects location.
     *
     * @param level The {@link Level}
     */
    public static void tickTime(Level level) {
        DimensionTimeHooks.tickTime(level);
    }

    /**
     * Checks whether eternal day is configured to be disabled, and disables it in the {@link AetherPlayerAttachment}.
     *
     * @param level The {@link Level}
     */
    public static void checkEternalDayConfig(Level level) {
        DimensionTimeHooks.checkEternalDayConfig(level);
    }

    /**
     * @param entity    The {@link Entity} travelling between dimensions.
     * @param dimension The {@link ResourceKey} of the dimension ({@link Level}) being teleported to.
     */
    public static void dimensionTravel(Entity entity, ResourceKey<Level> dimension) {
        DimensionTravelHooks.dimensionTravel(entity, dimension);
    }

    /**
     * @param entity The {@link Entity} travelling between dimensions.
     */
    public static void removePlayerAerbunny(Entity entity) {
        DimensionTravelHooks.removePlayerAerbunny(entity);
    }

    /**
     * @param player The {@link Player} travelling between dimensions.
     */
    public static void remountPlayerAerbunny(Player player) {
        DimensionTravelHooks.remountPlayerAerbunny(player);
    }

    /**
     * Checks if the player was falling out of the Aether, and prevents server fly-hack checks during this.
     *
     * @param player The {@link Player}.
     */
    public static void travelling(Player player) {
        DimensionTravelHooks.travelling(player);
    }

    /**
     * Initializes the Aether level data for time separate from the overworld.
     * serverLevelData and levelData are access transformed.
     *
     * @param level The {@link LevelAccessor}.
     */
    public static void initializeLevelData(LevelAccessor level) {
        DimensionTimeHooks.initializeLevelData(level);
    }

    /**
     * Resets the weather cycle if players finish sleeping in an Aether dimension.<br>
     * Sets the time in the Aether according to the Aether's day/night cycle.
     *
     * @param level The {@link LevelAccessor}.
     */
    @Nullable
    public static Long finishSleep(LevelAccessor level, long newTime) {
        return DimensionTimeHooks.finishSleep(level, newTime);
    }

    /**
     * Checks whether it is eternal day in the Aether.
     *
     * @param player The {@link Player}.
     * @return Whether it is eternal day, as a {@link Boolean}.
     */
    public static boolean isEternalDay(Player player) {
        return DimensionTimeHooks.isEternalDay(player);
    }
}
