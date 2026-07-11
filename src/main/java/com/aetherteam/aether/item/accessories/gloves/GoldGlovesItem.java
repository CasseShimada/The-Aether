package com.aetherteam.aether.item.accessories.gloves;

import com.aetherteam.aether.accessories.api.slot.SlotReference;
import com.aetherteam.aether.item.accessories.abilities.PiglinNeutralInducer;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.equipment.ArmorMaterials;
import net.minecraft.world.item.ItemStack;

public class GoldGlovesItem extends GlovesItem implements PiglinNeutralInducer {
    public GoldGlovesItem(double punchDamage, Properties properties) {
        super(ArmorMaterials.GOLD, punchDamage, "gold_gloves", SoundEvents.ARMOR_EQUIP_GOLD, properties);
    }

    @Override
    public TriState makePiglinsNeutral(ItemStack stack, SlotReference reference) {
        return TriState.TRUE;
    }
}
