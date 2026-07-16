package com.aetherteam.aether.item.combat.abilities.weapon;

import com.aetherteam.aether.AetherConfig;
import com.aetherteam.aether.AetherTags;
import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.attachment.AetherPlayerAttachment;
import com.aetherteam.aether.attachment.LightningTrackerAttachment;
import com.aetherteam.aether.attachment.PhoenixArrowAttachment;
import com.aetherteam.aether.entity.projectile.PoisonNeedle;
import com.aetherteam.aether.entity.projectile.dart.EnchantedDart;
import com.aetherteam.aether.entity.projectile.dart.GoldenDart;
import com.aetherteam.aether.entity.projectile.dart.PoisonDart;
import com.google.common.util.concurrent.AtomicDouble;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import javax.annotation.Nullable;

public final class WeaponAbilities {
    private WeaponAbilities() {
    }

    /**
     * Renders Darts that hit the player as stuck on their model, similar to Arrows.<br><br>
     * This is done through increasing values stored in {@link AetherPlayerAttachment} that track the amount of different darts stuck in the player.
     *
     * @param entity The hurt {@link LivingEntity}.
     * @param source The {@link DamageSource} that hurt the entity.
     */
    public static void stickDart(LivingEntity entity, DamageSource source) {
        if (entity instanceof Player player && !player.level().isClientSide()) {
            Entity sourceEntity = source.getDirectEntity();
            var data = player.getAttachedOrCreate(AetherDataAttachments.AETHER_PLAYER);
            if (sourceEntity instanceof GoldenDart) {
                data.setSyncedToClients(player, AetherPlayerAttachment.GOLDEN_DART_COUNT_SYNC_KEY, data.getGoldenDartCount() + 1);
            } else if (sourceEntity instanceof PoisonDart || sourceEntity instanceof PoisonNeedle) {
                data.setSyncedToClients(player, AetherPlayerAttachment.POISON_DART_COUNT_SYNC_KEY, data.getPoisonDartCount() + 1);
            } else if (sourceEntity instanceof EnchantedDart) {
                data.setSyncedToClients(player, AetherPlayerAttachment.ENCHANTED_DART_COUNT_SYNC_KEY, data.getEnchantedDartCount() + 1);
            }
        }
    }

    /**
     * Sets the hit entity on fire for the amount of seconds the Phoenix Arrow has stored, as determined by {@link com.aetherteam.aether.item.combat.loot.PhoenixBowItem#customArrow(AbstractArrow, ItemStack, ItemStack)}.
     *
     * @param result     The {@link HitResult} of the projectile.
     * @param projectile The {@link Projectile} that hit something.
     */
    public static void phoenixArrowHit(HitResult result, Projectile projectile) {
        if (result instanceof EntityHitResult entityHitResult && projectile instanceof AbstractArrow abstractArrow) {
            Entity impactedEntity = entityHitResult.getEntity();
            if (impactedEntity.getType() == EntityTypes.ENDERMAN) {
                return;
            }
            if (abstractArrow.hasAttached(AetherDataAttachments.PHOENIX_ARROW)) {
                var data = abstractArrow.getAttachedOrCreate(AetherDataAttachments.PHOENIX_ARROW);
                if (data.isPhoenixArrow() && data.getFireTime() > 0) {
                    impactedEntity.igniteForSeconds(data.getFireTime());
                }
            }
        }
    }

    public static void tickPhoenixArrow(AbstractArrow arrow, boolean inGround, int inGroundTime) {
        if (!arrow.hasAttached(AetherDataAttachments.PHOENIX_ARROW)) {
            return;
        }

        var attachment = arrow.getAttachedOrCreate(AetherDataAttachments.PHOENIX_ARROW);
        if (!attachment.isPhoenixArrow() || arrow.level().isClientSide()) {
            return;
        }

        attachment.setSyncedToClients(arrow, PhoenixArrowAttachment.PHOENIX_ARROW_SYNC_KEY, true);
        if (inGround) {
            if (inGroundTime % 5 == 0) {
                spawnPhoenixArrowParticle(arrow);
            }
        } else {
            for (int i = 0; i < 2; i++) {
                spawnPhoenixArrowParticle(arrow);
            }
        }
    }

    private static void spawnPhoenixArrowParticle(AbstractArrow arrow) {
        if (arrow.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.FLAME,
                    arrow.getX() + (serverLevel.getRandom().nextGaussian() / 5.0),
                    arrow.getY() + (serverLevel.getRandom().nextGaussian() / 3.0),
                    arrow.getZ() + (serverLevel.getRandom().nextGaussian() / 5.0),
                    1, 0.0, 0.0, 0.0, 0.0F);
        }
    }

    /**
     * Prevents an entity from being hurt by a lightning strike if {@link LightningTrackerAttachment#getOwner(Level)} finds an owner associated with the lightning, if it was summoned through usage of a weapon.
     *
     * @param entity    The {@link Entity} struck by the lightning bolt.
     * @param lightning The {@link LightningBolt} that struck the entity.
     * @return Whether the entity being hurt by the lightning strike should be prevented, as a {@link Boolean}.
     */
    public static boolean lightningTracking(Entity entity, LightningBolt lightning) {
        if (entity instanceof LivingEntity livingEntity) {
            if (lightning.hasAttached(AetherDataAttachments.LIGHTNING_TRACKER)) {
                var tracker = lightning.getAttachedOrCreate(AetherDataAttachments.LIGHTNING_TRACKER);
                Entity owner = tracker.getOwner(lightning.level());
                if (owner != null) {
                    return livingEntity == owner || livingEntity == owner.getVehicle() || owner.getPassengers().contains(livingEntity);
                }
            }
        }
        return false;
    }

    /**
     * Reduces the effectiveness of non-Aether weapons against Aether mobs.
     *
     * @param target The target {@link LivingEntity} being attacked.
     * @param source The attacking {@link Entity}.
     * @param damage The original damage as a {@link Float}.
     * @return The modified damage as a {@link Float}.
     */
    public static float reduceWeaponEffectiveness(LivingEntity target, Entity source, float damage) {
        if (AetherConfig.SERVER.tools_debuff.get() && !target.level().isClientSide()) {
            double pow = Math.max(Math.pow(damage, damage > 1.0 ? 0.6 : 1.6), 1.0);
            if (source instanceof LivingEntity livingEntity) {
                ItemStack stack = livingEntity.getMainHandItem();
                if ((target.getType().getDescriptionId().startsWith("entity.aether") || target.getType().builtInRegistryHolder().is(AetherTags.Entities.TREATED_AS_AETHER_ENTITY)) && !target.getType().builtInRegistryHolder().is(AetherTags.Entities.TREATED_AS_VANILLA_ENTITY)) {
                    if (!stack.isEmpty()) {
                        AtomicDouble value = new AtomicDouble();
                        stack.forEachModifier(EquipmentSlotGroup.MAINHAND, (attribute, modifier, display) -> {
                            if (attribute.is(Attributes.ATTACK_DAMAGE)) {
                                value.set(value.get() + modifier.amount());
                            }
                        });
                        if (livingEntity.getAttribute(Attributes.ATTACK_DAMAGE) != null && value.get() > livingEntity.getAttributeBaseValue(Attributes.ATTACK_DAMAGE) && !stack.getItem().getDescriptionId().startsWith("item.aether.") && !stack.is(AetherTags.Items.TREATED_AS_AETHER_ITEM)) {
                            damage = (float) pow;
                        }
                    }
                }
            } else if (source instanceof Projectile) {
                if ((target.getType().getDescriptionId().startsWith("entity.aether") || target.getType().builtInRegistryHolder().is(AetherTags.Entities.TREATED_AS_AETHER_ENTITY)) && !target.getType().builtInRegistryHolder().is(AetherTags.Entities.TREATED_AS_VANILLA_ENTITY)) {
                    if ((!source.getType().getDescriptionId().startsWith("entity.aether") && !source.getType().builtInRegistryHolder().is(AetherTags.Entities.TREATED_AS_AETHER_ENTITY))
                            && (!(source instanceof AbstractArrow abstractArrow) || !abstractArrow.hasAttached(AetherDataAttachments.PHOENIX_ARROW) || !abstractArrow.getAttachedOrCreate(AetherDataAttachments.PHOENIX_ARROW).isPhoenixArrow())) {
                        damage = (float) pow;
                    }
                }
            }
        }
        return damage;
    }

    /**
     * Reduces the effectiveness of non-Aether armor against Aether mobs.
     *
     * @param target The target {@link LivingEntity} wearing the armor.
     * @param source The attacking {@link Entity}.
     * @param damage The original damage as a {@link Float}.
     * @return The modified damage as a {@link Float}.
     */
    public static float reduceArmorEffectiveness(LivingEntity target, @Nullable Entity source, float damage) {
        if (source != null) {
            if ((source.getType().getDescriptionId().startsWith("entity.aether") || source.getType().builtInRegistryHolder().is(AetherTags.Entities.TREATED_AS_AETHER_ENTITY) && !source.getType().builtInRegistryHolder().is(AetherTags.Entities.TREATED_AS_VANILLA_ENTITY))) {
                for (EquipmentSlot equipmentSlot : EquipmentSlot.VALUES) {
                    if (!equipmentSlot.isArmor()) {
                        continue;
                    }
                    ItemStack stack = target.getItemBySlot(equipmentSlot);
                    if (stack.getItem() instanceof ArmorItem && !stack.getItem().getDescriptionId().startsWith("item.aether.") && !stack.is(AetherTags.Items.TREATED_AS_AETHER_ITEM)) {
                        AtomicDouble value = new AtomicDouble();
                        stack.forEachModifier(equipmentSlot, (attribute, modifier) -> {
                            if (attribute.is(Attributes.ARMOR)) {
                                value.set(value.get() + (modifier.amount() / 15));
                            }
                        });
                        damage += (float) value.get();
                    }
                }
            }
        }
        return damage;
    }
}
