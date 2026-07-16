package com.aetherteam.aether.client.renderer.entity.layers;

import com.aetherteam.aether.client.ClientAccess;
import com.aetherteam.aether.client.gui.screen.perks.MoaSkinsScreen;
import com.aetherteam.aether.client.renderer.entity.model.MoaModel;
import com.aetherteam.aether.client.renderer.entity.state.MoaRenderState;
import com.aetherteam.aether.entity.passive.Moa;
import com.aetherteam.aether.perk.data.ClientMoaSkinPerkData;
import com.aetherteam.aether.perk.types.MoaData;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.UUID;

public class MoaSaddleEmissiveLayer extends RenderLayer<MoaRenderState, MoaModel> {
    private final MoaModel saddle;

    public MoaSaddleEmissiveLayer(RenderLayerParent<MoaRenderState, MoaModel> entityRenderer, MoaModel saddleModel) {
        super(entityRenderer);
        this.saddle = saddleModel;
    }

    /**
     * Renders an emissive saddle layer on a Moa if the texture from a {@link com.aetherteam.aether.perk.types.MoaSkins.MoaSkin} is present.
     *
     * @param poseStack       The rendering {@link PoseStack}.
     * @param collector       The rendering {@link SubmitNodeCollector}.
     * @param packedLight     The {@link Integer} for the packed lighting for rendering.
     * @param renderState     The {@link MoaRenderState} for the entity.
     * @param netHeadYaw      The {@link Float} for the head yaw rotation.
     * @param headPitch       The {@link Float} for the head pitch rotation.
     */
    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector collector, int packedLight, MoaRenderState renderState, float netHeadYaw, float headPitch) {
        if (renderState.saddle && !renderState.isInvisible) {
            Identifier moaSkin = this.getMoaSkinLocation(renderState);
            if (moaSkin != null) {
                this.saddle.setupAnim(renderState);
                collector.order(0).submitModel(this.saddle, renderState, poseStack, RenderTypes.eyes(moaSkin), 15728640, LivingEntityRenderer.getOverlayCoords(renderState, 0.0F), -1, null);
            }
        }
    }

    /**
     * Retrieves the saddle emissive texture for the player's {@link com.aetherteam.aether.perk.types.MoaSkins.MoaSkin}, if there is one and the player has a Moa Skin.
     *
     * @param renderState The {@link MoaRenderState} to retrieve the skin from.
     * @return The {@link Identifier} for the emissive texture.
     */
    @Nullable
    private Identifier getMoaSkinLocation(MoaRenderState renderState) {
        UUID lastRiderUUID = renderState.lastRider;
        UUID moaUUID = renderState.moaUUID;
        Map<UUID, MoaData> userSkinsData = ClientMoaSkinPerkData.INSTANCE.getClientPerkData();
        if (ClientAccess.screen(Minecraft.getInstance()) instanceof MoaSkinsScreen moaSkinsScreen && moaSkinsScreen.getSelectedSkin() != null && moaSkinsScreen.getPreviewMoa() != null && moaSkinsScreen.getPreviewMoa().getMoaUUID() != null && moaSkinsScreen.getPreviewMoa().getMoaUUID().equals(moaUUID)) {
            return moaSkinsScreen.getSelectedSkin().getSaddleEmissiveLocation();
        } else if (lastRiderUUID != null && userSkinsData.containsKey(lastRiderUUID) && userSkinsData.get(lastRiderUUID).moaSkin() != null && userSkinsData.get(lastRiderUUID).moaUUID() != null && userSkinsData.get(lastRiderUUID).moaUUID().equals(moaUUID)) {
            return userSkinsData.get(lastRiderUUID).moaSkin().getSaddleEmissiveLocation();
        }
        return null;
    }
}
