package com.aetherteam.aether.network.packet.serverbound;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.attachment.AetherTimeAttachment;
import com.aetherteam.aether.block.utility.SunAltarBlock;
import com.aetherteam.aether.util.LevelTimeUtil;
import com.aetherteam.aether.world.AetherTimeController;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

/**
 * Updates the time on the server, then updates that time for all players in the Aether.
 */
public record SunAltarUpdatePacket(long dayTime, int timeScale, BlockPos altarPos) implements CustomPacketPayload {
    public static final Type<SunAltarUpdatePacket> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Aether.MODID, "update_sun_altar"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SunAltarUpdatePacket> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_LONG,
        SunAltarUpdatePacket::dayTime,
        ByteBufCodecs.INT,
        SunAltarUpdatePacket::timeScale,
        BlockPos.STREAM_CODEC,
        SunAltarUpdatePacket::altarPos,
        SunAltarUpdatePacket::new);

    @Override
    public Type<SunAltarUpdatePacket> type() {
        return TYPE;
    }

    public static void execute(SunAltarUpdatePacket payload, ServerPlayer player) {
        if (!(player.level() instanceof ServerLevel level)) {
            return;
        }
        int expectedTimeScale = AetherTimeAttachment.getTicksPerDay();
        if (!isValidTimeSelection(payload.dayTime(), payload.timeScale(), expectedTimeScale)
                || !SunAltarBlock.canPlayerUse(player)
                || !SunAltarBlock.canControlDimension(level)
                || !SunAltarBlock.canSetTime(level)
                || !SunAltarBlock.isLoadedSunAltar(level, payload.altarPos())
                || player.distanceToSqr(payload.altarPos().getX() + 0.5, payload.altarPos().getY() + 0.5, payload.altarPos().getZ() + 0.5) > 64.0) {
            return;
        }

        // Preserve the current day and replace only the within-day time selected by the slider.
        long dayBase = LevelTimeUtil.getTime(level) / expectedTimeScale;
        long updatedDayTime = (dayBase * expectedTimeScale) + payload.dayTime();
        AetherTimeController.setTime(level, updatedDayTime);
        level.getServer().getPlayerList().broadcastAll(level.clockManager().createFullSyncPacket(), level.dimension());
    }

    static boolean isValidTimeSelection(long dayTime, int timeScale, int expectedTimeScale) {
        return expectedTimeScale > 0
                && timeScale == expectedTimeScale
                && dayTime >= 0
                && dayTime <= expectedTimeScale;
    }
}
