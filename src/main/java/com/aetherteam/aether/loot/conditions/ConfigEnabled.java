package com.aetherteam.aether.loot.conditions;

import com.aetherteam.aether.data.ConfigSerializationUtil;
import com.aetherteam.aether.config.BooleanConfigEntry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

/**
 * Checks if a config value is true or false for a loot table.
 */
public class ConfigEnabled implements LootItemCondition {
    public static final MapCodec<ConfigEnabled> CODEC = RecordCodecBuilder.mapCodec((builder) -> builder.group(Codec.STRING.fieldOf("config").forGetter(instance -> ConfigSerializationUtil.serialize(instance.config))).apply(builder, (e) -> new ConfigEnabled(ConfigSerializationUtil.deserialize(e))));
    private final BooleanConfigEntry config;

    public ConfigEnabled(BooleanConfigEntry config) {
        this.config = config;
    }

    @Override
    public MapCodec<? extends LootItemCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(LootContext lootContext) {
        return this.config.get();
    }

    public static LootItemCondition.Builder isEnabled(BooleanConfigEntry config) {
        return () -> new ConfigEnabled(config);
    }
}
