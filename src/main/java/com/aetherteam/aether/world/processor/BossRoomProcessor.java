package com.aetherteam.aether.world.processor;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;

/**
 * Placeholder processor for boss-room templates.
 * Entity-level processor hooks were removed upstream and are now handled elsewhere.
 */
public class BossRoomProcessor extends StructureProcessor {
    public static final BossRoomProcessor INSTANCE = new BossRoomProcessor();

    public static final MapCodec<BossRoomProcessor> CODEC = MapCodec.unit(BossRoomProcessor.INSTANCE);

    @Override
    protected StructureProcessorType<?> getType() {
        return AetherStructureProcessors.BOSS_ROOM.get();
    }
}
