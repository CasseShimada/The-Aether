package com.aetherteam.aether.world.processor;

import com.aetherteam.aether.Aether;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.structure.templatesystem.PosRuleTestType;

public final class AetherPosRuleTests {
    public static final PosRuleTestType<BorderBoxPosTest> BORDER_BOX = Registry.register(
            BuiltInRegistries.POS_RULE_TEST,
            Identifier.fromNamespaceAndPath(Aether.MODID, "border_box"),
            () -> BorderBoxPosTest.CODEC);

    private AetherPosRuleTests() {
    }

    public static void bootstrap() {
    }
}
