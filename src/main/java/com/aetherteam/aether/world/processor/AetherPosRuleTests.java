package com.aetherteam.aether.world.processor;

import com.aetherteam.aether.Aether;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.structure.templatesystem.PosRuleTestType;

public class AetherPosRuleTests {
    public static final PosRuleTestType<BorderBoxPosTest> BORDER_BOX = register("border_box", () -> BorderBoxPosTest.CODEC);

    private static <T extends PosRuleTestType<?>> T register(String name, T type) {
        return Registry.register(BuiltInRegistries.POS_RULE_TEST, Identifier.fromNamespaceAndPath(Aether.MODID, name), type);
    }
}
