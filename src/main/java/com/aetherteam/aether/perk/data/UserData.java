package com.aetherteam.aether.perk.data;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Local user data storage used by Aether perk checks.
 */
public final class UserData {
    private UserData() {
    }

    public static final class Client {
        @Nullable
        private static volatile User clientUser;

        private Client() {
        }

        @Nullable
        public static User getClientUser() {
            return clientUser;
        }

        public static void setClientUser(@Nullable User user) {
            clientUser = user;
        }

        public static void clearClientUser() {
            clientUser = null;
        }
    }

    public static final class Server {
        private static final Map<UUID, User> STORED_USERS = new ConcurrentHashMap<>();
        private static final Map<UUID, User> STORED_USERS_VIEW = Collections.unmodifiableMap(STORED_USERS);

        private Server() {
        }

        public static Map<UUID, User> getStoredUsers() {
            return STORED_USERS_VIEW;
        }

        public static void setStoredUser(UUID uuid, User user) {
            STORED_USERS.put(uuid, user);
        }

        public static void removeStoredUser(UUID uuid) {
            STORED_USERS.remove(uuid);
        }

        public static void clearStoredUsers() {
            STORED_USERS.clear();
        }
    }
}
