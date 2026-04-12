package com.aetherteam.aether.client.particle;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.client.AetherClient;
import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry;
import net.minecraft.client.particle.SnowflakeParticle;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import com.aetherteam.aether.registry.DeferredHolder;
import com.aetherteam.aether.registry.DeferredRegister;

public class AetherParticleTypes {
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, Aether.MODID);

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> AETHER_PORTAL = PARTICLES.register("aether_portal", () -> new PublicSimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> CRYSTAL_LEAVES = PARTICLES.register("crystal_leaves", () -> new PublicSimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> BOSS_DOORWAY_BLOCK = PARTICLES.register("door", () -> new PublicSimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> EVIL_WHIRLWIND = PARTICLES.register("evil_whirlwind", () -> new PublicSimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> FROZEN = PARTICLES.register("frozen", () -> new PublicSimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> GOLDEN_OAK_LEAVES = PARTICLES.register("golden_oak_leaves", () -> new PublicSimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> HOLIDAY_LEAVES = PARTICLES.register("holiday_leaves", () -> new PublicSimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> PASSIVE_WHIRLWIND = PARTICLES.register("passive_whirlwind", () -> new PublicSimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> ZEPHYR_SNOWFLAKE = PARTICLES.register("zephyr_snowflake", () -> new PublicSimpleParticleType(false));

    public static void registerParticleFactories() {
        ParticleProviderRegistry registry = ParticleProviderRegistry.getInstance();
        registry.register(AETHER_PORTAL.get(), AetherPortalParticle.Factory::new);
        registry.register(CRYSTAL_LEAVES.get(), CrystalLeavesParticle.Factory::new);
        registry.register(BOSS_DOORWAY_BLOCK.get(), DungeonBlockOverlayParticle.Factory::new);
        registry.register(EVIL_WHIRLWIND.get(), EvilWhirlwindParticle.Factory::new);
        registry.register(FROZEN.get(), FrozenParticle.Factory::new);
        registry.register(GOLDEN_OAK_LEAVES.get(), GoldenOakLeavesParticle.Factory::new);
        registry.register(HOLIDAY_LEAVES.get(), HolidayLeavesParticle.Factory::new);
        registry.register(PASSIVE_WHIRLWIND.get(), PassiveWhirlwindParticle.Factory::new);
        registry.register(ZEPHYR_SNOWFLAKE.get(), SnowflakeParticle.Provider::new);
    }

    private static final class PublicSimpleParticleType extends SimpleParticleType {
        private PublicSimpleParticleType(boolean alwaysShow) {
            super(alwaysShow);
        }
    }
}
