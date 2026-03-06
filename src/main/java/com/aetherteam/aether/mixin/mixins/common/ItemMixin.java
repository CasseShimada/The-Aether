package com.aetherteam.aether.mixin.mixins.common;

import com.aetherteam.aether.registry.RegistryConstructionContext;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Item.class)
public class ItemMixin {
    @Inject(method = "<init>", at = @At("HEAD"))
    private static void aether$bindRegistryId(Item.Properties properties, CallbackInfo ci) {
        Identifier id = RegistryConstructionContext.currentId(Registries.ITEM);
        if (id != null) {
            properties.setId(ResourceKey.create(Registries.ITEM, id));
        }
    }
}
