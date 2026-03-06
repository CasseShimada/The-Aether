package com.aetherteam.aether.mixin.mixins.common;

import com.aetherteam.aether.event.hooks.DimensionHooks;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class)
public class ServerLevelMixin {
    @Inject(method = "wakeUpAllPlayers()V", at = @At("TAIL"))
    private void aether$finishSleep(CallbackInfo ci) {
        ServerLevel level = (ServerLevel) (Object) this;
        Long time = DimensionHooks.finishSleep(level, level.getDayTime());
        if (time != null) {
            level.setDayTime(time);
        }
    }
}
