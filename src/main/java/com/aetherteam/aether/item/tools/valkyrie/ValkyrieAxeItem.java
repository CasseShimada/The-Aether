package com.aetherteam.aether.item.tools.valkyrie;

import com.aetherteam.aether.item.AetherItems;
import com.aetherteam.aether.item.combat.AetherItemTiers;
import com.aetherteam.aether.item.tools.abilities.ValkyrieTool;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;

public class ValkyrieAxeItem extends AxeItem implements ValkyrieTool {
    public ValkyrieAxeItem() {
        super(AetherItemTiers.VALKYRIE, 5.0F, -3.3F, new Item.Properties().rarity(AetherItems.AETHER_LOOT));
    }
}
