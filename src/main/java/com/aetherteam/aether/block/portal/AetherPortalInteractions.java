package com.aetherteam.aether.block.portal;

import com.aetherteam.aether.AetherConfig;
import com.aetherteam.aether.AetherTags;
import com.aetherteam.aether.network.AetherPacketSender;
import com.aetherteam.aether.network.packet.clientbound.PortalInteractPacket;
import com.aetherteam.aether.world.LevelUtil;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

import javax.annotation.Nullable;
import java.util.Optional;

public final class AetherPortalInteractions {
    private AetherPortalInteractions() {
    }

    public static boolean createPortal(Player player, Level level, BlockPos pos, @Nullable Direction direction, ItemStack stack, InteractionHand hand) {
        if (level.isClientSide() || shouldDeferToImmersivePortals() || direction == null || !canCreatePortal(level, stack)) {
            return false;
        }

        Optional<AetherPortalShape> optional = AetherPortalShape.findEmptyAetherPortalShape(level, pos.relative(direction), Direction.Axis.X);
        if (optional.isEmpty()) {
            return false;
        }

        AetherPacketSender.sendToTrackingAndSelf(player, new PortalInteractPacket(player.getId(), hand == InteractionHand.MAIN_HAND));
        optional.get().createPortalBlocks();
        consumePortalActivationItem(player, stack, hand);
        return true;
    }

    public static boolean detectWaterInFrame(LevelAccessor levelAccessor, BlockPos pos, BlockState blockState, FluidState fluidState) {
        if (!(levelAccessor instanceof Level level)) {
            return false;
        }
        if (level.isClientSide() || shouldDeferToImmersivePortals()) {
            return false;
        }
        if (!fluidState.is(Fluids.WATER) || fluidState.createLegacyBlock().getBlock() != blockState.getBlock()) {
            return false;
        }
        if (!isPortalDimension(level) || AetherConfig.SERVER.disable_aether_portal.get()) {
            return false;
        }

        Optional<AetherPortalShape> optional = AetherPortalShape.findEmptyAetherPortalShape(level, pos, Direction.Axis.X);
        if (optional.isEmpty()) {
            return false;
        }

        optional.get().createPortalBlocks();
        return true;
    }

    private static boolean shouldDeferToImmersivePortals() {
        return FabricLoader.getInstance().isModLoaded("immersive_portals_core")
                && AetherConfig.COMMON.enable_immersive_portals_compatibility.get();
    }

    private static boolean canCreatePortal(Level level, ItemStack stack) {
        return stack.is(AetherTags.Items.AETHER_PORTAL_ACTIVATION_ITEMS) && isPortalDimension(level);
    }

    private static boolean isPortalDimension(Level level) {
        return level.dimension() == LevelUtil.returnDimension() || level.dimension() == LevelUtil.destinationDimension();
    }

    private static void consumePortalActivationItem(Player player, ItemStack stack, InteractionHand hand) {
        if (player.isCreative()) {
            return;
        }

        ItemStack craftingRemainder = stack.getItem().getCraftingRemainder().create();
        if (stack.getCount() > 1) {
            stack.shrink(1);
            if (!craftingRemainder.isEmpty()) {
                player.addItem(craftingRemainder);
            }
            return;
        }
        if (stack.isDamageableItem()) {
            stack.hurtAndBreak(1, player, hand);
            return;
        }

        player.setItemInHand(hand, craftingRemainder.isEmpty() ? ItemStack.EMPTY : craftingRemainder);
    }
}
