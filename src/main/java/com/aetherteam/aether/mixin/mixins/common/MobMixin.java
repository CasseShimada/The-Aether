package com.aetherteam.aether.mixin.mixins.common;

import com.aetherteam.aether.accessories.impl.MobAccessoryEquipment;
import com.aetherteam.aether.accessories.impl.MobAccessorySpawning;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(Mob.class)
public class MobMixin {
    /**
     * Allows {@link Mob}s to accept accessories from {@link net.minecraft.world.entity.EntitySelector.MobCanWearArmorEntitySelector}.
     *
     * @param original Whether an item could have been taken before.
     * @param stack The {@link ItemStack}.
     * @return Whether this {@link Mob} can take from an accessory otherwise whether it could have before.
     */
    @ModifyReturnValue(at = @At(value = "RETURN"), method = "canHoldItem(Lnet/minecraft/world/item/ItemStack;)Z")
    private boolean canTakeItem(boolean original, ItemStack stack) {
        return MobAccessoryEquipment.canHoldItem((Mob) (Object) this, stack, original);
    }

    /**
     * Handles equipping accessories for {@link Mob}s.
     *
     * @param original The {@link ItemStack} returned by the target method.
     * @param stack The {@link ItemStack} provided to the target method.
     */
    @ModifyReturnValue(at = @At(value = "RETURN"), method = "equipItemIfPossible(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/item/ItemStack;")
    private ItemStack equipItemIfPossible(ItemStack original, ServerLevel serverLevel, ItemStack stack) {
        return MobAccessoryEquipment.equipItemIfPossible((Mob) (Object) this, serverLevel, stack, original);
    }

    @Inject(method = "finalizeSpawn", at = @At("RETURN"))
    private void aether$spawnWithAccessories(ServerLevelAccessor level, DifficultyInstance difficulty, EntitySpawnReason spawnReason, @Nullable SpawnGroupData spawnData, CallbackInfoReturnable<SpawnGroupData> cir) {
        MobAccessorySpawning.spawnWithAccessories((Mob) (Object) this, difficulty);
    }
}
