package com.aetherteam.aether.mixin.mixins.common;

import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.entity.monster.dungeon.boss.ValkyrieQueen;
import com.aetherteam.aether.event.hooks.AbilityHooks;
import com.aetherteam.aether.event.hooks.EntityAccessorySpawnHooks;
import com.aetherteam.aether.event.hooks.EntityCombatHooks;
import com.aetherteam.aether.item.combat.abilities.armor.GravititeArmor;
import com.aetherteam.aether.item.combat.abilities.armor.NeptuneArmor;
import com.aetherteam.aether.item.combat.abilities.armor.PhoenixArmor;
import com.aetherteam.aether.item.combat.abilities.armor.ValkyrieArmor;
import com.aetherteam.aether.accessories.api.AccessoriesAPI;
import com.aetherteam.aether.accessories.api.slot.SlotEntryReference;
import com.aetherteam.aether.accessories.compat.AccessoryEffectBridge;
import com.aetherteam.aether.accessories.impl.AccessoryRuntime;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.gameevent.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Unique
    private boolean aether$trackingDeathDrops;
    @Unique
    private ItemStack aether$breakingAccessoryEquipmentStack = ItemStack.EMPTY;
    @Unique
    private EquipmentSlot aether$breakingAccessoryEquipmentSlot;
    @Unique
    private boolean aether$checkingAccessoryDeathProtection;

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
        if (EntityCombatHooks.preventSliderShieldBlock(source)) {
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

        var accessories = AccessoriesAPI.getAccessories(mob);
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

        List<ItemStack> drops = EntityAccessorySpawnHooks.handleEntityAccessoryDrops(livingEntity, equippedAccessories, recentlyHit, looting);
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
        cir.setReturnValue(EntityAccessorySpawnHooks.modifyExperience(livingEntity, cir.getReturnValueI()));
    }

    @Inject(method = "tick()V", at = @At("TAIL"))
    private void aether$applyArmorTickAbilities(CallbackInfo ci) {
        LivingEntity livingEntity = (LivingEntity) (Object) this;
        ValkyrieArmor.handleFlight(livingEntity);
        NeptuneArmor.boostWaterSwimming(livingEntity);
        PhoenixArmor.boostLavaSwimming(livingEntity);
        PhoenixArmor.damageArmor(livingEntity);
        AccessoryRuntime.tick(livingEntity);
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

    @ModifyReturnValue(method = "isHolding(Ljava/util/function/Predicate;)Z", at = @At("RETURN"))
    private boolean aether$includeAccessoryHolding(boolean original, Predicate<ItemStack> predicate) {
        if (original) {
            return true;
        }
        return AccessoryEffectBridge.isHoldingEquivalent((LivingEntity) (Object) this, predicate);
    }

    @Inject(method = "checkTotemDeathProtection(Lnet/minecraft/world/damagesource/DamageSource;)Z", at = @At("RETURN"), cancellable = true)
    private void aether$checkAccessoryDeathProtection(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ() || source.is(DamageTypeTags.BYPASSES_INVULNERABILITY) || this.aether$checkingAccessoryDeathProtection) {
            return;
        }

        LivingEntity livingEntity = (LivingEntity) (Object) this;
        this.aether$checkingAccessoryDeathProtection = true;
        try {
            AccessoryEffectBridge.DeathProtectionResult result = AccessoryEffectBridge.consumeDeathProtection(livingEntity);
            if (result == null) {
                return;
            }

            ItemStack usedStack = result.usedStack();
            if (livingEntity instanceof ServerPlayer serverPlayer) {
                serverPlayer.awardStat(Stats.ITEM_USED.get(usedStack.getItem()));
                CriteriaTriggers.USED_TOTEM.trigger(serverPlayer, usedStack);
                usedStack.causeUseVibration(livingEntity, GameEvent.ITEM_INTERACT_FINISH);
            }

            livingEntity.setHealth(1.0F);
            result.deathProtection().applyEffects(usedStack, livingEntity);
            livingEntity.level().broadcastEntityEvent(livingEntity, (byte) 35);
            cir.setReturnValue(true);
        } finally {
            this.aether$checkingAccessoryDeathProtection = false;
        }
    }

    @WrapOperation(method = {"canGlide()Z", "updateFallFlying()V", "onEquippedItemBroken(Lnet/minecraft/world/item/Item;Lnet/minecraft/world/entity/EquipmentSlot;)V"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack aether$useAccessoryElytraForVanillaFlightChecks(LivingEntity instance, EquipmentSlot slot, Operation<ItemStack> original) {
        ItemStack stack = original.call(instance, slot);
        if (slot != EquipmentSlot.CHEST || stack.is(Items.ELYTRA)) {
            return stack;
        }

        if (this.aether$breakingAccessoryEquipmentSlot == slot && !this.aether$breakingAccessoryEquipmentStack.isEmpty()) {
            return this.aether$breakingAccessoryEquipmentStack;
        }

        SlotEntryReference accessoryReference = AccessoryEffectBridge.findFirstElytraReference(instance);
        return accessoryReference != null ? accessoryReference.stack() : stack;
    }

    @WrapOperation(method = "updateFallFlying()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Util;getRandom(Ljava/util/List;Lnet/minecraft/util/RandomSource;)Ljava/lang/Object;"))
    private <T> T aether$selectAccessoryElytraDamageSlot(List<T> candidates, net.minecraft.util.RandomSource random, Operation<T> original) {
        if (!candidates.isEmpty()) {
            return original.call(candidates, random);
        }

        LivingEntity livingEntity = (LivingEntity) (Object) this;
        if (!livingEntity.getItemBySlot(EquipmentSlot.CHEST).is(Items.ELYTRA)
                && AccessoryEffectBridge.findFirstElytraReference(livingEntity) != null) {
            @SuppressWarnings("unchecked")
            T chestSlot = (T) EquipmentSlot.CHEST;
            return chestSlot;
        }

        return original.call(candidates, random);
    }

    @WrapOperation(method = "updateFallFlying()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;hurtAndBreak(ILnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/EquipmentSlot;)V"))
    private void aether$syncAccessoryElytraDamage(ItemStack stack, int amount, LivingEntity entity, EquipmentSlot slot, Operation<Void> original) {
        SlotEntryReference accessoryReference = null;
        if (slot == EquipmentSlot.CHEST) {
            accessoryReference = AccessoryEffectBridge.findFirstElytraReference(entity);
            if (accessoryReference != null && accessoryReference.stack() != stack) {
                accessoryReference = null;
            }
        }

        ItemStack previousStack = stack.copy();
        if (accessoryReference != null) {
            this.aether$breakingAccessoryEquipmentStack = previousStack;
            this.aether$breakingAccessoryEquipmentSlot = slot;
        }

        try {
            original.call(stack, amount, entity, slot);
        } finally {
            if (accessoryReference != null) {
                this.aether$breakingAccessoryEquipmentStack = ItemStack.EMPTY;
                this.aether$breakingAccessoryEquipmentSlot = null;
            }
        }

        if (accessoryReference != null) {
            AccessoryEffectBridge.syncAccessorySlotMutation(entity, accessoryReference, previousStack);
        }
    }

}
