package com.aetherteam.aether.recipe;

import com.aetherteam.aether.recipe.recipes.ban.BlockBanRecipe;
import com.aetherteam.aether.recipe.recipes.block.PlacementConversionRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public final class PlacementRecipeRules {
    private PlacementRecipeRules() {
    }

    /**
     * Checks if a block is unable to exist in the Aether, and either removes it or replaces it with another block.
     *
     * @param levelAccessor The {@link LevelAccessor} the block is in.
     * @param pos           The {@link BlockPos} of the block.
     */
    public static void checkExistenceBanned(LevelAccessor levelAccessor, BlockPos pos) {
        if (levelAccessor instanceof Level level) {
            BlockState state = levelAccessor.getBlockState(pos);
            if (isBlockPlacementBanned(level, pos, state)) {
                level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                if (state.getBlock().asItem() != Items.AIR) {
                    Block.dropResources(state, level, pos);
                }
            } else {
                convertBlockPlacement(level, pos, state);
            }
        }
    }

    /**
     * Spawns particles from a ban or conversion recipe interaction.
     *
     * @param accessor The {@link LevelAccessor} that the interaction is in.
     * @param pos      The {@link BlockPos} the interaction is at.
     */
    public static void banOrConvert(LevelAccessor accessor, BlockPos pos) {
        if (accessor instanceof ServerLevel serverLevel) {
            double x = pos.getX() + 0.5;
            double y = pos.getY() + 1.0;
            double z = pos.getZ() + 0.5;
            for (int i = 0; i < 10; i++) {
                serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE, x, y, z, 1, 0.0, 0.0, 0.0, 0.0F);
            }
            serverLevel.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS, 1.0F, 1.0F);
        }
    }

    /**
     * Checks if a block placement is banned through the {@link AetherRecipeTypes#BLOCK_PLACEMENT_BAN} recipe type.
     *
     * @param level The {@link Level} that the placement is in.
     * @param pos   The {@link BlockPos} the placement is at.
     * @param state The placed {@link BlockState}.
     * @return Whether the placement is banned, as a {@link Boolean}.
     */
    private static boolean isBlockPlacementBanned(Level level, BlockPos pos, BlockState state) {
        if (!level.isClientSide() && level.recipeAccess() instanceof RecipeManager recipeManager) {
            for (RecipeHolder<?> recipe : recipeManager.getRecipes()) {
                if (recipe.value().getType() == AetherRecipeTypes.BLOCK_PLACEMENT_BAN && ((BlockBanRecipe) recipe.value()).banBlock(level, pos, state)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks if a placed block should be converted through the {@link AetherRecipeTypes#PLACEMENT_CONVERSION} recipe type.
     *
     * @param level The {@link Level} that the placement is in.
     * @param pos   The {@link BlockPos} the placement is at.
     * @param state The placed {@link BlockState}.
     */
    private static void convertBlockPlacement(Level level, BlockPos pos, BlockState state) {
        if (!level.isClientSide() && level.recipeAccess() instanceof RecipeManager recipeManager) {
            for (RecipeHolder<?> recipe : recipeManager.getRecipes()) {
                if (recipe.value().getType() == AetherRecipeTypes.PLACEMENT_CONVERSION && ((PlacementConversionRecipe) recipe.value()).convert(level, pos, state)) {
                    return;
                }
            }
        }
    }
}
