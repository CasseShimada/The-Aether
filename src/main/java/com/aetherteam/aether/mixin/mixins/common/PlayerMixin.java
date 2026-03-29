package com.aetherteam.aether.mixin.mixins.common;

import com.aetherteam.aether.entity.passive.MountableAnimal;
import com.aetherteam.aether.accessories.compat.AccessoryEffectBridge;
import com.aetherteam.aether.event.hooks.AbilityHooks;
import com.aetherteam.aether.event.hooks.CapabilityHooks;
import com.aetherteam.aether.event.hooks.DimensionHooks;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin {
    @Shadow
    protected abstract boolean wantsToStopRiding();

    @Shadow
    public abstract void startFallFlying();

    @Shadow
    protected abstract boolean canGlide();

    /**
     * Damages gloves only once during a sweeping attack, instead of once for every damaged entity in the attack.
     *
     * @param target The target {@link Entity}.
     * @param ci     The {@link CallbackInfo} for the void method return.
     * @see AbilityHooks.AccessoryHooks#damageGloves(Player)
     */
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;setLastHurtMob(Lnet/minecraft/world/entity/Entity;)V", shift = At.Shift.AFTER), method = "attack(Lnet/minecraft/world/entity/Entity;)V")
    private void attack(Entity target, CallbackInfo ci) {
        Player player = (Player) (Object) this;
        if (target instanceof LivingEntity) {
            AbilityHooks.AccessoryHooks.damageGloves(player);
        }
    }

    /**
     * Used to set whether the player tried to crouch for {@link MountableAnimal}, before crouching is cancelled for mounts by the {@link Player} class.
     *
     * @param ci The {@link CallbackInfo} for the void method return.
     */
    @Inject(at = @At(value = "HEAD"), method = "rideTick()V")
    private void rideTickHead(CallbackInfo ci, @Share("wantsToStopRiding") LocalBooleanRef wantsToStopRiding) {
        Player player = (Player) (Object) this;
        wantsToStopRiding.set(this.wantsToStopRiding());
        if (!player.level().isClientSide()) {
            if (player.isPassenger() && player.getVehicle() instanceof MountableAnimal mountableAnimal) {
                mountableAnimal.setPlayerTriedToCrouch(player.isShiftKeyDown());
            }
        }
    }

    @Inject(at = @At(value = "TAIL"), method = "rideTick()V")
    private void rideTickTail(CallbackInfo ci, @Share("wantsToStopRiding") LocalBooleanRef wantsToStopRiding) {
        Player player = (Player) (Object) this;
        if (!player.level().isClientSide() && !player.isShiftKeyDown() && wantsToStopRiding.get()) {
            if (player.isPassenger() && player.getVehicle() instanceof MountableAnimal) {
                player.setShiftKeyDown(true);
            }
        }
    }

    /**
     * Mirrors NeoForge player lifecycle tick callbacks for Fabric, so attachment and dimension travel hooks run every tick.
     */
    @Inject(at = @At("TAIL"), method = "tick()V")
    private void tick(CallbackInfo ci) {
        Player player = (Player) (Object) this;
        CapabilityHooks.AetherPlayerHooks.update(player);
        DimensionHooks.travelling(player);
    }

    @ModifyReturnValue(method = "getDestroySpeed(Lnet/minecraft/world/level/block/state/BlockState;)F", at = @At("RETURN"))
    private float aether$modifyBreakSpeed(float original, BlockState state) {
        Player player = (Player) (Object) this;
        ItemStack stack = player.getMainHandItem();
        float speed = original;
        speed = AbilityHooks.AccessoryHooks.handleZaniteRingAbility(player, speed);
        speed = AbilityHooks.AccessoryHooks.handleZanitePendantAbility(player, speed);
        speed = AbilityHooks.ToolHooks.handleZaniteToolAbility(stack, speed);
        return AbilityHooks.ToolHooks.reduceToolEffectiveness(player, state, stack, speed);
    }

    @Inject(method = "tryToStartFallFlying()Z", at = @At("HEAD"), cancellable = true)
    private void aether$startAccessoryElytraFlight(CallbackInfoReturnable<Boolean> cir) {
        Player player = (Player) (Object) this;
        if (player.getItemBySlot(EquipmentSlot.CHEST).is(Items.ELYTRA)) {
            return;
        }
        if (!AccessoryEffectBridge.findFirstByEquipmentSlot(player, EquipmentSlot.CHEST).is(Items.ELYTRA)) {
            return;
        }
        if (player.onGround() || player.isFallFlying() || player.isInWater() || player.hasEffect(MobEffects.LEVITATION) || !this.canGlide()) {
            return;
        }

        this.startFallFlying();
        cir.setReturnValue(true);
    }
}
