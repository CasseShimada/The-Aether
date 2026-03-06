package com.aetherteam.aether.item.components;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

public record DungeonKind(Identifier id) {
    public static final Codec<DungeonKind> CODEC = Identifier.CODEC.xmap(DungeonKind::new, DungeonKind::id);

    public static final StreamCodec<RegistryFriendlyByteBuf, DungeonKind> STREAM_CODEC = StreamCodec.composite(
        Identifier.STREAM_CODEC,
        DungeonKind::id,
        DungeonKind::new);
}
