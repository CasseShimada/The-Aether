package com.aetherteam.aether.client.renderer;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.attachment.AetherPlayerAttachment;
import com.aetherteam.aether.block.AetherBlocks;
import com.aetherteam.aether.blockentity.AetherBlockEntityTypes;
import com.aetherteam.aether.client.AetherClient;
import com.aetherteam.aether.client.renderer.accessory.model.CapeModel;
import com.aetherteam.aether.client.renderer.accessory.model.GlovesModel;
import com.aetherteam.aether.client.renderer.accessory.model.PendantModel;
import com.aetherteam.aether.client.renderer.blockentity.ChestMimicRenderer;
import com.aetherteam.aether.client.renderer.blockentity.SkyrootBedRenderer;
import com.aetherteam.aether.client.renderer.blockentity.TreasureChestRenderer;
import com.aetherteam.aether.client.renderer.entity.*;
import com.aetherteam.aether.client.renderer.entity.model.*;
import com.aetherteam.aether.client.renderer.player.layer.PlayerWingsLayer;
import com.aetherteam.aether.entity.AetherEntityTypes;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.monster.slime.SlimeModel;
import net.minecraft.client.model.object.boat.BoatModel;
import net.minecraft.client.model.object.chest.ChestModel;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.blockentity.BedRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;

public class AetherRenderers {
    public static void registerEntityRenderers() {
        BlockEntityRendererRegistry.register(AetherBlockEntityTypes.SKYROOT_BED.get(), SkyrootBedRenderer::new);
        BlockEntityRendererRegistry.register(AetherBlockEntityTypes.SKYROOT_SIGN.get(), (BlockEntityRendererProvider) SignRenderer::new);
        BlockEntityRendererRegistry.register(AetherBlockEntityTypes.SKYROOT_HANGING_SIGN.get(), (BlockEntityRendererProvider) HangingSignRenderer::new);
        BlockEntityRendererRegistry.register(AetherBlockEntityTypes.CHEST_MIMIC.get(), ChestMimicRenderer::new);
        BlockEntityRendererRegistry.register(AetherBlockEntityTypes.TREASURE_CHEST.get(), TreasureChestRenderer::new);

        EntityRendererRegistry.register(AetherEntityTypes.PHYG.get(), PhygRenderer::new);
        EntityRendererRegistry.register(AetherEntityTypes.FLYING_COW.get(), FlyingCowRenderer::new);
        EntityRendererRegistry.register(AetherEntityTypes.SHEEPUFF.get(), SheepuffRenderer::new);
        EntityRendererRegistry.register(AetherEntityTypes.AERBUNNY.get(), AerbunnyRenderer::new);
        EntityRendererRegistry.register(AetherEntityTypes.MOA.get(), MoaRenderer::new);
        EntityRendererRegistry.register(AetherEntityTypes.AERWHALE.get(), AerwhaleRenderer::new);

        EntityRendererRegistry.register(AetherEntityTypes.BLUE_SWET.get(), BlueSwetRenderer::new);
        EntityRendererRegistry.register(AetherEntityTypes.GOLDEN_SWET.get(), GoldenSwetRenderer::new);
        EntityRendererRegistry.register(AetherEntityTypes.WHIRLWIND.get(), WhirlwindRenderer::new);
        EntityRendererRegistry.register(AetherEntityTypes.EVIL_WHIRLWIND.get(), WhirlwindRenderer::new);
        EntityRendererRegistry.register(AetherEntityTypes.AECHOR_PLANT.get(), AechorPlantRenderer::new);
        EntityRendererRegistry.register(AetherEntityTypes.COCKATRICE.get(), CockatriceRenderer::new);
        EntityRendererRegistry.register(AetherEntityTypes.ZEPHYR.get(), ZephyrRenderer::new);

        EntityRendererRegistry.register(AetherEntityTypes.MIMIC.get(), MimicRenderer::new);
        EntityRendererRegistry.register(AetherEntityTypes.SENTRY.get(), SentryRenderer::new);
        EntityRendererRegistry.register(AetherEntityTypes.VALKYRIE.get(), ValkyrieRenderer::new);
        EntityRendererRegistry.register(AetherEntityTypes.FIRE_MINION.get(), FireMinionRenderer::new);

        EntityRendererRegistry.register(AetherEntityTypes.SLIDER.get(), SliderRenderer::new);
        EntityRendererRegistry.register(AetherEntityTypes.VALKYRIE_QUEEN.get(), ValkyrieQueenRenderer::new);
        EntityRendererRegistry.register(AetherEntityTypes.SUN_SPIRIT.get(), SunSpiritRenderer::new);

        EntityRendererRegistry.register(AetherEntityTypes.SKYROOT_BOAT.get(), (context) -> new SkyrootBoatRenderer(context, AetherModelLayers.SKYROOT_BOAT, SkyrootBoatRenderer.SKYROOT_BOAT));
        EntityRendererRegistry.register(AetherEntityTypes.SKYROOT_CHEST_BOAT.get(), (context) -> new SkyrootBoatRenderer(context, AetherModelLayers.SKYROOT_CHEST_BOAT, SkyrootBoatRenderer.SKYROOT_CHEST_BOAT));
        EntityRendererRegistry.register(AetherEntityTypes.CLOUD_MINION.get(), CloudMinionRenderer::new);
        EntityRendererRegistry.register(AetherEntityTypes.COLD_PARACHUTE.get(), (context) -> new ParachuteRenderer(context, AetherBlocks.COLD_AERCLOUD));
        EntityRendererRegistry.register(AetherEntityTypes.GOLDEN_PARACHUTE.get(), (context) -> new ParachuteRenderer(context, AetherBlocks.GOLDEN_AERCLOUD));
        EntityRendererRegistry.register(AetherEntityTypes.FLOATING_BLOCK.get(), FloatingBlockRenderer::new);
        EntityRendererRegistry.register(AetherEntityTypes.TNT_PRESENT.get(), TntPresentRenderer::new);

        EntityRendererRegistry.register(AetherEntityTypes.ZEPHYR_SNOWBALL.get(), (context) -> new ThrownItemRenderer<>(context, 3.0F, true));
        EntityRendererRegistry.register(AetherEntityTypes.CLOUD_CRYSTAL.get(), CloudCrystalRenderer::new);
        EntityRendererRegistry.register(AetherEntityTypes.FIRE_CRYSTAL.get(), FireCrystalRenderer::new);
        EntityRendererRegistry.register(AetherEntityTypes.ICE_CRYSTAL.get(), IceCrystalRenderer::new);
        EntityRendererRegistry.register(AetherEntityTypes.THUNDER_CRYSTAL.get(), ThunderCrystalRenderer::new);
        EntityRendererRegistry.register(AetherEntityTypes.GOLDEN_DART.get(), GoldenDartRenderer::new);
        EntityRendererRegistry.register(AetherEntityTypes.POISON_DART.get(), PoisonDartRenderer::new);
        EntityRendererRegistry.register(AetherEntityTypes.ENCHANTED_DART.get(), EnchantedDartRenderer::new);
        EntityRendererRegistry.register(AetherEntityTypes.POISON_NEEDLE.get(), PoisonNeedleRenderer::new);
        EntityRendererRegistry.register(AetherEntityTypes.LIGHTNING_KNIFE.get(), LightningKnifeRenderer::new);
        EntityRendererRegistry.register(AetherEntityTypes.HAMMER_PROJECTILE.get(), HammerProjectileRenderer::new);
    }

    public static void registerLayerDefinitions() {
        EntityModelLayerRegistry.registerModelLayer(AetherModelLayers.SKYROOT_BED_FOOT, BedRenderer::createFootLayer);
        EntityModelLayerRegistry.registerModelLayer(AetherModelLayers.SKYROOT_BED_HEAD, BedRenderer::createHeadLayer);
        EntityModelLayerRegistry.registerModelLayer(AetherModelLayers.CHEST_MIMIC, ChestModel::createSingleBodyLayer);

        EntityModelLayerRegistry.registerModelLayer(AetherModelLayers.PHYG, () -> LegacyPigModel.createBodyLayer(CubeDeformation.NONE));
        EntityModelLayerRegistry.registerModelLayer(AetherModelLayers.PHYG_BABY, () -> LegacyPigModel.createBodyLayer(CubeDeformation.NONE));
        EntityModelLayerRegistry.registerModelLayer(AetherModelLayers.PHYG_WINGS, () -> QuadrupedWingsModel.createMainLayer(10.0F));
        EntityModelLayerRegistry.registerModelLayer(AetherModelLayers.PHYG_SADDLE, () -> LegacyPigModel.createBodyLayer(new CubeDeformation(0.5F)));
        EntityModelLayerRegistry.registerModelLayer(AetherModelLayers.PHYG_HALO, () -> HaloModel.createLayer(3.0F, -4.0F, 12.0F, -6.0F));
        EntityModelLayerRegistry.registerModelLayer(AetherModelLayers.FLYING_COW, () -> LegacyCowModel.createBodyLayer(CubeDeformation.NONE));
        EntityModelLayerRegistry.registerModelLayer(AetherModelLayers.FLYING_COW_BABY, () -> LegacyCowModel.createBodyLayer(CubeDeformation.NONE));
        EntityModelLayerRegistry.registerModelLayer(AetherModelLayers.FLYING_COW_WINGS, () -> QuadrupedWingsModel.createMainLayer(0.0F));
        EntityModelLayerRegistry.registerModelLayer(AetherModelLayers.FLYING_COW_SADDLE, () -> LegacyCowModel.createBodyLayer(new CubeDeformation(0.5F)));
        EntityModelLayerRegistry.registerModelLayer(AetherModelLayers.SHEEPUFF, SheepuffModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(AetherModelLayers.SHEEPUFF_BABY, SheepuffModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(AetherModelLayers.SHEEPUFF_WOOL, () -> SheepuffWoolModel.createFurLayer(new CubeDeformation(1.75F), 0.0F));
        EntityModelLayerRegistry.registerModelLayer(AetherModelLayers.SHEEPUFF_WOOL_PUFFED, () -> SheepuffWoolModel.createFurLayer(new CubeDeformation(3.75F), 2.0F));
        EntityModelLayerRegistry.registerModelLayer(AetherModelLayers.AERBUNNY, AerbunnyModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(AetherModelLayers.MOA, () -> MoaModel.createBodyLayer(CubeDeformation.NONE));
        EntityModelLayerRegistry.registerModelLayer(AetherModelLayers.MOA_HAT, () -> MoaModel.createBodyLayer(new CubeDeformation(0.23F)));
        EntityModelLayerRegistry.registerModelLayer(AetherModelLayers.MOA_SADDLE, () -> MoaModel.createBodyLayer(new CubeDeformation(0.27F)));
        EntityModelLayerRegistry.registerModelLayer(AetherModelLayers.AERWHALE, AerwhaleModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(AetherModelLayers.AERWHALE_CLASSIC, ClassicAerwhaleModel::createBodyLayer);

        EntityModelLayerRegistry.registerModelLayer(AetherModelLayers.SWET, SlimeModel::createInnerBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(AetherModelLayers.SWET_OUTER, SlimeModel::createOuterBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(AetherModelLayers.AECHOR_PLANT, AechorPlantModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(AetherModelLayers.COCKATRICE, () -> CockatriceModel.createBodyLayer(CubeDeformation.NONE));
        EntityModelLayerRegistry.registerModelLayer(AetherModelLayers.ZEPHYR, ZephyrModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(AetherModelLayers.ZEPHYR_TRANSPARENCY, ZephyrModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(AetherModelLayers.ZEPHYR_CLASSIC, ClassicZephyrModel::createBodyLayer);

        EntityModelLayerRegistry.registerModelLayer(AetherModelLayers.MIMIC, MimicModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(AetherModelLayers.SENTRY, SlimeModel::createOuterBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(AetherModelLayers.VALKYRIE, ValkyrieModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(AetherModelLayers.VALKYRIE_WINGS, () -> ValkyrieWingsModel.createMainLayer(4.5F, 2.5F));
        EntityModelLayerRegistry.registerModelLayer(AetherModelLayers.FIRE_MINION, FireMinionModel::createBodyLayer);

        EntityModelLayerRegistry.registerModelLayer(AetherModelLayers.SLIDER, SliderModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(AetherModelLayers.VALKYRIE_QUEEN, ValkyrieModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(AetherModelLayers.VALKYRIE_QUEEN_WINGS, () -> ValkyrieWingsModel.createMainLayer(4.5F, 2.5F));
        EntityModelLayerRegistry.registerModelLayer(AetherModelLayers.SUN_SPIRIT, SunSpiritModel::createBodyLayer);

        EntityModelLayerRegistry.registerModelLayer(AetherModelLayers.SKYROOT_BOAT, BoatModel::createBoatModel);
        EntityModelLayerRegistry.registerModelLayer(AetherModelLayers.SKYROOT_CHEST_BOAT, BoatModel::createChestBoatModel);

        EntityModelLayerRegistry.registerModelLayer(AetherModelLayers.CLOUD_MINION, CloudMinionModel::createBodyLayer);

        EntityModelLayerRegistry.registerModelLayer(AetherModelLayers.CLOUD_CRYSTAL, CrystalModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(AetherModelLayers.THUNDER_CRYSTAL, CrystalModel::createBodyLayer);

        EntityModelLayerRegistry.registerModelLayer(AetherModelLayers.VALKYRIE_ARMOR_WINGS, () -> ValkyrieWingsModel.createMainLayer(3.5F, 3.375F));

        EntityModelLayerRegistry.registerModelLayer(AetherModelLayers.PENDANT, PendantModel::createLayer);
        EntityModelLayerRegistry.registerModelLayer(AetherModelLayers.GLOVES, () -> GlovesModel.createLayer(new CubeDeformation(0.5F), false, false));
        EntityModelLayerRegistry.registerModelLayer(AetherModelLayers.GLOVES_TRIM, () -> GlovesModel.createLayer(new CubeDeformation(0.5F), false, true));
        EntityModelLayerRegistry.registerModelLayer(AetherModelLayers.GLOVES_SLIM, () -> GlovesModel.createLayer(new CubeDeformation(0.5F), true, false));
        EntityModelLayerRegistry.registerModelLayer(AetherModelLayers.GLOVES_TRIM_SLIM, () -> GlovesModel.createLayer(new CubeDeformation(0.5F), true, true));
        EntityModelLayerRegistry.registerModelLayer(AetherModelLayers.GLOVES_FIRST_PERSON, () -> GlovesModel.createLayer(new CubeDeformation(0.25F), false, false));
        EntityModelLayerRegistry.registerModelLayer(AetherModelLayers.GLOVES_TRIM_FIRST_PERSON, () -> GlovesModel.createLayer(new CubeDeformation(0.25F), false, true));
        EntityModelLayerRegistry.registerModelLayer(AetherModelLayers.SHIELD_OF_REPULSION, () -> LayerDefinition.create(PlayerModel.createMesh(new CubeDeformation(1.1F), false), 64, 64));
        EntityModelLayerRegistry.registerModelLayer(AetherModelLayers.SHIELD_OF_REPULSION_SLIM, () -> LayerDefinition.create(PlayerModel.createMesh(new CubeDeformation(1.15F), true), 64, 64));
        EntityModelLayerRegistry.registerModelLayer(AetherModelLayers.SHIELD_OF_REPULSION_ARM, () -> LayerDefinition.create(PlayerModel.createMesh(new CubeDeformation(0.4F), false), 64, 64));
        EntityModelLayerRegistry.registerModelLayer(AetherModelLayers.CAPE, CapeModel::createLayer);

        EntityModelLayerRegistry.registerModelLayer(AetherModelLayers.PLAYER_HALO, () -> HaloModel.createLayer(0.0F, 0.0F, 0.0F, 0.0F));
    }

    /**
     * @see com.aetherteam.aether.client.AetherClient#clientSetup(FMLClientSetupEvent)
     */
    public static void registerAccessoryRenderers() {
    }

    public static void addEntityLayers() {
        LivingEntityFeatureRendererRegistrationCallback.EVENT.register((entityType, entityRenderer, registrationHelper, context) -> {
            if (entityRenderer instanceof AvatarRenderer avatarRenderer) {
                registrationHelper.register(new PlayerWingsLayer(avatarRenderer, new ValkyrieWingsModel<>(context.getModelSet().bakeLayer(AetherModelLayers.VALKYRIE_ARMOR_WINGS))));
            }
        });
    }

    public static void bakeModels() {
    }
}
