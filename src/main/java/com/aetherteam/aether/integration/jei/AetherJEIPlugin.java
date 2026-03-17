package com.aetherteam.aether.integration.jei;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.block.AetherBlocks;
import com.aetherteam.aether.integration.jei.categories.ban.BlockBanRecipeCategory;
import com.aetherteam.aether.integration.jei.categories.ban.ItemBanRecipeCategory;
import com.aetherteam.aether.integration.jei.categories.block.*;
import com.aetherteam.aether.integration.jei.categories.fuel.AetherFuelCategory;
import com.aetherteam.aether.integration.jei.categories.fuel.AetherFuelRecipeMaker;
import com.aetherteam.aether.integration.jei.categories.item.AltarRepairRecipeCategory;
import com.aetherteam.aether.integration.jei.categories.item.EnchantingRecipeCategory;
import com.aetherteam.aether.integration.jei.categories.item.FreezingRecipeCategory;
import com.aetherteam.aether.integration.jei.categories.item.IncubationRecipeCategory;
import com.aetherteam.aether.item.AetherItems;
import com.aetherteam.aether.recipe.AetherRecipeTypes;
import com.aetherteam.aether.recipe.recipes.ban.BlockBanRecipe;
import com.aetherteam.aether.recipe.recipes.ban.ItemBanRecipe;
import com.aetherteam.aether.recipe.recipes.block.AccessoryFreezableRecipe;
import com.aetherteam.aether.recipe.recipes.block.AmbrosiumRecipe;
import com.aetherteam.aether.recipe.recipes.block.IcestoneFreezableRecipe;
import com.aetherteam.aether.recipe.recipes.block.PlacementConversionRecipe;
import com.aetherteam.aether.recipe.recipes.block.SwetBallRecipe;
import com.aetherteam.aether.recipe.recipes.item.AbstractAetherCookingRecipe;
import com.aetherteam.aether.recipe.recipes.item.AltarRepairRecipe;
import com.aetherteam.aether.recipe.recipes.item.EnchantingRecipe;
import com.aetherteam.aether.recipe.recipes.item.FreezingRecipe;
import com.aetherteam.aether.recipe.recipes.item.IncubationRecipe;
import com.mojang.logging.LogUtils;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IExtraIngredientRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.Blocks;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@JeiPlugin
public class AetherJEIPlugin implements IModPlugin {
    private static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public Identifier getPluginUid() {
        return Identifier.fromNamespaceAndPath(Aether.MODID, "jei");
    }

    @Override
    public void registerExtraIngredients(IExtraIngredientRegistration registration) {
        List<ItemStack> extraItems = BuiltInRegistries.ITEM.stream()
                .map(ItemStack::new)
                .filter(stack -> !stack.isEmpty())
                .filter(stack -> Objects.equals(BuiltInRegistries.ITEM.getKey(stack.getItem()).getNamespace(), Aether.MODID))
                .toList();
        registration.addExtraItemStacks(extraItems);
        LOGGER.info("Registered {} extra Aether item stacks with JEI.", extraItems.size());
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        // Item
        registration.addRecipeCategories(new EnchantingRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new AltarRepairRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new FreezingRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new IncubationRecipeCategory((registration.getJeiHelpers().getGuiHelper())));

        // Fuel
        registration.addRecipeCategories(new AetherFuelCategory(registration.getJeiHelpers().getGuiHelper()));

        // Block
        registration.addRecipeCategories(new AmbrosiumRecipeCategory(registration.getJeiHelpers().getGuiHelper(), registration.getJeiHelpers().getPlatformFluidHelper()));
        registration.addRecipeCategories(new SwetBallRecipeCategory(registration.getJeiHelpers().getGuiHelper(), registration.getJeiHelpers().getPlatformFluidHelper()));
        registration.addRecipeCategories(new IcestoneFreezableRecipeCategory(registration.getJeiHelpers().getGuiHelper(), registration.getJeiHelpers().getPlatformFluidHelper()));
        registration.addRecipeCategories(new AccessoryFreezableRecipeCategory(registration.getJeiHelpers().getGuiHelper(), registration.getJeiHelpers().getPlatformFluidHelper()));
        registration.addRecipeCategories(new PlacementConversionRecipeCategory(registration.getJeiHelpers().getGuiHelper(), registration.getJeiHelpers().getPlatformFluidHelper()));
        registration.addRecipeCategories(new ItemBanRecipeCategory(registration.getJeiHelpers().getGuiHelper(), registration.getJeiHelpers().getPlatformFluidHelper()));
        registration.addRecipeCategories(new BlockBanRecipeCategory(registration.getJeiHelpers().getGuiHelper(), registration.getJeiHelpers().getPlatformFluidHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        if (Minecraft.getInstance().level == null || !(Minecraft.getInstance().level.recipeAccess() instanceof RecipeManager rm)) {
            return;
        }

        // Item
        List<? extends RecipeHolder<?>> allRecipes = rm.getRecipes().stream().toList();
        List<? extends RecipeHolder<? extends AbstractAetherCookingRecipe>> unfilteredRecipes = allRecipes.stream().filter(holder -> holder.value().getType() == AetherRecipeTypes.ENCHANTING.get()).map((holder) -> (RecipeHolder<? extends AbstractAetherCookingRecipe>) holder).toList();
        List<EnchantingRecipe> enchantingRecipes = new ArrayList<>();
        List<AltarRepairRecipe> repairRecipes = new ArrayList<>();
        unfilteredRecipes.stream().filter(recipe -> recipe.value() instanceof EnchantingRecipe).forEach(recipe -> enchantingRecipes.add((EnchantingRecipe) recipe.value()));
        unfilteredRecipes.stream().filter(recipe -> recipe.value() instanceof AltarRepairRecipe).forEach(recipe -> repairRecipes.add((AltarRepairRecipe) recipe.value()));
        registration.addRecipes(EnchantingRecipeCategory.RECIPE_TYPE, enchantingRecipes);
        registration.addRecipes(AltarRepairRecipeCategory.RECIPE_TYPE, repairRecipes);
        registration.addRecipes(FreezingRecipeCategory.RECIPE_TYPE, this.getRecipes(allRecipes, FreezingRecipe.class));
        registration.addRecipes(IncubationRecipeCategory.RECIPE_TYPE, this.getRecipes(allRecipes, IncubationRecipe.class));

        // Fuel
        registration.addRecipes(AetherFuelCategory.RECIPE_TYPE, AetherFuelRecipeMaker.getFuelRecipes());

        // Block
        registration.addRecipes(AmbrosiumRecipeCategory.RECIPE_TYPE, this.getRecipes(allRecipes, AmbrosiumRecipe.class));
        registration.addRecipes(SwetBallRecipeCategory.RECIPE_TYPE, this.getRecipes(allRecipes, SwetBallRecipe.class));
        registration.addRecipes(IcestoneFreezableRecipeCategory.RECIPE_TYPE, this.getRecipes(allRecipes, IcestoneFreezableRecipe.class));
        registration.addRecipes(AccessoryFreezableRecipeCategory.RECIPE_TYPE, this.getRecipes(allRecipes, AccessoryFreezableRecipe.class));
        registration.addRecipes(PlacementConversionRecipeCategory.RECIPE_TYPE, this.getRecipes(allRecipes, PlacementConversionRecipe.class));
        registration.addRecipes(ItemBanRecipeCategory.RECIPE_TYPE, this.getRecipes(allRecipes, ItemBanRecipe.class));
        registration.addRecipes(BlockBanRecipeCategory.RECIPE_TYPE, this.getRecipes(allRecipes, BlockBanRecipe.class));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        // Item
        registration.addRecipeCatalyst(new ItemStack(AetherBlocks.ALTAR.get()), EnchantingRecipeCategory.RECIPE_TYPE, AltarRepairRecipeCategory.RECIPE_TYPE, AetherFuelCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(AetherBlocks.FREEZER.get()), FreezingRecipeCategory.RECIPE_TYPE, AetherFuelCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(AetherBlocks.INCUBATOR.get()), IncubationRecipeCategory.RECIPE_TYPE, AetherFuelCategory.RECIPE_TYPE);

        // Block
        registration.addRecipeCatalyst(new ItemStack(AetherItems.AMBROSIUM_SHARD.get()), AmbrosiumRecipeCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(AetherItems.SWET_BALL.get()), SwetBallRecipeCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(AetherBlocks.ICESTONE.get()), IcestoneFreezableRecipeCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(AetherBlocks.ICESTONE_SLAB.get()), IcestoneFreezableRecipeCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(AetherBlocks.ICESTONE_STAIRS.get()), IcestoneFreezableRecipeCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(AetherBlocks.ICESTONE_WALL.get()), IcestoneFreezableRecipeCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(AetherItems.ICE_RING.get()), AccessoryFreezableRecipeCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(AetherItems.ICE_PENDANT.get()), AccessoryFreezableRecipeCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(AetherItems.AETHER_PORTAL_FRAME.get()), PlacementConversionRecipeCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(Items.FLINT_AND_STEEL), ItemBanRecipeCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(Blocks.TORCH), BlockBanRecipeCategory.RECIPE_TYPE);
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        Set<Identifier> jeiItems = jeiRuntime.getIngredientManager().getAllIngredients(VanillaTypes.ITEM_STACK).stream()
                .map(ItemStack::getItem)
                .map(BuiltInRegistries.ITEM::getKey)
                .filter(id -> Objects.equals(id.getNamespace(), Aether.MODID))
                .collect(LinkedHashSet::new, Set::add, Set::addAll);
        List<Identifier> missingItems = BuiltInRegistries.ITEM.stream()
                .map(BuiltInRegistries.ITEM::getKey)
                .filter(id -> Objects.equals(id.getNamespace(), Aether.MODID))
                .filter(id -> !jeiItems.contains(id))
                .toList();

        if (missingItems.isEmpty()) {
            LOGGER.info("JEI runtime registered {} Aether items.", jeiItems.size());
        } else {
            LOGGER.warn("JEI runtime registered {} Aether items and is still missing {} entries: {}", jeiItems.size(), missingItems.size(), missingItems.stream().limit(20).toList());
        }
    }

    private <T> List<T> getRecipes(List<? extends RecipeHolder<?>> allRecipes, Class<T> recipeClass) {
        return allRecipes.stream().map(RecipeHolder::value).filter(recipeClass::isInstance).map(recipeClass::cast).toList();
    }
}
