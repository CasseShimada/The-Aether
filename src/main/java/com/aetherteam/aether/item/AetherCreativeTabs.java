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
                output.accept(AetherItems.AETHER_PORTAL_FRAME);
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
                output.accept(AetherItems.SKYROOT_CHEST_BOAT);
                output.accept(AetherBlocks.SKYROOT_DOOR);
                output.accept(AetherBlocks.SKYROOT_FENCE_GATE);
                output.accept(AetherBlocks.SKYROOT_TRAPDOOR);
                output.accept(AetherBlocks.ENCHANTED_GRAVITITE);
            }).build());
    public static final CreativeModeTab AETHER_EQUIPMENT_AND_UTILITIES = register("equipment_and_utilities", CreativeModeTab.builder(CreativeModeTab.Row.BOTTOM, 0)
            .icon(() -> new ItemStack(AetherItems.GRAVITITE_PICKAXE))
            .title(Component.translatable("itemGroup." + Aether.MODID + ".equipment_and_utilities"))
            .displayItems((features, output) -> {
                output.accept(AetherItems.SKYROOT_SWORD);
                output.accept(AetherItems.SKYROOT_SHOVEL);
                output.accept(AetherItems.SKYROOT_PICKAXE);
                output.accept(AetherItems.SKYROOT_AXE);
                output.accept(AetherItems.SKYROOT_HOE);
                output.accept(AetherItems.HOLYSTONE_SWORD);
                output.accept(AetherItems.HOLYSTONE_SHOVEL);
                output.accept(AetherItems.HOLYSTONE_PICKAXE);
                output.accept(AetherItems.HOLYSTONE_AXE);
                output.accept(AetherItems.HOLYSTONE_HOE);
                output.accept(AetherItems.ZANITE_SWORD);
                output.accept(AetherItems.ZANITE_SHOVEL);
                output.accept(AetherItems.ZANITE_PICKAXE);
                output.accept(AetherItems.ZANITE_AXE);
                output.accept(AetherItems.ZANITE_HOE);
                output.accept(AetherItems.GRAVITITE_SWORD);
                output.accept(AetherItems.GRAVITITE_SHOVEL);
                output.accept(AetherItems.GRAVITITE_PICKAXE);
                output.accept(AetherItems.GRAVITITE_AXE);
                output.accept(AetherItems.GRAVITITE_HOE);
                output.accept(AetherItems.VALKYRIE_LANCE);
                output.accept(AetherItems.VALKYRIE_SHOVEL);
                output.accept(AetherItems.VALKYRIE_PICKAXE);
                output.accept(AetherItems.VALKYRIE_AXE);
                output.accept(AetherItems.VALKYRIE_HOE);
                output.accept(AetherItems.GOLDEN_DART_SHOOTER);
                output.accept(AetherItems.GOLDEN_DART);
                output.accept(AetherItems.POISON_DART_SHOOTER);
                output.accept(AetherItems.POISON_DART);
                output.accept(AetherItems.ENCHANTED_DART_SHOOTER);
                output.accept(AetherItems.ENCHANTED_DART);
                output.accept(AetherItems.CANDY_CANE_SWORD);
                output.accept(AetherItems.HOLY_SWORD);
                output.accept(AetherItems.VAMPIRE_BLADE);
                output.accept(AetherItems.LIGHTNING_SWORD);
                output.accept(AetherItems.LIGHTNING_KNIFE);
                output.accept(AetherItems.FLAMING_SWORD);
                output.accept(AetherItems.PHOENIX_BOW);
                output.accept(AetherItems.PIG_SLAYER);
                output.accept(AetherItems.HAMMER_OF_KINGBDOGZ);
                output.accept(AetherItems.CLOUD_STAFF);
                output.accept(AetherItems.SKYROOT_BUCKET);
                output.accept(AetherItems.SKYROOT_WATER_BUCKET);
                output.accept(AetherItems.SKYROOT_PUFFERFISH_BUCKET);
                output.accept(AetherItems.SKYROOT_SALMON_BUCKET);
                output.accept(AetherItems.SKYROOT_COD_BUCKET);
                output.accept(AetherItems.SKYROOT_TROPICAL_FISH_BUCKET);
                output.accept(AetherItems.SKYROOT_AXOLOTL_BUCKET);
                output.accept(AetherItems.SKYROOT_TADPOLE_BUCKET);
                output.accept(AetherItems.SKYROOT_POWDER_SNOW_BUCKET);
                output.accept(AetherItems.SKYROOT_MILK_BUCKET);
                output.accept(AetherItems.SKYROOT_REMEDY_BUCKET);
                output.accept(AetherItems.SKYROOT_POISON_BUCKET);
                output.accept(AetherItems.BOOK_OF_LORE);
                output.accept(AetherItems.COLD_PARACHUTE);
                output.accept(AetherItems.GOLDEN_PARACHUTE);
                output.accept(AetherItems.AMBROSIUM_SHARD);
                output.accept(AetherItems.SWET_BALL);
                output.accept(AetherItems.BLUE_MOA_EGG);
                output.accept(AetherItems.WHITE_MOA_EGG);
                output.accept(AetherItems.BLACK_MOA_EGG);
                output.accept(AetherItems.NATURE_STAFF);
                output.accept(AetherItems.SKYROOT_BOAT);
                output.accept(AetherItems.SKYROOT_CHEST_BOAT);
                output.accept(AetherItems.BRONZE_DUNGEON_KEY);
                output.accept(AetherItems.SILVER_DUNGEON_KEY);
                output.accept(AetherItems.GOLD_DUNGEON_KEY);
                output.accept(AetherItems.VICTORY_MEDAL);
                output.accept(AetherItems.MUSIC_DISC_AETHER_TUNE);
                output.accept(AetherItems.MUSIC_DISC_ASCENDING_DAWN);
                output.accept(AetherItems.MUSIC_DISC_SLIDERS_WRATH);
            }).build());
    public static final CreativeModeTab AETHER_ARMOR_AND_ACCESSORIES = register("armor_and_accessories", CreativeModeTab.builder(CreativeModeTab.Row.BOTTOM, 1)
            .icon(() -> new ItemStack(AetherItems.VALKYRIE_CHESTPLATE))
            .title(Component.translatable("itemGroup." + Aether.MODID + ".armor_and_accessories"))
            .displayItems((features, output) -> {
                output.accept(AetherItems.ZANITE_HELMET);
                output.accept(AetherItems.ZANITE_CHESTPLATE);
                output.accept(AetherItems.ZANITE_LEGGINGS);
                output.accept(AetherItems.ZANITE_BOOTS);
                output.accept(AetherItems.ZANITE_GLOVES);
                output.accept(AetherItems.GRAVITITE_HELMET);
                output.accept(AetherItems.GRAVITITE_CHESTPLATE);
                output.accept(AetherItems.GRAVITITE_LEGGINGS);
                output.accept(AetherItems.GRAVITITE_BOOTS);
                output.accept(AetherItems.GRAVITITE_GLOVES);
                output.accept(AetherItems.NEPTUNE_HELMET);
                output.accept(AetherItems.NEPTUNE_CHESTPLATE);
                output.accept(AetherItems.NEPTUNE_LEGGINGS);
                output.accept(AetherItems.NEPTUNE_BOOTS);
                output.accept(AetherItems.NEPTUNE_GLOVES);
                output.accept(AetherItems.VALKYRIE_HELMET);
                output.accept(AetherItems.VALKYRIE_CHESTPLATE);
                output.accept(AetherItems.VALKYRIE_LEGGINGS);
                output.accept(AetherItems.VALKYRIE_BOOTS);
                output.accept(AetherItems.VALKYRIE_GLOVES);
                output.accept(AetherItems.PHOENIX_HELMET);
                output.accept(AetherItems.PHOENIX_CHESTPLATE);
                output.accept(AetherItems.PHOENIX_LEGGINGS);
                output.accept(AetherItems.PHOENIX_BOOTS);
                output.accept(AetherItems.PHOENIX_GLOVES);
                output.accept(AetherItems.OBSIDIAN_HELMET);
                output.accept(AetherItems.OBSIDIAN_CHESTPLATE);
                output.accept(AetherItems.OBSIDIAN_LEGGINGS);
                output.accept(AetherItems.OBSIDIAN_BOOTS);
                output.accept(AetherItems.OBSIDIAN_GLOVES);
                output.accept(AetherItems.SENTRY_BOOTS);
                output.accept(AetherItems.IRON_RING);
                output.accept(AetherItems.IRON_PENDANT);
                output.accept(AetherItems.GOLDEN_RING);
                output.accept(AetherItems.GOLDEN_PENDANT);
                output.accept(AetherItems.ZANITE_RING);
                output.accept(AetherItems.ZANITE_PENDANT);
                output.accept(AetherItems.ICE_RING);
                output.accept(AetherItems.ICE_PENDANT);
                output.accept(AetherItems.WHITE_CAPE);
                output.accept(AetherItems.YELLOW_CAPE);
                output.accept(AetherItems.RED_CAPE);
                output.accept(AetherItems.BLUE_CAPE);
                output.accept(AetherItems.AGILITY_CAPE);
                output.accept(AetherItems.SWET_CAPE);
                output.accept(AetherItems.INVISIBILITY_CLOAK);
                if (AetherConfig.SERVER.spawn_valkyrie_cape.get()) {
                    output.accept(AetherItems.VALKYRIE_CAPE);
                }
                if (AetherConfig.SERVER.spawn_golden_feather.get()) {
                    output.accept(AetherItems.GOLDEN_FEATHER);
                }
                output.accept(AetherItems.REGENERATION_STONE);
                output.accept(AetherItems.IRON_BUBBLE);
                output.accept(AetherItems.SHIELD_OF_REPULSION);
            }).build());
    public static final CreativeModeTab AETHER_FOOD_AND_DRINKS = register("food_and_drinks", CreativeModeTab.builder(CreativeModeTab.Row.BOTTOM, 2)
            .icon(() -> new ItemStack(AetherItems.BLUE_GUMMY_SWET))
            .title(Component.translatable("itemGroup." + Aether.MODID + ".food_and_drinks"))
            .displayItems((features, output) -> {
                output.accept(AetherItems.BLUE_BERRY);
                output.accept(AetherItems.ENCHANTED_BERRY);
                output.accept(AetherItems.WHITE_APPLE);
                if (AetherConfig.SERVER.edible_ambrosium.get()) {
                    output.accept(AetherItems.AMBROSIUM_SHARD);
                }
                output.accept(AetherItems.HEALING_STONE);
                output.accept(AetherItems.BLUE_GUMMY_SWET);
                output.accept(AetherItems.GOLDEN_GUMMY_SWET);
                output.accept(AetherItems.GINGERBREAD_MAN);
                output.accept(AetherItems.CANDY_CANE);
                output.accept(AetherItems.SKYROOT_MILK_BUCKET);
                output.accept(AetherItems.SKYROOT_REMEDY_BUCKET);
                output.accept(AetherItems.SKYROOT_POISON_BUCKET);
                output.accept(AetherItems.LIFE_SHARD);
            }).build());
    public static final CreativeModeTab AETHER_INGREDIENTS = register("ingredients", CreativeModeTab.builder(CreativeModeTab.Row.BOTTOM, 3)
            .icon(() -> new ItemStack(AetherItems.AMBROSIUM_SHARD))
            .title(Component.translatable("itemGroup." + Aether.MODID + ".ingredients"))
            .displayItems((features, output) -> {
                output.accept(AetherItems.AMBROSIUM_SHARD);
                output.accept(AetherItems.ZANITE_GEMSTONE);
                output.accept(AetherBlocks.ENCHANTED_GRAVITITE);
                output.accept(AetherItems.SKYROOT_STICK);
                output.accept(AetherItems.GOLDEN_AMBER);
                output.accept(AetherItems.AECHOR_PETAL);
                output.accept(AetherItems.SKYROOT_POISON_BUCKET);
                output.accept(AetherItems.SWET_BALL);
            }).build());
    public static final CreativeModeTab AETHER_SPAWN_EGGS = register("spawn_eggs", CreativeModeTab.builder(CreativeModeTab.Row.BOTTOM, 4)
            .icon(() -> new ItemStack(AetherItems.AERBUNNY_SPAWN_EGG))
            .title(Component.translatable("itemGroup." + Aether.MODID + ".spawn_eggs"))
            .displayItems((features, output) -> {
                output.accept(AetherItems.BLUE_MOA_EGG);
                output.accept(AetherItems.WHITE_MOA_EGG);
                output.accept(AetherItems.BLACK_MOA_EGG);
                output.accept(AetherItems.AECHOR_PLANT_SPAWN_EGG);
                output.accept(AetherItems.AERBUNNY_SPAWN_EGG);
                output.accept(AetherItems.AERWHALE_SPAWN_EGG);
                output.accept(AetherItems.BLUE_SWET_SPAWN_EGG);
                output.accept(AetherItems.COCKATRICE_SPAWN_EGG);
                output.accept(AetherItems.EVIL_WHIRLWIND_SPAWN_EGG);
                output.accept(AetherItems.FIRE_MINION_SPAWN_EGG);
                output.accept(AetherItems.FLYING_COW_SPAWN_EGG);
                output.accept(AetherItems.GOLDEN_SWET_SPAWN_EGG);
                output.accept(AetherItems.MIMIC_SPAWN_EGG);
                output.accept(AetherItems.MOA_SPAWN_EGG);
                output.accept(AetherItems.PHYG_SPAWN_EGG);
                output.accept(AetherItems.SENTRY_SPAWN_EGG);
                output.accept(AetherItems.SHEEPUFF_SPAWN_EGG);
                output.accept(AetherItems.WHIRLWIND_SPAWN_EGG);
                output.accept(AetherItems.VALKYRIE_SPAWN_EGG);
                output.accept(AetherItems.ZEPHYR_SPAWN_EGG);
            }).build());

    private static CreativeModeTab register(String name, CreativeModeTab tab) {
        return Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, Identifier.fromNamespaceAndPath(Aether.MODID, name), tab);
    }

    public static void registerVanillaTabEntries() {
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT).register(entries -> {
            entries.insertAfter(Items.LEATHER_BOOTS, AetherItems.LEATHER_GLOVES);
            entries.insertAfter(Items.CHAINMAIL_BOOTS, AetherItems.CHAINMAIL_GLOVES);
            entries.insertAfter(Items.IRON_BOOTS, AetherItems.IRON_GLOVES);
            entries.insertAfter(Items.GOLDEN_BOOTS, AetherItems.GOLDEN_GLOVES);
            entries.insertAfter(Items.DIAMOND_BOOTS, AetherItems.DIAMOND_GLOVES);
            entries.insertAfter(Items.NETHERITE_BOOTS, AetherItems.NETHERITE_GLOVES);
        });
    }
}
