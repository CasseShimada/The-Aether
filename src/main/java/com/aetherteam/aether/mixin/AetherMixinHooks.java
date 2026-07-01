package com.aetherteam.aether.mixin;

import com.aetherteam.aether.client.AetherClient;
import com.aetherteam.aether.client.ClientCompat;
import com.aetherteam.aether.accessories.Accessories;
import com.aetherteam.aether.accessories.api.AccessoriesCapability;
import com.aetherteam.aether.accessories.api.AccessoriesContainer;
import com.aetherteam.aether.accessories.api.slot.SlotEntryReference;
import com.aetherteam.aether.item.accessories.cape.CapeItem;
import com.aetherteam.aether.item.accessories.gloves.GlovesItem;
import com.aetherteam.aether.item.accessories.pendant.PendantItem;
import com.aetherteam.aether.mixin.mixins.common.accessor.MinecraftServerAccessor;
import com.aetherteam.aether.accessories.api.slot.SlotTypeReference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import java.nio.file.Path;
import java.util.Map;
import java.util.function.Predicate;

public class AetherMixinHooks {
    /**
     * Checks whether a cape accessory is visible.
     *
     * @param livingEntity The {@link LivingEntity} wearing the cape.
     * @return Whether the cape is visible, as a {@link Boolean}.
     * @see com.aetherteam.aether.mixin.mixins.client.PlayerSkinMixin
     */
    public static ItemStack isCapeVisible(LivingEntity livingEntity) {
        ItemStack stack = getVisibleAccessory(livingEntity, CapeItem.getStaticIdentifier(), 0);
        return getCapeTexture(stack) != null ? stack : ItemStack.EMPTY;
    }

    public static ItemStack getVisibleWingsAccessory(LivingEntity livingEntity) {
        AccessoriesCapability accessories = AccessoriesCapability.get(livingEntity);
        if (accessories != null) {
            for (SlotEntryReference reference : accessories.getAllEquipped()) {
                AccessoriesContainer accessoriesContainer = accessories.getContainer(reference.reference().type());
                ItemStack stack = getVisibleAccessory(accessoriesContainer, reference.reference().slot());
                if (stack.is(Items.ELYTRA)) {
                    return stack;
                }
            }
        }
        return ItemStack.EMPTY;
    }

    /**
     * Gets the cape texture from a {@link CapeItem}.
     *
     * @param stack The {@link ItemStack}.
     * @return The {@link Identifier} texture from the cape.
     */
    public static Identifier getCapeTexture(ItemStack stack) {
        if (stack.getItem() instanceof CapeItem capeItem) {
            for (Map.Entry<Predicate<ItemStack>, Identifier> entry : AetherClient.CAPE_SECRETS.entrySet()) {
                if (entry.getKey().test(stack)) {
                    return entry.getValue();
                }
            }
            return capeItem.getCapeTexture();
        }
        return null;
    }

    public static TextureAtlasSprite getHumanoidArmorTrimSprite(ItemStack stack, GlovesItem glovesItem) {
        ArmorTrim trim = stack.get(DataComponents.TRIM);
        if (trim == null) {
            return null;
        }
        return Minecraft.getInstance()
            .getAtlasManager()
            .getAtlasOrThrow(Sheets.ARMOR_TRIMS_SHEET)
            .getSprite(trim.layerAssetId("humanoid", glovesItem.getMaterial().assetId()));
    }

    public static RenderType getArmorTrimRenderType(ItemStack stack) {
        ArmorTrim trim = stack.get(DataComponents.TRIM);
        return trim != null ? Sheets.armorTrimsSheet(trim.pattern().value().decal()) : null;
    }

    public static ItemStack getVisibleAccessory(LivingEntity livingEntity, SlotTypeReference identifier, int slotIndex) {
        AccessoriesCapability accessories = AccessoriesCapability.get(livingEntity);
        if (accessories != null) {
            AccessoriesContainer accessoriesContainer = accessories.getContainer(identifier);
            return getVisibleAccessory(accessoriesContainer, slotIndex);
        }
        return ItemStack.EMPTY;
    }

    private static ItemStack getVisibleAccessory(AccessoriesContainer accessoriesContainer, int slotIndex) {
        if (accessoriesContainer != null && accessoriesContainer.shouldRender(slotIndex)) {
            ItemStack stack = accessoriesContainer.getAccessories().getItem(slotIndex);
            ItemStack cosmeticStack = accessoriesContainer.getCosmeticAccessories().getItem(slotIndex);
            if (!cosmeticStack.isEmpty() && Accessories.config().clientOptions.showCosmeticAccessories()) {
                stack = cosmeticStack;
            }
            return stack;
        }
        return ItemStack.EMPTY;
    }

    /**
     * Checks whether the {@link SelectWorldScreen} is open and the level that the lock belongs to is the same one as the level loaded by the world preview.
     *
     * @param basePath The {@link Path} for the level directory.
     * @return Whether the level can be unlocked, as a {@link Boolean}.
     * @see com.aetherteam.aether.mixin.mixins.common.DirectoryLockMixin
     */
    public static boolean canUnlockLevel(Path basePath) {
        if (ClientCompat.screen(Minecraft.getInstance()) instanceof SelectWorldScreen && Minecraft.getInstance().getSingleplayerServer() != null) {
            return basePath.getFileName().toString().equals(((MinecraftServerAccessor) Minecraft.getInstance().getSingleplayerServer()).aether$getStorageSource().getLevelId());
        }
        return false;
    }

    /**
     * Whether an accessory can be equipped or replace an already equipped accessory.
     *
     * @param mob       The {@link Mob} to equip the accessory to.
     * @param candidate The {@link ItemStack} to try to equip.
     * @param existing  The {@link ItemStack} already equipped.
     * @return Whether the accessory can be equipped or replaced, as a {@link Boolean}.
     */
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

    /**
     * Gets the corresponding slot identifier for an accessory item.
     *
     * @param livingEntity The {@link LivingEntity} to get the accessory from.
     * @param stack        The accessory {@link ItemStack}.
     * @return The slot identifier {@link String}.
     */
    public static SlotTypeReference getIdentifierForItem(LivingEntity livingEntity, ItemStack stack) {
        if (stack.getItem() instanceof GlovesItem glovesItem) {
            return glovesItem.getIdentifier();
        } else if (stack.getItem() instanceof PendantItem pendantItem && (livingEntity.getType() == EntityTypes.PIGLIN || livingEntity.getType() == EntityTypes.ZOMBIFIED_PIGLIN)) {
            return pendantItem.getIdentifier();
        }
        return null;
    }

    /**
     * Gets an accessory from an entity.
     *
     * @param livingEntity The {@link LivingEntity} to get the accessory from.
     * @param identifier The {@link SlotTypeReference} for the slot identifier.
     * @return The accessory {@link ItemStack} gotten from the entity.
     */
    public static ItemStack getItemByIdentifier(LivingEntity livingEntity, SlotTypeReference identifier) {
        AccessoriesCapability accessories = AccessoriesCapability.get(livingEntity);
        if (accessories != null) {
            AccessoriesContainer accessoriesContainer = accessories.getContainer(identifier);
            if (accessoriesContainer != null) {
                return accessoriesContainer.getAccessories().getItem(0);
            }
        }
        return ItemStack.EMPTY;
    }

    /**
     * Equips an accessory to an entity.
     *
     * @param livingEntity The {@link LivingEntity} to equip to.
     * @param itemStack    The {@link ItemStack} to equip.
     * @param identifier   The {@link SlotTypeReference} for the slot identifier.
     */
    public static void setItemByIdentifier(LivingEntity livingEntity, ItemStack itemStack, SlotTypeReference identifier) {
        AccessoriesCapability accessories = AccessoriesCapability.get(livingEntity);
        if (accessories != null) {
            AccessoriesContainer accessoriesContainer = accessories.getContainer(identifier);
            if (accessoriesContainer != null) {
                accessoriesContainer.getAccessories().setItem(0, itemStack);
            }
        }
    }
}
