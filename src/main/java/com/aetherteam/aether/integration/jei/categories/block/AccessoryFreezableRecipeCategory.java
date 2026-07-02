package com.aetherteam.aether.integration.jei.categories.block;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.item.AetherItems;
import com.aetherteam.aether.recipe.recipes.block.AccessoryFreezableRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IPlatformFluidHelper;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public class AccessoryFreezableRecipeCategory extends AbstractAetherBlockStateRecipeCategory<AccessoryFreezableRecipe> {
    public static final Identifier UID = Identifier.fromNamespaceAndPath(Aether.MODID, "accessory_freezable");
    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(Aether.MODID, "textures/gui/menu/jei_render.png");
    public static final IRecipeType<AccessoryFreezableRecipe> RECIPE_TYPE = IRecipeType.create(Aether.MODID, "accessory_freezable", AccessoryFreezableRecipe.class);

    public AccessoryFreezableRecipeCategory(IGuiHelper guiHelper, IPlatformFluidHelper<?> fluidHelper) {
        super("accessory_freezable", UID,
            guiHelper.createDrawable(TEXTURE, 0, 0, 84, 28),
            guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(AetherItems.ICE_RING)),
            RECIPE_TYPE, fluidHelper);
    }
}
