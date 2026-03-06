package com.aetherteam.aether.data.resources.registries;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.client.AetherSoundEvents;
import net.minecraft.util.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.JukeboxSong;

public class AetherJukeboxSongs {
    public static final ResourceKey<JukeboxSong> AETHER_TUNE = create("aether_tune");
    public static final ResourceKey<JukeboxSong> ASCENDING_DAWN = create("ascending_dawn");
    public static final ResourceKey<JukeboxSong> CHINCHILLA = create("chinchilla");
    public static final ResourceKey<JukeboxSong> HIGH = create("high");
    public static final ResourceKey<JukeboxSong> KLEPTO = create("klepto");
    public static final ResourceKey<JukeboxSong> SLIDERS_WRATH = create("sliders_wrath");

    private static ResourceKey<JukeboxSong> create(String pName) {
        return ResourceKey.create(Registries.JUKEBOX_SONG, Identifier.fromNamespaceAndPath(Aether.MODID, pName));
    }

    public static void bootstrap(BootstrapContext<JukeboxSong> context) {
        register(context, AETHER_TUNE, BuiltInRegistries.SOUND_EVENT.wrapAsHolder(AetherSoundEvents.ITEM_MUSIC_DISC_AETHER_TUNE.get()), 149, 1);
        register(context, ASCENDING_DAWN, BuiltInRegistries.SOUND_EVENT.wrapAsHolder(AetherSoundEvents.ITEM_MUSIC_DISC_ASCENDING_DAWN.get()), 350, 2);
        register(context, CHINCHILLA, BuiltInRegistries.SOUND_EVENT.wrapAsHolder(AetherSoundEvents.ITEM_MUSIC_DISC_CHINCHILLA.get()), 164, 3);
        register(context, HIGH, BuiltInRegistries.SOUND_EVENT.wrapAsHolder(AetherSoundEvents.ITEM_MUSIC_DISC_HIGH.get()), 186, 4);
        register(context, KLEPTO, BuiltInRegistries.SOUND_EVENT.wrapAsHolder(AetherSoundEvents.ITEM_MUSIC_DISC_KLEPTO.get()), 192, 5);
        register(context, SLIDERS_WRATH, BuiltInRegistries.SOUND_EVENT.wrapAsHolder(AetherSoundEvents.ITEM_MUSIC_DISC_SLIDERS_WRATH.get()), 172, 6);
    }

    private static void register(BootstrapContext<JukeboxSong> context, ResourceKey<JukeboxSong> key, Holder<SoundEvent> soundEvent, int lengthInSeconds, int comparatorOutput) {
        context.register(key, new JukeboxSong(soundEvent, Component.translatable(Util.makeDescriptionId("jukebox_song", key.identifier())), (float) lengthInSeconds, comparatorOutput));
    }
}
