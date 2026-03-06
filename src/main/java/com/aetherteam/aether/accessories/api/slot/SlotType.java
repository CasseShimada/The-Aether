package com.aetherteam.aether.accessories.api.slot;

public class SlotType {
    private final String name;
    private final int size;
    private final String translation;

    public SlotType(String name, int size, String translation) {
        this.name = name;
        this.size = size;
        this.translation = translation;
    }

    public String name() {
        return this.name;
    }

    public int size() {
        return this.size;
    }

    public String translation() {
        return this.translation;
    }
}
