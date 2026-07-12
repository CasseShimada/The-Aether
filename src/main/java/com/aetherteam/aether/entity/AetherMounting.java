package com.aetherteam.aether.entity;

import com.aetherteam.aether.block.AetherBlocks;
import com.aetherteam.aether.entity.monster.Swet;
import com.aetherteam.aether.entity.passive.MountableAnimal;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public final class AetherMounting {
    private AetherMounting() {
    }

    /**
     * Tracks a successfully started ride.
     *
     * @param mount   The mounted {@link Entity}.
     * @param mounted Whether the rider successfully mounted, as a {@link Boolean}.
     */
    public static void handleMountStart(Entity mount, boolean mounted) {
        if (mounted) {
            trackMount(mount, false);
        }
    }

    /**
     * Handles dismount prevention and tracks an allowed dismount.
     *
     * @param rider The {@link Entity} trying to dismount.
     * @return Whether the dismount should be prevented, as a {@link Boolean}.
     */
    public static boolean handleDismount(Entity rider) {
        Entity mount = rider.getVehicle();
        if (mount != null) {
            if (dismountPrevention(rider, mount, true)) {
                return true;
            }
            trackMount(mount, true);
        }
        return false;
    }

    /**
     * Prevents dismounting Aether mounts in the air, and Swets when consumed.
     *
     * @param rider       The {@link Entity} riding the mount.
     * @param mount       The mounted {@link Entity}.
     * @param dismounting Whether the rider is trying to dismount, as a {@link Boolean}.
     * @return Whether to prevent the rider from dismounting, as a {@link Boolean}.
     */
    public static boolean dismountPrevention(Entity rider, Entity mount, boolean dismounting) {
        if (dismounting && rider.isShiftKeyDown()) {
            return (mount instanceof MountableAnimal && !mount.onGround() && !mount.isInLiquid() && !mount.isPassenger()) || (mount instanceof Swet swet && !swet.isFriendly());
        }
        return false;
    }

    /**
     * Tracks whether a passenger has mounted or dismounted a {@link MountableAnimal}.
     *
     * @param mount       The mounted {@link Entity}.
     * @param dismounting Whether the rider is trying to dismount, as a {@link Boolean}.
     */
    public static void trackMount(Entity mount, boolean dismounting) {
        if (mount instanceof MountableAnimal mountableAnimal) {
            mountableAnimal.setHasPassenger(!dismounting);
        }
    }

    /**
     * Launches a mount when it interacts with a blue aercloud. This is handled as an event to get around a vanilla bug with it not working from the {@link com.aetherteam.aether.block.natural.BlueAercloudBlock} class.
     *
     * @param player The passenger {@link Player}.
     */
    public static void launchMount(Player player) {
        Entity mount = player.getVehicle();
        if (player.isPassenger() && mount != null) {
            if (mount.level().getBlockStates(mount.getBoundingBox()).anyMatch((state) -> state.is(AetherBlocks.BLUE_AERCLOUD))) {
                if (player.level().isClientSide()) {
                    mount.setDeltaMovement(mount.getDeltaMovement().x(), 2.0, mount.getDeltaMovement().z());
                }
            }
        }
    }
}
