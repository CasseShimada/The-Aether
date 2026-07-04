package com.aetherteam.aether.accessories.api.equip;

import com.aetherteam.aether.accessories.api.slot.SlotReference;

public record AccessoryEquipResult(SlotReference reference, EquipAction action) {
}
