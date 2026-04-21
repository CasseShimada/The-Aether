package com.aetherteam.aether.event.hooks;

import com.aetherteam.aether.AetherConfig;
import com.aetherteam.aether.AetherTags;
import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.attachment.AetherPlayerAttachment;
import com.aetherteam.aether.data.resources.registries.AetherDimensions;
import com.aetherteam.aether.world.LevelUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Set;

public class DimensionHooks {
    public static boolean playerLeavingAether;
    public static boolean displayAetherTravel;
    public static int teleportationTimer;

    /**
     * Spawns the player in the Aether dimension if the {@link AetherConfig.Server#spawn_in_aether} config is enabled.
     *
     * @param player The {@link Player}.
     * @see com.aetherteam.aether.event.listeners.DimensionListener#onPlayerLogin(PlayerEvent.PlayerLoggedInEvent)
     */
    public static void startInAether(Player player) {
        var aetherPlayer = player.getAttachedOrCreate(AetherDataAttachments.AETHER_PLAYER);
        if (AetherConfig.SERVER.spawn_in_aether.get()) {
            if (aetherPlayer.canSpawnInAether()) { // Checks if the player has been set to spawn in the Aether.
                if (player instanceof ServerPlayer serverPlayer) {
                    MinecraftServer server = serverPlayer.level().getServer();
                    if (server != null) {
                        ServerLevel aetherLevel = server.getLevel(AetherDimensions.AETHER_LEVEL);
                        if (aetherLevel != null && serverPlayer.level().dimension() == Level.OVERWORLD) {
                            TeleportTransition transition = new TeleportTransition(aetherLevel, checkPositionsForInitialSpawn(aetherLevel, serverPlayer.blockPosition()).getCenter(), Vec3.ZERO, serverPlayer.getYRot(), serverPlayer.getXRot(), Set.of(), TeleportTransition.DO_NOTHING);
                            if (serverPlayer.teleport(transition) != null) {
                                serverPlayer.setRespawnPosition(new ServerPlayer.RespawnConfig(LevelData.RespawnData.of(AetherDimensions.AETHER_LEVEL, serverPlayer.blockPosition(), serverPlayer.getYRot(), serverPlayer.getXRot()), true), false);
                                aetherPlayer.setCanSpawnInAether(false); // Sets that the player has already spawned in the Aether.
                            }
                        }
                    }
                }
            }
        } else {
            aetherPlayer.setCanSpawnInAether(false);
        }
    }

    private static BlockPos checkPositionsForInitialSpawn(Level level, BlockPos origin) {
        if (!isSafe(level, origin)) {
            for (int i = 0; i <= 750; i += 5) {
                for (Direction facing : Direction.Plane.HORIZONTAL) {
                    BlockPos offsetPosition = origin.offset(facing.getUnitVec3i().multiply(i));
                    if (isSafeAround(level, offsetPosition)) {
                        return offsetPosition;
                    }
                    BlockPos heightmapPosition = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, offsetPosition);
                    if (isSafeAround(level, heightmapPosition)) {
                        return heightmapPosition;
                    }
                }
            }
        }
        return origin;
    }

    public static boolean isSafeAround(Level level, BlockPos pos) {
        BlockPos belowPos = pos.below();
        if (!isSafe(level, belowPos)) {
            return false;
        }
        for (Direction facing : Direction.Plane.HORIZONTAL) {
            if (!isSafe(level, belowPos.relative(facing, 2))) {
                return false;
            }
        }
        return true;
    }

    private static boolean isSafe(Level level, BlockPos pos) {
        return level.getWorldBorder().isWithinBounds(pos) && level.getBlockState(pos).is(AetherTags.Blocks.AETHER_DIRT) && level.getBlockState(pos.above()).isAir() && level.getBlockState(pos.above(2)).isAir();
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
     * @see com.aetherteam.aether.event.listeners.DimensionListener#onInteractWithPortalFrame(PlayerInteractEvent.RightClickBlock)
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
     * @see com.aetherteam.aether.event.listeners.DimensionListener#onWaterExistsInsidePortalFrame(BlockEvent.NeighborNotifyEvent)
     */
    public static boolean detectWaterInFrame(LevelAccessor levelAccessor, BlockPos pos, BlockState blockState, FluidState fluidState) {
        return DimensionPortalHooks.detectWaterInFrame(levelAccessor, pos, blockState, fluidState);
    }

    /**
     * Ticks time in dimensions with the Aether effects location.
     *
     * @param level The {@link Level}
     * @see com.aetherteam.aether.event.listeners.DimensionListener#onWorldTick(LevelTickEvent.Post)
     */
    public static void tickTime(Level level) {
        DimensionTimeHooks.tickTime(level);
    }

    /**
     * Checks whether eternal day is configured to be disabled, and disables it in the {@link AetherPlayerAttachment}.
     *
     * @param level The {@link Level}
     * @see com.aetherteam.aether.event.listeners.DimensionListener#onWorldTick(LevelTickEvent.Post)
     */
    public static void checkEternalDayConfig(Level level) {
        DimensionTimeHooks.checkEternalDayConfig(level);
    }

    /**
     * @param entity    The {@link Entity} travelling between dimensions.
     * @param dimension The {@link ResourceKey} of the dimension ({@link Level}) being teleported to.
     * @see com.aetherteam.aether.event.listeners.DimensionListener#onEntityTravelToDimension(EntityTravelToDimensionEvent)
     */
    public static void dimensionTravel(Entity entity, ResourceKey<Level> dimension) {
        DimensionTravelHooks.dimensionTravel(entity, dimension);
    }

    /**
     * @param entity The {@link Entity} travelling between dimensions.
     * @see com.aetherteam.aether.event.listeners.DimensionListener#onEntityTravelToDimension(EntityTravelToDimensionEvent)
     */
    public static void removePlayerAerbunny(Entity entity) {
        DimensionTravelHooks.removePlayerAerbunny(entity);
    }

    /**
     * @param player The {@link Player} travelling between dimensions.
     * @see com.aetherteam.aether.event.listeners.DimensionListener#onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent)
     */
    public static void remountPlayerAerbunny(Player player) {
        DimensionTravelHooks.remountPlayerAerbunny(player);
    }

    /**
     * Checks if the player was falling out of the Aether, and prevents server fly-hack checks during this.
     *
     * @param player The {@link Player}.
     * @see com.aetherteam.aether.event.listeners.DimensionListener#onPlayerTraveling(PlayerTickEvent.Post)
     */
    public static void travelling(Player player) {
        DimensionTravelHooks.travelling(player);
    }

    /**
     * Initializes the Aether level data for time separate from the overworld.
     * serverLevelData and levelData are access transformed.
     *
     * @param level The {@link LevelAccessor}.
     * @see com.aetherteam.aether.event.listeners.DimensionListener#onPlayerTraveling(PlayerTickEvent.Post)
     */
    public static void initializeLevelData(LevelAccessor level) {
        DimensionTimeHooks.initializeLevelData(level);
    }

    /**
     * Resets the weather cycle if players finish sleeping in an Aether dimension.<br>
     * Sets the time in the Aether according to the Aether's day/night cycle.
     *
     * @param level The {@link LevelAccessor}.
     * @see com.aetherteam.aether.event.listeners.DimensionListener#onSleepFinish(SleepFinishedTimeEvent)
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
     * @see com.aetherteam.aether.event.listeners.DimensionListener#onTriedToSleep(CanPlayerSleepEvent)
     */
    public static boolean isEternalDay(Player player) {
        return DimensionTimeHooks.isEternalDay(player);
    }
}
