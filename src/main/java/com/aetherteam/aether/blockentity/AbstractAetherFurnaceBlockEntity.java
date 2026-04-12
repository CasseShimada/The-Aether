package com.aetherteam.aether.blockentity;

import com.aetherteam.aether.AetherTags;
import com.aetherteam.aether.mixin.mixins.common.accessor.AbstractFurnaceBlockEntityAccessor;
import net.minecraft.core.*;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * [CODE COPY] - {@link AbstractFurnaceBlockEntity}.<br><br>
 * Certain static methods are copied with minor noted changes, and use accessors for {@link AbstractFurnaceBlockEntity}.
 */
public abstract class AbstractAetherFurnaceBlockEntity extends AbstractFurnaceBlockEntity {
    private static final int[] SLOTS_FOR_UP = new int[]{0};
    private static final int[] SLOTS_FOR_DOWN = new int[]{2, 0};
    private static final int[] SLOTS_FOR_SIDES = new int[]{1};
    protected ItemStack remainderItem = ItemStack.EMPTY;

    public AbstractAetherFurnaceBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, RecipeType<? extends AbstractCookingRecipe> recipeType) {
        super(type, pos, state, recipeType);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, AbstractAetherFurnaceBlockEntity blockEntity) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        AbstractFurnaceBlockEntityAccessor abstractFurnaceBlockEntityAccessor = (AbstractFurnaceBlockEntityAccessor) blockEntity;
        boolean flag = abstractFurnaceBlockEntityAccessor.aether$getLitTimeRemaining() > 0;
        boolean flag1 = false;

        if (abstractFurnaceBlockEntityAccessor.aether$getLitTimeRemaining() > 0) {
            abstractFurnaceBlockEntityAccessor.aether$setLitTimeRemaining(abstractFurnaceBlockEntityAccessor.aether$getLitTimeRemaining() - 1);
        }

        ItemStack itemstack = blockEntity.items.get(1);
        ItemStack itemstack1 = blockEntity.items.get(0);
        boolean flag2 = !itemstack1.isEmpty();
        boolean flag3 = !itemstack.isEmpty();
        if (abstractFurnaceBlockEntityAccessor.aether$getLitTimeRemaining() > 0 || flag3 && flag2) {
            RecipeHolder<? extends AbstractCookingRecipe> recipe;
            if (flag2) {
                recipe = abstractFurnaceBlockEntityAccessor.aether$getQuickCheck().getRecipeFor(new SingleRecipeInput(itemstack1), serverLevel).orElse(null);
            } else {
                recipe = null;
            }

            int i = blockEntity.getMaxStackSize();
            if (abstractFurnaceBlockEntityAccessor.aether$getLitTimeRemaining() <= 0 && canBurn(level.registryAccess(), recipe, new SingleRecipeInput(itemstack1), blockEntity.items, i)) {
                abstractFurnaceBlockEntityAccessor.aether$setLitTimeRemaining(blockEntity.getBurnDuration(itemstack));
                abstractFurnaceBlockEntityAccessor.aether$setLitTotalTime(abstractFurnaceBlockEntityAccessor.aether$getLitTimeRemaining());
                if (abstractFurnaceBlockEntityAccessor.aether$getLitTimeRemaining() > 0) {
                    flag1 = true;
                    ItemStack remainder = itemstack.getItem().getCraftingRemainder().create();
                    if (!remainder.isEmpty()) {
                        blockEntity.items.set(1, remainder.copy());
                    } else if (flag3) {
                        itemstack.shrink(1);
                    }
                }
            }

            if (abstractFurnaceBlockEntityAccessor.aether$getLitTimeRemaining() > 0 && canBurn(level.registryAccess(), recipe, new SingleRecipeInput(itemstack1), blockEntity.items, i)) {
                abstractFurnaceBlockEntityAccessor.aether$setCookingTimer(abstractFurnaceBlockEntityAccessor.aether$getCookingTimer() + 1);
                if (abstractFurnaceBlockEntityAccessor.aether$getCookingTimer() == abstractFurnaceBlockEntityAccessor.aether$getCookingTotalTime()) {
                    abstractFurnaceBlockEntityAccessor.aether$setCookingTimer(0);
                    abstractFurnaceBlockEntityAccessor.aether$setCookingTotalTime(getTotalCookTime(serverLevel, blockEntity));
                    if (blockEntity.burn(level.registryAccess(), recipe, blockEntity.items, i)) {
                        blockEntity.setRecipeUsed(recipe);
                    }

                    flag1 = true;
                }
            } else {
                abstractFurnaceBlockEntityAccessor.aether$setCookingTimer(0);
            }
        } else if (abstractFurnaceBlockEntityAccessor.aether$getLitTimeRemaining() <= 0 && abstractFurnaceBlockEntityAccessor.aether$getCookingTimer() > 0) {
            abstractFurnaceBlockEntityAccessor.aether$setCookingTimer(Mth.clamp(abstractFurnaceBlockEntityAccessor.aether$getCookingTimer() - 2, 0, abstractFurnaceBlockEntityAccessor.aether$getCookingTotalTime()));
        }

        if (flag != (abstractFurnaceBlockEntityAccessor.aether$getLitTimeRemaining() > 0)) {
            flag1 = true;
            state = state.setValue(AbstractFurnaceBlock.LIT, abstractFurnaceBlockEntityAccessor.aether$getLitTimeRemaining() > 0);
            level.setBlock(pos, state, 1 | 2);
        }

        if (flag1) {
            setChanged(level, pos, state);
        }

        if (blockEntity.items.get(0).isEmpty() && blockEntity.items.get(2).isEmpty()) {
            blockEntity.remainderItem = ItemStack.EMPTY; // Resets the remainder item variable used for hopper extraction at the end of the tick loop. This is necessary so that it actually has enough time to get extracted.
        }
    }

    /**
     * Ensures that NBT is carried over between input and result stacks.<br><br>
     * Warning for "unchecked" is suppressed because casting {@link Recipe}<{@link WorldlyContainer}> is fine and done by vanilla.
     *
     * @param recipe    The {@link Recipe Recipe<?>} being burned.
     * @param stacks    The {@link NonNullList NonNullList<ItemStack>} of items in the menu.
     * @param stackSize The max stack size as an {@link Integer}.
     * @return A {@link Boolean} for whether the item successfully burnt.
     */
    @SuppressWarnings("unchecked")
    private boolean burn(RegistryAccess registryAccess, @Nullable RecipeHolder<?> recipe, NonNullList<ItemStack> stacks, int stackSize) {
        if (recipe != null && canBurn(registryAccess, recipe, new SingleRecipeInput(this.items.getFirst()), stacks, stackSize)) {
            ItemStack inputSlotStack = stacks.get(0);
            ItemStack resultStack = ((Recipe<SingleRecipeInput>) recipe.value()).assemble(new SingleRecipeInput(this.items.getFirst()));
            ItemStack resultSlotStack = stacks.get(2);

            if (inputSlotStack.is(resultStack.getItem()) || resultStack.is(AetherTags.Items.SAVE_NBT_IN_RECIPE)) {
                resultStack = new ItemStack(resultStack.typeHolder(), 1, inputSlotStack.getComponentsPatch());
            }
            if (inputSlotStack.is(resultStack.getItem())) {
                resultStack.setDamageValue(0);
            }

            if (resultSlotStack.isEmpty()) {
                stacks.set(2, resultStack.copy());
            } else if (resultSlotStack.is(resultStack.getItem())) {
                resultSlotStack.grow(resultStack.getCount());
            }

            ItemStack inputRemainder = inputSlotStack.getItem().getCraftingRemainder().create();
            ItemStack resultRemainder = resultStack.getItem().getCraftingRemainder().create();
            if (!inputRemainder.isEmpty() && !inputRemainder.is(resultRemainder.getItem())) {
                stacks.set(0, inputRemainder.copy());
            } else {
                inputSlotStack.shrink(1);
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int[] getSlotsForFace(Direction direction) {
        if (direction == Direction.DOWN) {
            return SLOTS_FOR_DOWN;
        } else {
            return direction == Direction.UP ? SLOTS_FOR_UP : SLOTS_FOR_SIDES;
        }
    }

    @Override
    public boolean canPlaceItem(int index, ItemStack stack) {
        if (index == 2) {
            return false;
        } else if (index != 1) {
            return true;
        } else {
            return this.getBurnDuration(stack) > 0;
        }
    }

    /**
     * Allows the Aether's furnaces to have remaining crafting byproducts extracted (like buckets) alongside product items.
     *
     * @param index     The {@link Integer} for the slot index.
     * @param stack     The {@link ItemStack} trying to be taken from the block.
     * @param direction The {@link Direction} for a face.
     * @return Whether the item can be taken by a hopper through the face, as a {@link Boolean}.
     */
    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        AbstractFurnaceBlockEntityAccessor abstractFurnaceBlockEntityAccessor = (AbstractFurnaceBlockEntityAccessor) this;
        boolean hasRecipe = this.level instanceof ServerLevel serverLevel
            && abstractFurnaceBlockEntityAccessor.aether$getQuickCheck().getRecipeFor(new SingleRecipeInput(this.items.getFirst()), serverLevel).isPresent();
        if (this.remainderItem.isEmpty()) {
            if (hasRecipe) {
                this.remainderItem = stack.getItem().getCraftingRemainder().create(); // Stores the correlating crafting remainder item.
            }
        }
        if (direction == Direction.DOWN && index == 0) {
            if (!this.remainderItem.isEmpty()) {
                return stack.is(this.remainderItem.getItem()); // An item can be taken as long as it matches the stored crafting remainder item.
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        super.setItem(index, stack);
        if (this.getLevel() != null) {
            this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 1 | 2);
        }
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    private static boolean canBurn(RegistryAccess registryAccess, @Nullable RecipeHolder<?> recipe, SingleRecipeInput input, NonNullList<ItemStack> items, int maxCount) {
        if (recipe == null || input.isEmpty()) {
            return false;
        }
        @SuppressWarnings("unchecked")
        ItemStack result = ((Recipe<SingleRecipeInput>) recipe.value()).assemble(input);
        if (result.isEmpty()) {
            return false;
        }
        ItemStack output = items.get(2);
        if (output.isEmpty()) {
            return true;
        }
        if (!ItemStack.isSameItemSameComponents(output, result)) {
            return false;
        }
        return output.getCount() < maxCount && output.getCount() < output.getMaxStackSize();
    }

    private static int getTotalCookTime(ServerLevel level, AbstractAetherFurnaceBlockEntity blockEntity) {
        AbstractFurnaceBlockEntityAccessor accessor = (AbstractFurnaceBlockEntityAccessor) blockEntity;
        return accessor.aether$getQuickCheck().getRecipeFor(new SingleRecipeInput(blockEntity.items.getFirst()), level).map(recipe -> recipe.value().cookingTime()).orElse(200);
    }

    protected abstract int getBurnDuration(ItemStack fuelStack);
}
