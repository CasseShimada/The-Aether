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

public final class AetherStructurePieceTypes {
    public static final StructurePieceType LARGE_AERCLOUD = Registry.register(
            BuiltInRegistries.STRUCTURE_PIECE,
            Identifier.fromNamespaceAndPath(Aether.MODID, "alc"),
            LargeAercloudChunk::new);
    public static final StructurePieceType BRONZE_BOSS_ROOM = Registry.register(
            BuiltInRegistries.STRUCTURE_PIECE,
            Identifier.fromNamespaceAndPath(Aether.MODID, "bbossroom"),
            BronzeBossRoom::new);
    public static final StructurePieceType BRONZE_DUNGEON_ROOM = Registry.register(
            BuiltInRegistries.STRUCTURE_PIECE,
            Identifier.fromNamespaceAndPath(Aether.MODID, "bdungeonroom"),
            BronzeDungeonRoom::new);
    public static final StructurePieceType BRONZE_TUNNEL = Registry.register(
            BuiltInRegistries.STRUCTURE_PIECE,
            Identifier.fromNamespaceAndPath(Aether.MODID, "btunnel"),
            BronzeTunnel::new);
    public static final StructurePieceType BRONZE_SURFACE_RUINS = Registry.register(
            BuiltInRegistries.STRUCTURE_PIECE,
            Identifier.fromNamespaceAndPath(Aether.MODID, "bsurface"),
            BronzeDungeonSurfaceRuins::new);
    public static final StructurePieceType SILVER_TEMPLE_PIECE = Registry.register(
            BuiltInRegistries.STRUCTURE_PIECE,
            Identifier.fromNamespaceAndPath(Aether.MODID, "stemplepiece"),
            SilverTemplePiece::new);
    public static final StructurePieceType SILVER_FLOOR_PIECE = Registry.register(
            BuiltInRegistries.STRUCTURE_PIECE,
            Identifier.fromNamespaceAndPath(Aether.MODID, "sfloorpiece"),
            SilverFloorPiece::new);
    public static final StructurePieceType SILVER_DUNGEON_ROOM = Registry.register(
            BuiltInRegistries.STRUCTURE_PIECE,
            Identifier.fromNamespaceAndPath(Aether.MODID, "sdungeonroom"),
            SilverDungeonRoom::new);
    public static final StructurePieceType SILVER_BOSS_ROOM = Registry.register(
            BuiltInRegistries.STRUCTURE_PIECE,
            Identifier.fromNamespaceAndPath(Aether.MODID, "sbossroom"),
            SilverBossRoom::new);
    public static final StructurePieceType GOLD_BOSS_ROOM = Registry.register(
            BuiltInRegistries.STRUCTURE_PIECE,
            Identifier.fromNamespaceAndPath(Aether.MODID, "gbossroom"),
            GoldBossRoom::new);
    public static final StructurePieceType GOLD_ISLAND = Registry.register(
            BuiltInRegistries.STRUCTURE_PIECE,
            Identifier.fromNamespaceAndPath(Aether.MODID, "gisland"),
            GoldIsland::new);
    public static final StructurePieceType GOLD_STUB = Registry.register(
            BuiltInRegistries.STRUCTURE_PIECE,
            Identifier.fromNamespaceAndPath(Aether.MODID, "gstub"),
            GoldStub::new);
    public static final StructurePieceType GOLD_TUNNEL = Registry.register(
            BuiltInRegistries.STRUCTURE_PIECE,
            Identifier.fromNamespaceAndPath(Aether.MODID, "gtunnel"),
            GoldTunnel::new);
    public static final StructurePieceType GUMDROP_CAVE = Registry.register(
            BuiltInRegistries.STRUCTURE_PIECE,
            Identifier.fromNamespaceAndPath(Aether.MODID, "gumdropcave"),
            GoldStubCave::new);
    public static final StructurePieceType RUINED_PORTAL = Registry.register(
            BuiltInRegistries.STRUCTURE_PIECE,
            Identifier.fromNamespaceAndPath(Aether.MODID, "glowstoneruins"),
            GlowstoneRuinedPortalPiece::new);

    private AetherStructurePieceTypes() {
    }

    public static void bootstrap() {
    }
}
