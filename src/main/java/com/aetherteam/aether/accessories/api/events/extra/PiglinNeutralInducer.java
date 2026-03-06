package com.aetherteam.aether.accessories.api.events.extra;

import com.aetherteam.aether.accessories.api.slot.SlotReference;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.world.item.ItemStack;

public interface PiglinNeutralInducer {
    TriState makePiglinsNeutral(ItemStack stack, SlotReference reference);
}
