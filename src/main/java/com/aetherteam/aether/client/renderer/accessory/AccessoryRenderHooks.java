package com.aetherteam.aether.client.renderer.accessory;

import com.aetherteam.aether.client.AetherClient;
import com.aetherteam.aether.accessories.Accessories;
import com.aetherteam.aether.accessories.api.AccessoriesAPI;
import com.aetherteam.aether.accessories.api.AccessoriesContainer;
import com.aetherteam.aether.accessories.api.slot.SlotEntryReference;
import com.aetherteam.aether.item.accessories.cape.CapeItem;
import com.aetherteam.aether.item.accessories.gloves.GlovesItem;
import com.aetherteam.aether.accessories.api.slot.SlotTypeReference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.equipment.trim.ArmorTrim;

import java.util.Map;
import java.util.function.Predicate;

public final class AccessoryRenderHooks {
    private AccessoryRenderHooks() {
    }

    /**
     * Checks whether a cape accessory is visible.
     *
     * @param livingEntity The {@link LivingEntity} wearing the cape.
     * @return Whether the cape is visible, as a {@link Boolean}.
     * @see com.aetherteam.aether.mixin.mixins.client.AbstractClientPlayerMixin
     */
    public static ItemStack isCapeVisible(LivingEntity livingEntity) {
        ItemStack stack = getVisibleAccessory(livingEntity, CapeItem.getStaticSlotType(), 0);
        return getCapeTexture(stack) != null ? stack : ItemStack.EMPTY;
    }

    public static ItemStack getVisibleWingsAccessory(LivingEntity livingEntity) {
        var accessories = AccessoriesAPI.getAccessories(livingEntity);
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

    public static ItemStack getVisibleAccessory(LivingEntity livingEntity, SlotTypeReference slotType, int slotIndex) {
        var accessories = AccessoriesAPI.getAccessories(livingEntity);
        if (accessories != null) {
            AccessoriesContainer accessoriesContainer = accessories.getContainer(slotType);
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
}
