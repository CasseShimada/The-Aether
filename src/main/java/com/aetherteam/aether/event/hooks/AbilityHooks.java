package com.aetherteam.aether.event.hooks;

import com.aetherteam.aether.AetherConfig;
import com.aetherteam.aether.AetherTags;
import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.attachment.AetherPlayerAttachment;
import com.aetherteam.aether.attachment.LightningTrackerAttachment;
import com.aetherteam.aether.entity.projectile.PoisonNeedle;
import com.aetherteam.aether.entity.projectile.dart.EnchantedDart;
import com.aetherteam.aether.entity.projectile.dart.GoldenDart;
import com.aetherteam.aether.entity.projectile.dart.PoisonDart;
import com.aetherteam.aether.item.EquipmentUtil;
import com.aetherteam.aether.item.accessories.abilities.ZaniteAccessory;
import com.aetherteam.nitrogen.attachment.INBTSynchable;
import com.google.common.util.concurrent.AtomicDouble;
import com.aetherteam.aether.accessories.api.AccessoriesAPI;
import com.aetherteam.aether.accessories.api.slot.SlotEntryReference;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.List;

public class AbilityHooks {
    public static class AccessoryHooks {
        /**
         * Damages an entity's Gloves when they hurt another entity.
         *
         * @param player The attacking {@link Player}.
         * @see com.aetherteam.aether.mixin.mixins.common.PlayerMixin#attack(Entity, CallbackInfo)
         */
        public static void damageGloves(Player player) {
            SlotEntryReference slotResult = EquipmentUtil.getGloves(player);
            if (slotResult != null) {
                damageAccessoryItem(slotResult, player, 1);
            }
        }

        /**
         * Damages Zanite Rings when a block is broken.
         */
        public static void damageZaniteRing(LivingEntity entity, LevelAccessor level, BlockState state, BlockPos pos) {
            List<SlotEntryReference> slotResults = EquipmentUtil.getZaniteRings(entity);
            for (SlotEntryReference slotResult : slotResults) {
                if (slotResult != null) {
                    if (state.getDestroySpeed(level, pos) > 0 && entity.getRandom().nextInt(6) == 0) {
                        damageAccessoryItem(slotResult, entity, 1);
                    }
                }
            }
        }

        /**
         * Damages Zanite Pendant when a block is broken.
         */
        public static void damageZanitePendant(LivingEntity entity, LevelAccessor level, BlockState state, BlockPos pos) {
            SlotEntryReference slotResult = EquipmentUtil.getZanitePendant(entity);
            if (slotResult != null) {
                if (state.getDestroySpeed(level, pos) > 0 && entity.getRandom().nextInt(6) == 0) {
                    damageAccessoryItem(slotResult, entity, 1);
                }
            }
        }

        private static void damageAccessoryItem(SlotEntryReference slotResult, LivingEntity entity, int amount) {
            if (entity.level().isClientSide() || amount <= 0) {
                return;
            }

            if (entity.level() instanceof ServerLevel serverLevel && entity instanceof ServerPlayer serverPlayer) {
                slotResult.stack().hurtAndBreak(amount, serverLevel, serverPlayer, (item) -> AccessoriesAPI.breakStack(slotResult.reference()));
                return;
            }

            ItemStack stack = slotResult.stack();
            stack.hurtAndBreak(amount, entity, EquipmentSlot.MAINHAND);
            if (stack.isEmpty()) {
                slotResult.reference().setStack(ItemStack.EMPTY);
            }
        }

        /**
         * Handles ability for {@link ZaniteAccessory} for Zanite Rings (accounts for if multiple are equipped).
         * @see ZaniteAccessory#handleMiningSpeed(float, ItemStack)
         */
        public static float handleZaniteRingAbility(LivingEntity entity, float speed) {
            float newSpeed = speed;
            List<SlotEntryReference> slotResults = EquipmentUtil.getZaniteRings(entity);
            for (SlotEntryReference slotResult : slotResults) {
                if (slotResult != null) {
                    newSpeed = ZaniteAccessory.handleMiningSpeed(newSpeed, slotResult.stack());
                }
            }
            return newSpeed;
        }

        /**
         * Handles ability for {@link ZaniteAccessory} for the Zanite Pendant.
         * @see ZaniteAccessory#handleMiningSpeed(float, ItemStack)
         */
        public static float handleZanitePendantAbility(LivingEntity entity, float speed) {
            SlotEntryReference slotResult = EquipmentUtil.getZanitePendant(entity);
            if (slotResult != null) {
                speed = ZaniteAccessory.handleMiningSpeed(speed, slotResult.stack());
            }
            return speed;
        }

        /**
         * Checks whether an entity can be targeted while wearing an Invisibility Cloak.
         */
        public static boolean preventTargeting(LivingEntity target, @Nullable Entity lookingEntity) {
            if (target instanceof Player player) {
                var data = player.getAttachedOrCreate(AetherDataAttachments.AETHER_PLAYER);
                return lookingEntity != null
                        && !lookingEntity.getType().builtInRegistryHolder().is(AetherTags.Entities.IGNORE_INVISIBILITY)
                        && data.isWearingInvisibilityCloak()
                        && data.isInvisibilityEnabled()
                        && !data.attackedWithInvisibility();
            } else {
                return lookingEntity != null
                        && !lookingEntity.getType().builtInRegistryHolder().is(AetherTags.Entities.IGNORE_INVISIBILITY)
                        && EquipmentUtil.hasInvisibilityCloak(target);
            }
        }

        /**
         * Checks if an entity recently attacked while wearing an Invisibility Cloak.
         */
        public static boolean recentlyAttackedWithInvisibility(LivingEntity target, Entity lookingEntity) {
            if (target instanceof Player player) {
                var data = player.getAttachedOrCreate(AetherDataAttachments.AETHER_PLAYER);
                return !lookingEntity.getType().builtInRegistryHolder().is(AetherTags.Entities.IGNORE_INVISIBILITY)
                        && data.isWearingInvisibilityCloak()
                        && data.isInvisibilityEnabled()
                        && data.attackedWithInvisibility();
            } else {
                return false;
            }
        }

        /**
         * Sets that the player recently attacked.
         */
        public static void setAttack(DamageSource source) {
            if (source.getEntity() instanceof Player player) {
                player.getAttachedOrCreate(AetherDataAttachments.AETHER_PLAYER).setAttackedWithInvisibility(true);
            }
        }

        /**
         * Prevents magma block damage when wearing ice accessories.
         */
        public static boolean preventMagmaDamage(LivingEntity entity, DamageSource source) {
            return source == entity.level().damageSources().hotFloor() && EquipmentUtil.hasFreezingAccessory(entity);
        }
    }

    public static class WeaponHooks {
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
                    data.setSynched(player.getId(), INBTSynchable.Direction.CLIENT, "setGoldenDartCount", data.getGoldenDartCount() + 1);
                } else if (sourceEntity instanceof PoisonDart || sourceEntity instanceof PoisonNeedle) {
                    data.setSynched(player.getId(), INBTSynchable.Direction.CLIENT, "setPoisonDartCount", data.getPoisonDartCount() + 1);
                } else if (sourceEntity instanceof EnchantedDart) {
                    data.setSynched(player.getId(), INBTSynchable.Direction.CLIENT, "setEnchantedDartCount", data.getEnchantedDartCount() + 1);
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
            if (AetherConfig.SERVER.tools_debuff.get() && !target.level().isClientSide()) { // Checks if tool debuffs are enabled and if the level is on the server side.
                double pow = Math.max(Math.pow(damage, damage > 1.0 ? 0.6 : 1.6), 1.0);
                if (source instanceof LivingEntity livingEntity) {
                    ItemStack stack = livingEntity.getMainHandItem();
                    if ((target.getType().getDescriptionId().startsWith("entity.aether") || target.getType().builtInRegistryHolder().is(AetherTags.Entities.TREATED_AS_AETHER_ENTITY)) && !target.getType().builtInRegistryHolder().is(AetherTags.Entities.TREATED_AS_VANILLA_ENTITY)) { // Checks if the target is an Aether entity.
                        if (!stack.isEmpty()) {
                            AtomicDouble value = new AtomicDouble(); // Used for checking if the attack damage from the item is greater than the attacker's default (fist).
                            stack.forEachModifier(EquipmentSlotGroup.MAINHAND, (attribute, modifier, display) -> {
                                if (attribute.is(Attributes.ATTACK_DAMAGE)) {
                                    value.set(value.get() + modifier.amount());
                                }
                            });
                            if (livingEntity.getAttribute(Attributes.ATTACK_DAMAGE) != null && value.get() > livingEntity.getAttributeBaseValue(Attributes.ATTACK_DAMAGE) && !stack.getItem().getDescriptionId().startsWith("item.aether.") && !stack.is(AetherTags.Items.TREATED_AS_AETHER_ITEM)) { // Checks if the attacking item is non-Aether.
                                damage = (float) pow;
                            }
                        }
                    }
                } else if (source instanceof Projectile) { // Used for reducing projectile weapon effectiveness.
                    if ((target.getType().getDescriptionId().startsWith("entity.aether") || target.getType().builtInRegistryHolder().is(AetherTags.Entities.TREATED_AS_AETHER_ENTITY)) && !target.getType().builtInRegistryHolder().is(AetherTags.Entities.TREATED_AS_VANILLA_ENTITY)) { // Checks if the target is an Aether entity.
                        if ((!source.getType().getDescriptionId().startsWith("entity.aether") && !source.getType().builtInRegistryHolder().is(AetherTags.Entities.TREATED_AS_AETHER_ENTITY)) // Checks if the projectile is non-Aether.
                                && (!(source instanceof AbstractArrow abstractArrow) || !abstractArrow.hasAttached(AetherDataAttachments.PHOENIX_ARROW) || !abstractArrow.getAttachedOrCreate(AetherDataAttachments.PHOENIX_ARROW).isPhoenixArrow())) { // Special check against Phoenix Arrows.
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
                if ((source.getType().getDescriptionId().startsWith("entity.aether") || source.getType().builtInRegistryHolder().is(AetherTags.Entities.TREATED_AS_AETHER_ENTITY) && !source.getType().builtInRegistryHolder().is(AetherTags.Entities.TREATED_AS_VANILLA_ENTITY))) { // Checks if the attacker is an Aether entity.
                    for (EquipmentSlot equipmentSlot : EquipmentSlot.VALUES) {
                        if (!equipmentSlot.isArmor()) {
                            continue;
                        }
                        ItemStack stack = target.getItemBySlot(equipmentSlot);
                        if (stack.getItem() instanceof ArmorItem && !stack.getItem().getDescriptionId().startsWith("item.aether.") && !stack.is(AetherTags.Items.TREATED_AS_AETHER_ITEM)) { // Checks if the armor is non-Aether.
                            AtomicDouble value = new AtomicDouble();
                            stack.forEachModifier(equipmentSlot, (attribute, modifier) -> { // Checks if the armor has an armor modifier attribute.
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
}
