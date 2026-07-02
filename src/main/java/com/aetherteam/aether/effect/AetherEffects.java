package com.aetherteam.aether.effect;

import com.aetherteam.aether.Aether;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;

public final class AetherEffects {
    public static final MobEffect INEBRIATION = Registry.register(
            BuiltInRegistries.MOB_EFFECT,
            Identifier.fromNamespaceAndPath(Aether.MODID, "inebriation"),
            new InebriationEffect());
    public static final MobEffect REMEDY = Registry.register(
            BuiltInRegistries.MOB_EFFECT,
            Identifier.fromNamespaceAndPath(Aether.MODID, "remedy"),
            new RemedyEffect());

    private AetherEffects() {
    }

    public static void bootstrap() {
    }
}
