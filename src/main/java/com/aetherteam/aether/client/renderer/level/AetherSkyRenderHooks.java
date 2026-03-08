package com.aetherteam.aether.client.renderer.level;

import com.aetherteam.aether.AetherConfig;
import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.attachment.AetherTimeAttachment;
import com.aetherteam.aether.data.resources.registries.AetherDimensions;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.state.SkyRenderState;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.attribute.EnvironmentAttributes;
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

        renderState.sunAngle = getAetherSunAngle(level, partialTick);
        renderState.skyColor = getAetherSkyColor(level, partialTick);
        // Aether never uses vanilla's lower-half dark disc.
        renderState.shouldRenderDarkDisc = false;

        // Enforce overworld skybox rendering path for Aether sky states on 1.21.11.
        renderState.skybox = DimensionType.Skybox.OVERWORLD;

        if (!isCustomSkyEnabled(level)) {
            return;
        }

        // Keep horizon tint disabled while eternal day is active.
        renderState.sunriseAndSunsetColor = isEternalDay(level) ? 0 : getSunriseAndSunsetColor(renderState.sunAngle);
    }

    public static int getAetherSkyColor(ClientLevel level, float partialTick) {
        int baseSkyColor = 0x78A7FF;
        var skyColorEntry = level.dimensionType().attributes().get(EnvironmentAttributes.SKY_COLOR);
        if (skyColorEntry != null && skyColorEntry.argument() instanceof Integer skyColor) {
            baseSkyColor = skyColor;
        }

        float timeOfDay = getAetherTimeOfDay(level, partialTick);
        float dayBrightness = Mth.cos(timeOfDay * Mth.TWO_PI) * 2.0F + 0.5F;
        dayBrightness = Mth.clamp(dayBrightness, 0.0F, 1.0F);

        float red = ARGB.redFloat(baseSkyColor) * dayBrightness;
        float green = ARGB.greenFloat(baseSkyColor) * dayBrightness;
        float blue = ARGB.blueFloat(baseSkyColor) * dayBrightness;

        float rainLevel = level.getRainLevel(partialTick);
        if (rainLevel > 0.0F) {
            float rainGray = (red * 0.3F + green * 0.59F + blue * 0.11F) * 0.61F;
            float rainMix = 1.0F - rainLevel * 0.2F;
            red = red * rainMix + rainGray * (1.0F - rainMix);
            green = green * rainMix + rainGray * (1.0F - rainMix);
            blue = blue * rainMix + rainGray * (1.0F - rainMix);
        }

        float thunderLevel = level.getThunderLevel(partialTick);
        if (thunderLevel > 0.0F) {
            float thunderGray = (red * 0.3F + green * 0.59F + blue * 0.11F) * 0.48F;
            float thunderMix = 1.0F - thunderLevel * 0.21F;
            red = red * thunderMix + thunderGray * (1.0F - thunderMix);
            green = green * thunderMix + thunderGray * (1.0F - thunderMix);
            blue = blue * thunderMix + thunderGray * (1.0F - thunderMix);
        }

        return ARGB.colorFromFloat(1.0F, Mth.clamp(red, 0.0F, 1.0F), Mth.clamp(green, 0.0F, 1.0F), Mth.clamp(blue, 0.0F, 1.0F));
    }

    public static float getAetherSunAngle(ClientLevel level, float partialTick) {
        return getAetherTimeOfDay(level, partialTick) * (Mth.PI * 2.0F);
    }

    public static float getAetherTimeOfDay(ClientLevel level, float partialTick) {
        long ticksPerDay = Math.max(1L, AetherTimeAttachment.getTicksPerDay());
        long dayTime = getAetherDayTime(level);
        float timeOfDay = ((float) Math.floorMod(dayTime, ticksPerDay) + partialTick) / (float) ticksPerDay;
        float shiftedTimeOfDay = timeOfDay - 0.25F;
        if (shiftedTimeOfDay < 0.0F) {
            shiftedTimeOfDay += 1.0F;
        }
        if (shiftedTimeOfDay > 1.0F) {
            shiftedTimeOfDay -= 1.0F;
        }

        float baseTimeOfDay = shiftedTimeOfDay;
        shiftedTimeOfDay = 1.0F - (Mth.cos(shiftedTimeOfDay * Mth.PI) + 1.0F) / 2.0F;
        return baseTimeOfDay + (shiftedTimeOfDay - baseTimeOfDay) / 3.0F;
    }

    public static net.minecraft.world.phys.Vec3 getAetherSkyColorVector(ClientLevel level, float partialTick) {
        int color = getAetherSkyColor(level, partialTick);
        return new net.minecraft.world.phys.Vec3(ARGB.redFloat(color), ARGB.greenFloat(color), ARGB.blueFloat(color));
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
        long dayTime = getAetherDayTime(level) % (long) AetherTimeAttachment.getTicksPerDay();
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

    public static long getAetherDayTime(ClientLevel level) {
        if (level.hasAttached(AetherDataAttachments.AETHER_TIME)) {
            AetherTimeAttachment attachment = level.getAttachedOrCreate(AetherDataAttachments.AETHER_TIME);
            long attachmentDayTime = attachment.getDayTime();
            if (attachmentDayTime >= 0L) {
                return attachmentDayTime;
            }
            if (attachment.isEternalDay()) {
                return AetherTimeAttachment.getTicksPerDay() / 4L;
            }
        }
        if (!AetherConfig.SERVER.disable_eternal_day.get()) {
            return AetherTimeAttachment.getTicksPerDay() / 4L;
        }
        return level.getDayTime();
    }

    private static boolean isEternalDay(ClientLevel level) {
        if (level.hasAttached(AetherDataAttachments.AETHER_TIME)) {
            return level.getAttachedOrCreate(AetherDataAttachments.AETHER_TIME).isEternalDay();
        }
        return !AetherConfig.SERVER.disable_eternal_day.get();
    }
}
