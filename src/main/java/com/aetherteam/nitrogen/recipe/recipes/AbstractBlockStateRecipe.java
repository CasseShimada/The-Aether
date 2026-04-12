package com.aetherteam.nitrogen.recipe.recipes;

import com.aetherteam.nitrogen.recipe.BlockPropertyPair;
import com.aetherteam.nitrogen.recipe.BlockStateIngredient;
import com.aetherteam.nitrogen.recipe.BlockStateRecipeUtil;
import com.aetherteam.nitrogen.recipe.input.BlockStateRecipeInput;
import net.minecraft.commands.CacheableFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.Map;
import java.util.Optional;

public abstract class AbstractBlockStateRecipe implements BlockStateRecipe {
    protected final RecipeType<?> type;
    protected final BlockStateIngredient ingredient;
    protected final BlockPropertyPair result;
    protected final Optional<Identifier> functionId;
    protected final Optional<CacheableFunction> function;

    public AbstractBlockStateRecipe(RecipeType<?> type, BlockStateIngredient ingredient, BlockPropertyPair result, Optional<Identifier> function) {
        this.type = type;
        this.ingredient = ingredient;
        this.result = result;
        this.functionId = function;
        this.function = function.map(CacheableFunction::new);
    }

    @Override
    public boolean matches(Level level, BlockPos pos, BlockState state) {
        return this.ingredient.test(state);
    }

    @Override
    public BlockState getResultState(BlockState oldState) {
        BlockState newState = this.result.block().defaultBlockState();
        for (Property<?> property : oldState.getProperties()) {
            if (newState.hasProperty(property)) {
                newState = setPropertyFromState(oldState, newState, property);
            }
        }
        if (this.result.properties().isPresent()) {
            for (Map.Entry<Property<?>, Comparable<?>> entry : this.result.properties().get().entrySet()) {
                newState = BlockStateRecipeUtil.setHelper(entry, newState);
            }
        }
        return newState;
    }

    @Override
    public BlockStateIngredient getIngredient() {
        return this.ingredient;
    }

    @Override
    public BlockPropertyPair getResult() {
        return this.result;
    }

    @Override
    public Optional<Identifier> getFunctionId() {
        return this.functionId;
    }

    @Override
    public Optional<CacheableFunction> getFunction() {
        return this.function;
    }

    @Override
    public boolean matches(BlockStateRecipeInput input, Level level) {
        return input != null && this.ingredient.test(input.state());
    }

    @Override
    public ItemStack assemble(BlockStateRecipeInput input) {
        return new ItemStack(this.result.block());
    }

    @Override
    public String group() {
        return "";
    }

    @Override
    @SuppressWarnings("unchecked")
    public RecipeType<? extends Recipe<BlockStateRecipeInput>> getType() {
        return (RecipeType<? extends Recipe<BlockStateRecipeInput>>) this.type;
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static BlockState setPropertyFromState(BlockState fromState, BlockState toState, Property<?> property) {
        try {
            Comparable value = fromState.getValue((Property) property);
            return toState.setValue((Property) property, value);
        } catch (IllegalArgumentException exception) {
            return toState;
        }
    }

    public interface Factory<T extends AbstractBlockStateRecipe> {
        T create(BlockStateIngredient ingredient, BlockPropertyPair result, Optional<Identifier> function);
    }
}
