package com.aetherteam.aether.world.structurepiece;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.world.structurepiece.bronzedungeon.BronzeBossRoom;
import com.aetherteam.aether.world.structurepiece.bronzedungeon.BronzeDungeonRoom;
import com.aetherteam.aether.world.structurepiece.bronzedungeon.BronzeDungeonSurfaceRuins;
import com.aetherteam.aether.world.structurepiece.bronzedungeon.BronzeTunnel;
import com.aetherteam.aether.world.structurepiece.golddungeon.*;
import com.aetherteam.aether.world.structurepiece.silverdungeon.SilverBossRoom;
import com.aetherteam.aether.world.structurepiece.silverdungeon.SilverDungeonRoom;
import com.aetherteam.aether.world.structurepiece.silverdungeon.SilverFloorPiece;
import com.aetherteam.aether.world.structurepiece.silverdungeon.SilverTemplePiece;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;

import java.util.Locale;

public final class AetherStructurePieceTypes {
    public static final StructurePieceType LARGE_AERCLOUD = register("ALC", LargeAercloudChunk::new);
    public static final StructurePieceType BRONZE_BOSS_ROOM = register("BBossRoom", BronzeBossRoom::new);
    public static final StructurePieceType BRONZE_DUNGEON_ROOM = register("BDungeonRoom", BronzeDungeonRoom::new);
    public static final StructurePieceType BRONZE_TUNNEL = register("BTunnel", BronzeTunnel::new);
    public static final StructurePieceType BRONZE_SURFACE_RUINS = register("BSurface", BronzeDungeonSurfaceRuins::new);
    public static final StructurePieceType SILVER_TEMPLE_PIECE = register("STemplePiece", SilverTemplePiece::new);
    public static final StructurePieceType SILVER_FLOOR_PIECE = register("SFloorPiece", SilverFloorPiece::new);
    public static final StructurePieceType SILVER_DUNGEON_ROOM = register("SDungeonRoom", SilverDungeonRoom::new);
    public static final StructurePieceType SILVER_BOSS_ROOM = register("SBossRoom", SilverBossRoom::new);
    public static final StructurePieceType GOLD_BOSS_ROOM = register("GBossRoom", GoldBossRoom::new);
    public static final StructurePieceType GOLD_ISLAND = register("GIsland", GoldIsland::new);
    public static final StructurePieceType GOLD_STUB = register("GStub", GoldStub::new);
    public static final StructurePieceType GOLD_TUNNEL = register("GTunnel", GoldTunnel::new);
    public static final StructurePieceType GUMDROP_CAVE = register("GumdropCave", GoldStubCave::new);
    public static final StructurePieceType RUINED_PORTAL = register("GlowstoneRuins", GlowstoneRuinedPortalPiece::new);

    private static StructurePieceType register(String name, StructurePieceType structurePieceType) {
        return Registry.register(BuiltInRegistries.STRUCTURE_PIECE, Identifier.fromNamespaceAndPath(Aether.MODID, name.toLowerCase(Locale.ROOT)), structurePieceType);
    }

    private AetherStructurePieceTypes() {
    }

    public static void bootstrap() {
    }
}
