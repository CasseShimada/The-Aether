package com.aetherteam.aether.client.renderer.entity.layers;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.client.renderer.AetherModelLayers;
import com.aetherteam.aether.client.renderer.entity.model.SheepuffModel;
import com.aetherteam.aether.client.renderer.entity.model.SheepuffWoolModel;
import com.aetherteam.aether.client.renderer.entity.state.SheepuffRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;

/**
 * [CODE COPY] - {@link net.minecraft.client.renderer.entity.layers.SheepWoolLayer}.
 */
public class SheepuffWoolLayer extends RenderLayer<SheepuffRenderState, SheepuffModel> {
    private static final Identifier SHEEPUFF_WOOL_TEXTURE = Identifier.fromNamespaceAndPath(Aether.MODID, "textures/entity/mobs/sheepuff/sheepuff_wool.png");
    private final EntityModel<SheepuffRenderState> adultModel;
    private final EntityModel<SheepuffRenderState> babyModel;
    private final EntityModel<SheepuffRenderState> adultPuffModel;
    private final EntityModel<SheepuffRenderState> babyPuffModel;

    public SheepuffWoolLayer(RenderLayerParent<SheepuffRenderState, SheepuffModel> entityRenderer, EntityModelSet modelSet) {
        super(entityRenderer);
        this.adultModel = new SheepuffWoolModel(modelSet.bakeLayer(AetherModelLayers.SHEEPUFF_WOOL));
        this.babyModel = new SheepuffWoolModel(modelSet.bakeLayer(AetherModelLayers.SHEEPUFF_WOOL));
        this.adultPuffModel = new SheepuffWoolModel(modelSet.bakeLayer(AetherModelLayers.SHEEPUFF_WOOL_PUFFED));
        this.babyPuffModel = new SheepuffWoolModel(modelSet.bakeLayer(AetherModelLayers.SHEEPUFF_WOOL_PUFFED));
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector collector, int packedLight, SheepuffRenderState renderState, float yRot, float xRot) {
        if (!renderState.isSheared) {
            EntityModel<SheepuffRenderState> entitymodel = renderState.isBaby ? this.babyModel : this.adultModel;
            if (renderState.puff) {
                entitymodel = renderState.isBaby ? this.babyPuffModel : this.adultPuffModel;
            }
            if (renderState.isInvisible) {
                if (renderState.appearsGlowing()) {
                    collector.submitModel(entitymodel, renderState, poseStack, RenderTypes.outline(SHEEPUFF_WOOL_TEXTURE), packedLight, LivingEntityRenderer.getOverlayCoords(renderState, 0.0F), -16777216, null, renderState.outlineColor, null);
                }
            } else {
                int color;
                if (renderState.nameTag != null && "jeb_".equals(renderState.nameTag.getString())) {
                    int j = 25;
                    int k = Mth.floor(renderState.ageInTicks);
                    int l = k / j + renderState.id;
                    int i1 = DyeColor.values().length;
                    int j1 = l % i1;
                    int k1 = (l + 1) % i1;
                    float f = ((float) (k % j) + Mth.frac(renderState.ageInTicks)) / j;
                    int l1 = ARGB.opaque(DyeColor.byId(j1).getTextureDiffuseColor());
                    int i2 = ARGB.opaque(DyeColor.byId(k1).getTextureDiffuseColor());
                    color = ARGB.srgbLerp(f, l1, i2);
                } else {
                    color = ARGB.opaque(renderState.woolColor.getTextureDiffuseColor());
                }
                coloredCutoutModelCopyLayerRender(entitymodel, SHEEPUFF_WOOL_TEXTURE, poseStack, collector, packedLight, renderState, color, 0);
            }
        }
    }
}
