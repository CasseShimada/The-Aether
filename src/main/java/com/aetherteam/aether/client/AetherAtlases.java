package com.aetherteam.aether.client;

import com.aetherteam.aether.Aether;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.resources.Identifier;

public class AetherAtlases {
    public static SpriteId TREASURE_CHEST_MATERIAL;
    public static SpriteId TREASURE_CHEST_LEFT_MATERIAL;
    public static SpriteId TREASURE_CHEST_RIGHT_MATERIAL;

    /**
     * Need to register these static values during client setup,
     * otherwise they'll be loaded too early from static initialization in the field.
     */
    public static void registerTreasureChestAtlases() {
        TREASURE_CHEST_MATERIAL = getChestMaterial("treasure_chest");
        TREASURE_CHEST_LEFT_MATERIAL = getChestMaterial("treasure_chest_left");
        TREASURE_CHEST_RIGHT_MATERIAL = getChestMaterial("treasure_chest_right");
    }

    public static void registerWoodTypeAtlases() {
        // WoodType atlas entries are initialized from the global wood type registry.
    }

    public static SpriteId getChestMaterial(String chestName) {
        return new SpriteId(Sheets.CHEST_SHEET, Identifier.fromNamespaceAndPath(Aether.MODID, "entity/tiles/chest/" + chestName));
    }
}
