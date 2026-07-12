package com.aetherteam.aether.recipe;

import com.aetherteam.aether.AetherConfig;
import com.aetherteam.aether.AetherTags;
import com.aetherteam.aether.block.AetherBlocks;
import com.aetherteam.aether.recipe.recipes.ban.ItemBanRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.phys.Vec3;

public final class InteractionRecipeRules {
    private InteractionRecipeRules() {
    }

    public static boolean isBlockedInteraction(Player player, Level level, InteractionHand hand, BlockPos blockPos, Direction direction) {
        ItemStack inHand = player.getItemInHand(hand);
        ItemStack interactionStack = getInteractionStack(player, hand, inHand);
        return checkInteractionBanned(
                player,
                level,
                blockPos,
                direction,
                interactionStack,
                level.getBlockState(blockPos),
                !inHand.isEmpty()
        );
    }

    /**
     * Checks if an interaction in the Aether is banned. This is used both for item interaction recipes and interacting with beds in the Aether.
     *
     * @param player         The {@link Player} performing the interaction.
     * @param level          The {@link Level} that the interaction is in.
     * @param pos            The {@link BlockPos} the interaction is at.
     * @param face           The {@link Direction} of the block face that is interacted with.
     * @param stack          The {@link ItemStack} used for interaction.
     * @param state          The {@link BlockState} being interacted with.
     * @param spawnParticles A {@link Boolean} for whether to spawn particles from the interaction's failure.
     * @return Whether an interaction is banned, as a {@link Boolean}.
     */
    public static boolean checkInteractionBanned(Player player, Level level, BlockPos pos, Direction face, ItemStack stack, BlockState state, boolean spawnParticles) {
        if (isItemPlacementBanned(level, pos, face, stack, spawnParticles)) {
            com.aetherteam.aether.util.MessageUtil.sendPlayerMessage(player, Component.translatable("aether.banned_item", stack.getItem().getName(stack)), true);
            return true;
        }
        if (level.getBiome(pos).is(AetherTags.Biomes.ULTRACOLD) && AetherConfig.SERVER.enable_bed_explosions.get()) {
            if (state.is(BlockTags.BEDS) && state.getBlock() != AetherBlocks.SKYROOT_BED) {
                if (!level.isClientSide()) {
                    if (state.getValue(BedBlock.PART) != BedPart.HEAD) {
                        pos = pos.relative(state.getValue(BedBlock.FACING));
                        state = level.getBlockState(pos);
                    }
                    BlockPos blockpos = pos.relative(state.getValue(BedBlock.FACING).getOpposite());
                    if (level.getBlockState(blockpos).is(BlockTags.BEDS) && level.getBlockState(blockpos).getBlock() != AetherBlocks.SKYROOT_BED) {
                        level.removeBlock(blockpos, false);
                    }
                    Vec3 vec3 = Vec3.atCenterOf(pos);
                    level.explode(null, level.damageSources().badRespawnPointExplosion(vec3), null, (double) pos.getX() + 0.5, (double) pos.getY() + 0.5, (double) pos.getZ() + 0.5, 5.0F, true, Level.ExplosionInteraction.BLOCK);
                }
                player.swing(InteractionHand.MAIN_HAND);
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if an item placement is banned through the {@link AetherRecipeTypes#ITEM_PLACEMENT_BAN} recipe type.
     *
     * @param level          The {@link Level} that the interaction is in.
     * @param pos            The {@link BlockPos} the interaction is at.
     * @param face           The {@link Direction} of the block face that is interacted with.
     * @param stack          The {@link ItemStack} used for interaction.
     * @param spawnParticles A {@link Boolean} for whether to spawn particles from the interaction's failure.
     * @return Whether the interaction is banned, as a {@link Boolean}.
     */
    public static boolean isItemPlacementBanned(Level level, BlockPos pos, Direction face, ItemStack stack, boolean spawnParticles) {
        if (level.recipeAccess() instanceof RecipeManager recipeManager) {
            for (RecipeHolder<?> recipe : recipeManager.getRecipes()) {
                if (recipe.value().getType() == AetherRecipeTypes.ITEM_PLACEMENT_BAN && ((ItemBanRecipe) recipe.value()).banItem(level, pos, face, stack, spawnParticles)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static ItemStack getInteractionStack(Player player, InteractionHand hand, ItemStack inHand) {
        if (!inHand.isEmpty()) {
            return inHand;
        }
        InteractionHand otherHand = hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
        return player.getItemInHand(otherHand);
    }
}
