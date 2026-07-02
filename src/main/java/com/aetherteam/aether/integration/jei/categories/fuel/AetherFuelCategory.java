package com.aetherteam.aether.integration.jei.categories.fuel;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.block.AetherBlocks;
import com.aetherteam.nitrogen.integration.jei.categories.fuel.AbstractFuelCategory;
import com.aetherteam.nitrogen.integration.jei.categories.fuel.FuelRecipe;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.List;

public class AetherFuelCategory extends AbstractFuelCategory {
    public static final Identifier ICON_TEXTURE = Identifier.fromNamespaceAndPath(Aether.MODID, "textures/gui/sprites/menu/lit_progress_transparent.png");
    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(Aether.MODID, "textures/gui/menu/altar.png");
    public static final IRecipeType<FuelRecipe> RECIPE_TYPE = IRecipeType.create(Aether.MODID, "fuel", FuelRecipe.class);

    public AetherFuelCategory(IGuiHelper helper) {
        super(helper, List.of(AetherBlocks.ALTAR.getName().getString(), AetherBlocks.FREEZER.getName().getString(), AetherBlocks.INCUBATOR.getName().getString()));
    }

    @Override
    public Component getTitle() {
        return Component.translatable("gui." + Aether.MODID + ".jei.fuel");
    }

    @Override
    public IRecipeType<FuelRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public Identifier getBackgroundTexture() {
        return TEXTURE;
    }

    @Override
    public Identifier getIconTexture() {
        return ICON_TEXTURE;
    }

    @Override
    public int getWidth() {
        return 82;
    }

    @Override
    public int getHeight() {
        return 54;
    }
}
