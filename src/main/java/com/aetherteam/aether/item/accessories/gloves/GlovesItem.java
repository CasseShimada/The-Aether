package com.aetherteam.aether.item.accessories.gloves;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.AetherConfig;
import com.aetherteam.aether.inventory.AetherAccessorySlots;
import com.aetherteam.aether.item.accessories.AccessorySlotProvider;
import com.aetherteam.aether.item.accessories.AccessoryItem;
import com.aetherteam.aether.accessories.api.attributes.AccessoryAttributeBuilder;
import com.aetherteam.aether.accessories.api.slot.SlotReference;
import com.aetherteam.aether.accessories.api.slot.SlotTypeReference;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.ArmorMaterial;

public class GlovesItem extends AccessoryItem implements AccessorySlotProvider {
    public static final Identifier BASE_PUNCH_DAMAGE_ID = Identifier.fromNamespaceAndPath(Aether.MODID, "base_punch_damage");
    protected final ArmorMaterial material;
    protected final double damage;
    protected Identifier GLOVES_TEXTURE;

    public GlovesItem(ArmorMaterial material, double punchDamage, String glovesName, Holder<SoundEvent> glovesSound, Properties properties) {
        this(material, punchDamage, Identifier.fromNamespaceAndPath(Aether.MODID, glovesName), glovesSound, properties);
    }

    public GlovesItem(Holder<ArmorMaterial> material, double punchDamage, String glovesName, SoundEvent glovesSound, Properties properties) {
        this(material.value(), punchDamage, Identifier.fromNamespaceAndPath(Aether.MODID, glovesName), BuiltInRegistries.SOUND_EVENT.wrapAsHolder(glovesSound), properties);
    }

    public GlovesItem(Holder<ArmorMaterial> material, double punchDamage, String glovesName, Holder<SoundEvent> glovesSound, Properties properties) {
        this(material.value(), punchDamage, Identifier.fromNamespaceAndPath(Aether.MODID, glovesName), glovesSound, properties);
    }

    public GlovesItem(ArmorMaterial material, double punchDamage, String glovesName, SoundEvent glovesSound, Properties properties) {
        this(material, punchDamage, Identifier.fromNamespaceAndPath(Aether.MODID, glovesName), BuiltInRegistries.SOUND_EVENT.wrapAsHolder(glovesSound), properties);
    }

    public GlovesItem(ArmorMaterial material, double punchDamage, Identifier glovesName, Holder<SoundEvent> glovesSound, Properties properties) {
        super(glovesSound, properties);
        this.material = material;
        this.damage = punchDamage;
        this.setRenderTexture(glovesName.getNamespace(), glovesName.getPath());
    }

    @Override
    public void getDynamicModifiers(ItemStack stack, SlotReference reference, AccessoryAttributeBuilder builder) {
        builder.addStackable(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_PUNCH_DAMAGE_ID, this.damage, AttributeModifier.Operation.ADD_VALUE));
    }

    public int getEnchantmentValue() {
        return this.material.enchantmentValue();
    }

    public boolean isValidRepairItem(ItemStack item, ItemStack material) {
        return material.is(this.material.repairIngredient());
    }

    public ArmorMaterial getMaterial() {
        return this.material;
    }

    public double getDamage() {
        return this.damage;
    }

    public void setRenderTexture(String modId, String registryName) {
        this.GLOVES_TEXTURE = Identifier.fromNamespaceAndPath(modId, "textures/models/accessory/gloves/" + registryName + "_accessory.png");
    }

    public Identifier getGlovesTexture() {
        return this.GLOVES_TEXTURE;
    }

    /**
     * @return {@link GlovesItem}'s own accessory slot type,
     * using a static method as it is used in other conditions without access to an instance.
     */
    @Override
    public SlotTypeReference getSlotType() {
        return getStaticIdentifier();
    }

    public static SlotTypeReference getStaticIdentifier() {
        return AetherConfig.COMMON.use_default_accessories_menu.get() ? () -> "hand" : AetherAccessorySlots.getGlovesSlotType();
    }
}
