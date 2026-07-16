package com.aetherteam.aether.mixin.mixins.client;

import com.aetherteam.aether.client.renderer.level.AetherSkyRendering;
import com.aetherteam.aether.data.resources.registries.AetherDimensions;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.attribute.EnvironmentAttributeSystem;
import net.minecraft.world.attribute.EnvironmentAttributes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientLevel.class)
public class ClientLevelMixin {
    @Inject(method = "addEnvironmentAttributeLayers", at = @At("RETURN"), cancellable = true, require = 1)
    private void aether$addAetherSkyWeatherLayers(EnvironmentAttributeSystem.Builder builder, CallbackInfoReturnable<EnvironmentAttributeSystem.Builder> cir) {
        ClientLevel level = (ClientLevel) (Object) this;
        if (!level.dimension().equals(AetherDimensions.AETHER_LEVEL)) {
            return;
        }

        EnvironmentAttributeSystem.Builder result = cir.getReturnValue();
        result.addTimeBasedLayer(EnvironmentAttributes.SKY_LIGHT_FACTOR, (current, tickId) -> {
            float timeOfDay = AetherSkyRendering.getAetherTimeOfDay(level, 0.0F);
            float skyLightFactor = Mth.cos(timeOfDay * Mth.TWO_PI) * 2.0F + 0.5F;
            skyLightFactor = Mth.clamp(skyLightFactor, 0.0F, 1.0F);
            skyLightFactor *= 1.0F - level.getRainLevel(1.0F) * (5.0F / 16.0F);
            skyLightFactor *= 1.0F - level.getThunderLevel(1.0F) * (5.0F / 16.0F);
            return Mth.clamp(skyLightFactor, 0.0F, 1.0F);
        });

        result.addTimeBasedLayer(EnvironmentAttributes.SKY_COLOR, (current, tickId) ->
                AetherSkyRendering.getAetherSkyColor(level, 0.0F));

        result.addTimeBasedLayer(EnvironmentAttributes.CLOUD_COLOR, (current, tickId) ->
                AetherSkyRendering.getAetherCloudColor(level, 0.0F));
    }
}
