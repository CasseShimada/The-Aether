package com.aetherteam.aether.inventory.menu;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.client.gui.screen.inventory.*;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

public final class AetherMenuTypes {
    public static final MenuType<AetherAccessoriesMenu> ACCESSORIES = register("accessories", AetherAccessoriesMenu::new);
    public static final MenuType<LoreBookMenu> BOOK_OF_LORE = register("book_of_lore", LoreBookMenu::new);
    public static final MenuType<AltarMenu> ALTAR = register("altar", AltarMenu::new);
    public static final MenuType<FreezerMenu> FREEZER = register("freezer", FreezerMenu::new);
    public static final MenuType<IncubatorMenu> INCUBATOR = register("incubator", IncubatorMenu::new);

    private static <T extends AbstractContainerMenu> MenuType<T> register(String name, MenuType.MenuSupplier<T> menu) {
        return Registry.register(BuiltInRegistries.MENU, Identifier.fromNamespaceAndPath(Aether.MODID, name), new MenuType<>(menu, FeatureFlags.VANILLA_SET));
    }

    public static void registerMenuScreens() {
        MenuScreens.<AetherAccessoriesMenu, AetherAccessoriesScreen>register(AetherMenuTypes.ACCESSORIES, AetherAccessoriesScreen::new);
        MenuScreens.register(AetherMenuTypes.BOOK_OF_LORE, LoreBookScreen::new);
        MenuScreens.register(AetherMenuTypes.ALTAR, AltarScreen::new);
        MenuScreens.register(AetherMenuTypes.FREEZER, FreezerScreen::new);
        MenuScreens.register(AetherMenuTypes.INCUBATOR, IncubatorScreen::new);
    }

    private AetherMenuTypes() {
    }

    public static void bootstrap() {
    }
}
