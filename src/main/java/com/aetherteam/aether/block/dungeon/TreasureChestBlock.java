package com.aetherteam.aether.block.dungeon;

import com.aetherteam.aether.blockentity.AetherBlockEntityTypes;
import com.aetherteam.aether.blockentity.TreasureChestBlockEntity;
import com.aetherteam.aether.item.components.AetherDataComponents;
import com.aetherteam.aether.item.components.DungeonKind;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.function.Supplier;

/**
 * Mostly copied from {@link ChestBlock}.
 */
public class TreasureChestBlock extends AbstractChestBlock<TreasureChestBlockEntity> implements SimpleWaterloggedBlock {
    public static final MapCodec<TreasureChestBlock> CODEC = simpleCodec(TreasureChestBlock::new);
    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    protected static final VoxelShape SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 14.0, 15.0);

    public TreasureChestBlock(Properties properties) {
        this(properties, AetherBlockEntityTypes.TREASURE_CHEST::get);
    }

    public TreasureChestBlock(Properties properties, Supplier<BlockEntityType<? extends TreasureChestBlockEntity>> blockEntityTypeSupplier) {
        super(properties, blockEntityTypeSupplier);
        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false));
    }

    @Override
    protected MapCodec<? extends AbstractChestBlock<TreasureChestBlockEntity>> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TreasureChestBlockEntity(pos, state);
    }

    public BlockEntityType<? extends TreasureChestBlockEntity> blockEntityType() {
        return this.blockEntityType.get();
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide() ? createTickerHelper(blockEntityType, this.blockEntityType(), TreasureChestBlockEntity::lidAnimateTick) : null;
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof TreasureChestBlockEntity treasureChestBlockEntity) {
            treasureChestBlockEntity.recheckOpen();
        }
    }

    /**
     * [CODE COPY] - {@link ChestBlock#useWithoutItem(BlockState, Level, BlockPos, Player, BlockHitResult)}.<br><br>
     * Handles behavior for checking if a chest is locked and being able to unlock the chest.
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
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof TreasureChestBlockEntity treasureChestBlockEntity) {
            if (!treasureChestBlockEntity.getLocked()) {
                if (level.isClientSide()) {
                    return InteractionResult.SUCCESS;
                } else {
                    MenuProvider menuprovider = this.getMenuProvider(state, level, pos);
                    if (menuprovider != null) {
                        player.openMenu(menuprovider);
                        player.awardStat(Stats.CUSTOM.get(Stats.OPEN_CHEST));
                        PiglinAi.angerNearbyPiglins((ServerLevel) level, player, true);
                    }

                    return InteractionResult.CONSUME;
                }
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof TreasureChestBlockEntity treasureChestBlockEntity) {
            Identifier kind = treasureChestBlockEntity.getKind();
            if (treasureChestBlockEntity.getLocked()) {
                DungeonKind type = stack.get(AetherDataComponents.DUNGEON_KIND);
                if (type != null && type.id().equals(treasureChestBlockEntity.getKind())) {
                    if (!stack.isEmpty() && treasureChestBlockEntity.tryUnlock(player)) {
                        if (player instanceof ServerPlayer) {
                            player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
                        }
                        if (!player.getAbilities().instabuild) {
                            stack.shrink(1);
                        }
                        return InteractionResult.SUCCESS;
                    }
                }
                com.aetherteam.aether.util.MessageUtil.sendPlayerMessage(player, Component.translatable(kind.getNamespace() + "." + kind.getPath() + "_treasure_chest_locked"), true);
            }
        }
        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction direction = context.getHorizontalDirection().getOpposite();
        FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
        return this.defaultBlockState().setValue(FACING, direction).setValue(WATERLOGGED, fluidState.is(Fluids.WATER));
    }

    /**
     * Warning for "deprecation" is suppressed because the method is fine to override.
     */
    @SuppressWarnings("deprecation")
    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    /**
     * Prevents Treasure Chests from being broken when they're locked, but allows it when they're unlocked.
     */
    @Override
    public float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof TreasureChestBlockEntity treasureChestBlockEntity) {
            float f = treasureChestBlockEntity.getLocked() ? state.getDestroySpeed(level, pos) : 3.0F;
            if (f < 0.0F) {
                return 0.0F;
            } else {
                int i = player.hasCorrectToolForDrops(state) ? 30 : 100;
                return player.getDestroySpeed(state) / f / (float) i;
            }
        }
        return super.getDestroyProgress(state, player, level, pos);
    }

    @Override
    protected void onExplosionHit(BlockState state, ServerLevel level, BlockPos pos, Explosion explosion, java.util.function.BiConsumer<ItemStack, BlockPos> dropConsumer) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof TreasureChestBlockEntity treasureChestBlockEntity && treasureChestBlockEntity.getLocked()) {
            return;
        }
        super.onExplosionHit(state, level, pos, explosion, dropConsumer);
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos, Direction direction) {
        return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(level.getBlockEntity(pos));
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    @Override
    public DoubleBlockCombiner.NeighborCombineResult<? extends ChestBlockEntity> combine(BlockState state, Level level, BlockPos pos, boolean flag) {
        return DoubleBlockCombiner.Combiner::acceptNone;
    }

    /**
     * Copies all the NBT data from the block entity to the item when using pick block on the Treasure Chest.<br><br>
     * Warning for "deprecation" is suppressed because the method is fine to override.
     *
     * @param level The {@link Level} the block is in.
     * @param pos   The {@link BlockPos} of the block.
     * @param state The {@link BlockState} of the block.
     * @return The cloned {@link ItemStack} from the block.
     */
    @SuppressWarnings("deprecation")
    @Override
    protected ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData) {
        ItemStack stack = super.getCloneItemStack(level, pos, state, includeData);
        TreasureChestBlockEntity treasureChestBlockEntity = (TreasureChestBlockEntity) level.getBlockEntity(pos);
        if (includeData && treasureChestBlockEntity != null) {
            stack.applyComponents(treasureChestBlockEntity.collectComponents());
        }
        return stack;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess scheduledTickAccess, BlockPos currentPos, Direction direction, BlockPos facingPos, BlockState facingState, RandomSource randomSource) {
        if (state.getValue(WATERLOGGED)) {
            scheduledTickAccess.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return super.updateShape(state, level, scheduledTickAccess, currentPos, direction, facingPos, facingState, randomSource);
    }
}
