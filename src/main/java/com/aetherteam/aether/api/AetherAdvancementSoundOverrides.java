package com.aetherteam.aether.api;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.api.registers.AdvancementSoundOverride;
import com.aetherteam.aether.client.AetherSoundEvents;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;

public class AetherAdvancementSoundOverrides {
    private static final Map<Identifier, AdvancementSoundOverride> ADVANCEMENT_SOUND_OVERRIDES = new LinkedHashMap<>();

    public static final AdvancementSoundOverride GENERAL = register("general", new AdvancementSoundOverride(0, advancement -> checkRoot(advancement, Identifier.fromNamespaceAndPath(Aether.MODID, "enter_aether")), AetherSoundEvents.UI_TOAST_AETHER_GENERAL));
    public static final AdvancementSoundOverride BRONZE_DUNGEON = register("bronze_dungeon", new AdvancementSoundOverride(10, advancement -> advancement.id().getPath().equals("bronze_dungeon"), AetherSoundEvents.UI_TOAST_AETHER_BRONZE));
    public static final AdvancementSoundOverride SILVER_DUNGEON = register("silver_dungeon", new AdvancementSoundOverride(10, advancement -> advancement.id().getPath().equals("silver_dungeon"), AetherSoundEvents.UI_TOAST_AETHER_SILVER));
    public static final AdvancementSoundOverride GOLD_DUNGEON = register("gold_dungeon", new AdvancementSoundOverride(10, advancement -> advancement.id().getPath().equals("gold_dungeon"), AetherSoundEvents.UI_TOAST_AETHER_GOLD));
    public static final AdvancementSoundOverride EMPTY = register("empty", new AdvancementSoundOverride(10, advancement -> advancement.id().getPath().equals("enter_aether"), SoundEvents.EMPTY));

    private static AdvancementSoundOverride register(String id, AdvancementSoundOverride override) {
        ADVANCEMENT_SOUND_OVERRIDES.put(Identifier.fromNamespaceAndPath(Aether.MODID, id), override);
        return override;
    }

    public static void bootstrap() {
    }

    @Nullable
    public static AdvancementSoundOverride get(String id) {
        return ADVANCEMENT_SOUND_OVERRIDES.get(Identifier.parse(id));
    }

    /**
     * Retrieves the {@link SoundEvent} to use in an override for the given {@link AdvancementHolder}.
     *
     * @param advancement The {@link AdvancementHolder}.
     * @return The new {@link SoundEvent}.
     */
    @Nullable
    public static SoundEvent retrieveOverride(AdvancementHolder advancement) {
        @Nullable AdvancementSoundOverride usedOverride = null;
        for (AdvancementSoundOverride override : ADVANCEMENT_SOUND_OVERRIDES.values()) {
            if (override.matches(advancement) && (usedOverride == null || override.priority() > usedOverride.priority())) {
                usedOverride = override;
            }
        }
        return usedOverride == null ? null : usedOverride.sound();
    }

    /**
     * Checks all the way up to the root of the advancement tree to determine if it matches a given root.
     */
    public static boolean checkRoot(AdvancementHolder holder, Identifier root) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            for (AdvancementHolder current = holder; current != null && current.value().parent().isPresent(); current = player.connection.getAdvancements().get(current.value().parent().get())) {
                if (current.id().equals(root)) {
                    return true;
                }
            }
        }
        return false;
    }
}
