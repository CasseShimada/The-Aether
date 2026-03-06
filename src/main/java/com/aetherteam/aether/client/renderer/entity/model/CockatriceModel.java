package com.aetherteam.aether.client.renderer.entity.model;

import com.aetherteam.aether.client.renderer.entity.state.BipedBirdRenderState;
import net.minecraft.client.model.geom.ModelPart;

public class CockatriceModel extends BipedBirdModel<BipedBirdRenderState> {
    public CockatriceModel(ModelPart root) {
        super(root);
    }

    @Override
    public void setupAnim(BipedBirdRenderState renderState) {
        super.setupAnim(renderState);
        this.jaw.xRot = 0.35F;
    }
}
