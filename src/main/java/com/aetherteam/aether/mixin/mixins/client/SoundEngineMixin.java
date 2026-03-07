package com.aetherteam.aether.mixin.mixins.client;

import com.aetherteam.aether.client.event.hooks.AudioHooks;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SoundEngine.class)
public class SoundEngineMixin {
    @Inject(method = "play(Lnet/minecraft/client/resources/sounds/SoundInstance;)Lnet/minecraft/client/sounds/SoundEngine$PlayResult;", at = @At("HEAD"), cancellable = true)
    private void aether$onPlaySound(SoundInstance sound, CallbackInfoReturnable<SoundEngine.PlayResult> cir) {
        SoundEngine soundEngine = (SoundEngine) (Object) this;
        if (AudioHooks.shouldCancelMusic(sound) || AudioHooks.preventAmbientPortalSound(soundEngine, sound)) {
            cir.setReturnValue(SoundEngine.PlayResult.NOT_STARTED);
            return;
        }
        AudioHooks.overrideActivatedPortalSound(soundEngine, sound);
    }
}
