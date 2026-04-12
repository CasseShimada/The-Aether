package com.aetherteam.aether.item.accessories.abilities;

import com.aetherteam.aether.AetherTags;
import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.attachment.AetherPlayerAttachment;
import com.aetherteam.aether.item.AetherItems;
import com.aetherteam.aether.item.EquipmentUtil;
import com.aetherteam.nitrogen.ConstantsUtil;
import com.aetherteam.aether.accessories.api.AccessoriesAPI;
import com.aetherteam.aether.accessories.api.slot.SlotEntryReference;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Input;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public interface ShieldOfRepulsionAccessory {
    /**
     * Deflects a projectile when it hits an entity wearing a Shield of Repulsion.
     *
     * @return {@code true} if the projectile was deflected and the hit should be cancelled.
     */
    static boolean deflectProjectile(HitResult hitResult, Projectile projectile) {
        if (hitResult.getType() == HitResult.Type.ENTITY && hitResult instanceof EntityHitResult entityHitResult) {
            if (entityHitResult.getEntity() instanceof LivingEntity impactedLiving) {
                if (projectile.getType().builtInRegistryHolder().is(AetherTags.Entities.DEFLECTABLE_PROJECTILES)) {
                    SlotEntryReference slotResult = EquipmentUtil.getAccessory(impactedLiving, AetherItems.SHIELD_OF_REPULSION.get());
                    if (slotResult != null) {
                        Vec3 motion = impactedLiving.getDeltaMovement();
                        if (impactedLiving instanceof Player player) {
                            var data = player.getAttachedOrCreate(AetherDataAttachments.AETHER_PLAYER);
                            if (!data.isMoving() || (data.isMoving() && motion.x() == 0.0 && (motion.y() == ConstantsUtil.DEFAULT_DELTA_MOVEMENT_Y || motion.y() == 0.0) && motion.z() == 0.0)) {
                                if (player.level().isClientSide()) { // Values used by the Shield of Repulsion screen overlay vignette.
                                    data.setProjectileImpactedMaximum(150);
                                    data.setProjectileImpactedTimer(150);
                                }
                                return handleDeflection(projectile, player, slotResult);
                            }
                        } else {
                            if (motion.x() == 0.0 && (motion.y() == ConstantsUtil.DEFAULT_DELTA_MOVEMENT_Y || motion.y() == 0.0) && motion.z() == 0.0) {
                                return handleDeflection(projectile, impactedLiving, slotResult);
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Handles the event cancellation and the projectile deflection by scaling the projectile's motion by 0.25, and reversing its direction with negative scaling. This scaling is also applied to the projectile's power if its class has power values (which is necessary for certain projectiles like Ghast Fireballs).<br><br>
     * Each deflection takes 1 durability off of the Shield of Repulsion.
     *
     * @param projectile     The impacting {@link Projectile}.
     * @param impactedLiving The impacted {@link LivingEntity}.
     * @param slotResult     The {@link SlotEntryReference} of the Shield of Repulsion.
     */
    private static boolean handleDeflection(Projectile projectile, LivingEntity impactedLiving, SlotEntryReference slotResult) {
        if (!impactedLiving.equals(projectile.getOwner())) {
            var owner = projectile.getOwner();
            projectile.deflect(ProjectileDeflection.REVERSE, impactedLiving, owner != null ? EntityReference.of(owner) : EntityReference.of(projectile), false);
            projectile.setDeltaMovement(projectile.getDeltaMovement().scale(0.25));
            if (impactedLiving.level() instanceof ServerLevel serverLevel) {
                if (impactedLiving instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                    slotResult.stack().hurtAndBreak(1, serverLevel, serverPlayer, (item) -> AccessoriesAPI.breakStack(slotResult.reference()));
                } else {
                    var stack = slotResult.stack();
                    stack.hurtAndBreak(1, impactedLiving, EquipmentSlot.MAINHAND);
                    if (stack.isEmpty()) {
                        slotResult.reference().setStack(net.minecraft.world.item.ItemStack.EMPTY);
                    }
                }
            }
            return true;
        }
        return false;
    }
}
