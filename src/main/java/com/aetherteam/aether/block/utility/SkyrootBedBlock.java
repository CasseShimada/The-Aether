package com.aetherteam.aether.block.utility;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.properties.BedPart;

public class SkyrootBedBlock extends BedBlock {
    public SkyrootBedBlock(Properties properties) {
        super(DyeColor.CYAN, properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(PART, BedPart.FOOT).setValue(OCCUPIED, false));
    }

}

