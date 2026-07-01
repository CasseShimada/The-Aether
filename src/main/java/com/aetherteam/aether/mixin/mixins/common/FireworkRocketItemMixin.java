package com.aetherteam.aether.mixin.mixins.common;

import com.aetherteam.aether.accessories.compat.AccessoryEffectBridge;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FireworkRocketItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FireworkRocketItem.class)
public abstract class FireworkRocketItemMixin {
    @Shadow
    public abstract InteractionResult use(Level level, Player player, InteractionHand hand);

    @Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
    private void aether$preferAccessoryElytraBoost(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        Player player = context.getPlayer();
        if (player == null || !player.isFallFlying()) {
            return;
        }
        if (player.getItemBySlot(EquipmentSlot.CHEST).is(Items.ELYTRA)) {
            return;
        }
        if (AccessoryEffectBridge.findFirstElytraReference(player) == null) {
            return;
        }

        cir.setReturnValue(this.use(context.getLevel(), player, context.getHand()));
    }
}
