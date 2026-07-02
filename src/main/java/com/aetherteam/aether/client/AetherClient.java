package com.aetherteam.aether.client;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.AetherConfig;
import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.client.event.hooks.ClientLifecycleHooks;
import com.aetherteam.aether.client.event.hooks.ClientDimensionTimeHooks;
import com.aetherteam.aether.client.event.hooks.ClientMusicHooks;
import com.aetherteam.aether.client.event.hooks.GuiAccessoryMenuHooks;
import com.aetherteam.aether.client.event.hooks.GuiPerkScreenHooks;
import com.aetherteam.aether.client.event.hooks.GuiTriviaHooks;
import com.aetherteam.aether.client.event.hooks.DungeonOverlayClientHooks;
import com.aetherteam.aether.client.event.hooks.TitleScreenHooks;
import com.aetherteam.aether.client.gui.component.inventory.AccessoryButton;
import com.aetherteam.aether.client.gui.screen.inventory.SunAltarScreen;
import com.aetherteam.aether.client.particle.AetherParticleTypes;
import com.aetherteam.aether.client.renderer.AetherOverlays;
import com.aetherteam.aether.client.renderer.AetherBlockRenderLayers;
import com.aetherteam.aether.client.renderer.AetherRenderers;
import com.aetherteam.aether.client.renderer.level.AetherRenderEffects;
import com.aetherteam.aether.event.hooks.EntityMountHooks;
import com.aetherteam.aether.event.hooks.ItemTooltipHooks;
import com.aetherteam.aether.inventory.menu.AetherMenuTypes;
import com.aetherteam.aether.inventory.menu.LoreBookMenu;
import com.aetherteam.aether.item.AetherItems;
import com.aetherteam.aether.perk.CustomizationsOptions;
import com.aetherteam.nitrogen.attachment.INBTSynchable;
import com.aetherteam.nitrogen.event.listeners.TooltipListeners;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.fabricmc.loader.api.FabricLoader;
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
import net.minecraft.world.entity.player.Input;
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
        initializeClass(CustomizationsOptions.class);
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
        ClientLifecycleEvents.CLIENT_STARTED.register(ClientLifecycleHooks::started);

        ItemTooltipCallback.EVENT.register(ItemTooltipHooks::addItemTooltip);
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
            ClientMusicHooks.tick();
            ClientDimensionTimeHooks.tickTime();
            GuiPerkScreenHooks.handlePatreonRefreshRebound();
            tickPlayerState(client);
            handleAccessoryHotkey(client);
            GuiAccessoryMenuHooks.openAccessoryMenu();
        });
    }

    private static void registerConnectionCallbacks() {
        ClientPlayConnectionEvents.DISCONNECT.register(ClientLifecycleHooks::disconnect);
    }

    private static void registerLevelRenderCallbacks() {
        LevelRenderEvents.COLLECT_SUBMITS.register(DungeonOverlayClientHooks::collectSubmits);
    }

    private static void renderScreenOverlay(Screen currentScreen, GuiGraphicsExtractor guiGraphics) {
        updateAccessoryButtons(currentScreen);
        logJeiOverlayState(currentScreen);
        if (!tipsModLoaded) {
            GuiTriviaHooks.drawTrivia(currentScreen, guiGraphics);
        }
        GuiTriviaHooks.drawAetherTravelMessage(currentScreen, guiGraphics);
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

        syncPlayerInput(client);
        EntityMountHooks.launchMount(client.player);
    }

    private static void syncPlayerInput(Minecraft client) {
        var player = client.player;
        var aetherPlayer = player.getAttachedOrCreate(AetherDataAttachments.AETHER_PLAYER);
        Input keys = player.input.keyPresses;

        boolean isJumping = keys.jump();
        if (isJumping != aetherPlayer.isJumping()) {
            aetherPlayer.setSynched(player.getId(), INBTSynchable.Direction.SERVER, "setJumping", isJumping);
        }

        boolean isMoving = isJumping || keys.forward() || keys.backward() || keys.left() || keys.right() || player.isFallFlying();
        if (isMoving != aetherPlayer.isMoving()) {
            aetherPlayer.setSynched(player.getId(), INBTSynchable.Direction.SERVER, "setMoving", isMoving);
        }

        boolean isHitting = client.options.keyAttack.isDown();
        if (isHitting != aetherPlayer.isHitting()) {
            aetherPlayer.setSynched(player.getId(), INBTSynchable.Direction.SERVER, "setHitting", isHitting);
        }

        boolean gravititeJumpActive = AetherKeys.GRAVITITE_JUMP_ABILITY.isDown();
        if (gravititeJumpActive != aetherPlayer.isGravititeJumpActive()) {
            aetherPlayer.setSynched(player.getId(), INBTSynchable.Direction.SERVER, "setGravititeJumpActive", gravititeJumpActive);
        }
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
            TitleScreenHooks.setCustomSplashText(titleScreen);
        }

        var offsets = com.aetherteam.aether.client.gui.screen.inventory.AetherAccessoriesScreen.getButtonOffset(screen);
        var inventoryAccessoryButton = GuiAccessoryMenuHooks.setupAccessoryButton(screen, offsets);
        if (inventoryAccessoryButton != null && GuiAccessoryMenuHooks.isAccessoryButtonEnabled()) {
            Screens.getWidgets(screen).add(inventoryAccessoryButton);
        }

        GridLayout layout = GuiPerkScreenHooks.setupPerksButtons(screen);
        if (layout != null && !GuiAccessoryMenuHooks.isAccessoryButtonEnabled()) {
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

    private static void initializeClass(Class<?> type) {
        try {
            Class.forName(type.getName(), true, type.getClassLoader());
        } catch (ClassNotFoundException exception) {
            throw new IllegalStateException("Unable to initialize " + type.getName(), exception);
        }
    }
}
