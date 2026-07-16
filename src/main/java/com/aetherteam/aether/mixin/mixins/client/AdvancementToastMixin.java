package com.aetherteam.aether.mixin.mixins.client;

import com.aetherteam.aether.api.AetherAdvancementSoundOverrides;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.client.gui.components.toasts.AdvancementToast;
import net.minecraft.sounds.SoundEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AdvancementToast.class)
public class AdvancementToastMixin {
    @Final
    @Shadow
    private AdvancementHolder advancement;

    /**
     * Plays the Aether's advancement sounds when the player gets an Aether advancement.
     */
    @Inject(method = "getSoundEvent", at = @At("HEAD"), cancellable = true, require = 1)
    private void aether$getSoundEvent(CallbackInfoReturnable<SoundEvent> cir) {
        if (this.advancement != null) {
            SoundEvent soundOverride = AetherAdvancementSoundOverrides.retrieveOverride(this.advancement);
            if (soundOverride != null) {
                cir.setReturnValue(soundOverride);
            }
        }
    }
}
