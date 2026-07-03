package com.aetherteam.aether.client.event.hooks;

import com.aetherteam.aether.data.resources.registries.AetherDimensions;
import com.aetherteam.aether.item.EquipmentUtil;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogRenderer;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import javax.annotation.Nullable;

public final class DimensionFogClientHooks {
    private DimensionFogClientHooks() {
    }

    /**
     * Halves the far fog distance in the Aether to add to the dimension's ambience, similar to beta.
     *
     * @param camera      The {@link Camera} for rendering the fog.
     * @param mode        The {@link net.minecraft.client.renderer.FogRenderer.FogMode}.
     * @param farDistance The far distance for the fog, as a {@link Float}.
     * @return The new far distance for the fog, as a {@link Float}.
     */
    @Nullable
    public static Float renderNearFog(Camera camera, FogRenderer.FogMode mode, float farDistance) {
        if (camera.entity().level() instanceof ClientLevel clientLevel) {
            if (clientLevel.dimension().equals(AetherDimensions.AETHER_LEVEL)) {
                FogType fluidState = camera.getFluidInCamera();
                if (mode == FogRenderer.FogMode.WORLD && fluidState == FogType.NONE) {
                    return farDistance / 2.0F;
                }
            }
        }
        return null;
    }

    /**
     * Increases the view distance while in lava when wearing Phoenix Armor.
     *
     * @param camera       The {@link Camera} for rendering the fog.
     * @param nearDistance The near distance for the fog, as a {@link Float}.
     * @return The new near distance for the fog, as a {@link Float}.
     */
    @Nullable
    public static Float reduceLavaFog(Camera camera, float nearDistance) {
        if (camera.entity().level() instanceof ClientLevel) {
            if (camera.entity() instanceof LivingEntity livingEntity && EquipmentUtil.hasFullPhoenixSet(livingEntity)) {
                FogType fluidState = camera.getFluidInCamera();
                if (fluidState == FogType.LAVA) {
                    return nearDistance * 5.0F;
                }
            }
        }
        return null;
    }

    /**
     * Prevents the sky fog from turning black near the void in the Aether.
     * This works with any dimension using the Aether's dimension effects.
     *
     * @param camera The {@link Camera} for rendering the fog.
     * @param red    The red value of the fog color, as a {@link Float}.
     * @param green  The green value of the fog color, as a {@link Float}.
     * @param blue   The blue value of the fog color, as a {@link Float}.
     * @return A {@link Vector3f} containing the RGB values for the fog color.
     */
    @Nullable
    public static Vector3f renderFogColors(Camera camera, float red, float green, float blue) {
        if (camera.entity().level() instanceof ClientLevel clientLevel) {
            if (clientLevel.dimension().equals(AetherDimensions.AETHER_LEVEL)) {
                ClientLevel.ClientLevelData worldInfo = clientLevel.getLevelData();
                double d0 = (camera.position().y() - (double) clientLevel.getMinY()) * worldInfo.voidDarknessOnsetRange();
                FogType fluidState = camera.getFluidInCamera();
                if (d0 < 1.0 && fluidState != FogType.LAVA) {
                    if (d0 < 0.0) {
                        d0 = 0.0;
                    }
                    d0 = d0 * d0;
                    if (d0 != 0.0) {
                        return new Vector3f((float) ((double) red / d0), (float) ((double) green / d0), (float) ((double) blue / d0));
                    }
                }
            }
        }
        return null;
    }

    /**
     * Makes the sky fog less dark in the Aether during weather.
     *
     * @param camera The {@link Camera} for rendering the fog.
     * @param red    The red value of the fog color, as a {@link Float}.
     * @param green  The green value of the fog color, as a {@link Float}.
     * @param blue   The blue value of the fog color, as a {@link Float}.
     * @return A {@link Vector3f} containing the RGB values for the fog color.
     */
    @Nullable
    public static Vector3f adjustWeatherFogColors(Camera camera, float red, float green, float blue) {
        if (camera.entity().level() instanceof ClientLevel clientLevel) {
            if (clientLevel.dimension().equals(AetherDimensions.AETHER_LEVEL)) {
                FogType fluidState = camera.getFluidInCamera();
                if (fluidState == FogType.NONE) {
                    float rainLevel = clientLevel.getRainLevel(1.0F);
                    if (rainLevel > 0.0F) {
                        float redBrightness = 1.0F + rainLevel * 0.8F;
                        float greenBrightness = 1.0F + rainLevel * 0.8F;
                        float blueBrightness = 1.0F + rainLevel * 0.56F;
                        red *= redBrightness;
                        green *= greenBrightness;
                        blue *= blueBrightness;
                    }
                    float thunderLevel = clientLevel.getThunderLevel(1.0F);
                    if (thunderLevel > 0.0F) {
                        float redBrightness = 1.0F + thunderLevel * 0.66F;
                        float greenBrightness = 1.0F + thunderLevel * 0.66F;
                        float blueBrightness = 1.0F + thunderLevel * 0.76F;
                        red *= redBrightness;
                        green *= greenBrightness;
                        blue *= blueBrightness;
                    }
                    int defaultFogColor = 0xC0D8FF;
                    var fogEntry = clientLevel.dimensionType().attributes().get(EnvironmentAttributes.FOG_COLOR);
                    if (fogEntry != null && fogEntry.argument() instanceof Integer fogColor) {
                        defaultFogColor = fogColor;
                    }
                    Vec3 defaultFog = new Vec3(ARGB.redFloat(defaultFogColor), ARGB.greenFloat(defaultFogColor), ARGB.blueFloat(defaultFogColor));
                    return new Vector3f(
                            Mth.clamp((float) Math.min(red, defaultFog.x()), 0.0F, 1.0F),
                            Mth.clamp((float) Math.min(green, defaultFog.y()), 0.0F, 1.0F),
                            Mth.clamp((float) Math.min(blue, defaultFog.z()), 0.0F, 1.0F)
                    );
                }
            }
        }
        return null;
    }
}
