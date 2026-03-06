package com.aetherteam.aether.accessories.api.attributes;

import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AccessoryAttributeBuilder {
    private final List<AttributeModifier> modifiers = new ArrayList<>();

    public void addStackable(Object attribute, AttributeModifier modifier) {
        this.modifiers.add(modifier);
    }

    public List<AttributeModifier> modifiers() {
        return Collections.unmodifiableList(this.modifiers);
    }
}
