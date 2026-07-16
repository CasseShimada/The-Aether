package com.aetherteam.aether.config;

import java.util.List;
import java.util.Objects;

public class AetherConfigEntry<T> {
    private final AetherConfigFile owner;
    private final List<String> path;
    private final T defaultValue;
    private final String comment;
    private final RestartRequirement restartRequirement;
    private final ValueCodec<T> codec;
    private volatile T value;
    private T persistedValue;

    AetherConfigEntry(AetherConfigFile owner, List<String> path, T defaultValue, String comment,
                      RestartRequirement restartRequirement, ValueCodec<T> codec) {
        this.owner = owner;
        this.path = List.copyOf(path);
        this.codec = codec;
        this.defaultValue = codec.copy(defaultValue);
        this.comment = Objects.requireNonNull(comment, "comment");
        this.restartRequirement = Objects.requireNonNull(restartRequirement, "restartRequirement");
        this.value = codec.copy(defaultValue);
        this.persistedValue = codec.copy(defaultValue);
    }

    public List<String> path() {
        return this.path;
    }

    public T get() {
        return this.value;
    }

    public void set(T value) {
        this.value = this.codec.copy(Objects.requireNonNull(value, "value"));
    }

    public void save() {
        this.owner.save();
    }

    public T defaultValue() {
        return this.codec.copy(this.defaultValue);
    }

    public String comment() {
        return this.comment;
    }

    public RestartRequirement restartRequirement() {
        return this.restartRequirement;
    }

    String serializedValue() {
        return this.codec.format(this.value);
    }

    boolean hasUnsavedValue() {
        return !Objects.equals(this.value, this.persistedValue);
    }

    void load(String serializedValue) {
        T loadedValue = this.codec.parse(serializedValue);
        this.value = this.codec.copy(loadedValue);
        this.persistedValue = this.codec.copy(loadedValue);
    }

    void markPersisted() {
        this.persistedValue = this.codec.copy(this.value);
    }

    void reset() {
        this.value = this.codec.copy(this.defaultValue);
        this.persistedValue = this.codec.copy(this.defaultValue);
    }

    interface ValueCodec<T> {
        T parse(String value);

        String format(T value);

        default T copy(T value) {
            return value;
        }
    }

    public enum RestartRequirement {
        NONE,
        WORLD
    }
}
