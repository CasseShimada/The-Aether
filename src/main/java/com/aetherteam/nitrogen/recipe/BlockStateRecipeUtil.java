package com.aetherteam.nitrogen.recipe;

import com.aetherteam.aether.Aether;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.commands.CacheableFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;

public final class BlockStateRecipeUtil {
    public static final Codec<Either<ResourceKey<Biome>, TagKey<Biome>>> KEY_CODEC = Codec.STRING.comapFlatMap(BlockStateRecipeUtil::decodeBiomeKey, BlockStateRecipeUtil::encodeBiomeKey);
    public static final StreamCodec<RegistryFriendlyByteBuf, Either<ResourceKey<Biome>, TagKey<Biome>>> STREAM_CODEC = StreamCodec.of(BlockStateRecipeUtil::toNetwork, BlockStateRecipeUtil::fromNetwork);

    private BlockStateRecipeUtil() {
    }

    public static BlockPropertyPair readPair(RegistryFriendlyByteBuf buffer) {
        Identifier blockId = buffer.readIdentifier();
        Block block = BuiltInRegistries.BLOCK.getOptional(blockId).orElse(Blocks.AIR);
        Optional<it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap<Property<?>, Comparable<?>>> optionalProperties = Optional.empty();
        if (buffer.readBoolean()) {
            int size = buffer.readVarInt();
            it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap<Property<?>, Comparable<?>> properties = new it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap<>();
            for (int i = 0; i < size; i++) {
                String propertyName = buffer.readUtf();
                String propertyValue = buffer.readUtf();
                Property<?> property = block.getStateDefinition().getProperty(propertyName);
                if (property != null) {
                    Optional<?> parsedValue = property.getValue(propertyValue);
                    parsedValue.ifPresent(value -> properties.put(property, (Comparable<?>) value));
                }
            }
            if (!properties.isEmpty()) {
                optionalProperties = Optional.of(properties);
            }
        }
        return BlockPropertyPair.of(block, optionalProperties);
    }

    public static void writePair(RegistryFriendlyByteBuf buffer, BlockPropertyPair pair) {
        buffer.writeIdentifier(BuiltInRegistries.BLOCK.getKey(pair.block()));
        if (pair.properties().isPresent() && !pair.properties().get().isEmpty()) {
            buffer.writeBoolean(true);
            buffer.writeVarInt(pair.properties().get().size());
            for (Map.Entry<Property<?>, Comparable<?>> entry : pair.properties().get().entrySet()) {
                buffer.writeUtf(entry.getKey().getName());
                buffer.writeUtf(getNameUnchecked(entry.getKey(), entry.getValue()));
            }
        } else {
            buffer.writeBoolean(false);
        }
    }

    public static BlockState setHelper(Map.Entry<Property<?>, Comparable<?>> propertyEntry, BlockState state) {
        return setPropertyUnchecked(state, propertyEntry.getKey(), propertyEntry.getValue());
    }

    public static void executeFunction(LevelAccessor levelAccessor, BlockPos pos, Optional<CacheableFunction> function) {
        if (function.isEmpty() || !(levelAccessor instanceof net.minecraft.server.level.ServerLevel serverLevel)) {
            return;
        }
        if (serverLevel.getServer() == null) {
            return;
        }
        Object functionManager = serverLevel.getServer().getFunctions();
        Optional<?> resolvedFunction = resolveFunction(function.get(), functionManager);
        if (resolvedFunction.isEmpty()) {
            return;
        }
        Object source = serverLevel.getServer().createCommandSourceStack();
        invokeExecute(functionManager, resolvedFunction.get(), source, pos);
    }

    private static Either<ResourceKey<Biome>, TagKey<Biome>> fromNetwork(RegistryFriendlyByteBuf buffer) {
        boolean isTag = buffer.readBoolean();
        Identifier id = buffer.readIdentifier();
        return isTag ? Either.right(TagKey.create(Registries.BIOME, id)) : Either.left(ResourceKey.create(Registries.BIOME, id));
    }

    private static void toNetwork(RegistryFriendlyByteBuf buffer, Either<ResourceKey<Biome>, TagKey<Biome>> biomeKey) {
        if (biomeKey.right().isPresent()) {
            buffer.writeBoolean(true);
            buffer.writeIdentifier(biomeKey.right().get().location());
        } else {
            buffer.writeBoolean(false);
            buffer.writeIdentifier(biomeKey.left().orElseThrow().identifier());
        }
    }

    private static DataResult<Either<ResourceKey<Biome>, TagKey<Biome>>> decodeBiomeKey(String value) {
        boolean isTag = value.startsWith("#");
        String idString = isTag ? value.substring(1) : value;
        try {
            Identifier identifier = Identifier.parse(idString);
            if (isTag) {
                return DataResult.success(Either.right(TagKey.create(Registries.BIOME, identifier)));
            }
            return DataResult.success(Either.left(ResourceKey.create(Registries.BIOME, identifier)));
        } catch (IllegalArgumentException exception) {
            return DataResult.error(() -> "Invalid biome key identifier: " + value);
        }
    }

    private static String encodeBiomeKey(Either<ResourceKey<Biome>, TagKey<Biome>> biomeKey) {
        if (biomeKey.right().isPresent()) {
            return "#" + biomeKey.right().get().location();
        }
        return biomeKey.left().orElseThrow().identifier().toString();
    }

    private static Optional<?> resolveFunction(CacheableFunction cacheableFunction, Object functionManager) {
        try {
            for (Method method : cacheableFunction.getClass().getMethods()) {
                if ("get".equals(method.getName()) && method.getParameterCount() == 1 && method.getParameterTypes()[0].isAssignableFrom(functionManager.getClass())) {
                    Object result = method.invoke(cacheableFunction, functionManager);
                    if (result instanceof Optional<?> optional) {
                        return optional;
                    }
                }
            }
        } catch (ReflectiveOperationException exception) {
            Aether.LOGGER.warn("Failed to resolve cacheable function for block state recipe", exception);
        }
        return Optional.empty();
    }

    private static void invokeExecute(Object functionManager, Object commandFunction, Object source, BlockPos pos) {
        try {
            for (Method method : functionManager.getClass().getMethods()) {
                if ("execute".equals(method.getName()) && method.getParameterCount() == 2 && method.getParameterTypes()[0].isInstance(commandFunction) && method.getParameterTypes()[1].isInstance(source)) {
                    method.invoke(functionManager, commandFunction, source);
                    return;
                }
            }
            Aether.LOGGER.warn("Could not invoke server function at {} because no compatible execute method was found", pos);
        } catch (ReflectiveOperationException exception) {
            Aether.LOGGER.warn("Failed to execute server function at {}", pos, exception);
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static String getNameUnchecked(Property<?> property, Comparable<?> value) {
        return ((Property) property).getName(value);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static BlockState setPropertyUnchecked(BlockState state, Property<?> property, Comparable<?> value) {
        Property typedProperty = property;
        if (!state.hasProperty(typedProperty)) {
            return state;
        }
        try {
            return setTypedValue(state, typedProperty, value);
        } catch (IllegalArgumentException ignored) {
            return state;
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <T extends Comparable<T>> BlockState setTypedValue(BlockState state, Property<T> property, Comparable<?> value) {
        return state.setValue(property, (T) value);
    }
}
