package com.aetherteam.aether.advancement;

import com.aetherteam.aether.Aether;
import net.minecraft.advancements.triggers.CriterionTrigger;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

public class AetherAdvancementTriggers {
    public static final IncubationTrigger INCUBATION_TRIGGER = register("incubation_trigger", new IncubationTrigger());
    public static final LoreTrigger LORE_ENTRY = register("lore_entry", new LoreTrigger());

    private static <T extends CriterionTrigger<?>> T register(String name, T trigger) {
        return Registry.register(BuiltInRegistries.TRIGGER_TYPES, Identifier.fromNamespaceAndPath(Aether.MODID, name), trigger);
    }
}
