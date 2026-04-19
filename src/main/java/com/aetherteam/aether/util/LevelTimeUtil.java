package com.aetherteam.aether.util;

import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.data.resources.registries.AetherDimensions;
import com.aetherteam.aether.mixin.mixins.common.accessor.ServerLevelAccessor;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.clock.WorldClock;

public final class LevelTimeUtil {
    private LevelTimeUtil() {
    }

    public static long getTime(Level level) {
        if (!usesDefaultClock(level)) {
            return level.getLevelData().getGameTime();
        }
        Holder<WorldClock> clock = getDefaultClock(level);
        if (clock != null) {
            return level.getDefaultClockTime();
        }
        return level.getLevelData().getGameTime();
    }

    public static void setTime(ServerLevel level, long time) {
        if (!usesDefaultClock(level)) {
            ((ServerLevelAccessor) level).aether$getServerLevelData().setGameTime(time);
            return;
        }
        Holder<WorldClock> clock = getDefaultClock(level);
        if (clock != null) {
            level.getServer().clockManager().setTotalTicks(clock, time);
            return;
        }
        ((ServerLevelAccessor) level).aether$getServerLevelData().setGameTime(time);
    }

    private static boolean usesDefaultClock(Level level) {
        if (!level.dimension().equals(AetherDimensions.AETHER_LEVEL)) {
            return true;
        }
        if (!level.hasAttached(AetherDataAttachments.AETHER_TIME)) {
            return true;
        }
        return level.getAttachedOrCreate(AetherDataAttachments.AETHER_TIME).isTimeSynced();
    }

    private static Holder<WorldClock> getDefaultClock(Level level) {
        return level.dimensionType().defaultClock().orElse(null);
    }
}
