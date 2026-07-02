package com.aetherteam.aether.client.event.hooks;

import com.aetherteam.aether.AetherConfig;
import com.aetherteam.aether.client.gui.component.inventory.AccessoryButton;
import com.aetherteam.aether.client.gui.component.inventory.ScreenOffset;
import com.aetherteam.aether.entity.AetherBossMob;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.BossEvent;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.UUID;

public class GuiHooks {
    /**
     * Set of UUIDs of boss bars that belong to Aether bosses.
     */
    public static final Map<UUID, Integer> BOSS_EVENTS = GuiBossBarHooks.BOSS_EVENTS;

    /**
     * Checks whether the accessory button isn't disabled by {@link AetherConfig.Client#disable_accessory_button} or accessory tags being empty.
     *
     * @return The {@link Boolean} value.
     */
    public static boolean isAccessoryButtonEnabled() {
        return !AetherConfig.CLIENT.disable_accessory_button.get() && !AetherConfig.COMMON.use_default_accessories_menu.get();
    }

    /**
     * Creates an {@link AccessoryButton} if one can be created for the screen according to {@link GuiHooks#canCreateAccessoryButtonForScreen(Screen)}.
     *
     * @param screen  The parent {@link Screen}.
     * @param offsets A {@link ScreenOffset} containing the x and y offsets.
     * @return The {@link AccessoryButton}.
     */
    @Nullable
    public static AccessoryButton setupAccessoryButton(Screen screen, ScreenOffset offsets) {
        return GuiAccessoryMenuHooks.setupAccessoryButton(screen, offsets);
    }

    /**
     * Sets up the buttons for the {@link MoaSkinsScreen} and the {@link AetherCustomizationsScreen} in a {@link GridLayout}.
     *
     * @param screen The parent {@link Screen}.
     * @return The {@link GridLayout} holding the buttons.
     */
    @Nullable
    public static GridLayout setupPerksButtons(Screen screen) {
        return GuiPerkScreenHooks.setupPerksButtons(screen);
    }

    /**
     * Generates and draws the Aether's trivia lines in various loading screens.
     *
     * @param screen      The current {@link Screen}.
     * @param guiGraphics The rendering {@link GuiGraphicsExtractor}.
     */
    public static void drawTrivia(Screen screen, GuiGraphicsExtractor guiGraphics) {
        GuiTriviaHooks.drawTrivia(screen, guiGraphics);
    }

    /**
     * Draws text for leaving and entering the Aether.
     * Checks for when to display different text are handled by {@link DimensionHooks}.
     *
     * @param screen      The current {@link Screen}.
     * @param guiGraphics The rendering {@link GuiGraphicsExtractor}.
     */
    public static void drawAetherTravelMessage(Screen screen, GuiGraphicsExtractor guiGraphics) {
        GuiTriviaHooks.drawAetherTravelMessage(screen, guiGraphics);
    }

    /**
     * Handles the time until the Patreon {@link RefreshButton} can be clicked again.
     */
    public static void handlePatreonRefreshRebound() {
        GuiPerkScreenHooks.handlePatreonRefreshRebound();
    }

    /**
     * Handles opening the {@link AetherAccessoriesMenu} when clicking the {@link AetherKeys#OPEN_ACCESSORY_INVENTORY} keybind.
     */
    public static void openAccessoryMenu() {
        GuiAccessoryMenuHooks.openAccessoryMenu();
    }

    /**
     * Allows various menus to be closed with the {@link AetherKeys#OPEN_ACCESSORY_INVENTORY} keybind.
     *
     * @param key    The {@link Integer} ID for the key.
     * @param action The {@link Integer} for the key action.
     */
    public static void closeContainerMenu(int key, int action) {
        GuiAccessoryMenuHooks.closeContainerMenu(key, action);
    }

    /**
     * [CODE COPY] - {@link net.minecraft.client.gui.components.BossHealthOverlay#render(GuiGraphicsExtractor)}
     * Modified to draw the Aether's custom boss health bars.
     */
    public static void drawBossHealthBar(GuiGraphicsExtractor guiGraphics, int x, int y, LerpingBossEvent bossEvent) {
        GuiBossBarHooks.drawBossHealthBar(guiGraphics, x, y, bossEvent);
    }

    /**
     * [CODE COPY] - {@link net.minecraft.client.gui.components.BossHealthOverlay#drawBar(GuiGraphicsExtractor, int, int, BossEvent)}
     * This version of the method doesn't account for other types of boss bars because the Aether only has one.
     */
    public static void drawBar(GuiGraphicsExtractor guiGraphics, int x, int y, BossEvent bossEvent, AetherBossMob<?> aetherBossMob) {
        GuiBossBarHooks.drawBar(guiGraphics, x, y, bossEvent, aetherBossMob);
    }

    /**
     * Checks whether a boss bar belongs to an Aether boss, as determined by {@link GuiHooks#BOSS_EVENTS}.
     *
     * @param uuid The boss {@link UUID}.
     * @return The {@link Boolean} value.
     */
    public static boolean isAetherBossBar(UUID uuid) {
        return BOSS_EVENTS.containsKey(uuid);
    }
}
