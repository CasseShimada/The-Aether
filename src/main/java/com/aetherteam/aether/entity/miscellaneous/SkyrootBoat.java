package com.aetherteam.aether.entity.miscellaneous;

import com.aetherteam.aether.entity.AetherEntityTypes;
import com.aetherteam.aether.item.AetherItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.boat.Boat;
import net.minecraft.world.level.Level;

public class SkyrootBoat extends Boat {
    public SkyrootBoat(EntityType<? extends SkyrootBoat> type, Level level) {
        super(type, level, AetherItems.SKYROOT_BOAT::get);
    }

    public SkyrootBoat(Level level, double x, double y, double z) {
        this(AetherEntityTypes.SKYROOT_BOAT, level);
        this.setInitialPos(x, y, z);
    }
}
