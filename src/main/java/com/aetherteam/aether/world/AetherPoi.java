package com.aetherteam.aether.world;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.block.AetherBlocks;
import com.aetherteam.aether.mixin.mixins.common.accessor.PoiTypesAccessor;
import com.google.common.collect.ImmutableSet;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import com.aetherteam.aether.registry.DeferredHolder;
import com.aetherteam.aether.registry.DeferredRegister;

import java.util.Set;

public class AetherPoi {
    public static final DeferredRegister<PoiType> POI = DeferredRegister.create(BuiltInRegistries.POINT_OF_INTEREST_TYPE, Aether.MODID);

    public static final DeferredHolder<PoiType, PoiType> AETHER_PORTAL = POI.register("aether_portal", () -> new PoiType(getBlockStates(AetherBlocks.AETHER_PORTAL.get()), 0, 1));

    public static void registerBlockStateMappings() {
        PoiTypesAccessor.aether$registerBlockStates(getPortalHolder(), getBlockStates(AetherBlocks.AETHER_PORTAL.get()));
    }

    public static void registerPortal(ServerLevel level, BlockPos pos) {
        level.getPoiManager().add(pos.immutable(), getPortalHolder());
    }

    private static Set<BlockState> getBlockStates(Block block) {
        return ImmutableSet.copyOf(block.getStateDefinition().getPossibleStates());
    }

    private static Holder<PoiType> getPortalHolder() {
        return BuiltInRegistries.POINT_OF_INTEREST_TYPE.wrapAsHolder(AETHER_PORTAL.get());
    }
}
