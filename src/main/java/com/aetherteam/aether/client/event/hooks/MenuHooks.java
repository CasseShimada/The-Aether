package com.aetherteam.aether.client.event.hooks;

import com.aetherteam.aether.mixin.mixins.client.accessor.SplashRendererAccessor;
import com.aetherteam.aether.mixin.mixins.client.accessor.TitleScreenAccessor;
import net.minecraft.client.gui.components.SplashRenderer;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;

import java.util.Calendar;

public class MenuHooks {
    /**
     * If the current date is July 22nd, displays the Aether's anniversary splash text.
     */
    public static void setCustomSplashText(TitleScreen screen) {
        Calendar calendar = Calendar.getInstance();
        if (calendar.get(Calendar.MONTH) + 1 == 7 && calendar.get(Calendar.DATE) == 22) {
            TitleScreenAccessor accessor = (TitleScreenAccessor) screen;
            SplashRenderer splashRenderer = accessor.aether$getSplash();
            Component splash = ((SplashRendererAccessor) splashRenderer).aether$getSplash();
            if (!"Happy anniversary to the Aether!".equals(splash.getString())) {
                accessor.aether$setSplash(new SplashRenderer(Component.literal("Happy anniversary to the Aether!")));
            }
        }
    }
}
