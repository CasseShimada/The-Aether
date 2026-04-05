package com.aetherteam.aether.mixin.mixins.client;

import com.aetherteam.aether.client.renderer.accessory.state.AvatarAccessoryRenderState;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(AvatarRenderState.class)
public class AvatarRenderStateMixin implements AvatarAccessoryRenderState {
    @Unique
    private ItemStack aether$wingAccessory = ItemStack.EMPTY;

    @Override
    public ItemStack aether$getWingAccessory() {
        return this.aether$wingAccessory;
    }

    @Override
    public void aether$setWingAccessory(ItemStack stack) {
        this.aether$wingAccessory = stack;
    }
}
