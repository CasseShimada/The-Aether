package net.minecraft.world.item;

import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public class PickaxeItem extends Item {
    private final ToolMaterial material;

    public PickaxeItem(ToolMaterial material, ItemAttributeModifiers attributes, Item.Properties properties) {
        super(properties.pickaxe(material, 1.0F, -2.8F).attributes(attributes));
        this.material = material;
    }

    public PickaxeItem(ToolMaterial material, Item.Properties properties) {
        this(material, createAttributes(material, 1.0F, -2.8F), properties);
    }

    public ToolMaterial getTier() {
        return this.material;
    }

    public ToolMaterial getMaterial() {
        return this.material;
    }

    public static ItemAttributeModifiers createAttributes(ToolMaterial material, float attackDamage, float attackSpeed) {
        return ItemAttributeModifiers.builder()
                .add(
                        Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, attackDamage + material.attackDamageBonus(), AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND
                )
                .add(
                        Attributes.ATTACK_SPEED,
                        new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, attackSpeed, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND
                )
                .build();
    }
}
