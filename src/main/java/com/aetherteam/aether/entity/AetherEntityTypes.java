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
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.function.Function;

public final class AetherEntityTypes {
    // Passive Mobs
    public static final EntityType<Phyg> PHYG = register("phyg",
            key -> EntityType.Builder.of(Phyg::new, MobCategory.CREATURE).sized(0.9F, 0.9F).clientTrackingRange(10).build(key));

    public static final EntityType<FlyingCow> FLYING_COW = register("flying_cow",
            key -> EntityType.Builder.of(FlyingCow::new, MobCategory.CREATURE).sized(0.9F, 1.4F).clientTrackingRange(10).build(key));

    public static final EntityType<Sheepuff> SHEEPUFF = register("sheepuff",
            key -> EntityType.Builder.of(Sheepuff::new, MobCategory.CREATURE).sized(0.9F, 1.3F).clientTrackingRange(10).build(key));

    public static final EntityType<Moa> MOA = register("moa",
            key -> EntityType.Builder.of(Moa::new, MobCategory.CREATURE).sized(0.9F, 2.15F).clientTrackingRange(10).build(key));

    public static final EntityType<Aerbunny> AERBUNNY = register("aerbunny",
            key -> EntityType.Builder.of(Aerbunny::new, MobCategory.CREATURE).sized(0.6F, 0.5F).clientTrackingRange(10).build(key));

    public static final EntityType<Aerwhale> AERWHALE = register("aerwhale",
            key -> EntityType.Builder.of(Aerwhale::new, AetherMobCategory.AETHER_AERWHALE).fireImmune().sized(3.0F, 3.0F).clientTrackingRange(10).build(key));

    // Hostile Mobs
    public static final EntityType<Swet> BLUE_SWET = register("blue_swet",
            key -> EntityType.Builder.of(Swet::new, AetherMobCategory.AETHER_SURFACE_MONSTER).sized(0.9F, 0.9F).clientTrackingRange(10).build(key));

    public static final EntityType<Swet> GOLDEN_SWET = register("golden_swet",
            key -> EntityType.Builder.of(Swet::new, AetherMobCategory.AETHER_SURFACE_MONSTER).sized(0.9F, 0.9F).clientTrackingRange(10).build(key));

    public static final EntityType<PassiveWhirlwind> WHIRLWIND = register("whirlwind",
            key -> EntityType.Builder.of(PassiveWhirlwind::new, AetherMobCategory.AETHER_SURFACE_MONSTER).fireImmune().sized(0.6F, 0.8F).clientTrackingRange(8).build(key));

    public static final EntityType<EvilWhirlwind> EVIL_WHIRLWIND = register("evil_whirlwind",
            key -> EntityType.Builder.of(EvilWhirlwind::new, AetherMobCategory.AETHER_SURFACE_MONSTER).fireImmune().sized(0.6F, 0.8F).clientTrackingRange(8).build(key));

    public static final EntityType<AechorPlant> AECHOR_PLANT = register("aechor_plant",
            key -> EntityType.Builder.of(AechorPlant::new, AetherMobCategory.AETHER_SURFACE_MONSTER).sized(1.0F, 1.0F).clientTrackingRange(8).build(key));

    public static final EntityType<Cockatrice> COCKATRICE = register("cockatrice",
            key -> EntityType.Builder.of(Cockatrice::new, AetherMobCategory.AETHER_DARKNESS_MONSTER).sized(0.9F, 2.15F).clientTrackingRange(10).build(key));

    public static final EntityType<Zephyr> ZEPHYR = register("zephyr",
            key -> EntityType.Builder.of(Zephyr::new, AetherMobCategory.AETHER_SKY_MONSTER).sized(4.5F, 3.5F).clientTrackingRange(10).build(key));

    // Dungeon Mobs
    public static final EntityType<Mimic> MIMIC = register("mimic",
            key -> EntityType.Builder.of(Mimic::new, MobCategory.MONSTER).sized(1.0F, 2.0F).clientTrackingRange(8).build(key));

    public static final EntityType<Sentry> SENTRY = register("sentry",
            key -> EntityType.Builder.of(Sentry::new, MobCategory.MONSTER).sized(0.9F, 0.9F).clientTrackingRange(10).build(key));

    public static final EntityType<Slider> SLIDER = register("slider",
            key -> EntityType.Builder.of(Slider::new, MobCategory.MONSTER).sized(2.0F, 2.0F).fireImmune().clientTrackingRange(10).build(key));

    public static final EntityType<Valkyrie> VALKYRIE = register("valkyrie",
            key -> EntityType.Builder.of(Valkyrie::new, MobCategory.MONSTER).sized(0.8F, 1.95F).clientTrackingRange(8).build(key));

    public static final EntityType<ValkyrieQueen> VALKYRIE_QUEEN = register("valkyrie_queen",
            key -> EntityType.Builder.of(ValkyrieQueen::new, MobCategory.MONSTER).sized(0.8F, 1.95F).fireImmune().clientTrackingRange(10).build(key));

    public static final EntityType<FireMinion> FIRE_MINION = register("fire_minion",
            key -> EntityType.Builder.of(FireMinion::new, MobCategory.MONSTER).sized(1.1F, 1.95F).fireImmune().clientTrackingRange(8).build(key));

    public static final EntityType<SunSpirit> SUN_SPIRIT = register("sun_spirit",
            key -> EntityType.Builder.of(SunSpirit::new, MobCategory.MONSTER).sized(2.5F, 3.4F).fireImmune().clientTrackingRange(10).build(key));

    // Miscellaneous Entities
    public static final EntityType<SkyrootBoat> SKYROOT_BOAT = register("skyroot_boat",
            key -> EntityType.Builder.<SkyrootBoat>of(SkyrootBoat::new, MobCategory.MISC).sized(1.375F, 0.5625F).clientTrackingRange(10).build(key));

    public static final EntityType<SkyrootChestBoat> SKYROOT_CHEST_BOAT = register("skyroot_chest_boat",
            key -> EntityType.Builder.<SkyrootChestBoat>of(SkyrootChestBoat::new, MobCategory.MISC).sized(1.375F, 0.5625F).clientTrackingRange(10).build(key));

    public static final EntityType<CloudMinion> CLOUD_MINION = register("cloud_minion",
            key -> EntityType.Builder.<CloudMinion>of(CloudMinion::new, MobCategory.MISC).sized(0.75F, 0.75F).clientTrackingRange(5).build(key));

    public static final EntityType<Parachute> COLD_PARACHUTE = register("cold_parachute",
            key -> EntityType.Builder.of(Parachute::new, MobCategory.MISC).sized(1.0F, 1.0F).clientTrackingRange(8).build(key));

    public static final EntityType<Parachute> GOLDEN_PARACHUTE = register("golden_parachute",
            EntityType.Builder.of(Parachute::new, MobCategory.MISC).sized(1.0F, 1.0F).clientTrackingRange(8).build(key("golden_parachute")));

    public static final EntityType<FloatingBlockEntity> FLOATING_BLOCK = register("floating_block",
            EntityType.Builder.<FloatingBlockEntity>of(FloatingBlockEntity::new, MobCategory.MISC).sized(1.0F, 1.0F).clientTrackingRange(10).updateInterval(20).build(key("floating_block")));

    public static final EntityType<TntPresent> TNT_PRESENT = register("tnt_present",
            EntityType.Builder.<TntPresent>of(TntPresent::new, MobCategory.MISC).fireImmune().sized(1.0F, 1.0F).eyeHeight(0.15F).clientTrackingRange(10).updateInterval(10).build(key("tnt_present")));

    // Projectiles
    public static final EntityType<ZephyrSnowball> ZEPHYR_SNOWBALL = register("zephyr_snowball",
            EntityType.Builder.<ZephyrSnowball>of(ZephyrSnowball::new, MobCategory.MISC).sized(1.0F, 1.0F).clientTrackingRange(4).updateInterval(10).build(key("zephyr_snowball")));

    public static final EntityType<CloudCrystal> CLOUD_CRYSTAL = register("cloud_crystal",
            EntityType.Builder.<CloudCrystal>of(CloudCrystal::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(10).build(key("cloud_crystal")));

    public static final EntityType<FireCrystal> FIRE_CRYSTAL = register("fire_crystal",
            EntityType.Builder.<FireCrystal>of(FireCrystal::new, MobCategory.MISC).sized(0.85F, 0.85F).clientTrackingRange(4).updateInterval(10).fireImmune().build(key("fire_crystal")));

    public static final EntityType<IceCrystal> ICE_CRYSTAL = register("ice_crystal",
            EntityType.Builder.<IceCrystal>of(IceCrystal::new, MobCategory.MISC).sized(1.2F, 1.2F).clientTrackingRange(4).updateInterval(10).fireImmune().build(key("ice_crystal")));

    public static final EntityType<ThunderCrystal> THUNDER_CRYSTAL = register("thunder_crystal",
            EntityType.Builder.<ThunderCrystal>of(ThunderCrystal::new, MobCategory.MISC).sized(0.7F, 0.7F).updateInterval(2).build(key("thunder_crystal")));

    public static final EntityType<GoldenDart> GOLDEN_DART = register("golden_dart",
            EntityType.Builder.<GoldenDart>of(GoldenDart::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(20).build(key("golden_dart")));

    public static final EntityType<PoisonDart> POISON_DART = register("poison_dart",
            EntityType.Builder.<PoisonDart>of(PoisonDart::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(20).build(key("poison_dart")));

    public static final EntityType<EnchantedDart> ENCHANTED_DART = register("enchanted_dart",
            EntityType.Builder.<EnchantedDart>of(EnchantedDart::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(20).build(key("enchanted_dart")));

    public static final EntityType<PoisonNeedle> POISON_NEEDLE = register("poison_needle",
            EntityType.Builder.<PoisonNeedle>of(PoisonNeedle::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(20).build(key("poison_needle")));

    public static final EntityType<ThrownLightningKnife> LIGHTNING_KNIFE = register("lightning_knife",
            EntityType.Builder.<ThrownLightningKnife>of(ThrownLightningKnife::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10).build(key("lightning_knife")));

    public static final EntityType<HammerProjectile> HAMMER_PROJECTILE = register("hammer_projectile",
            EntityType.Builder.<HammerProjectile>of(HammerProjectile::new, MobCategory.MISC).sized(0.35F, 0.35F).clientTrackingRange(4).updateInterval(10).build(key("hammer_projectile")));

    public static void registerSpawnPlacements() {
        // Passive Mobs
        SpawnPlacementsAccessor.aether$register(AetherEntityTypes.PHYG, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, AetherAnimal::checkAetherAnimalSpawnRules);
        SpawnPlacementsAccessor.aether$register(AetherEntityTypes.FLYING_COW, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, AetherAnimal::checkAetherAnimalSpawnRules);
        SpawnPlacementsAccessor.aether$register(AetherEntityTypes.SHEEPUFF, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, AetherAnimal::checkAetherAnimalSpawnRules);
        SpawnPlacementsAccessor.aether$register(AetherEntityTypes.MOA, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, AetherAnimal::checkAetherAnimalSpawnRules);
        SpawnPlacementsAccessor.aether$register(AetherEntityTypes.AERBUNNY, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, AetherAnimal::checkAetherAnimalSpawnRules);
        SpawnPlacementsAccessor.aether$register(AetherEntityTypes.AERWHALE, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Aerwhale::checkAerwhaleSpawnRules);

        // Hostile Mobs
        SpawnPlacementsAccessor.aether$register(AetherEntityTypes.BLUE_SWET, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Swet::checkSwetSpawnRules);
        SpawnPlacementsAccessor.aether$register(AetherEntityTypes.GOLDEN_SWET, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Swet::checkSwetSpawnRules);
        SpawnPlacementsAccessor.aether$register(AetherEntityTypes.WHIRLWIND, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, AbstractWhirlwind::checkWhirlwindSpawnRules);
        SpawnPlacementsAccessor.aether$register(AetherEntityTypes.EVIL_WHIRLWIND, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, AbstractWhirlwind::checkWhirlwindSpawnRules);
        SpawnPlacementsAccessor.aether$register(AetherEntityTypes.AECHOR_PLANT, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, AechorPlant::checkAechorPlantSpawnRules);
        SpawnPlacementsAccessor.aether$register(AetherEntityTypes.COCKATRICE, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Cockatrice::checkCockatriceSpawnRules);
        SpawnPlacementsAccessor.aether$register(AetherEntityTypes.ZEPHYR, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Zephyr::checkZephyrSpawnRules);
    }

    public static void registerEntityAttributes() {
        // Passive Mobs
        FabricDefaultAttributeRegistry.register(AetherEntityTypes.PHYG, Phyg.createMobAttributes());
        FabricDefaultAttributeRegistry.register(AetherEntityTypes.FLYING_COW, FlyingCow.createMobAttributes());
        FabricDefaultAttributeRegistry.register(AetherEntityTypes.SHEEPUFF, Sheepuff.createMobAttributes());
        FabricDefaultAttributeRegistry.register(AetherEntityTypes.MOA, Moa.createMobAttributes());
        FabricDefaultAttributeRegistry.register(AetherEntityTypes.AERBUNNY, Aerbunny.createMobAttributes());
        FabricDefaultAttributeRegistry.register(AetherEntityTypes.AERWHALE, Aerwhale.createMobAttributes());

        // Hostile Mobs
        FabricDefaultAttributeRegistry.register(AetherEntityTypes.BLUE_SWET, Swet.createMobAttributes());
        FabricDefaultAttributeRegistry.register(AetherEntityTypes.GOLDEN_SWET, Swet.createMobAttributes());
        FabricDefaultAttributeRegistry.register(AetherEntityTypes.WHIRLWIND, AbstractWhirlwind.createMobAttributes());
        FabricDefaultAttributeRegistry.register(AetherEntityTypes.EVIL_WHIRLWIND, AbstractWhirlwind.createMobAttributes());
        FabricDefaultAttributeRegistry.register(AetherEntityTypes.AECHOR_PLANT, AechorPlant.createMobAttributes());
        FabricDefaultAttributeRegistry.register(AetherEntityTypes.COCKATRICE, Cockatrice.createMobAttributes());
        FabricDefaultAttributeRegistry.register(AetherEntityTypes.ZEPHYR, Zephyr.createMobAttributes());

        // Dungeon Mobs
        FabricDefaultAttributeRegistry.register(AetherEntityTypes.MIMIC, Mimic.createMobAttributes());
        FabricDefaultAttributeRegistry.register(AetherEntityTypes.SENTRY, Sentry.createMobAttributes());
        FabricDefaultAttributeRegistry.register(AetherEntityTypes.SLIDER, Slider.createMobAttributes());
        FabricDefaultAttributeRegistry.register(AetherEntityTypes.VALKYRIE, Valkyrie.createMobAttributes());
        FabricDefaultAttributeRegistry.register(AetherEntityTypes.VALKYRIE_QUEEN, ValkyrieQueen.createMobAttributes());
        FabricDefaultAttributeRegistry.register(AetherEntityTypes.FIRE_MINION, FireMinion.createMobAttributes());
        FabricDefaultAttributeRegistry.register(AetherEntityTypes.SUN_SPIRIT, SunSpirit.createMobAttributes());

        // Miscellaneous Entities
        FabricDefaultAttributeRegistry.register(AetherEntityTypes.CLOUD_MINION, CloudMinion.createMobAttributes());
    }

    private static ResourceKey<EntityType<?>> key(String path) {
        return ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Aether.MODID, path));
    }

    private static <T extends net.minecraft.world.entity.Entity> EntityType<T> register(String name, EntityType<T> type) {
        return Registry.register(BuiltInRegistries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Aether.MODID, name), type);
    }

    private static <T extends net.minecraft.world.entity.Entity> EntityType<T> register(String name, Function<ResourceKey<EntityType<?>>, EntityType<T>> factory) {
        Identifier id = Identifier.fromNamespaceAndPath(Aether.MODID, name);
        ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, id);
        return Registry.register(BuiltInRegistries.ENTITY_TYPE, id, factory.apply(key));
    }

    private AetherEntityTypes() {
    }

    public static void bootstrap() {
    }
}
