package com.aetherteam.aether.mixin.mixins.common;

import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.entity.monster.dungeon.boss.ValkyrieQueen;
import com.aetherteam.aether.event.hooks.AbilityHooks;
import com.aetherteam.aether.event.hooks.EntityHooks;
import com.aetherteam.aether.item.combat.abilities.armor.GravititeArmor;
import com.aetherteam.aether.item.combat.abilities.armor.NeptuneArmor;
import com.aetherteam.aether.item.combat.abilities.armor.PhoenixArmor;
import com.aetherteam.aether.item.combat.abilities.armor.ValkyrieArmor;
import com.aetherteam.aether.accessories.api.AccessoriesCapability;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Unique
    private boolean aether$trackingDeathDrops;

    /**
     * Handles vertical swimming for Phoenix Armor in lava without being affected by the upwards speed debuff from lava.
     *
     * @param ci The {@link CallbackInfo} for the void method return.
     * @see PhoenixArmor#boostVerticalLavaSwimming(LivingEntity)
     */
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getFluidJumpThreshold()D", shift = At.Shift.AFTER), method = "travel(Lnet/minecraft/world/phys/Vec3;)V")
    private void travel(CallbackInfo ci) {
        LivingEntity livingEntity = (LivingEntity) (Object) this;
        PhoenixArmor.boostVerticalLavaSwimming(livingEntity);
    }

    @WrapWithCondition(method = "hurtServer(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;knockback(DDD)V"))
    private boolean hurt(LivingEntity instance, double strength, double x, double z, ServerLevel serverLevel, DamageSource source, float amount) {
        return (!(instance instanceof ValkyrieQueen) || !(source.getDirectEntity() instanceof Projectile));
    }

    @Inject(method = "applyItemBlocking(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)F", at = @At("HEAD"), cancellable = true)
    private void aether$preventSliderShieldBlock(ServerLevel level, DamageSource source, float amount, CallbackInfoReturnable<Float> cir) {
        if (EntityHooks.preventSliderShieldBlock(source)) {
            cir.setReturnValue(amount);
        }
    }

    @Inject(method = "dropAllDeathLoot(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;)V", at = @At("HEAD"))
    private void aether$beginTrackingDeathDrops(ServerLevel level, DamageSource source, CallbackInfo ci) {
        this.aether$trackingDeathDrops = true;
    }

    @Inject(method = "dropAllDeathLoot(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;)V", at = @At("TAIL"))
    private void aether$stopTrackingDeathDrops(ServerLevel level, DamageSource source, CallbackInfo ci) {
        this.aether$trackingDeathDrops = false;
    }

    @Inject(method = "dropCustomDeathLoot(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;Z)V", at = @At("TAIL"))
    private void aether$dropAccessoryLoot(ServerLevel level, DamageSource source, boolean recentlyHit, CallbackInfo ci) {
        LivingEntity livingEntity = (LivingEntity) (Object) this;
        if (!(livingEntity instanceof Mob mob)) {
            return;
        }

        AccessoriesCapability accessories = AccessoriesCapability.get(mob);
        if (accessories == null) {
            return;
        }

        List<ItemStack> equippedAccessories = new ArrayList<>();
        accessories.getAllEquipped().forEach(reference -> equippedAccessories.add(reference.stack().copy()));
        if (equippedAccessories.isEmpty()) {
            return;
        }

        int looting = EnchantmentHelper.getEnchantmentLevel(
                level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.LOOTING),
                livingEntity
        );

        List<ItemStack> drops = EntityHooks.handleEntityAccessoryDrops(livingEntity, equippedAccessories, recentlyHit, looting);
        drops.stream().filter(stack -> !stack.isEmpty()).forEach(stack -> livingEntity.spawnAtLocation(level, stack.copy()));
    }

    @Inject(method = "drop(Lnet/minecraft/world/item/ItemStack;ZZ)Lnet/minecraft/world/entity/item/ItemEntity;", at = @At("RETURN"))
    private void aether$trackPlayerDeathDrop(ItemStack stack, boolean dropAround, boolean includeName, CallbackInfoReturnable<ItemEntity> cir) {
        if (this.aether$trackingDeathDrops) {
            LivingEntity livingEntity = (LivingEntity) (Object) this;
            if (livingEntity instanceof Player player) {
                ItemEntity itemEntity = cir.getReturnValue();
                if (itemEntity != null) {
                    itemEntity.getAttachedOrCreate(AetherDataAttachments.DROPPED_ITEM).setOwner(player);
                }
            }
        }
    }

    @Inject(method = "getExperienceReward(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/Entity;)I", at = @At("RETURN"), cancellable = true)
    private void aether$modifyExperienceReward(ServerLevel level, net.minecraft.world.entity.Entity attacker, CallbackInfoReturnable<Integer> cir) {
        LivingEntity livingEntity = (LivingEntity) (Object) this;
        cir.setReturnValue(EntityHooks.modifyExperience(livingEntity, cir.getReturnValueI()));
    }

    @Inject(method = "tick()V", at = @At("TAIL"))
    private void aether$applyArmorTickAbilities(CallbackInfo ci) {
        LivingEntity livingEntity = (LivingEntity) (Object) this;
        ValkyrieArmor.handleFlight(livingEntity);
        NeptuneArmor.boostWaterSwimming(livingEntity);
        PhoenixArmor.boostLavaSwimming(livingEntity);
        PhoenixArmor.damageArmor(livingEntity);
    }

    @Inject(method = "jumpFromGround()V", at = @At("TAIL"))
    private void aether$boostJump(CallbackInfo ci) {
        GravititeArmor.boostedJump((LivingEntity) (Object) this);
    }

    @Inject(method = "causeFallDamage(DFLnet/minecraft/world/damagesource/DamageSource;)Z", at = @At("HEAD"), cancellable = true)
    private void aether$cancelFallDamage(double fallDistance, float multiplier, DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        if (AbilityHooks.ArmorHooks.fallCancellation((LivingEntity) (Object) this)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "hurtServer(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)Z", at = @At("HEAD"), cancellable = true)
    private void aether$beforeHurt(ServerLevel level, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity livingEntity = (LivingEntity) (Object) this;
        AbilityHooks.AccessoryHooks.setAttack(source);
        AbilityHooks.WeaponHooks.stickDart(livingEntity, source);
        if (AbilityHooks.AccessoryHooks.preventMagmaDamage(livingEntity, source) || PhoenixArmor.extinguishUser(livingEntity, source)) {
            cir.setReturnValue(false);
        }
    }

    @ModifyVariable(method = "hurtServer(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)Z", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private float aether$modifyIncomingDamage(float amount, ServerLevel level, DamageSource source) {
        LivingEntity livingEntity = (LivingEntity) (Object) this;
        Entity direct = source.getDirectEntity();
        amount = AbilityHooks.WeaponHooks.reduceWeaponEffectiveness(livingEntity, direct, amount);
        return AbilityHooks.WeaponHooks.reduceArmorEffectiveness(livingEntity, direct, amount);
    }

    @ModifyReturnValue(method = "getVisibilityPercent(Lnet/minecraft/world/entity/Entity;)D", at = @At("RETURN"))
    private double aether$modifyVisibility(double original, Entity lookingEntity) {
        LivingEntity livingEntity = (LivingEntity) (Object) this;
        if (lookingEntity != null) {
            if (AbilityHooks.AccessoryHooks.preventTargeting(livingEntity, lookingEntity)
                    && !AbilityHooks.AccessoryHooks.recentlyAttackedWithInvisibility(livingEntity, lookingEntity)) {
                return 0.0D;
            }
            if (AbilityHooks.AccessoryHooks.recentlyAttackedWithInvisibility(livingEntity, lookingEntity)) {
                return 1.0D;
            }
        }
        return original;
    }
}
