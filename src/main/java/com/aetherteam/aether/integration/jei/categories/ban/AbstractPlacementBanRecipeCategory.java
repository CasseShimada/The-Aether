package com.aetherteam.aether.integration.jei.categories.ban;

import com.aetherteam.aether.integration.jei.categories.BiomeTooltip;
import com.aetherteam.aether.recipe.recipes.ban.AbstractPlacementBanRecipe;
import com.aetherteam.aether.integration.jei.categories.AbstractRecipeCategory;
import com.aetherteam.nitrogen.recipe.BlockPropertyPair;
import com.aetherteam.nitrogen.recipe.BlockStateIngredient;
import com.aetherteam.nitrogen.recipe.BlockStateRecipeUtil;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IPlatformFluidHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public abstract class AbstractPlacementBanRecipeCategory<T, S extends Predicate<T>, F extends RecipeInput, R extends AbstractPlacementBanRecipe<T, S, F>> extends AbstractRecipeCategory<R> implements BiomeTooltip {
    protected final IPlatformFluidHelper<?> fluidHelper;
    private final IDrawable slot;

    public AbstractPlacementBanRecipeCategory(IGuiHelper guiHelper, String id, Identifier uid, IDrawable background, IDrawable icon, IRecipeType<R> recipeType, IPlatformFluidHelper<?> fluidHelper) {
        super(id, uid, background, icon, recipeType);
        this.fluidHelper = fluidHelper;
        this.slot = guiHelper.getSlotDrawable();
    }

    @Override
    public Component getTitle() {
        return Component.translatable("gui.aether.jei." + this.id);
    }

    @Override
    public int getWidth() {
        return this.background.getWidth();
    }

    @Override
    public int getHeight() {
        return this.background.getHeight();
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, R recipe, IFocusGroup focusGroup) {
        Optional<BlockStateIngredient> bypassBlockIngredient = recipe.getBypassBlock();
        if (bypassBlockIngredient.isPresent() && !bypassBlockIngredient.get().isEmpty()) {
            BlockPropertyPair[] pairs = bypassBlockIngredient.get().getPairs();
            if (pairs != null) {
                List<Object> ingredients = this.setupIngredients(pairs);
                builder.addSlot(RecipeIngredientRole.INPUT, 99, 1).addIngredientsUnsafe(ingredients);
            }
        }
    }

    protected List<Object> setupIngredients(BlockPropertyPair[] pairs) {
        List<Object> ingredients = new ArrayList<>();
        if (Minecraft.getInstance().level != null) {
            for (BlockPropertyPair pair : pairs) {
                if (pair.block() instanceof LiquidBlock liquidBlock) {
                    ingredients.add(this.fluidHelper.create(liquidBlock.defaultBlockState().getFluidState().getType().builtInRegistryHolder(), this.fluidHelper.bucketVolume()));
                } else {
                    BlockState state = pair.block().defaultBlockState();
                    if (pair.properties().isPresent()) {
                        for (Map.Entry<Property<?>, Comparable<?>> propertyEntry : pair.properties().get().entrySet()) {
                            state = BlockStateRecipeUtil.setHelper(propertyEntry, state);
                        }
                    }
                    ItemStack stack = new ItemStack(pair.block());
                    stack = stack.isEmpty() ? new ItemStack(Blocks.STONE) : stack;
                    ingredients.add(stack);
                }
            }
        }
        return ingredients;
    }

    @Override
    public void draw(R recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
        if (recipe.getBypassBlock().isEmpty() || recipe.getBypassBlock().get().isEmpty()) {
            this.slot.draw(guiGraphics, 49, 0);
        } else {
            this.slot.draw(guiGraphics);
            this.slot.draw(guiGraphics, 98, 0);
            String text = Component.translatable("gui.aether.jei.bypass").getString();
            Font font = Minecraft.getInstance().font;
            guiGraphics.text(font, text, 24, 5, 0xFF808080);
        }
    }

    protected void populateAdditionalInformation(R recipe, ITooltipBuilder tooltip) {
        if (Minecraft.getInstance().level != null) {
            this.populateBiomeInformation(recipe.getBiome().left().orElse(null), recipe.getBiome().right().orElse(null), tooltip);
        }
    }
}
