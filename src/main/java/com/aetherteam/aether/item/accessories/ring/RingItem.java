package com.aetherteam.aether.item.accessories.ring;

import com.aetherteam.aether.item.accessories.AccessoryItem;
import com.aetherteam.aether.registry.DeferredHolder;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;

public class RingItem extends AccessoryItem {
    public RingItem(DeferredHolder<SoundEvent, SoundEvent> ringSound, Properties properties) {
        super(ringSound, properties);
    }

    public RingItem(Holder<SoundEvent> ringSound, Properties properties) {
        super(ringSound, properties);
    }
}
