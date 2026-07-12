package com.aetherteam.aether.mixin.mixins.common;

import com.aetherteam.aether.util.LevelTimeUtil;
import com.aetherteam.aether.world.AetherTimeController;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class)
public class ServerLevelSleepMixin {
    // EntitySleepEvents observes individual players, not completion of the level-wide wake operation.
    @Inject(method = "wakeUpAllPlayers()V", at = @At("TAIL"))
    private void aether$finishSleep(CallbackInfo ci) {
        ServerLevel level = (ServerLevel) (Object) this;
        Long time = AetherTimeController.finishSleep(level, LevelTimeUtil.getTime(level));
        if (time != null) {
            LevelTimeUtil.setTime(level, time);
        }
    }
}
