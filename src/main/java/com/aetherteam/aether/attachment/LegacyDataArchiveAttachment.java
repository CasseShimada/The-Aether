package com.aetherteam.aether.attachment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Fabric-persistent archive of legacy loader data that Minecraft would otherwise discard on save.
 */
public final class LegacyDataArchiveAttachment {
    public static final Codec<LegacyDataArchiveAttachment> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.optionalFieldOf("migration_version", 0).forGetter(LegacyDataArchiveAttachment::migrationVersion),
            Codec.unboundedMap(Codec.STRING, CompoundTag.CODEC).optionalFieldOf("roots", Map.of()).forGetter(LegacyDataArchiveAttachment::roots)
    ).apply(instance, LegacyDataArchiveAttachment::new));

    private int migrationVersion;
    private final Map<String, CompoundTag> roots;

    public LegacyDataArchiveAttachment() {
        this(0, Map.of());
    }

    private LegacyDataArchiveAttachment(int migrationVersion, Map<String, CompoundTag> roots) {
        this.migrationVersion = Math.max(0, migrationVersion);
        this.roots = new LinkedHashMap<>();
        roots.forEach((key, value) -> this.roots.put(key, value.copy()));
    }

    public synchronized int migrationVersion() {
        return this.migrationVersion;
    }

    public synchronized void setMigrationVersion(int migrationVersion) {
        this.migrationVersion = Math.max(this.migrationVersion, migrationVersion);
    }

    public synchronized void archive(String key, CompoundTag value) {
        this.roots.putIfAbsent(key, value.copy());
    }

    public synchronized Map<String, CompoundTag> roots() {
        Map<String, CompoundTag> copy = new LinkedHashMap<>();
        this.roots.forEach((key, value) -> copy.put(key, value.copy()));
        return copy;
    }
}
