package com.aetherteam.aether.mixin.mixins.common;

import com.aetherteam.aether.AetherTags;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.cubemob.AbstractCubeMob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractCubeMob.class)
public class AbstractCubeMobMixin {
    @ModifyExpressionValue(
        method = "remove(Lnet/minecraft/world/entity/Entity$RemovalReason;)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/cubemob/AbstractCubeMob;isDeadOrDying()Z")
    )
    private boolean aether$preventSwetSplit(boolean original) {
        Mob mob = (Mob) (Object) this;
        return original && !mob.getType().builtInRegistryHolder().is(AetherTags.Entities.SWETS);
    }
}
