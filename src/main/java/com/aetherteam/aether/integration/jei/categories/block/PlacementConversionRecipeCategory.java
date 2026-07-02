package com.aetherteam.aether.integration.jei.categories.block;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.item.AetherItems;
import com.aetherteam.aether.recipe.recipes.block.PlacementConversionRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IPlatformFluidHelper;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public class PlacementConversionRecipeCategory extends AbstractBiomeParameterRecipeCategory<PlacementConversionRecipe> {
    public static final Identifier UID = Identifier.fromNamespaceAndPath(Aether.MODID, "placement_conversion");
    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(Aether.MODID, "textures/gui/menu/jei_render.png");
    public static final IRecipeType<PlacementConversionRecipe> RECIPE_TYPE = IRecipeType.create(Aether.MODID, "placement_conversion", PlacementConversionRecipe.class);

    public PlacementConversionRecipeCategory(IGuiHelper helper, IPlatformFluidHelper<?> fluidHelper) {
        super("placement_conversion", UID,
            helper.createDrawable(TEXTURE, 0, 0, 84, 28),
            helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(AetherItems.AETHER_PORTAL_FRAME)),
            RECIPE_TYPE, fluidHelper);
    }
}
