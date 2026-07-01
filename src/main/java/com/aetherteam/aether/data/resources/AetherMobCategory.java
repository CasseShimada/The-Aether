package com.aetherteam.aether.data.resources;

import net.minecraft.world.entity.MobCategory;

public class AetherMobCategory {
    public static final MobCategory AETHER_SURFACE_MONSTER = resolve("AETHER_AETHER_SURFACE_MONSTER", MobCategory.MONSTER);
    public static final MobCategory AETHER_DARKNESS_MONSTER = resolve("AETHER_AETHER_DARKNESS_MONSTER", MobCategory.MONSTER);
    public static final MobCategory AETHER_SKY_MONSTER = resolve("AETHER_AETHER_SKY_MONSTER", MobCategory.MONSTER);
    public static final MobCategory AETHER_AERWHALE = resolve("AETHER_AETHER_AERWHALE", MobCategory.CREATURE);

    public static boolean hasCustomSkyMonsterCategory() {
        return AETHER_SKY_MONSTER != MobCategory.MONSTER;
    }

    public static boolean hasCustomAerwhaleCategory() {
        return AETHER_AERWHALE != MobCategory.CREATURE;
    }

    private static MobCategory resolve(String name, MobCategory fallback) {
        try {
            return MobCategory.valueOf(name);
        } catch (IllegalArgumentException ignored) {
            return fallback;
        }
    }
}
