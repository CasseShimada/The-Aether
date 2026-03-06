package com.aetherteam.aether.inventory.menu;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.client.gui.screen.inventory.*;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import com.aetherteam.aether.registry.DeferredHolder;
import com.aetherteam.aether.registry.DeferredRegister;

public class AetherMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(BuiltInRegistries.MENU, Aether.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<AetherAccessoriesMenu>> ACCESSORIES = register("accessories", AetherAccessoriesMenu::new);
    public static final DeferredHolder<MenuType<?>, MenuType<LoreBookMenu>> BOOK_OF_LORE = register("book_of_lore", LoreBookMenu::new);
    public static final DeferredHolder<MenuType<?>, MenuType<AltarMenu>> ALTAR = register("altar", AltarMenu::new);
    public static final DeferredHolder<MenuType<?>, MenuType<FreezerMenu>> FREEZER = register("freezer", FreezerMenu::new);
    public static final DeferredHolder<MenuType<?>, MenuType<IncubatorMenu>> INCUBATOR = register("incubator", IncubatorMenu::new);

    private static <T extends AbstractContainerMenu> DeferredHolder<MenuType<?>, MenuType<T>> register(String name, MenuType.MenuSupplier<T> menu) {
        return MENU_TYPES.register(name, () -> new MenuType<>(menu, FeatureFlags.VANILLA_SET));
    }

    public static void registerMenuScreens() {
        AetherMenuTypes.<AetherAccessoriesMenu, AetherAccessoriesScreen>registerScreen(AetherMenuTypes.ACCESSORIES.get(), AetherAccessoriesScreen::new);
        registerScreen(AetherMenuTypes.BOOK_OF_LORE.get(), LoreBookScreen::new);
        registerScreen(AetherMenuTypes.ALTAR.get(), AltarScreen::new);
        registerScreen(AetherMenuTypes.FREEZER.get(), FreezerScreen::new);
        registerScreen(AetherMenuTypes.INCUBATOR.get(), IncubatorScreen::new);
    }

    private static <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> void registerScreen(MenuType<M> menuType, MenuScreens.ScreenConstructor<M, U> constructor) {
        MenuScreens.register(menuType, constructor);
    }
}
