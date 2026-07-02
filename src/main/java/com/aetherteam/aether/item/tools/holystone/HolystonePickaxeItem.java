package com.aetherteam.aether.item.tools.holystone;

import com.aetherteam.aether.item.combat.AetherItemTiers;
import com.aetherteam.aether.item.tools.abilities.HolystoneTool;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.PickaxeItem;

/**
 * Ambrosium dropping behavior is called by {@link com.aetherteam.aether.event.hooks.ToolAbilityHooks#handleHolystoneToolAbility}.
 */
public class HolystonePickaxeItem extends PickaxeItem implements HolystoneTool {
    public HolystonePickaxeItem() {
        super(AetherItemTiers.HOLYSTONE, PickaxeItem.createAttributes(AetherItemTiers.HOLYSTONE, 1.0F, -2.8F), new Item.Properties());
    }
}
