package com.aetherteam.aether.perk.data;

import com.aetherteam.aether.perk.types.DeveloperGlow;
import com.aetherteam.aether.perk.types.Halo;
import com.aetherteam.aether.perk.types.MoaData;
import com.aetherteam.aether.perk.types.MoaSkins;
import com.google.common.collect.ImmutableMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.level.storage.DimensionDataStorage;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PerkSavedData extends SavedData {
    public static final String FILE_NAME = "perks";
    private static final SavedDataType<PerkSavedData> TYPE = new SavedDataType<>(
            FILE_NAME,
            PerkSavedData::new,
            CompoundTag.CODEC.xmap(PerkSavedData::load, PerkSavedData::saveTag),
            null
    );

    private final Map<UUID, MoaData> storedSkinData = new HashMap<>();
    private final Map<UUID, Halo> storedHaloData = new HashMap<>();
    private final Map<UUID, DeveloperGlow> storedDeveloperGlowData = new HashMap<>();

    /**
     * Saves perk data to the world in a file named "perks.dat".
     *
     * @return A {@link CompoundTag} with the data.
     */
    private CompoundTag saveTag() {
        CompoundTag tag = new CompoundTag();
        CompoundTag storedSkinDataTag = new CompoundTag();
        for (Map.Entry<UUID, MoaData> moaDataEntry : this.storedSkinData.entrySet()) {
            CompoundTag moaDataEntryTag = new CompoundTag();
            if (moaDataEntry.getValue().moaUUID() != null) {
                moaDataEntryTag.putString("MoaUUID", moaDataEntry.getValue().moaUUID().toString());
            }
            if (moaDataEntry.getValue().moaSkin() != null) {
                moaDataEntryTag.putString("MoaSkin", moaDataEntry.getValue().moaSkin().getId());
            }
            storedSkinDataTag.put(moaDataEntry.getKey().toString(), moaDataEntryTag);
        }
        tag.put("StoredSkinData", storedSkinDataTag);

        CompoundTag storedHaloDataTag = new CompoundTag();
        for (Map.Entry<UUID, Halo> haloEntry : this.storedHaloData.entrySet()) {
            CompoundTag haloEntryTag = new CompoundTag();
            if (haloEntry.getValue().hexColor() != null) {
                haloEntryTag.putString("HexColor", haloEntry.getValue().hexColor());
            }
            storedHaloDataTag.put(haloEntry.getKey().toString(), haloEntryTag);
        }
        tag.put("StoredHaloData", storedHaloDataTag);

        CompoundTag storedDeveloperGlowDataTag = new CompoundTag();
        for (Map.Entry<UUID, DeveloperGlow> developerGlowEntry : this.storedDeveloperGlowData.entrySet()) {
            CompoundTag developerGlowTag = new CompoundTag();
            if (developerGlowEntry.getValue().hexColor() != null) {
                developerGlowTag.putString("HexColor", developerGlowEntry.getValue().hexColor());
            }
            storedDeveloperGlowDataTag.put(developerGlowEntry.getKey().toString(), developerGlowTag);
        }
        tag.put("StoredDeveloperGlowData", storedDeveloperGlowDataTag);

        return tag;
    }

    /**
     * Loads perk data from the world from a {@link CompoundTag} representing the data in "perks.dat".
     *
     * @param tag The {@link CompoundTag}.
     * @return The {@link PerkSavedData} created from the world data.
     */
    public static PerkSavedData load(CompoundTag tag) {
        PerkSavedData data = PerkSavedData.create();
        CompoundTag storedSkinDataTag = tag.getCompoundOrEmpty("StoredSkinData");
        for (String storedSkinDataKey : storedSkinDataTag.keySet()) {
            CompoundTag moaDataEntryTag = storedSkinDataTag.getCompoundOrEmpty(storedSkinDataKey);
            UUID playerUUID = parseUuid(storedSkinDataKey);
            if (playerUUID == null) {
                continue;
            }
            UUID moaUUID = parseUuid(moaDataEntryTag.getStringOr("MoaUUID", ""));
            String moaSkinId = moaDataEntryTag.getStringOr("MoaSkin", "");
            MoaSkins.MoaSkin moaSkin = MoaSkins.getMoaSkins().get(moaSkinId);
            data.storedSkinData.put(playerUUID, new MoaData(moaUUID, moaSkin));
        }

        CompoundTag storedHaloDataTag = tag.getCompoundOrEmpty("StoredHaloData");
        for (String storedHaloDataKey : storedHaloDataTag.keySet()) {
            CompoundTag haloEntryTag = storedHaloDataTag.getCompoundOrEmpty(storedHaloDataKey);
            UUID playerUUID = parseUuid(storedHaloDataKey);
            if (playerUUID != null) {
                data.storedHaloData.put(playerUUID, new Halo(haloEntryTag.getStringOr("HexColor", "")));
            }
        }

        CompoundTag storedDeveloperGlowDataTag = tag.getCompoundOrEmpty("StoredDeveloperGlowData");
        for (String storedDeveloperGlowDataKey : storedDeveloperGlowDataTag.keySet()) {
            CompoundTag developerGlowEntryTag = storedDeveloperGlowDataTag.getCompoundOrEmpty(storedDeveloperGlowDataKey);
            UUID playerUUID = parseUuid(storedDeveloperGlowDataKey);
            if (playerUUID != null) {
                data.storedDeveloperGlowData.put(playerUUID, new DeveloperGlow(developerGlowEntryTag.getStringOr("HexColor", "")));
            }
        }
        return data;
    }

    public static PerkSavedData create() {
        return new PerkSavedData();
    }

    /**
     * Loads or creates the "perks.dat" file.
     *
     * @param dataStorage The {@link DimensionDataStorage} of the world.
     * @return The {@link PerkSavedData} corresponding to the data file.
     */
    public static PerkSavedData compute(DimensionDataStorage dataStorage) {
        return dataStorage.computeIfAbsent(TYPE);
    }

    private static UUID parseUuid(String value) {
        try {
            return value.isEmpty() ? null : UUID.fromString(value);
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    /**
     * @return A {@link Map} of player {@link UUID}s and {@link MoaData} retrieved from "perks.dat".
     */
    Map<UUID, MoaData> getStoredSkinData() {
        return ImmutableMap.copyOf(this.storedSkinData);
    }

    /**
     * Modifies the {@link MoaData} stored in the world data.
     *
     * @param uuid    The {@link UUID} of the player that the {@link MoaData} belongs to.
     * @param moaData The {@link MoaData}.
     */
    void modifyStoredSkinData(UUID uuid, MoaData moaData) {
        this.storedSkinData.put(uuid, moaData);
        this.setDirty();
    }

    /**
     * Removes an entry for a player's {@link MoaData} from the world data.
     *
     * @param uuid The player's {@link UUID}.
     */
    void removeStoredSkinData(UUID uuid) {
        this.storedSkinData.remove(uuid);
        this.setDirty();
    }

    /**
     * @return A {@link Map} of player {@link UUID}s and {@link Halo} info retrieved from "perks.dat".
     */
    Map<UUID, Halo> getStoredHaloData() {
        return ImmutableMap.copyOf(this.storedHaloData);
    }

    /**
     * Modifies the {@link Halo} info stored in the world data.
     *
     * @param uuid The {@link UUID} of the player that the {@link Halo} belongs to.
     * @param halo The {@link Halo}.
     */
    void modifyStoredHaloData(UUID uuid, Halo halo) {
        this.storedHaloData.put(uuid, halo);
        this.setDirty();
    }

    /**
     * Removes an entry for a player's {@link Halo} from the world data.
     *
     * @param uuid The player's {@link UUID}.
     */
    void removeStoredHaloData(UUID uuid) {
        this.storedHaloData.remove(uuid);
        this.setDirty();
    }

    /**
     * @return A {@link Map} of player {@link UUID}s and {@link DeveloperGlow} info retrieved from "perks.dat".
     */
    Map<UUID, DeveloperGlow> getStoredDeveloperGlowData() {
        return ImmutableMap.copyOf(this.storedDeveloperGlowData);
    }

    /**
     * Modifies the {@link DeveloperGlow} info stored in the world data.
     *
     * @param uuid          The {@link UUID} of the player that the {@link Halo} belongs to.
     * @param developerGlow The {@link DeveloperGlow}.
     */
    void modifyStoredDeveloperGlowData(UUID uuid, DeveloperGlow developerGlow) {
        this.storedDeveloperGlowData.put(uuid, developerGlow);
        this.setDirty();
    }

    /**
     * Removes an entry for a player's {@link DeveloperGlow} from the world data.
     *
     * @param uuid The player's {@link UUID}.
     */
    void removeStoredDeveloperGlowData(UUID uuid) {
        this.storedDeveloperGlowData.remove(uuid);
        this.setDirty();
    }
}
