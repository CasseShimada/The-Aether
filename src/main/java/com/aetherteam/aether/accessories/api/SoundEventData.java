package com.aetherteam.aether.accessories.api;

import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;

public record SoundEventData(Holder<SoundEvent> event, float volume, float pitch) {
}
