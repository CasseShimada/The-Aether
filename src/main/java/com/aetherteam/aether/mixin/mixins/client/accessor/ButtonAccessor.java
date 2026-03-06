package com.aetherteam.aether.mixin.mixins.client.accessor;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.MutableComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Button.class)
public interface ButtonAccessor {
    @Accessor("onPress")
    Button.OnPress aether$getOnPress();

    @Accessor("createNarration")
    Button.CreateNarration aether$getCreateNarration();

    @Invoker
    MutableComponent callCreateNarrationMessage();
}
