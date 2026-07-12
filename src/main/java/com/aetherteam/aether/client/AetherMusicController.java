package com.aetherteam.aether.client;

import com.aetherteam.aether.AetherConfig;
import com.aetherteam.aether.AetherTags;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

import java.util.Optional;

public final class AetherMusicController {
    private AetherMusicController() {
    }

    /**
     * Stops other music from playing over Aether music.
     */
    public static boolean shouldCancelMusic(SoundInstance sound) {
        if (Minecraft.getInstance().level != null && !AetherConfig.CLIENT.disable_music_manager.get()) {
            Holder<SoundEvent> soundEvent = getSoundEvent(sound);
            if (sound.getSource() == SoundSource.MUSIC && soundEvent != null && !soundEvent.is(AetherTags.SoundEvents.ACHIEVEMENT_SOUNDS)) {
                return AetherMusicManager.getSituationalMusic() != null && !sound.getIdentifier().equals(SimpleSoundInstance.forMusic(AetherMusicManager.getSituationalMusic().sound().value()).getIdentifier())
                        || (AetherMusicManager.getCurrentMusic() != null && !sound.getIdentifier().equals(AetherMusicManager.getCurrentMusic().getIdentifier()));
            }
        }
        return false;
    }

    /**
     * Ticks the Aether's music manager.
     */
    public static void tick() {
        if (!Minecraft.getInstance().isPaused() && Minecraft.getInstance().level != null && !AetherConfig.CLIENT.disable_music_manager.get()) {
            AetherMusicManager.tick();
        }
    }

    /**
     * Resets the music on respawn.
     */
    public static void stop() {
        if (!AetherConfig.CLIENT.disable_music_manager.get()) {
            AetherMusicManager.stopPlaying();
        }
    }

    private static Holder<SoundEvent> getSoundEvent(SoundInstance sound) {
        Optional<Holder.Reference<SoundEvent>> soundEvent = BuiltInRegistries.SOUND_EVENT.get(sound.getIdentifier());
        return soundEvent.map(reference -> (Holder<SoundEvent>) reference).orElse(null);
    }
}
