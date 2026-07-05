package com.aetherteam.aether.client.particle;

import com.aetherteam.aether.Aether;
import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry;
import net.minecraft.client.particle.SnowflakeParticle;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

public final class AetherParticleTypes {
    public static final SimpleParticleType AETHER_PORTAL = Registry.register(
            BuiltInRegistries.PARTICLE_TYPE,
            Identifier.fromNamespaceAndPath(Aether.MODID, "aether_portal"),
            new PublicSimpleParticleType(false));
    public static final SimpleParticleType CRYSTAL_LEAVES = Registry.register(
            BuiltInRegistries.PARTICLE_TYPE,
            Identifier.fromNamespaceAndPath(Aether.MODID, "crystal_leaves"),
            new PublicSimpleParticleType(false));
    public static final SimpleParticleType BOSS_DOORWAY_BLOCK = Registry.register(
            BuiltInRegistries.PARTICLE_TYPE,
            Identifier.fromNamespaceAndPath(Aether.MODID, "door"),
            new PublicSimpleParticleType(true));
    public static final SimpleParticleType EVIL_WHIRLWIND = Registry.register(
            BuiltInRegistries.PARTICLE_TYPE,
            Identifier.fromNamespaceAndPath(Aether.MODID, "evil_whirlwind"),
            new PublicSimpleParticleType(true));
    public static final SimpleParticleType FROZEN = Registry.register(
            BuiltInRegistries.PARTICLE_TYPE,
            Identifier.fromNamespaceAndPath(Aether.MODID, "frozen"),
            new PublicSimpleParticleType(false));
    public static final SimpleParticleType GOLDEN_OAK_LEAVES = Registry.register(
            BuiltInRegistries.PARTICLE_TYPE,
            Identifier.fromNamespaceAndPath(Aether.MODID, "golden_oak_leaves"),
            new PublicSimpleParticleType(false));
    public static final SimpleParticleType HOLIDAY_LEAVES = Registry.register(
            BuiltInRegistries.PARTICLE_TYPE,
            Identifier.fromNamespaceAndPath(Aether.MODID, "holiday_leaves"),
            new PublicSimpleParticleType(false));
    public static final SimpleParticleType PASSIVE_WHIRLWIND = Registry.register(
            BuiltInRegistries.PARTICLE_TYPE,
            Identifier.fromNamespaceAndPath(Aether.MODID, "passive_whirlwind"),
            new PublicSimpleParticleType(true));
    public static final SimpleParticleType ZEPHYR_SNOWFLAKE = Registry.register(
            BuiltInRegistries.PARTICLE_TYPE,
            Identifier.fromNamespaceAndPath(Aether.MODID, "zephyr_snowflake"),
            new PublicSimpleParticleType(false));

    public static void registerParticleFactories() {
        ParticleProviderRegistry registry = ParticleProviderRegistry.getInstance();
        registry.register(AETHER_PORTAL, AetherPortalParticle.Factory::new);
        registry.register(CRYSTAL_LEAVES, CrystalLeavesParticle.Factory::new);
        registry.register(BOSS_DOORWAY_BLOCK, DungeonBlockOverlayParticle.Factory::new);
        registry.register(EVIL_WHIRLWIND, EvilWhirlwindParticle.Factory::new);
        registry.register(FROZEN, FrozenParticle.Factory::new);
        registry.register(GOLDEN_OAK_LEAVES, GoldenOakLeavesParticle.Factory::new);
        registry.register(HOLIDAY_LEAVES, HolidayLeavesParticle.Factory::new);
        registry.register(PASSIVE_WHIRLWIND, PassiveWhirlwindParticle.Factory::new);
        registry.register(ZEPHYR_SNOWFLAKE, SnowflakeParticle.Provider::new);
    }

    private AetherParticleTypes() {
    }

    private static final class PublicSimpleParticleType extends SimpleParticleType {
        private PublicSimpleParticleType(boolean alwaysShow) {
            super(alwaysShow);
        }
    }

    public static void bootstrap() {
    }
}
