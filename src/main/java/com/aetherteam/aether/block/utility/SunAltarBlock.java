package com.aetherteam.aether.block.utility;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.AetherConfig;
import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.attachment.AetherTimeAttachment;
import com.aetherteam.aether.block.AetherBlocks;
import com.aetherteam.aether.blockentity.SunAltarBlockEntity;
import com.aetherteam.aether.command.SunAltarWhitelist;
import com.aetherteam.aether.data.resources.registries.AetherDimensions;
import com.aetherteam.aether.network.AetherPacketSender;
import com.aetherteam.aether.network.packet.clientbound.OpenSunAltarPacket;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.server.players.NameAndId;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class SunAltarBlock extends BaseEntityBlock {

    public static final MapCodec<SunAltarBlock> CODEC = simpleCodec(SunAltarBlock::new);

    public SunAltarBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SunAltarBlockEntity(pos, state);
    }

    /**
     * Controls when the Sun Altar can be used and interacted with.<br><br>
     *
     * @param state  The {@link BlockState} of the block.
     * @param level  The {@link Level} the block is in.
     * @param pos    The {@link BlockPos} of the block.
     * @param player The {@link Player} interacting with the block.
     * @param hit    The {@link BlockHitResult} of the interaction.
     * @return The {@link InteractionResult} of the interaction.
     */
    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide()) {
            if (!canPlayerUse(player)) { // Prevents non-operator or non-whitelisted players from using the Sun Altar on servers
                com.aetherteam.aether.util.MessageUtil.sendPlayerMessage(player, Component.translatable(Aether.MODID + ".sun_altar.no_permission"), true); // Player doesn't have permission to use the Sun Altar.
            } else {
                if (canControlDimension(level)) {
                    if (canSetTime(level)) {
                        this.openScreen(level, pos, player, AetherTimeAttachment.getTicksPerDay());
                    } else {
                        com.aetherteam.aether.util.MessageUtil.sendPlayerMessage(player, Component.translatable(Aether.MODID + ".sun_altar.in_control"), true); // Sun Spirit is still in control of the realm.
                    }
                } else {
                    com.aetherteam.aether.util.MessageUtil.sendPlayerMessage(player, Component.translatable(Aether.MODID + ".sun_altar.no_power"), true); // Sun Altar has no power in the dimension.
                }
            }
        }
        return InteractionResult.SUCCESS;
    }

    public static boolean canPlayerUse(Player player) {
        boolean isOperator = player.permissions().hasPermission(Permissions.COMMANDS_OWNER);
        return !AetherConfig.SERVER.sun_altar_whitelist.get()
                || isOperator
                || SunAltarWhitelist.INSTANCE.isWhiteListed(new NameAndId(player.getGameProfile()));
    }

    public static boolean canControlDimension(Level level) {
        boolean defaultCheck = AetherConfig.SERVER.sun_altar_dimensions.get().contains(level.dimension().identifier().toString());
        if (AetherConfig.SERVER.sync_aether_time.get()) {
            return level.dimension() == Level.OVERWORLD || level.dimension() == AetherDimensions.AETHER_LEVEL || defaultCheck;
        } else {
            return defaultCheck;
        }
    }

    public static boolean canSetTime(Level level) {
        return !level.hasAttached(AetherDataAttachments.AETHER_TIME)
                || !level.getAttachedOrCreate(AetherDataAttachments.AETHER_TIME).isEternalDay();
    }

    public static boolean isLoadedSunAltar(Level level, BlockPos pos) {
        return level.hasChunkAt(pos) && level.getBlockState(pos).is(AetherBlocks.SUN_ALTAR);
    }

    protected void openScreen(Level level, BlockPos pos, Player player, int timeScale) {
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof SunAltarBlockEntity sunAltar) {
                AetherPacketSender.sendToPlayer(serverPlayer, new OpenSunAltarPacket(sunAltar.getName(), timeScale, pos));
            }
        }
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
}
