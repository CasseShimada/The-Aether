package com.aetherteam.aether.client.event.hooks;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;

import javax.annotation.Nullable;

public final class DungeonOverlayClientHooks {
    private DungeonOverlayClientHooks() {
    }

    /**
     * [CODE COPY] - {@link ClientLevel#animateTick(int, int, int)}.
     * Checks to set up positions and render overlays for dungeon blocks from whatever block item the player is holding.
     */
    public static void collectSubmits(LevelRenderContext context) {
        Minecraft minecraft = Minecraft.getInstance();
        renderDungeonBlockOverlays(context.poseStack(), context.submitNodeCollector(), minecraft.gameRenderer.mainCamera(), context.levelState().cameraRenderState.cullFrustum, minecraft);
    }

    public static void renderDungeonBlockOverlays(PoseStack poseStack, SubmitNodeCollector collector, Camera camera, @Nullable Frustum frustum, Minecraft minecraft) {
        if (minecraft.level != null) {
            LocalPlayer player = minecraft.player;
            ClientLevel level = minecraft.level;
            int range = 32;
            if (player != null && player.isCreative()) {
                BlockPos playerPos = player.blockPosition();
                ItemStack stack = player.getMainHandItem();
                int type = DungeonOverlayStateHooks.idForItem(stack);
                if (type != -1) {
                    DungeonOverlayStateHooks.updateTrackedPositions(playerPos, level, stack, range, type, false);
                }
                for (int i = 0; i < DungeonOverlayStateHooks.trackedTypeCount(); i++) {
                    DungeonOverlayRenderHooks.renderOverlays(DungeonOverlayStateHooks.positionsForType(i), level, poseStack, collector, camera, frustum, i);
                    DungeonOverlayStateHooks.updateTrackedPositions(playerPos, level, stack, range, i, true);
                }
            }
        }
    }
}
