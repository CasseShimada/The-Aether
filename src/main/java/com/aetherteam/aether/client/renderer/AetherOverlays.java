package com.aetherteam.aether.client.renderer;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.api.registers.MoaType;
import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.mojang.blaze3d.platform.Window;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

public class AetherOverlays {
    private static final Identifier OVERLAY_ELEMENT_ID = Identifier.fromNamespaceAndPath(Aether.MODID, "overlay");
    private static final Identifier LIFE_SHARD_HEARTS_ELEMENT_ID = Identifier.fromNamespaceAndPath(Aether.MODID, "life_shard_hearts");

    public static void registerOverlays() {
        HudElementRegistry.attachElementAfter(VanillaHudElements.MISC_OVERLAYS, OVERLAY_ELEMENT_ID, (guiGraphics, partialTicks) -> {
            Minecraft minecraft = Minecraft.getInstance();
            Window window = minecraft.getWindow();
            Gui gui = minecraft.gui;
            LocalPlayer player = minecraft.player;
            if (player != null) {
                AetherVignetteRendering.renderAetherPortalOverlay(guiGraphics, minecraft, player.getAttachedOrCreate(AetherDataAttachments.AETHER_PLAYER), partialTicks);
                AetherVignetteRendering.renderInebriationOverlay(guiGraphics, minecraft, window, player);
                AetherVignetteRendering.renderRemedyOverlay(guiGraphics, minecraft, window, player);
                AetherVignetteRendering.renderRepulsionOverlay(guiGraphics, minecraft, window, player);
                AetherStatusHudRendering.renderHammerCooldownOverlay(guiGraphics, minecraft, window, player);
                AetherStatusHudRendering.renderMoaJumps(guiGraphics, window, player);
            }
        });
        HudElementRegistry.attachElementAfter(VanillaHudElements.HEALTH_BAR, LIFE_SHARD_HEARTS_ELEMENT_ID, (guiGraphics, partialTicks) -> {
            Minecraft minecraft = Minecraft.getInstance();
            LocalPlayer player = minecraft.player;
            if (player != null) {
                AetherOverlayLifeShardHooks.renderSilverLifeShardHearts(guiGraphics, minecraft, minecraft.getWindow(), minecraft.gui, player);
            }
        });
    }

    /**
     * @param type The {@link MoaType} being rendered.
     * @return The {@link Identifier} of the texture that should be rendered on top of the screen.
     * Uses the default Aether jumps texture as a fallback if no other texture has been specified inside the {@link MoaType}
     */
    public static Identifier getDefaultJumpsTexture(@Nullable MoaType type) {
        return AetherStatusHudRendering.getDefaultJumpsTexture(type);
    }
}
