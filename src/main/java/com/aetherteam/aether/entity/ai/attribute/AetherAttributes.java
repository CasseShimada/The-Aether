package com.aetherteam.aether.entity.ai.attribute;

import com.aetherteam.aether.Aether;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;

public final class AetherAttributes {
    public static final Attribute MOA_MAX_JUMPS = Registry.register(
            BuiltInRegistries.ATTRIBUTE,
            Identifier.fromNamespaceAndPath(Aether.MODID, "moa_max_jumps"),
            new RangedAttribute("aether.attribute.name.moa_max_jumps", -1.0, -1.0, 1024.0).setSyncable(true));

    private AetherAttributes() {
    }

    public static void bootstrap() {
    }
}
