package com.aetherteam.aether.mixin.mixins.client;

import com.aetherteam.aether.client.renderer.accessory.state.ArmorStandCapeRenderState;
import net.minecraft.client.renderer.entity.state.ArmorStandRenderState;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ArmorStandRenderState.class)
public class ArmorStandRenderStateMixin implements ArmorStandCapeRenderState {
    @Unique
    private ItemStack aether$capeAccessory = ItemStack.EMPTY;

    @Override
    public ItemStack aether$getCapeAccessory() {
        return this.aether$capeAccessory;
    }

    @Override
    public void aether$setCapeAccessory(ItemStack stack) {
        this.aether$capeAccessory = stack;
    }
}
