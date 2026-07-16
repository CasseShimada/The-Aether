package com.aetherteam.aether.data;

import com.aetherteam.aether.AetherConfig;
import com.aetherteam.aether.config.AetherConfigEntry;
import com.aetherteam.aether.config.AetherConfigFile;
import com.aetherteam.aether.config.BooleanConfigEntry;
import com.google.gson.JsonSyntaxException;

import java.util.List;

public final class ConfigSerializationUtil {
    /**
     * Create a serializable string out of a config value's path.
     *
     * @param config The {@link BooleanConfigEntry} to serialize from.
     * @return The serializable {@link String}.
     */
    public static String serialize(BooleanConfigEntry config) {
        return AetherConfigFile.serializePath(config.path());
    }

    /**
     * Gets a config value out of a serialized string.
     *
     * @param string The {@link String} to deserialize from.
     * @return The deserialized {@link BooleanConfigEntry}.
     */
    public static BooleanConfigEntry deserialize(String string) {
        List<String> path = AetherConfigFile.parseSerializedPath(string);
        AetherConfigEntry<?> config = AetherConfig.SERVER_FILE.entries().get(path);
        if (config == null) {
            config = AetherConfig.COMMON_FILE.entries().get(path);
        }
        if (config instanceof BooleanConfigEntry booleanConfig) {
            return booleanConfig;
        }
        throw new JsonSyntaxException("Unknown or non-boolean Aether config entry " + string);
    }
}
