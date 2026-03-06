package com.aetherteam.aether.item.food;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;

public class HealingStoneItem extends Item {
    public HealingStoneItem() {
        super(new Item.Properties().rarity(Rarity.RARE).food(AetherFoods.HEALING_STONE));
    }

    /**
     * @param stack The {@link ItemStack}.
     * @return Whether the item should render an enchantment glint, as a {@link Boolean}.
     */
    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity user) {
        if (!level.isClientSide()) {
            user.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 610, 0));
        }
        return super.finishUsingItem(stack, level, user);
    }
}
