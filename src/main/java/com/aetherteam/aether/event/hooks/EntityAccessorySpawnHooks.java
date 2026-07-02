package com.aetherteam.aether.event.hooks;

import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.accessories.api.AccessoriesCapability;
import com.aetherteam.aether.accessories.api.AccessoriesContainer;
import com.aetherteam.aether.accessories.api.slot.SlotEntryReference;
import com.aetherteam.aether.accessories.api.slot.SlotTypeReference;
import com.aetherteam.aether.item.AetherItems;
import com.aetherteam.aether.item.accessories.gloves.GlovesItem;
import com.aetherteam.aether.item.accessories.pendant.PendantItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorMaterials;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

final class EntityAccessorySpawnHooks {
    private static final SlotTypeReference[] ALL_ACCESSORY_SLOTS = {
            GlovesItem.getStaticIdentifier(),
            PendantItem.getStaticIdentifier()
    };
    private static final SlotTypeReference[] GLOVE_SLOTS = {
            GlovesItem.getStaticIdentifier()
    };

    private EntityAccessorySpawnHooks() {
    }

    static boolean canMobSpawnWithAccessories(Entity entity) {
        EntityType<?> entityType = entity.getType();
        return entity instanceof Mob
                && (entityType == EntityTypes.ZOMBIE
                || entityType == EntityTypes.ZOMBIE_VILLAGER
                || entityType == EntityTypes.HUSK
                || entityType == EntityTypes.SKELETON
                || entityType == EntityTypes.STRAY
                || entityType == EntityTypes.PIGLIN);
    }

    static void spawnWithAccessories(Entity entity, DifficultyInstance difficulty) {
        if (!(entity instanceof Mob mob) || !(mob.level() instanceof ServerLevel)) {
            return;
        }

        RandomSource random = mob.getRandom();
        if (mob.getType() == EntityTypes.PIGLIN) {
            equipPiglinAccessories(mob, random);
        } else {
            equipArmoredMobAccessories(mob, random);
        }
        enchantAccessories(mob, difficulty);
    }

    static List<ItemStack> handleEntityAccessoryDrops(LivingEntity entity, List<ItemStack> itemStacks, boolean recentlyHit, int looting) {
        if (!(entity instanceof Mob mob)) {
            return itemStacks;
        }

        for (SlotTypeReference identifier : ALL_ACCESSORY_SLOTS) {
            if (itemStacks.isEmpty()) {
                break;
            }

            ItemStack itemStack = itemStacks.getFirst();
            float chance = mob.getAttachedOrCreate(AetherDataAttachments.MOB_ACCESSORY).getEquipmentDropChance(identifier);
            boolean guaranteedDrop = chance > 1.0F;
            if (!itemStack.isEmpty()) {
                itemStacks.removeIf(stack -> ItemStack.isSameItemSameComponents(stack, itemStack));
            }
            if (!itemStack.isEmpty()
                    && EnchantmentHelper.getItemEnchantmentLevel(entity.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.VANISHING_CURSE), itemStack) == 0
                    && recentlyHit
                    && Math.max(mob.getRandom().nextFloat() - (float) looting * 0.01F, 0.0F) < chance) {
                if (!guaranteedDrop && itemStack.isDamageableItem()) {
                    itemStack.setDamageValue(itemStack.getMaxDamage() - mob.getRandom().nextInt(1 + mob.getRandom().nextInt(Math.max(itemStack.getMaxDamage() - 3, 1))));
                }
                itemStacks.add(itemStack);
            }
        }
        return itemStacks;
    }

    static int modifyExperience(LivingEntity entity, int experience) {
        if (!(entity instanceof Mob mob) || !mob.hasAttached(AetherDataAttachments.MOB_ACCESSORY) || experience <= 0) {
            return experience;
        }

        AccessoriesCapability accessories = AccessoriesCapability.get(entity);
        if (accessories == null) {
            return experience;
        }

        for (SlotTypeReference identifier : ALL_ACCESSORY_SLOTS) {
            AccessoriesContainer accessoriesContainer = accessories.getContainer(identifier);
            if (accessoriesContainer == null) {
                continue;
            }

            ItemStack stack = accessoriesContainer.getAccessories().getItem(0);
            if (!stack.isEmpty() && mob.getAttachedOrCreate(AetherDataAttachments.MOB_ACCESSORY).getEquipmentDropChance(identifier) <= 1.0F) {
                experience += 1 + mob.getRandom().nextInt(3);
            }
        }
        return experience;
    }

    private static void equipPiglinAccessories(Mob mob, RandomSource random) {
        if (!(mob instanceof AbstractPiglin abstractPiglin) || !abstractPiglin.isAdult()) {
            return;
        }

        for (SlotTypeReference identifier : ALL_ACCESSORY_SLOTS) {
            if (random.nextFloat() < 0.1F) {
                equipAccessory(mob, identifier, ArmorMaterials.GOLD);
            }
        }
    }

    private static void equipArmoredMobAccessories(Mob mob, RandomSource random) {
        if (!isFullyArmored(mob) || random.nextInt(4) != 1) {
            return;
        }

        if (mob.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof ArmorItem armorItem) {
            for (SlotTypeReference identifier : GLOVE_SLOTS) {
                equipAccessory(mob, identifier, armorItem.getMaterial());
            }
        }
    }

    private static boolean isFullyArmored(Mob mob) {
        for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
            if (equipmentSlot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR && mob.getItemBySlot(equipmentSlot).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private static void equipAccessory(Mob mob, SlotTypeReference identifier, ArmorMaterial armorMaterial) {
        AccessoriesCapability accessories = AccessoriesCapability.get(mob);
        if (accessories == null) {
            return;
        }

        AccessoriesContainer accessoriesContainer = accessories.getContainer(identifier);
        if (accessoriesContainer == null || !isContainerEmpty(accessoriesContainer)) {
            return;
        }

        Item item = getEquipmentForSlot(identifier, armorMaterial);
        if (item != null) {
            accessoriesContainer.getAccessories().setItem(0, new ItemStack(item));
        }
    }

    private static boolean isContainerEmpty(AccessoriesContainer accessoriesContainer) {
        AccessoriesCapability accessories = accessoriesContainer.capability();
        for (SlotEntryReference slotResult : accessories.getAllEquipped()) {
            if (!slotResult.stack().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Nullable
    private static Item getEquipmentForSlot(SlotTypeReference identifier, ArmorMaterial armorMaterial) {
        if (identifier.equals(GlovesItem.getStaticIdentifier())) {
            if (armorMaterial == ArmorMaterials.LEATHER) {
                return AetherItems.LEATHER_GLOVES;
            } else if (armorMaterial == ArmorMaterials.GOLD) {
                return AetherItems.GOLDEN_GLOVES;
            } else if (armorMaterial == ArmorMaterials.CHAINMAIL) {
                return AetherItems.CHAINMAIL_GLOVES;
            } else if (armorMaterial == ArmorMaterials.IRON) {
                return AetherItems.IRON_GLOVES;
            } else if (armorMaterial == ArmorMaterials.DIAMOND) {
                return AetherItems.DIAMOND_GLOVES;
            }
        } else if (identifier.equals(PendantItem.getStaticIdentifier())) {
            if (armorMaterial == ArmorMaterials.IRON) {
                return AetherItems.IRON_PENDANT;
            } else if (armorMaterial == ArmorMaterials.GOLD) {
                return AetherItems.GOLDEN_PENDANT;
            }
        }
        return null;
    }

    private static void enchantAccessories(Mob mob, DifficultyInstance difficulty) {
        RandomSource random = mob.getRandom();
        float chanceMultiplier = difficulty.getSpecialMultiplier();
        AccessoriesCapability accessories = AccessoriesCapability.get(mob);
        if (accessories == null) {
            return;
        }

        for (SlotTypeReference identifier : ALL_ACCESSORY_SLOTS) {
            AccessoriesContainer accessoriesContainer = accessories.getContainer(identifier);
            if (accessoriesContainer == null) {
                continue;
            }

            ItemStack itemStack = accessoriesContainer.getAccessories().getItem(0);
            if (!itemStack.isEmpty() && random.nextFloat() < 0.5F * chanceMultiplier) {
                accessoriesContainer.getAccessories().setItem(0, EnchantmentHelper.enchantItem(random, itemStack, (int) (5.0F + chanceMultiplier * (float) random.nextInt(18)), mob.registryAccess(), Optional.of(mob.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(EnchantmentTags.ON_MOB_SPAWN_EQUIPMENT))));
            }
        }
    }
}
