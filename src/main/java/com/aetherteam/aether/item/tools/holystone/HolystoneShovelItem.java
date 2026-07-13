package com.aetherteam.aether.item.tools.holystone;

import com.aetherteam.aether.item.combat.AetherItemTiers;
import com.aetherteam.aether.item.tools.abilities.HolystoneTool;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ShovelItem;

/**
 * Ambrosium dropping behavior is called by {@link com.aetherteam.aether.item.tools.abilities.ToolAbilities#handleHolystoneToolAbility}.
 */
public class HolystoneShovelItem extends ShovelItem implements HolystoneTool {
    public HolystoneShovelItem(Item.Properties properties) {
        super(AetherItemTiers.HOLYSTONE, 1.5F, -3.0F, properties);
    }
}
