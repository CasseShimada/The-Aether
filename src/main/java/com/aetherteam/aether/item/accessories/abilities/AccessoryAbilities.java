package com.aetherteam.aether.item.accessories.abilities;

import com.aetherteam.aether.AetherTags;
import com.aetherteam.aether.accessories.api.AccessoriesAPI;
import com.aetherteam.aether.accessories.api.slot.SlotEntryReference;
import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.item.EquipmentUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.List;

public final class AccessoryAbilities {
    private AccessoryAbilities() {
    }

    /**
     * Damages an entity's Gloves when they hurt another entity.
     *
     * @param player The attacking {@link Player}.
     * @see com.aetherteam.aether.mixin.mixins.common.PlayerMixin#attack(Entity, CallbackInfo)
     */
    public static void damageGloves(Player player) {
        SlotEntryReference slotResult = EquipmentUtil.getGloves(player);
        if (slotResult != null) {
            damageAccessoryItem(slotResult, player, 1);
        }
    }

    /**
     * Damages Zanite Rings when a block is broken.
     */
    public static void damageZaniteRing(LivingEntity entity, LevelAccessor level, BlockState state, BlockPos pos) {
        List<SlotEntryReference> slotResults = EquipmentUtil.getZaniteRings(entity);
        for (SlotEntryReference slotResult : slotResults) {
            if (slotResult != null) {
                if (state.getDestroySpeed(level, pos) > 0 && entity.getRandom().nextInt(6) == 0) {
                    damageAccessoryItem(slotResult, entity, 1);
                }
            }
        }
    }

    /**
     * Damages Zanite Pendant when a block is broken.
     */
    public static void damageZanitePendant(LivingEntity entity, LevelAccessor level, BlockState state, BlockPos pos) {
        SlotEntryReference slotResult = EquipmentUtil.getZanitePendant(entity);
        if (slotResult != null) {
            if (state.getDestroySpeed(level, pos) > 0 && entity.getRandom().nextInt(6) == 0) {
                damageAccessoryItem(slotResult, entity, 1);
            }
        }
    }

    private static void damageAccessoryItem(SlotEntryReference slotResult, LivingEntity entity, int amount) {
        if (entity.level().isClientSide() || amount <= 0) {
            return;
        }

        if (entity.level() instanceof ServerLevel serverLevel && entity instanceof ServerPlayer serverPlayer) {
            slotResult.stack().hurtAndBreak(amount, serverLevel, serverPlayer, (item) -> AccessoriesAPI.breakStack(slotResult.reference()));
            return;
        }

        ItemStack stack = slotResult.stack();
        stack.hurtAndBreak(amount, entity, EquipmentSlot.MAINHAND);
        if (stack.isEmpty()) {
            slotResult.reference().setStack(ItemStack.EMPTY);
        }
    }

    /**
     * Handles ability for {@link ZaniteAccessory} for Zanite Rings (accounts for if multiple are equipped).
     * @see ZaniteAccessory#handleMiningSpeed(float, ItemStack)
     */
    public static float handleZaniteRingAbility(LivingEntity entity, float speed) {
        float newSpeed = speed;
        List<SlotEntryReference> slotResults = EquipmentUtil.getZaniteRings(entity);
        for (SlotEntryReference slotResult : slotResults) {
            if (slotResult != null) {
                newSpeed = ZaniteAccessory.handleMiningSpeed(newSpeed, slotResult.stack());
            }
        }
        return newSpeed;
    }

    /**
     * Handles ability for {@link ZaniteAccessory} for the Zanite Pendant.
     * @see ZaniteAccessory#handleMiningSpeed(float, ItemStack)
     */
    public static float handleZanitePendantAbility(LivingEntity entity, float speed) {
        SlotEntryReference slotResult = EquipmentUtil.getZanitePendant(entity);
        if (slotResult != null) {
            speed = ZaniteAccessory.handleMiningSpeed(speed, slotResult.stack());
        }
        return speed;
    }

    /**
     * Checks whether an entity can be targeted while wearing an Invisibility Cloak.
     */
    public static boolean preventTargeting(LivingEntity target, @Nullable Entity lookingEntity) {
        if (target instanceof Player player) {
            var data = player.getAttachedOrCreate(AetherDataAttachments.AETHER_PLAYER);
            return lookingEntity != null
                    && !lookingEntity.getType().builtInRegistryHolder().is(AetherTags.Entities.IGNORE_INVISIBILITY)
                    && data.isWearingInvisibilityCloak()
                    && data.isInvisibilityEnabled()
                    && !data.attackedWithInvisibility();
        } else {
            return lookingEntity != null
                    && !lookingEntity.getType().builtInRegistryHolder().is(AetherTags.Entities.IGNORE_INVISIBILITY)
                    && EquipmentUtil.hasInvisibilityCloak(target);
        }
    }

    /**
     * Checks if an entity recently attacked while wearing an Invisibility Cloak.
     */
    public static boolean recentlyAttackedWithInvisibility(LivingEntity target, Entity lookingEntity) {
        if (target instanceof Player player) {
            var data = player.getAttachedOrCreate(AetherDataAttachments.AETHER_PLAYER);
            return !lookingEntity.getType().builtInRegistryHolder().is(AetherTags.Entities.IGNORE_INVISIBILITY)
                    && data.isWearingInvisibilityCloak()
                    && data.isInvisibilityEnabled()
                    && data.attackedWithInvisibility();
        } else {
            return false;
        }
    }

    /**
     * Sets that the player recently attacked.
     */
    public static void setAttack(DamageSource source) {
        if (source.getEntity() instanceof Player player) {
            player.getAttachedOrCreate(AetherDataAttachments.AETHER_PLAYER).setAttackedWithInvisibility(true);
        }
    }

    /**
     * Prevents magma block damage when wearing ice accessories.
     */
    public static boolean preventMagmaDamage(LivingEntity entity, DamageSource source) {
        return source == entity.level().damageSources().hotFloor() && EquipmentUtil.hasFreezingAccessory(entity);
    }
}
