package com.aetherteam.aether.mixin.mixins.client;

import com.aetherteam.aether.client.renderer.AetherModelLayers;
import com.aetherteam.aether.client.renderer.accessory.layer.ArmorStandCapeLayer;
import com.aetherteam.aether.client.renderer.accessory.model.CapeModel;
import com.aetherteam.aether.client.renderer.accessory.state.ArmorStandCapeRenderState;
import com.aetherteam.aether.item.accessories.cape.CapeItem;
import com.aetherteam.aether.client.renderer.accessory.AccessoryRendering;
import com.aetherteam.aether.mixin.mixins.client.accessor.LivingEntityRendererAccessor;
import net.minecraft.client.model.object.armorstand.ArmorStandArmorModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ArmorStandRenderer;
import net.minecraft.client.renderer.entity.state.ArmorStandRenderState;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArmorStandRenderer.class)
public abstract class ArmorStandRendererMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void aether$addCapeLayer(EntityRendererProvider.Context context, CallbackInfo ci) {
        ((LivingEntityRendererAccessor) this).aether$getLayers().add(new ArmorStandCapeLayer((ArmorStandRenderer) (Object) this, new CapeModel<>(context.bakeLayer(AetherModelLayers.CAPE))));
    }

    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/decoration/ArmorStand;Lnet/minecraft/client/renderer/entity/state/ArmorStandRenderState;F)V", at = @At("TAIL"), require = 0)
    private void aether$extractCapeAccessory(ArmorStand armorStand, ArmorStandRenderState renderState, float partialTick, CallbackInfo ci) {
        ((ArmorStandCapeRenderState) renderState).aether$setCapeAccessory(AccessoryRendering.getVisibleAccessory(armorStand, CapeItem.getStaticSlotType(), 0).copy());
    }
}
