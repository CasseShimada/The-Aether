package com.aetherteam.aether.client.renderer;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.attachment.AetherPlayerAttachment;
import com.aetherteam.aether.block.AetherBlocks;
import com.aetherteam.aether.effect.AetherEffects;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;

final class AetherOverlayVignetteHooks {
    private static final Identifier TEXTURE_INEBRIATION_VIGNETTE = Identifier.fromNamespaceAndPath(Aether.MODID, "textures/blur/inebriation_vignette.png");
    private static final Identifier TEXTURE_REMEDY_VIGNETTE = Identifier.fromNamespaceAndPath(Aether.MODID, "textures/blur/remedy_vignette.png");
    private static final Identifier TEXTURE_SHIELD_OF_REPULSION_VIGNETTE = Identifier.fromNamespaceAndPath(Aether.MODID, "textures/blur/shield_of_repulsion_vignette.png");

    private AetherOverlayVignetteHooks() {
    }

    static void renderAetherPortalOverlay(GuiGraphicsExtractor guiGraphics, Minecraft minecraft, AetherPlayerAttachment handler, DeltaTracker partialTicks) {
        if (minecraft.gui.hud.isHidden()) {
            return;
        }

        float timeInPortal = Mth.lerp(partialTicks.getGameTimeDeltaPartialTick(false), handler.getOldPortalIntensity(), handler.getPortalIntensity());
        if (timeInPortal <= 0.0F) {
            return;
        }

        if (timeInPortal < 1.0F) {
            timeInPortal *= timeInPortal;
            timeInPortal *= timeInPortal;
            timeInPortal = timeInPortal * 0.8F + 0.2F;
        }

        TextureAtlasSprite sprite = minecraft.getModelManager().getBlockStateModelSet().getParticleMaterial(AetherBlocks.AETHER_PORTAL.get().defaultBlockState()).sprite();
        int color = ARGB.color(Mth.clamp((int) (timeInPortal * 255.0F), 0, 255), 255, 255, 255);
        guiGraphics.blitSprite(RenderPipelines.GUI_NAUSEA_OVERLAY, sprite, 0, 0, guiGraphics.guiWidth(), guiGraphics.guiHeight(), color);
    }

    static void renderInebriationOverlay(GuiGraphicsExtractor guiGraphics, Minecraft minecraft, Window window, Player player) {
        if (minecraft.gui.hud.isHidden()) {
            return;
        }

        MobEffectInstance inebriation = player.getEffect(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(AetherEffects.INEBRIATION.get()));
        if (inebriation != null) {
            float inebriationDuration = (float) (inebriation.getDuration() % 50) / 50;
            float alpha = (inebriationDuration * inebriationDuration) / 5.0F + 0.4F;
            renderVignette(guiGraphics, window, minecraft.options.screenEffectScale().get(), alpha, TEXTURE_INEBRIATION_VIGNETTE);
        }
    }

    static void renderRemedyOverlay(GuiGraphicsExtractor guiGraphics, Minecraft minecraft, Window window, Player player) {
        if (minecraft.gui.hud.isHidden()) {
            return;
        }

        MobEffectInstance remedy = player.getEffect(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(AetherEffects.REMEDY.get()));
        if (remedy == null) {
            return;
        }

        int remedyStartDuration = player.getAttachedOrCreate(AetherDataAttachments.AETHER_PLAYER).getRemedyStartDuration();
        int remedyDuration = remedy.getDuration();
        if (remedyStartDuration > 0 && remedyDuration > 0) {
            float alpha = ((float) remedyDuration / remedyStartDuration) / 1.5F;
            renderVignette(guiGraphics, window, minecraft.options.screenEffectScale().get(), alpha, TEXTURE_REMEDY_VIGNETTE);
        }
    }

    static void renderRepulsionOverlay(GuiGraphicsExtractor guiGraphics, Minecraft minecraft, Window window, Player player) {
        if (minecraft.gui.hud.isHidden()) {
            return;
        }

        var handler = player.getAttachedOrCreate(AetherDataAttachments.AETHER_PLAYER);
        if (handler.getProjectileImpactedTimer() > 0) {
            float alpha = (float) handler.getProjectileImpactedTimer() / handler.getProjectileImpactedMaximum();
            renderVignette(guiGraphics, window, minecraft.options.screenEffectScale().get(), alpha, TEXTURE_SHIELD_OF_REPULSION_VIGNETTE);
        }
    }

    private static void renderVignette(GuiGraphicsExtractor guiGraphics, Window window, double effectScale, float alpha, Identifier resource) {
        alpha *= (float) Math.sqrt(effectScale);
        int color = ARGB.color(Mth.clamp((int) (alpha * 255.0F), 0, 255), 255, 255, 255);
        guiGraphics.blit(RenderPipelines.GUI_NAUSEA_OVERLAY, resource, 0, 0, 0.0F, 0.0F, window.getGuiScaledWidth(), window.getGuiScaledHeight(), window.getGuiScaledWidth(), window.getGuiScaledHeight(), color);
    }
}
