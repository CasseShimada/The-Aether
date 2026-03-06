package com.aetherteam.aether.item.miscellaneous;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;

public class AetherSpawnEggItem extends SpawnEggItem {
    private final EntityType<? extends Mob> entityType;
    private final int backgroundColor;
    private final int highlightColor;

    public AetherSpawnEggItem(EntityType<? extends Mob> entityType, int backgroundColor, int highlightColor, Properties properties) {
        super(properties);
        this.entityType = entityType;
        this.backgroundColor = backgroundColor;
        this.highlightColor = highlightColor;
    }

    @Override
    public EntityType<?> getType(ItemStack stack) {
        return this.entityType;
    }

    public int getColor(int tintIndex) {
        return tintIndex == 0 ? this.backgroundColor : this.highlightColor;
    }
}
