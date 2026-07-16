package com.aetherteam.aether.client.renderer.level;

import com.aetherteam.aether.Aether;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import java.util.List;

final class DungeonOverlayRenderer {
    private DungeonOverlayRenderer() {
    }

    static void renderOverlays(List<BlockPos> positions, net.minecraft.client.multiplayer.ClientLevel level, PoseStack poseStack, SubmitNodeCollector collector, Camera camera, @Nullable Frustum frustum, int type) {
        for (BlockPos blockPos : positions) {
            if ((frustum == null || frustum.isVisible(new AABB(blockPos))) && level.getBlockState(blockPos).getRenderShape() != RenderShape.INVISIBLE) {
                TextureAtlasSprite sprite = spriteForId(type);
                if (sprite != null) {
                    collector.submitCustomGeometry(poseStack, RenderTypes.cutoutMovingBlock(), (pose, builder) ->
                            drawSurfaces(builder, pose, sprite, blockPos, camera,
                                    (float) (blockPos.getX() - camera.position().x()) - 0.001F,
                                    (float) (blockPos.getZ() - camera.position().z()) - 0.001F,
                                    (float) (blockPos.getX() - camera.position().x()) + 1.001F,
                                    (float) (blockPos.getZ() - camera.position().z()) + 1.001F,
                                    (float) (blockPos.getY() - camera.position().y()) - 0.001F,
                                    (float) (blockPos.getY() - camera.position().y()) + 1.001F));
                }
            }
        }
    }

    private static void drawSurfaces(VertexConsumer builder, PoseStack.Pose pose, TextureAtlasSprite sprite, BlockPos blockPos, Camera camera, float startX, float startZ, float endX, float endZ, float botY, float topY) {
        float minU = sprite.getU1();
        float maxU = sprite.getU0();
        float minV = sprite.getV1();
        float maxV = sprite.getV0();

        if (camera.position().y() < blockPos.getY() + botY) {
            buildVertex(builder, pose, startX, botY, startZ, minU, minV, 0, -1, 0);
            buildVertex(builder, pose, endX, botY, startZ, maxU, minV, 0, -1, 0);
            buildVertex(builder, pose, endX, botY, endZ, maxU, maxV, 0, -1, 0);
            buildVertex(builder, pose, startX, botY, endZ, minU, maxV, 0, -1, 0);
        }

        if (camera.position().y() > blockPos.getY() + topY) {
            buildVertex(builder, pose, endX, topY, startZ, minU, minV, 0, 1, 0);
            buildVertex(builder, pose, startX, topY, startZ, maxU, minV, 0, 1, 0);
            buildVertex(builder, pose, startX, topY, endZ, maxU, maxV, 0, 1, 0);
            buildVertex(builder, pose, endX, topY, endZ, minU, maxV, 0, 1, 0);
        }

        if (camera.position().z() < blockPos.getZ() + startZ) {
            buildVertex(builder, pose, startX, botY, startZ, minU, minV, 0, 0, -1);
            buildVertex(builder, pose, startX, topY, startZ, minU, maxV, 0, 0, -1);
            buildVertex(builder, pose, endX, topY, startZ, maxU, maxV, 0, 0, -1);
            buildVertex(builder, pose, endX, botY, startZ, maxU, minV, 0, 0, -1);
        }

        if (camera.position().z() > blockPos.getZ() + endZ) {
            buildVertex(builder, pose, endX, botY, endZ, minU, minV, 0, 0, 1);
            buildVertex(builder, pose, endX, topY, endZ, minU, maxV, 0, 0, 1);
            buildVertex(builder, pose, startX, topY, endZ, maxU, maxV, 0, 0, 1);
            buildVertex(builder, pose, startX, botY, endZ, maxU, minV, 0, 0, 1);
        }

        if (camera.position().x() < blockPos.getX() + startX) {
            buildVertex(builder, pose, startX, botY, endZ, minU, minV, -1, 0, 0);
            buildVertex(builder, pose, startX, topY, endZ, minU, maxV, -1, 0, 0);
            buildVertex(builder, pose, startX, topY, startZ, maxU, maxV, -1, 0, 0);
            buildVertex(builder, pose, startX, botY, startZ, maxU, minV, -1, 0, 0);
        }

        if (camera.position().x() > blockPos.getX() + endX) {
            buildVertex(builder, pose, endX, botY, startZ, minU, minV, 1, 0, 0);
            buildVertex(builder, pose, endX, topY, startZ, minU, maxV, 1, 0, 0);
            buildVertex(builder, pose, endX, topY, endZ, maxU, maxV, 1, 0, 0);
            buildVertex(builder, pose, endX, botY, endZ, maxU, minV, 1, 0, 0);
        }
    }

    private static void buildVertex(VertexConsumer builder, PoseStack.Pose pose, float x, float y, float z, float u, float v, float normalX, float normalY, float normalZ) {
        builder.addVertex(pose, x, y, z).setColor(0xFF, 0xFF, 0xFF, 0xAA).setUv(u, v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(240).setNormal(pose, normalX, normalY, normalZ);
    }

    @Nullable
    private static TextureAtlasSprite spriteForId(int id) {
        TextureAtlas blockAtlas = Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.BLOCKS);
        return switch (id) {
            case 0 -> blockAtlas.getSprite(Identifier.fromNamespaceAndPath(Aether.MODID, "block/dungeon/lock"));
            case 1 -> blockAtlas.getSprite(Identifier.fromNamespaceAndPath(Aether.MODID, "block/dungeon/exclamation"));
            case 2 -> blockAtlas.getSprite(Identifier.fromNamespaceAndPath(Aether.MODID, "block/dungeon/door"));
            case 3 -> blockAtlas.getSprite(Identifier.fromNamespaceAndPath(Aether.MODID, "block/dungeon/treasure"));
            default -> null;
        };
    }
}
