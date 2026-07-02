package com.aetherteam.aether.accessories.api;

import com.aetherteam.aether.accessories.api.equip.EquipAction;
import com.aetherteam.aether.accessories.api.slot.SlotEntryReference;
import com.aetherteam.aether.accessories.api.slot.SlotReference;
import com.aetherteam.aether.accessories.api.slot.SlotTypeReference;
import com.aetherteam.aether.network.packet.clientbound.AccessorySyncPacket;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

/**
 * Current per-entity Aether accessory storage exposed through {@link AccessoriesAPI}.
 */
public interface AccessoriesStorage {
    @Nullable
    AccessoriesContainer getContainer(SlotTypeReference slotTypeReference);

    @Nullable
    Pair<SlotReference, EquipAction> canEquipAccessory(ItemStack stack, boolean requireEmptySlot);

    @Nullable
    Pair<SlotReference, EquipAction> canEquipAccessory(ItemStack stack, boolean requireEmptySlot, Predicate<SlotReference> slotFilter);

    List<SlotEntryReference> getEquipped(Item item);

    @Nullable
    SlotEntryReference getFirstEquipped(Predicate<ItemStack> predicate);

    List<SlotEntryReference> getAllEquipped();

    void clearAccessories(boolean clearCosmeticAccessories);

    void process(boolean runAccessoryTick);

    boolean consumeSyncDirty();

    AccessorySyncPacket createSyncPacket();

    void applyClientSync(AccessorySyncPacket packet);

    void clearRuntimeState(boolean invokeUnequipCallbacks);

    void handleImmediateUnequip(SlotReference reference);

    void handleImmediateStackMutation(SlotReference reference);
}
