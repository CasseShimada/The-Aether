package com.aetherteam.aether.client.event.hooks;

import com.aetherteam.aether.Aether;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public class LevelClientHooks {
    /**
     * [CODE COPY] - {@link ClientLevel#animateTick(int, int, int)}.
     * Checks to set up positions and render overlays for dungeon blocks from whatever block item the player is holding.
     */
    public static void renderDungeonBlockOverlays(PoseStack poseStack, SubmitNodeCollector collector, Camera camera, @Nullable Frustum frustum, Minecraft minecraft) {
        if (minecraft.level != null) {
            LocalPlayer player = minecraft.player;
            ClientLevel level = minecraft.level;
            int range = 32; // Range for how far the overlays can be rendered at.
            if (player != null && player.isCreative()) {
                BlockPos playerPos = player.blockPosition();
                ItemStack stack = player.getMainHandItem();
                int type = DungeonOverlayStateHooks.idForItem(stack); // Get an ID for the currently held dungeon block item.
                if (type != -1) {
                    DungeonOverlayStateHooks.updateTrackedPositions(playerPos, level, stack, range, type, false); // Check to add overlays to the map.
                }
                for (int i = 0; i < DungeonOverlayStateHooks.trackedTypeCount(); i++) {
                    DungeonOverlayRenderHooks.renderOverlays(DungeonOverlayStateHooks.positionsForType(i), level, poseStack, collector, camera, frustum, i); // Render any overlays at positions in the map.
                    DungeonOverlayStateHooks.updateTrackedPositions(playerPos, level, stack, range, i, true); // Check to remove overlays from the map.
                }
            }
        }
    }
}
