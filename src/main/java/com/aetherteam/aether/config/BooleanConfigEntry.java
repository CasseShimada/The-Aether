package com.aetherteam.aether.config;

import java.util.List;

public final class BooleanConfigEntry extends AetherConfigEntry<Boolean> {
    BooleanConfigEntry(AetherConfigFile owner, List<String> path, boolean defaultValue, String comment,
                       RestartRequirement restartRequirement, ValueCodec<Boolean> codec) {
        super(owner, path, defaultValue, comment, restartRequirement, codec);
    }
}
