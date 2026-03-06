package com.aetherteam.aether.inventory.menu;

import com.aetherteam.aether.data.resources.registries.AetherDataMaps;
import com.aetherteam.aether.inventory.AetherRecipeBookTypes;
import com.aetherteam.aether.inventory.menu.slot.IncubatorFuelSlot;
import com.aetherteam.aether.inventory.menu.slot.IncubatorItemSlot;
import com.aetherteam.aether.recipe.recipes.item.IncubationRecipe;
import com.aetherteam.aether.recipe.recipes.set.AetherRecipePropertySets;
import net.minecraft.core.BlockPos;
import net.minecraft.recipebook.ServerPlaceRecipe;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipePropertySet;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

import java.util.List;

public class IncubatorMenu extends RecipeBookMenu {
    public final Container container;
    public final ContainerData data;
    public final Level level;
    private final RecipePropertySet acceptedInputs;

    public IncubatorMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(2), new SimpleContainerData(7));
    }

    public IncubatorMenu(int containerId, Inventory playerInventory, Container container, ContainerData data) {
        super(AetherMenuTypes.INCUBATOR.get(), containerId);
        checkContainerSize(container, 2);
        checkContainerDataCount(data, 7);
        this.container = container;
        this.data = data;
        this.level = playerInventory.player.level();
        this.acceptedInputs = this.level.recipeAccess().propertySet(AetherRecipePropertySets.INCUBATOR_INPUT);
        this.addSlot(new IncubatorItemSlot(this, container, 0, 73, 17, playerInventory.player));
        this.addSlot(new IncubatorFuelSlot(this, container, 1, 73, 53));
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(playerInventory, k, 8 + k * 18, 142));
        }
        this.addDataSlots(data);
    }

    @Override
    public void fillCraftSlotsStackedContents(StackedItemContents contents) {
        if (this.container instanceof StackedContentsCompatible stackedContentsCompatible) {
            stackedContentsCompatible.fillStackedContents(contents);
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack itemStack1 = slot.getItem();
            itemStack = itemStack1.copy();
            if (index != 1 && index != 0) {
                if (this.canIncubate(itemStack1)) {
                    if (!this.moveItemStackTo(itemStack1, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (this.isFuel(itemStack1)) {
                    if (!this.moveItemStackTo(itemStack1, 1, 2, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 2 && index < 29) {
                    if (!this.moveItemStackTo(itemStack1, 29, 38, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 29 && index < 38 && !this.moveItemStackTo(itemStack1, 2, 29, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemStack1, 2, 38, false)) {
                return ItemStack.EMPTY;
            }
            if (itemStack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
            if (itemStack1.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(player, itemStack1);
        }
        return itemStack;
    }

    protected boolean canIncubate(ItemStack stack) {
        return this.acceptedInputs.test(stack);
    }

    public boolean isFuel(ItemStack stack) {
        return AetherDataMaps.isIncubatorFuel(stack);
    }

    public int getIncubationProgressScaled() {
        return this.data.get(3) != 0 ? (this.data.get(2) * 54) / this.data.get(3) : 0;
    }

    public boolean isIncubating() {
        return this.data.get(0) > 0;
    }

    public int getIncubationTimeRemaining() {
        int i = this.data.get(1);
        if (i == 0) {
            i = 1000;
        }
        return (this.data.get(0) * 11) / i;
    }

    public BlockPos getIncubatorPos() {
        int x = this.data.get(4);
        int y = this.data.get(5);
        int z = this.data.get(6);
        return new BlockPos(x, y, z);
    }

    @Override
    public RecipeBookType getRecipeBookType() {
        return AetherRecipeBookTypes.INCUBATOR;
    }

    @Override
    public RecipeBookMenu.PostPlaceAction handlePlacement(boolean useMaxItems, boolean isCreative, RecipeHolder<?> recipe, final ServerLevel level, Inventory playerInventory) {
        final List<Slot> list = List.of(this.getSlot(0));
        return ServerPlaceRecipe.placeRecipe(new ServerPlaceRecipe.CraftingMenuAccess<>() {
            @Override
            public void fillCraftSlotsStackedContents(StackedItemContents stackedItemContents) {
                IncubatorMenu.this.fillCraftSlotsStackedContents(stackedItemContents);
            }

            @Override
            public void clearCraftingContent() {
                list.forEach((slot) -> slot.set(ItemStack.EMPTY));
            }

            @Override
            public boolean recipeMatches(RecipeHolder<IncubationRecipe> recipe) {
                return recipe.value().matches(new SingleRecipeInput(IncubatorMenu.this.container.getItem(0)), level);
            }
        }, 1, 1, List.of(this.getSlot(0)), list, playerInventory, (RecipeHolder<IncubationRecipe>) recipe, useMaxItems, isCreative);
    }
}
