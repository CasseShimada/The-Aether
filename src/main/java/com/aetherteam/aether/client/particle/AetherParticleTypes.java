package com.aetherteam.aether.client.particle;

import com.aetherteam.aether.Aether;
import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry;
import net.minecraft.client.particle.SnowflakeParticle;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

public final class AetherParticleTypes {
    public static final SimpleParticleType AETHER_PORTAL = register("aether_portal", false);
    public static final SimpleParticleType CRYSTAL_LEAVES = register("crystal_leaves", false);
    public static final SimpleParticleType BOSS_DOORWAY_BLOCK = register("door", true);
    public static final SimpleParticleType EVIL_WHIRLWIND = register("evil_whirlwind", true);
    public static final SimpleParticleType FROZEN = register("frozen", false);
    public static final SimpleParticleType GOLDEN_OAK_LEAVES = register("golden_oak_leaves", false);
    public static final SimpleParticleType HOLIDAY_LEAVES = register("holiday_leaves", false);
    public static final SimpleParticleType PASSIVE_WHIRLWIND = register("passive_whirlwind", true);
    public static final SimpleParticleType ZEPHYR_SNOWFLAKE = register("zephyr_snowflake", false);

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

    private static SimpleParticleType register(String name, boolean alwaysShow) {
        return Registry.register(BuiltInRegistries.PARTICLE_TYPE, Identifier.fromNamespaceAndPath(Aether.MODID, name), new PublicSimpleParticleType(alwaysShow));
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
