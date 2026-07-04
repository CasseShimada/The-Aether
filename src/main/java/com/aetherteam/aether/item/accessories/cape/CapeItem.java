package com.aetherteam.aether.item.accessories.cape;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.AetherConfig;
import com.aetherteam.aether.client.AetherSoundEvents;
import com.aetherteam.aether.inventory.AetherAccessorySlots;
import com.aetherteam.aether.item.accessories.AccessorySlotProvider;
import com.aetherteam.aether.item.accessories.AccessoryItem;
import com.aetherteam.aether.accessories.api.slot.SlotTypeReference;
import net.minecraft.resources.Identifier;

public class CapeItem extends AccessoryItem implements AccessorySlotProvider {
    protected Identifier CAPE_LOCATION;

    public CapeItem(String capeLocation, Properties properties) {
        this(Identifier.fromNamespaceAndPath(Aether.MODID, capeLocation), properties);
    }

    public CapeItem(Identifier capeLocation, Properties properties) {
        super(AetherSoundEvents.ITEM_ACCESSORY_EQUIP_CAPE, properties);
        this.setRenderTexture(capeLocation.getNamespace(), capeLocation.getPath());
    }

    public void setRenderTexture(String modId, String registryName) {
        this.CAPE_LOCATION = Identifier.fromNamespaceAndPath(modId, "textures/models/accessory/capes/" + registryName + "_accessory.png");
    }

    public Identifier getCapeTexture() {
        return this.CAPE_LOCATION;
    }


    /**
     * @return {@link CapeItem}'s own accessory slot type,
     * using a static method as it is used in other conditions without access to an instance.
     */
    @Override
    public SlotTypeReference getSlotType() {
        return getStaticIdentifier();
    }

    public static SlotTypeReference getStaticIdentifier() {
        return AetherConfig.COMMON.use_default_accessories_menu.get() ? () -> "cape" : AetherAccessorySlots.getCapeSlotType();
    }
}
