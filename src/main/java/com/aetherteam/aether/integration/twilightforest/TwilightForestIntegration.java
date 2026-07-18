package com.aetherteam.aether.integration.twilightforest;

import com.aetherteam.aether.Aether;
import net.fabricmc.loader.api.FabricLoader;

/** Loads the optional compatibility target after Mixin preparation without linking TF at compile time. */
public final class TwilightForestIntegration {
    private static final String ITEM_STACK_UTILS = "twilightforest.util.TFItemStackUtils";
    private static boolean initialized;

    private TwilightForestIntegration() {
    }

    public static void init() {
        if (initialized || !FabricLoader.getInstance().isModLoaded("twilightforest")) {
            return;
        }
        initialized = true;
        try {
            Class.forName(ITEM_STACK_UTILS, false, TwilightForestIntegration.class.getClassLoader());
        } catch (ClassNotFoundException | LinkageError exception) {
            Aether.LOGGER.error("Twilight Forest TFItemStackUtils is unavailable; Aether charm-slot consumption compatibility is disabled", exception);
        }
    }
}
