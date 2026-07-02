package com.aetherteam.aether.event.hooks;

import com.aetherteam.aether.accessories.api.AccessoriesAPI;
import com.aetherteam.aether.accessories.api.AccessoriesContainer;
import com.aetherteam.aether.accessories.api.slot.SlotTypeReference;
import com.aetherteam.aether.item.accessories.gloves.GlovesItem;
import com.aetherteam.aether.item.accessories.pendant.PendantItem;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public final class EntityAccessoryEquipHooks {
    private EntityAccessoryEquipHooks() {
    }

    public static boolean canReplaceCurrentAccessory(Mob mob, ItemStack candidate, ItemStack existing) {
        if (EnchantmentHelper.hasAnyEnchantments(existing)) {
            return false;
        } else {
            if (candidate.getItem() instanceof GlovesItem candidateGloves) {
                if (!(existing.getItem() instanceof GlovesItem existingGloves)) {
                    return true;
                } else {
                    if (candidateGloves.getDamage() != existingGloves.getDamage()) {
                        return candidateGloves.getDamage() > existingGloves.getDamage();
                    } else {
                        return mob.canReplaceEqualItem(candidate, existing);
                    }
                }
            } else if (candidate.getItem() instanceof PendantItem) {
                if (!(existing.getItem() instanceof PendantItem)) {
                    return true;
                } else {
                    return mob.canReplaceEqualItem(candidate, existing);
                }
            }
        }
        return false;
    }

    public static SlotTypeReference getIdentifierForItem(LivingEntity livingEntity, ItemStack stack) {
        if (stack.getItem() instanceof GlovesItem glovesItem) {
            return glovesItem.getIdentifier();
        } else if (stack.getItem() instanceof PendantItem pendantItem && (livingEntity.getType() == EntityTypes.PIGLIN || livingEntity.getType() == EntityTypes.ZOMBIFIED_PIGLIN)) {
            return pendantItem.getIdentifier();
        }
        return null;
    }

    public static ItemStack getItemByIdentifier(LivingEntity livingEntity, SlotTypeReference identifier) {
        var accessories = AccessoriesAPI.getAccessories(livingEntity);
        if (accessories != null) {
            AccessoriesContainer accessoriesContainer = accessories.getContainer(identifier);
            if (accessoriesContainer != null) {
                return accessoriesContainer.getAccessories().getItem(0);
            }
        }
        return ItemStack.EMPTY;
    }

    public static void setItemByIdentifier(LivingEntity livingEntity, ItemStack itemStack, SlotTypeReference identifier) {
        var accessories = AccessoriesAPI.getAccessories(livingEntity);
        if (accessories != null) {
            AccessoriesContainer accessoriesContainer = accessories.getContainer(identifier);
            if (accessoriesContainer != null) {
                accessoriesContainer.getAccessories().setItem(0, itemStack);
            }
        }
    }
}
