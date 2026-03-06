package com.aetherteam.aether.client.renderer.entity.model;

import com.aetherteam.aether.client.renderer.entity.state.MoaRenderState;
import net.minecraft.client.model.geom.ModelPart;

public class MoaModel extends BipedBirdModel<MoaRenderState> {
    public MoaModel(ModelPart root) {
        super(root);
    }

    @Override
    public void setupAnim(MoaRenderState renderState) {
        super.setupAnim(renderState);
        if (renderState.sitting) {
            this.jaw.xRot = 0.0F;
        } else {
            this.jaw.xRot = 0.35F;
        }
        this.rightLeg.visible = renderState.renderLegs;
        this.leftLeg.visible = renderState.renderLegs;
    }
}
