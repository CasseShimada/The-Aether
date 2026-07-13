package com.aetherteam.aether.item.tools.zanite;

import com.aetherteam.aether.item.combat.AetherItemTiers;
import com.aetherteam.aether.item.tools.abilities.ZaniteTool;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.PickaxeItem;

/**
 * Zanite mining speed boost behavior is called by {@link com.aetherteam.aether.item.tools.abilities.ToolAbilities#handleZaniteToolAbility}.
 */
public class ZanitePickaxeItem extends PickaxeItem implements ZaniteTool {
    public ZanitePickaxeItem(Item.Properties properties) {
        super(AetherItemTiers.ZANITE, PickaxeItem.createAttributes(AetherItemTiers.ZANITE, 1.0F, -2.8F), properties);
    }
}
