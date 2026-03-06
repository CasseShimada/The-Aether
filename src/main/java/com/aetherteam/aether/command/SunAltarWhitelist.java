package com.aetherteam.aether.command;

import com.aetherteam.aether.Aether;
import net.minecraft.server.notifications.NotificationService;
import net.minecraft.server.players.NameAndId;
import net.minecraft.server.players.UserWhiteList;
import net.minecraft.server.players.UserWhiteListEntry;

import java.io.File;

/**
 * A whitelist handling who can use the Sun Altar on a server.
 */
public class SunAltarWhitelist {
    public static final File SUN_ALTAR_WHITELIST_FILE = new File(Aether.DIRECTORY.toString(), "sun_altar_whitelist.json");
    private static final NotificationService NO_OP_NOTIFICATION_SERVICE = new NotificationService() {
        @Override
        public void ipBanned(net.minecraft.server.players.IpBanListEntry entry) {
        }

        @Override
        public void ipUnbanned(String address) {
        }

        @Override
        public <T> void onGameRuleChanged(net.minecraft.world.level.gamerules.GameRule<T> gameRule, T value) {
        }

        @Override
        public void playerAddedToAllowlist(NameAndId profile) {
        }

        @Override
        public void playerBanned(net.minecraft.server.players.UserBanListEntry entry) {
        }

        @Override
        public void playerDeoped(net.minecraft.server.players.ServerOpListEntry entry) {
        }

        @Override
        public void playerJoined(net.minecraft.server.level.ServerPlayer player) {
        }

        @Override
        public void playerLeft(net.minecraft.server.level.ServerPlayer player) {
        }

        @Override
        public void playerOped(net.minecraft.server.players.ServerOpListEntry entry) {
        }

        @Override
        public void playerRemovedFromAllowlist(NameAndId profile) {
        }

        @Override
        public void playerUnbanned(NameAndId profile) {
        }

        @Override
        public void serverActivityOccured() {
        }

        @Override
        public void serverSaveCompleted() {
        }

        @Override
        public void serverSaveStarted() {
        }

        @Override
        public void serverShuttingDown() {
        }

        @Override
        public void serverStarted() {
        }

        @Override
        public void statusHeartbeat() {
        }
    };
    private final UserWhiteList sunAltarWhitelist = new UserWhiteList(SUN_ALTAR_WHITELIST_FILE, NO_OP_NOTIFICATION_SERVICE);

    public static final SunAltarWhitelist INSTANCE = new SunAltarWhitelist();

    public SunAltarWhitelist() {
        this.load();
        this.save();
    }

    /**
     * @return The {@link UserWhiteList} for Sun Altar usage.
     */
    public UserWhiteList getSunAltarWhiteList() {
        return this.sunAltarWhitelist;
    }

    /**
     * @return A {@link String String[]} listing the player names in the whitelist.
     */
    public String[] getSunAltarWhiteListNames() {
        return this.sunAltarWhitelist.getUserList();
    }

    /**
     * Checks if a player is whitelisted.
     *
     * @param profile The player's {@link NameAndId}.
     * @return Whether the player was found in the whitelist data, as a {@link Boolean}.
     */
    public boolean isWhiteListed(NameAndId profile) {
        return this.sunAltarWhitelist.isWhiteListed(profile);
    }

    /**
     * Adds a player to the whitelist.
     *
     * @param element The {@link UserWhiteListEntry} for the player.
     */
    public void add(UserWhiteListEntry element) {
        this.getSunAltarWhiteList().add(element);
        this.save();
    }

    /**
     * Removes a player from the whitelist.
     *
     * @param element The {@link UserWhiteListEntry} for the player.
     */
    public void remove(UserWhiteListEntry element) {
        this.getSunAltarWhiteList().remove(element);
        this.save();
    }

    /**
     * Reloads the whitelist file.
     *
     * @see SunAltarWhitelist#load()
     */
    public void reload() {
        this.load();
    }

    /**
     * Loads the whitelist data from {@link SunAltarWhitelist#SUN_ALTAR_WHITELIST_FILE}.
     */
    private void load() {
        try {
            this.getSunAltarWhiteList().load();
        } catch (Exception exception) {
            Aether.LOGGER.warn("Failed to load Sun Altar whitelist: ", exception);
        }
    }

    /**
     * Saves the whitelist data to {@link SunAltarWhitelist#SUN_ALTAR_WHITELIST_FILE}.
     */
    private void save() {
        try {
            this.getSunAltarWhiteList().save();
        } catch (Exception exception) {
            Aether.LOGGER.warn("Failed to save Sun Altar whitelist: ", exception);
        }
    }
}
