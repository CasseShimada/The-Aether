package com.aetherteam.aether.item.combat;

import com.aetherteam.aether.AetherTags;
import com.aetherteam.aether.client.AetherSoundEvents;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;

import java.util.EnumMap;
import java.util.Map;

public class AetherArmorMaterials {
    public static final Holder<ArmorMaterial> ZANITE = Holder.direct(create(
            15,
            defense(2, 6, 5, 2),
            9,
            Holder.direct(AetherSoundEvents.ITEM_ARMOR_EQUIP_ZANITE),
            0.0F,
            0.0F,
            AetherTags.Items.ZANITE_REPAIRING,
            EquipmentAssets.createId("zanite")
    ));

    public static final Holder<ArmorMaterial> GRAVITITE = Holder.direct(create(
            33,
            defense(3, 8, 6, 3),
            10,
            Holder.direct(AetherSoundEvents.ITEM_ARMOR_EQUIP_GRAVITITE),
            2.0F,
            0.0F,
            AetherTags.Items.GRAVITITE_REPAIRING,
            EquipmentAssets.createId("gravitite")
    ));

    public static final Holder<ArmorMaterial> NEPTUNE = Holder.direct(create(
            15,
            defense(2, 6, 5, 2),
            10,
            Holder.direct(AetherSoundEvents.ITEM_ARMOR_EQUIP_NEPTUNE),
            1.0F,
            0.0F,
            AetherTags.Items.NEPTUNE_REPAIRING,
            EquipmentAssets.createId("neptune")
    ));

    public static final Holder<ArmorMaterial> VALKYRIE = Holder.direct(create(
            33,
            defense(3, 8, 6, 3),
            10,
            Holder.direct(AetherSoundEvents.ITEM_ARMOR_EQUIP_VALKYRIE),
            2.0F,
            0.0F,
            AetherTags.Items.VALKYRIE_REPAIRING,
            EquipmentAssets.createId("valkyrie")
    ));

    public static final Holder<ArmorMaterial> PHOENIX = Holder.direct(create(
            33,
            defense(3, 8, 6, 3),
            10,
            Holder.direct(AetherSoundEvents.ITEM_ARMOR_EQUIP_PHOENIX),
            2.0F,
            0.0F,
            AetherTags.Items.PHOENIX_REPAIRING,
            EquipmentAssets.createId("phoenix")
    ));

    public static final Holder<ArmorMaterial> OBSIDIAN = Holder.direct(create(
            37,
            defense(3, 8, 6, 3),
            15,
            Holder.direct(AetherSoundEvents.ITEM_ARMOR_EQUIP_OBSIDIAN),
            3.0F,
            0.0F,
            AetherTags.Items.OBSIDIAN_REPAIRING,
            EquipmentAssets.createId("obsidian")
    ));

    public static final Holder<ArmorMaterial> SENTRY = Holder.direct(create(
            15,
            defense(2, 6, 5, 2),
            9,
            Holder.direct(AetherSoundEvents.ITEM_ARMOR_EQUIP_SENTRY),
            0.0F,
            0.0F,
            AetherTags.Items.SENTRY_REPAIRING,
            EquipmentAssets.createId("sentry")
    ));

    private static ArmorMaterial create(int durability, Map<ArmorType, Integer> defense, int enchantability, Holder<net.minecraft.sounds.SoundEvent> equipSound, float toughness, float knockbackResistance, net.minecraft.tags.TagKey<Item> repairTag, ResourceKey<EquipmentAsset> assetId) {
        return new ArmorMaterial(durability, defense, enchantability, equipSound, toughness, knockbackResistance, repairTag, assetId);
    }

    private static Map<ArmorType, Integer> defense(int boots, int chestplate, int leggings, int helmet) {
        EnumMap<ArmorType, Integer> map = new EnumMap<>(ArmorType.class);
        map.put(ArmorType.BOOTS, boots);
        map.put(ArmorType.LEGGINGS, leggings);
        map.put(ArmorType.CHESTPLATE, chestplate);
        map.put(ArmorType.HELMET, helmet);
        return map;
    }
}
