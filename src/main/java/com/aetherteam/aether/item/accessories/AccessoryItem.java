package com.aetherteam.aether.item.accessories;

import com.aetherteam.aether.block.dispenser.AetherDispenseBehaviors;
import com.aetherteam.aether.client.AetherSoundEvents;
import com.aetherteam.aether.registry.DeferredHolder;
import io.wispforest.accessories.api.SoundEventData;
import io.wispforest.accessories.api.core.Accessory;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import org.jetbrains.annotations.Nullable;

public class AccessoryItem extends Item implements Accessory {
    private final Holder<SoundEvent> soundEventSupplier;

    public AccessoryItem(Properties properties) {
        this(AetherSoundEvents.ITEM_ACCESSORY_EQUIP_GENERIC, properties);
    }

    public AccessoryItem(DeferredHolder<SoundEvent, SoundEvent> soundEventSupplier, Properties properties) {
        this(BuiltInRegistries.SOUND_EVENT.wrapAsHolder(soundEventSupplier.get()), properties);
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
    public boolean canEquipFromUse(ItemStack stack, SlotReference reference) {
        return true;
    }

    @Override
    public @Nullable SoundEventData getEquipSound(ItemStack stack, SlotReference reference) {
        return new SoundEventData(this.soundEventSupplier, 1.0F, 1.0F);
    }
}
