package com.aetherteam.aether.client.event.hooks;

import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.attachment.AetherTimeAttachment;
import com.aetherteam.aether.data.resources.registries.AetherDimensions;
import com.aetherteam.aether.item.EquipmentUtil;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.material.FogType;
import org.apache.commons.lang3.tuple.Triple;

import javax.annotation.Nullable;

public class DimensionClientHooks {
    /**
     * Halves the far fog distance in the Aether to add to the dimension's ambience, similar to beta.
     *
     * @param camera      The {@link Camera} for rendering the fog.
     * @param mode        The {@link net.minecraft.client.renderer.FogRenderer.FogMode}.
     * @param farDistance The far distance for the fog, as a {@link Float}.
     * @return The new far distance for the fog, as a {@link Float}.
     * @see com.aetherteam.aether.client.event.listeners.DimensionClientListener#onRenderFog(ViewportEvent.RenderFog)
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
     * @see com.aetherteam.aether.client.event.listeners.DimensionClientListener#onRenderFog(ViewportEvent.RenderFog)
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
     * @return A {@link Triple} of {@link Float}s, containing the RGB values for the fog color.
     * @see com.aetherteam.aether.client.event.listeners.DimensionClientListener#onRenderFogColor(ViewportEvent.ComputeFogColor)
     */
    @Nullable
    public static Triple<Float, Float, Float> renderFogColors(Camera camera, float red, float green, float blue) {
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
                        return Triple.of((float) ((double) red / d0), (float) ((double) green / d0), (float) ((double) blue / d0));
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
     * @return A {@link Triple} of {@link Float}s, containing the RGB values for the fog color.
     * @see com.aetherteam.aether.client.event.listeners.DimensionClientListener#onRenderFogColor(ViewportEvent.ComputeFogColor)
     */
    @Nullable
    public static Triple<Float, Float, Float> adjustWeatherFogColors(Camera camera, float red, float green, float blue) {
        if (camera.entity().level() instanceof ClientLevel clientLevel) {
            if (clientLevel.dimension().equals(AetherDimensions.AETHER_LEVEL)) {
                FogType fluidState = camera.getFluidInCamera();
                if (fluidState == FogType.NONE) {
                    float rainLevel = clientLevel.getRainLevel(1.0F);
                    if (rainLevel > 0.0) { // Check for rain.
                        float f14 = 1.0F + rainLevel * 0.8F;
                        float f17 = 1.0F + rainLevel * 0.56F;
                        red *= f14;
                        green *= f14;
                        blue *= f17;
                    }
                    float thunderLevel = clientLevel.getThunderLevel(1.0F);
                    if (thunderLevel > 0.0) { // Check for thunder.
                        float f18 = 1.0F + thunderLevel * 0.66F;
                        float f19 = 1.0F + thunderLevel * 0.76F;
                        red *= f18;
                        green *= f18;
                        blue *= f19;
                    }
                    return Triple.of(red, green, blue);
                }
            }
        }
        return null;
    }

    /**
     * Ticks time in clientside Aether levels.
     *
     * @see com.aetherteam.aether.client.event.listeners.DimensionClientListener#onClientTick(ClientTickEvent.Post)
     */
    public static void tickTime() {
        ClientLevel level = Minecraft.getInstance().level;
        if (level != null && !Minecraft.getInstance().isPaused() && level.dimension().equals(AetherDimensions.AETHER_LEVEL)) {
            AetherTimeAttachment data = level.getAttachedOrCreate(AetherDataAttachments.AETHER_TIME);
            if (!data.isTimeSynced()) {
                long dayTime = data.tickTime(level) - 1; // The client always increments time by 1 every tick.
                level.getLevelData().setDayTime(dayTime);
            }
        }
    }
}
