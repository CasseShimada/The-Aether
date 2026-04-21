package com.aetherteam.aether.event.hooks;

import com.aetherteam.aether.client.AetherSoundEvents;
import com.aetherteam.aether.entity.passive.FlyingCow;
import com.aetherteam.aether.item.AetherItems;
import com.aetherteam.aether.item.miscellaneous.bucket.SkyrootBucketItem;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.animal.cow.Cow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.level.Level;

import java.util.Optional;

final class EntityBucketHooks {
    private EntityBucketHooks() {
    }

    static void skyrootBucketMilking(Entity target, Player player, InteractionHand hand) {
        if (!canMilkWithSkyrootBucket(target, player, hand)) {
            return;
        }

        ItemStack heldStack = player.getItemInHand(hand);
        if (target instanceof FlyingCow) {
            player.playSound(AetherSoundEvents.ENTITY_FLYING_COW_MILK.get(), 1.0F, 1.0F);
        } else {
            player.playSound(SoundEvents.COW_MILK, 1.0F, 1.0F);
        }
        ItemStack filledBucket = ItemUtils.createFilledResult(heldStack, player, AetherItems.SKYROOT_MILK_BUCKET.get().getDefaultInstance());
        player.swing(hand);
        player.setItemInHand(hand, filledBucket);
    }

    static Optional<InteractionResult> pickupBucketable(Entity target, Player player, InteractionHand hand) {
        ItemStack heldStack = player.getItemInHand(hand);
        if (!heldStack.is(AetherItems.SKYROOT_WATER_BUCKET.get())
                || !(target instanceof Bucketable bucketable)
                || !(target instanceof LivingEntity livingEntity)
                || !livingEntity.isAlive()) {
            return Optional.empty();
        }

        ItemStack bucketStack = SkyrootBucketItem.swapBucketType(bucketable.getBucketItemStack());
        if (bucketStack.isEmpty()) {
            return Optional.of(InteractionResult.FAIL);
        }

        target.playSound(bucketable.getPickupSound(), 1.0F, 1.0F);
        bucketable.saveToBucketTag(bucketStack);
        ItemStack filledStack = ItemUtils.createFilledResult(heldStack, player, bucketStack, false);
        player.setItemInHand(hand, filledStack);
        Level level = livingEntity.level();
        if (!level.isClientSide()) {
            CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayer) player, bucketStack);
        }
        target.discard();
        return Optional.of(level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER);
    }

    private static boolean canMilkWithSkyrootBucket(Entity target, Player player, InteractionHand hand) {
        return (target instanceof Cow || target instanceof FlyingCow)
                && !((Animal) target).isBaby()
                && player.getItemInHand(hand).is(AetherItems.SKYROOT_BUCKET.get());
    }
}
