package com.aetherteam.aether.item.miscellaneous.bucket;

import com.aetherteam.aether.item.AetherItems;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MobBucketItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;

public class SkyrootMobBucketItem extends MobBucketItem {
    public SkyrootMobBucketItem(EntityType<? extends Mob> entitySupplier, Fluid fluidSupplier, SoundEvent soundSupplier, Item.Properties properties) {
        super(entitySupplier, fluidSupplier, soundSupplier, properties);
    }

    /**
     * Sets the bucket after usage to a Skyroot Bucket if it returns as a Bucket. Otherwise behavior is the same as {@link MobBucketItem}.
     *
     * @param level  The {@link Level} of the user.
     * @param player The {@link Player} using this item.
     * @param hand   The {@link InteractionHand} in which the item is being used.
     * @return The super interaction result.
     */
    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        InteractionResult result = super.use(level, player, hand);
        if (result.consumesAction() && player.getItemInHand(hand).is(Items.BUCKET)) {
            player.setItemInHand(hand, new ItemStack(AetherItems.SKYROOT_BUCKET.get()));
        }
        return result;
    }
}
