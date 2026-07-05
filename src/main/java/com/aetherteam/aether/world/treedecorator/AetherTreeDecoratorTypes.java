package com.aetherteam.aether.world.treedecorator;

import com.aetherteam.aether.Aether;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;

public final class AetherTreeDecoratorTypes {
    public static final TreeDecoratorType<HolidayTreeDecorator> HOLIDAY_TREE_DECORATOR = Registry.register(
            BuiltInRegistries.TREE_DECORATOR_TYPE,
            Identifier.fromNamespaceAndPath(Aether.MODID, "holiday_tree_decorator"),
            new TreeDecoratorType<>(HolidayTreeDecorator.CODEC));

    private AetherTreeDecoratorTypes() {
    }

    public static void bootstrap() {
    }
}
