package com.aetherteam.aether.accessories.api.attributes;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AccessoryAttributeBuilder {
    private final List<Entry> modifiers = new ArrayList<>();

    public void addStackable(Holder<Attribute> attribute, AttributeModifier modifier) {
        this.modifiers.add(new Entry(attribute, modifier));
    }

    public List<Entry> entries() {
        return Collections.unmodifiableList(this.modifiers);
    }

    public record Entry(Holder<Attribute> attribute, AttributeModifier modifier) {
    }
}
