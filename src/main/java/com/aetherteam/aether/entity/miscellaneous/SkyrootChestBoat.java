package com.aetherteam.aether.entity.miscellaneous;

import com.aetherteam.aether.entity.AetherEntityTypes;
import com.aetherteam.aether.item.AetherItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.boat.ChestBoat;
import net.minecraft.world.level.Level;

public class SkyrootChestBoat extends ChestBoat {
    public SkyrootChestBoat(EntityType<? extends SkyrootChestBoat> entityType, Level level) {
        super(entityType, level, AetherItems.SKYROOT_CHEST_BOAT::get);
    }

    public SkyrootChestBoat(Level level, double x, double y, double z) {
        this(AetherEntityTypes.SKYROOT_CHEST_BOAT.get(), level);
        this.setInitialPos(x, y, z);
    }
}
