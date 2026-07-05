package com.aetherteam.aether.inventory.menu;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.client.gui.screen.inventory.*;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;

public final class AetherMenuTypes {
    public static final MenuType<AetherAccessoriesMenu> ACCESSORIES = Registry.register(
            BuiltInRegistries.MENU,
            Identifier.fromNamespaceAndPath(Aether.MODID, "accessories"),
            new MenuType<>(AetherAccessoriesMenu::new, FeatureFlags.VANILLA_SET));
    public static final MenuType<LoreBookMenu> BOOK_OF_LORE = Registry.register(
            BuiltInRegistries.MENU,
            Identifier.fromNamespaceAndPath(Aether.MODID, "book_of_lore"),
            new MenuType<>(LoreBookMenu::new, FeatureFlags.VANILLA_SET));
    public static final MenuType<AltarMenu> ALTAR = Registry.register(
            BuiltInRegistries.MENU,
            Identifier.fromNamespaceAndPath(Aether.MODID, "altar"),
            new MenuType<>(AltarMenu::new, FeatureFlags.VANILLA_SET));
    public static final MenuType<FreezerMenu> FREEZER = Registry.register(
            BuiltInRegistries.MENU,
            Identifier.fromNamespaceAndPath(Aether.MODID, "freezer"),
            new MenuType<>(FreezerMenu::new, FeatureFlags.VANILLA_SET));
    public static final MenuType<IncubatorMenu> INCUBATOR = Registry.register(
            BuiltInRegistries.MENU,
            Identifier.fromNamespaceAndPath(Aether.MODID, "incubator"),
            new MenuType<>(IncubatorMenu::new, FeatureFlags.VANILLA_SET));

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
