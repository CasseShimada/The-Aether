package com.aetherteam.aether.client.event.hooks;

import com.aetherteam.aether.AetherTags;
import com.aetherteam.aether.client.sound.FadeOutSoundInstance;
import com.aetherteam.aether.mixin.mixins.client.accessor.SoundEngineAccessor;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;

import java.util.Optional;

public final class PortalSoundHooks {
    private PortalSoundHooks() {
    }

    /**
     * Prevents ambient Aether Portal sounds from overlapping other portal sounds.
     */
    public static boolean preventAmbientPortalSound(SoundEngine soundEngine, SoundInstance sound) {
        if (sound != null) {
            Holder<SoundEvent> soundEvent = getSoundEvent(sound);
            if (soundEvent != null && soundEvent.is(AetherTags.SoundEvents.AMBIENT_PORTAL_SOUNDS)) {
                return ((SoundEngineAccessor) soundEngine).aether$getInstanceToChannel().keySet().stream().anyMatch((playingInstance) -> {
                    Holder<SoundEvent> playingSound = getSoundEvent(playingInstance);
                    return playingSound != null && playingSound.is(AetherTags.SoundEvents.PORTAL_SOUNDS);
                });
            }
        }
        return false;
    }

    /**
     * Stops ambient Aether Portal sounds when other portal sounds are activated.
     */
    public static void overrideActivatedPortalSound(SoundEngine soundEngine, SoundInstance sound) {
        if (sound != null) {
            Holder<SoundEvent> soundEvent = getSoundEvent(sound);
            if (soundEvent != null && soundEvent.is(AetherTags.SoundEvents.ACTIVATED_PORTAL_SOUNDS)) {
                ((SoundEngineAccessor) soundEngine).aether$getInstanceToChannel().keySet().forEach((playingInstance) -> {
                    Holder<SoundEvent> playingSound = getSoundEvent(playingInstance);
                    if (playingSound != null && playingSound.is(AetherTags.SoundEvents.AMBIENT_PORTAL_SOUNDS)) {
                        if (playingInstance instanceof FadeOutSoundInstance fadeOutSoundInstance) {
                            fadeOutSoundInstance.fadeOut();
                        }
                    }
                });
            }
        }
    }

    private static Holder<SoundEvent> getSoundEvent(SoundInstance sound) {
        Optional<Holder.Reference<SoundEvent>> soundEvent = BuiltInRegistries.SOUND_EVENT.get(sound.getIdentifier());
        return soundEvent.map(reference -> (Holder<SoundEvent>) reference).orElse(null);
    }
}
