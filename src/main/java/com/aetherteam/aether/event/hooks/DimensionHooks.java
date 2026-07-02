package com.aetherteam.aether.event.hooks;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

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

}
