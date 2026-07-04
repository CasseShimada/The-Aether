package com.aetherteam.aether.config;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Lightweight config spec used on Fabric to preserve existing config call sites.
 */
public final class ModConfigSpec {
    private final Map<List<String>, ConfigValue<?>> values;
    private final boolean loaded;

    private ModConfigSpec(Map<List<String>, ConfigValue<?>> values) {
        this.values = values;
        this.loaded = true;
    }

    public Map<List<String>, ConfigValue<?>> getValues() {
        return this.values;
    }

    public boolean isLoaded() {
        return this.loaded;
    }

    public static class Builder {
        private final Deque<String> sections = new ArrayDeque<>();
        private final Map<List<String>, ConfigValue<?>> values = new LinkedHashMap<>();

        public Builder push(String section) {
            this.sections.addLast(section);
            return this;
        }

        public Builder pop() {
            if (!this.sections.isEmpty()) {
                this.sections.removeLast();
            }
            return this;
        }

        public Builder comment(String ignored) {
            return this;
        }

        public Builder translation(String ignored) {
            return this;
        }

        public Builder gameRestart() {
            return this;
        }

        public Builder worldRestart() {
            return this;
        }

        public <T> ConfigValue<T> define(String name, T defaultValue) {
            List<String> path = this.currentPath(name);
            ConfigValue<T> value = defaultValue instanceof Boolean
                    ? (ConfigValue<T>) new BooleanValue(path, (Boolean) defaultValue)
                    : new ConfigValue<>(path, defaultValue);
            this.values.put(path, value);
            return value;
        }

        public <T> ConfigValue<List<? extends T>> defineList(String name, List<? extends T> defaultValue, Supplier<T> ignoredFactory, Predicate<Object> validator) {
            List<T> sanitized = new ArrayList<>();
            for (T entry : defaultValue) {
                if (validator.test(entry)) {
                    sanitized.add(entry);
                }
            }
            return this.define(name, List.copyOf(sanitized));
        }

        public <O> Configured<O> configure(Function<Builder, O> factory) {
            O instance = factory.apply(this);
            return new Configured<>(instance, new ModConfigSpec(Collections.unmodifiableMap(this.values)));
        }

        private List<String> currentPath(String leaf) {
            List<String> path = new ArrayList<>(this.sections);
            path.add(leaf);
            return List.copyOf(path);
        }
    }

    public record Configured<T>(T instance, ModConfigSpec spec) {
    }

    public static class ConfigValue<T> {
        private final List<String> path;
        private T value;

        public ConfigValue(List<String> path, T defaultValue) {
            this.path = List.copyOf(path);
            this.value = defaultValue;
        }

        public List<String> getPath() {
            return this.path;
        }

        public T get() {
            return this.value;
        }

        public void set(T value) {
            this.value = Objects.requireNonNull(value, "value");
        }

        public void save() {
            // Persisted config IO is handled separately in Fabric runtime.
        }
    }

    public static final class BooleanValue extends ConfigValue<Boolean> {
        public BooleanValue(List<String> path, Boolean defaultValue) {
            super(path, defaultValue);
        }
    }
}
