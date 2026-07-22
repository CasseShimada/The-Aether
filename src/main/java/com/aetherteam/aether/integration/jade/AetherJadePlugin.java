package com.aetherteam.aether.integration.jade;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.blockentity.ChestMimicBlockEntity;
import com.aetherteam.aether.integration.viewer.AetherViewerBlockPolicy;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;
import snownee.jade.api.Accessor;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;
import snownee.jade.api.config.IPluginConfig;

@WailaPlugin
public final class AetherJadePlugin implements IWailaPlugin {
    private static final IBlockComponentProvider MIMIC_TOOLTIP_PROVIDER = new MimicTooltipProvider();

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        // The facade changes the accessor's block to a vanilla chest, so register this lightweight
        // provider for Block and identify the retained authoritative block entity instead.
        registration.registerBlockComponent(MIMIC_TOOLTIP_PROVIDER, Block.class);
        registration.addRayTraceCallback((hitResult, accessor, originalAccessor) ->
                this.registerAetherOverrides(registration, hitResult, accessor, originalAccessor));
    }

    @Nullable
    private Accessor<?> registerAetherOverrides(
            IWailaClientRegistration registration,
            HitResult hitResult,
            @Nullable Accessor<?> accessor,
            @Nullable Accessor<?> originalAccessor
    ) {
        if (accessor instanceof BlockAccessor target) {
            Player player = accessor.getPlayer();
            boolean privileged = isPrivileged(player);
            BlockState targetState = target.getBlockState();
            BlockState displayState = AetherViewerBlockPolicy.displayState(targetState, privileged);
            if (displayState == targetState) {
                return accessor;
            }

            if (AetherViewerBlockPolicy.masksChestMimic(targetState, privileged)) {
                // Never pass the mimic's synchronized inventory payload to Jade's universal storage provider.
                return registration.blockAccessor().from(target)
                        .serverData(new CompoundTag()).blockState(displayState).build();
            }
            return registration.blockAccessor().from(target).blockState(displayState).build();
        }
        return accessor;
    }

    private static boolean isPrivileged(Player player) {
        return player.isCreative() || player.isSpectator();
    }

    private static final class MimicTooltipProvider implements IBlockComponentProvider {
        private static final Identifier UID = Identifier.fromNamespaceAndPath(Aether.MODID, "mimic_masked");

        @Override
        public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
            if (accessor.getBlockEntity() instanceof ChestMimicBlockEntity && !isPrivileged(accessor.getPlayer())) {
                tooltip.add(Component.translatable("tooltip.aether.viewer.inventory_not_generated")
                        .withStyle(ChatFormatting.GRAY));
            }
        }

        @Override
        public Identifier getUid() {
            return UID;
        }

        @Override
        public boolean isRequired() {
            return true;
        }
    }
}
