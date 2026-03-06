package com.aetherteam.aether.client.renderer.entity.model;

import com.aetherteam.aether.client.renderer.entity.state.SheepuffRenderState;
import com.aetherteam.aether.entity.passive.Sheepuff;
import net.minecraft.client.model.BabyModelTransform;
import net.minecraft.client.model.QuadrupedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

import java.util.Set;

/**
 * [CODE COPY] - {@link net.minecraft.client.model.SheepModel}
 */
public class SheepuffModel extends QuadrupedModel<SheepuffRenderState> {
    public SheepuffModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshDefinition = QuadrupedModel.createBodyMesh(12, false, false, CubeDeformation.NONE);
        PartDefinition partDefinition = meshDefinition.getRoot();
        partDefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -4.0F, -6.0F, 6.0F, 6.0F, 8.0F), PartPose.offset(0.0F, 6.0F, -8.0F));
        partDefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(28, 8).addBox(-4.0F, -10.0F, -7.0F, 8.0F, 16.0F, 6.0F), PartPose.offsetAndRotation(0.0F, 5.0F, 2.0F, Mth.HALF_PI, 0.0F, 0.0F));
        return LayerDefinition.create(meshDefinition, 64, 32);
    }

    @Override
    public void setupAnim(SheepuffRenderState renderState) {
        super.setupAnim(renderState);
        this.head.y += renderState.headEatPositionScale * 9.0F * renderState.ageScale;
        this.head.xRot = renderState.headEatAngleScale;
    }
}
