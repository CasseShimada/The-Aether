package com.aetherteam.aether.block;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.block.construction.*;
import com.aetherteam.aether.block.dungeon.*;
import com.aetherteam.aether.block.miscellaneous.AetherFrostedIceBlock;
import com.aetherteam.aether.block.miscellaneous.FacingPillarBlock;
import com.aetherteam.aether.block.miscellaneous.FloatingBlock;
import com.aetherteam.aether.block.miscellaneous.UnstableObsidianBlock;
import com.aetherteam.aether.block.natural.*;
import com.aetherteam.aether.block.portal.AetherPortalBlock;
import com.aetherteam.aether.block.utility.*;
import com.aetherteam.aether.blockentity.ChestMimicBlockEntity;
import com.aetherteam.aether.blockentity.TreasureChestBlockEntity;
import com.aetherteam.aether.client.particle.AetherParticleTypes;
import com.aetherteam.aether.effect.AetherEffects;
import com.aetherteam.aether.entity.AetherEntityTypes;
import com.aetherteam.aether.mixin.mixins.common.accessor.FireBlockAccessor;
import com.aetherteam.aether.registry.RegistryConstructionContext;
import com.aetherteam.aether.world.treegrower.AetherTreeGrowers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.item.*;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class AetherBlocks {
    private static final List<BlockItemRegistration> BLOCK_ITEMS = new ArrayList<>();

    public static final AetherPortalBlock AETHER_PORTAL = registerBlockOnly("aether_portal", () -> new AetherPortalBlock(Block.Properties.of().noCollision().randomTicks().strength(-1.0F).sound(SoundType.GLASS).lightLevel(AetherBlocks::lightLevel11).pushReaction(PushReaction.BLOCK).forceSolidOn()));

    public static final Block AETHER_GRASS_BLOCK = registerKeyed("aether_grass_block", key -> new AetherGrassBlock(Block.Properties.of().mapColor(MapColor.WARPED_WART_BLOCK).randomTicks().strength(0.2F).sound(SoundType.GRASS).setId(key)));
    public static final Block ENCHANTED_AETHER_GRASS_BLOCK = registerKeyed("enchanted_aether_grass_block", key -> new EnchantedAetherGrassBlock(Block.Properties.of().mapColor(MapColor.GOLD).randomTicks().strength(0.2F).sound(SoundType.GRASS).setId(key)));
    public static final Block AETHER_DIRT = registerKeyed("aether_dirt", key -> new AetherDoubleDropBlock(Block.Properties.of().mapColor(MapColor.TERRACOTTA_CYAN).strength(0.2F).sound(SoundType.GRAVEL).setId(key)));
    public static final Block QUICKSOIL = registerKeyed("quicksoil", key -> new QuicksoilBlock(Block.Properties.of().mapColor(MapColor.COLOR_YELLOW).instrument(NoteBlockInstrument.SNARE).strength(0.5F).friction(1.1F).sound(SoundType.SAND).setId(key)));
    public static final Block HOLYSTONE = registerKeyed("holystone", key -> new AetherDoubleDropBlock(Block.Properties.of().mapColor(MapColor.WOOL).instrument(NoteBlockInstrument.BASEDRUM).strength(0.5F).requiresCorrectToolForDrops().setId(key)));
    public static final Block MOSSY_HOLYSTONE = registerKeyed("mossy_holystone", key -> new AetherDoubleDropBlock(Block.Properties.ofFullCopy(AetherBlocks.HOLYSTONE).setId(key)));
    public static final Block AETHER_FARMLAND = registerKeyed("aether_farmland", key -> new AetherFarmBlock(Block.Properties.of().mapColor(MapColor.TERRACOTTA_CYAN).randomTicks().strength(0.2F).sound(SoundType.GRAVEL).isViewBlocking(AetherBlocks::always).isSuffocating(AetherBlocks::always).setId(key)));
    public static final Block AETHER_DIRT_PATH = registerKeyed("aether_dirt_path", key -> new AetherDirtPathBlock(Block.Properties.of().mapColor(MapColor.TERRACOTTA_CYAN).strength(0.2F).sound(SoundType.GRASS).isViewBlocking(AetherBlocks::always).isSuffocating(AetherBlocks::always).setId(key)));

    public static final Block COLD_AERCLOUD = registerKeyed("cold_aercloud", key -> new AercloudBlock(Block.Properties.of().mapColor(MapColor.SNOW).instrument(NoteBlockInstrument.FLUTE).strength(0.3F).sound(SoundType.WOOL).noOcclusion().dynamicShape().isRedstoneConductor(AetherBlocks::never).isSuffocating(AetherBlocks::never).isViewBlocking(AetherBlocks::never).setId(key)));
    public static final Block BLUE_AERCLOUD = registerKeyed("blue_aercloud", key -> new BlueAercloudBlock(Block.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).instrument(NoteBlockInstrument.FLUTE).strength(0.3F).sound(SoundType.WOOL).noOcclusion().dynamicShape().isRedstoneConductor(AetherBlocks::never).isSuffocating(AetherBlocks::never).isViewBlocking(AetherBlocks::never).setId(key)));
    public static final Block GOLDEN_AERCLOUD = registerKeyed("golden_aercloud", key -> new AercloudBlock(Block.Properties.of().mapColor(MapColor.COLOR_YELLOW).instrument(NoteBlockInstrument.FLUTE).strength(0.3F).sound(SoundType.WOOL).noOcclusion().dynamicShape().isRedstoneConductor(AetherBlocks::never).isSuffocating(AetherBlocks::never).isViewBlocking(AetherBlocks::never).setId(key)));

    public static final Block ICESTONE = registerKeyed("icestone", key -> new IcestoneBlock(Block.Properties.of().mapColor(MapColor.ICE).instrument(NoteBlockInstrument.CHIME).strength(0.5F).randomTicks().sound(SoundType.GLASS).requiresCorrectToolForDrops().setId(key)));
    public static final Block AMBROSIUM_ORE = registerKeyed("ambrosium_ore", key -> new AetherDoubleDropsOreBlock(UniformInt.of(0, 2), Block.Properties.of().mapColor(MapColor.WOOL).instrument(NoteBlockInstrument.BASEDRUM).strength(3.0F).requiresCorrectToolForDrops().setId(key)));
    public static final Block ZANITE_ORE = registerKeyed("zanite_ore", key -> new DropExperienceBlock(UniformInt.of(3, 5), Block.Properties.of().mapColor(MapColor.WOOL).instrument(NoteBlockInstrument.BASEDRUM).strength(3.0F).requiresCorrectToolForDrops().setId(key)));
    public static final Block GRAVITITE_ORE = registerKeyed("gravitite_ore", key -> new FloatingBlock(false, Block.Properties.of().mapColor(MapColor.WOOL).instrument(NoteBlockInstrument.BASEDRUM).strength(3.0F).randomTicks().requiresCorrectToolForDrops().setId(key)));

    public static final Block SKYROOT_LEAVES = registerKeyed("skyroot_leaves", key -> new AetherDoubleDropsLeaves(Block.Properties.of().mapColor(MapColor.GRASS).ignitedByLava().pushReaction(PushReaction.DESTROY).strength(0.2F).randomTicks().sound(SoundType.GRASS).noOcclusion().isValidSpawn(AetherBlocks::ocelotOrParrot).isRedstoneConductor(AetherBlocks::never).isSuffocating(AetherBlocks::never).isViewBlocking(AetherBlocks::never).setId(key)));
    public static final Block GOLDEN_OAK_LEAVES = registerKeyed("golden_oak_leaves", key -> new LeavesWithParticlesBlock(() -> AetherParticleTypes.GOLDEN_OAK_LEAVES, Block.Properties.of().mapColor(MapColor.GOLD).ignitedByLava().pushReaction(PushReaction.DESTROY).strength(0.2F).randomTicks().sound(SoundType.GRASS).noOcclusion().isValidSpawn(AetherBlocks::ocelotOrParrot).isRedstoneConductor(AetherBlocks::never).isSuffocating(AetherBlocks::never).isViewBlocking(AetherBlocks::never).setId(key)));
    public static final Block CRYSTAL_LEAVES = registerKeyed("crystal_leaves", key -> new LeavesWithParticlesBlock(() -> AetherParticleTypes.CRYSTAL_LEAVES, Block.Properties.of().mapColor(MapColor.DIAMOND).ignitedByLava().pushReaction(PushReaction.DESTROY).strength(0.2F).randomTicks().sound(SoundType.GRASS).noOcclusion().isValidSpawn(AetherBlocks::ocelotOrParrot).isRedstoneConductor(AetherBlocks::never).isSuffocating(AetherBlocks::never).isViewBlocking(AetherBlocks::never).setId(key)));
    public static final Block CRYSTAL_FRUIT_LEAVES = registerKeyed("crystal_fruit_leaves", key -> new CrystalFruitLeavesBlock(() -> AetherParticleTypes.CRYSTAL_LEAVES, Block.Properties.of().mapColor(MapColor.DIAMOND).ignitedByLava().pushReaction(PushReaction.DESTROY).strength(0.2F).randomTicks().sound(SoundType.GRASS).noOcclusion().isValidSpawn(AetherBlocks::ocelotOrParrot).isRedstoneConductor(AetherBlocks::never).isSuffocating(AetherBlocks::never).isViewBlocking(AetherBlocks::never).setId(key)));
    public static final Block HOLIDAY_LEAVES = registerKeyed("holiday_leaves", key -> new LeavesWithParticlesBlock(() -> AetherParticleTypes.HOLIDAY_LEAVES, Block.Properties.of().mapColor(MapColor.COLOR_PURPLE).ignitedByLava().pushReaction(PushReaction.DESTROY).strength(0.2F).randomTicks().sound(SoundType.GRASS).noOcclusion().isValidSpawn(AetherBlocks::ocelotOrParrot).isRedstoneConductor(AetherBlocks::never).isSuffocating(AetherBlocks::never).isViewBlocking(AetherBlocks::never).setId(key)));
    public static final Block DECORATED_HOLIDAY_LEAVES = registerKeyed("decorated_holiday_leaves", key -> new LeavesWithParticlesBlock(() -> AetherParticleTypes.HOLIDAY_LEAVES, Block.Properties.of().mapColor(MapColor.COLOR_PURPLE).ignitedByLava().pushReaction(PushReaction.DESTROY).strength(0.2F).randomTicks().sound(SoundType.GRASS).noOcclusion().isValidSpawn(AetherBlocks::ocelotOrParrot).isRedstoneConductor(AetherBlocks::never).isSuffocating(AetherBlocks::never).isViewBlocking(AetherBlocks::never).setId(key)));

    public static final RotatedPillarBlock SKYROOT_LOG = registerKeyed("skyroot_log", key -> new AetherLogBlock(Block.Properties.ofFullCopy(Blocks.OAK_LOG).setId(key)));
    public static final RotatedPillarBlock GOLDEN_OAK_LOG = registerKeyed("golden_oak_log", key -> new AetherLogBlock(Block.Properties.ofFullCopy(Blocks.OAK_LOG).setId(key)));
    public static final RotatedPillarBlock STRIPPED_SKYROOT_LOG = registerKeyed("stripped_skyroot_log", key -> new RotatedPillarBlock(Block.Properties.ofFullCopy(Blocks.STRIPPED_OAK_LOG).setId(key)));
    public static final RotatedPillarBlock SKYROOT_WOOD = registerKeyed("skyroot_wood", key -> new AetherLogBlock(Block.Properties.ofFullCopy(Blocks.OAK_WOOD).setId(key)));
    public static final RotatedPillarBlock GOLDEN_OAK_WOOD = registerKeyed("golden_oak_wood", key -> new AetherLogBlock(Block.Properties.ofFullCopy(Blocks.OAK_WOOD).setId(key)));
    public static final RotatedPillarBlock STRIPPED_SKYROOT_WOOD = registerKeyed("stripped_skyroot_wood", key -> new RotatedPillarBlock(Block.Properties.ofFullCopy(Blocks.STRIPPED_OAK_WOOD).setId(key)));

    public static final Block SKYROOT_PLANKS = registerKeyed("skyroot_planks", key -> new Block(Block.Properties.ofFullCopy(Blocks.OAK_PLANKS).setId(key)));
    public static final Block HOLYSTONE_BRICKS = registerKeyed("holystone_bricks", key -> new Block(Block.Properties.of().mapColor(MapColor.WOOL).instrument(NoteBlockInstrument.BASEDRUM).strength(2.0F, 6.0F).requiresCorrectToolForDrops().setId(key)));
    public static final TransparentBlock QUICKSOIL_GLASS = registerKeyed("quicksoil_glass", key -> new QuicksoilGlassBlock(Block.Properties.of().mapColor(MapColor.COLOR_YELLOW).instrument(NoteBlockInstrument.HAT).strength(0.2F).friction(1.1F).lightLevel(AetherBlocks::lightLevel11).sound(SoundType.GLASS).noOcclusion().isValidSpawn(AetherBlocks::never).isRedstoneConductor(AetherBlocks::never).isSuffocating(AetherBlocks::never).isViewBlocking(AetherBlocks::never).setId(key)));
    public static final IronBarsBlock QUICKSOIL_GLASS_PANE = registerKeyed("quicksoil_glass_pane", key -> new QuicksoilGlassPaneBlock(Block.Properties.of().mapColor(MapColor.COLOR_YELLOW).instrument(NoteBlockInstrument.HAT).strength(0.2F).friction(1.1F).lightLevel(AetherBlocks::lightLevel11).sound(SoundType.GLASS).noOcclusion().setId(key)));
    public static final Block AEROGEL = registerKeyed("aerogel", key -> new AerogelBlock(Block.Properties.of().mapColor(MapColor.DIAMOND).instrument(NoteBlockInstrument.IRON_XYLOPHONE).strength(1.0F, 2000.0F).sound(SoundType.METAL).noOcclusion().requiresCorrectToolForDrops().isViewBlocking(AetherBlocks::never).setId(key)));

    public static final Block AMBROSIUM_BLOCK = registerKeyed("ambrosium_block", key -> new Block(Block.Properties.of().mapColor(MapColor.COLOR_YELLOW).strength(5.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.METAL).setId(key)));
    public static final Block ZANITE_BLOCK = registerKeyed("zanite_block", key -> new Block(Block.Properties.of().mapColor(MapColor.COLOR_PURPLE).instrument(NoteBlockInstrument.BIT).strength(5.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.METAL).setId(key)));
    public static final Block ENCHANTED_GRAVITITE = registerKeyed("enchanted_gravitite", key -> new FloatingBlock(true, Block.Properties.of().mapColor(MapColor.COLOR_PINK).instrument(NoteBlockInstrument.PLING).strength(5.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.METAL).setId(key)));

    public static final Block ALTAR = registerKeyed("altar", key -> new AltarBlock(Block.Properties.of().mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASEDRUM).strength(2.5F).setId(key)));
    public static final Block FREEZER = registerKeyed("freezer", key -> new FreezerBlock(Block.Properties.of().mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASEDRUM).strength(2.0F).setId(key)));
    public static final Block INCUBATOR = registerKeyed("incubator", key -> new IncubatorBlock(Block.Properties.of().mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASEDRUM).strength(2.0F).setId(key)));

    public static final Block AMBROSIUM_WALL_TORCH = registerBlockOnly("ambrosium_wall_torch", () -> new WallTorchBlock(ParticleTypes.SMOKE, Block.Properties.ofFullCopy(Blocks.WALL_TORCH)));
    public static final Block AMBROSIUM_TORCH = registerKeyed("ambrosium_torch", key -> new TorchBlock(ParticleTypes.SMOKE, Block.Properties.ofFullCopy(Blocks.TORCH).setId(key)));

    public static final StandingSignBlock SKYROOT_SIGN = registerKeyed("skyroot_sign", key -> new SkyrootSignBlock(AetherWoodTypes.SKYROOT, Block.Properties.of().mapColor(MapColor.SAND).forceSolidOn().ignitedByLava().instrument(NoteBlockInstrument.BASS).noCollision().strength(1.0F).sound(SoundType.WOOD).setId(key)));
    public static final WallSignBlock SKYROOT_WALL_SIGN = registerBlockOnly("skyroot_wall_sign", () -> new SkyrootWallSignBlock(AetherWoodTypes.SKYROOT, Block.Properties.ofFullCopy(SKYROOT_SIGN)));
    public static final CeilingHangingSignBlock SKYROOT_HANGING_SIGN = registerKeyed("skyroot_hanging_sign", key -> new SkyrootCeilingHangingSignBlock(AetherWoodTypes.SKYROOT, BlockBehaviour.Properties.of().mapColor(Blocks.OAK_LOG.defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollision().strength(1.0F).ignitedByLava().setId(key)));
    public static final WallHangingSignBlock SKYROOT_WALL_HANGING_SIGN = registerBlockOnly("skyroot_wall_hanging_sign", () -> new SkyrootWallHangingSignBlock(AetherWoodTypes.SKYROOT, BlockBehaviour.Properties.of().mapColor(Blocks.OAK_LOG.defaultMapColor()).forceSolidOn().instrument(NoteBlockInstrument.BASS).noCollision().strength(1.0F).ignitedByLava()));

    public static final Block BERRY_BUSH = registerKeyed("berry_bush", key -> new BerryBushBlock(Block.Properties.of().mapColor(MapColor.GRASS).pushReaction(PushReaction.DESTROY).strength(0.2F).sound(SoundType.GRASS).noOcclusion().isValidSpawn(AetherBlocks::ocelotOrParrot).isRedstoneConductor(AetherBlocks::never).isSuffocating(AetherBlocks::never).isViewBlocking(AetherBlocks::never).setId(key)));
    public static final Block BERRY_BUSH_STEM = registerKeyed("berry_bush_stem", key -> new BerryBushStemBlock(Block.Properties.of().mapColor(MapColor.GRASS).pushReaction(PushReaction.DESTROY).strength(0.2F).sound(SoundType.GRASS).noCollision().setId(key)));
    public static final FlowerPotBlock POTTED_BERRY_BUSH = registerBlockOnly("potted_berry_bush", () -> new FlowerPotBlock(BERRY_BUSH, Block.Properties.ofFullCopy(Blocks.FLOWER_POT)));
    public static final FlowerPotBlock POTTED_BERRY_BUSH_STEM = registerBlockOnly("potted_berry_bush_stem", () -> new FlowerPotBlock(BERRY_BUSH_STEM, Block.Properties.ofFullCopy(Blocks.FLOWER_POT)));

    public static final Block PURPLE_FLOWER = registerKeyed("purple_flower", key -> new AetherFlowerBlock(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(AetherEffects.INEBRIATION), 12, Block.Properties.ofFullCopy(Blocks.DANDELION).setId(key)));
    public static final Block WHITE_FLOWER = registerKeyed("white_flower", key -> new AetherFlowerBlock(MobEffects.SLOW_FALLING, 4, Block.Properties.ofFullCopy(Blocks.DANDELION).setId(key)));
    public static final FlowerPotBlock POTTED_PURPLE_FLOWER = registerBlockOnly("potted_purple_flower", () -> new FlowerPotBlock(PURPLE_FLOWER, Block.Properties.ofFullCopy(Blocks.FLOWER_POT)));
    public static final FlowerPotBlock POTTED_WHITE_FLOWER = registerBlockOnly("potted_white_flower", () -> new FlowerPotBlock(WHITE_FLOWER, Block.Properties.ofFullCopy(Blocks.FLOWER_POT)));

    public static final SaplingBlock SKYROOT_SAPLING = registerKeyed("skyroot_sapling", key -> new SaplingBlock(AetherTreeGrowers.SKYROOT, Block.Properties.ofFullCopy(Blocks.OAK_SAPLING).setId(key)));
    public static final SaplingBlock GOLDEN_OAK_SAPLING = registerKeyed("golden_oak_sapling", key -> new SaplingBlock(AetherTreeGrowers.GOLDEN_OAK, Block.Properties.ofFullCopy(Blocks.OAK_SAPLING).setId(key)));
    public static final FlowerPotBlock POTTED_SKYROOT_SAPLING = registerBlockOnly("potted_skyroot_sapling", () -> new FlowerPotBlock(SKYROOT_SAPLING, Block.Properties.ofFullCopy(Blocks.FLOWER_POT)));
    public static final FlowerPotBlock POTTED_GOLDEN_OAK_SAPLING = registerBlockOnly("potted_golden_oak_sapling", () -> new FlowerPotBlock(GOLDEN_OAK_SAPLING, Block.Properties.ofFullCopy(Blocks.FLOWER_POT)));

    public static final Block CARVED_STONE = registerKeyed("carved_stone", key -> new Block(Block.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).strength(0.5F, 6.0F).requiresCorrectToolForDrops().setId(key)));
    public static final Block SENTRY_STONE = registerKeyed("sentry_stone", key -> new Block(Block.Properties.ofFullCopy(CARVED_STONE).lightLevel(AetherBlocks::lightLevel11).setId(key)));
    public static final Block ANGELIC_STONE = registerKeyed("angelic_stone", key -> new Block(Block.Properties.of().mapColor(MapColor.SAND).instrument(NoteBlockInstrument.BASEDRUM).strength(0.5F, 6.0F).requiresCorrectToolForDrops().setId(key)));
    public static final Block LIGHT_ANGELIC_STONE = registerKeyed("light_angelic_stone", key -> new Block(Block.Properties.ofFullCopy(ANGELIC_STONE).lightLevel(AetherBlocks::lightLevel11).setId(key)));
    public static final Block HELLFIRE_STONE = registerKeyed("hellfire_stone", key -> new Block(Block.Properties.of().mapColor(MapColor.NETHER).instrument(NoteBlockInstrument.BASEDRUM).strength(0.5F, 6.0F).requiresCorrectToolForDrops().setId(key)));
    public static final Block LIGHT_HELLFIRE_STONE = registerKeyed("light_hellfire_stone", key -> new Block(Block.Properties.ofFullCopy(HELLFIRE_STONE).lightLevel(AetherBlocks::lightLevel11).setId(key)));

    public static final Block LOCKED_CARVED_STONE = registerKeyed("locked_carved_stone", key -> new Block(Block.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).strength(-1.0F, 3600000.0F).setId(key)));
    public static final Block LOCKED_SENTRY_STONE = registerKeyed("locked_sentry_stone", key -> new Block(Block.Properties.ofFullCopy(LOCKED_CARVED_STONE).lightLevel(AetherBlocks::lightLevel11).setId(key)));
    public static final Block LOCKED_ANGELIC_STONE = registerKeyed("locked_angelic_stone", key -> new Block(Block.Properties.of().mapColor(MapColor.SAND).instrument(NoteBlockInstrument.BASEDRUM).strength(-1.0F, 3600000.0F).setId(key)));
    public static final Block LOCKED_LIGHT_ANGELIC_STONE = registerKeyed("locked_light_angelic_stone", key -> new Block(Block.Properties.ofFullCopy(LOCKED_ANGELIC_STONE).lightLevel(AetherBlocks::lightLevel11).setId(key)));
    public static final Block LOCKED_HELLFIRE_STONE = registerKeyed("locked_hellfire_stone", key -> new Block(Block.Properties.of().mapColor(MapColor.NETHER).instrument(NoteBlockInstrument.BASEDRUM).strength(-1.0F, 3600000.0F).setId(key)));
    public static final Block LOCKED_LIGHT_HELLFIRE_STONE = registerKeyed("locked_light_hellfire_stone", key -> new Block(Block.Properties.ofFullCopy(LOCKED_HELLFIRE_STONE).lightLevel(AetherBlocks::lightLevel11).setId(key)));

    public static final Block TRAPPED_CARVED_STONE = registerKeyed("trapped_carved_stone", key -> new TrappedBlock(() -> AetherEntityTypes.SENTRY, () -> CARVED_STONE.defaultBlockState(), Block.Properties.ofFullCopy(CARVED_STONE).setId(key)));
    public static final Block TRAPPED_SENTRY_STONE = registerKeyed("trapped_sentry_stone", key -> new TrappedBlock(() -> AetherEntityTypes.SENTRY, () -> SENTRY_STONE.defaultBlockState(), Block.Properties.ofFullCopy(SENTRY_STONE).setId(key)));
    public static final Block TRAPPED_ANGELIC_STONE = registerKeyed("trapped_angelic_stone", key -> new TrappedBlock(() -> AetherEntityTypes.VALKYRIE, () -> LOCKED_ANGELIC_STONE.defaultBlockState(), Block.Properties.ofFullCopy(LOCKED_ANGELIC_STONE).setId(key)));
    public static final Block TRAPPED_LIGHT_ANGELIC_STONE = registerKeyed("trapped_light_angelic_stone", key -> new TrappedBlock(() -> AetherEntityTypes.VALKYRIE, () -> LOCKED_LIGHT_ANGELIC_STONE.defaultBlockState(), Block.Properties.ofFullCopy(LOCKED_LIGHT_ANGELIC_STONE).setId(key)));
    public static final Block TRAPPED_HELLFIRE_STONE = registerKeyed("trapped_hellfire_stone", key -> new TrappedBlock(() -> AetherEntityTypes.FIRE_MINION, () -> LOCKED_HELLFIRE_STONE.defaultBlockState(), Block.Properties.ofFullCopy(LOCKED_HELLFIRE_STONE).setId(key)));
    public static final Block TRAPPED_LIGHT_HELLFIRE_STONE = registerKeyed("trapped_light_hellfire_stone", key -> new TrappedBlock(() -> AetherEntityTypes.FIRE_MINION, () -> LOCKED_LIGHT_HELLFIRE_STONE.defaultBlockState(), Block.Properties.ofFullCopy(LOCKED_LIGHT_HELLFIRE_STONE).setId(key)));

    public static final Block BOSS_DOORWAY_CARVED_STONE = registerKeyed("boss_doorway_carved_stone", key -> new DoorwayBlock(() -> AetherEntityTypes.SLIDER, BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).strength(-1.0F, 3600000.0F).forceSolidOn().setId(key)));
    public static final Block BOSS_DOORWAY_SENTRY_STONE = registerKeyed("boss_doorway_sentry_stone", key -> new DoorwayBlock(() -> AetherEntityTypes.SLIDER, BlockBehaviour.Properties.ofFullCopy(BOSS_DOORWAY_CARVED_STONE).setId(key)));
    public static final Block BOSS_DOORWAY_ANGELIC_STONE = registerKeyed("boss_doorway_angelic_stone", key -> new DoorwayBlock(() -> AetherEntityTypes.VALKYRIE_QUEEN, BlockBehaviour.Properties.of().mapColor(MapColor.SAND).instrument(NoteBlockInstrument.BASEDRUM).strength(-1.0F, 3600000.0F).forceSolidOn().setId(key)));
    public static final Block BOSS_DOORWAY_LIGHT_ANGELIC_STONE = registerKeyed("boss_doorway_light_angelic_stone", key -> new DoorwayBlock(() -> AetherEntityTypes.VALKYRIE_QUEEN, BlockBehaviour.Properties.ofFullCopy(BOSS_DOORWAY_ANGELIC_STONE).setId(key)));
    public static final Block BOSS_DOORWAY_HELLFIRE_STONE = registerKeyed("boss_doorway_hellfire_stone", key -> new DoorwayBlock(() -> AetherEntityTypes.SUN_SPIRIT, BlockBehaviour.Properties.of().mapColor(MapColor.NETHER).instrument(NoteBlockInstrument.BASEDRUM).strength(-1.0F, 3600000.0F).forceSolidOn().setId(key)));
    public static final Block BOSS_DOORWAY_LIGHT_HELLFIRE_STONE = registerKeyed("boss_doorway_light_hellfire_stone", key -> new DoorwayBlock(() -> AetherEntityTypes.SUN_SPIRIT, BlockBehaviour.Properties.ofFullCopy(BOSS_DOORWAY_HELLFIRE_STONE).setId(key)));

    public static final Block TREASURE_DOORWAY_CARVED_STONE = registerKeyed("treasure_doorway_carved_stone", key -> new TreasureDoorwayBlock(BlockBehaviour.Properties.ofFullCopy(LOCKED_CARVED_STONE).setId(key)));
    public static final Block TREASURE_DOORWAY_SENTRY_STONE = registerKeyed("treasure_doorway_sentry_stone", key -> new TreasureDoorwayBlock(BlockBehaviour.Properties.ofFullCopy(LOCKED_SENTRY_STONE).setId(key)));
    public static final Block TREASURE_DOORWAY_ANGELIC_STONE = registerKeyed("treasure_doorway_angelic_stone", key -> new TreasureDoorwayBlock(BlockBehaviour.Properties.ofFullCopy(LOCKED_ANGELIC_STONE).setId(key)));
    public static final Block TREASURE_DOORWAY_LIGHT_ANGELIC_STONE = registerKeyed("treasure_doorway_light_angelic_stone", key -> new TreasureDoorwayBlock(BlockBehaviour.Properties.ofFullCopy(LOCKED_LIGHT_ANGELIC_STONE).setId(key)));
    public static final Block TREASURE_DOORWAY_HELLFIRE_STONE = registerKeyed("treasure_doorway_hellfire_stone", key -> new TreasureDoorwayBlock(BlockBehaviour.Properties.ofFullCopy(LOCKED_HELLFIRE_STONE).setId(key)));
    public static final Block TREASURE_DOORWAY_LIGHT_HELLFIRE_STONE = registerKeyed("treasure_doorway_light_hellfire_stone", key -> new TreasureDoorwayBlock(BlockBehaviour.Properties.ofFullCopy(LOCKED_LIGHT_HELLFIRE_STONE).setId(key)));

    public static final Block CHEST_MIMIC = registerKeyed("chest_mimic", key -> new ChestMimicBlock(Block.Properties.ofFullCopy(Blocks.CHEST).setId(key)));
    public static final Block TREASURE_CHEST = registerKeyed("treasure_chest", key -> new TreasureChestBlock(Block.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).strength(-1.0F, 3600000.0F).requiresCorrectToolForDrops().setId(key)));

    public static final RotatedPillarBlock PILLAR = registerKeyed("pillar",
            key -> new RotatedPillarBlock(Block.Properties.of().mapColor(MapColor.QUARTZ).instrument(NoteBlockInstrument.BASEDRUM).strength(0.5F).sound(SoundType.METAL).requiresCorrectToolForDrops().setId(key)));
    public static final FacingPillarBlock PILLAR_TOP = registerKeyed("pillar_top",
            key -> new FacingPillarBlock(Block.Properties.of().mapColor(MapColor.QUARTZ).instrument(NoteBlockInstrument.BASEDRUM).strength(0.5F).sound(SoundType.METAL).requiresCorrectToolForDrops().setId(key)));

    public static final Block PRESENT = registerKeyed("present",
            key -> new Block(Block.Properties.of().mapColor(MapColor.COLOR_GREEN).instrument(NoteBlockInstrument.BELL).strength(0.1F).sound(SoundType.WOOL).setId(key)));

    public static final FenceBlock SKYROOT_FENCE = registerKeyed("skyroot_fence", key -> new FenceBlock(Block.Properties.ofFullCopy(Blocks.OAK_FENCE).setId(key)));
    public static final FenceGateBlock SKYROOT_FENCE_GATE = registerKeyed("skyroot_fence_gate", key -> new FenceGateBlock(AetherWoodTypes.SKYROOT, Block.Properties.ofFullCopy(Blocks.OAK_FENCE_GATE).setId(key)));
    public static final DoorBlock SKYROOT_DOOR = registerKeyed("skyroot_door", key -> new DoorBlock(AetherWoodTypes.SKYROOT_BLOCK_SET, Block.Properties.ofFullCopy(Blocks.OAK_DOOR).setId(key)));
    public static final TrapDoorBlock SKYROOT_TRAPDOOR = registerKeyed("skyroot_trapdoor", key -> new TrapDoorBlock(AetherWoodTypes.SKYROOT_BLOCK_SET, Block.Properties.ofFullCopy(Blocks.OAK_TRAPDOOR).setId(key)));
    public static final ButtonBlock SKYROOT_BUTTON = registerKeyed("skyroot_button", key -> new ButtonBlock(AetherWoodTypes.SKYROOT_BLOCK_SET, 30, Block.Properties.ofFullCopy(Blocks.OAK_BUTTON).setId(key)));
    public static final PressurePlateBlock SKYROOT_PRESSURE_PLATE = registerKeyed("skyroot_pressure_plate", key -> new PressurePlateBlock(AetherWoodTypes.SKYROOT_BLOCK_SET, Block.Properties.ofFullCopy(Blocks.OAK_PRESSURE_PLATE).setId(key)));

    public static final ButtonBlock HOLYSTONE_BUTTON = registerKeyed("holystone_button", key -> new ButtonBlock(BlockSetType.STONE, 20, Block.Properties.ofFullCopy(Blocks.STONE_BUTTON).setId(key)));
    public static final PressurePlateBlock HOLYSTONE_PRESSURE_PLATE = registerKeyed("holystone_pressure_plate", key -> new PressurePlateBlock(BlockSetType.STONE, Block.Properties.of().mapColor(MapColor.WOOL).forceSolidOn().instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().noCollision().strength(0.5F).setId(key)));

    public static final WallBlock CARVED_WALL = registerKeyed("carved_wall", key -> new WallBlock(Block.Properties.ofFullCopy(AetherBlocks.CARVED_STONE).forceSolidOn().setId(key)));
    public static final WallBlock ANGELIC_WALL = registerKeyed("angelic_wall", key -> new WallBlock(Block.Properties.ofFullCopy(AetherBlocks.ANGELIC_STONE).forceSolidOn().setId(key)));
    public static final WallBlock HELLFIRE_WALL = registerKeyed("hellfire_wall", key -> new WallBlock(Block.Properties.ofFullCopy(AetherBlocks.HELLFIRE_STONE).forceSolidOn().setId(key)));
    public static final WallBlock HOLYSTONE_WALL = registerKeyed("holystone_wall", key -> new WallBlock(Block.Properties.ofFullCopy(AetherBlocks.HOLYSTONE).forceSolidOn().setId(key)));
    public static final WallBlock MOSSY_HOLYSTONE_WALL = registerKeyed("mossy_holystone_wall", key -> new WallBlock(Block.Properties.ofFullCopy(AetherBlocks.MOSSY_HOLYSTONE).forceSolidOn().setId(key)));
    public static final WallBlock ICESTONE_WALL = registerKeyed("icestone_wall", key -> new IcestoneWallBlock(Block.Properties.ofFullCopy(AetherBlocks.ICESTONE).forceSolidOn().setId(key)));
    public static final WallBlock HOLYSTONE_BRICK_WALL = registerKeyed("holystone_brick_wall", key -> new WallBlock(Block.Properties.ofFullCopy(AetherBlocks.HOLYSTONE_BRICKS).forceSolidOn().setId(key)));
    public static final WallBlock AEROGEL_WALL = registerKeyed("aerogel_wall", key -> new AerogelWallBlock(Block.Properties.of().mapColor(MapColor.DIAMOND).forceSolidOn().instrument(NoteBlockInstrument.BASEDRUM).strength(1.0F, 2000.0F).sound(SoundType.METAL).requiresCorrectToolForDrops().isViewBlocking(AetherBlocks::never).noOcclusion().setId(key)));

    public static final StairBlock SKYROOT_STAIRS = registerKeyed("skyroot_stairs",
            key -> new StairBlock(SKYROOT_PLANKS.defaultBlockState(), Block.Properties.ofFullCopy(AetherBlocks.SKYROOT_PLANKS).setId(key)));
    public static final StairBlock CARVED_STAIRS = registerKeyed("carved_stairs",
            key -> new StairBlock(CARVED_STONE.defaultBlockState(), Block.Properties.ofFullCopy(AetherBlocks.CARVED_STONE).setId(key)));
    public static final StairBlock ANGELIC_STAIRS = registerKeyed("angelic_stairs",
            key -> new StairBlock(ANGELIC_STONE.defaultBlockState(), Block.Properties.ofFullCopy(AetherBlocks.ANGELIC_STONE).setId(key)));
    public static final StairBlock HELLFIRE_STAIRS = registerKeyed("hellfire_stairs",
            key -> new StairBlock(HELLFIRE_STONE.defaultBlockState(), Block.Properties.ofFullCopy(AetherBlocks.HELLFIRE_STONE).setId(key)));
    public static final StairBlock HOLYSTONE_STAIRS = registerKeyed("holystone_stairs",
            key -> new StairBlock(HOLYSTONE.defaultBlockState(), Block.Properties.ofFullCopy(AetherBlocks.HOLYSTONE).setId(key)));
    public static final StairBlock MOSSY_HOLYSTONE_STAIRS = registerKeyed("mossy_holystone_stairs",
            key -> new StairBlock(MOSSY_HOLYSTONE.defaultBlockState(), Block.Properties.ofFullCopy(AetherBlocks.MOSSY_HOLYSTONE).setId(key)));
    public static final StairBlock ICESTONE_STAIRS = registerKeyed("icestone_stairs",
            key -> new IcestoneStairsBlock(ICESTONE.defaultBlockState(), Block.Properties.ofFullCopy(AetherBlocks.ICESTONE).setId(key)));
    public static final StairBlock HOLYSTONE_BRICK_STAIRS = registerKeyed("holystone_brick_stairs",
            key -> new StairBlock(HOLYSTONE_BRICKS.defaultBlockState(), Block.Properties.ofFullCopy(AetherBlocks.HOLYSTONE_BRICKS).setId(key)));
    public static final StairBlock AEROGEL_STAIRS = registerKeyed("aerogel_stairs",
            key -> new AerogelStairsBlock(AEROGEL.defaultBlockState(), Block.Properties.ofFullCopy(AetherBlocks.AEROGEL).isViewBlocking(AetherBlocks::never).setId(key)));

    public static final SlabBlock SKYROOT_SLAB = registerKeyed("skyroot_slab",
            key -> new SlabBlock(Block.Properties.ofFullCopy(AetherBlocks.SKYROOT_PLANKS).strength(2.0F, 3.0F).setId(key)));
    public static final SlabBlock CARVED_SLAB = registerKeyed("carved_slab",
            key -> new SlabBlock(Block.Properties.ofFullCopy(AetherBlocks.CARVED_STONE).strength(0.5F, 6.0F).setId(key)));
    public static final SlabBlock ANGELIC_SLAB = registerKeyed("angelic_slab",
            key -> new SlabBlock(Block.Properties.ofFullCopy(AetherBlocks.ANGELIC_STONE).strength(0.5F, 6.0F).setId(key)));
    public static final SlabBlock HELLFIRE_SLAB = registerKeyed("hellfire_slab",
            key -> new SlabBlock(Block.Properties.ofFullCopy(AetherBlocks.HELLFIRE_STONE).strength(0.5F, 6.0F).setId(key)));
    public static final SlabBlock HOLYSTONE_SLAB = registerKeyed("holystone_slab",
            key -> new SlabBlock(Block.Properties.ofFullCopy(AetherBlocks.HOLYSTONE).strength(0.5F, 6.0F).setId(key)));
    public static final SlabBlock MOSSY_HOLYSTONE_SLAB = registerKeyed("mossy_holystone_slab",
            key -> new SlabBlock(Block.Properties.ofFullCopy(AetherBlocks.MOSSY_HOLYSTONE).strength(0.5F, 6.0F).setId(key)));
    public static final SlabBlock ICESTONE_SLAB = registerKeyed("icestone_slab",
            key -> new IcestoneSlabBlock(Block.Properties.ofFullCopy(AetherBlocks.ICESTONE).strength(0.5F, 6.0F).setId(key)));
    public static final SlabBlock HOLYSTONE_BRICK_SLAB = registerKeyed("holystone_brick_slab",
            key -> new SlabBlock(Block.Properties.ofFullCopy(AetherBlocks.HOLYSTONE_BRICKS).strength(2.0F, 6.0F).setId(key)));
    public static final SlabBlock AEROGEL_SLAB = registerKeyed("aerogel_slab",
            key -> new AerogelSlabBlock(Block.Properties.ofFullCopy(AetherBlocks.AEROGEL).strength(1.0F, 2000.0F).isViewBlocking(AetherBlocks::never).setId(key)));

    public static final Block SUN_ALTAR = registerKeyed("sun_altar",
            key -> new SunAltarBlock(Block.Properties.of().mapColor(MapColor.NETHER).instrument(NoteBlockInstrument.BASEDRUM).strength(2.0F).sound(SoundType.METAL).setId(key)));

    public static final Block SKYROOT_BOOKSHELF = registerKeyed("skyroot_bookshelf", key -> new BookshelfBlock(Block.Properties.ofFullCopy(Blocks.BOOKSHELF).setId(key)));

    public static final BedBlock SKYROOT_BED = register("skyroot_bed", () -> new SkyrootBedBlock(Block.Properties.ofFullCopy(Blocks.BED.pick(DyeColor.CYAN))));

    public static final Block FROSTED_ICE = registerBlockOnly("frosted_ice", () -> new AetherFrostedIceBlock(BlockBehaviour.Properties.of().mapColor(MapColor.ICE).friction(0.98F).randomTicks().strength(0.5F).sound(SoundType.GLASS).noOcclusion().isValidSpawn((state, level, pos, entityType) -> entityType == EntityTypes.POLAR_BEAR).isRedstoneConductor(AetherBlocks::never)));
    public static final Block UNSTABLE_OBSIDIAN = registerBlockOnly("unstable_obsidian", () -> new UnstableObsidianBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).instrument(NoteBlockInstrument.BASEDRUM).randomTicks().requiresCorrectToolForDrops().strength(50.0F, 1200.0F)));

    public static void bootstrap() {
    }

    public static void registerFlammability() {
        FireBlockAccessor fireBlockAccessor = (FireBlockAccessor) Blocks.FIRE;
        fireBlockAccessor.callSetFlammable(AetherBlocks.SKYROOT_LEAVES, 30, 60);
        fireBlockAccessor.callSetFlammable(AetherBlocks.GOLDEN_OAK_LEAVES, 30, 60);
        fireBlockAccessor.callSetFlammable(AetherBlocks.CRYSTAL_LEAVES, 30, 60);
        fireBlockAccessor.callSetFlammable(AetherBlocks.CRYSTAL_FRUIT_LEAVES, 30, 60);
        fireBlockAccessor.callSetFlammable(AetherBlocks.HOLIDAY_LEAVES, 30, 60);
        fireBlockAccessor.callSetFlammable(AetherBlocks.DECORATED_HOLIDAY_LEAVES, 30, 60);
        fireBlockAccessor.callSetFlammable(AetherBlocks.SKYROOT_LOG, 5, 5);
        fireBlockAccessor.callSetFlammable(AetherBlocks.GOLDEN_OAK_LOG, 5, 5);
        fireBlockAccessor.callSetFlammable(AetherBlocks.STRIPPED_SKYROOT_LOG, 5, 5);
        fireBlockAccessor.callSetFlammable(AetherBlocks.SKYROOT_WOOD, 5, 5);
        fireBlockAccessor.callSetFlammable(AetherBlocks.GOLDEN_OAK_WOOD, 5, 5);
        fireBlockAccessor.callSetFlammable(AetherBlocks.STRIPPED_SKYROOT_WOOD, 5, 5);
        fireBlockAccessor.callSetFlammable(AetherBlocks.SKYROOT_PLANKS, 5, 20);
        fireBlockAccessor.callSetFlammable(AetherBlocks.BERRY_BUSH, 30, 60);
        fireBlockAccessor.callSetFlammable(AetherBlocks.BERRY_BUSH_STEM, 60, 100);
        fireBlockAccessor.callSetFlammable(AetherBlocks.PURPLE_FLOWER, 60, 100);
        fireBlockAccessor.callSetFlammable(AetherBlocks.WHITE_FLOWER, 60, 100);
        fireBlockAccessor.callSetFlammable(AetherBlocks.SKYROOT_FENCE_GATE, 5, 20);
        fireBlockAccessor.callSetFlammable(AetherBlocks.SKYROOT_FENCE, 5, 20);
        fireBlockAccessor.callSetFlammable(AetherBlocks.SKYROOT_STAIRS, 5, 20);
        fireBlockAccessor.callSetFlammable(AetherBlocks.SKYROOT_SLAB, 5, 20);
        fireBlockAccessor.callSetFlammable(AetherBlocks.SKYROOT_BOOKSHELF, 30, 20);
    }

    public static void registerBlockItems() {
        for (BlockItemRegistration registration : BLOCK_ITEMS) {
            Identifier id = Identifier.fromNamespaceAndPath(Aether.MODID, registration.name());
            ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id);
            BlockItem item = createBlockItem(registration.block(), new Item.Properties().setId(key));
            Registry.register(BuiltInRegistries.ITEM, id, item);
        }
        BLOCK_ITEMS.clear();
    }

    private static <T extends Block> T registerBlockOnly(String name, Supplier<? extends T> supplier) {
        Identifier id = Identifier.fromNamespaceAndPath(Aether.MODID, name);
        T block = RegistryConstructionContext.constructWithId(Registries.BLOCK, id, supplier);
        return Registry.register(BuiltInRegistries.BLOCK, id, block);
    }

    private static <B extends Block> B register(String name, Supplier<B> block) {
        B register = registerBlockOnly(name, block);
        BLOCK_ITEMS.add(new BlockItemRegistration(name, register));
        return register;
    }

    private static <B extends Block> B registerKeyed(String name, Function<ResourceKey<Block>, B> factory) {
        Identifier id = Identifier.fromNamespaceAndPath(Aether.MODID, name);
        ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, id);
        B block = Registry.register(BuiltInRegistries.BLOCK, id, factory.apply(key));
        BLOCK_ITEMS.add(new BlockItemRegistration(name, block));
        return block;
    }

    private static BlockItem createBlockItem(Block block, Item.Properties properties) {
        if (block == ENCHANTED_AETHER_GRASS_BLOCK
                || block == QUICKSOIL_GLASS
                || block == QUICKSOIL_GLASS_PANE
                || block == ENCHANTED_GRAVITITE) {
            return new BlockItem(block, properties.rarity(Rarity.RARE));
        } else if (block == AEROGEL
                || block == AEROGEL_WALL
                || block == AEROGEL_STAIRS
                || block == AEROGEL_SLAB) {
            return new BlockItem(block, properties.rarity(Rarity.EPIC));
        } else if (block == AMBROSIUM_TORCH) {
            return new StandingAndWallBlockItem(AMBROSIUM_TORCH, AMBROSIUM_WALL_TORCH, Direction.DOWN, properties);
        } else if (block == SKYROOT_SIGN) {
            return new SignItem(SKYROOT_SIGN, SKYROOT_WALL_SIGN, properties.stacksTo(16));
        } else if (block == SKYROOT_HANGING_SIGN) {
            return new HangingSignItem(SKYROOT_HANGING_SIGN, SKYROOT_WALL_HANGING_SIGN, properties.stacksTo(16));
        } else if (block == CHEST_MIMIC) {
            return new BlockItem(block, properties);
        } else if (block == TREASURE_CHEST) {
            return new BlockItem(block, properties);
        } else if (block == SKYROOT_DOOR) {
            return new DoubleHighBlockItem(block, properties);
        } else if (block == SUN_ALTAR) {
            return new BlockItem(block, properties.fireResistant());
        } else if (block == SKYROOT_BED) {
            return new BlockItem(block, properties.stacksTo(1));
        } else {
            return new BlockItem(block, properties);
        }
    }

    private record BlockItemRegistration(String name, Block block) {
    }

    private static boolean never(BlockState state, BlockGetter getter, BlockPos pos) {
        return false;
    }

    private static boolean always(BlockState state, BlockGetter getter, BlockPos pos) {
        return true;
    }

    private static <A> boolean never(BlockState state, BlockGetter getter, BlockPos pos, A block) {
        return false;
    }

    private static boolean ocelotOrParrot(BlockState state, BlockGetter getter, BlockPos pos, EntityType<?> type) {
        return type == EntityTypes.OCELOT || type == EntityTypes.PARROT;
    }

    private static int lightLevel11(BlockState state) {
        return 11;
    }
}
