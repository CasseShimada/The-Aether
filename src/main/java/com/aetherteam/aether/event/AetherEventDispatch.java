package com.aetherteam.aether.event;

import com.aetherteam.aether.event.hooks.RecipeHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class AetherEventDispatch {
    /**
     * @see PlacementConvertEvent
     */
    public static PlacementConvertEvent onPlacementConvert(LevelAccessor level, BlockPos pos, BlockState oldState, BlockState newState) {
        PlacementConvertEvent event = new PlacementConvertEvent(level, pos, oldState, newState);
        if (!event.isCanceled()) {
            RecipeHooks.banOrConvert(level, pos);
        }
        return event;
    }

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

    /**
     * @see ItemUseConvertEvent
     */
    public static ItemUseConvertEvent onItemUseConvert(@Nullable Player player, LevelAccessor level, BlockPos pos, @Nullable ItemStack stack, BlockState oldState, BlockState newState, RecipeType<?> recipeType) {
        return new ItemUseConvertEvent(player, level, pos, stack, oldState, newState, recipeType);
    }

}
