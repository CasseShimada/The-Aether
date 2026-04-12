package com.aetherteam.aether.client.renderer.entity.state;

import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

public class TntPresentRenderState extends EntityRenderState {
    public final BlockModelRenderState blockState = new BlockModelRenderState();
    public int fuse;
}
