package com.aetherteam.aether.advancement;

import com.aetherteam.aether.Aether;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

public final class AetherAdvancementTriggers {
    public static final IncubationTrigger INCUBATION_TRIGGER = Registry.register(
            BuiltInRegistries.TRIGGER_TYPES,
            Identifier.fromNamespaceAndPath(Aether.MODID, "incubation_trigger"),
            new IncubationTrigger());
    public static final LoreTrigger LORE_ENTRY = Registry.register(
            BuiltInRegistries.TRIGGER_TYPES,
            Identifier.fromNamespaceAndPath(Aether.MODID, "lore_entry"),
            new LoreTrigger());

    private AetherAdvancementTriggers() {
    }

    public static void bootstrap() {
    }
}
