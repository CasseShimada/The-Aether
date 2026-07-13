package com.aetherteam.aether.item.tools.zanite;

import com.aetherteam.aether.item.combat.AetherItemTiers;
import com.aetherteam.aether.item.tools.abilities.ZaniteTool;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;

/**
 * Zanite mining speed boost behavior is called by {@link com.aetherteam.aether.item.tools.abilities.ToolAbilities#handleZaniteToolAbility}.
 */
public class ZaniteHoeItem extends HoeItem implements ZaniteTool {
    public ZaniteHoeItem(Item.Properties properties) {
        super(AetherItemTiers.ZANITE, -2.0F, -1.0F, properties);
    }
}
