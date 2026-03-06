package com.aetherteam.nitrogen.api.users;

import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;
import java.time.format.DateTimeFormatter;

/**
 * Minimal local replacement of Nitrogen's user model used by Aether perk logic.
 */
public final class User {
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    @Nullable
    private Tier highestPastTier;
    @Nullable
    private Tier currentTier;
    @Nullable
    private String renewalDate;
    @Nullable
    private Group highestGroup;

    public User(@Nullable Tier highestPastTier, @Nullable Tier currentTier, @Nullable String renewalDate, @Nullable Group highestGroup) {
        this.highestPastTier = highestPastTier;
        this.currentTier = currentTier;
        this.renewalDate = renewalDate;
        this.highestGroup = highestGroup;
    }

    @Nullable
    public Tier getHighestPastTier() {
        return this.highestPastTier;
    }

    public int getHighestPastTierLevel() {
        Tier tier = this.getHighestPastTier();
        return tier != null ? tier.getLevel() : 0;
    }

    @Nullable
    public Tier getCurrentTier() {
        return this.currentTier;
    }

    public int getCurrentTierLevel() {
        Tier tier = this.getCurrentTier();
        return tier != null ? tier.getLevel() : 0;
    }

    @Nullable
    public String getRenewalDate() {
        return this.renewalDate;
    }

    @Nullable
    public Group getHighestGroup() {
        return this.highestGroup;
    }

    public void setHighestPastTier(@Nullable Tier highestPastTier) {
        this.highestPastTier = highestPastTier;
    }

    public void setCurrentTier(@Nullable Tier currentTier) {
        this.currentTier = currentTier;
    }

    public void setRenewalDate(@Nullable String renewalDate) {
        this.renewalDate = renewalDate;
    }

    public void setHighestGroup(@Nullable Group highestGroup) {
        this.highestGroup = highestGroup;
    }

    public enum Tier {
        HUMAN(0, 2429462, Component.translatable("nitrogen_internals.patreon.tier.human")),
        ASCENTAN(1, 616325, Component.translatable("nitrogen_internals.patreon.tier.ascentan")),
        VALKYRIE(2, 616326, Component.translatable("nitrogen_internals.patreon.tier.valkyrie")),
        ARKENZUS(3, 616327, Component.translatable("nitrogen_internals.patreon.tier.arkenzus"));

        private final int level;
        private final int id;
        private final Component displayName;

        Tier(int level, int id, Component displayName) {
            this.level = level;
            this.id = id;
            this.displayName = displayName;
        }

        public int getLevel() {
            return this.level;
        }

        public int getId() {
            return this.id;
        }

        public Component getDisplayName() {
            return this.displayName;
        }

        @Nullable
        public static Tier byId(int id) {
            for (Tier tier : values()) {
                if (tier.id == id) {
                    return tier;
                }
            }
            return null;
        }
    }

    public enum Group {
        AETHER_TEAM(7),
        MODDING_LEGACY(6),
        CONTRIBUTOR(5),
        LEGACY_CONTRIBUTOR(4),
        STAFF(3),
        CELEBRITY(2),
        TRANSLATOR(1);

        private final int level;

        Group(int level) {
            this.level = level;
        }

        public int getLevel() {
            return this.level;
        }
    }
}
