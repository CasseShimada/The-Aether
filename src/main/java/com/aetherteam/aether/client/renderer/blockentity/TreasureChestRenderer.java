package com.aetherteam.aether.client.renderer.blockentity;

import com.aetherteam.aether.blockentity.TreasureChestBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.ChestRenderer;

public class TreasureChestRenderer extends ChestRenderer<TreasureChestBlockEntity> {
    public TreasureChestRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }
}
