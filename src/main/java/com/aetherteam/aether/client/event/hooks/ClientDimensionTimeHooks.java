package com.aetherteam.aether.client.event.hooks;

import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.attachment.AetherTimeAttachment;
import com.aetherteam.aether.data.resources.registries.AetherDimensions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;

public final class ClientDimensionTimeHooks {
    private ClientDimensionTimeHooks() {
    }

    /**
     * Ticks time in clientside Aether levels.
     */
    public static void tickTime() {
        ClientLevel level = Minecraft.getInstance().level;
        if (level != null && !Minecraft.getInstance().isPaused() && level.dimension().equals(AetherDimensions.AETHER_LEVEL)) {
            AetherTimeAttachment data = level.getAttachedOrCreate(AetherDataAttachments.AETHER_TIME);
            if (!data.isTimeSynced()) {
                long dayTime = data.tickTime(level) - 1;
                level.getLevelData().setGameTime(dayTime);
            }
        }
    }
}
