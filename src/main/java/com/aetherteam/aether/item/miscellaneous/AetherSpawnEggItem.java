package com.aetherteam.aether.item.miscellaneous;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.SpawnEggItem;

public class AetherSpawnEggItem extends SpawnEggItem {
    private final int backgroundColor;
    private final int highlightColor;

    public AetherSpawnEggItem(EntityType<? extends Mob> entityType, int backgroundColor, int highlightColor, Properties properties) {
        super(properties.spawnEgg(entityType));
        this.backgroundColor = backgroundColor;
        this.highlightColor = highlightColor;
    }

    public int getColor(int tintIndex) {
        return tintIndex == 0 ? this.backgroundColor : this.highlightColor;
    }
}
