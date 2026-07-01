package com.aetherteam.aether.effect;

import com.aetherteam.aether.Aether;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;

public class AetherEffects {
    public static final MobEffect INEBRIATION = register("inebriation", new InebriationEffect());
    public static final MobEffect REMEDY = register("remedy", new RemedyEffect());

    private static MobEffect register(String name, MobEffect effect) {
        return Registry.register(BuiltInRegistries.MOB_EFFECT, Identifier.fromNamespaceAndPath(Aether.MODID, name), effect);
    }
}
