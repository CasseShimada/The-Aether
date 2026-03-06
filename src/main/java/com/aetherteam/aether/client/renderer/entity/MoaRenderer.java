package com.aetherteam.aether.client.renderer.entity;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.api.registers.MoaType;
import com.aetherteam.aether.client.gui.screen.perks.MoaSkinsScreen;
import com.aetherteam.aether.client.renderer.AetherModelLayers;
import com.aetherteam.aether.client.renderer.entity.layers.*;
import com.aetherteam.aether.client.renderer.entity.model.MoaModel;
import com.aetherteam.aether.client.renderer.entity.state.MoaRenderState;
import com.aetherteam.aether.data.resources.registries.AetherMoaTypes;
import com.aetherteam.aether.entity.monster.Cockatrice;
import com.aetherteam.aether.entity.passive.Moa;
import com.aetherteam.aether.perk.data.ClientMoaSkinPerkData;
import com.aetherteam.aether.perk.types.MoaData;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.UUID;

public class MoaRenderer extends MobRenderer<Moa, MoaRenderState, MoaModel> {
    private static final Identifier DEFAULT_TEXTURE = Identifier.fromNamespaceAndPath(Aether.MODID, "textures/entity/mobs/moa/white_moa.png");
    private static final Identifier MOS_TEXTURE = Identifier.fromNamespaceAndPath(Aether.MODID, "textures/entity/mobs/moa/mos.png");
    private static final Identifier RAPTOR_TEXTURE = Identifier.fromNamespaceAndPath(Aether.MODID, "textures/entity/mobs/moa/raptor.png");

    public MoaRenderer(EntityRendererProvider.Context context) {
        super(context, new MoaModel(context.bakeLayer(AetherModelLayers.MOA)), 0.7F);
        this.addLayer(new MoaEmissiveLayer(this));
        this.addLayer(new MoaHatLayer(this, new MoaModel(context.bakeLayer(AetherModelLayers.MOA_HAT))));
        this.addLayer(new MoaHatEmissiveLayer(this, new MoaModel(context.bakeLayer(AetherModelLayers.MOA_HAT))));
        this.addLayer(new MoaSaddleLayer(this, new MoaModel(context.bakeLayer(AetherModelLayers.MOA_SADDLE))));
        this.addLayer(new MoaSaddleEmissiveLayer(this, new MoaModel(context.bakeLayer(AetherModelLayers.MOA_SADDLE))));
    }

    @Override
    public MoaRenderState createRenderState() {
        return new MoaRenderState();
    }

    @Override
    public void extractRenderState(Moa entity, MoaRenderState reusedState, float partialTick) {
        super.extractRenderState(entity, reusedState, partialTick);
        reusedState.isEntityOnGround = entity.isEntityOnGround();
        reusedState.renderLegs = !entity.isSitting() || (!entity.isEntityOnGround() && entity.isSitting());
        reusedState.sitting = entity.isSitting();
        reusedState.ageInTicks = this.getWingRotation(reusedState, entity, partialTick);
        reusedState.rider = entity.getRider();
        reusedState.lastRider = entity.getLastRider();
        reusedState.moaUUID = entity.getMoaUUID();
        MoaType moaType = entity.getMoaType();
        if (moaType != null) {
            reusedState.type = entity.getMoaTypeKey();
            reusedState.location = moaType.moaTexture();
            reusedState.saddleLocation = moaType.saddleTexture();
        }
    }

    /**
     * Scales the Moa and also repositions it if it is sitting.
     *
     * @param renderState  The {@link MoaRenderState} for the entity.
     * @param poseStack    The rendering {@link PoseStack}.
     */
    @Override
    protected void scale(MoaRenderState renderState, PoseStack poseStack) {
        float moaScale = renderState.isBaby ? 1.0F : 1.8F;
        poseStack.scale(moaScale, moaScale, moaScale);
        if (renderState.sitting) {
            poseStack.translate(0.0, 0.5, 0.0);
        }
    }

    /**
     * Passes the Moa's wing rotation to the model as the "ageInTicks" parameter.
     *
     * @param renderState   The {@link MoaRenderState} for the entity.
     * @param entity    The {@link Cockatrice} entity
     * @param partialTick The {@link Float} for the game's partial ticks.
     * @return The {@link Float} for the petal rotation.
     */
    protected float getWingRotation(MoaRenderState renderState, Moa entity, float partialTick) {
        return renderState.setupWingsAnimation(entity, partialTick);
    }

    /**
     * Retrieves the texture for the Moa, whether it be from the {@link MoaType}, a player's {@link com.aetherteam.aether.perk.types.MoaSkins.MoaSkin}, or an Easter Egg skin.
     *
     * @param renderState The {@link MoaRenderState} to retrieve the skin from.
     * @return The {@link Identifier} for the emissive texture.
     */
    @Override
    public Identifier getTextureLocation(MoaRenderState renderState) {
        return getTexture(renderState);
    }

    public static Identifier getTexture(MoaRenderState renderState) {
        Identifier moaSkin = getMoaSkinLocation(renderState);
        if (moaSkin != null) {
            return moaSkin;
        }
        if (renderState.nameTag != null && renderState.nameTag.getString().equals("Mos")) {
            return MOS_TEXTURE;
        }
        if ((renderState.nameTag != null && renderState.nameTag.getString().equals("Raptor__") && renderState.type == AetherMoaTypes.BLUE)
            || (renderState.rider != null && renderState.rider.equals(UUID.fromString("c3e6871e-8e60-490a-8a8d-2bbe35ad1604")))) { // Raptor__
            return RAPTOR_TEXTURE;
        }
        Identifier moaType = renderState.location;
        return moaType == null ? DEFAULT_TEXTURE : moaType;
    }

    /**
     * Retrieves the texture for the player's {@link com.aetherteam.aether.perk.types.MoaSkins.MoaSkin}, if there is one and the player has a Moa Skin.
     *
     * @param renderState The {@link MoaRenderState} to retrieve the skin from.
     * @return The {@link Identifier} for the emissive texture.
     */
    @Nullable
    private static Identifier getMoaSkinLocation(MoaRenderState renderState) {
        UUID lastRiderUUID = renderState.lastRider;
        UUID moaUUID = renderState.moaUUID;
        Map<UUID, MoaData> userSkinsData = ClientMoaSkinPerkData.INSTANCE.getClientPerkData();
        if (Minecraft.getInstance().screen instanceof MoaSkinsScreen moaSkinsScreen && moaSkinsScreen.getSelectedSkin() != null && moaSkinsScreen.getPreviewMoa() != null && moaSkinsScreen.getPreviewMoa().getMoaUUID() != null && moaSkinsScreen.getPreviewMoa().getMoaUUID().equals(moaUUID)) {
            return moaSkinsScreen.getSelectedSkin().getSkinLocation();
        } else if (userSkinsData.containsKey(lastRiderUUID) && userSkinsData.get(lastRiderUUID).moaSkin() != null && userSkinsData.get(lastRiderUUID).moaUUID() != null && userSkinsData.get(lastRiderUUID).moaUUID().equals(moaUUID)) {
            return userSkinsData.get(lastRiderUUID).moaSkin().getSkinLocation();
        }
        return null;
    }
}
