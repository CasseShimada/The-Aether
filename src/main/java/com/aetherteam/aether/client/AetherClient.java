package com.aetherteam.aether.client;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.AetherConfig;
import com.aetherteam.aether.client.event.hooks.AudioHooks;
import com.aetherteam.aether.client.event.hooks.CapabilityClientHooks;
import com.aetherteam.aether.client.event.hooks.DimensionClientHooks;
import com.aetherteam.aether.client.event.hooks.GuiHooks;
import com.aetherteam.aether.client.event.hooks.LevelClientHooks;
import com.aetherteam.aether.client.event.hooks.MenuHooks;
import com.aetherteam.aether.client.gui.component.inventory.AccessoryButton;
import com.aetherteam.aether.client.gui.screen.inventory.SunAltarScreen;
import com.aetherteam.aether.client.particle.AetherParticleTypes;
import com.aetherteam.aether.client.renderer.AetherOverlays;
import com.aetherteam.aether.client.renderer.AetherRenderers;
import com.aetherteam.aether.client.renderer.level.AetherRenderEffects;
import com.aetherteam.aether.event.hooks.AbilityHooks;
import com.aetherteam.aether.inventory.menu.AetherMenuTypes;
import com.aetherteam.aether.inventory.menu.LoreBookMenu;
import com.aetherteam.aether.item.AetherItems;
import com.aetherteam.aether.perk.CustomizationsOptions;
import com.aetherteam.nitrogen.event.listeners.TooltipListeners;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.fabricmc.loader.api.FabricLoader;
import com.google.common.reflect.Reflection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class AetherClient {
    public static Map<Predicate<ItemStack>, Identifier> CAPE_SECRETS = new HashMap<>();
    private static boolean initialized;

    public static void init() {
        if (initialized) {
            return;
        }
        initialized = true;

        Reflection.initialize(CustomizationsOptions.class);
        AetherRenderers.registerAccessoryRenderers();
        AetherAtlases.registerTreasureChestAtlases();
        AetherAtlases.registerWoodTypeAtlases();
        registerItemModelProperties();
        registerTooltipOverrides();
        registerLoreOverrides();
        AetherMenuTypes.registerMenuScreens();
        AetherColorResolvers.registerBlockColor();
        AetherColorResolvers.registerItemColor();
        AetherKeys.registerKeyMappings();
        AetherRecipeCategories.registerRecipeCategories();
        AetherParticleTypes.registerParticleFactories();
        AetherOverlays.registerOverlays();
        AetherRenderers.registerEntityRenderers();
        AetherRenderers.registerLayerDefinitions();
        AetherRenderers.addEntityLayers();
        AetherRenderers.bakeModels();
        AetherRenderEffects.registerRenderEffects();

        registerClientCallbacks();
    }

    public static void registerItemModelProperties() {
        // 1.21.11 item model properties are data-driven; keep custom cape predicates active.

        CAPE_SECRETS.put((stack) -> stack.getHoverName().getString().equalsIgnoreCase("swuff_'s cape"), Identifier.fromNamespaceAndPath(Aether.MODID, "textures/models/accessory/capes/swuff_accessory.png"));
    }

    public static void registerTooltipOverrides() {
        TooltipListeners.PREDICATES.put(AetherItems.BLUE_GUMMY_SWET.get().builtInRegistryHolder(), (player, stack, components, context, component) -> {
            if (AetherConfig.SERVER.healing_gummy_swets.get() && component.getContents() instanceof TranslatableContents contents && contents.getKey().endsWith(".1")) {
                return Component.translatable(contents.getKey() + ".health");
            } else {
                return component;
            }
        });
        TooltipListeners.PREDICATES.put(AetherItems.GOLDEN_GUMMY_SWET.get().builtInRegistryHolder(), (player, stack, components, context, component) -> {
            if (AetherConfig.SERVER.healing_gummy_swets.get() && component.getContents() instanceof TranslatableContents contents && contents.getKey().endsWith(".1")) {
                return Component.translatable(contents.getKey() + ".health");
            } else {
                return component;
            }
        });
        TooltipListeners.PREDICATES.put(AetherItems.LIFE_SHARD.get().builtInRegistryHolder(), (player, stack, components, context, component) -> {
            if (component.getContents() instanceof TranslatableContents contents && contents.getKey().endsWith(".1")) {
                return Component.translatable(contents.getKey(), AetherConfig.SERVER.maximum_life_shards.get());
            } else {
                return component;
            }
        });
    }

    /**
     * Applies a unique lore entry in the Book of Lore for the Hammer of Jeb Easter Egg item texture.
     */
    public static void registerLoreOverrides() {
        LoreBookMenu.addLoreEntryOverride(registryAccess -> stack -> stack.is(AetherItems.HAMMER_OF_KINGBDOGZ.get()) && stack.getHoverName().getString().equalsIgnoreCase("hammer of jeb"), "lore.item.aether.hammer_of_jeb");
        LoreBookMenu.addLoreEntryOverride(registryAccess -> stack -> ItemStack.isSameItemSameComponents(stack, AetherItems.createSwetBannerItemStack(registryAccess.lookupOrThrow(Registries.BANNER_PATTERN))), "lore.item.aether.swet_banner");
    }

    private static void registerClientCallbacks() {
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            configureScreen(screen);
            ScreenEvents.afterRender(screen).register((currentScreen, guiGraphics, mouseX, mouseY, tickDelta) -> {
                Screens.getButtons(currentScreen).forEach(widget -> {
                    if (widget instanceof AccessoryButton accessoryButton) {
                        accessoryButton.updateButtonState();
                    }
                });
                if (!FabricLoader.getInstance().isModLoaded("tipsmod")) {
                    GuiHooks.drawTrivia(currentScreen, guiGraphics);
                }
                GuiHooks.drawAetherTravelMessage(currentScreen, guiGraphics);
            });
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            AudioHooks.tick();
            DimensionClientHooks.tickTime();
            GuiHooks.handlePatreonRefreshRebound();

            if (client.player != null) {
                CapabilityClientHooks.AetherPlayerHooks.movementInput(client.player, client.player.input);
            }

            if (client.screen instanceof AbstractContainerScreen<?> containerScreen
                && !AetherConfig.CLIENT.disable_accessory_button.get()
                && AetherKeys.OPEN_ACCESSORY_INVENTORY.consumeClick()) {
                containerScreen.onClose();
            }

            GuiHooks.openAccessoryMenu();
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            AudioHooks.stop();
            AbilityHooks.ToolHooks.resetDebuffToolsState();
        });

        WorldRenderEvents.BEFORE_DEBUG_RENDER.register(context -> {
            Minecraft minecraft = Minecraft.getInstance();
            LevelClientHooks.renderDungeonBlockOverlays(context.matrices(), minecraft.gameRenderer.getMainCamera(), null, minecraft);
        });
    }

    private static void configureScreen(Screen screen) {
        if (screen instanceof TitleScreen titleScreen) {
            MenuHooks.setCustomSplashText(titleScreen);
        }

        var offsets = com.aetherteam.aether.client.gui.screen.inventory.AetherAccessoriesScreen.getButtonOffset(screen);
        var inventoryAccessoryButton = GuiHooks.setupAccessoryButton(screen, offsets);
        if (inventoryAccessoryButton != null && GuiHooks.isAccessoryButtonEnabled()) {
            Screens.getButtons(screen).add(inventoryAccessoryButton);
        }

        GridLayout layout = GuiHooks.setupPerksButtons(screen);
        if (layout != null && !GuiHooks.isAccessoryButtonEnabled()) {
            layout.visitWidgets(widget -> {
                if (widget instanceof AbstractWidget abstractWidget) {
                    Screens.getButtons(screen).add(abstractWidget);
                }
            });
        }
    }

    /**
     * Used to work around a classloading crash on the server.
     */
    public static void setToSunAltarScreen(Component name, int timeScale) {
        Minecraft.getInstance().setScreen(new SunAltarScreen(name, timeScale));
    }
}
