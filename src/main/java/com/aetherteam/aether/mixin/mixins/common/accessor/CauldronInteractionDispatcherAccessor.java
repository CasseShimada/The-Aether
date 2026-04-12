package com.aetherteam.aether.mixin.mixins.common.accessor;

import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(CauldronInteraction.Dispatcher.class)
public interface CauldronInteractionDispatcherAccessor {
    @Invoker("put")
    void aether$put(Item item, CauldronInteraction interaction);
}
