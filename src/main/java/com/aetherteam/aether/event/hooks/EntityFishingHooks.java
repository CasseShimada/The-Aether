package com.aetherteam.aether.event.hooks;

import com.aetherteam.aether.AetherTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public final class EntityFishingHooks {
    private EntityFishingHooks() {
    }

    /**
     * Prevents an entity from being hooked with a Fishing Rod.
     *
     * @param projectileEntity The hook projectile {@link Entity}.
     * @param rayTraceResult   The {@link HitResult} of the projectile.
     * @return Whether to prevent the hook interaction, as a {@link Boolean}.
     */
    public static boolean preventEntityHooked(Entity projectileEntity, HitResult rayTraceResult) {
        if (rayTraceResult instanceof EntityHitResult entityHitResult) {
            return entityHitResult.getEntity().getType().builtInRegistryHolder().is(AetherTags.Entities.UNHOOKABLE) && projectileEntity instanceof FishingHook;
        }
        return false;
    }
}
