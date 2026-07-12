package com.aetherteam.aether.client;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.AetherConfig;
import com.aetherteam.aether.AetherTags;
import com.aetherteam.aether.api.AetherAdvancementSoundOverrides;
import com.aetherteam.aether.client.event.hooks.ClientMusicHooks;
import com.aetherteam.aether.client.event.hooks.ClientScreenHooks;
import com.aetherteam.aether.client.event.hooks.ClientTickHooks;
import com.aetherteam.aether.client.event.hooks.DungeonOverlayClientHooks;
import com.aetherteam.aether.client.event.hooks.AbilityTooltipHooks;
import com.aetherteam.aether.client.gui.screen.inventory.SunAltarScreen;
import com.aetherteam.aether.client.particle.AetherParticleTypes;
import com.aetherteam.aether.client.renderer.AetherOverlays;
import com.aetherteam.aether.client.renderer.AetherBlockRenderLayers;
import com.aetherteam.aether.client.renderer.AetherRenderers;
import com.aetherteam.aether.client.renderer.level.AetherRenderEffects;
import com.aetherteam.aether.item.tools.abilities.ToolAbilityHooks;
import com.aetherteam.aether.inventory.menu.AetherMenuTypes;
import com.aetherteam.aether.inventory.menu.LoreBookMenu;
import com.aetherteam.aether.item.AetherItems;
import com.aetherteam.aether.perk.CustomizationsOptions;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.Minecraft;
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
        registerClientContent();
        registerClientCallbacks();
    }

    public static void registerItemModelProperties() {
        // Item model properties are data-driven; keep custom cape predicates active.

        CAPE_SECRETS.put((stack) -> stack.getHoverName().getString().equalsIgnoreCase("swuff_'s cape"), Identifier.fromNamespaceAndPath(Aether.MODID, "textures/models/accessory/capes/swuff_accessory.png"));
    }

    public static void registerTooltipOverrides() {
        AbilityTooltipHooks.onTooltipCreationLowPriority();
        registerHealingGummySwetOverride(AetherItems.BLUE_GUMMY_SWET.builtInRegistryHolder());
        registerHealingGummySwetOverride(AetherItems.GOLDEN_GUMMY_SWET.builtInRegistryHolder());
        registerLifeShardOverride();
    }

    private static void registerHealingGummySwetOverride(net.minecraft.core.Holder.Reference<net.minecraft.world.item.Item> itemHolder) {
        AbilityTooltipHooks.PREDICATES.put(itemHolder, (player, stack, components, context, component) -> {
            if (AetherConfig.SERVER.healing_gummy_swets.get() && component.getContents() instanceof TranslatableContents contents && contents.getKey().endsWith(".1")) {
                return Component.translatable(contents.getKey() + ".health");
            }
            return component;
        });
    }

    private static void registerLifeShardOverride() {
        AbilityTooltipHooks.PREDICATES.put(AetherItems.LIFE_SHARD.builtInRegistryHolder(), (player, stack, components, context, component) -> {
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

    private static void registerClientContent() {
        CustomizationsOptions.bootstrap();
        AetherAdvancementSoundOverrides.bootstrap();
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
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> AetherColorResolvers.registerBlockColor(client.getBlockColors()));

        ItemTooltipCallback.EVENT.register((stack, context, flag, components) -> {
            if (!flag.isCreative()) {
                return;
            }

            int position = components.size();
            Component itemName = stack.getItem().getName(stack);
            for (int i = 0; i < position; i++) {
                Component component = components.get(i);
                if (component.getString().equals(itemName.getString())) {
                    position = i + 1;
                    break;
                }
            }
            if (stack.is(AetherTags.Items.BRONZE_DUNGEON_LOOT)) {
                components.add(position, AetherItems.BRONZE_DUNGEON_TOOLTIP);
            }
            if (stack.is(AetherTags.Items.SILVER_DUNGEON_LOOT)) {
                components.add(position, AetherItems.SILVER_DUNGEON_TOOLTIP);
            }
            if (stack.is(AetherTags.Items.GOLD_DUNGEON_LOOT)) {
                components.add(position, AetherItems.GOLD_DUNGEON_TOOLTIP);
            }
        });
    }

    private static void registerScreenCallbacks() {
        ScreenEvents.AFTER_INIT.register(ClientScreenHooks::afterInit);
    }

    private static void registerTickCallbacks() {
        ClientTickEvents.END_CLIENT_TICK.register(ClientTickHooks::endClientTick);
    }

    private static void registerConnectionCallbacks() {
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            ClientMusicHooks.stop();
            ToolAbilityHooks.resetDebuffToolsState();
        });
    }

    private static void registerLevelRenderCallbacks() {
        LevelRenderEvents.COLLECT_SUBMITS.register(context -> {
            Minecraft minecraft = Minecraft.getInstance();
            DungeonOverlayClientHooks.renderDungeonBlockOverlays(context.poseStack(), context.submitNodeCollector(), minecraft.gameRenderer.mainCamera(), context.levelState().cameraRenderState.cullFrustum, minecraft);
        });
    }

    /**
     * Used to work around a classloading crash on the server.
     */
    public static void setToSunAltarScreen(Component name, int timeScale) {
        ClientAccess.setScreen(Minecraft.getInstance(), new SunAltarScreen(name, timeScale));
    }
}
