package com.aetherteam.aether.recipe.blockstate;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public final class BlockStateIngredient implements Predicate<BlockState> {
    private static final Codec<TagKey<Block>> TAG_CODEC = Codec.STRING.comapFlatMap(BlockStateIngredient::decodeTag, BlockStateIngredient::encodeTag);
    private static final Codec<Either<BlockPropertyPair, TagKey<Block>>> ENTRY_CODEC = Codec.either(BlockPropertyPair.CODEC, TAG_CODEC);
    public static final Codec<BlockStateIngredient> CODEC = Codec.either(ENTRY_CODEC, ENTRY_CODEC.listOf()).xmap(
            either -> either.map(
                    entry -> new BlockStateIngredient(List.of(entry)),
                    BlockStateIngredient::new
            ),
            ingredient -> ingredient.entries.size() == 1 ? Either.left(ingredient.entries.get(0)) : Either.right(ingredient.entries)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, BlockStateIngredient> CONTENTS_STREAM_CODEC = StreamCodec.of(BlockStateIngredient::toNetwork, BlockStateIngredient::fromNetwork);
    public static final BlockStateIngredient EMPTY = new BlockStateIngredient(List.of());

    private final List<Either<BlockPropertyPair, TagKey<Block>>> entries;

    private BlockStateIngredient(List<Either<BlockPropertyPair, TagKey<Block>>> entries) {
        this.entries = List.copyOf(entries);
    }

    @Override
    public boolean test(BlockState state) {
        for (Either<BlockPropertyPair, TagKey<Block>> entry : this.entries) {
            if (entry.left().isPresent() && entry.left().get().matches(state)) {
                return true;
            }
            if (entry.right().isPresent() && state.is(entry.right().get())) {
                return true;
            }
        }
        return false;
    }

    public BlockPropertyPair[] getPairs() {
        List<BlockPropertyPair> resolvedPairs = new ArrayList<>();
        for (Either<BlockPropertyPair, TagKey<Block>> entry : this.entries) {
            entry.left().ifPresent(resolvedPairs::add);
            entry.right().ifPresent(tag -> BuiltInRegistries.BLOCK.getTagOrEmpty(tag).forEach(holder -> resolvedPairs.add(BlockPropertyPair.of(holder.value(), java.util.Optional.empty()))));
        }
        return resolvedPairs.toArray(BlockPropertyPair[]::new);
    }

    public boolean isEmpty() {
        return this.entries.isEmpty();
    }

    private static BlockStateIngredient fromNetwork(RegistryFriendlyByteBuf buffer) {
        int size = buffer.readVarInt();
        List<Either<BlockPropertyPair, TagKey<Block>>> entries = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            boolean isTag = buffer.readBoolean();
            if (isTag) {
                entries.add(Either.right(TagKey.create(Registries.BLOCK, buffer.readIdentifier())));
            } else {
                entries.add(Either.left(BlockStateRecipeUtil.readPair(buffer)));
            }
        }
        return new BlockStateIngredient(entries);
    }

    private static void toNetwork(RegistryFriendlyByteBuf buffer, BlockStateIngredient ingredient) {
        buffer.writeVarInt(ingredient.entries.size());
        for (Either<BlockPropertyPair, TagKey<Block>> entry : ingredient.entries) {
            if (entry.right().isPresent()) {
                buffer.writeBoolean(true);
                buffer.writeIdentifier(entry.right().get().location());
            } else {
                buffer.writeBoolean(false);
                BlockStateRecipeUtil.writePair(buffer, entry.left().orElseThrow());
            }
        }
    }

    private static DataResult<TagKey<Block>> decodeTag(String value) {
        if (!value.startsWith("#")) {
            return DataResult.error(() -> "Block tag entries must start with '#': " + value);
        }
        try {
            return DataResult.success(TagKey.create(Registries.BLOCK, Identifier.parse(value.substring(1))));
        } catch (IllegalArgumentException exception) {
            return DataResult.error(() -> "Invalid block tag identifier: " + value);
        }
    }

    private static String encodeTag(TagKey<Block> tag) {
        return "#" + tag.location();
    }
}
