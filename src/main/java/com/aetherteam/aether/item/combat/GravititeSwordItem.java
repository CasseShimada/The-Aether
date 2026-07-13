package com.aetherteam.aether.item.combat;

import com.aetherteam.aether.item.combat.abilities.weapon.GravititeWeapon;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;

public class GravititeSwordItem extends SwordItem implements GravititeWeapon {
    public GravititeSwordItem(Item.Properties properties) {
        super(AetherItemTiers.GRAVITITE, SwordItem.createAttributes(AetherItemTiers.GRAVITITE, 3.0F, -2.4F), properties);
    }

    @Override
    public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        this.launchEntity(target, attacker);
        super.hurtEnemy(stack, target, attacker);
    }
}
