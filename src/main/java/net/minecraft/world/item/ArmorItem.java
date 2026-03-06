package net.minecraft.world.item;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;

import java.util.function.Supplier;

public class ArmorItem extends Item {
    public enum Type {
        HELMET(ArmorType.HELMET),
        CHESTPLATE(ArmorType.CHESTPLATE),
        LEGGINGS(ArmorType.LEGGINGS),
        BOOTS(ArmorType.BOOTS);

        private final ArmorType armorType;

        Type(ArmorType armorType) {
            this.armorType = armorType;
        }

        public int getDurability(int baseDurability) {
            return this.armorType.getDurability(baseDurability);
        }

        public EquipmentSlot getSlot() {
            return this.armorType.getSlot();
        }

        public ArmorType asArmorType() {
            return this.armorType;
        }
    }

    private final Supplier<ArmorMaterial> materialSupplier;
    private final Type type;

    public ArmorItem(Supplier<ArmorMaterial> materialSupplier, Type type, Item.Properties properties) {
        super(properties.humanoidArmor(materialSupplier.get(), type.asArmorType()));
        this.materialSupplier = materialSupplier;
        this.type = type;
    }

    public ArmorMaterial getMaterial() {
        return this.materialSupplier.get();
    }

    public Type getType() {
        return this.type;
    }
}
