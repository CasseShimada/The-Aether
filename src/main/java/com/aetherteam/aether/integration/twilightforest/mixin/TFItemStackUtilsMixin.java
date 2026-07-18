package com.aetherteam.aether.integration.twilightforest.mixin;

import com.aetherteam.aether.integration.twilightforest.TwilightForestCharmCompatibility;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ItemLike;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.Desc;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "twilightforest.util.TFItemStackUtils", remap = false)
public abstract class TFItemStackUtilsMixin {
    @Inject(
        target = @Desc(
            value = "consumeInventoryItem",
            args = { Player.class, ItemLike.class, CompoundTag.class, boolean.class },
            ret = boolean.class
        ),
        at = @At("RETURN"),
        cancellable = true,
        require = 0,
        remap = false
    )
    private static void aether$consumeAccessoryCharm(Player player, ItemLike item, CompoundTag persistentTag, boolean saveItemToTag, CallbackInfoReturnable<Boolean> cir) {
        if (TwilightForestCharmCompatibility.afterInventoryResult(cir.getReturnValueZ(),
            () -> TwilightForestCharmCompatibility.consumeAccessoryCharm(player, item.asItem(), persistentTag, saveItemToTag))) {
            cir.setReturnValue(true);
        }
    }
}
