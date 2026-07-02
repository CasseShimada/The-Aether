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
import com.aetherteam.aether.client.renderer.AetherBlockRenderLayers;
import com.aetherteam.aether.client.renderer.AetherRenderers;
import com.aetherteam.aether.client.renderer.level.AetherRenderEffects;
import com.aetherteam.aether.event.hooks.AbilityHooks;
import com.aetherteam.aether.event.hooks.EntityHooks;
import com.aetherteam.aether.event.hooks.ItemHooks;
import com.aetherteam.aether.inventory.menu.AetherMenuTypes;
import com.aetherteam.aether.inventory.menu.LoreBookMenu;
import com.aetherteam.aether.item.AetherItems;
import com.aetherteam.aether.perk.CustomizationsOptions;
import com.aetherteam.nitrogen.event.listeners.TooltipListeners;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.fabricmc.loader.api.FabricLoader;
import com.google.common.reflect.Reflection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
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
import java.lang.reflect.Method;

public class AetherClient {
    public static Map<Predicate<ItemStack>, Identifier> CAPE_SECRETS = new HashMap<>();
    private static boolean initialized;
    private static boolean jeiLoaded;
    private static boolean tipsModLoaded;
    private static boolean jeiOverlayLoggerResolved;
    private static Method jeiOverlayLogger;

    public static void init() {
        if (initialized) {
            return;
        }
        initialized = true;
        captureLoadedClientMods();
        registerClientContent();
        registerClientCallbacks();
    }

    public static void registerItemModelProperties() {
        // Item model properties are data-driven; keep custom cape predicates active.

        CAPE_SECRETS.put((stack) -> stack.getHoverName().getString().equalsIgnoreCase("swuff_'s cape"), Identifier.fromNamespaceAndPath(Aether.MODID, "textures/models/accessory/capes/swuff_accessory.png"));
    }

    public static void registerTooltipOverrides() {
        TooltipListeners.onTooltipCreationLowPriority();
        registerHealingGummySwetOverride(AetherItems.BLUE_GUMMY_SWET.builtInRegistryHolder());
        registerHealingGummySwetOverride(AetherItems.GOLDEN_GUMMY_SWET.builtInRegistryHolder());
        registerLifeShardOverride();
    }

    private static void registerHealingGummySwetOverride(net.minecraft.core.Holder.Reference<net.minecraft.world.item.Item> itemHolder) {
        TooltipListeners.PREDICATES.put(itemHolder, (player, stack, components, context, component) -> {
            if (AetherConfig.SERVER.healing_gummy_swets.get() && component.getContents() instanceof TranslatableContents contents && contents.getKey().endsWith(".1")) {
                return Component.translatable(contents.getKey() + ".health");
            }
            return component;
        });
    }

    private static void registerLifeShardOverride() {
        TooltipListeners.PREDICATES.put(AetherItems.LIFE_SHARD.builtInRegistryHolder(), (player, stack, components, context, component) -> {
            if (component.getContents() instanceof TranslatableContents contents && contents.getKey().endsWith(".1")) {
                return Component.translatable(contents.getKey(), AetherConfig.SERVER.maximum_life_shards.get());
            }
            return component;
        });
    }

    /**
     * Applies a unique lore entry in the Book of Lore for the Hammer of Jeb Easter Egg item texture.
     */
    public static void registerLoreOverrides() {
        LoreBookMenu.addLoreEntryOverride(registryAccess -> stack -> stack.is(AetherItems.HAMMER_OF_KINGBDOGZ) && stack.getHoverName().getString().equalsIgnoreCase("hammer of jeb"), "lore.item.aether.hammer_of_jeb");
        LoreBookMenu.addLoreEntryOverride(registryAccess -> stack -> ItemStack.isSameItemSameComponents(stack, AetherItems.createSwetBannerItemStack(registryAccess.lookupOrThrow(Registries.BANNER_PATTERN))), "lore.item.aether.swet_banner");
    }

    private static void captureLoadedClientMods() {
        jeiLoaded = FabricLoader.getInstance().isModLoaded("jei");
        tipsModLoaded = FabricLoader.getInstance().isModLoaded("tipsmod");
    }

    private static void registerClientContent() {
        Reflection.initialize(CustomizationsOptions.class);
        registerVisualContent();
        registerMenuAndInputContent();
        registerTooltipOverrides();
        registerLoreOverrides();
    }

    private static void registerVisualContent() {
        AetherRenderers.registerAccessoryRenderers();
        AetherAtlases.registerTreasureChestAtlases();
        AetherAtlases.registerWoodTypeAtlases();
        registerItemModelProperties();
        AetherColorResolvers.registerItemColor();
        AetherParticleTypes.registerParticleFactories();
        AetherOverlays.registerOverlays();
        AetherRenderers.registerEntityRenderers();
        AetherRenderers.registerLayerDefinitions();
        AetherBlockRenderLayers.register();
        AetherRenderers.addEntityLayers();
        AetherRenderers.bakeModels();
        AetherRenderEffects.registerRenderEffects();
    }

    private static void registerMenuAndInputContent() {
        AetherMenuTypes.registerMenuScreens();
        AetherKeys.registerKeyMappings();
        AetherRecipeCategories.registerRecipeCategories();
    }

    private static void registerClientCallbacks() {
        registerLifecycleCallbacks();
        registerScreenCallbacks();
        registerTickCallbacks();
        registerConnectionCallbacks();
        registerLevelRenderCallbacks();
    }

    private static void registerLifecycleCallbacks() {
        ClientLifecycleEvents.CLIENT_STARTED.register(client ->
                AetherColorResolvers.registerBlockColor(client.getBlockColors()));

        ItemTooltipCallback.EVENT.register((stack, context, tooltipType, components) ->
                ItemHooks.addDungeonTooltips(components, stack, tooltipType));
    }

    private static void registerScreenCallbacks() {
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            configureScreen(screen);
            ScreenEvents.afterExtract(screen).register((currentScreen, guiGraphics, mouseX, mouseY, tickDelta) ->
                    renderScreenOverlay(currentScreen, guiGraphics));
        });
    }

    private static void registerTickCallbacks() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            AudioHooks.tick();
            DimensionClientHooks.tickTime();
            GuiHooks.handlePatreonRefreshRebound();
            tickPlayerState(client);
            handleAccessoryHotkey(client);
            GuiHooks.openAccessoryMenu();
        });
    }

    private static void registerConnectionCallbacks() {
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            AudioHooks.stop();
            AbilityHooks.ToolHooks.resetDebuffToolsState();
        });
    }

    private static void registerLevelRenderCallbacks() {
        LevelRenderEvents.COLLECT_SUBMITS.register(context -> {
            Minecraft minecraft = Minecraft.getInstance();
            LevelClientHooks.renderDungeonBlockOverlays(context.poseStack(), context.submitNodeCollector(), minecraft.gameRenderer.mainCamera(), context.levelState().cameraRenderState.cullFrustum, minecraft);
        });
    }

    private static void renderScreenOverlay(Screen currentScreen, GuiGraphicsExtractor guiGraphics) {
        updateAccessoryButtons(currentScreen);
        logJeiOverlayState(currentScreen);
        if (!tipsModLoaded) {
            GuiHooks.drawTrivia(currentScreen, guiGraphics);
        }
        GuiHooks.drawAetherTravelMessage(currentScreen, guiGraphics);
    }

    private static void updateAccessoryButtons(Screen currentScreen) {
        Screens.getWidgets(currentScreen).forEach(widget -> {
            if (widget instanceof AccessoryButton accessoryButton) {
                accessoryButton.updateButtonState();
            }
        });
    }

    private static void tickPlayerState(Minecraft client) {
        if (client.player == null) {
            return;
        }

        CapabilityClientHooks.AetherPlayerHooks.movementInput(client.player, client.player.input);
        CapabilityClientHooks.AetherPlayerHooks.tickInput(client.player);
        EntityHooks.launchMount(client.player);
    }

    private static void handleAccessoryHotkey(Minecraft client) {
        if (!(ClientCompat.screen(client) instanceof AbstractContainerScreen<?> containerScreen)) {
            return;
        }
        if (AetherConfig.CLIENT.disable_accessory_button.get() || !AetherKeys.OPEN_ACCESSORY_INVENTORY.consumeClick()) {
            return;
        }
        containerScreen.onClose();
    }

    private static void configureScreen(Screen screen) {
        if (screen instanceof TitleScreen titleScreen) {
            MenuHooks.setCustomSplashText(titleScreen);
        }

        var offsets = com.aetherteam.aether.client.gui.screen.inventory.AetherAccessoriesScreen.getButtonOffset(screen);
        var inventoryAccessoryButton = GuiHooks.setupAccessoryButton(screen, offsets);
        if (inventoryAccessoryButton != null && GuiHooks.isAccessoryButtonEnabled()) {
            Screens.getWidgets(screen).add(inventoryAccessoryButton);
        }

        GridLayout layout = GuiHooks.setupPerksButtons(screen);
        if (layout != null && !GuiHooks.isAccessoryButtonEnabled()) {
            addPerkWidgets(screen, layout);
        }
    }

    private static void addPerkWidgets(Screen screen, GridLayout layout) {
        layout.visitWidgets(widget -> {
            if (widget instanceof AbstractWidget abstractWidget) {
                Screens.getWidgets(screen).add(abstractWidget);
            }
        });
    }

    private static void logJeiOverlayState(Screen screen) {
        if (!jeiLoaded) {
            return;
        }

        if (!jeiOverlayLoggerResolved) {
            jeiOverlayLoggerResolved = true;
            try {
                Class<?> pluginClass = Class.forName("com.aetherteam.aether.integration.jei.AetherJEIPlugin");
                jeiOverlayLogger = pluginClass.getMethod("logVisibleOverlayState", Screen.class);
            } catch (ReflectiveOperationException | LinkageError exception) {
                Aether.LOGGER.debug("Failed to resolve JEI overlay logger", exception);
                jeiOverlayLogger = null;
            }
        }

        if (jeiOverlayLogger == null) {
            return;
        }

        try {
            jeiOverlayLogger.invoke(null, screen);
        } catch (ReflectiveOperationException exception) {
            Aether.LOGGER.debug("Failed to query JEI overlay state", exception);
        }
    }

    /**
     * Used to work around a classloading crash on the server.
     */
    public static void setToSunAltarScreen(Component name, int timeScale) {
        ClientCompat.setScreen(Minecraft.getInstance(), new SunAltarScreen(name, timeScale));
    }
}
