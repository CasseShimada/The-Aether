package com.aetherteam.aether.mixin.mixins.client;

import com.aetherteam.aether.accessories.impl.AccessoryItemInteractions;
import com.aetherteam.aether.network.AetherPacketSender;
import com.aetherteam.aether.network.packet.serverbound.UseAccessoryPacket;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Shadow
    public LocalPlayer player;

    @Shadow
    public MultiPlayerGameMode gameMode;

    @Unique
    private boolean aether$useInteractionHandled;

    @Unique
    private boolean aether$mainHandItemPassed;

    @Unique
    private boolean aether$offHandItemPassed;

    @Inject(method = "startUseItem()V", at = @At("HEAD"))
    private void aether$beginAccessoryUseFallback(CallbackInfo ci) {
        this.aether$useInteractionHandled = false;
        this.aether$mainHandItemPassed = false;
        this.aether$offHandItemPassed = false;
    }

    @WrapOperation(method = "startUseItem()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;useItemOn(Lnet/minecraft/client/player/LocalPlayer;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/BlockHitResult;)Lnet/minecraft/world/InteractionResult;"))
    private InteractionResult aether$trackBlockUse(MultiPlayerGameMode instance, LocalPlayer player, InteractionHand hand, BlockHitResult hit, Operation<InteractionResult> original) {
        InteractionResult result = original.call(instance, player, hand, hit);
        if (result instanceof InteractionResult.Success || result instanceof InteractionResult.Fail) {
            this.aether$useInteractionHandled = true;
        }
        return result;
    }

    @WrapOperation(method = "startUseItem()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;interact(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/EntityHitResult;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;"))
    private InteractionResult aether$trackEntityUse(MultiPlayerGameMode instance, Player player, Entity entity, EntityHitResult hit, InteractionHand hand, Operation<InteractionResult> original) {
        InteractionResult result = original.call(instance, player, entity, hit, hand);
        if (result instanceof InteractionResult.Success) {
            this.aether$useInteractionHandled = true;
        }
        return result;
    }

    @WrapOperation(method = "startUseItem()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;useItem(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;"))
    private InteractionResult aether$trackItemUse(MultiPlayerGameMode instance, Player player, InteractionHand hand, Operation<InteractionResult> original) {
        InteractionResult result = original.call(instance, player, hand);
        if (result instanceof InteractionResult.Success) {
            this.aether$useInteractionHandled = true;
        } else if (result instanceof InteractionResult.Pass) {
            if (hand == InteractionHand.MAIN_HAND) {
                this.aether$mainHandItemPassed = true;
            } else {
                this.aether$offHandItemPassed = true;
            }
        }
        return result;
    }

    @Inject(method = "startUseItem()V", at = @At("TAIL"))
    private void aether$useAccessoryAfterPass(CallbackInfo ci) {
        if (this.aether$useInteractionHandled
            || this.player == null
            || this.gameMode == null
            || this.gameMode.isDestroying()
            || !this.player.isAlive()
            || this.player.isSpectator()
            || this.player.isUsingItem()) {
            return;
        }
        if (!this.aether$mainHandItemPassed && !this.aether$offHandItemPassed) {
            return;
        }
        InteractionHand equipHand = AccessoryItemInteractions.findEquipFromUseHand(this.player, hand ->
            hand == InteractionHand.MAIN_HAND ? this.aether$mainHandItemPassed : this.aether$offHandItemPassed);
        if (equipHand != null) {
            AetherPacketSender.sendToServer(new UseAccessoryPacket(equipHand, false));
        } else if (AccessoryItemInteractions.startUsingShield(this.player)) {
            AetherPacketSender.sendToServer(new UseAccessoryPacket(InteractionHand.OFF_HAND, true));
        }
    }
}
