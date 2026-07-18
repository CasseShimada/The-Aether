package com.aetherteam.aether.mixin.mixins.common;

import com.aetherteam.aether.accessories.api.slot.SlotEntryReference;
import com.aetherteam.aether.accessories.effect.AccessoryEffectBridge;
import com.aetherteam.aether.accessories.impl.AccessoryRuntime;
import com.aetherteam.aether.accessories.impl.AccessoryItemInteractions;
import com.aetherteam.aether.accessories.impl.AccessoryUsingEntity;
import com.aetherteam.aether.accessories.api.AccessoriesAPI;
import com.aetherteam.aether.accessories.api.slot.SlotReference;
import com.aetherteam.aether.accessories.impl.MobAccessorySpawning;
import com.aetherteam.aether.entity.AetherBossCombatRules;
import com.aetherteam.aether.entity.AetherCombat;
import com.aetherteam.aether.item.accessories.abilities.AccessoryAbilities;
import com.aetherteam.aether.item.EquipmentUtil;
import com.aetherteam.aether.item.combat.abilities.armor.GravititeArmor;
import com.aetherteam.aether.item.combat.abilities.armor.NeptuneArmor;
import com.aetherteam.aether.item.combat.abilities.armor.PhoenixArmor;
import com.aetherteam.aether.item.combat.abilities.armor.ValkyrieArmor;
import com.aetherteam.aether.world.AetherTravelController;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.BlocksAttacks;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;
import net.minecraft.network.syncher.EntityDataAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Predicate;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements AccessoryUsingEntity {
    @Shadow
    protected ItemStack useItem;
    @Shadow
    protected int useItemRemaining;
    @Shadow
    protected abstract void setLivingEntityFlag(int flag, boolean value);

    @Unique
    private boolean aether$trackingDeathDrops;
    @Unique
    private ItemStack aether$breakingAccessoryEquipmentStack = ItemStack.EMPTY;
    @Unique
    private EquipmentSlot aether$breakingAccessoryEquipmentSlot;
    @Unique
    private boolean aether$checkingAccessoryDeathProtection;
    @Unique
    private SlotReference aether$usingAccessoryReference;

    @Override
    public void aether$startUsingAccessory(SlotReference reference, InteractionHand hand) {
        LivingEntity livingEntity = (LivingEntity) (Object) this;
        ItemStack stack = reference == null ? ItemStack.EMPTY : reference.getStack();
        if (stack.isEmpty() || livingEntity.isUsingItem()) {
            return;
        }
        this.aether$usingAccessoryReference = reference;
        this.useItem = stack;
        this.useItemRemaining = stack.getUseDuration(livingEntity);
        if (!livingEntity.level().isClientSide()) {
            this.setLivingEntityFlag(1, true);
            this.setLivingEntityFlag(2, hand == InteractionHand.OFF_HAND);
            this.useItem.causeUseVibration(livingEntity, net.minecraft.world.level.gameevent.GameEvent.ITEM_INTERACT_START);
        }
    }

    @Override
    public boolean aether$isUsingAccessory() {
        return this.aether$usingAccessoryReference != null && !this.useItem.isEmpty();
    }

    /**
     * Handles vertical swimming for Phoenix Armor in lava without being affected by the upwards speed debuff from lava.
     *
     * @param ci The {@link CallbackInfo} for the void method return.
     * @see PhoenixArmor#boostVerticalLavaSwimming(LivingEntity)
     */
    @Inject(
        method = "travelInLava(Lnet/minecraft/world/phys/Vec3;DZD)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;move(Lnet/minecraft/world/entity/MoverType;Lnet/minecraft/world/phys/Vec3;)V",
            shift = At.Shift.AFTER
        )
    )
    private void aether$boostVerticalLavaSwimming(CallbackInfo ci) {
        LivingEntity livingEntity = (LivingEntity) (Object) this;
        PhoenixArmor.boostVerticalLavaSwimming(livingEntity);
    }

    @WrapWithCondition(
        method = "hurtServer(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)Z",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;dealDefaultKnockback(Lnet/minecraft/world/damagesource/DamageSource;FZ)V"
        )
    )
    private boolean aether$shouldApplyDefaultKnockback(LivingEntity instance, DamageSource source, float amount, boolean blocked) {
        return AetherBossCombatRules.shouldApplyKnockback(instance, source);
    }

    @Inject(method = "applyItemBlocking(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)F", at = @At("HEAD"), cancellable = true)
    private void aether$preventSliderShieldBlock(ServerLevel level, DamageSource source, float amount, CallbackInfoReturnable<Float> cir) {
        if (AetherBossCombatRules.bypassesShieldBlocking(source)) {
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
        MobAccessorySpawning.dropAccessories((LivingEntity) (Object) this, level, recentlyHit);
    }

    @Inject(method = "drop(Lnet/minecraft/world/item/ItemStack;ZZ)Lnet/minecraft/world/entity/item/ItemEntity;", at = @At("RETURN"))
    private void aether$trackPlayerDeathDrop(ItemStack stack, boolean dropAround, boolean includeName, CallbackInfoReturnable<ItemEntity> cir) {
        if (this.aether$trackingDeathDrops) {
            AetherTravelController.trackPlayerDeathDrop((LivingEntity) (Object) this, cir.getReturnValue());
        }
    }

    @Inject(method = "getExperienceReward(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/Entity;)I", at = @At("RETURN"), cancellable = true)
    private void aether$modifyExperienceReward(ServerLevel level, net.minecraft.world.entity.Entity attacker, CallbackInfoReturnable<Integer> cir) {
        LivingEntity livingEntity = (LivingEntity) (Object) this;
        cir.setReturnValue(MobAccessorySpawning.modifyExperience(livingEntity, cir.getReturnValueI()));
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
        if (EquipmentUtil.preventsFallDamage((LivingEntity) (Object) this)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "hurtServer(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)Z", at = @At("HEAD"), cancellable = true)
    private void aether$beforeHurt(ServerLevel level, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (AetherCombat.beforeHurt((LivingEntity) (Object) this, source)) {
            cir.setReturnValue(false);
        }
    }

    @ModifyVariable(method = "hurtServer(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)Z", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private float aether$modifyIncomingDamage(float amount, ServerLevel level, DamageSource source) {
        return AetherCombat.modifyIncomingDamage((LivingEntity) (Object) this, source, amount);
    }

    @ModifyReturnValue(method = "getVisibilityPercent(Lnet/minecraft/world/entity/Entity;)D", at = @At("RETURN"))
    private double aether$modifyVisibility(double original, Entity lookingEntity) {
        return AccessoryAbilities.modifyVisibility((LivingEntity) (Object) this, lookingEntity, original);
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
        if (!AccessoryEffectBridge.shouldTryAccessoryDeathProtection(cir.getReturnValueZ(), source.is(DamageTypeTags.BYPASSES_INVULNERABILITY))
            || this.aether$checkingAccessoryDeathProtection) {
            return;
        }

        LivingEntity livingEntity = (LivingEntity) (Object) this;
        this.aether$checkingAccessoryDeathProtection = true;
        try {
            if (AccessoryEffectBridge.applyDeathProtection(livingEntity)) {
                cir.setReturnValue(true);
            }
        } finally {
            this.aether$checkingAccessoryDeathProtection = false;
        }
    }

    @WrapOperation(method = {"canGlide()Z", "updateFallFlying()V", "onEquippedItemBroken(Lnet/minecraft/world/item/Item;Lnet/minecraft/world/entity/EquipmentSlot;)V"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack aether$useAccessoryElytraForVanillaFlightChecks(LivingEntity instance, EquipmentSlot slot, Operation<ItemStack> original) {
        ItemStack stack = original.call(instance, slot);
        if (this.aether$breakingAccessoryEquipmentSlot == slot && !this.aether$breakingAccessoryEquipmentStack.isEmpty()) {
            return this.aether$breakingAccessoryEquipmentStack;
        }
        if (slot != EquipmentSlot.CHEST || stack.is(Items.ELYTRA)) {
            return stack;
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

    @WrapOperation(method = "applyItemBlocking(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)F", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/component/BlocksAttacks;hurtBlockingItem(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/InteractionHand;F)V"))
    private void aether$damageAccessoryShield(BlocksAttacks instance, Level level, ItemStack stack, LivingEntity user, InteractionHand hand, float damage, Operation<Void> original) {
        SlotReference reference = this.aether$usingAccessoryReference;
        if (reference == null || reference.getStack() != stack) {
            original.call(instance, level, stack, user, hand, damage);
            return;
        }

        var accessories = AccessoriesAPI.getAccessories(user);
        if (accessories == null) {
            original.call(instance, level, stack, user, hand, damage);
            return;
        }

        this.aether$breakingAccessoryEquipmentStack = stack.copy();
        this.aether$breakingAccessoryEquipmentSlot = EquipmentSlot.OFFHAND;
        try {
            accessories.mutateAccessory(reference, liveStack -> original.call(instance, level, liveStack, user, hand, damage));
        } finally {
            this.aether$breakingAccessoryEquipmentStack = ItemStack.EMPTY;
            this.aether$breakingAccessoryEquipmentSlot = null;
        }
    }

    @WrapOperation(method = {"updatingUsingItem()V", "releaseUsingItem()V", "completeUsingItem()V"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getItemInHand(Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack aether$keepUsingAccessoryItem(LivingEntity instance, InteractionHand hand, Operation<ItemStack> original) {
        if (this.aether$usingAccessoryReference != null) {
            return this.aether$usingAccessoryReference.getStack();
        }
        return original.call(instance, hand);
    }

    @Inject(method = "onSyncedDataUpdated(Lnet/minecraft/network/syncher/EntityDataAccessor;)V", at = @At("TAIL"))
    private void aether$resolveSyncedAccessoryUse(EntityDataAccessor<?> accessor, CallbackInfo ci) {
        LivingEntity livingEntity = (LivingEntity) (Object) this;
        if (!livingEntity.level().isClientSide()) {
            return;
        }
        if (!livingEntity.isUsingItem()) {
            this.aether$usingAccessoryReference = null;
            return;
        }
        if (this.useItem.getUseDuration(livingEntity) > 0 || !(livingEntity instanceof net.minecraft.world.entity.player.Player player)) {
            return;
        }
        SlotEntryReference shield = AccessoryItemInteractions.findBlockingShield(player);
        if (shield != null) {
            this.aether$usingAccessoryReference = shield.reference();
            this.useItem = shield.stack();
            this.useItemRemaining = this.useItem.getUseDuration(livingEntity);
        }
    }

    @Inject(method = "stopUsingItem()V", at = @At("TAIL"))
    private void aether$clearAccessoryUseReference(CallbackInfo ci) {
        this.aether$usingAccessoryReference = null;
    }

}
