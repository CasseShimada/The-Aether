package com.aetherteam.aether.mixin.mixins.common;

import com.aetherteam.aether.registry.RegistryConstructionContext;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public class BlockRegistryIdMixin {
    @Inject(method = "<init>", at = @At("HEAD"))
    private static void aether$bindRegistryId(BlockBehaviour.Properties properties, CallbackInfo ci) {
        Identifier id = RegistryConstructionContext.currentId(Registries.BLOCK);
        if (id != null) {
            properties.setId(ResourceKey.create(Registries.BLOCK, id));
        }
    }
}
