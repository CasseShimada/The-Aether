package com.aetherteam.aether.world.treedecorator;

import com.aetherteam.aether.Aether;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;

public class AetherTreeDecoratorTypes {
    public static final TreeDecoratorType<HolidayTreeDecorator> HOLIDAY_TREE_DECORATOR = register("holiday_tree_decorator", new TreeDecoratorType<>(HolidayTreeDecorator.CODEC));

    private static <T extends TreeDecoratorType<?>> T register(String name, T type) {
        return Registry.register(BuiltInRegistries.TREE_DECORATOR_TYPE, Identifier.fromNamespaceAndPath(Aether.MODID, name), type);
    }
}
