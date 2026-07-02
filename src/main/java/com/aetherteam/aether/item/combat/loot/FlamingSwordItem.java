package com.aetherteam.aether.item.combat.loot;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.item.AetherItems;
import com.aetherteam.aether.item.EquipmentUtil;
import com.aetherteam.aether.item.combat.AetherItemTiers;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

public class FlamingSwordItem extends SwordItem {
    public FlamingSwordItem() {
        super(AetherItemTiers.FLAMING, SwordItem.createAttributes(AetherItemTiers.FLAMING, 3.0F, -2.4F), new Item.Properties().rarity(AetherItems.AETHER_LOOT));
    }

    public static void onLivingDamage(LivingEntity target, DamageSource damageSource) {
        handleFlamingSwordAbility(target, damageSource);
    }

    /**
     * Inflicts 30 seconds of fire on the target with an extra 4 seconds for every level of Fire Aspect the item has. This occurs if the attacker attacked with full strength as determined by {@link EquipmentUtil#isFullStrength(LivingEntity)}.
     *
     * @param target The damaged {@link LivingEntity}.
     * @param source The attacking {@link DamageSource}.
     */
    private static void handleFlamingSwordAbility(LivingEntity target, DamageSource source) {
        if (source.getDirectEntity() instanceof LivingEntity attacker) {
            if (EquipmentUtil.isFullStrength(attacker)) {
                ItemStack heldStack = attacker.getMainHandItem();
                if (heldStack.is(AetherItems.FLAMING_SWORD)) {
                    int defaultTime = 30;
                    int fireAspectModifier = EnchantmentHelper.getEnchantmentLevel(attacker.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.FIRE_ASPECT), attacker);
                    if (fireAspectModifier > 0) {
                        defaultTime += (fireAspectModifier * 4);
                    }
                    target.igniteForSeconds(defaultTime);
                }
            }
        }
    }
}
