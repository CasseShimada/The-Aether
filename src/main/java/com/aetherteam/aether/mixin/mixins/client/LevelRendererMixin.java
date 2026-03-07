package com.aetherteam.aether.mixin.mixins.client;

import com.aetherteam.aether.AetherConfig;
import com.aetherteam.aether.data.resources.registries.AetherDimensions;
import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import net.minecraft.client.CloudStatus;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {
    @Shadow
    private ClientLevel level;

    @Inject(method = "addCloudsPass", at = @At("HEAD"), cancellable = true)
    private void aether$cancelAetherCloudPass(FrameGraphBuilder frameGraphBuilder, CloudStatus cloudStatus, Vec3 cloudColor, long gameTime, float partialTick, int packedCloudColor, float cloudHeight, CallbackInfo ci) {
        if (this.level != null
                && this.level.dimension().equals(AetherDimensions.AETHER_LEVEL)
                && AetherConfig.CLIENT.disable_clouds.get()) {
            ci.cancel();
        }
    }
}
