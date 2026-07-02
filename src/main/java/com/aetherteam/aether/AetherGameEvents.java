package com.aetherteam.aether;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.gameevent.GameEvent;

public class AetherGameEvents {
    public static final Identifier ICESTONE_FREEZABLE_UPDATE_ID = Identifier.fromNamespaceAndPath(Aether.MODID, "icestone_freezable_update");

    public static final GameEvent ICESTONE_FREEZABLE_UPDATE = Registry.register(
            BuiltInRegistries.GAME_EVENT,
            ICESTONE_FREEZABLE_UPDATE_ID,
            new GameEvent(4));
}
