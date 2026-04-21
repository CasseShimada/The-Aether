package com.aetherteam.aether.event.hooks;

import com.aetherteam.aether.AetherConfig;
import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.attachment.AetherTimeAttachment;
import com.aetherteam.aether.data.resources.registries.AetherDimensions;
import com.aetherteam.aether.mixin.mixins.common.accessor.ServerLevelAccessor;
import com.aetherteam.aether.util.LevelTimeUtil;
import com.aetherteam.aether.world.AetherLevelData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.gamerules.GameRules;

import javax.annotation.Nullable;

final class DimensionTimeHooks {
    private DimensionTimeHooks() {
    }

    static void tickTime(Level level) {
        if (level.dimension().equals(AetherDimensions.AETHER_LEVEL) && level instanceof ServerLevel serverLevel) {
            ServerLevelAccessor serverLevelAccessor = (ServerLevelAccessor) serverLevel;
            com.aetherteam.aether.mixin.mixins.common.accessor.LevelAccessor levelAccessor = (com.aetherteam.aether.mixin.mixins.common.accessor.LevelAccessor) level;
            long gameTime = levelAccessor.aether$getLevelData().getGameTime() + 1L;
            serverLevelAccessor.aether$getServerLevelData().setGameTime(gameTime);
            if (serverLevel.getGameRules().get(GameRules.ADVANCE_TIME)) {
                LevelTimeUtil.setTime(serverLevel, serverLevel.getAttachedOrCreate(AetherDataAttachments.AETHER_TIME).tickTime(level));
            }

            EntityHooks.tickAetherSkySpawns(serverLevel);
        }
    }

    static void checkEternalDayConfig(Level level) {
        if (!level.isClientSide() && level.hasAttached(AetherDataAttachments.AETHER_TIME)) {
            var aetherTime = level.getAttachedOrCreate(AetherDataAttachments.AETHER_TIME);
            boolean eternalDay = aetherTime.isEternalDay();
            if (AetherConfig.SERVER.disable_eternal_day.get() && eternalDay) {
                aetherTime.setEternalDay(false);
                aetherTime.updateEternalDay(level);
            }
        }
    }

    static void initializeLevelData(LevelAccessor level) {
        if (level instanceof ServerLevel serverLevel && serverLevel.dimension().equals(AetherDimensions.AETHER_LEVEL)) {
            AetherLevelData levelData = new AetherLevelData(serverLevel, serverLevel.getServer().getWorldData(), serverLevel.getServer().getWorldData().overworldData(), serverLevel.getAttachedOrCreate(AetherDataAttachments.AETHER_TIME).getDayTime());
            ServerLevelAccessor serverLevelAccessor = (ServerLevelAccessor) serverLevel;
            com.aetherteam.aether.mixin.mixins.common.accessor.LevelAccessor levelAccessor = (com.aetherteam.aether.mixin.mixins.common.accessor.LevelAccessor) level;
            serverLevelAccessor.aether$setServerLevelData(levelData);
            levelAccessor.aether$setLevelData(levelData);
        }
    }

    @Nullable
    static Long finishSleep(LevelAccessor level, long newTime) {
        if (level instanceof ServerLevel serverLevel && serverLevel.dimension().equals(AetherDimensions.AETHER_LEVEL)) {
            serverLevel.getWeatherData().setRainTime(0);
            serverLevel.getWeatherData().setRaining(false);
            serverLevel.getWeatherData().setThunderTime(0);
            serverLevel.getWeatherData().setThundering(false);

            long time = newTime + (24000L * AetherTimeAttachment.getTicksPerDayMultiplier());
            return time - time % (long) AetherTimeAttachment.getTicksPerDay();
        }
        return null;
    }

    static boolean isEternalDay(Player player) {
        return player.level().dimension().equals(AetherDimensions.AETHER_LEVEL)
                && player.level().getAttachedOrCreate(AetherDataAttachments.AETHER_TIME).isEternalDay();
    }
}
