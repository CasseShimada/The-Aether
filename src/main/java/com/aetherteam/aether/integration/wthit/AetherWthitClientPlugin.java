package com.aetherteam.aether.integration.wthit;

import com.aetherteam.aether.block.dungeon.ChestMimicBlock;
import com.aetherteam.aether.block.dungeon.DoorwayBlock;
import com.aetherteam.aether.block.dungeon.TrappedBlock;
import com.aetherteam.aether.block.dungeon.TreasureDoorwayBlock;
import com.aetherteam.aether.blockentity.ChestMimicBlockEntity;
import com.aetherteam.aether.integration.viewer.AetherViewerBlockPolicy;
import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IClientRegistrar;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.IWailaClientPlugin;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

/** Client-only WTHIT facade and tooltip adapter. */
public final class AetherWthitClientPlugin implements IWailaClientPlugin {
    private static final IBlockComponentProvider OVERRIDE_PROVIDER = new OverrideProvider();
    private static final IBlockComponentProvider MIMIC_TOOLTIP_PROVIDER = new MimicTooltipProvider();

    @Override
    public void register(IClientRegistrar registrar) {
        registrar.override(OVERRIDE_PROVIDER, TrappedBlock.class);
        registrar.override(OVERRIDE_PROVIDER, DoorwayBlock.class);
        registrar.override(OVERRIDE_PROVIDER, TreasureDoorwayBlock.class);
        registrar.override(OVERRIDE_PROVIDER, ChestMimicBlock.class);
        registrar.body(MIMIC_TOOLTIP_PROVIDER, ChestMimicBlockEntity.class);
    }

    private static boolean isPrivileged(Player player) {
        return player.isCreative() || player.isSpectator();
    }

    private static final class OverrideProvider implements IBlockComponentProvider {
        @Nullable
        @Override
        public BlockState getOverride(IBlockAccessor accessor, IPluginConfig config) {
            BlockState target = accessor.getBlockState();
            BlockState display = AetherViewerBlockPolicy.displayState(target, isPrivileged(accessor.getPlayer()));
            return display == target ? null : display;
        }
    }

    private static final class MimicTooltipProvider implements IBlockComponentProvider {
        @Override
        public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
            if (accessor.getData().raw().getBooleanOr(AetherWthitCommonPlugin.MIMIC_MASKED_KEY, false)) {
                tooltip.addLine(Component.translatable("tooltip.aether.viewer.inventory_not_generated")
                        .withStyle(ChatFormatting.GRAY));
            }
        }
    }
}
