package com.aetherteam.aether.event;

import com.aetherteam.aether.event.hooks.RecipeHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public class AetherEventDispatch {
    /**
     * @see FreezeEvent.FreezeFromBlock
     */
    public static FreezeEvent.FreezeFromBlock onBlockFreezeFluid(LevelAccessor level, BlockPos pos, BlockPos origin, BlockState fluidState, BlockState blockState, BlockState sourceBlock) {
        FreezeEvent.FreezeFromBlock event = new FreezeEvent.FreezeFromBlock(level, pos, origin, fluidState, blockState, sourceBlock);
        if (RecipeHooks.preventBlockFreezing(level, origin, pos)) {
            event.setCanceled(true);
        }
        return event;
    }

    /**
     * @see FreezeEvent.FreezeFromItem
     */
    public static FreezeEvent.FreezeFromItem onItemFreezeFluid(LevelAccessor level, BlockPos pos, BlockState fluidState, BlockState blockState, ItemStack sourceItem) {
        return new FreezeEvent.FreezeFromItem(level, pos, fluidState, blockState, sourceItem);
    }

}
