package com.aetherteam.aether.item.combat;

import com.aetherteam.aether.item.combat.abilities.weapon.HolystoneWeapon;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;

public class HolystoneSwordItem extends SwordItem implements HolystoneWeapon {
    public HolystoneSwordItem(Item.Properties properties) {
        super(AetherItemTiers.HOLYSTONE, SwordItem.createAttributes(AetherItemTiers.HOLYSTONE, 3.0F, -2.4F), properties);
    }

    @Override
    public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        this.dropAmbrosium(target, attacker);
        super.hurtEnemy(stack, target, attacker);
    }
}
