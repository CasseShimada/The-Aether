package com.aetherteam.aether.client.renderer.level;

import com.aetherteam.aether.AetherConfig;
import com.aetherteam.aether.attachment.AetherTimeAttachment;
import com.aetherteam.aether.data.resources.registries.AetherDimensions;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.state.SkyRenderState;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.level.dimension.DimensionType;

import javax.annotation.Nullable;

public final class AetherSkyRenderHooks {
    private AetherSkyRenderHooks() {
    }

    public static boolean isAetherLevel(@Nullable ClientLevel level) {
        return level != null && level.dimension().equals(AetherDimensions.AETHER_LEVEL);
    }

    public static boolean isCustomSkyEnabled(@Nullable ClientLevel level) {
        return isAetherLevel(level) && !AetherConfig.CLIENT.disable_aether_skybox.get();
    }

    public static void adjustSkyRenderState(ClientLevel level, float partialTick, SkyRenderState renderState) {
        if (!isAetherLevel(level)) {
            return;
        }

        // Enforce overworld skybox rendering path for Aether sky states on 1.21.11.
        renderState.skybox = DimensionType.Skybox.OVERWORLD;

        if (!isCustomSkyEnabled(level)) {
            return;
        }

        renderState.sunriseAndSunsetColor = getSunriseAndSunsetColor(renderState.sunAngle);
        renderState.shouldRenderDarkDisc = false;
    }

    public static int getSunriseAndSunsetColor(float sunAngle) {
        float cosine = Mth.cos(sunAngle);
        if (cosine < -0.4F || cosine > 0.4F) {
            return 0;
        }

        float blend = (cosine / 0.4F) * 0.5F + 0.5F;
        float alpha = 1.0F - (1.0F - Mth.sin(blend * Mth.PI)) * 0.99F;
        alpha *= alpha;

        if (AetherConfig.CLIENT.green_sunset.get()) {
            float red = blend * 0.5F;
            float green = blend * blend * 0.3F + 0.3F;
            float blue = blend * blend * 0.5F + 0.3F;
            return ARGB.colorFromFloat(alpha, red, green, blue);
        }

        float red = blend * 0.3F + 0.65F;
        float green = blend * blend * 0.7F + 0.25F;
        float blue = 0.4F;
        return ARGB.colorFromFloat(alpha, red, green, blue);
    }

    public static float[] getCelestialOpacities(ClientLevel level, float rainBrightness) {
        long dayTime = level.getDayTime() % (long) AetherTimeAttachment.getTicksPerDay();
        long multiplier = AetherTimeAttachment.getTicksPerDayMultiplier();

        float sunOpacity;
        float moonOpacity;
        if (dayTime > 23800L * multiplier) {
            dayTime -= 23800L * multiplier;
            float transition = Mth.clamp(dayTime * 0.00167F, 0.0F, 1.0F);
            sunOpacity = transition;
            moonOpacity = 1.0F - transition;
        } else if (dayTime > 12800L * multiplier) {
            dayTime -= 12800L * multiplier;
            float transition = Mth.clamp(dayTime * 0.00167F, 0.0F, 1.0F);
            sunOpacity = 1.0F - transition;
            moonOpacity = transition;
        } else {
            sunOpacity = 1.0F;
            moonOpacity = 0.0F;
        }

        float rainPenalty = 1.0F - Mth.clamp(rainBrightness, 0.0F, 1.0F);
        sunOpacity = Mth.clamp(sunOpacity - rainPenalty, 0.0F, 1.0F);
        moonOpacity = Mth.clamp(moonOpacity - rainPenalty, 0.0F, 1.0F);
        return new float[]{sunOpacity, moonOpacity};
    }
}
