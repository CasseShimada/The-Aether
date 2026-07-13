package com.aetherteam.aether.client.gui;

import java.util.HashMap;
import java.util.Map;
import java.util.OptionalInt;
import java.util.UUID;

public final class AetherBossBarTracker {
    private static final Map<UUID, Integer> BOSS_EVENTS = new HashMap<>();

    private AetherBossBarTracker() {
    }

    public static void track(UUID bossEvent, int entityId) {
        BOSS_EVENTS.put(bossEvent, entityId);
    }

    public static void untrack(UUID bossEvent) {
        BOSS_EVENTS.remove(bossEvent);
    }

    public static boolean isTracked(UUID bossEvent) {
        return BOSS_EVENTS.containsKey(bossEvent);
    }

    public static OptionalInt entityId(UUID bossEvent) {
        Integer entityId = BOSS_EVENTS.get(bossEvent);
        return entityId != null ? OptionalInt.of(entityId) : OptionalInt.empty();
    }
}
