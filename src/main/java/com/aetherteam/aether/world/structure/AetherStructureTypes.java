package com.aetherteam.aether.world.structure;

import com.aetherteam.aether.Aether;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.structure.StructureType;

public class AetherStructureTypes {
    public static final StructureType<LargeAercloudStructure> LARGE_AERCLOUD = register("large_aercloud", () -> LargeAercloudStructure.CODEC);
    public static final StructureType<BronzeDungeonStructure> BRONZE_DUNGEON = register("bronze_dungeon", () -> BronzeDungeonStructure.CODEC);
    public static final StructureType<SilverDungeonStructure> SILVER_DUNGEON = register("silver_dungeon", () -> SilverDungeonStructure.CODEC);
    public static final StructureType<GoldDungeonStructure> GOLD_DUNGEON = register("gold_dungeon", () -> GoldDungeonStructure.CODEC);
    public static final StructureType<GlowstoneRuinedPortalStructure> RUINED_PORTAL = register("ruined_portal", () -> GlowstoneRuinedPortalStructure.CODEC);

    private static <T extends StructureType<?>> T register(String name, T type) {
        return Registry.register(BuiltInRegistries.STRUCTURE_TYPE, Identifier.fromNamespaceAndPath(Aether.MODID, name), type);
    }
}
