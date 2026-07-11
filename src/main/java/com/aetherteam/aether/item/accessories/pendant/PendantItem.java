package com.aetherteam.aether.item.accessories.pendant;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.inventory.AetherAccessorySlots;
import com.aetherteam.aether.item.accessories.AccessorySlotProvider;
import com.aetherteam.aether.item.accessories.AccessoryItem;
import com.aetherteam.aether.accessories.api.slot.SlotTypeReference;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

public class PendantItem extends AccessoryItem implements AccessorySlotProvider {
    protected Identifier PENDANT_LOCATION;

    public PendantItem(String pendantLocation, Holder<SoundEvent> pendantSound, Properties properties) {
        this(Identifier.fromNamespaceAndPath(Aether.MODID, pendantLocation), pendantSound, properties);
    }

    public PendantItem(String pendantLocation, SoundEvent pendantSound, Properties properties) {
        this(Identifier.fromNamespaceAndPath(Aether.MODID, pendantLocation), pendantSound, properties);
    }

    public PendantItem(Identifier pendantLocation, Holder<SoundEvent> pendantSound, Properties properties) {
        super(pendantSound, properties);
        this.setRenderTexture(pendantLocation.getNamespace(), pendantLocation.getPath());
    }

    public PendantItem(Identifier pendantLocation, SoundEvent pendantSound, Properties properties) {
        super(pendantSound, properties);
        this.setRenderTexture(pendantLocation.getNamespace(), pendantLocation.getPath());
    }

    public void setRenderTexture(String modId, String registryName) {
        this.PENDANT_LOCATION = Identifier.fromNamespaceAndPath(modId, "textures/models/accessory/pendant/" + registryName + "_accessory.png");
    }

    public Identifier getPendantTexture() {
        return this.PENDANT_LOCATION;
    }

    /**
     * @return {@link PendantItem}'s own accessory slot type,
     * using a static method as it is used in other conditions without access to an instance.
     */
    @Override
    public SlotTypeReference getSlotType() {
        return getStaticSlotType();
    }

    public static SlotTypeReference getStaticSlotType() {
        return AetherAccessorySlots.getPendantSlotType();
    }
}
