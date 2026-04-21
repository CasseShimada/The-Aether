package com.aetherteam.aether.event.hooks;

import com.aetherteam.aether.AetherConfig;
import com.aetherteam.aether.AetherTags;
import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.attachment.AetherPlayerAttachment;
import com.aetherteam.aether.attachment.AetherTimeAttachment;
import com.aetherteam.aether.block.portal.AetherPortalShape;
import com.aetherteam.aether.data.resources.registries.AetherDimensions;
import com.aetherteam.aether.mixin.mixins.common.accessor.ServerGamePacketListenerImplAccessor;
import com.aetherteam.aether.mixin.mixins.common.accessor.ServerLevelAccessor;
import com.aetherteam.aether.network.packet.clientbound.AetherTravelPacket;
import com.aetherteam.aether.network.packet.clientbound.LeavingAetherPacket;
import com.aetherteam.aether.network.packet.clientbound.PortalInteractPacket;
import com.aetherteam.aether.util.LevelTimeUtil;
import com.aetherteam.aether.world.AetherLevelData;
import com.aetherteam.aether.world.LevelUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;
import net.fabricmc.loader.api.FabricLoader;
import com.aetherteam.aether.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.Optional;
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
        if (level.isClientSide() || shouldDeferToImmersivePortals() || direction == null || !canCreatePortal(level, stack)) {
            return false;
        }

        Optional<AetherPortalShape> optional = AetherPortalShape.findEmptyAetherPortalShape(level, pos.relative(direction), Direction.Axis.X);
        if (optional.isEmpty()) {
            return false;
        }

        PacketDistributor.sendToAllPlayers(new PortalInteractPacket(player.getId(), hand == InteractionHand.MAIN_HAND));
        optional.get().createPortalBlocks();
        consumePortalActivationItem(player, stack, hand);
        return true;
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
        if (!(levelAccessor instanceof Level level)) {
            return false;
        }
        if (level.isClientSide() || shouldDeferToImmersivePortals()) {
            return false;
        }
        if (!fluidState.is(Fluids.WATER) || fluidState.createLegacyBlock().getBlock() != blockState.getBlock()) {
            return false;
        }
        if (!isPortalDimension(level) || AetherConfig.SERVER.disable_aether_portal.get()) {
            return false;
        }

        Optional<AetherPortalShape> optional = AetherPortalShape.findEmptyAetherPortalShape(level, pos, Direction.Axis.X);
        if (optional.isEmpty()) {
            return false;
        }

        optional.get().createPortalBlocks();
        return true;
    }

    /**
     * Ticks time in dimensions with the Aether effects location.
     *
     * @param level The {@link Level}
     * @see com.aetherteam.aether.event.listeners.DimensionListener#onWorldTick(LevelTickEvent.Post)
     */
    public static void tickTime(Level level) {
        if (level.dimension().equals(AetherDimensions.AETHER_LEVEL) && level instanceof ServerLevel serverLevel) {
            ServerLevelAccessor serverLevelAccessor = (ServerLevelAccessor) serverLevel;
            com.aetherteam.aether.mixin.mixins.common.accessor.LevelAccessor levelAccessor = (com.aetherteam.aether.mixin.mixins.common.accessor.LevelAccessor) level;
            long i = levelAccessor.aether$getLevelData().getGameTime() + 1L;
            serverLevelAccessor.aether$getServerLevelData().setGameTime(i);
            if (serverLevel.getGameRules().get(GameRules.ADVANCE_TIME)) {
                LevelTimeUtil.setTime(serverLevel, serverLevel.getAttachedOrCreate(AetherDataAttachments.AETHER_TIME).tickTime(level));
            }

            EntityHooks.tickAetherSkySpawns(serverLevel);
        }
    }

    /**
     * Checks whether eternal day is configured to be disabled, and disables it in the {@link AetherPlayerAttachment}.
     *
     * @param level The {@link Level}
     * @see com.aetherteam.aether.event.listeners.DimensionListener#onWorldTick(LevelTickEvent.Post)
     */
    public static void checkEternalDayConfig(Level level) {
        if (!level.isClientSide() && level.hasAttached(AetherDataAttachments.AETHER_TIME)) {
            var aetherTime = level.getAttachedOrCreate(AetherDataAttachments.AETHER_TIME);
            boolean eternalDay = aetherTime.isEternalDay();
            if (AetherConfig.SERVER.disable_eternal_day.get() && eternalDay) {
                aetherTime.setEternalDay(false);
                aetherTime.updateEternalDay(level);
            }
        }
    }

    /**
     * @param entity    The {@link Entity} travelling between dimensions.
     * @param dimension The {@link ResourceKey} of the dimension ({@link Level}) being teleported to.
     * @see com.aetherteam.aether.event.listeners.DimensionListener#onEntityTravelToDimension(EntityTravelToDimensionEvent)
     */
    public static void dimensionTravel(Entity entity, ResourceKey<Level> dimension) {
        if (!(entity instanceof Player player) || player.level().isClientSide()) {
            return;
        }

        var aetherPlayer = player.getAttachedOrCreate(AetherDataAttachments.AETHER_PLAYER);
        if (AetherConfig.SERVER.spawn_in_aether.get() && aetherPlayer.canSpawnInAether()) {
            return;
        }
        if (!entity.level().getBiome(entity.blockPosition()).is(AetherTags.Biomes.DISPLAY_TRAVEL_TEXT)) {
            return;
        }

        if (entity.level().dimension() == LevelUtil.destinationDimension() && dimension == LevelUtil.returnDimension()) {
            updateTravelDisplay(true, true);
            return;
        }
        if (entity.level().dimension() == LevelUtil.returnDimension() && dimension == LevelUtil.destinationDimension()) {
            updateTravelDisplay(true, false);
            return;
        }

        updateTravelDisplay(false, playerLeavingAether);
    }

    /**
     * @param entity The {@link Entity} travelling between dimensions.
     * @see com.aetherteam.aether.event.listeners.DimensionListener#onEntityTravelToDimension(EntityTravelToDimensionEvent)
     */
    public static void removePlayerAerbunny(Entity entity) {
        if (entity instanceof Player player) {
            player.getAttachedOrCreate(AetherDataAttachments.AETHER_PLAYER).removeAerbunny();
        }
    }

    /**
     * @param player The {@link Player} travelling between dimensions.
     * @see com.aetherteam.aether.event.listeners.DimensionListener#onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent)
     */
    public static void remountPlayerAerbunny(Player player) {
        player.getAttachedOrCreate(AetherDataAttachments.AETHER_PLAYER).remountAerbunny(player);
    }

    /**
     * Checks if the player was falling out of the Aether, and prevents server fly-hack checks during this.
     *
     * @param player The {@link Player}.
     * @see com.aetherteam.aether.event.listeners.DimensionListener#onPlayerTraveling(PlayerTickEvent.Post)
     */
    public static void travelling(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            if (teleportationTimer > 0) { // Prevents the player from being kicked for flying.
                ServerGamePacketListenerImplAccessor serverGamePacketListenerImplAccessor = (ServerGamePacketListenerImplAccessor) serverPlayer.connection;
                serverGamePacketListenerImplAccessor.aether$setAboveGroundTickCount(0);
                serverGamePacketListenerImplAccessor.aether$setAboveGroundVehicleTickCount(0);
                teleportationTimer--;
            }
            if (teleportationTimer < 0 || serverPlayer.verticalCollisionBelow) {
                teleportationTimer = 0;
            }
        }
    }

    /**
     * Initializes the Aether level data for time separate from the overworld.
     * serverLevelData and levelData are access transformed.
     *
     * @param level The {@link LevelAccessor}.
     * @see com.aetherteam.aether.event.listeners.DimensionListener#onPlayerTraveling(PlayerTickEvent.Post)
     */
    public static void initializeLevelData(LevelAccessor level) {
        if (level instanceof ServerLevel serverLevel && serverLevel.dimension().equals(AetherDimensions.AETHER_LEVEL)) {
            AetherLevelData levelData = new AetherLevelData(serverLevel, serverLevel.getServer().getWorldData(), serverLevel.getServer().getWorldData().overworldData(), serverLevel.getAttachedOrCreate(AetherDataAttachments.AETHER_TIME).getDayTime());
            ServerLevelAccessor serverLevelAccessor = (ServerLevelAccessor) serverLevel;
            com.aetherteam.aether.mixin.mixins.common.accessor.LevelAccessor levelAccessor = (com.aetherteam.aether.mixin.mixins.common.accessor.LevelAccessor) level;
            serverLevelAccessor.aether$setServerLevelData(levelData);
            levelAccessor.aether$setLevelData(levelData);
        }
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
        if (level instanceof ServerLevel serverLevel && serverLevel.dimension().equals(AetherDimensions.AETHER_LEVEL)) {
            serverLevel.getWeatherData().setRainTime(0);
            serverLevel.getWeatherData().setRaining(false);
            serverLevel.getWeatherData().setThunderTime(0);
            serverLevel.getWeatherData().setThundering(false);

            long time = newTime + (24000L * AetherTimeAttachment.getTicksPerDayMultiplier());
            return time - time % (long) AetherTimeAttachment.getTicksPerDay();
        }
        return null;
    }

    /**
     * Checks whether it is eternal day in the Aether.
     *
     * @param player The {@link Player}.
     * @return Whether it is eternal day, as a {@link Boolean}.
     * @see com.aetherteam.aether.event.listeners.DimensionListener#onTriedToSleep(CanPlayerSleepEvent)
     */
    public static boolean isEternalDay(Player player) {
        if (player.level().dimension().equals(AetherDimensions.AETHER_LEVEL)) {
            return player.level().getAttachedOrCreate(AetherDataAttachments.AETHER_TIME).isEternalDay();
        }
        return false;
    }

    private static boolean shouldDeferToImmersivePortals() {
        return FabricLoader.getInstance().isModLoaded("immersive_portals_core")
                && AetherConfig.COMMON.enable_immersive_portals_compatibility.get();
    }

    private static boolean canCreatePortal(Level level, ItemStack stack) {
        return stack.is(AetherTags.Items.AETHER_PORTAL_ACTIVATION_ITEMS) && isPortalDimension(level);
    }

    private static boolean isPortalDimension(Level level) {
        return level.dimension() == LevelUtil.returnDimension() || level.dimension() == LevelUtil.destinationDimension();
    }

    private static void consumePortalActivationItem(Player player, ItemStack stack, InteractionHand hand) {
        if (player.isCreative()) {
            return;
        }

        ItemStack craftingRemainder = stack.getItem().getCraftingRemainder().create();
        if (stack.getCount() > 1) {
            stack.shrink(1);
            if (!craftingRemainder.isEmpty()) {
                player.addItem(craftingRemainder);
            }
            return;
        }
        if (stack.isDamageableItem()) {
            stack.hurtAndBreak(1, player, hand);
            return;
        }

        player.setItemInHand(hand, craftingRemainder.isEmpty() ? ItemStack.EMPTY : craftingRemainder);
    }

    private static void updateTravelDisplay(boolean visible, boolean leavingAether) {
        displayAetherTravel = visible;
        if (visible) {
            playerLeavingAether = leavingAether;
        }
        PacketDistributor.sendToAllPlayers(new AetherTravelPacket(visible));
        if (visible) {
            PacketDistributor.sendToAllPlayers(new LeavingAetherPacket(leavingAether));
        }
    }
}
