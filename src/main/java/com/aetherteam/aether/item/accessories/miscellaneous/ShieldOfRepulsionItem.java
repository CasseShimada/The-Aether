package com.aetherteam.aether.item.accessories.miscellaneous;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.AetherConfig;
import com.aetherteam.aether.inventory.AetherAccessorySlots;
import com.aetherteam.aether.item.accessories.AccessorySlotProvider;
import com.aetherteam.aether.item.accessories.AccessoryItem;
import com.aetherteam.aether.accessories.api.slot.SlotTypeReference;
import net.minecraft.resources.Identifier;

public class ShieldOfRepulsionItem extends AccessoryItem implements AccessorySlotProvider {
    private static final Identifier SHIELD_OF_REPULSION = Identifier.fromNamespaceAndPath(Aether.MODID, "textures/models/accessory/shield_of_repulsion/shield_of_repulsion_accessory.png");
    private static final Identifier SHIELD_OF_REPULSION_INACTIVE = Identifier.fromNamespaceAndPath(Aether.MODID, "textures/models/accessory/shield_of_repulsion/shield_of_repulsion_inactive_accessory.png");
    private static final Identifier SHIELD_OF_REPULSION_SLIM = Identifier.fromNamespaceAndPath(Aether.MODID, "textures/models/accessory/shield_of_repulsion/shield_of_repulsion_slim_accessory.png");
    private static final Identifier SHIELD_OF_REPULSION_SLIM_INACTIVE = Identifier.fromNamespaceAndPath(Aether.MODID, "textures/models/accessory/shield_of_repulsion/shield_of_repulsion_slim_inactive_accessory.png");

    public ShieldOfRepulsionItem(Properties properties) {
        super(properties);
    }

    public Identifier getShieldOfRepulsionTexture() {
        return SHIELD_OF_REPULSION;
    }

    public Identifier getShieldOfRepulsionInactiveTexture() {
        return SHIELD_OF_REPULSION_INACTIVE;
    }

    public Identifier getShieldOfRepulsionSlimTexture() {
        return SHIELD_OF_REPULSION_SLIM;
    }

    public Identifier getShieldOfRepulsionSlimInactiveTexture() {
        return SHIELD_OF_REPULSION_SLIM_INACTIVE;
    }

    /**
     * @return {@link ShieldOfRepulsionItem}'s own accessory slot type,
     * using a static method as it is used in other conditions without access to an instance.
     */
    @Override
    public SlotTypeReference getSlotType() {
        return getStaticIdentifier();
    }

    public static SlotTypeReference getStaticIdentifier() {
        return AetherConfig.COMMON.use_default_accessories_menu.get() ? () -> "back" : AetherAccessorySlots.getShieldSlotType();
    }
}
