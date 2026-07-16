package com.aetherteam.aether.mixin.mixins.client;

import com.aetherteam.aether.client.gui.screen.menu.CustomPosition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.LogoRenderer;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LogoRenderer.class)
public class LogoRendererMixin {
    @WrapOperation(method = "extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IFI)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blit(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIFFIIIII)V"), require = 1)
    private void aether$adjustLogoPosition(GuiGraphicsExtractor instance, RenderPipeline pipeline, Identifier atlasLocation, int x, int y, float uOffset, float vOffset, int width, int height, int textureWidth, int textureHeight, int color, Operation<Void> original) {
        LogoRenderer renderer = (LogoRenderer) (Object) this;
        if (renderer instanceof CustomPosition customPosition) {
            original.call(instance, pipeline, atlasLocation, (int) customPosition.getXOffset(x), (int) customPosition.getYOffset(y), uOffset, vOffset, width, height, textureWidth, textureHeight, color);
        } else {
            original.call(instance, pipeline, atlasLocation, x, y, uOffset, vOffset, width, height, textureWidth, textureHeight, color);
        }
    }
}
