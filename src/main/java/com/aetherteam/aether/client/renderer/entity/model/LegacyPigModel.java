package com.aetherteam.aether.client.renderer.entity.model;

import net.minecraft.client.model.animal.pig.PigModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

/**
 * 1.21.11 pig variants use 64x64 textures; Aether Phyg textures are still authored against the legacy 64x32 UV layout.
 */
public class LegacyPigModel extends PigModel {
    public LegacyPigModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createBodyLayer(CubeDeformation deformation) {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition root = meshDefinition.getRoot();

        root.addOrReplaceChild("head",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-4.0F, -4.0F, -8.0F, 8.0F, 8.0F, 8.0F, deformation)
                        .texOffs(16, 16)
                        .addBox(-2.0F, 0.0F, -9.0F, 4.0F, 3.0F, 1.0F, deformation),
                PartPose.offset(0.0F, 12.0F, -6.0F));

        root.addOrReplaceChild("body",
                CubeListBuilder.create()
                        .texOffs(28, 8)
                        .addBox(-5.0F, -10.0F, -7.0F, 10.0F, 16.0F, 8.0F, deformation),
                PartPose.offsetAndRotation(0.0F, 11.0F, 2.0F, ((float) Math.PI / 2F), 0.0F, 0.0F));

        CubeListBuilder leg = CubeListBuilder.create()
                .texOffs(0, 16)
                .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, deformation);

        root.addOrReplaceChild("right_hind_leg", leg, PartPose.offset(-3.0F, 18.0F, 7.0F));
        root.addOrReplaceChild("left_hind_leg", leg, PartPose.offset(3.0F, 18.0F, 7.0F));
        root.addOrReplaceChild("right_front_leg", leg, PartPose.offset(-3.0F, 18.0F, -5.0F));
        root.addOrReplaceChild("left_front_leg", leg, PartPose.offset(3.0F, 18.0F, -5.0F));

        return LayerDefinition.create(meshDefinition, 64, 32);
    }
}
