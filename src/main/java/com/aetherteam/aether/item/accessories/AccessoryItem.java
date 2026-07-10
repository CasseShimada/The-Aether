package com.aetherteam.aether.item.accessories;

import com.aetherteam.aether.accessories.api.AccessoriesAPI;
import com.aetherteam.aether.accessories.api.SoundEventData;
import com.aetherteam.aether.accessories.api.core.Accessory;
import com.aetherteam.aether.accessories.api.slot.SlotReference;
import com.aetherteam.aether.accessories.impl.AccessoriesState;
import com.aetherteam.aether.block.dispenser.AetherDispenseBehaviors;
import com.aetherteam.aether.client.AetherSoundEvents;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import org.jetbrains.annotations.Nullable;

public class AccessoryItem extends Item implements Accessory {
    private final Holder<SoundEvent> soundEventSupplier;

    public AccessoryItem(Properties properties) {
        this(AetherSoundEvents.ITEM_ACCESSORY_EQUIP_GENERIC, properties);
    }

    public AccessoryItem(SoundEvent soundEvent, Properties properties) {
        this(BuiltInRegistries.SOUND_EVENT.wrapAsHolder(soundEvent), properties);
    }

    public AccessoryItem(Holder<SoundEvent> soundEventSupplier, Properties properties) {
        super(properties);
        this.soundEventSupplier = soundEventSupplier;
        DispenserBlock.registerBehavior(this, AetherDispenseBehaviors.DISPENSE_ACCESSORY_BEHAVIOR); // Behavior to allow accessories to be equipped from a Dispenser.
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack heldStack = player.getItemInHand(hand);
        if (heldStack.isEmpty()) {
            return InteractionResult.PASS;
        }

        var accessories = AccessoriesAPI.getAccessories(player);
        if (accessories == null) {
            return InteractionResult.PASS;
        }

        Accessory accessory = AccessoriesAPI.getOrDefaultAccessory(heldStack);
        var equipReference = accessories.canEquipAccessory(heldStack, true, reference -> canEquipFromUse(accessory, heldStack, reference));
        if (equipReference == null) {
            return InteractionResult.PASS;
        }

        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        ItemStack equippedStack = player.getAbilities().instabuild ? heldStack.copyWithCount(1) : heldStack.split(1);
        SlotReference reference = equipReference.reference();
        accessory.onEquipFromUse(equippedStack, reference);
        equipReference.action().accept(equippedStack.copy());

        SoundEventData equipSound = accessory.getEquipSound(equippedStack, reference);
        if (equipSound != null) {
            level.playSound(null, player.getX(), player.getY(), player.getZ(), equipSound.event().value(), player.getSoundSource(), equipSound.volume(), equipSound.pitch());
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        return InteractionResult.CONSUME;
    }

    @Override
    public boolean canEquipFromUse(ItemStack stack, SlotReference reference) {
        return true;
    }

    @Override
    public @Nullable SoundEventData getEquipSound(ItemStack stack, SlotReference reference) {
        return new SoundEventData(this.soundEventSupplier, 1.0F, 1.0F);
    }

    private static boolean canEquipFromUse(Accessory accessory, ItemStack stack, SlotReference reference) {
        var definition = AccessoriesState.getSlot(reference.slotName());
        return definition != null
                && definition.allowEquipFromUse()
                && accessory.canEquip(stack, reference)
                && accessory.canEquipFromUse(stack, reference);
    }
}
