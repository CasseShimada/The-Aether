package com.aetherteam.aether.mixin.mixins.common;

import com.aetherteam.aether.item.combat.abilities.weapon.WeaponAbilities;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractArrow.class)
public abstract class AbstractArrowMixin {
    @Shadow
    protected abstract boolean isInGround();

    @Shadow
    protected int inGroundTime;

    /**
     * Spawns particles from Phoenix Arrows.
     *
     * @param ci The {@link CallbackInfo} for the void method return.
     * @see WeaponAbilities#tickPhoenixArrow(AbstractArrow, boolean, int)
     */
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/Projectile;tick()V", shift = At.Shift.AFTER), method = "tick()V")
    private void tick(CallbackInfo ci) {
        WeaponAbilities.tickPhoenixArrow((AbstractArrow) (Object) this, this.isInGround(), this.inGroundTime);
    }
}
