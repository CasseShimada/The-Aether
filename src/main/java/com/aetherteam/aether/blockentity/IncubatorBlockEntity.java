package com.aetherteam.aether.blockentity;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.advancement.AetherAdvancementTriggers;
import com.aetherteam.aether.data.resources.registries.AetherDataMaps;
import com.aetherteam.aether.inventory.menu.IncubatorMenu;
import com.aetherteam.aether.recipe.AetherRecipeTypes;
import com.aetherteam.aether.recipe.recipes.item.IncubationRecipe;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.RecipeCraftingHolder;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import javax.annotation.Nullable;
import java.util.List;

/**
 * [CODE COPY] - {@link AbstractFurnaceBlockEntity}.<br><br>
 * Has heavy modifications for Incubator-specific behavior.
 */
public class IncubatorBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer, RecipeCraftingHolder, StackedContentsCompatible {
    private static final int[] SLOTS_NS = {0};
    private static final int[] SLOTS_EW = {1};
    protected NonNullList<ItemStack> items = NonNullList.withSize(2, ItemStack.EMPTY);
    private ServerPlayer player; // The last player to put an item in the egg slot.
    private int litTime; // The current fuel burning progress time.
    private int litDuration; // Total time it takes a fuel item to burn.
    private int incubationProgress; // The current incubation progress time.
    private int incubationTotalTime; // Total time a recipe takes to incubate.
    private int x; // The x position of the block entity.
    private int y; // The y position of the block entity.
    private int z; // The z position of the block entity.
    protected final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> IncubatorBlockEntity.this.litTime;
                case 1 -> IncubatorBlockEntity.this.litDuration;
                case 2 -> IncubatorBlockEntity.this.incubationProgress;
                case 3 -> IncubatorBlockEntity.this.incubationTotalTime;
                case 4 -> IncubatorBlockEntity.this.x;
                case 5 -> IncubatorBlockEntity.this.y;
                case 6 -> IncubatorBlockEntity.this.z;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> IncubatorBlockEntity.this.litTime = value;
                case 1 -> IncubatorBlockEntity.this.litDuration = value;
                case 2 -> IncubatorBlockEntity.this.incubationProgress = value;
                case 3 -> IncubatorBlockEntity.this.incubationTotalTime = value;
                case 4 -> IncubatorBlockEntity.this.x = value;
                case 5 -> IncubatorBlockEntity.this.y = value;
                case 6 -> IncubatorBlockEntity.this.z = value;
            }
        }

        @Override
        public int getCount() {
            return 7;
        }
    };
    private final Object2IntOpenHashMap<ResourceKey<Recipe<?>>> recipesUsed = new Object2IntOpenHashMap<>();
    private final RecipeManager.CachedCheck<SingleRecipeInput, IncubationRecipe> quickCheck;

    public IncubatorBlockEntity(BlockPos pos, BlockState state) {
        this(pos, state, AetherRecipeTypes.INCUBATION);
    }

    public IncubatorBlockEntity(BlockPos pos, BlockState state, RecipeType<IncubationRecipe> recipeType) {
        super(AetherBlockEntityTypes.INCUBATOR.get(), pos, state);
        this.quickCheck = RecipeManager.createCheck(recipeType);
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory playerInventory) {
        return new IncubatorMenu(id, playerInventory, this, this.dataAccess);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, IncubatorBlockEntity blockEntity) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        boolean flag = blockEntity.isLit();
        boolean flag1 = false;

        if (blockEntity.isLit()) {
            --blockEntity.litTime;
        }

        ItemStack itemstack = blockEntity.items.get(1);
        ItemStack itemstack1 = blockEntity.items.get(0);
        boolean flag2 = !itemstack1.isEmpty();
        boolean flag3 = !itemstack.isEmpty();
        if (blockEntity.isLit() || flag3 && flag2) {
            RecipeHolder<IncubationRecipe> recipe;
            if (flag2) {
                recipe = blockEntity.quickCheck.getRecipeFor(new SingleRecipeInput(itemstack1), serverLevel).orElse(null);
            } else {
                recipe = null;
            }

            if (!blockEntity.isLit() && blockEntity.canIncubate(recipe, blockEntity.items)) {
                blockEntity.litTime = blockEntity.getBurnDuration(itemstack);
                blockEntity.litDuration = blockEntity.litTime;
                if (blockEntity.isLit()) {
                    flag1 = true;
                    ItemStack remainder = itemstack.getItem().getCraftingRemainder().create();
                    if (!remainder.isEmpty()) {
                        blockEntity.items.set(1, remainder.copy());
                    } else if (flag3) {
                        itemstack.shrink(1);
                    }
                }
            }

            if (blockEntity.isLit() && blockEntity.canIncubate(recipe, blockEntity.items)) {
                ++blockEntity.incubationProgress;
                if (blockEntity.incubationProgress == blockEntity.incubationTotalTime) {
                    blockEntity.incubationProgress = 0;
                    blockEntity.incubationTotalTime = getTotalIncubationTime(level, blockEntity);
                    if (blockEntity.incubate(recipe, blockEntity.items)) {
                        blockEntity.setRecipeUsed(recipe);
                    }
                    flag1 = true;
                }
            } else {
                blockEntity.incubationProgress = 0;
            }
        } else if (!blockEntity.isLit() && blockEntity.incubationProgress > 0) {
            blockEntity.incubationProgress = Mth.clamp(blockEntity.incubationProgress - 2, 0, blockEntity.incubationTotalTime);
        }

        if (flag != blockEntity.isLit()) {
            flag1 = true;
            state = state.setValue(AbstractFurnaceBlock.LIT, blockEntity.isLit());
            level.setBlock(pos, state, 1 | 2);
        }

        if (flag1) {
            setChanged(level, pos, state);
        }

        if (blockEntity.x != pos.getX()) {
            blockEntity.x = pos.getX();
        }
        if (blockEntity.y != pos.getY()) {
            blockEntity.y = pos.getY();
        }
        if (blockEntity.z != pos.getZ()) {
            blockEntity.z = pos.getZ();
        }
    }

    /**
     * Spawns an entity on top of the incubator with the recipe's NBT data and the item's custom name.
     *
     * @param recipe The {@link IncubationRecipe} being incubated.
     * @param stacks The {@link NonNullList NonNullList<ItemStack>} of items in the menu.
     * @return A {@link Boolean} for whether the item successfully incubated.
     */
    private boolean incubate(@Nullable RecipeHolder<IncubationRecipe> recipe, NonNullList<ItemStack> stacks) {
        if (recipe != null && this.canIncubate(recipe, stacks)) {
            ItemStack itemStack = stacks.getFirst();
            EntityType<?> entityType = recipe.value().getEntity();
            BlockPos spawnPos = this.getBlockPos().above();
            if (this.level != null && !this.level.isClientSide() && this.level instanceof ServerLevel serverLevel) {
                CompoundTag tag = recipe.value().getTag().orElse(null);
                Component customName = itemStack.has(DataComponents.CUSTOM_NAME) ? itemStack.getHoverName() : null;
                Entity entity = entityType.spawn(serverLevel, EntityType.appendDefaultStackConfig(consumerEntity -> {
                    if (tag != null) {
                        consumerEntity.load(TagValueInput.create(ProblemReporter.DISCARDING, serverLevel.registryAccess(), tag));
                    }
                }, serverLevel, itemStack, player), spawnPos, EntitySpawnReason.TRIGGERED, true, false);
                if (entity != null) {
                    entity.setCustomName(customName);
                    if (this.player != null) {
                        AetherAdvancementTriggers.INCUBATION_TRIGGER.trigger(this.player, itemStack);
                    }
                }
            }
            itemStack.shrink(1);
            return true;
        } else {
            return false;
        }
    }

    private boolean canIncubate(@Nullable RecipeHolder<IncubationRecipe> recipe, NonNullList<ItemStack> stacks) {
        return !stacks.getFirst().isEmpty() && recipe != null;
    }

    protected int getBurnDuration(ItemStack fuelStack) {
        return AetherDataMaps.getIncubatorBurnTime(fuelStack);
    }

    private static int getTotalIncubationTime(Level level, IncubatorBlockEntity blockEntity) {
        if (level instanceof ServerLevel serverLevel) {
            return blockEntity.quickCheck.getRecipeFor(new SingleRecipeInput(blockEntity.items.getFirst()), serverLevel).map((recipe) -> recipe.value().getIncubationTime()).orElse(5700);
        }
        return 5700;
    }

    private boolean isLit() {
        return this.litTime > 0;
    }

    public void setPlayer(ServerPlayer player) {
        this.player = player;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemstack : this.items) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int index) {
        return this.items.get(index);
    }

    @Override
    public ItemStack removeItem(int index, int count) {
        return ContainerHelper.removeItem(this.items, index, count);
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        return ContainerHelper.takeItem(this.items, index);
    }

    @Override
    public void clearContent() {
        this.items.clear();
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        ItemStack itemstack = this.items.get(index);
        boolean flag = !stack.isEmpty() && ItemStack.isSameItemSameComponents(itemstack, stack);
        this.items.set(index, stack);
        if (stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize());
        }
        if (index == 0 && !flag) {
            this.incubationTotalTime = getTotalIncubationTime(this.level, this);
            this.incubationProgress = 0;
            this.setChanged();
        }
    }

    @Override
    public int getContainerSize() {
        return this.items.size();
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    @Override
    public void fillStackedContents(StackedItemContents helper) {
        for (ItemStack itemstack : this.items) {
            helper.accountStack(itemstack);
        }
    }

    @Override
    public int[] getSlotsForFace(Direction direction) {
        if (direction == Direction.NORTH || direction == Direction.SOUTH) {
            return SLOTS_NS;
        } else {
            return SLOTS_EW;
        }
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack stack, @Nullable Direction direction) {
        return this.canPlaceItem(index, stack);
    }

    @Override
    public boolean canPlaceItem(int index, ItemStack stack) {
        if (index == 1) {
            return this.getBurnDuration(stack) > 0;
        } else {
            return true;
        }
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        return false;
    }

    @Override
    public void setRecipeUsed(@Nullable RecipeHolder<?> recipe) {
        if (recipe != null) {
            ResourceKey<Recipe<?>> resourcelocation = recipe.id();
            this.recipesUsed.addTo(resourcelocation, 1);
        }
    }

    @Nullable
    @Override
    public RecipeHolder<?> getRecipeUsed() {
        return null;
    }

    @Override
    public void awardUsedRecipes(Player player, List<ItemStack> items) {
    }

    @Override
    public boolean stillValid(Player player) {
        if (this.level.getBlockEntity(this.getBlockPos()) != this) {
            return false;
        } else {
            return player.distanceToSqr(this.getBlockPos().getX() + 0.5, this.getBlockPos().getY() + 0.5, this.getBlockPos().getZ() + 0.5) <= 64.0;
        }
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("menu." + Aether.MODID + ".incubator");
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(input, this.items);
        this.litTime = input.getIntOr("LitTime", 0);
        this.litDuration = this.getBurnDuration(this.items.get(1));
        this.incubationProgress = input.getIntOr("IncubationProgress", 0);
        this.incubationTotalTime = input.getIntOr("IncubationTotalTime", 0);
        this.recipesUsed.clear();
        CompoundTag recipesTag = input.read("RecipesUsed", CompoundTag.CODEC).orElseGet(CompoundTag::new);
        for (String string : recipesTag.keySet()) {
            this.recipesUsed.put(ResourceKey.create(Registries.RECIPE, Identifier.parse(string)), recipesTag.getIntOr(string, 0));
        }
    }

    @Override
    public void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("LitTime", this.litTime);
        output.putInt("IncubationProgress", this.incubationProgress);
        output.putInt("IncubationTotalTime", this.incubationTotalTime);
        ContainerHelper.saveAllItems(output, this.items);
        CompoundTag compoundTag = new CompoundTag();
        this.recipesUsed.forEach((location, integer) -> compoundTag.putInt(location.identifier().toString(), integer));
        output.store("RecipesUsed", CompoundTag.CODEC, compoundTag);
    }
}
