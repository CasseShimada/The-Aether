package com.aetherteam.aether.mixin.mixins.client;

import com.aetherteam.aether.AetherConfig;
import com.aetherteam.aether.data.resources.registries.AetherDimensions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LightmapRenderStateExtractor;
import net.minecraft.client.renderer.state.LightmapRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LightmapRenderStateExtractor.class)
public class LightTextureMixin {
    @Inject(method = "extract", at = @At("TAIL"))
    private void aether$normalizeAetherSkyLightColor(LightmapRenderState state, float partialTick, CallbackInfo ci) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level != null
                && level.dimension().equals(AetherDimensions.AETHER_LEVEL)
                && AetherConfig.CLIENT.colder_lightmap.get()) {
            state.skyLightColor = LightmapRenderStateExtractor.WHITE;
        }
    }
}
