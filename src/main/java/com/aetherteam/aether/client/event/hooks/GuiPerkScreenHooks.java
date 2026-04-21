package com.aetherteam.aether.client.event.hooks;

import com.aetherteam.aether.AetherConfig;
import com.aetherteam.aether.client.gui.component.skins.RefreshButton;
import com.aetherteam.aether.client.gui.screen.inventory.AetherAccessoriesScreen;
import com.aetherteam.aether.client.gui.screen.perks.AetherCustomizationsScreen;
import com.aetherteam.aether.client.gui.screen.perks.MoaSkinsScreen;
import com.aetherteam.aether.perk.PerkUtil;
import com.aetherteam.nitrogen.api.users.User;
import com.aetherteam.nitrogen.api.users.UserData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;

final class GuiPerkScreenHooks {
    private GuiPerkScreenHooks() {
    }

    @Nullable
    static GridLayout setupPerksButtons(Screen screen) {
        if (!(screen instanceof PauseScreen)) {
            return null;
        }

        User user = UserData.Client.getClientUser();
        int x = AetherConfig.CLIENT.layout_perks_x.get();
        int y = AetherConfig.CLIENT.layout_perks_y.get();
        GridLayout gridLayout = new GridLayout();
        gridLayout.defaultCellSetting().padding(58, 4, 4, 0);
        GridLayout.RowHelper rowHelper = gridLayout.createRowHelper(1);

        if (user == null) {
            y -= 12;
        } else {
            if (shouldShowSkinsButton(user)) {
                createSkinsButton(screen, rowHelper);
            } else {
                y -= 6;
            }
            if (shouldShowCustomizationsButton(user)) {
                createCustomizationsButton(screen, rowHelper);
            } else {
                y -= 6;
            }
        }

        gridLayout.arrangeElements();
        FrameLayout.alignInRectangle(gridLayout, x, y, screen.width, screen.height, 0.5F, 0.25F);
        return gridLayout;
    }

    static void handlePatreonRefreshRebound() {
        if (RefreshButton.reboundTimer > 0) {
            RefreshButton.reboundTimer--;
        }
        if (RefreshButton.reboundTimer < 0) {
            RefreshButton.reboundTimer = 0;
        }
    }

    private static boolean shouldShowSkinsButton(User user) {
        return !AetherConfig.CLIENT.disable_skins_button.get() || PerkUtil.hasAnyMoaSkins().test(user);
    }

    private static boolean shouldShowCustomizationsButton(User user) {
        return PerkUtil.hasDeveloperGlow().test(user) || PerkUtil.hasHalo().test(user);
    }

    private static void createSkinsButton(Screen screen, GridLayout.RowHelper rowHelper) {
        ImageButton skinsButton = new ImageButton(0, 0, 20, 20, AetherAccessoriesScreen.SKINS_BUTTON,
                pressed -> Minecraft.getInstance().setScreen(new MoaSkinsScreen(screen)),
                Component.translatable("gui.aether.accessories.skins_button"));
        skinsButton.setTooltip(Tooltip.create(Component.translatable("gui.aether.accessories.skins_button")));
        rowHelper.addChild(skinsButton);
    }

    private static void createCustomizationsButton(Screen screen, GridLayout.RowHelper rowHelper) {
        ImageButton customizationButton = new ImageButton(0, 0, 20, 20, AetherAccessoriesScreen.CUSTOMIZATION_BUTTON,
                pressed -> Minecraft.getInstance().setScreen(new AetherCustomizationsScreen(screen)),
                Component.translatable("gui.aether.accessories.customization_button"));
        customizationButton.setTooltip(Tooltip.create(Component.translatable("gui.aether.accessories.customization_button")));
        rowHelper.addChild(customizationButton);
    }
}
