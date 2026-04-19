package com.aetherteam.aether.world;

import com.aetherteam.aether.attachment.AetherDataAttachments;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.DerivedLevelData;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.level.storage.WorldData;

/**
 * A wrapper for ServerLevelData. This is used to detach the day cycle from the Overworld and to allow the weather to be
 * set from the Aether. It gets applied to any dimension where the effects are equal to the Aether's dimension type ID.
 * A gamerule wrapper is used to prevent the overworld's weather cycle from being affected by the Aether.
 */
public class AetherLevelData extends DerivedLevelData {
    private final ServerLevel level;
    private final ServerLevelData wrapped;

    private long dayTime;

    public AetherLevelData(ServerLevel level, WorldData worldData, ServerLevelData overworldData, long dayTime) {
        super(worldData, overworldData);
        this.level = level;
        this.wrapped = overworldData;
        this.dayTime = dayTime;
    }

    /**
     * @return The overworld time in ticks.
     */
    public long getOverworldDayTime() {
        return this.wrapped.getGameTime();
    }

    /**
     * @return The world time in ticks.
     */
    public long getDayTime() {
        return this.getGameTime();
    }

    @Override
    public long getGameTime() {
        return this.level.getAttachedOrCreate(AetherDataAttachments.AETHER_TIME).isTimeSynced() ? this.wrapped.getGameTime() : this.dayTime;
    }

    /**
     * Sets the world time.
     *
     * @param time The {@link Integer} for the time in ticks.
     */
    public void setDayTime(long time) {
        this.setGameTime(time);
    }

    @Override
    public void setGameTime(long time) {
        if (this.level.getAttachedOrCreate(AetherDataAttachments.AETHER_TIME).isTimeSynced()) {
            this.wrapped.setGameTime(time);
        }
        this.dayTime = time;
    }
}
