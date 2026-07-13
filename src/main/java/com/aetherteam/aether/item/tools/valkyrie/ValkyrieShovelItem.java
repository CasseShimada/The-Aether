package com.aetherteam.aether.item.tools.valkyrie;

import com.aetherteam.aether.item.combat.AetherItemTiers;
import com.aetherteam.aether.item.tools.abilities.ValkyrieTool;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ShovelItem;

public class ValkyrieShovelItem extends ShovelItem implements ValkyrieTool {
    public ValkyrieShovelItem(Item.Properties properties) {
        super(AetherItemTiers.VALKYRIE, 1.5F, -3.3F, properties);
    }
}
