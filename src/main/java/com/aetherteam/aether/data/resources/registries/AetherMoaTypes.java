package com.aetherteam.aether.data.resources.registries;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.api.registers.MoaType;
import com.aetherteam.aether.item.AetherItems;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Optional;

public class AetherMoaTypes {
    public static final ResourceKey<Registry<MoaType>> MOA_TYPE_REGISTRY_KEY = ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath(Aether.MODID, "moa_type"));

    public static final ResourceKey<MoaType> BLUE = createKey("blue");
    public static final ResourceKey<MoaType> WHITE = createKey("white");
    public static final ResourceKey<MoaType> BLACK = createKey("black");

    private static ResourceKey<MoaType> createKey(String name) {
        return ResourceKey.create(AetherMoaTypes.MOA_TYPE_REGISTRY_KEY, Identifier.fromNamespaceAndPath(Aether.MODID, name));
    }

    public static void bootstrap(BootstrapContext<MoaType> context) {
        context.register(BLUE, new MoaType(new ItemStack(AetherItems.BLUE_MOA_EGG.get()), 3, 0.155F, 100, Identifier.fromNamespaceAndPath(Aether.MODID, "textures/entity/mobs/moa/blue_moa.png"), Identifier.fromNamespaceAndPath(Aether.MODID, "textures/entity/mobs/moa/moa_saddle.png"), Optional.empty()));
        context.register(WHITE, new MoaType(new ItemStack(AetherItems.WHITE_MOA_EGG.get()), 4, 0.155F, 50, Identifier.fromNamespaceAndPath(Aether.MODID, "textures/entity/mobs/moa/white_moa.png"), Identifier.fromNamespaceAndPath(Aether.MODID, "textures/entity/mobs/moa/moa_saddle.png"), Optional.empty()));
        context.register(BLACK, new MoaType(new ItemStack(AetherItems.BLACK_MOA_EGG.get()), 8, 0.155F, 25, Identifier.fromNamespaceAndPath(Aether.MODID, "textures/entity/mobs/moa/black_moa.png"), Identifier.fromNamespaceAndPath(Aether.MODID, "textures/entity/mobs/moa/black_moa_saddle.png"), Optional.empty()));
    }

    @Nullable
    public static ResourceKey<MoaType> getResourceKey(RegistryAccess registryAccess, String location) {
        return getResourceKey(registryAccess, Identifier.parse(location));
    }

    @Nullable
    public static ResourceKey<MoaType> getResourceKey(RegistryAccess registryAccess, Identifier location) {
        MoaType moaType = getMoaType(registryAccess, location);
        if (moaType != null) {
            return registryAccess.lookupOrThrow(AetherMoaTypes.MOA_TYPE_REGISTRY_KEY).getResourceKey(moaType).orElse(null);
        } else {
            return null;
        }
    }

    @Nullable
    public static ResourceKey<MoaType> getResourceKey(RegistryAccess registryAccess, MoaType moaType) {
        return registryAccess.lookupOrThrow(AetherMoaTypes.MOA_TYPE_REGISTRY_KEY).getResourceKey(moaType).orElse(null);
    }

    @Nullable
    public static MoaType getMoaType(RegistryAccess registryAccess, String location) {
        return getMoaType(registryAccess, Identifier.parse(location));
    }

    @Nullable
    public static MoaType getMoaType(RegistryAccess registryAccess, Identifier location) {
        return registryAccess.lookupOrThrow(AetherMoaTypes.MOA_TYPE_REGISTRY_KEY).getValue(location);
    }

    /**
     * Gets a random {@link MoaType} with a weighted chance. This is used when spawning Moas in the world.<br>
     * A {@link WeightedList} is built with all the {@link MoaType}s and their spawn chance weights, and one is randomly picked out of the list.
     *
     * @param registryAccess The {@link RegistryAccess} to use.
     * @param random The {@link RandomSource} to use.
     * @return The {@link MoaType}.
     */
    public static MoaType getWeightedChance(RegistryAccess registryAccess, RandomSource random) {
        Registry<MoaType> moaTypeRegistry = registryAccess.lookupOrThrow(AetherMoaTypes.MOA_TYPE_REGISTRY_KEY);
        WeightedList.Builder<MoaType> weightedListBuilder = WeightedList.builder();
        moaTypeRegistry.stream().forEach((moaType) -> weightedListBuilder.add(moaType, moaType.spawnChance()));
        WeightedList<MoaType> weightedList = weightedListBuilder.build();
        Optional<MoaType> moaType = weightedList.getRandom(random);
        return moaType.orElse(moaTypeRegistry.getValueOrThrow(AetherMoaTypes.BLUE));
    }
}
