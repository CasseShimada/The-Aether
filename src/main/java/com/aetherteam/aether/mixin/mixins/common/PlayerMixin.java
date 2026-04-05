package com.aetherteam.aether.mixin.mixins.common;

import com.aetherteam.aether.entity.passive.MountableAnimal;
import com.aetherteam.aether.entity.monster.Swet;
import com.aetherteam.aether.event.hooks.AbilityHooks;
import com.aetherteam.aether.event.hooks.CapabilityHooks;
import com.aetherteam.aether.event.hooks.DimensionHooks;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Player.class)
public abstract class PlayerMixin {
    @Shadow
    protected abstract boolean wantsToStopRiding();

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
            if (player.isPassenger() && (player.getVehicle() instanceof MountableAnimal || player.getVehicle() instanceof Swet)) {
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

    /**
     * Vanilla guards this path with `isEmpty()`, but a transformed runtime can still reach `Util#getRandom` with an empty list.
     * Preserve the normal selection logic and only short-circuit the impossible empty-list edge case.
     */
    @WrapOperation(method = "aiStep()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Util;getRandom(Ljava/util/List;Lnet/minecraft/util/RandomSource;)Ljava/lang/Object;"))
    private <T> T aether$guardRandomTouchedExperienceOrb(List<T> entities, RandomSource random, Operation<T> original) {
        return entities.isEmpty() ? null : original.call(entities, random);
    }

    @WrapOperation(method = "aiStep()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;touch(Lnet/minecraft/world/entity/Entity;)V", ordinal = 1))
    private void aether$skipMissingRandomTouch(Player instance, Entity entity, Operation<Void> original) {
        if (entity != null) {
            original.call(instance, entity);
        }
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
}
