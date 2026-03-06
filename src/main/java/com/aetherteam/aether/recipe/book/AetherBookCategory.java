package com.aetherteam.aether.recipe.book;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

import java.util.function.IntFunction;

public enum AetherBookCategory implements StringRepresentable {
    ENCHANTING_FOOD(0, "enchanting_food"),
    ENCHANTING_BLOCKS(1, "enchanting_blocks"),
    ENCHANTING_MISC(2, "enchanting_misc"),
    ENCHANTING_REPAIR(3, "enchanting_repair"),
    FREEZABLE_BLOCKS(4, "freezable_blocks"),
    FREEZABLE_MISC(5, "freezable_misc"),
    UNKNOWN(6, "unknown");

    private static final IntFunction<AetherBookCategory> BY_ID = ByIdMap.continuous((category) -> category.id, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
    public static final StringRepresentable.EnumCodec<AetherBookCategory> CODEC = StringRepresentable.fromEnum(AetherBookCategory::values);
    public static final StreamCodec<ByteBuf, AetherBookCategory> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, (category) -> category.id);
    private final int id;
    private final String name;

    AetherBookCategory(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getSerializedName() {
        return this.name;
    }
}
