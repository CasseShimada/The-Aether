package com.aetherteam.aether.util;

import com.aetherteam.aether.mixin.mixins.common.accessor.ServerLevelAccessor;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

public final class LevelTimeUtil {
    private LevelTimeUtil() {
    }

    public static long getTime(Level level) {
        return level.getLevelData().getGameTime();
    }

    public static void setTime(ServerLevel level, long time) {
        ((ServerLevelAccessor) level).aether$getServerLevelData().setGameTime(time);
    }

    public static void setTime(ClientLevel level, long time) {
        level.getLevelData().setGameTime(time);
    }
}
