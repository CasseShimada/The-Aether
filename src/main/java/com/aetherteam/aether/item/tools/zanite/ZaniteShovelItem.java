package com.aetherteam.aether.item.tools.zanite;

import com.aetherteam.aether.item.combat.AetherItemTiers;
import com.aetherteam.aether.item.tools.abilities.ZaniteTool;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ShovelItem;

/**
 * Zanite mining speed boost behavior is called by {@link com.aetherteam.aether.item.tools.abilities.ToolAbilities#handleZaniteToolAbility}.
 */
public class ZaniteShovelItem extends ShovelItem implements ZaniteTool {
    public ZaniteShovelItem() {
        super(AetherItemTiers.ZANITE, 1.5F, -3.0F, new Item.Properties());
    }
}
