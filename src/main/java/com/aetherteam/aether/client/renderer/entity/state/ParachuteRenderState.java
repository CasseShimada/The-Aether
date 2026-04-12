package com.aetherteam.aether.client.renderer.entity.state;

import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

public class ParachuteRenderState extends EntityRenderState {
    public final MovingBlockRenderState movingBlockRenderState = new MovingBlockRenderState();
    public float yRot;
}
