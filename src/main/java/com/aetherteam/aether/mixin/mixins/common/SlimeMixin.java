package com.aetherteam.aether.mixin.mixins.common;

import com.aetherteam.aether.event.hooks.EntityHooks;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.cubemob.Slime;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Slime.class)
public class SlimeMixin {
    @WrapWithCondition(
            method = "remove(Lnet/minecraft/world/entity/Entity$RemovalReason;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z")
    )
    private boolean aether$preventSwetSplit(Level level, Entity entity) {
        return !EntityHooks.preventSplit((Mob) (Object) this);
    }
}
