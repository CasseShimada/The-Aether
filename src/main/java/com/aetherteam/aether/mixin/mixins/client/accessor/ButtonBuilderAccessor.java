package com.aetherteam.aether.mixin.mixins.client.accessor;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Button.Builder.class)
public interface ButtonBuilderAccessor {
    @Accessor("x")
    int aether$getX();

    @Accessor("y")
    int aether$getY();

    @Accessor("width")
    int aether$getWidth();

    @Accessor("height")
    int aether$getHeight();

    @Accessor("message")
    Component aether$getMessage();

    @Accessor("onPress")
    Button.OnPress aether$getOnPress();

    @Accessor("createNarration")
    Button.CreateNarration aether$getCreateNarration();

    @Accessor("tooltip")
    Tooltip aether$getTooltip();
}
