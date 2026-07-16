package com.aetherteam.aether.item.food;

import com.aetherteam.aether.AetherConfig;
import com.aetherteam.aether.item.miscellaneous.ConsumableItem;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.Level;

public class GummySwetItem extends Item implements ConsumableItem {
    public GummySwetItem(Properties properties) {
        super(properties);
    }

    /**
     * Checks if the Gummy Swet can be used either if the player is able to eat it according to {@link Player#canEat(boolean)} if it behaves as food, or if the player is missing health and isn't in creative if it behaves as a healing item.
     *
     * @param level  The {@link Level} of the user.
     * @param player The {@link Player} using this item.
     * @param hand   The {@link InteractionHand} in which the item is being used.
     * @return Consume (cause the item to bob down then up in hand) if the item is successfully used whether it be for eating or healing, or fail (do nothing) if those conditions aren't met.
     */
    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (!(AetherConfig.SERVER_FILE.isLoaded() && AetherConfig.SERVER.healing_gummy_swets.get())) { // Behaves as food.
            FoodProperties foodProperties = AetherFoods.GUMMY_SWET;
            if (player.canEat(foodProperties.canAlwaysEat())) {
                player.startUsingItem(hand);
                return InteractionResult.CONSUME;
            } else {
                return InteractionResult.FAIL;
            }
        } else { // Behaves as direct healing.
            if (player.getHealth() < player.getMaxHealth() && !player.isCreative()) {
                player.startUsingItem(hand);
                return InteractionResult.CONSUME;
            } else {
                return InteractionResult.FAIL;
            }
        }
    }

    /**
     * Performs the {@link LivingEntity#eat(Level, ItemStack)} code if the Gummy Swet is edible/behaves as food.
     * Otherwise, it heals the player, and consumes the item using {@link ConsumableItem#consume(Item, ItemStack, LivingEntity)}.
     *
     * @param stack The {@link ItemStack} in use.
     * @param level The {@link Level} of the user.
     * @param user  The {@link LivingEntity} using the stack.
     * @return The used {@link ItemStack}.
     */
    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity user) {
        if (!(AetherConfig.SERVER_FILE.isLoaded() && AetherConfig.SERVER.healing_gummy_swets.get())) { // Behaves as food.
            return super.finishUsingItem(stack, level, user);
        } else { // Behaves as direct healing.
            user.heal(user.getMaxHealth());
            this.consume(this, stack, user);
            return stack;
        }
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.EAT;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 16;
    }
}
