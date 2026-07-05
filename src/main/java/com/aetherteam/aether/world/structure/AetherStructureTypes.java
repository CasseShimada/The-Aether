package com.aetherteam.aether.world.structure;

import com.aetherteam.aether.Aether;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.structure.StructureType;

public final class AetherStructureTypes {
    public static final StructureType<LargeAercloudStructure> LARGE_AERCLOUD = Registry.register(
            BuiltInRegistries.STRUCTURE_TYPE,
            Identifier.fromNamespaceAndPath(Aether.MODID, "large_aercloud"),
            () -> LargeAercloudStructure.CODEC);
    public static final StructureType<BronzeDungeonStructure> BRONZE_DUNGEON = Registry.register(
            BuiltInRegistries.STRUCTURE_TYPE,
            Identifier.fromNamespaceAndPath(Aether.MODID, "bronze_dungeon"),
            () -> BronzeDungeonStructure.CODEC);
    public static final StructureType<SilverDungeonStructure> SILVER_DUNGEON = Registry.register(
            BuiltInRegistries.STRUCTURE_TYPE,
            Identifier.fromNamespaceAndPath(Aether.MODID, "silver_dungeon"),
            () -> SilverDungeonStructure.CODEC);
    public static final StructureType<GoldDungeonStructure> GOLD_DUNGEON = Registry.register(
            BuiltInRegistries.STRUCTURE_TYPE,
            Identifier.fromNamespaceAndPath(Aether.MODID, "gold_dungeon"),
            () -> GoldDungeonStructure.CODEC);
    public static final StructureType<GlowstoneRuinedPortalStructure> RUINED_PORTAL = Registry.register(
            BuiltInRegistries.STRUCTURE_TYPE,
            Identifier.fromNamespaceAndPath(Aether.MODID, "ruined_portal"),
            () -> GlowstoneRuinedPortalStructure.CODEC);

    private AetherStructureTypes() {
    }

    public static void bootstrap() {
    }
}
