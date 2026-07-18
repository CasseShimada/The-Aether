package com.aetherteam.aether.accessories.impl;

import com.aetherteam.aether.accessories.api.slot.SlotReference;
import net.minecraft.world.InteractionHand;

public interface AccessoryUsingEntity {
    void aether$startUsingAccessory(SlotReference reference, InteractionHand hand);

    boolean aether$isUsingAccessory();
}
