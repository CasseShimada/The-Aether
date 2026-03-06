package com.aetherteam.aether.entity;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.data.resources.AetherMobCategory;
import com.aetherteam.aether.entity.block.FloatingBlockEntity;
import com.aetherteam.aether.entity.block.TntPresent;
import com.aetherteam.aether.entity.miscellaneous.CloudMinion;
import com.aetherteam.aether.entity.miscellaneous.Parachute;
import com.aetherteam.aether.entity.miscellaneous.SkyrootBoat;
import com.aetherteam.aether.entity.miscellaneous.SkyrootChestBoat;
import com.aetherteam.aether.entity.monster.*;
import com.aetherteam.aether.entity.monster.dungeon.FireMinion;
import com.aetherteam.aether.entity.monster.dungeon.Mimic;
import com.aetherteam.aether.entity.monster.dungeon.Sentry;
import com.aetherteam.aether.entity.monster.dungeon.Valkyrie;
import com.aetherteam.aether.entity.monster.dungeon.boss.Slider;
import com.aetherteam.aether.entity.monster.dungeon.boss.SunSpirit;
import com.aetherteam.aether.entity.monster.dungeon.boss.ValkyrieQueen;
import com.aetherteam.aether.entity.passive.*;
import com.aetherteam.aether.entity.projectile.PoisonNeedle;
import com.aetherteam.aether.entity.projectile.ZephyrSnowball;
import com.aetherteam.aether.entity.projectile.crystal.CloudCrystal;
import com.aetherteam.aether.entity.projectile.crystal.FireCrystal;
import com.aetherteam.aether.entity.projectile.crystal.IceCrystal;
import com.aetherteam.aether.entity.projectile.crystal.ThunderCrystal;
import com.aetherteam.aether.entity.projectile.dart.EnchantedDart;
import com.aetherteam.aether.entity.projectile.dart.GoldenDart;
import com.aetherteam.aether.entity.projectile.dart.PoisonDart;
import com.aetherteam.aether.entity.projectile.weapon.HammerProjectile;
import com.aetherteam.aether.entity.projectile.weapon.ThrownLightningKnife;
import com.aetherteam.aether.mixin.mixins.common.accessor.SpawnPlacementsAccessor;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.level.levelgen.Heightmap;
import com.aetherteam.aether.registry.DeferredHolder;
import com.aetherteam.aether.registry.DeferredRegister;

public class AetherEntityTypes {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, Aether.MODID);

    // Passive Mobs
    public static final DeferredHolder<EntityType<?>, EntityType<Phyg>> PHYG = ENTITY_TYPES.register("phyg",
            () -> EntityType.Builder.of(Phyg::new, MobCategory.CREATURE).sized(0.9F, 0.9F).clientTrackingRange(10).build(key("phyg")));

    public static final DeferredHolder<EntityType<?>, EntityType<FlyingCow>> FLYING_COW = ENTITY_TYPES.register("flying_cow",
            () -> EntityType.Builder.of(FlyingCow::new, MobCategory.CREATURE).sized(0.9F, 1.4F).clientTrackingRange(10).build(key("flying_cow")));

    public static final DeferredHolder<EntityType<?>, EntityType<Sheepuff>> SHEEPUFF = ENTITY_TYPES.register("sheepuff",
            () -> EntityType.Builder.of(Sheepuff::new, MobCategory.CREATURE).sized(0.9F, 1.3F).clientTrackingRange(10).build(key("sheepuff")));

    public static final DeferredHolder<EntityType<?>, EntityType<Moa>> MOA = ENTITY_TYPES.register("moa",
            () -> EntityType.Builder.of(Moa::new, MobCategory.CREATURE).sized(0.9F, 2.15F).clientTrackingRange(10).build(key("moa")));

    public static final DeferredHolder<EntityType<?>, EntityType<Aerbunny>> AERBUNNY = ENTITY_TYPES.register("aerbunny",
            () -> EntityType.Builder.of(Aerbunny::new, MobCategory.CREATURE).sized(0.6F, 0.5F).clientTrackingRange(10).build(key("aerbunny")));

    public static final DeferredHolder<EntityType<?>, EntityType<Aerwhale>> AERWHALE = ENTITY_TYPES.register("aerwhale",
            () -> EntityType.Builder.of(Aerwhale::new, AetherMobCategory.AETHER_AERWHALE).fireImmune().sized(3.0F, 3.0F).clientTrackingRange(10).build(key("aerwhale")));

    // Hostile Mobs
    public static final DeferredHolder<EntityType<?>, EntityType<Swet>> BLUE_SWET = ENTITY_TYPES.register("blue_swet",
            () -> EntityType.Builder.of(Swet::new, AetherMobCategory.AETHER_SURFACE_MONSTER).sized(0.9F, 0.9F).clientTrackingRange(10).build(key("blue_swet")));

    public static final DeferredHolder<EntityType<?>, EntityType<Swet>> GOLDEN_SWET = ENTITY_TYPES.register("golden_swet",
            () -> EntityType.Builder.of(Swet::new, AetherMobCategory.AETHER_SURFACE_MONSTER).sized(0.9F, 0.9F).clientTrackingRange(10).build(key("golden_swet")));

    public static final DeferredHolder<EntityType<?>, EntityType<PassiveWhirlwind>> WHIRLWIND = ENTITY_TYPES.register("whirlwind",
            () -> EntityType.Builder.of(PassiveWhirlwind::new, AetherMobCategory.AETHER_SURFACE_MONSTER).fireImmune().sized(0.6F, 0.8F).clientTrackingRange(8).build(key("whirlwind")));

    public static final DeferredHolder<EntityType<?>, EntityType<EvilWhirlwind>> EVIL_WHIRLWIND = ENTITY_TYPES.register("evil_whirlwind",
            () -> EntityType.Builder.of(EvilWhirlwind::new, AetherMobCategory.AETHER_SURFACE_MONSTER).fireImmune().sized(0.6F, 0.8F).clientTrackingRange(8).build(key("evil_whirlwind")));

    public static final DeferredHolder<EntityType<?>, EntityType<AechorPlant>> AECHOR_PLANT = ENTITY_TYPES.register("aechor_plant",
            () -> EntityType.Builder.of(AechorPlant::new, AetherMobCategory.AETHER_SURFACE_MONSTER).sized(1.0F, 1.0F).clientTrackingRange(8).build(key("aechor_plant")));

    public static final DeferredHolder<EntityType<?>, EntityType<Cockatrice>> COCKATRICE = ENTITY_TYPES.register("cockatrice",
            () -> EntityType.Builder.of(Cockatrice::new, AetherMobCategory.AETHER_DARKNESS_MONSTER).sized(0.9F, 2.15F).clientTrackingRange(10).build(key("cockatrice")));

    public static final DeferredHolder<EntityType<?>, EntityType<Zephyr>> ZEPHYR = ENTITY_TYPES.register("zephyr",
            () -> EntityType.Builder.of(Zephyr::new, AetherMobCategory.AETHER_SKY_MONSTER).sized(4.5F, 3.5F).clientTrackingRange(10).build(key("zephyr")));

    // Dungeon Mobs
    public static final DeferredHolder<EntityType<?>, EntityType<Mimic>> MIMIC = ENTITY_TYPES.register("mimic",
            () -> EntityType.Builder.of(Mimic::new, MobCategory.MONSTER).sized(1.0F, 2.0F).clientTrackingRange(8).build(key("mimic")));

    public static final DeferredHolder<EntityType<?>, EntityType<Sentry>> SENTRY = ENTITY_TYPES.register("sentry",
            () -> EntityType.Builder.of(Sentry::new, MobCategory.MONSTER).sized(0.9F, 0.9F).clientTrackingRange(10).build(key("sentry")));

    public static final DeferredHolder<EntityType<?>, EntityType<Slider>> SLIDER = ENTITY_TYPES.register("slider",
            () -> EntityType.Builder.of(Slider::new, MobCategory.MONSTER).sized(2.0F, 2.0F).fireImmune().clientTrackingRange(10).build(key("slider")));

    public static final DeferredHolder<EntityType<?>, EntityType<Valkyrie>> VALKYRIE = ENTITY_TYPES.register("valkyrie",
            () -> EntityType.Builder.of(Valkyrie::new, MobCategory.MONSTER).sized(0.8F, 1.95F).clientTrackingRange(8).build(key("valkyrie")));

    public static final DeferredHolder<EntityType<?>, EntityType<ValkyrieQueen>> VALKYRIE_QUEEN = ENTITY_TYPES.register("valkyrie_queen",
            () -> EntityType.Builder.of(ValkyrieQueen::new, MobCategory.MONSTER).sized(0.8F, 1.95F).fireImmune().clientTrackingRange(10).build(key("valkyrie_queen")));

    public static final DeferredHolder<EntityType<?>, EntityType<FireMinion>> FIRE_MINION = ENTITY_TYPES.register("fire_minion",
            () -> EntityType.Builder.of(FireMinion::new, MobCategory.MONSTER).sized(1.1F, 1.95F).fireImmune().clientTrackingRange(8).build(key("fire_minion")));

    public static final DeferredHolder<EntityType<?>, EntityType<SunSpirit>> SUN_SPIRIT = ENTITY_TYPES.register("sun_spirit",
            () -> EntityType.Builder.of(SunSpirit::new, MobCategory.MONSTER).sized(2.5F, 3.4F).fireImmune().clientTrackingRange(10).build(key("sun_spirit")));

    // Miscellaneous Entities
    public static final DeferredHolder<EntityType<?>, EntityType<SkyrootBoat>> SKYROOT_BOAT = ENTITY_TYPES.register("skyroot_boat",
            () -> EntityType.Builder.<SkyrootBoat>of(SkyrootBoat::new, MobCategory.MISC).sized(1.375F, 0.5625F).clientTrackingRange(10).build(key("skyroot_boat")));

    public static final DeferredHolder<EntityType<?>, EntityType<SkyrootChestBoat>> SKYROOT_CHEST_BOAT = ENTITY_TYPES.register("skyroot_chest_boat",
            () -> EntityType.Builder.<SkyrootChestBoat>of(SkyrootChestBoat::new, MobCategory.MISC).sized(1.375F, 0.5625F).clientTrackingRange(10).build(key("skyroot_chest_boat")));

    public static final DeferredHolder<EntityType<?>, EntityType<CloudMinion>> CLOUD_MINION = ENTITY_TYPES.register("cloud_minion",
            () -> EntityType.Builder.<CloudMinion>of(CloudMinion::new, MobCategory.MISC).sized(0.75F, 0.75F).clientTrackingRange(5).build(key("cloud_minion")));

    public static final DeferredHolder<EntityType<?>, EntityType<Parachute>> COLD_PARACHUTE = ENTITY_TYPES.register("cold_parachute",
            () -> EntityType.Builder.of(Parachute::new, MobCategory.MISC).sized(1.0F, 1.0F).clientTrackingRange(8).build(key("cold_parachute")));

    public static final DeferredHolder<EntityType<?>, EntityType<Parachute>> GOLDEN_PARACHUTE = ENTITY_TYPES.register("golden_parachute",
            () -> EntityType.Builder.of(Parachute::new, MobCategory.MISC).sized(1.0F, 1.0F).clientTrackingRange(8).build(key("golden_parachute")));

    public static final DeferredHolder<EntityType<?>, EntityType<FloatingBlockEntity>> FLOATING_BLOCK = ENTITY_TYPES.register("floating_block",
            () -> EntityType.Builder.<FloatingBlockEntity>of(FloatingBlockEntity::new, MobCategory.MISC).sized(1.0F, 1.0F).clientTrackingRange(10).updateInterval(20).build(key("floating_block")));

    public static final DeferredHolder<EntityType<?>, EntityType<TntPresent>> TNT_PRESENT = ENTITY_TYPES.register("tnt_present",
            () -> EntityType.Builder.<TntPresent>of(TntPresent::new, MobCategory.MISC).fireImmune().sized(1.0F, 1.0F).eyeHeight(0.15F).clientTrackingRange(10).updateInterval(10).build(key("tnt_present")));

    // Projectiles
    public static final DeferredHolder<EntityType<?>, EntityType<ZephyrSnowball>> ZEPHYR_SNOWBALL = ENTITY_TYPES.register("zephyr_snowball",
            () -> EntityType.Builder.<ZephyrSnowball>of(ZephyrSnowball::new, MobCategory.MISC).sized(1.0F, 1.0F).clientTrackingRange(4).updateInterval(10).build(key("zephyr_snowball")));

    public static final DeferredHolder<EntityType<?>, EntityType<CloudCrystal>> CLOUD_CRYSTAL = ENTITY_TYPES.register("cloud_crystal",
            () -> EntityType.Builder.<CloudCrystal>of(CloudCrystal::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(10).build(key("cloud_crystal")));

    public static final DeferredHolder<EntityType<?>, EntityType<FireCrystal>> FIRE_CRYSTAL = ENTITY_TYPES.register("fire_crystal",
            () -> EntityType.Builder.<FireCrystal>of(FireCrystal::new, MobCategory.MISC).sized(0.85F, 0.85F).clientTrackingRange(4).updateInterval(10).fireImmune().build(key("fire_crystal")));

    public static final DeferredHolder<EntityType<?>, EntityType<IceCrystal>> ICE_CRYSTAL = ENTITY_TYPES.register("ice_crystal",
            () -> EntityType.Builder.<IceCrystal>of(IceCrystal::new, MobCategory.MISC).sized(1.2F, 1.2F).clientTrackingRange(4).updateInterval(10).fireImmune().build(key("ice_crystal")));

    public static final DeferredHolder<EntityType<?>, EntityType<ThunderCrystal>> THUNDER_CRYSTAL = ENTITY_TYPES.register("thunder_crystal",
            () -> EntityType.Builder.<ThunderCrystal>of(ThunderCrystal::new, MobCategory.MISC).sized(0.7F, 0.7F).updateInterval(2).build(key("thunder_crystal")));

    public static final DeferredHolder<EntityType<?>, EntityType<GoldenDart>> GOLDEN_DART = ENTITY_TYPES.register("golden_dart",
            () -> EntityType.Builder.<GoldenDart>of(GoldenDart::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(20).build(key("golden_dart")));

    public static final DeferredHolder<EntityType<?>, EntityType<PoisonDart>> POISON_DART = ENTITY_TYPES.register("poison_dart",
            () -> EntityType.Builder.<PoisonDart>of(PoisonDart::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(20).build(key("poison_dart")));

    public static final DeferredHolder<EntityType<?>, EntityType<EnchantedDart>> ENCHANTED_DART = ENTITY_TYPES.register("enchanted_dart",
            () -> EntityType.Builder.<EnchantedDart>of(EnchantedDart::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(20).build(key("enchanted_dart")));

    public static final DeferredHolder<EntityType<?>, EntityType<PoisonNeedle>> POISON_NEEDLE = ENTITY_TYPES.register("poison_needle",
            () -> EntityType.Builder.<PoisonNeedle>of(PoisonNeedle::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(20).build(key("poison_needle")));

    public static final DeferredHolder<EntityType<?>, EntityType<ThrownLightningKnife>> LIGHTNING_KNIFE = ENTITY_TYPES.register("lightning_knife",
            () -> EntityType.Builder.<ThrownLightningKnife>of(ThrownLightningKnife::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10).build(key("lightning_knife")));

    public static final DeferredHolder<EntityType<?>, EntityType<HammerProjectile>> HAMMER_PROJECTILE = ENTITY_TYPES.register("hammer_projectile",
            () -> EntityType.Builder.<HammerProjectile>of(HammerProjectile::new, MobCategory.MISC).sized(0.35F, 0.35F).clientTrackingRange(4).updateInterval(10).build(key("hammer_projectile")));

    /**
     * @see Aether#eventSetup(IEventBus)
     */
    public static void registerSpawnPlacements() {
        // Passive Mobs
        SpawnPlacementsAccessor.aether$register(AetherEntityTypes.PHYG.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, AetherAnimal::checkAetherAnimalSpawnRules);
        SpawnPlacementsAccessor.aether$register(AetherEntityTypes.FLYING_COW.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, AetherAnimal::checkAetherAnimalSpawnRules);
        SpawnPlacementsAccessor.aether$register(AetherEntityTypes.SHEEPUFF.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, AetherAnimal::checkAetherAnimalSpawnRules);
        SpawnPlacementsAccessor.aether$register(AetherEntityTypes.MOA.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, AetherAnimal::checkAetherAnimalSpawnRules);
        SpawnPlacementsAccessor.aether$register(AetherEntityTypes.AERBUNNY.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, AetherAnimal::checkAetherAnimalSpawnRules);
        SpawnPlacementsAccessor.aether$register(AetherEntityTypes.AERWHALE.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Aerwhale::checkAerwhaleSpawnRules);

        // Hostile Mobs
        SpawnPlacementsAccessor.aether$register(AetherEntityTypes.BLUE_SWET.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Swet::checkSwetSpawnRules);
        SpawnPlacementsAccessor.aether$register(AetherEntityTypes.GOLDEN_SWET.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Swet::checkSwetSpawnRules);
        SpawnPlacementsAccessor.aether$register(AetherEntityTypes.WHIRLWIND.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, AbstractWhirlwind::checkWhirlwindSpawnRules);
        SpawnPlacementsAccessor.aether$register(AetherEntityTypes.EVIL_WHIRLWIND.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, AbstractWhirlwind::checkWhirlwindSpawnRules);
        SpawnPlacementsAccessor.aether$register(AetherEntityTypes.AECHOR_PLANT.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, AechorPlant::checkAechorPlantSpawnRules);
        SpawnPlacementsAccessor.aether$register(AetherEntityTypes.COCKATRICE.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Cockatrice::checkCockatriceSpawnRules);
        SpawnPlacementsAccessor.aether$register(AetherEntityTypes.ZEPHYR.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Zephyr::checkZephyrSpawnRules);
    }

    /**
     * @see Aether#eventSetup(IEventBus)
     */
    public static void registerEntityAttributes() {
        // Passive Mobs
        FabricDefaultAttributeRegistry.register(AetherEntityTypes.PHYG.get(), Phyg.createMobAttributes());
        FabricDefaultAttributeRegistry.register(AetherEntityTypes.FLYING_COW.get(), FlyingCow.createMobAttributes());
        FabricDefaultAttributeRegistry.register(AetherEntityTypes.SHEEPUFF.get(), Sheepuff.createMobAttributes());
        FabricDefaultAttributeRegistry.register(AetherEntityTypes.MOA.get(), Moa.createMobAttributes());
        FabricDefaultAttributeRegistry.register(AetherEntityTypes.AERBUNNY.get(), Aerbunny.createMobAttributes());
        FabricDefaultAttributeRegistry.register(AetherEntityTypes.AERWHALE.get(), Aerwhale.createMobAttributes());

        // Hostile Mobs
        FabricDefaultAttributeRegistry.register(AetherEntityTypes.BLUE_SWET.get(), Swet.createMobAttributes());
        FabricDefaultAttributeRegistry.register(AetherEntityTypes.GOLDEN_SWET.get(), Swet.createMobAttributes());
        FabricDefaultAttributeRegistry.register(AetherEntityTypes.WHIRLWIND.get(), AbstractWhirlwind.createMobAttributes());
        FabricDefaultAttributeRegistry.register(AetherEntityTypes.EVIL_WHIRLWIND.get(), AbstractWhirlwind.createMobAttributes());
        FabricDefaultAttributeRegistry.register(AetherEntityTypes.AECHOR_PLANT.get(), AechorPlant.createMobAttributes());
        FabricDefaultAttributeRegistry.register(AetherEntityTypes.COCKATRICE.get(), Cockatrice.createMobAttributes());
        FabricDefaultAttributeRegistry.register(AetherEntityTypes.ZEPHYR.get(), Zephyr.createMobAttributes());

        // Dungeon Mobs
        FabricDefaultAttributeRegistry.register(AetherEntityTypes.MIMIC.get(), Mimic.createMobAttributes());
        FabricDefaultAttributeRegistry.register(AetherEntityTypes.SENTRY.get(), Sentry.createMobAttributes());
        FabricDefaultAttributeRegistry.register(AetherEntityTypes.SLIDER.get(), Slider.createMobAttributes());
        FabricDefaultAttributeRegistry.register(AetherEntityTypes.VALKYRIE.get(), Valkyrie.createMobAttributes());
        FabricDefaultAttributeRegistry.register(AetherEntityTypes.VALKYRIE_QUEEN.get(), ValkyrieQueen.createMobAttributes());
        FabricDefaultAttributeRegistry.register(AetherEntityTypes.FIRE_MINION.get(), FireMinion.createMobAttributes());
        FabricDefaultAttributeRegistry.register(AetherEntityTypes.SUN_SPIRIT.get(), SunSpirit.createMobAttributes());

        // Miscellaneous Entities
        FabricDefaultAttributeRegistry.register(AetherEntityTypes.CLOUD_MINION.get(), CloudMinion.createMobAttributes());
    }

    private static ResourceKey<EntityType<?>> key(String path) {
        return ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Aether.MODID, path));
    }
}
