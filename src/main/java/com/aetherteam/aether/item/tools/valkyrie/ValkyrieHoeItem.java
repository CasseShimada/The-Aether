package com.aetherteam.aether.item.tools.valkyrie;

import com.aetherteam.aether.item.AetherItems;
import com.aetherteam.aether.item.combat.AetherItemTiers;
import com.aetherteam.aether.item.tools.abilities.ValkyrieTool;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;

public class ValkyrieHoeItem extends HoeItem implements ValkyrieTool {
    public ValkyrieHoeItem() {
        super(AetherItemTiers.VALKYRIE, -3.0F, -0.3F, new Item.Properties().rarity(AetherItems.AETHER_LOOT));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        return super.useOn(context);
    }
}
