package com.aetherteam.aether.item.tools.abilities;

import com.aetherteam.aether.AetherConfig;
import com.aetherteam.aether.AetherTags;
import com.aetherteam.aether.block.AetherBlocks;
import com.aetherteam.aether.loot.AetherLoot;
import com.aetherteam.aether.loot.AetherLootContexts;
import com.aetherteam.aether.network.AetherPacketSender;
import com.aetherteam.aether.network.packet.clientbound.ToolDebuffPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Map;

public final class ToolAbilityHooks {
    public enum ToolAction {
        AXE_STRIP,
        SHOVEL_FLATTEN,
        HOE_TILL
    }

    /**
     * Blocks able to be stripped, and the equivalent result block.
     */
    public static final Map<Block, Block> STRIPPABLES = Map.of(
            AetherBlocks.SKYROOT_LOG, AetherBlocks.STRIPPED_SKYROOT_LOG,
            AetherBlocks.GOLDEN_OAK_LOG, AetherBlocks.STRIPPED_SKYROOT_LOG,
            AetherBlocks.SKYROOT_WOOD, AetherBlocks.STRIPPED_SKYROOT_WOOD,
            AetherBlocks.GOLDEN_OAK_WOOD, AetherBlocks.STRIPPED_SKYROOT_WOOD);

    /**
     * Blocks able to be flattened, and the equivalent result block.
     */
    public static final Map<Block, Block> FLATTENABLES = Map.of(
            AetherBlocks.AETHER_GRASS_BLOCK, AetherBlocks.AETHER_DIRT_PATH,
            AetherBlocks.ENCHANTED_AETHER_GRASS_BLOCK, AetherBlocks.AETHER_DIRT_PATH,
            AetherBlocks.AETHER_DIRT, AetherBlocks.AETHER_DIRT_PATH);

    /**
     * Blocks able to be tilled, and the equivalent result block.
     */
    public static final Map<Block, Block> TILLABLES = Map.of(
            AetherBlocks.AETHER_DIRT, AetherBlocks.AETHER_FARMLAND,
            AetherBlocks.AETHER_GRASS_BLOCK, AetherBlocks.AETHER_FARMLAND,
            AetherBlocks.ENCHANTED_AETHER_GRASS_BLOCK, AetherBlocks.AETHER_FARMLAND,
            AetherBlocks.AETHER_DIRT_PATH, AetherBlocks.AETHER_FARMLAND);

    public static boolean debuffTools;

    private ToolAbilityHooks() {
    }

    /**
     * Handles modifying blocks when a tool action is performed on them.
     *
     * @param accessor The {@link LevelAccessor} of the level.
     * @param pos      The {@link Block} within the level.
     * @param old      The old {@link BlockState} of the block an action is being performed on.
     * @param action   The tool action being performed on the block.
     * @return The new {@link BlockState} of the block.
     */
    public static BlockState setupItemAbilities(LevelAccessor accessor, BlockPos pos, BlockState old, ToolAction action) {
        Block oldBlock = old.getBlock();
        if (action == ToolAction.AXE_STRIP) {
            if (STRIPPABLES.containsKey(oldBlock)) {
                return STRIPPABLES.get(oldBlock).withPropertiesOf(old);
            }
        } else if (action == ToolAction.SHOVEL_FLATTEN) {
            if (FLATTENABLES.containsKey(oldBlock)) {
                return FLATTENABLES.get(oldBlock).withPropertiesOf(old);
            }
        } else if (action == ToolAction.HOE_TILL) {
            if (accessor.getBlockState(pos.above()).isAir()) {
                if (TILLABLES.containsKey(oldBlock)) {
                    return TILLABLES.get(oldBlock).withPropertiesOf(old);
                }
            }
        }
        return old;
    }

    /**
     * Handles ability for {@link com.aetherteam.aether.item.tools.abilities.HolystoneTool}.
     *
     * @see HolystoneTool#dropAmbrosium(Player, Level, BlockPos, ItemStack, BlockState)
     */
    public static void handleHolystoneToolAbility(Player player, Level level, BlockPos pos, ItemStack stack, BlockState blockState) {
        if (stack.getItem() instanceof HolystoneTool holystoneTool) {
            holystoneTool.dropAmbrosium(player, level, pos, stack, blockState);
        }
    }

    /**
     * Handles ability for {@link com.aetherteam.aether.item.tools.abilities.ZaniteTool}.
     * @see ZaniteTool#increaseSpeed(ItemStack, float)
     */
    public static float handleZaniteToolAbility(ItemStack stack, float speed) {
        if (stack.getItem() instanceof ZaniteTool zaniteTool) {
            return zaniteTool.increaseSpeed(stack, speed);
        }
        return speed;
    }

    /**
     * Debuffs the mining speed of blocks if they're Aether blocks (according to the description id, and the tags {@link AetherTags.Blocks#TREATED_AS_AETHER_BLOCK} and {@link AetherTags.Blocks#TREATED_AS_VANILLA_BLOCK}),
     * and if the item is non-Aether (according to the description id, and the tag {@link AetherTags.Items#TREATED_AS_AETHER_ITEM}), as long as it's not an empty item (no item at all) and as long as its detected as the correct tool type for the block.<br><br>
     * The debuffed value is the original value to the power of -0.2.
     *
     * @param state The {@link BlockState} of the block being mined.
     * @param stack The {@link ItemStack} being used for mining.
     * @param speed The mining speed of the stack, as a {@link Float}.
     * @return The debuffed mining speed, as a {@link Float}.
     */
    public static float reduceToolEffectiveness(Player player, BlockState state, ItemStack stack, float speed) {
        if (debuffTools) {
            if ((state.getBlock().getDescriptionId().startsWith("block.aether.") || state.is(AetherTags.Blocks.TREATED_AS_AETHER_BLOCK)) && !state.is(AetherTags.Blocks.TREATED_AS_VANILLA_BLOCK)) {
                if (!stack.isEmpty() && stack.isCorrectToolForDrops(state) && !stack.getItem().getDescriptionId().startsWith("item.aether.") && !stack.is(AetherTags.Items.TREATED_AS_AETHER_ITEM)) {
                    speed = (float) Math.max(Math.pow(speed, speed > 1.0 ? -0.5 : 1.5), 1.0);
                }
            }
        }
        return speed;
    }

    /**
     * Sets up the debuff tool state based on the current server value and attempts to sync the state to the client if needed
     *
     * @param player Current player logging into the server
     */
    public static void setDebuffToolsState(ServerPlayer player) {
        if (debuffTools) {
            AetherPacketSender.sendToPlayer(player, new ToolDebuffPacket(true));
        } else if (AetherConfig.SERVER.tools_debuff.get()) {
            debuffTools = true;

            AetherPacketSender.sendToAllPlayers(new ToolDebuffPacket(true));
        }
    }

    /**
     * Method used to reset the debuffTools state to false on player logout.
     */
    public static void resetDebuffToolsState() {
        debuffTools = false;
    }

    /**
     * Spawns Golden Amber at the user's click position when stripping Golden Oak Logs ({@link AetherTags.Blocks#GOLDEN_OAK_LOGS}), as long as the tool in usage can harvest Golden Amber ({@link AetherTags.Items#GOLDEN_AMBER_HARVESTERS}).<br><br>
     * The drops are handled using a special loot context type {@link AetherLootContexts#STRIPPING}.
     *
     * @param accessor The {@link LevelAccessor} of the level.
     * @param state    The {@link BlockState} an action is being performed on.
     * @param stack    The {@link ItemStack} performing an action.
     * @param action   The tool action being performed.
     * @param context  The {@link UseOnContext} of this interaction.
     */
    public static void stripGoldenOak(LevelAccessor accessor, BlockState state, ItemStack stack, ToolAction action, UseOnContext context) {
        if (action == ToolAction.AXE_STRIP) {
            if (accessor instanceof Level level) {
                if (state.is(AetherTags.Blocks.GOLDEN_OAK_LOGS) && stack.is(AetherTags.Items.GOLDEN_AMBER_HARVESTERS)) {
                    if (level.getServer() != null && level instanceof ServerLevel serverLevel) {
                        Vec3 vector = context.getClickLocation();
                        LootParams parameters = new LootParams.Builder(serverLevel).withParameter(LootContextParams.TOOL, stack).create(AetherLootContexts.STRIPPING);
                        LootTable lootTable = level.getServer().reloadableRegistries().getLootTable(AetherLoot.STRIP_GOLDEN_OAK);
                        List<ItemStack> list = lootTable.getRandomItems(parameters);
                        for (ItemStack itemStack : list) {
                            ItemEntity itemEntity = new ItemEntity(level, vector.x(), vector.y(), vector.z(), itemStack);
                            itemEntity.setDefaultPickUpDelay();
                            level.addFreshEntity(itemEntity);
                        }
                    }
                }
            }
        }
    }
}
