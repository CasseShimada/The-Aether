package com.aetherteam.aether.item;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.client.AetherSoundEvents;
import com.aetherteam.aether.data.resources.registries.AetherJukeboxSongs;
import com.aetherteam.aether.data.resources.registries.AetherMoaTypes;
import com.aetherteam.aether.entity.AetherEntityTypes;
import com.aetherteam.aether.item.accessories.cape.AgilityCapeItem;
import com.aetherteam.aether.item.accessories.cape.CapeItem;
import com.aetherteam.aether.item.accessories.cape.InvisibilityCloakItem;
import com.aetherteam.aether.item.accessories.cape.ValkyrieCapeItem;
import com.aetherteam.aether.item.accessories.gloves.GlovesItem;
import com.aetherteam.aether.item.accessories.gloves.GoldGlovesItem;
import com.aetherteam.aether.item.accessories.gloves.LeatherGlovesItem;
import com.aetherteam.aether.item.accessories.gloves.ZaniteGlovesItem;
import com.aetherteam.aether.item.accessories.miscellaneous.GoldenFeatherItem;
import com.aetherteam.aether.item.accessories.miscellaneous.IronBubbleItem;
import com.aetherteam.aether.item.accessories.miscellaneous.RegenerationStoneItem;
import com.aetherteam.aether.item.accessories.miscellaneous.ShieldOfRepulsionItem;
import com.aetherteam.aether.item.accessories.pendant.IcePendantItem;
import com.aetherteam.aether.item.accessories.pendant.PendantItem;
import com.aetherteam.aether.item.accessories.pendant.ZanitePendantItem;
import com.aetherteam.aether.item.accessories.ring.IceRingItem;
import com.aetherteam.aether.item.accessories.ring.RingItem;
import com.aetherteam.aether.item.accessories.ring.ZaniteRingItem;
import com.aetherteam.aether.item.combat.*;
import com.aetherteam.aether.item.combat.loot.*;
import com.aetherteam.aether.item.components.AetherDataComponents;
import com.aetherteam.aether.item.components.DungeonKind;
import com.aetherteam.aether.item.food.AetherFoods;
import com.aetherteam.aether.item.food.GummySwetItem;
import com.aetherteam.aether.item.food.HealingStoneItem;
import com.aetherteam.aether.item.food.WhiteAppleItem;
import com.aetherteam.aether.item.materials.AmbrosiumShardItem;
import com.aetherteam.aether.item.materials.SwetBallItem;
import com.aetherteam.aether.item.miscellaneous.*;
import com.aetherteam.aether.item.miscellaneous.bucket.*;
import com.aetherteam.aether.item.tools.gravitite.GravititeAxeItem;
import com.aetherteam.aether.item.tools.gravitite.GravititeHoeItem;
import com.aetherteam.aether.item.tools.gravitite.GravititePickaxeItem;
import com.aetherteam.aether.item.tools.gravitite.GravititeShovelItem;
import com.aetherteam.aether.item.tools.holystone.HolystoneAxeItem;
import com.aetherteam.aether.item.tools.holystone.HolystoneHoeItem;
import com.aetherteam.aether.item.tools.holystone.HolystonePickaxeItem;
import com.aetherteam.aether.item.tools.holystone.HolystoneShovelItem;
import com.aetherteam.aether.item.tools.skyroot.SkyrootAxeItem;
import com.aetherteam.aether.item.tools.skyroot.SkyrootHoeItem;
import com.aetherteam.aether.item.tools.skyroot.SkyrootPickaxeItem;
import com.aetherteam.aether.item.tools.skyroot.SkyrootShovelItem;
import com.aetherteam.aether.item.tools.valkyrie.ValkyrieAxeItem;
import com.aetherteam.aether.item.tools.valkyrie.ValkyrieHoeItem;
import com.aetherteam.aether.item.tools.valkyrie.ValkyriePickaxeItem;
import com.aetherteam.aether.item.tools.valkyrie.ValkyrieShovelItem;
import com.aetherteam.aether.item.tools.zanite.ZaniteAxeItem;
import com.aetherteam.aether.item.tools.zanite.ZaniteHoeItem;
import com.aetherteam.aether.item.tools.zanite.ZanitePickaxeItem;
import com.aetherteam.aether.item.tools.zanite.ZaniteShovelItem;
import com.aetherteam.aether.registry.RegistryConstructionContext;
import com.aetherteam.aether.accessories.api.AccessoriesAPI;
import com.aetherteam.aether.accessories.api.core.Accessory;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.equipment.ArmorMaterials;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import net.minecraft.world.level.block.entity.BannerPatterns;
import net.minecraft.world.level.material.Fluids;

import java.util.function.Function;
import java.util.function.Supplier;

public class AetherItems {
    public static final Rarity AETHER_LOOT = Rarity.EPIC;

    public static final Component BRONZE_DUNGEON_TOOLTIP = Component.translatable("aether.dungeon.bronze_dungeon").withStyle(Style.EMPTY.withItalic(true).withColor(TextColor.parseColor("#D9AB7E").result().get()));
    public static final Component SILVER_DUNGEON_TOOLTIP = Component.translatable("aether.dungeon.silver_dungeon").withStyle(Style.EMPTY.withItalic(true).withColor(TextColor.parseColor("#E0E0E0").result().get()));
    public static final Component GOLD_DUNGEON_TOOLTIP = Component.translatable("aether.dungeon.gold_dungeon").withStyle(Style.EMPTY.withItalic(true).withColor(TextColor.parseColor("#FDF55F").result().get()));

    // Tools
    public static final PickaxeItem SKYROOT_PICKAXE = register("skyroot_pickaxe", properties -> new SkyrootPickaxeItem(properties));
    public static final AxeItem SKYROOT_AXE = register("skyroot_axe", properties -> new SkyrootAxeItem(properties));
    public static final ShovelItem SKYROOT_SHOVEL = register("skyroot_shovel", properties -> new SkyrootShovelItem(properties));
    public static final HoeItem SKYROOT_HOE = register("skyroot_hoe", SkyrootHoeItem::new);

    public static final PickaxeItem HOLYSTONE_PICKAXE = register("holystone_pickaxe", HolystonePickaxeItem::new);
    public static final AxeItem HOLYSTONE_AXE = register("holystone_axe", HolystoneAxeItem::new);
    public static final ShovelItem HOLYSTONE_SHOVEL = register("holystone_shovel", HolystoneShovelItem::new);
    public static final HoeItem HOLYSTONE_HOE = register("holystone_hoe", HolystoneHoeItem::new);

    public static final PickaxeItem ZANITE_PICKAXE = register("zanite_pickaxe", ZanitePickaxeItem::new);
    public static final AxeItem ZANITE_AXE = register("zanite_axe", ZaniteAxeItem::new);
    public static final ShovelItem ZANITE_SHOVEL = register("zanite_shovel", ZaniteShovelItem::new);
    public static final HoeItem ZANITE_HOE = register("zanite_hoe", ZaniteHoeItem::new);

    public static final PickaxeItem GRAVITITE_PICKAXE = register("gravitite_pickaxe", GravititePickaxeItem::new);
    public static final AxeItem GRAVITITE_AXE = register("gravitite_axe", GravititeAxeItem::new);
    public static final ShovelItem GRAVITITE_SHOVEL = register("gravitite_shovel", GravititeShovelItem::new);
    public static final HoeItem GRAVITITE_HOE = register("gravitite_hoe", GravititeHoeItem::new);

    public static final PickaxeItem VALKYRIE_PICKAXE = register("valkyrie_pickaxe", ValkyriePickaxeItem::new);
    public static final AxeItem VALKYRIE_AXE = register("valkyrie_axe", ValkyrieAxeItem::new);
    public static final ShovelItem VALKYRIE_SHOVEL = register("valkyrie_shovel", ValkyrieShovelItem::new);
    public static final HoeItem VALKYRIE_HOE = register("valkyrie_hoe", ValkyrieHoeItem::new);

    // Weapons
    public static final SwordItem SKYROOT_SWORD = register("skyroot_sword", properties -> new SkyrootSwordItem(properties));
    public static final SwordItem HOLYSTONE_SWORD = register("holystone_sword", properties -> new HolystoneSwordItem(properties));
    public static final SwordItem ZANITE_SWORD = register("zanite_sword", properties -> new ZaniteSwordItem(properties));
    public static final SwordItem GRAVITITE_SWORD = register("gravitite_sword", properties -> new GravititeSwordItem(properties));

    public static final SwordItem VALKYRIE_LANCE = register("valkyrie_lance", properties -> new ValkyrieLanceItem(properties.rarity(AETHER_LOOT)));

    public static final SwordItem FLAMING_SWORD = register("flaming_sword", properties -> new FlamingSwordItem(properties.rarity(AETHER_LOOT)));
    public static final SwordItem LIGHTNING_SWORD = register("lightning_sword", properties -> new LightningSwordItem(properties.rarity(AETHER_LOOT)));
    public static final SwordItem HOLY_SWORD = register("holy_sword", properties -> new HolySwordItem(properties.rarity(AETHER_LOOT)));
    public static final SwordItem VAMPIRE_BLADE = register("vampire_blade", properties -> new VampireBladeItem(properties.rarity(AETHER_LOOT)));
    public static final SwordItem PIG_SLAYER = register("pig_slayer", properties -> new PigSlayerItem(properties.rarity(AETHER_LOOT)));
    public static final SwordItem CANDY_CANE_SWORD = register("candy_cane_sword", properties -> new CandyCaneSwordItem(properties));

    public static final SwordItem HAMMER_OF_KINGBDOGZ = register("hammer_of_kingbdogz", properties -> new HammerOfKingbdogzItem(properties.rarity(AETHER_LOOT)));

    public static final Item LIGHTNING_KNIFE = register("lightning_knife", properties -> new LightningKnifeItem(properties.rarity(AETHER_LOOT).stacksTo(16)));

    public static final Item GOLDEN_DART = register("golden_dart", GoldenDartItem::new);
    public static final Item POISON_DART = register("poison_dart", PoisonDartItem::new);
    public static final Item ENCHANTED_DART = register("enchanted_dart", properties -> new EnchantedDartItem(properties.rarity(Rarity.RARE)));

    public static final Item GOLDEN_DART_SHOOTER = register("golden_dart_shooter", properties -> new DartShooterItem(() -> GOLDEN_DART, properties.stacksTo(1)));
    public static final Item POISON_DART_SHOOTER = register("poison_dart_shooter", properties -> new DartShooterItem(() -> POISON_DART, properties.stacksTo(1)));
    public static final Item ENCHANTED_DART_SHOOTER = register("enchanted_dart_shooter", properties -> new DartShooterItem(() -> ENCHANTED_DART, properties.stacksTo(1).rarity(Rarity.RARE)));

    public static final BowItem PHOENIX_BOW = register("phoenix_bow", properties -> new PhoenixBowItem(properties.durability(384).rarity(AETHER_LOOT)));

    // Armor
    public static final Item ZANITE_HELMET = register("zanite_helmet", properties -> new ArmorItem(() -> AetherArmorMaterials.ZANITE.value(), ArmorItem.Type.HELMET, properties.durability(ArmorItem.Type.HELMET.getDurability(15))));
    public static final Item ZANITE_CHESTPLATE = register("zanite_chestplate", properties -> new ArmorItem(() -> AetherArmorMaterials.ZANITE.value(), ArmorItem.Type.CHESTPLATE, properties.durability(ArmorItem.Type.CHESTPLATE.getDurability(15))));
    public static final Item ZANITE_LEGGINGS = register("zanite_leggings", properties -> new ArmorItem(() -> AetherArmorMaterials.ZANITE.value(), ArmorItem.Type.LEGGINGS, properties.durability(ArmorItem.Type.LEGGINGS.getDurability(15))));
    public static final Item ZANITE_BOOTS = register("zanite_boots", properties -> new ArmorItem(() -> AetherArmorMaterials.ZANITE.value(), ArmorItem.Type.BOOTS, properties.durability(ArmorItem.Type.BOOTS.getDurability(15))));

    public static final Item GRAVITITE_HELMET = register("gravitite_helmet", properties -> new ArmorItem(() -> AetherArmorMaterials.GRAVITITE.value(), ArmorItem.Type.HELMET, properties.durability(ArmorItem.Type.HELMET.getDurability(33))));
    public static final Item GRAVITITE_CHESTPLATE = register("gravitite_chestplate", properties -> new ArmorItem(() -> AetherArmorMaterials.GRAVITITE.value(), ArmorItem.Type.CHESTPLATE, properties.durability(ArmorItem.Type.CHESTPLATE.getDurability(33))));
    public static final Item GRAVITITE_LEGGINGS = register("gravitite_leggings", properties -> new ArmorItem(() -> AetherArmorMaterials.GRAVITITE.value(), ArmorItem.Type.LEGGINGS, properties.durability(ArmorItem.Type.LEGGINGS.getDurability(33))));
    public static final Item GRAVITITE_BOOTS = register("gravitite_boots", properties -> new ArmorItem(() -> AetherArmorMaterials.GRAVITITE.value(), ArmorItem.Type.BOOTS, properties.durability(ArmorItem.Type.BOOTS.getDurability(33))));

    public static final Item VALKYRIE_HELMET = register("valkyrie_helmet", properties -> new ArmorItem(() -> AetherArmorMaterials.VALKYRIE.value(), ArmorItem.Type.HELMET, properties.durability(ArmorItem.Type.HELMET.getDurability(33)).rarity(AETHER_LOOT)));
    public static final Item VALKYRIE_CHESTPLATE = register("valkyrie_chestplate", properties -> new ArmorItem(() -> AetherArmorMaterials.VALKYRIE.value(), ArmorItem.Type.CHESTPLATE, properties.durability(ArmorItem.Type.CHESTPLATE.getDurability(33)).rarity(AETHER_LOOT)));
    public static final Item VALKYRIE_LEGGINGS = register("valkyrie_leggings", properties -> new ArmorItem(() -> AetherArmorMaterials.VALKYRIE.value(), ArmorItem.Type.LEGGINGS, properties.durability(ArmorItem.Type.LEGGINGS.getDurability(33)).rarity(AETHER_LOOT)));
    public static final Item VALKYRIE_BOOTS = register("valkyrie_boots", properties -> new ArmorItem(() -> AetherArmorMaterials.VALKYRIE.value(), ArmorItem.Type.BOOTS, properties.durability(ArmorItem.Type.BOOTS.getDurability(33)).rarity(AETHER_LOOT)));

    public static final Item NEPTUNE_HELMET = register("neptune_helmet", properties -> new ArmorItem(() -> AetherArmorMaterials.NEPTUNE.value(), ArmorItem.Type.HELMET, properties.durability(ArmorItem.Type.HELMET.getDurability(15)).rarity(AETHER_LOOT)));
    public static final Item NEPTUNE_CHESTPLATE = register("neptune_chestplate", properties -> new ArmorItem(() -> AetherArmorMaterials.NEPTUNE.value(), ArmorItem.Type.CHESTPLATE, properties.durability(ArmorItem.Type.CHESTPLATE.getDurability(15)).rarity(AETHER_LOOT)));
    public static final Item NEPTUNE_LEGGINGS = register("neptune_leggings", properties -> new ArmorItem(() -> AetherArmorMaterials.NEPTUNE.value(), ArmorItem.Type.LEGGINGS, properties.durability(ArmorItem.Type.LEGGINGS.getDurability(15)).rarity(AETHER_LOOT)));
    public static final Item NEPTUNE_BOOTS = register("neptune_boots", properties -> new ArmorItem(() -> AetherArmorMaterials.NEPTUNE.value(), ArmorItem.Type.BOOTS, properties.durability(ArmorItem.Type.BOOTS.getDurability(15)).rarity(AETHER_LOOT)));

    public static final Item PHOENIX_HELMET = register("phoenix_helmet", properties -> new ArmorItem(() -> AetherArmorMaterials.PHOENIX.value(), ArmorItem.Type.HELMET, properties.durability(ArmorItem.Type.HELMET.getDurability(33)).rarity(AETHER_LOOT).fireResistant()));
    public static final Item PHOENIX_CHESTPLATE = register("phoenix_chestplate", properties -> new ArmorItem(() -> AetherArmorMaterials.PHOENIX.value(), ArmorItem.Type.CHESTPLATE, properties.durability(ArmorItem.Type.CHESTPLATE.getDurability(33)).rarity(AETHER_LOOT).fireResistant()));
    public static final Item PHOENIX_LEGGINGS = register("phoenix_leggings", properties -> new ArmorItem(() -> AetherArmorMaterials.PHOENIX.value(), ArmorItem.Type.LEGGINGS, properties.durability(ArmorItem.Type.LEGGINGS.getDurability(33)).rarity(AETHER_LOOT).fireResistant()));
    public static final Item PHOENIX_BOOTS = register("phoenix_boots", properties -> new ArmorItem(() -> AetherArmorMaterials.PHOENIX.value(), ArmorItem.Type.BOOTS, properties.durability(ArmorItem.Type.BOOTS.getDurability(33)).rarity(AETHER_LOOT).fireResistant()));

    public static final Item OBSIDIAN_HELMET = register("obsidian_helmet", properties -> new ArmorItem(() -> AetherArmorMaterials.OBSIDIAN.value(), ArmorItem.Type.HELMET, properties.durability(ArmorItem.Type.HELMET.getDurability(37)).rarity(AETHER_LOOT)));
    public static final Item OBSIDIAN_CHESTPLATE = register("obsidian_chestplate", properties -> new ArmorItem(() -> AetherArmorMaterials.OBSIDIAN.value(), ArmorItem.Type.CHESTPLATE, properties.durability(ArmorItem.Type.CHESTPLATE.getDurability(37)).rarity(AETHER_LOOT)));
    public static final Item OBSIDIAN_LEGGINGS = register("obsidian_leggings", properties -> new ArmorItem(() -> AetherArmorMaterials.OBSIDIAN.value(), ArmorItem.Type.LEGGINGS, properties.durability(ArmorItem.Type.LEGGINGS.getDurability(37)).rarity(AETHER_LOOT)));
    public static final Item OBSIDIAN_BOOTS = register("obsidian_boots", properties -> new ArmorItem(() -> AetherArmorMaterials.OBSIDIAN.value(), ArmorItem.Type.BOOTS, properties.durability(ArmorItem.Type.BOOTS.getDurability(37)).rarity(AETHER_LOOT)));

    public static final Item SENTRY_BOOTS = register("sentry_boots", properties -> new ArmorItem(() -> AetherArmorMaterials.SENTRY.value(), ArmorItem.Type.BOOTS, properties.durability(ArmorItem.Type.BOOTS.getDurability(15)).rarity(AETHER_LOOT)));

    // Food
    public static final Item BLUE_BERRY = register("blue_berry", properties -> new Item(properties.food(AetherFoods.BLUE_BERRY)));
    public static final Item ENCHANTED_BERRY = register("enchanted_berry", properties -> new Item(properties.rarity(Rarity.RARE).food(AetherFoods.ENCHANTED_BERRY)));
    public static final Item WHITE_APPLE = register("white_apple", properties -> new WhiteAppleItem(properties.food(AetherFoods.WHITE_APPLE)));
    public static final Item BLUE_GUMMY_SWET = register("blue_gummy_swet", properties -> new GummySwetItem(properties.rarity(AETHER_LOOT).food(AetherFoods.GUMMY_SWET)));
    public static final Item GOLDEN_GUMMY_SWET = register("golden_gummy_swet", properties -> new GummySwetItem(properties.rarity(AETHER_LOOT).food(AetherFoods.GUMMY_SWET)));
    public static final Item HEALING_STONE = register("healing_stone", properties -> new HealingStoneItem(properties.rarity(Rarity.RARE).food(AetherFoods.HEALING_STONE)));
    public static final Item CANDY_CANE = register("candy_cane", properties -> new Item(properties.food(AetherFoods.CANDY_CANE)));
    public static final Item GINGERBREAD_MAN = register("gingerbread_man", properties -> new Item(properties.food(AetherFoods.GINGERBREAD_MAN)));

    // Accessories
    public static final Item IRON_RING = register("iron_ring", properties -> new RingItem(AetherSoundEvents.ITEM_ACCESSORY_EQUIP_IRON_RING, properties.stacksTo(1)));
    public static final Item GOLDEN_RING = register("golden_ring", properties -> new RingItem(AetherSoundEvents.ITEM_ACCESSORY_EQUIP_GOLD_RING, properties.stacksTo(1)));
    public static final Item ZANITE_RING = register("zanite_ring", properties -> new ZaniteRingItem(properties.durability(49)));
    public static final Item ICE_RING = register("ice_ring", properties -> new IceRingItem(properties.durability(125)));

    public static final Item IRON_PENDANT = register("iron_pendant", properties -> new PendantItem("iron_pendant", AetherSoundEvents.ITEM_ACCESSORY_EQUIP_IRON_PENDANT, properties.stacksTo(1)));
    public static final Item GOLDEN_PENDANT = register("golden_pendant", properties -> new PendantItem("golden_pendant", AetherSoundEvents.ITEM_ACCESSORY_EQUIP_GOLD_PENDANT, properties.stacksTo(1)));
    public static final Item ZANITE_PENDANT = register("zanite_pendant", properties -> new ZanitePendantItem(properties.durability(98)));
    public static final Item ICE_PENDANT = register("ice_pendant", properties -> new IcePendantItem(properties.durability(250)));

    public static final Item LEATHER_GLOVES = register("leather_gloves", properties -> new LeatherGlovesItem(0.25, properties.durability(59)));
    public static final Item CHAINMAIL_GLOVES = register("chainmail_gloves", properties -> new GlovesItem(ArmorMaterials.CHAINMAIL, 0.35, "chainmail_gloves", SoundEvents.ARMOR_EQUIP_CHAIN, properties.durability(131)));
    public static final Item IRON_GLOVES = register("iron_gloves", properties -> new GlovesItem(ArmorMaterials.IRON, 0.5, "iron_gloves", SoundEvents.ARMOR_EQUIP_IRON, properties.durability(250)));
    public static final Item GOLDEN_GLOVES = register("golden_gloves", properties -> new GoldGlovesItem(0.25, properties.durability(32)));
    public static final Item DIAMOND_GLOVES = register("diamond_gloves", properties -> new GlovesItem(ArmorMaterials.DIAMOND, 0.75, "diamond_gloves", SoundEvents.ARMOR_EQUIP_DIAMOND, properties.durability(1561)));
    public static final Item NETHERITE_GLOVES = register("netherite_gloves", properties -> new GlovesItem(ArmorMaterials.NETHERITE, 1.0, "netherite_gloves", SoundEvents.ARMOR_EQUIP_NETHERITE, properties.durability(2031).fireResistant()));
    public static final Item ZANITE_GLOVES = register("zanite_gloves", properties -> new ZaniteGlovesItem(0.5, properties.durability(250)));
    public static final Item GRAVITITE_GLOVES = register("gravitite_gloves", properties -> new GlovesItem(AetherArmorMaterials.GRAVITITE, 0.75, "gravitite_gloves", AetherSoundEvents.ITEM_ARMOR_EQUIP_GRAVITITE, properties.durability(1561)));
    public static final Item VALKYRIE_GLOVES = register("valkyrie_gloves", properties -> new GlovesItem(AetherArmorMaterials.VALKYRIE, 1.0, "valkyrie_gloves", AetherSoundEvents.ITEM_ARMOR_EQUIP_VALKYRIE, properties.stacksTo(1).rarity(AETHER_LOOT).durability(1561)));
    public static final Item NEPTUNE_GLOVES = register("neptune_gloves", properties -> new GlovesItem(AetherArmorMaterials.NEPTUNE, 0.5, "neptune_gloves", AetherSoundEvents.ITEM_ARMOR_EQUIP_NEPTUNE, properties.stacksTo(1).rarity(AETHER_LOOT).durability(250)));
    public static final Item PHOENIX_GLOVES = register("phoenix_gloves", properties -> new GlovesItem(AetherArmorMaterials.PHOENIX, 1.0, "phoenix_gloves", AetherSoundEvents.ITEM_ARMOR_EQUIP_PHOENIX, properties.stacksTo(1).rarity(AETHER_LOOT).fireResistant().durability(1561)));
    public static final Item OBSIDIAN_GLOVES = register("obsidian_gloves", properties -> new GlovesItem(AetherArmorMaterials.OBSIDIAN, 1.0, "obsidian_gloves", AetherSoundEvents.ITEM_ARMOR_EQUIP_OBSIDIAN, properties.stacksTo(1).rarity(AETHER_LOOT).durability(2031)));

    public static final Item RED_CAPE = register("red_cape", properties -> new CapeItem("red_cape", properties.stacksTo(1)));
    public static final Item BLUE_CAPE = register("blue_cape", properties -> new CapeItem("blue_cape", properties.stacksTo(1)));
    public static final Item YELLOW_CAPE = register("yellow_cape", properties -> new CapeItem("yellow_cape", properties.stacksTo(1)));
    public static final Item WHITE_CAPE = register("white_cape", properties -> new CapeItem("white_cape", properties.stacksTo(1)));
    public static final Item AGILITY_CAPE = register("agility_cape", properties -> new AgilityCapeItem("agility_cape", properties.stacksTo(1).rarity(AETHER_LOOT)));
    public static final Item SWET_CAPE = register("swet_cape", properties -> new CapeItem("swet_cape", properties.stacksTo(1)));
    public static final Item INVISIBILITY_CLOAK = register("invisibility_cloak", properties -> new InvisibilityCloakItem(properties.stacksTo(1).rarity(AETHER_LOOT)));
    public static final Item VALKYRIE_CAPE = register("valkyrie_cape", properties -> new ValkyrieCapeItem(properties.stacksTo(1).rarity(AETHER_LOOT)));

    public static final Item GOLDEN_FEATHER = register("golden_feather", properties -> new GoldenFeatherItem(properties.stacksTo(1).rarity(AETHER_LOOT)));
    public static final Item REGENERATION_STONE = register("regeneration_stone", properties -> new RegenerationStoneItem(properties.stacksTo(1).rarity(AETHER_LOOT)));
    public static final Item IRON_BUBBLE = register("iron_bubble", properties -> new IronBubbleItem(properties.stacksTo(1).rarity(AETHER_LOOT)));
    public static final Item SHIELD_OF_REPULSION = register("shield_of_repulsion", properties -> new ShieldOfRepulsionItem(properties.durability(512).rarity(AETHER_LOOT)));

    // Materials
    public static final Item SKYROOT_STICK = register("skyroot_stick", Item::new);
    public static final Item GOLDEN_AMBER = register("golden_amber", Item::new);
    public static final Item SWET_BALL = register("swet_ball", SwetBallItem::new);
    public static final Item AECHOR_PETAL = register("aechor_petal", Item::new);
    public static final Item AMBROSIUM_SHARD = register("ambrosium_shard", AmbrosiumShardItem::new);
    public static final Item ZANITE_GEMSTONE = register("zanite_gemstone", Item::new);

    // Misc
    public static final Item VICTORY_MEDAL = register("victory_medal", properties -> new Item(properties.stacksTo(10).rarity(AETHER_LOOT)));

    public static final Item BRONZE_DUNGEON_KEY = register("bronze_dungeon_key", properties -> new Item(properties.stacksTo(1).rarity(AETHER_LOOT).fireResistant().component(AetherDataComponents.DUNGEON_KIND, new DungeonKind(Identifier.fromNamespaceAndPath(Aether.MODID, "bronze")))));
    public static final Item SILVER_DUNGEON_KEY = register("silver_dungeon_key", properties -> new Item(properties.stacksTo(1).rarity(AETHER_LOOT).fireResistant().component(AetherDataComponents.DUNGEON_KIND, new DungeonKind(Identifier.fromNamespaceAndPath(Aether.MODID, "silver")))));
    public static final Item GOLD_DUNGEON_KEY = register("gold_dungeon_key", properties -> new Item(properties.stacksTo(1).rarity(AETHER_LOOT).fireResistant().component(AetherDataComponents.DUNGEON_KIND, new DungeonKind(Identifier.fromNamespaceAndPath(Aether.MODID, "gold")))));

    public static final Item MUSIC_DISC_AETHER_TUNE = register("music_disc_aether_tune", properties -> new Item(properties.stacksTo(1).rarity(Rarity.RARE).jukeboxPlayable(AetherJukeboxSongs.AETHER_TUNE)));
    public static final Item MUSIC_DISC_ASCENDING_DAWN = register("music_disc_ascending_dawn", properties -> new Item(properties.stacksTo(1).rarity(Rarity.RARE).jukeboxPlayable(AetherJukeboxSongs.ASCENDING_DAWN)));
    public static final Item MUSIC_DISC_CHINCHILLA = register("music_disc_chinchilla", properties -> new Item(properties.stacksTo(1).rarity(Rarity.RARE).jukeboxPlayable(AetherJukeboxSongs.CHINCHILLA)));
    public static final Item MUSIC_DISC_HIGH = register("music_disc_high", properties -> new Item(properties.stacksTo(1).rarity(Rarity.RARE).jukeboxPlayable(AetherJukeboxSongs.HIGH)));
    public static final Item MUSIC_DISC_KLEPTO = register("music_disc_klepto", properties -> new Item(properties.stacksTo(1).rarity(Rarity.RARE).jukeboxPlayable(AetherJukeboxSongs.KLEPTO)));
    public static final Item MUSIC_DISC_SLIDERS_WRATH = register("music_disc_sliders_wrath", properties -> new Item(properties.stacksTo(1).rarity(Rarity.RARE).jukeboxPlayable(AetherJukeboxSongs.SLIDERS_WRATH)));

    public static final Item SKYROOT_BUCKET = register("skyroot_bucket", properties -> new SkyrootBucketItem(Fluids.EMPTY, properties.stacksTo(16)));
    public static final Item SKYROOT_WATER_BUCKET = register("skyroot_water_bucket", properties -> new SkyrootBucketItem(Fluids.WATER, properties.craftRemainder(SKYROOT_BUCKET).stacksTo(1)));
    public static final Item SKYROOT_POISON_BUCKET = register("skyroot_poison_bucket", properties -> new SkyrootPoisonBucketItem(properties.craftRemainder(SKYROOT_BUCKET).stacksTo(1)));
    public static final Item SKYROOT_REMEDY_BUCKET = register("skyroot_remedy_bucket", properties -> new SkyrootRemedyBucketItem(properties.craftRemainder(SKYROOT_BUCKET).stacksTo(1).rarity(Rarity.RARE)));
    public static final Item SKYROOT_MILK_BUCKET = register("skyroot_milk_bucket", properties -> new SkyrootMilkBucketItem(properties.craftRemainder(SKYROOT_BUCKET).stacksTo(1)));
    public static final Item SKYROOT_POWDER_SNOW_BUCKET = register("skyroot_powder_snow_bucket", properties -> new SkyrootSolidBucketItem(Blocks.POWDER_SNOW, SoundEvents.BUCKET_EMPTY_POWDER_SNOW, properties.craftRemainder(SKYROOT_BUCKET).stacksTo(1)));
    public static final Item SKYROOT_COD_BUCKET = register("skyroot_cod_bucket", properties -> new SkyrootMobBucketItem(EntityTypes.COD, Fluids.WATER, SoundEvents.BUCKET_EMPTY_FISH, properties.craftRemainder(SKYROOT_BUCKET).stacksTo(1).component(DataComponents.BUCKET_ENTITY_DATA, CustomData.EMPTY)));
    public static final Item SKYROOT_SALMON_BUCKET = register("skyroot_salmon_bucket", properties -> new SkyrootMobBucketItem(EntityTypes.SALMON, Fluids.WATER, SoundEvents.BUCKET_EMPTY_FISH, properties.craftRemainder(SKYROOT_BUCKET).stacksTo(1).component(DataComponents.BUCKET_ENTITY_DATA, CustomData.EMPTY)));
    public static final Item SKYROOT_PUFFERFISH_BUCKET = register("skyroot_pufferfish_bucket", properties -> new SkyrootMobBucketItem(EntityTypes.PUFFERFISH, Fluids.WATER, SoundEvents.BUCKET_EMPTY_FISH, properties.craftRemainder(SKYROOT_BUCKET).stacksTo(1).component(DataComponents.BUCKET_ENTITY_DATA, CustomData.EMPTY)));
    public static final Item SKYROOT_TROPICAL_FISH_BUCKET = register("skyroot_tropical_fish_bucket", properties -> new SkyrootMobBucketItem(EntityTypes.TROPICAL_FISH, Fluids.WATER, SoundEvents.BUCKET_EMPTY_FISH, properties.craftRemainder(SKYROOT_BUCKET).stacksTo(1).component(DataComponents.BUCKET_ENTITY_DATA, CustomData.EMPTY)));
    public static final Item SKYROOT_AXOLOTL_BUCKET = register("skyroot_axolotl_bucket", properties -> new SkyrootMobBucketItem(EntityTypes.AXOLOTL, Fluids.WATER, SoundEvents.BUCKET_EMPTY_AXOLOTL, properties.craftRemainder(SKYROOT_BUCKET).stacksTo(1).component(DataComponents.BUCKET_ENTITY_DATA, CustomData.EMPTY)));
    public static final Item SKYROOT_TADPOLE_BUCKET = register("skyroot_tadpole_bucket", properties -> new SkyrootMobBucketItem(EntityTypes.TADPOLE, Fluids.WATER, SoundEvents.BUCKET_EMPTY_TADPOLE, properties.craftRemainder(SKYROOT_BUCKET).stacksTo(1).component(DataComponents.BUCKET_ENTITY_DATA, CustomData.EMPTY)));

    public static final Item SKYROOT_BOAT = register("skyroot_boat", properties -> new SkyrootBoatItem(false, properties.stacksTo(1)));
    public static final Item SKYROOT_CHEST_BOAT = register("skyroot_chest_boat", properties -> new SkyrootBoatItem(true, properties.stacksTo(1)));

    public static final Item COLD_PARACHUTE = register("cold_parachute", properties -> new ParachuteItem(() -> AetherEntityTypes.COLD_PARACHUTE, properties.durability(1)));
    public static final Item GOLDEN_PARACHUTE = register("golden_parachute", properties -> new ParachuteItem(() -> AetherEntityTypes.GOLDEN_PARACHUTE, properties.durability(20)));

    public static final Item BLUE_MOA_EGG = register("blue_moa_egg", properties -> new MoaEggItem(AetherMoaTypes.BLUE, 0x7777FF, properties));
    public static final Item WHITE_MOA_EGG = register("white_moa_egg", properties -> new MoaEggItem(AetherMoaTypes.WHITE, 0xFFFFFF, properties));
    public static final Item BLACK_MOA_EGG = register("black_moa_egg", properties -> new MoaEggItem(AetherMoaTypes.BLACK, 0x222222, properties));

    public static final Item NATURE_STAFF = register("nature_staff", properties -> new Item(properties.durability(100)));
    public static final Item CLOUD_STAFF = register("cloud_staff", properties -> new CloudStaffItem(properties.durability(60).rarity(AETHER_LOOT)));

    public static final Item LIFE_SHARD = register("life_shard", properties -> new LifeShardItem(properties.stacksTo(1).rarity(AETHER_LOOT)));

    public static final Item BOOK_OF_LORE = register("book_of_lore", properties -> new LoreBookItem(properties.stacksTo(1).rarity(AETHER_LOOT)));

    public static final Item AETHER_PORTAL_FRAME = register("aether_portal_frame", properties -> new AetherPortalItem(properties.stacksTo(1)));

    public static final SpawnEggItem AECHOR_PLANT_SPAWN_EGG = register("aechor_plant_spawn_egg", properties -> new AetherSpawnEggItem(AetherEntityTypes.AECHOR_PLANT, 0x076178, 0x4BC69E, properties));
    public static final SpawnEggItem AERBUNNY_SPAWN_EGG = register("aerbunny_spawn_egg", properties -> new AetherSpawnEggItem(AetherEntityTypes.AERBUNNY, 0xE2FCFF, 0xFFDFF9, properties));
    public static final SpawnEggItem AERWHALE_SPAWN_EGG = register("aerwhale_spawn_egg", properties -> new AetherSpawnEggItem(AetherEntityTypes.AERWHALE, 0xC0E7FD, 0x879EAA, properties));
    public static final SpawnEggItem COCKATRICE_SPAWN_EGG = register("cockatrice_spawn_egg", properties -> new AetherSpawnEggItem(AetherEntityTypes.COCKATRICE, 0x6CB15C, 0x6C579D, properties));
    public static final SpawnEggItem FIRE_MINION_SPAWN_EGG = register("fire_minion_spawn_egg", properties -> new AetherSpawnEggItem(AetherEntityTypes.FIRE_MINION, 0xFF6D01, 0xFEF500, properties));
    public static final SpawnEggItem FLYING_COW_SPAWN_EGG = register("flying_cow_spawn_egg", properties -> new AetherSpawnEggItem(AetherEntityTypes.FLYING_COW, 0xD8D8D8, 0xFFD939, properties));
    public static final SpawnEggItem MIMIC_SPAWN_EGG = register("mimic_spawn_egg", properties -> new AetherSpawnEggItem(AetherEntityTypes.MIMIC, 0xB18132, 0x605A4E, properties));
    public static final SpawnEggItem MOA_SPAWN_EGG = register("moa_spawn_egg", properties -> new AetherSpawnEggItem(AetherEntityTypes.MOA, 0x87BFEF, 0x7A7A7A, properties));
    public static final SpawnEggItem PHYG_SPAWN_EGG = register("phyg_spawn_egg", properties -> new AetherSpawnEggItem(AetherEntityTypes.PHYG, 0xFFC1D0, 0xFFD939, properties));
    public static final SpawnEggItem SENTRY_SPAWN_EGG = register("sentry_spawn_egg", properties -> new AetherSpawnEggItem(AetherEntityTypes.SENTRY, 0x808080, 0x3A8AEC, properties));
    public static final SpawnEggItem SHEEPUFF_SPAWN_EGG = register("sheepuff_spawn_egg", properties -> new AetherSpawnEggItem(AetherEntityTypes.SHEEPUFF, 0xE2FCFF, 0xCB9090, properties));
    public static final SpawnEggItem BLUE_SWET_SPAWN_EGG = register("blue_swet_spawn_egg", properties -> new AetherSpawnEggItem(AetherEntityTypes.BLUE_SWET, 0x4FB1DA, 0xCDDA4F, properties));
    public static final SpawnEggItem GOLDEN_SWET_SPAWN_EGG = register("golden_swet_spawn_egg", properties -> new AetherSpawnEggItem(AetherEntityTypes.GOLDEN_SWET, 0xCDDA4F, 0x4FB1DA, properties));
    public static final SpawnEggItem WHIRLWIND_SPAWN_EGG = register("whirlwind_spawn_egg", properties -> new AetherSpawnEggItem(AetherEntityTypes.WHIRLWIND, 0x9FC3F7, 0xFFFFFF, properties));
    public static final SpawnEggItem EVIL_WHIRLWIND_SPAWN_EGG = register("evil_whirlwind_spawn_egg", properties -> new AetherSpawnEggItem(AetherEntityTypes.EVIL_WHIRLWIND, 0x9FC3F7, 0x111111, properties));
    public static final SpawnEggItem VALKYRIE_SPAWN_EGG = register("valkyrie_spawn_egg", properties -> new AetherSpawnEggItem(AetherEntityTypes.VALKYRIE, 0xF9F5E3, 0xF2D200, properties));
    public static final SpawnEggItem VALKYRIE_QUEEN_SPAWN_EGG = register("valkyrie_queen_spawn_egg", properties -> new AetherSpawnEggItem(AetherEntityTypes.VALKYRIE_QUEEN, 0xF2D200, 0xF9F5E3, properties));
    public static final SpawnEggItem SLIDER_SPAWN_EGG = register("slider_spawn_egg", properties -> new SliderSpawnEggItem(AetherEntityTypes.SLIDER, 0xA7A7A7, 0x5C9FF2, properties));
    public static final SpawnEggItem SUN_SPIRIT_SPAWN_EGG = register("sun_spirit_spawn_egg", properties -> new AetherSpawnEggItem(AetherEntityTypes.SUN_SPIRIT, 0xFEF500, 0xFF6D01, properties));
    public static final SpawnEggItem ZEPHYR_SPAWN_EGG = register("zephyr_spawn_egg", properties -> new AetherSpawnEggItem(AetherEntityTypes.ZEPHYR, 0xDFDFDF, 0x99CFE8, properties));

    public static ItemStack SWET_BANNER = null;

    public static void bootstrap() {
    }

    private static <I extends Item> I register(String name, Supplier<? extends I> supplier) {
        Identifier id = Identifier.fromNamespaceAndPath(Aether.MODID, name);
        I item = RegistryConstructionContext.constructWithId(Registries.ITEM, id, supplier);
        return Registry.register(BuiltInRegistries.ITEM, id, item);
    }

    private static <I extends Item> I register(String name, Function<Item.Properties, ? extends I> factory) {
        Identifier id = Identifier.fromNamespaceAndPath(Aether.MODID, name);
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id);
        I item = factory.apply(new Item.Properties().setId(key));
        return Registry.register(BuiltInRegistries.ITEM, id, item);
    }

    public static void registerAccessories() {
        AccessoriesAPI.registerAccessory(AetherItems.IRON_RING, (Accessory) AetherItems.IRON_RING);
        AccessoriesAPI.registerAccessory(AetherItems.GOLDEN_RING, (Accessory) AetherItems.GOLDEN_RING);
        AccessoriesAPI.registerAccessory(AetherItems.ZANITE_RING, (Accessory) AetherItems.ZANITE_RING);
        AccessoriesAPI.registerAccessory(AetherItems.ICE_RING, (Accessory) AetherItems.ICE_RING);

        AccessoriesAPI.registerAccessory(AetherItems.IRON_PENDANT, (Accessory) AetherItems.IRON_PENDANT);
        AccessoriesAPI.registerAccessory(AetherItems.GOLDEN_PENDANT, (Accessory) AetherItems.GOLDEN_PENDANT);
        AccessoriesAPI.registerAccessory(AetherItems.ZANITE_PENDANT, (Accessory) AetherItems.ZANITE_PENDANT);
        AccessoriesAPI.registerAccessory(AetherItems.ICE_PENDANT, (Accessory) AetherItems.ICE_PENDANT);

        AccessoriesAPI.registerAccessory(AetherItems.LEATHER_GLOVES, (Accessory) AetherItems.LEATHER_GLOVES);
        AccessoriesAPI.registerAccessory(AetherItems.CHAINMAIL_GLOVES, (Accessory) AetherItems.CHAINMAIL_GLOVES);
        AccessoriesAPI.registerAccessory(AetherItems.IRON_GLOVES, (Accessory) AetherItems.IRON_GLOVES);
        AccessoriesAPI.registerAccessory(AetherItems.GOLDEN_GLOVES, (Accessory) AetherItems.GOLDEN_GLOVES);
        AccessoriesAPI.registerAccessory(AetherItems.DIAMOND_GLOVES, (Accessory) AetherItems.DIAMOND_GLOVES);
        AccessoriesAPI.registerAccessory(AetherItems.NETHERITE_GLOVES, (Accessory) AetherItems.NETHERITE_GLOVES);
        AccessoriesAPI.registerAccessory(AetherItems.ZANITE_GLOVES, (Accessory) AetherItems.ZANITE_GLOVES);
        AccessoriesAPI.registerAccessory(AetherItems.GRAVITITE_GLOVES, (Accessory) AetherItems.GRAVITITE_GLOVES);
        AccessoriesAPI.registerAccessory(AetherItems.VALKYRIE_GLOVES, (Accessory) AetherItems.VALKYRIE_GLOVES);
        AccessoriesAPI.registerAccessory(AetherItems.NEPTUNE_GLOVES, (Accessory) AetherItems.NEPTUNE_GLOVES);
        AccessoriesAPI.registerAccessory(AetherItems.PHOENIX_GLOVES, (Accessory) AetherItems.PHOENIX_GLOVES);
        AccessoriesAPI.registerAccessory(AetherItems.OBSIDIAN_GLOVES, (Accessory) AetherItems.OBSIDIAN_GLOVES);

        AccessoriesAPI.registerAccessory(AetherItems.RED_CAPE, (Accessory) AetherItems.RED_CAPE);
        AccessoriesAPI.registerAccessory(AetherItems.BLUE_CAPE, (Accessory) AetherItems.BLUE_CAPE);
        AccessoriesAPI.registerAccessory(AetherItems.YELLOW_CAPE, (Accessory) AetherItems.YELLOW_CAPE);
        AccessoriesAPI.registerAccessory(AetherItems.WHITE_CAPE, (Accessory) AetherItems.WHITE_CAPE);
        AccessoriesAPI.registerAccessory(AetherItems.AGILITY_CAPE, (Accessory) AetherItems.AGILITY_CAPE);
        AccessoriesAPI.registerAccessory(AetherItems.SWET_CAPE, (Accessory) AetherItems.SWET_CAPE);
        AccessoriesAPI.registerAccessory(AetherItems.INVISIBILITY_CLOAK, (Accessory) AetherItems.INVISIBILITY_CLOAK);
        AccessoriesAPI.registerAccessory(AetherItems.VALKYRIE_CAPE, (Accessory) AetherItems.VALKYRIE_CAPE);

        AccessoriesAPI.registerAccessory(AetherItems.GOLDEN_FEATHER, (Accessory) AetherItems.GOLDEN_FEATHER);
        AccessoriesAPI.registerAccessory(AetherItems.REGENERATION_STONE, (Accessory) AetherItems.REGENERATION_STONE);
        AccessoriesAPI.registerAccessory(AetherItems.IRON_BUBBLE, (Accessory) AetherItems.IRON_BUBBLE);
        AccessoriesAPI.registerAccessory(AetherItems.SHIELD_OF_REPULSION, (Accessory) AetherItems.SHIELD_OF_REPULSION);
    }

    /**
     * Sets up the possible replacements for vanilla buckets to Skyroot buckets.
     *
     * @see com.aetherteam.aether.item.miscellaneous.bucket.SkyrootBucketInteractions#pickupBucketable
     */
    public static void setupBucketReplacements() {
        SkyrootBucketItem.REPLACEMENTS.put(() -> Items.WATER_BUCKET, () -> AetherItems.SKYROOT_WATER_BUCKET);
        SkyrootBucketItem.REPLACEMENTS.put(() -> Items.POWDER_SNOW_BUCKET, () -> AetherItems.SKYROOT_POWDER_SNOW_BUCKET);
        SkyrootBucketItem.REPLACEMENTS.put(() -> Items.COD_BUCKET, () -> AetherItems.SKYROOT_COD_BUCKET);
        SkyrootBucketItem.REPLACEMENTS.put(() -> Items.SALMON_BUCKET, () -> AetherItems.SKYROOT_SALMON_BUCKET);
        SkyrootBucketItem.REPLACEMENTS.put(() -> Items.PUFFERFISH_BUCKET, () -> AetherItems.SKYROOT_PUFFERFISH_BUCKET);
        SkyrootBucketItem.REPLACEMENTS.put(() -> Items.TROPICAL_FISH_BUCKET, () -> AetherItems.SKYROOT_TROPICAL_FISH_BUCKET);
        SkyrootBucketItem.REPLACEMENTS.put(() -> Items.AXOLOTL_BUCKET, () -> AetherItems.SKYROOT_AXOLOTL_BUCKET);
        SkyrootBucketItem.REPLACEMENTS.put(() -> Items.TADPOLE_BUCKET, () -> AetherItems.SKYROOT_TADPOLE_BUCKET);
        SkyrootBucketItem.REPLACEMENTS.put(() -> Items.MILK_BUCKET, () -> AetherItems.SKYROOT_MILK_BUCKET);
    }

    public static ItemStack createSwetBannerItemStack(HolderGetter<BannerPattern> patternRegistry) {
        if (SWET_BANNER == null) {
            ItemStack bannerStack = new ItemStack(Items.BANNER.pick(DyeColor.BLACK));
            BannerPatternLayers layers = new BannerPatternLayers.Builder()
                .add(patternRegistry.getOrThrow(BannerPatterns.STRIPE_DOWNLEFT), DyeColor.CYAN)
                .add(patternRegistry.getOrThrow(BannerPatterns.STRIPE_BOTTOM), DyeColor.CYAN)
                .add(patternRegistry.getOrThrow(BannerPatterns.STRIPE_LEFT), DyeColor.CYAN)
                .add(patternRegistry.getOrThrow(BannerPatterns.HALF_HORIZONTAL), DyeColor.BLACK)
                .add(patternRegistry.getOrThrow(BannerPatterns.STRAIGHT_CROSS), DyeColor.CYAN)
                .add(patternRegistry.getOrThrow(BannerPatterns.BORDER), DyeColor.WHITE)
                .add(patternRegistry.getOrThrow(BannerPatterns.GRADIENT_UP), DyeColor.LIGHT_BLUE)
                .add(patternRegistry.getOrThrow(BannerPatterns.GRADIENT), DyeColor.LIGHT_BLUE)
                .build();
            bannerStack.set(DataComponents.BANNER_PATTERNS, layers);
            bannerStack.set(DataComponents.ITEM_NAME, Component.translatable("aether.block.aether.swet_banner").withStyle(ChatFormatting.GOLD));
            SWET_BANNER = bannerStack;
        }
        return SWET_BANNER;
    }
}
