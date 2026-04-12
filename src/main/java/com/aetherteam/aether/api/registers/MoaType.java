package com.aetherteam.aether.api.registers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public record MoaType(Item eggItem, int maxJumps, float speed, int spawnChance, Identifier moaTexture, Identifier saddleTexture, Optional<Identifier> jumpsTexture) {
    public static final Codec<MoaType> CODEC =
        RecordCodecBuilder.create(in -> in.group(
            BuiltInRegistries.ITEM.byNameCodec().fieldOf("egg").forGetter(MoaType::eggItem),
            Codec.INT.fieldOf("max_jumps").forGetter(MoaType::maxJumps),
            Codec.FLOAT.fieldOf("speed").forGetter(MoaType::speed),
            Codec.INT.fieldOf("spawn_chance").forGetter(MoaType::spawnChance),
            Identifier.CODEC.fieldOf("moa_texture").forGetter(MoaType::moaTexture),
            Identifier.CODEC.fieldOf("saddle_texture").forGetter(MoaType::saddleTexture),
            Identifier.CODEC.optionalFieldOf("jumps_texture").forGetter(MoaType::jumpsTexture)
        ).apply(in, MoaType::new));

    public ItemStack egg() {
        return new ItemStack(this.eggItem);
    }
}
