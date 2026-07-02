package com.aetherteam.aether.item;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.AetherConfig;
import com.aetherteam.aether.block.AetherBlocks;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class AetherCreativeTabs {
    public static final CreativeModeTab AETHER_BUILDING_BLOCKS = register("building_blocks", CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
            .icon(() -> new ItemStack(AetherBlocks.HOLYSTONE_BRICKS))
            .title(Component.translatable("itemGroup." + Aether.MODID + ".building_blocks"))
            .displayItems((features, output) -> {
                output.accept(AetherBlocks.SKYROOT_LOG);
                output.accept(AetherBlocks.SKYROOT_WOOD);
                output.accept(AetherBlocks.STRIPPED_SKYROOT_LOG);
                output.accept(AetherBlocks.STRIPPED_SKYROOT_WOOD);
                output.accept(AetherBlocks.SKYROOT_PLANKS);
                output.accept(AetherBlocks.SKYROOT_STAIRS);
                output.accept(AetherBlocks.SKYROOT_SLAB);
                output.accept(AetherBlocks.SKYROOT_FENCE);
                output.accept(AetherBlocks.SKYROOT_FENCE_GATE);
                output.accept(AetherBlocks.SKYROOT_DOOR);
                output.accept(AetherBlocks.SKYROOT_TRAPDOOR);
                output.accept(AetherBlocks.SKYROOT_PRESSURE_PLATE);
                output.accept(AetherBlocks.SKYROOT_BUTTON);
                output.accept(AetherBlocks.GOLDEN_OAK_LOG);
                output.accept(AetherBlocks.GOLDEN_OAK_WOOD);
                output.accept(AetherBlocks.QUICKSOIL_GLASS);
                output.accept(AetherBlocks.QUICKSOIL_GLASS_PANE);
                output.accept(AetherBlocks.HOLYSTONE);
                output.accept(AetherBlocks.HOLYSTONE_STAIRS);
                output.accept(AetherBlocks.HOLYSTONE_SLAB);
                output.accept(AetherBlocks.HOLYSTONE_WALL);
                output.accept(AetherBlocks.HOLYSTONE_PRESSURE_PLATE);
                output.accept(AetherBlocks.HOLYSTONE_BUTTON);
                output.accept(AetherBlocks.MOSSY_HOLYSTONE);
                output.accept(AetherBlocks.MOSSY_HOLYSTONE_STAIRS);
                output.accept(AetherBlocks.MOSSY_HOLYSTONE_SLAB);
                output.accept(AetherBlocks.MOSSY_HOLYSTONE_WALL);
                output.accept(AetherBlocks.HOLYSTONE_BRICKS);
                output.accept(AetherBlocks.HOLYSTONE_BRICK_STAIRS);
                output.accept(AetherBlocks.HOLYSTONE_BRICK_SLAB);
                output.accept(AetherBlocks.HOLYSTONE_BRICK_WALL);
                output.accept(AetherBlocks.ICESTONE);
                output.accept(AetherBlocks.ICESTONE_STAIRS);
                output.accept(AetherBlocks.ICESTONE_SLAB);
                output.accept(AetherBlocks.ICESTONE_WALL);
                output.accept(AetherBlocks.AMBROSIUM_BLOCK);
                output.accept(AetherBlocks.ZANITE_BLOCK);
                output.accept(AetherBlocks.ENCHANTED_GRAVITITE);
                output.accept(AetherBlocks.AEROGEL);
                output.accept(AetherBlocks.AEROGEL_STAIRS);
                output.accept(AetherBlocks.AEROGEL_SLAB);
                output.accept(AetherBlocks.AEROGEL_WALL);
            }).build());
    public static final CreativeModeTab AETHER_DUNGEON_BLOCKS = register("dungeon_blocks", CreativeModeTab.builder(CreativeModeTab.Row.TOP, 1)
            .icon(() -> new ItemStack(AetherBlocks.LIGHT_ANGELIC_STONE))
            .title(Component.translatable("itemGroup." + Aether.MODID + ".dungeon_blocks"))
            .displayItems((features, output) -> {
                output.accept(AetherBlocks.CARVED_STONE);
                output.accept(AetherBlocks.LOCKED_CARVED_STONE);
                output.accept(AetherBlocks.TRAPPED_CARVED_STONE);
                output.accept(AetherBlocks.BOSS_DOORWAY_CARVED_STONE);
                output.accept(AetherBlocks.TREASURE_DOORWAY_CARVED_STONE);
                output.accept(AetherBlocks.CARVED_STAIRS);
                output.accept(AetherBlocks.CARVED_SLAB);
                output.accept(AetherBlocks.CARVED_WALL);
                output.accept(AetherBlocks.SENTRY_STONE);
                output.accept(AetherBlocks.LOCKED_SENTRY_STONE);
                output.accept(AetherBlocks.TRAPPED_SENTRY_STONE);
                output.accept(AetherBlocks.BOSS_DOORWAY_SENTRY_STONE);
                output.accept(AetherBlocks.TREASURE_DOORWAY_SENTRY_STONE);
                output.accept(AetherBlocks.ANGELIC_STONE);
                output.accept(AetherBlocks.LOCKED_ANGELIC_STONE);
                output.accept(AetherBlocks.TRAPPED_ANGELIC_STONE);
                output.accept(AetherBlocks.BOSS_DOORWAY_ANGELIC_STONE);
                output.accept(AetherBlocks.TREASURE_DOORWAY_ANGELIC_STONE);
                output.accept(AetherBlocks.ANGELIC_STAIRS);
                output.accept(AetherBlocks.ANGELIC_SLAB);
                output.accept(AetherBlocks.ANGELIC_WALL);
                output.accept(AetherBlocks.LIGHT_ANGELIC_STONE);
                output.accept(AetherBlocks.LOCKED_LIGHT_ANGELIC_STONE);
                output.accept(AetherBlocks.TRAPPED_LIGHT_ANGELIC_STONE);
                output.accept(AetherBlocks.BOSS_DOORWAY_LIGHT_ANGELIC_STONE);
                output.accept(AetherBlocks.TREASURE_DOORWAY_LIGHT_ANGELIC_STONE);
                output.accept(AetherBlocks.PILLAR);
                output.accept(AetherBlocks.PILLAR_TOP);
                output.accept(AetherBlocks.HELLFIRE_STONE);
                output.accept(AetherBlocks.LOCKED_HELLFIRE_STONE);
                output.accept(AetherBlocks.TRAPPED_HELLFIRE_STONE);
                output.accept(AetherBlocks.BOSS_DOORWAY_HELLFIRE_STONE);
                output.accept(AetherBlocks.TREASURE_DOORWAY_HELLFIRE_STONE);
                output.accept(AetherBlocks.HELLFIRE_STAIRS);
                output.accept(AetherBlocks.HELLFIRE_SLAB);
                output.accept(AetherBlocks.HELLFIRE_WALL);
                output.accept(AetherBlocks.LIGHT_HELLFIRE_STONE);
                output.accept(AetherBlocks.LOCKED_LIGHT_HELLFIRE_STONE);
                output.accept(AetherBlocks.TRAPPED_LIGHT_HELLFIRE_STONE);
                output.accept(AetherBlocks.BOSS_DOORWAY_LIGHT_HELLFIRE_STONE);
                output.accept(AetherBlocks.TREASURE_DOORWAY_LIGHT_HELLFIRE_STONE);
                output.accept(AetherBlocks.TREASURE_CHEST);
                output.accept(AetherBlocks.CHEST_MIMIC);
            }).build());
    public static final CreativeModeTab AETHER_NATURAL_BLOCKS = register("natural_blocks", CreativeModeTab.builder(CreativeModeTab.Row.TOP, 2)
            .icon(() -> new ItemStack(AetherBlocks.AETHER_GRASS_BLOCK))
            .title(Component.translatable("itemGroup." + Aether.MODID + ".natural_blocks"))
            .displayItems((features, output) -> {
                output.accept(AetherBlocks.AETHER_GRASS_BLOCK);
                output.accept(AetherBlocks.ENCHANTED_AETHER_GRASS_BLOCK);
                output.accept(AetherBlocks.AETHER_DIRT_PATH);
                output.accept(AetherBlocks.AETHER_DIRT);
                output.accept(AetherBlocks.AETHER_FARMLAND);
                output.accept(AetherBlocks.QUICKSOIL);
                output.accept(AetherBlocks.HOLYSTONE);
                output.accept(AetherBlocks.MOSSY_HOLYSTONE);
                output.accept(AetherBlocks.ICESTONE);
                output.accept(AetherBlocks.AMBROSIUM_ORE);
                output.accept(AetherBlocks.ZANITE_ORE);
                output.accept(AetherBlocks.GRAVITITE_ORE);
                output.accept(AetherBlocks.SKYROOT_LOG);
                output.accept(AetherBlocks.GOLDEN_OAK_LOG);
                output.accept(AetherBlocks.SKYROOT_LEAVES);
                output.accept(AetherBlocks.GOLDEN_OAK_LEAVES);
                output.accept(AetherBlocks.CRYSTAL_LEAVES);
                output.accept(AetherBlocks.CRYSTAL_FRUIT_LEAVES);
                output.accept(AetherBlocks.HOLIDAY_LEAVES);
                output.accept(AetherBlocks.DECORATED_HOLIDAY_LEAVES);
                output.accept(AetherBlocks.SKYROOT_SAPLING);
                output.accept(AetherBlocks.GOLDEN_OAK_SAPLING);
                output.accept(AetherBlocks.BERRY_BUSH_STEM);
                output.accept(AetherBlocks.BERRY_BUSH);
                output.accept(AetherBlocks.PURPLE_FLOWER);
                output.accept(AetherBlocks.WHITE_FLOWER);
                output.accept(AetherBlocks.COLD_AERCLOUD);
                output.accept(AetherBlocks.BLUE_AERCLOUD);
                output.accept(AetherBlocks.GOLDEN_AERCLOUD);
                output.accept(AetherBlocks.PRESENT);
            }).build());
    public static final CreativeModeTab AETHER_FUNCTIONAL_BLOCKS = register("functional_blocks", CreativeModeTab.builder(CreativeModeTab.Row.TOP, 3)
            .icon(() -> new ItemStack(AetherBlocks.SKYROOT_SIGN))
            .title(Component.translatable("itemGroup." + Aether.MODID + ".functional_blocks"))
            .displayItems((features, output) -> {
                output.accept(AetherBlocks.AMBROSIUM_TORCH);
                output.accept(AetherBlocks.ALTAR);
                output.accept(AetherBlocks.FREEZER);
                output.accept(AetherBlocks.INCUBATOR);
                output.accept(AetherBlocks.SUN_ALTAR);
                output.accept(AetherBlocks.SKYROOT_BOOKSHELF);
                output.accept(AetherBlocks.SKYROOT_SIGN);
                output.accept(AetherBlocks.SKYROOT_HANGING_SIGN);
                output.accept(AetherBlocks.SKYROOT_BED);
                output.accept(AetherBlocks.TREASURE_CHEST);
                output.accept(AetherBlocks.CHEST_MIMIC);
                output.accept(AetherBlocks.PRESENT);
                output.accept(AetherItems.createSwetBannerItemStack(features.holders().lookupOrThrow(Registries.BANNER_PATTERN)));
                output.accept(AetherItems.AETHER_PORTAL_FRAME.get());
            }).build());
    public static final CreativeModeTab AETHER_REDSTONE_BLOCKS = register("redstone_blocks", CreativeModeTab.builder(CreativeModeTab.Row.TOP, 4)
            .icon(() -> new ItemStack(AetherBlocks.SKYROOT_FENCE_GATE))
            .title(Component.translatable("itemGroup." + Aether.MODID + ".redstone_blocks"))
            .displayItems((features, output) -> {
                output.accept(AetherBlocks.SKYROOT_BUTTON);
                output.accept(AetherBlocks.HOLYSTONE_BUTTON);
                output.accept(AetherBlocks.SKYROOT_PRESSURE_PLATE);
                output.accept(AetherBlocks.HOLYSTONE_PRESSURE_PLATE);
                output.accept(AetherBlocks.ALTAR);
                output.accept(AetherBlocks.FREEZER);
                output.accept(AetherBlocks.INCUBATOR);
                output.accept(AetherBlocks.TREASURE_CHEST);
                output.accept(AetherItems.SKYROOT_CHEST_BOAT.get());
                output.accept(AetherBlocks.SKYROOT_DOOR);
                output.accept(AetherBlocks.SKYROOT_FENCE_GATE);
                output.accept(AetherBlocks.SKYROOT_TRAPDOOR);
                output.accept(AetherBlocks.ENCHANTED_GRAVITITE);
            }).build());
    public static final CreativeModeTab AETHER_EQUIPMENT_AND_UTILITIES = register("equipment_and_utilities", CreativeModeTab.builder(CreativeModeTab.Row.BOTTOM, 0)
            .icon(() -> new ItemStack(AetherItems.GRAVITITE_PICKAXE.get()))
            .title(Component.translatable("itemGroup." + Aether.MODID + ".equipment_and_utilities"))
            .displayItems((features, output) -> {
                output.accept(AetherItems.SKYROOT_SWORD.get());
                output.accept(AetherItems.SKYROOT_SHOVEL.get());
                output.accept(AetherItems.SKYROOT_PICKAXE.get());
                output.accept(AetherItems.SKYROOT_AXE.get());
                output.accept(AetherItems.SKYROOT_HOE.get());
                output.accept(AetherItems.HOLYSTONE_SWORD.get());
                output.accept(AetherItems.HOLYSTONE_SHOVEL.get());
                output.accept(AetherItems.HOLYSTONE_PICKAXE.get());
                output.accept(AetherItems.HOLYSTONE_AXE.get());
                output.accept(AetherItems.HOLYSTONE_HOE.get());
                output.accept(AetherItems.ZANITE_SWORD.get());
                output.accept(AetherItems.ZANITE_SHOVEL.get());
                output.accept(AetherItems.ZANITE_PICKAXE.get());
                output.accept(AetherItems.ZANITE_AXE.get());
                output.accept(AetherItems.ZANITE_HOE.get());
                output.accept(AetherItems.GRAVITITE_SWORD.get());
                output.accept(AetherItems.GRAVITITE_SHOVEL.get());
                output.accept(AetherItems.GRAVITITE_PICKAXE.get());
                output.accept(AetherItems.GRAVITITE_AXE.get());
                output.accept(AetherItems.GRAVITITE_HOE.get());
                output.accept(AetherItems.VALKYRIE_LANCE.get());
                output.accept(AetherItems.VALKYRIE_SHOVEL.get());
                output.accept(AetherItems.VALKYRIE_PICKAXE.get());
                output.accept(AetherItems.VALKYRIE_AXE.get());
                output.accept(AetherItems.VALKYRIE_HOE.get());
                output.accept(AetherItems.GOLDEN_DART_SHOOTER.get());
                output.accept(AetherItems.GOLDEN_DART.get());
                output.accept(AetherItems.POISON_DART_SHOOTER.get());
                output.accept(AetherItems.POISON_DART.get());
                output.accept(AetherItems.ENCHANTED_DART_SHOOTER.get());
                output.accept(AetherItems.ENCHANTED_DART.get());
                output.accept(AetherItems.CANDY_CANE_SWORD.get());
                output.accept(AetherItems.HOLY_SWORD.get());
                output.accept(AetherItems.VAMPIRE_BLADE.get());
                output.accept(AetherItems.LIGHTNING_SWORD.get());
                output.accept(AetherItems.LIGHTNING_KNIFE.get());
                output.accept(AetherItems.FLAMING_SWORD.get());
                output.accept(AetherItems.PHOENIX_BOW.get());
                output.accept(AetherItems.PIG_SLAYER.get());
                output.accept(AetherItems.HAMMER_OF_KINGBDOGZ.get());
                output.accept(AetherItems.CLOUD_STAFF.get());
                output.accept(AetherItems.SKYROOT_BUCKET.get());
                output.accept(AetherItems.SKYROOT_WATER_BUCKET.get());
                output.accept(AetherItems.SKYROOT_PUFFERFISH_BUCKET.get());
                output.accept(AetherItems.SKYROOT_SALMON_BUCKET.get());
                output.accept(AetherItems.SKYROOT_COD_BUCKET.get());
                output.accept(AetherItems.SKYROOT_TROPICAL_FISH_BUCKET.get());
                output.accept(AetherItems.SKYROOT_AXOLOTL_BUCKET.get());
                output.accept(AetherItems.SKYROOT_TADPOLE_BUCKET.get());
                output.accept(AetherItems.SKYROOT_POWDER_SNOW_BUCKET.get());
                output.accept(AetherItems.SKYROOT_MILK_BUCKET.get());
                output.accept(AetherItems.SKYROOT_REMEDY_BUCKET.get());
                output.accept(AetherItems.SKYROOT_POISON_BUCKET.get());
                output.accept(AetherItems.BOOK_OF_LORE.get());
                output.accept(AetherItems.COLD_PARACHUTE.get());
                output.accept(AetherItems.GOLDEN_PARACHUTE.get());
                output.accept(AetherItems.AMBROSIUM_SHARD.get());
                output.accept(AetherItems.SWET_BALL.get());
                output.accept(AetherItems.BLUE_MOA_EGG.get());
                output.accept(AetherItems.WHITE_MOA_EGG.get());
                output.accept(AetherItems.BLACK_MOA_EGG.get());
                output.accept(AetherItems.NATURE_STAFF.get());
                output.accept(AetherItems.SKYROOT_BOAT.get());
                output.accept(AetherItems.SKYROOT_CHEST_BOAT.get());
                output.accept(AetherItems.BRONZE_DUNGEON_KEY.get());
                output.accept(AetherItems.SILVER_DUNGEON_KEY.get());
                output.accept(AetherItems.GOLD_DUNGEON_KEY.get());
                output.accept(AetherItems.VICTORY_MEDAL.get());
                output.accept(AetherItems.MUSIC_DISC_AETHER_TUNE.get());
                output.accept(AetherItems.MUSIC_DISC_ASCENDING_DAWN.get());
                output.accept(AetherItems.MUSIC_DISC_SLIDERS_WRATH.get());
            }).build());
    public static final CreativeModeTab AETHER_ARMOR_AND_ACCESSORIES = register("armor_and_accessories", CreativeModeTab.builder(CreativeModeTab.Row.BOTTOM, 1)
            .icon(() -> new ItemStack(AetherItems.VALKYRIE_CHESTPLATE.get()))
            .title(Component.translatable("itemGroup." + Aether.MODID + ".armor_and_accessories"))
            .displayItems((features, output) -> {
                output.accept(AetherItems.ZANITE_HELMET.get());
                output.accept(AetherItems.ZANITE_CHESTPLATE.get());
                output.accept(AetherItems.ZANITE_LEGGINGS.get());
                output.accept(AetherItems.ZANITE_BOOTS.get());
                output.accept(AetherItems.ZANITE_GLOVES.get());
                output.accept(AetherItems.GRAVITITE_HELMET.get());
                output.accept(AetherItems.GRAVITITE_CHESTPLATE.get());
                output.accept(AetherItems.GRAVITITE_LEGGINGS.get());
                output.accept(AetherItems.GRAVITITE_BOOTS.get());
                output.accept(AetherItems.GRAVITITE_GLOVES.get());
                output.accept(AetherItems.NEPTUNE_HELMET.get());
                output.accept(AetherItems.NEPTUNE_CHESTPLATE.get());
                output.accept(AetherItems.NEPTUNE_LEGGINGS.get());
                output.accept(AetherItems.NEPTUNE_BOOTS.get());
                output.accept(AetherItems.NEPTUNE_GLOVES.get());
                output.accept(AetherItems.VALKYRIE_HELMET.get());
                output.accept(AetherItems.VALKYRIE_CHESTPLATE.get());
                output.accept(AetherItems.VALKYRIE_LEGGINGS.get());
                output.accept(AetherItems.VALKYRIE_BOOTS.get());
                output.accept(AetherItems.VALKYRIE_GLOVES.get());
                output.accept(AetherItems.PHOENIX_HELMET.get());
                output.accept(AetherItems.PHOENIX_CHESTPLATE.get());
                output.accept(AetherItems.PHOENIX_LEGGINGS.get());
                output.accept(AetherItems.PHOENIX_BOOTS.get());
                output.accept(AetherItems.PHOENIX_GLOVES.get());
                output.accept(AetherItems.OBSIDIAN_HELMET.get());
                output.accept(AetherItems.OBSIDIAN_CHESTPLATE.get());
                output.accept(AetherItems.OBSIDIAN_LEGGINGS.get());
                output.accept(AetherItems.OBSIDIAN_BOOTS.get());
                output.accept(AetherItems.OBSIDIAN_GLOVES.get());
                output.accept(AetherItems.SENTRY_BOOTS.get());
                output.accept(AetherItems.IRON_RING.get());
                output.accept(AetherItems.IRON_PENDANT.get());
                output.accept(AetherItems.GOLDEN_RING.get());
                output.accept(AetherItems.GOLDEN_PENDANT.get());
                output.accept(AetherItems.ZANITE_RING.get());
                output.accept(AetherItems.ZANITE_PENDANT.get());
                output.accept(AetherItems.ICE_RING.get());
                output.accept(AetherItems.ICE_PENDANT.get());
                output.accept(AetherItems.WHITE_CAPE.get());
                output.accept(AetherItems.YELLOW_CAPE.get());
                output.accept(AetherItems.RED_CAPE.get());
                output.accept(AetherItems.BLUE_CAPE.get());
                output.accept(AetherItems.AGILITY_CAPE.get());
                output.accept(AetherItems.SWET_CAPE.get());
                output.accept(AetherItems.INVISIBILITY_CLOAK.get());
                if (AetherConfig.SERVER.spawn_valkyrie_cape.get()) {
                    output.accept(AetherItems.VALKYRIE_CAPE.get());
                }
                if (AetherConfig.SERVER.spawn_golden_feather.get()) {
                    output.accept(AetherItems.GOLDEN_FEATHER.get());
                }
                output.accept(AetherItems.REGENERATION_STONE.get());
                output.accept(AetherItems.IRON_BUBBLE.get());
                output.accept(AetherItems.SHIELD_OF_REPULSION.get());
            }).build());
    public static final CreativeModeTab AETHER_FOOD_AND_DRINKS = register("food_and_drinks", CreativeModeTab.builder(CreativeModeTab.Row.BOTTOM, 2)
            .icon(() -> new ItemStack(AetherItems.BLUE_GUMMY_SWET.get()))
            .title(Component.translatable("itemGroup." + Aether.MODID + ".food_and_drinks"))
            .displayItems((features, output) -> {
                output.accept(AetherItems.BLUE_BERRY.get());
                output.accept(AetherItems.ENCHANTED_BERRY.get());
                output.accept(AetherItems.WHITE_APPLE.get());
                if (AetherConfig.SERVER.edible_ambrosium.get()) {
                    output.accept(AetherItems.AMBROSIUM_SHARD.get());
                }
                output.accept(AetherItems.HEALING_STONE.get());
                output.accept(AetherItems.BLUE_GUMMY_SWET.get());
                output.accept(AetherItems.GOLDEN_GUMMY_SWET.get());
                output.accept(AetherItems.GINGERBREAD_MAN.get());
                output.accept(AetherItems.CANDY_CANE.get());
                output.accept(AetherItems.SKYROOT_MILK_BUCKET.get());
                output.accept(AetherItems.SKYROOT_REMEDY_BUCKET.get());
                output.accept(AetherItems.SKYROOT_POISON_BUCKET.get());
                output.accept(AetherItems.LIFE_SHARD.get());
            }).build());
    public static final CreativeModeTab AETHER_INGREDIENTS = register("ingredients", CreativeModeTab.builder(CreativeModeTab.Row.BOTTOM, 3)
            .icon(() -> new ItemStack(AetherItems.AMBROSIUM_SHARD.get()))
            .title(Component.translatable("itemGroup." + Aether.MODID + ".ingredients"))
            .displayItems((features, output) -> {
                output.accept(AetherItems.AMBROSIUM_SHARD.get());
                output.accept(AetherItems.ZANITE_GEMSTONE.get());
                output.accept(AetherBlocks.ENCHANTED_GRAVITITE);
                output.accept(AetherItems.SKYROOT_STICK.get());
                output.accept(AetherItems.GOLDEN_AMBER.get());
                output.accept(AetherItems.AECHOR_PETAL.get());
                output.accept(AetherItems.SKYROOT_POISON_BUCKET.get());
                output.accept(AetherItems.SWET_BALL.get());
            }).build());
    public static final CreativeModeTab AETHER_SPAWN_EGGS = register("spawn_eggs", CreativeModeTab.builder(CreativeModeTab.Row.BOTTOM, 4)
            .icon(() -> new ItemStack(AetherItems.AERBUNNY_SPAWN_EGG.get()))
            .title(Component.translatable("itemGroup." + Aether.MODID + ".spawn_eggs"))
            .displayItems((features, output) -> {
                output.accept(AetherItems.BLUE_MOA_EGG.get());
                output.accept(AetherItems.WHITE_MOA_EGG.get());
                output.accept(AetherItems.BLACK_MOA_EGG.get());
                output.accept(AetherItems.AECHOR_PLANT_SPAWN_EGG.get());
                output.accept(AetherItems.AERBUNNY_SPAWN_EGG.get());
                output.accept(AetherItems.AERWHALE_SPAWN_EGG.get());
                output.accept(AetherItems.BLUE_SWET_SPAWN_EGG.get());
                output.accept(AetherItems.COCKATRICE_SPAWN_EGG.get());
                output.accept(AetherItems.EVIL_WHIRLWIND_SPAWN_EGG.get());
                output.accept(AetherItems.FIRE_MINION_SPAWN_EGG.get());
                output.accept(AetherItems.FLYING_COW_SPAWN_EGG.get());
                output.accept(AetherItems.GOLDEN_SWET_SPAWN_EGG.get());
                output.accept(AetherItems.MIMIC_SPAWN_EGG.get());
                output.accept(AetherItems.MOA_SPAWN_EGG.get());
                output.accept(AetherItems.PHYG_SPAWN_EGG.get());
                output.accept(AetherItems.SENTRY_SPAWN_EGG.get());
                output.accept(AetherItems.SHEEPUFF_SPAWN_EGG.get());
                output.accept(AetherItems.WHIRLWIND_SPAWN_EGG.get());
                output.accept(AetherItems.VALKYRIE_SPAWN_EGG.get());
                output.accept(AetherItems.ZEPHYR_SPAWN_EGG.get());
            }).build());

    private static CreativeModeTab register(String name, CreativeModeTab tab) {
        return Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, Identifier.fromNamespaceAndPath(Aether.MODID, name), tab);
    }

    public static void registerVanillaTabEntries() {
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT).register(entries -> {
            entries.insertAfter(Items.LEATHER_BOOTS, AetherItems.LEATHER_GLOVES.get());
            entries.insertAfter(Items.CHAINMAIL_BOOTS, AetherItems.CHAINMAIL_GLOVES.get());
            entries.insertAfter(Items.IRON_BOOTS, AetherItems.IRON_GLOVES.get());
            entries.insertAfter(Items.GOLDEN_BOOTS, AetherItems.GOLDEN_GLOVES.get());
            entries.insertAfter(Items.DIAMOND_BOOTS, AetherItems.DIAMOND_GLOVES.get());
            entries.insertAfter(Items.NETHERITE_BOOTS, AetherItems.NETHERITE_GLOVES.get());
        });
    }
}
