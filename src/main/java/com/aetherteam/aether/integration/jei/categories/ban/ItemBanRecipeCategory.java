package com.aetherteam.aether.integration.jei.categories.ban;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.recipe.recipes.ban.ItemBanRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IPlatformFluidHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SingleRecipeInput;

public class ItemBanRecipeCategory extends AbstractPlacementBanRecipeCategory<ItemStack, Ingredient, SingleRecipeInput, ItemBanRecipe> {
    public static final Identifier UID = Identifier.fromNamespaceAndPath(Aether.MODID, "item_placement_ban");
    public static final IRecipeType<ItemBanRecipe> RECIPE_TYPE = IRecipeType.create(Aether.MODID, "item_placement_ban", ItemBanRecipe.class);

    public ItemBanRecipeCategory(IGuiHelper guiHelper, IPlatformFluidHelper<?> fluidHelper) {
        super(guiHelper, "item_placement_ban", UID,
            guiHelper.createBlankDrawable(116, 18),
            guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(Items.FLINT_AND_STEEL)),
            RECIPE_TYPE, fluidHelper);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ItemBanRecipe recipe, IFocusGroup focusGroup) {
        Ingredient ingredient = recipe.getIngredient();
        if (recipe.getBypassBlock().isEmpty() || recipe.getBypassBlock().get().isEmpty()) {
            builder.addSlot(RecipeIngredientRole.INPUT, 50, 1).add(ingredient).addRichTooltipCallback((recipeSlotView, tooltip) -> this.populateAdditionalInformation(recipe, tooltip));
        } else {
            builder.addSlot(RecipeIngredientRole.INPUT, 1, 1).add(ingredient).addRichTooltipCallback((recipeSlotView, tooltip) -> this.populateAdditionalInformation(recipe, tooltip));
        }
        super.setRecipe(builder, recipe, focusGroup);
    }
}
