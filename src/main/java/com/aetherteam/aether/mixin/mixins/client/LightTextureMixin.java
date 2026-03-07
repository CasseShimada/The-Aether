package com.aetherteam.aether.mixin.mixins.client;

import com.aetherteam.aether.AetherConfig;
import com.aetherteam.aether.data.resources.registries.AetherDimensions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LightTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LightTexture.class)
public class LightTextureMixin {
    @ModifyVariable(method = "updateLightTexture", at = @At("STORE"), ordinal = 0)
    private int aether$normalizeAetherSkyLightColor(int skyLightColor) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level != null
                && level.dimension().equals(AetherDimensions.AETHER_LEVEL)
                && AetherConfig.CLIENT.colder_lightmap.get()) {
            return 0xFFFFFF;
        }
        return skyLightColor;
    }
}
