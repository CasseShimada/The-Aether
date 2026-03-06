package com.aetherteam.nitrogen.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public record BlockPropertyPair(Block block, Optional<Reference2ObjectArrayMap<Property<?>, Comparable<?>>> properties) {
    private record RawPair(Block block, Optional<Map<String, String>> properties) {
    }

    private static final Codec<RawPair> RAW_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("block").forGetter(RawPair::block),
            Codec.unboundedMap(Codec.STRING, Codec.STRING).optionalFieldOf("properties").forGetter(RawPair::properties)
    ).apply(instance, RawPair::new));

    public static final Codec<BlockPropertyPair> CODEC = RAW_CODEC.flatXmap(BlockPropertyPair::decode, BlockPropertyPair::encode);

    public BlockPropertyPair {
        properties = properties.filter(map -> !map.isEmpty());
    }

    public static BlockPropertyPair of(Block block, Optional<Reference2ObjectArrayMap<Property<?>, Comparable<?>>> properties) {
        return new BlockPropertyPair(block, properties.map(Reference2ObjectArrayMap::new));
    }

    public boolean matches(BlockState state) {
        if (!state.is(this.block)) {
            return false;
        }
        if (this.properties.isEmpty()) {
            return true;
        }
        for (Map.Entry<Property<?>, Comparable<?>> entry : this.properties.get().entrySet()) {
            if (!state.hasProperty(entry.getKey()) || !entry.getValue().equals(getValueUnchecked(state, entry.getKey()))) {
                return false;
            }
        }
        return true;
    }

    private static DataResult<BlockPropertyPair> decode(RawPair rawPair) {
        Optional<Reference2ObjectArrayMap<Property<?>, Comparable<?>>> optionalProperties = Optional.empty();
        if (rawPair.properties().isPresent()) {
            Reference2ObjectArrayMap<Property<?>, Comparable<?>> parsedProperties = new Reference2ObjectArrayMap<>();
            for (Map.Entry<String, String> entry : rawPair.properties().get().entrySet()) {
                Property<?> property = rawPair.block().getStateDefinition().getProperty(entry.getKey());
                if (property == null) {
                    return DataResult.error(() -> "Unknown property '" + entry.getKey() + "' for block '" + BuiltInRegistries.BLOCK.getKey(rawPair.block()) + "'");
                }
                Optional<?> parsedValue = property.getValue(entry.getValue());
                if (parsedValue.isEmpty()) {
                    return DataResult.error(() -> "Unknown value '" + entry.getValue() + "' for property '" + entry.getKey() + "'");
                }
                parsedProperties.put(property, (Comparable<?>) parsedValue.get());
            }
            if (!parsedProperties.isEmpty()) {
                optionalProperties = Optional.of(parsedProperties);
            }
        }
        return DataResult.success(BlockPropertyPair.of(rawPair.block(), optionalProperties));
    }

    private static DataResult<RawPair> encode(BlockPropertyPair pair) {
        Optional<Map<String, String>> encodedProperties = pair.properties.map(BlockPropertyPair::toRawProperties);
        return DataResult.success(new RawPair(pair.block, encodedProperties));
    }

    private static Map<String, String> toRawProperties(Reference2ObjectArrayMap<Property<?>, Comparable<?>> properties) {
        Map<String, String> map = new HashMap<>();
        for (Map.Entry<Property<?>, Comparable<?>> entry : properties.entrySet()) {
            map.put(entry.getKey().getName(), getNameUnchecked(entry.getKey(), entry.getValue()));
        }
        return map;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static Comparable<?> getValueUnchecked(BlockState state, Property<?> property) {
        return state.getValue((Property) property);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static String getNameUnchecked(Property<?> property, Comparable<?> value) {
        return ((Property) property).getName(value);
    }
}
