package com.aetherteam.aether.mixin.mixins.client.accessor;

import net.minecraft.client.renderer.entity.AbstractBoatRenderer;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractBoatRenderer.class)
public interface AbstractBoatRendererAccessor {
    @Mutable
    @Accessor("texture")
    void aether$setTexture(Identifier texture);
}
