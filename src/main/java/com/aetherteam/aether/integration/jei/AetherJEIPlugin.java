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
import com.aetherteam.aether.recipe.recipes.ban.BlockBanRecipe;
import com.aetherteam.aether.recipe.recipes.ban.ItemBanRecipe;
import com.aetherteam.aether.recipe.recipes.block.AccessoryFreezableRecipe;
import com.aetherteam.aether.recipe.recipes.block.AmbrosiumRecipe;
import com.aetherteam.aether.recipe.recipes.block.IcestoneFreezableRecipe;
import com.aetherteam.aether.recipe.recipes.block.PlacementConversionRecipe;
import com.aetherteam.aether.recipe.recipes.block.SwetBallRecipe;
import com.aetherteam.aether.recipe.recipes.item.AltarRepairRecipe;
import com.aetherteam.aether.recipe.recipes.item.EnchantingRecipe;
import com.aetherteam.aether.recipe.recipes.item.FreezingRecipe;
import com.aetherteam.aether.recipe.recipes.item.IncubationRecipe;
import com.mojang.logging.LogUtils;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IIngredientAliasRegistration;
import mezz.jei.api.registration.IExtraIngredientRegistration;
import mezz.jei.api.registration.IModInfoRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.Blocks;
import org.slf4j.Logger;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

@JeiPlugin
public class AetherJEIPlugin implements IModPlugin {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final List<String> AETHER_SEARCH_ALIASES = List.of("aether", "the aether", "天境");
    private static final List<Supplier<ItemStack>> EXTRA_INGREDIENTS = List.of(
            () -> new ItemStack(AetherItems.GOLDEN_FEATHER),
            () -> new ItemStack(AetherItems.MUSIC_DISC_CHINCHILLA),
            () -> new ItemStack(AetherItems.MUSIC_DISC_HIGH),
            () -> new ItemStack(AetherItems.MUSIC_DISC_KLEPTO),
            () -> new ItemStack(AetherItems.VALKYRIE_QUEEN_SPAWN_EGG),
            () -> new ItemStack(AetherItems.SLIDER_SPAWN_EGG),
            () -> new ItemStack(AetherItems.SUN_SPIRIT_SPAWN_EGG)
    );
    private static IJeiRuntime runtime;
    private static String lastOverlayLogState;

    @Override
    public Identifier getPluginUid() {
        return Identifier.fromNamespaceAndPath(Aether.MODID, "jei");
    }

    @Override
    public void registerIngredientAliases(IIngredientAliasRegistration registration) {
        List<ItemStack> aetherItems = getAllAetherItemStacks();
        registration.addAliases(VanillaTypes.ITEM_STACK, aetherItems, AETHER_SEARCH_ALIASES);
        LOGGER.info("Registered {} JEI search aliases for {} Aether item stacks.", AETHER_SEARCH_ALIASES.size(), aetherItems.size());
    }

    @Override
    public void registerModInfo(IModInfoRegistration registration) {
        registration.addModAliases(Aether.MODID, AETHER_SEARCH_ALIASES);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registerItemCategories(registration);
        registerFuelCategories(registration);
        registerBlockCategories(registration);
    }

    @Override
    public void registerExtraIngredients(IExtraIngredientRegistration registration) {
        registration.addExtraItemStacks(EXTRA_INGREDIENTS.stream().map(Supplier::get).toList());
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager recipeManager = getRecipeManager();
        if (recipeManager == null) {
            return;
        }

        List<?> allRecipes = AetherJeiRecipeCollector.getAllRecipes(recipeManager);
        registerItemRecipes(registration, allRecipes);
        registerFuelRecipes(registration);
        registerBlockRecipes(registration, allRecipes);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registerItemCatalysts(registration);
        registerBlockCatalysts(registration);
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        runtime = jeiRuntime;
        lastOverlayLogState = null;
        AetherJeiRuntimeLogger.logRuntimeAvailability(LOGGER, jeiRuntime);
    }

    @Override
    public void onRuntimeUnavailable() {
        runtime = null;
        lastOverlayLogState = null;
    }

    public static void logVisibleOverlayState(Screen screen) {
        if (runtime == null || screen == null) {
            return;
        }

        lastOverlayLogState = AetherJeiRuntimeLogger.logVisibleOverlayState(LOGGER, runtime, screen, lastOverlayLogState);
    }

    private static void registerItemCategories(IRecipeCategoryRegistration registration) {
        var guiHelper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(new EnchantingRecipeCategory(guiHelper));
        registration.addRecipeCategories(new AltarRepairRecipeCategory(guiHelper));
        registration.addRecipeCategories(new FreezingRecipeCategory(guiHelper));
        registration.addRecipeCategories(new IncubationRecipeCategory(guiHelper));
    }

    private static void registerFuelCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new AetherFuelCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    private static void registerBlockCategories(IRecipeCategoryRegistration registration) {
        var guiHelper = registration.getJeiHelpers().getGuiHelper();
        var fluidHelper = registration.getJeiHelpers().getPlatformFluidHelper();
        registration.addRecipeCategories(new AmbrosiumRecipeCategory(guiHelper, fluidHelper));
        registration.addRecipeCategories(new SwetBallRecipeCategory(guiHelper, fluidHelper));
        registration.addRecipeCategories(new IcestoneFreezableRecipeCategory(guiHelper, fluidHelper));
        registration.addRecipeCategories(new AccessoryFreezableRecipeCategory(guiHelper, fluidHelper));
        registration.addRecipeCategories(new PlacementConversionRecipeCategory(guiHelper, fluidHelper));
        registration.addRecipeCategories(new ItemBanRecipeCategory(guiHelper, fluidHelper));
        registration.addRecipeCategories(new BlockBanRecipeCategory(guiHelper, fluidHelper));
    }

    private static RecipeManager getRecipeManager() {
        if (Minecraft.getInstance().level == null || !(Minecraft.getInstance().level.recipeAccess() instanceof RecipeManager recipeManager)) {
            return null;
        }
        return recipeManager;
    }

    private static void registerItemRecipes(IRecipeRegistration registration, List<?> allRecipes) {
        @SuppressWarnings("unchecked")
        List<? extends net.minecraft.world.item.crafting.RecipeHolder<?>> recipes = (List<? extends net.minecraft.world.item.crafting.RecipeHolder<?>>) allRecipes;
        List<EnchantingRecipe> enchantingRecipes = AetherJeiRecipeCollector.getEnchantingRecipes(recipes);
        List<AltarRepairRecipe> repairRecipes = AetherJeiRecipeCollector.getRepairRecipes(recipes);
        registration.addRecipes(EnchantingRecipeCategory.RECIPE_TYPE, enchantingRecipes);
        registration.addRecipes(AltarRepairRecipeCategory.RECIPE_TYPE, repairRecipes);
        registration.addRecipes(FreezingRecipeCategory.RECIPE_TYPE, AetherJeiRecipeCollector.getRecipes(recipes, FreezingRecipe.class));
        registration.addRecipes(IncubationRecipeCategory.RECIPE_TYPE, AetherJeiRecipeCollector.getRecipes(recipes, IncubationRecipe.class));
    }

    private static void registerFuelRecipes(IRecipeRegistration registration) {
        registration.addRecipes(AetherFuelCategory.RECIPE_TYPE, AetherFuelRecipeMaker.getFuelRecipes());
    }

    private static void registerBlockRecipes(IRecipeRegistration registration, List<?> allRecipes) {
        @SuppressWarnings("unchecked")
        List<? extends net.minecraft.world.item.crafting.RecipeHolder<?>> recipes = (List<? extends net.minecraft.world.item.crafting.RecipeHolder<?>>) allRecipes;
        registration.addRecipes(AmbrosiumRecipeCategory.RECIPE_TYPE, AetherJeiRecipeCollector.getRecipes(recipes, AmbrosiumRecipe.class));
        registration.addRecipes(SwetBallRecipeCategory.RECIPE_TYPE, AetherJeiRecipeCollector.getRecipes(recipes, SwetBallRecipe.class));
        registration.addRecipes(IcestoneFreezableRecipeCategory.RECIPE_TYPE, AetherJeiRecipeCollector.getRecipes(recipes, IcestoneFreezableRecipe.class));
        registration.addRecipes(AccessoryFreezableRecipeCategory.RECIPE_TYPE, AetherJeiRecipeCollector.getRecipes(recipes, AccessoryFreezableRecipe.class));
        registration.addRecipes(PlacementConversionRecipeCategory.RECIPE_TYPE, AetherJeiRecipeCollector.getRecipes(recipes, PlacementConversionRecipe.class));
        registration.addRecipes(ItemBanRecipeCategory.RECIPE_TYPE, AetherJeiRecipeCollector.getRecipes(recipes, ItemBanRecipe.class));
        registration.addRecipes(BlockBanRecipeCategory.RECIPE_TYPE, AetherJeiRecipeCollector.getRecipes(recipes, BlockBanRecipe.class));
    }

    private static void registerItemCatalysts(IRecipeCatalystRegistration registration) {
        ItemStack altar = new ItemStack(AetherBlocks.ALTAR);
        addItemCatalyst(registration, altar, EnchantingRecipeCategory.RECIPE_TYPE, AltarRepairRecipeCategory.RECIPE_TYPE, AetherFuelCategory.RECIPE_TYPE);

        ItemStack freezer = new ItemStack(AetherBlocks.FREEZER);
        addItemCatalyst(registration, freezer, FreezingRecipeCategory.RECIPE_TYPE, AetherFuelCategory.RECIPE_TYPE);

        ItemStack incubator = new ItemStack(AetherBlocks.INCUBATOR);
        addItemCatalyst(registration, incubator, IncubationRecipeCategory.RECIPE_TYPE, AetherFuelCategory.RECIPE_TYPE);
    }

    private static void registerBlockCatalysts(IRecipeCatalystRegistration registration) {
        addItemCatalyst(registration, new ItemStack(AetherItems.AMBROSIUM_SHARD), AmbrosiumRecipeCategory.RECIPE_TYPE);
        addItemCatalyst(registration, new ItemStack(AetherItems.SWET_BALL), SwetBallRecipeCategory.RECIPE_TYPE);
        addItemCatalyst(registration,
                new ItemStack(AetherBlocks.ICESTONE),
                IcestoneFreezableRecipeCategory.RECIPE_TYPE);
        addItemCatalyst(registration,
                new ItemStack(AetherBlocks.ICESTONE_SLAB),
                IcestoneFreezableRecipeCategory.RECIPE_TYPE);
        addItemCatalyst(registration,
                new ItemStack(AetherBlocks.ICESTONE_STAIRS),
                IcestoneFreezableRecipeCategory.RECIPE_TYPE);
        addItemCatalyst(registration,
                new ItemStack(AetherBlocks.ICESTONE_WALL),
                IcestoneFreezableRecipeCategory.RECIPE_TYPE);
        addItemCatalyst(registration,
                new ItemStack(AetherItems.ICE_RING),
                AccessoryFreezableRecipeCategory.RECIPE_TYPE);
        addItemCatalyst(registration,
                new ItemStack(AetherItems.ICE_PENDANT),
                AccessoryFreezableRecipeCategory.RECIPE_TYPE);
        addItemCatalyst(registration, new ItemStack(AetherItems.AETHER_PORTAL_FRAME), PlacementConversionRecipeCategory.RECIPE_TYPE);
        addItemCatalyst(registration, new ItemStack(Items.FLINT_AND_STEEL), ItemBanRecipeCategory.RECIPE_TYPE);
        addItemCatalyst(registration, new ItemStack(Blocks.TORCH), BlockBanRecipeCategory.RECIPE_TYPE);
    }

    @SafeVarargs
    private static void addItemCatalyst(IRecipeCatalystRegistration registration, ItemStack stack, mezz.jei.api.recipe.types.IRecipeType<?>... recipeTypes) {
        registration.addRecipeCatalyst(VanillaTypes.ITEM_STACK, stack, recipeTypes);
    }

    private static List<ItemStack> getAllAetherItemStacks() {
        return BuiltInRegistries.ITEM.stream()
                .map(ItemStack::new)
                .filter(stack -> !stack.isEmpty())
                .filter(stack -> Objects.equals(BuiltInRegistries.ITEM.getKey(stack.getItem()).getNamespace(), Aether.MODID))
                .toList();
    }
}
