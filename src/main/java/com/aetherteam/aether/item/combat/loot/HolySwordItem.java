package com.aetherteam.aether.item.combat.loot;

import com.aetherteam.aether.item.AetherItems;
import com.aetherteam.aether.item.EquipmentUtil;
import com.aetherteam.aether.item.combat.AetherItemTiers;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

public class HolySwordItem extends SwordItem {
    public HolySwordItem(Item.Properties properties) {
        super(AetherItemTiers.HOLY, SwordItem.createAttributes(AetherItemTiers.HOLY, 3, -2.4F), properties);
    }

    /**
     * Reduces the item's durability by 10 when attacking  undead mobs or mobs that treat healing and harming effects as inverted. This occurs if the attacker attacked with full strength as determined by {@link EquipmentUtil#isFullStrength(LivingEntity)}.
     *
     * @param stack    The {@link ItemStack} used to hurt the target
     * @param target   The hurt {@link LivingEntity}.
     * @param attacker The attacking {@link LivingEntity}.
     * @return Whether the enemy was hurt or not, as a {@link Boolean}.
     */
    @Override
    public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (EquipmentUtil.isFullStrength(attacker)) {
            if (target.getType().builtInRegistryHolder().is(EntityTypeTags.UNDEAD) || target.isInvertedHealAndHarm()) {
                stack.hurtAndBreak(10, attacker, InteractionHand.MAIN_HAND);
            }
        }
        super.hurtEnemy(stack, target, attacker);
    }

    /**
     * Deals bonus damage to undead mobs or mobs that invert healing/harming effects.
     */
    public static float onLivingDamage(LivingEntity target, DamageSource damageSource, float damage) {
        if (canPerformAbility(target, damageSource)) {
            ItemStack itemStack = ((LivingEntity) damageSource.getDirectEntity()).getMainHandItem();
            float bonus = 8.25F;
            int smiteModifier = EnchantmentHelper.getItemEnchantmentLevel(target.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.SMITE), itemStack);
            if (smiteModifier > 0) {
                bonus += (smiteModifier * 2.5F);
            }
            return damage + bonus; // Default ~7 + bonus 8 at minimum.
        }
        return damage;
    }

    /**
     * Basic checks to perform the ability if the source is living, the target is an undead entity, the item is a Holy Sword, and if the attacker attacked with full strength as determined by {@link EquipmentUtil#isFullStrength(LivingEntity)}.
     *
     * @param target The killed {@link LivingEntity}.
     * @param source The attacking {@link DamageSource}.
     */
    private static boolean canPerformAbility(LivingEntity target, DamageSource source) {
        if (source.getDirectEntity() instanceof LivingEntity attacker) {
            if (EquipmentUtil.isFullStrength(attacker)) {
                if (target.getType().builtInRegistryHolder().is(EntityTypeTags.UNDEAD) || target.isInvertedHealAndHarm()) {
                    return attacker.getMainHandItem().is(AetherItems.HOLY_SWORD);
                }
            }
        }
        return false;
    }
}
