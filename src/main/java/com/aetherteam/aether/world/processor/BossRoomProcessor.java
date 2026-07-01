package com.aetherteam.aether.world.processor;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;

/**
 * Placeholder processor for boss-room templates.
 * Entity-level processor hooks were removed upstream and are now handled elsewhere.
 */
public class BossRoomProcessor implements StructureProcessor {
    public static final BossRoomProcessor INSTANCE = new BossRoomProcessor();

    public static final MapCodec<BossRoomProcessor> CODEC = MapCodec.unit(BossRoomProcessor.INSTANCE);

    @Override
    public MapCodec<BossRoomProcessor> codec() {
        return AetherStructureProcessors.BOSS_ROOM;
    }
}
