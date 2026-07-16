package com.aetherteam.aether;

import com.aetherteam.aether.config.AetherConfigEntry;
import com.aetherteam.aether.config.AetherConfigFile;
import com.aetherteam.aether.config.BooleanConfigEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public final class AetherConfig {
    public static final AetherConfigFile STARTUP_FILE = new AetherConfigFile("aether-startup.toml");
    public static final Startup STARTUP = new Startup(STARTUP_FILE);
    public static final AetherConfigFile SERVER_FILE = new AetherConfigFile("aether-server.toml");
    public static final Server SERVER = new Server(SERVER_FILE);
    public static final AetherConfigFile COMMON_FILE = new AetherConfigFile("aether-common.toml");
    public static final Common COMMON = new Common(COMMON_FILE);
    public static final AetherConfigFile CLIENT_FILE = new AetherConfigFile("aether-client.toml");
    public static final Client CLIENT = new Client(CLIENT_FILE);

    private AetherConfig() {
    }

    public static void loadGlobal(Path configDirectory, boolean includeClient) {
        STARTUP_FILE.load(configDirectory);
        COMMON_FILE.load(configDirectory);
        if (includeClient) {
            CLIENT_FILE.load(configDirectory);
        }
    }

    public static void loadServer(MinecraftServer server) {
        Path serverConfigDirectory = server.getWorldPath(LevelResource.ROOT).resolve("serverconfig");
        SERVER_FILE.load(serverConfigDirectory);
    }

    public static void unloadServer() {
        SERVER_FILE.unload();
    }

    public static Map<String, String> synchronizedServerValues() {
        return SERVER_FILE.synchronizedValues();
    }

    public static void applySynchronizedServerValues(Map<String, String> values) {
        SERVER_FILE.applySynchronizedValues(values);
    }

    public static void clearSynchronizedServerValues() {
        SERVER_FILE.clearSynchronizedValues();
    }

    public static final class Startup {
        public final BooleanConfigEntry enable_trivia;

        private Startup(AetherConfigFile file) {
            this.enable_trivia = file.booleanEntry("Gui", "Enables random trivia", true,
                    "Adds random trivia and tips to the bottom of loading screens");
        }
    }

    public static final class Server {
        public final BooleanConfigEntry enable_bed_explosions;
        public final BooleanConfigEntry tools_debuff;
        public final BooleanConfigEntry edible_ambrosium;
        public final BooleanConfigEntry berry_bush_consistency;
        public final BooleanConfigEntry crystal_leaves_consistency;
        public final BooleanConfigEntry healing_gummy_swets;
        public final AetherConfigEntry<Integer> hammer_of_kingbdogz_cooldown;
        public final AetherConfigEntry<Integer> cloud_staff_cooldown;
        public final AetherConfigEntry<Integer> maximum_life_shards;
        public final BooleanConfigEntry require_gloves;
        public final BooleanConfigEntry spawn_golden_feather;
        public final BooleanConfigEntry spawn_valkyrie_cape;
        public final BooleanConfigEntry generate_tall_grass;
        public final BooleanConfigEntry generate_holiday_tree_always;
        public final BooleanConfigEntry generate_holiday_tree_seasonally;
        public final BooleanConfigEntry balance_invisibility_cloak;
        public final AetherConfigEntry<Integer> invisibility_visibility_time;
        public final BooleanConfigEntry sun_altar_whitelist;
        public final AetherConfigEntry<List<String>> sun_altar_dimensions;
        public final BooleanConfigEntry spawn_in_aether;
        public final BooleanConfigEntry disable_aether_portal;
        public final BooleanConfigEntry disable_falling_to_overworld;
        public final BooleanConfigEntry disable_eternal_day;
        public final BooleanConfigEntry normal_length_aether_time;
        public final BooleanConfigEntry sync_aether_time;
        public final AetherConfigEntry<String> portal_destination_dimension_ID;
        public final AetherConfigEntry<String> portal_return_dimension_ID;

        private Server(AetherConfigFile file) {
            this.enable_bed_explosions = file.booleanEntry("Gameplay", "Beds explode", false,
                    "Vanilla's beds will explode in the Aether");
            this.tools_debuff = file.booleanEntry("Gameplay", "Debuff non-Aether tools", true,
                    "Tools that aren't from the Aether will mine Aether blocks slower than tools that are from the Aether");
            this.edible_ambrosium = file.booleanEntry("Gameplay", "Ambrosium Shards are edible", true,
                    "Ambrosium Shards can be eaten to restore a half heart of health");
            this.berry_bush_consistency = file.booleanEntry("Gameplay", "Berry Bush consistency", false,
                    "Makes Berry Bushes and Bush Stems behave consistently with Sweet Berry Bushes");
            this.crystal_leaves_consistency = file.booleanEntry("Gameplay", "Crystal Fruit Leaves consistency", false,
                    "Makes Crystal Fruit Leaves behave consistently with Sweet Berry Bushes");
            this.healing_gummy_swets = file.booleanEntry("Gameplay", "Gummy Swets restore health", false,
                    "Gummy Swets when eaten restore full health instead of full hunger");
            this.maximum_life_shards = file.integerEntry("Gameplay", "Maximum consumable Life Shards", 10,
                    "Determines the limit of the amount of Life Shards a player can consume to increase their health");
            this.hammer_of_kingbdogz_cooldown = file.integerEntry("Gameplay", "Cooldown for the Hammer of Kingbdogz projectile", 50,
                    "Determines the cooldown in ticks for the Hammer of Kingbdogz's ability");
            this.cloud_staff_cooldown = file.integerEntry("Gameplay", "Cooldown for the Cloud Staff", 40,
                    "Determines the cooldown in ticks for the Cloud Staff's ability");
            this.require_gloves = file.booleanEntry("Gameplay", "Require gloves for set abilities", true,
                    "Makes armor abilities depend on wearing the respective gloves belonging to an armor set");

            this.spawn_golden_feather = file.booleanEntry("Loot", "Golden Feather in loot", false,
                    "Allows the Golden Feather to spawn in the Silver Dungeon loot table");
            this.spawn_valkyrie_cape = file.booleanEntry("Loot", "Valkyrie Cape in loot", true,
                    "Allows the Valkyrie Cape to spawn in the Silver Dungeon loot table");

            this.generate_tall_grass = file.booleanEntry("World Generation", "Generate Tall Grass in the Aether", true,
                    "Determines whether the Aether should generate Tall Grass blocks on terrain or not");
            this.generate_holiday_tree_always = file.booleanEntry("World Generation", "Generate Holiday Trees always", false,
                    "Determines whether Holiday Trees should always be able to generate when exploring new chunks in the Aether, if true, this overrides 'Generate Holiday Trees seasonally'");
            this.generate_holiday_tree_seasonally = file.booleanEntry("World Generation", "Generate Holiday Trees seasonally", true,
                    "Determines whether Holiday Trees should be able to generate during the time frame of December and January when exploring new chunks in the Aether, only works if 'Generate Holiday Trees always' is set to false");

            this.balance_invisibility_cloak = file.booleanEntry("Multiplayer", "Balance Invisibility Cloak for PVP", false,
                    "Makes the Invisibility Cloak more balanced in PVP by disabling equipment invisibility temporarily after attacks");
            this.invisibility_visibility_time = file.integerEntry("Multiplayer", "Invisibility Cloak visibility timer", 50,
                    "Sets the time in ticks that it takes for the player to become fully invisible again after attacking when wearing an Invisibility Cloak; only works with 'Balance Invisibility Cloak for PVP'");
            this.sun_altar_whitelist = file.booleanEntry("Multiplayer", "Only whitelisted users access Sun Altars", false,
                    "Makes it so that only whitelisted users or anyone with permission level 4 can use the Sun Altar on a server");
            this.sun_altar_dimensions = file.stringListEntry("Multiplayer", "Configure Sun Altar dimensions", List.of("aether:the_aether"),
                    "Configures what dimensions are able to have their time changed by the Sun Altar");

            this.spawn_in_aether = file.booleanEntry("Modpack", "Spawns the player in the Aether", false,
                    "Spawns the player in the Aether dimension; this is best enabled alongside other modpack configuration to avoid issues");
            this.disable_aether_portal = file.booleanEntry("Modpack", "Disables Aether Portal creation", false,
                    "Prevents the Aether Portal from being created normally in the mod");
            this.disable_falling_to_overworld = file.booleanEntry("Modpack", "Disables falling into the Overworld", false,
                    "Prevents the player from falling back to the Overworld when they fall out of the Aether");
            this.disable_eternal_day = file.booleanEntry("Modpack", "Disables eternal day", false,
                    "Removes eternal day so that the Aether has a normal daylight cycle even before defeating the Sun Spirit");
            this.normal_length_aether_time = file.booleanEntry("Modpack", "Overworld-length Aether time cycle", false,
                    "Sets the Aether's time cycle to be the same length as the Overworld's");
            this.sync_aether_time = file.booleanEntry("Modpack", "Syncs time cycles", false,
                    "Syncs the Aether's time cycle to the Overworld's");
            this.portal_destination_dimension_ID = file.stringEntry("Modpack", "Sets portal destination dimension",
                    "aether:the_aether",
                    "Sets the ID of the dimension that the Aether Portal will send the player to");
            this.portal_return_dimension_ID = file.stringEntry("Modpack", "Sets portal return dimension",
                    "minecraft:overworld",
                    "Sets the ID of the dimension that the Aether Portal will return the player to");
        }
    }

    public static final class Common {
        public final BooleanConfigEntry start_with_portal;
        public final BooleanConfigEntry enable_startup_loot;
        public final BooleanConfigEntry reposition_slider_message;
        public final BooleanConfigEntry repeat_sun_spirit_dialogue;
        public final BooleanConfigEntry show_patreon_message;
        public final BooleanConfigEntry add_temporary_freezing_automatically;
        public final BooleanConfigEntry add_ruined_portal_automatically;
        public final BooleanConfigEntry randomize_boss_names;
        public final BooleanConfigEntry enable_immersive_portals_compatibility;

        private Common(AetherConfigFile file) {
            this.start_with_portal = file.booleanEntry("Gameplay", "Gives player Aether Portal Frame item", false,
                    "On world creation, the player is given an Aether Portal Frame item to automatically go to the Aether with");
            this.enable_startup_loot = file.booleanEntry("Gameplay", "Gives starting loot on entry", true,
                    "When the player enters the Aether, they are given a Book of Lore and Golden Parachutes as starting loot");
            this.reposition_slider_message = file.booleanEntry("Gameplay", "Reposition attack message above hotbar", false,
                    "Moves the message for when a player attacks the Slider with an incorrect item to be above the hotbar instead of in chat");
            this.repeat_sun_spirit_dialogue = file.booleanEntry("Gameplay", "Repeat Sun Spirit's battle dialogue", true,
                    "Determines whether the Sun Spirit's dialogue when meeting him should play through every time you meet him");
            this.show_patreon_message = file.booleanEntry("Gameplay", "Show Patreon message", true,
                    "Determines if a message that links The Aether mod's Patreon should show");
            this.add_temporary_freezing_automatically = file.worldRestartBooleanEntry("Data Pack", "Add Temporary Freezing automatically", false,
                    "Sets the Aether Temporary Freezing data pack to be added to new worlds automatically");
            this.add_ruined_portal_automatically = file.worldRestartBooleanEntry("Data Pack", "Add Ruined Portals automatically", false,
                    "Sets the Aether Ruined Portals data pack to be added to new worlds automatically");
            this.randomize_boss_names = file.booleanEntry("Modpack", "Randomize boss names", true,
                    "Determines whether bosses should display a randomized name above their boss bar");
            this.enable_immersive_portals_compatibility = file.booleanEntry("Modpack", "Enables Immersive Portals compatibility", true,
                    "Enables code and data pack features used for modifying Aether Portals when Immersive Portals is installed");
        }
    }

    public static final class Client {
        public final BooleanConfigEntry legacy_models;
        public final BooleanConfigEntry disable_aether_skybox;
        public final BooleanConfigEntry disable_clouds;
        public final BooleanConfigEntry colder_lightmap;
        public final BooleanConfigEntry green_sunset;
        public final BooleanConfigEntry enable_silver_hearts;
        public final BooleanConfigEntry disable_accessory_button;
        public final BooleanConfigEntry disable_skins_button;
        public final AetherConfigEntry<Integer> portal_text_y;
        public final AetherConfigEntry<Integer> button_inventory_x;
        public final AetherConfigEntry<Integer> button_inventory_y;
        public final AetherConfigEntry<Integer> button_creative_x;
        public final AetherConfigEntry<Integer> button_creative_y;
        public final AetherConfigEntry<Integer> button_accessories_x;
        public final AetherConfigEntry<Integer> button_accessories_y;
        public final AetherConfigEntry<Integer> layout_perks_x;
        public final AetherConfigEntry<Integer> layout_perks_y;
        public final BooleanConfigEntry enable_hammer_cooldown_overlay;
        public final BooleanConfigEntry blue_aercloud_bounce_sfx;
        public final AetherConfigEntry<Integer> music_backup_min_delay;
        public final AetherConfigEntry<Integer> music_backup_max_delay;
        public final BooleanConfigEntry disable_music_manager;
        public final BooleanConfigEntry disable_aether_boss_music;
        public final BooleanConfigEntry disable_aether_menu_music;
        public final BooleanConfigEntry disable_vanilla_world_preview_menu_music;
        public final BooleanConfigEntry disable_aether_world_preview_menu_music;
        public final BooleanConfigEntry enable_server_button;

        private Client(AetherConfigFile file) {
            this.legacy_models = file.booleanEntry("Rendering", "Switches to legacy mob models", false,
                    "Changes Zephyr and Aerwhale rendering to use their old models from the b1.7.3 version of the mod");
            this.disable_aether_skybox = file.booleanEntry("Rendering", "Disables Aether custom skybox", false,
                    "Disables the Aether's custom skybox in case you have a shader that is incompatible with custom skyboxes");
            this.disable_clouds = file.booleanEntry("Rendering", "Disables Aether's clouds", false,
                    "Disables the cloud rendering in the Aether");
            this.colder_lightmap = file.booleanEntry("Rendering", "Makes lightmap colder", false,
                    "Removes warm-tinting of the lightmap in the Aether, giving the lighting a colder feel");
            this.green_sunset = file.booleanEntry("Rendering", "Enables green sunrise/sunset", false,
                    "Enables a green-tinted sunrise and sunset in the Aether, similar to the original mod");

            this.enable_silver_hearts = file.booleanEntry("Gui", "Enables silver life shard hearts", true,
                    "Makes the extra hearts given by life shards display as silver colored");
            this.disable_accessory_button = file.booleanEntry("Gui", "Disables the accessories button", false,
                    "Disables the Aether's accessories button from appearing in GUIs");
            this.disable_skins_button = file.booleanEntry("Gui", "Disables the Moa Skins button", false,
                    "Disables the Aether's Moa Skins button from appearing in GUIs");
            this.portal_text_y = file.integerEntry("Gui", "Portal text y-coordinate in loading screens", 50,
                    "The y-coordinate of the Ascending to the Aether and Descending from the Aether text in loading screens");
            this.button_inventory_x = file.integerEntry("Gui", "Button x-coordinate in inventory menus", 27,
                    "The x-coordinate of the accessories button in the inventory and accessories menus");
            this.button_inventory_y = file.integerEntry("Gui", "Button y-coordinate in inventory menus", 68,
                    "The y-coordinate of the accessories button in the inventory and accessories menus");
            this.button_creative_x = file.integerEntry("Gui", "Button x-coordinate in creative menu", 74,
                    "The x-coordinate of the accessories button in the creative menu");
            this.button_creative_y = file.integerEntry("Gui", "Button y-coordinate in creative menu", 40,
                    "The y-coordinate of the accessories button in the creative menu");
            this.button_accessories_x = file.integerEntry("Gui", "Button x-coordinate in accessories menu", 9,
                    "The x-coordinate of the accessories button in the accessories menu");
            this.button_accessories_y = file.integerEntry("Gui", "Button y-coordinate in accessories menu", 68,
                    "The y-coordinate of the accessories button in the accessories menu");
            this.layout_perks_x = file.integerEntry("Gui", "Perks layout x-coordinate in pause menu", -116,
                    "The x-coordinate of the layout of perks buttons when in the pause menu");
            this.layout_perks_y = file.integerEntry("Gui", "Perks layout y-coordinate in pause menu", 0,
                    "The y-coordinate of the layout of perks buttons when in the pause menu");
            this.enable_hammer_cooldown_overlay = file.booleanEntry("Gui", "Enables Hammer of Kingbdogz' cooldown overlay", true,
                    "Enables the overlay at the top of the screen for the Hammer of Kingbdogz' cooldown");

            this.blue_aercloud_bounce_sfx = file.booleanEntry("Audio", "Blue Aercloud bouncing sounds", true,
                    "Makes Blue Aerclouds have their wobbly sounds that play when bouncing on them");
            this.music_backup_min_delay = file.integerEntry("Audio", "Set backup minimum music delay", 12000,
                    "Sets the minimum delay for the Aether's music manager to use if needing to reset the song delay outside the Aether");
            this.music_backup_max_delay = file.integerEntry("Audio", "Set backup maximum music delay", 24000,
                    "Sets the maximum delay for the Aether's music manager to use if needing to reset the song delay outside the Aether");
            this.disable_music_manager = file.booleanEntry("Audio", "Disables Aether music manager", false,
                    "Disables the Aether's internal music manager, if true, this overrides all other audio configs");
            this.disable_aether_boss_music = file.booleanEntry("Audio", "Disables Aether boss music", false,
                    "Disables the Aether's boss fight music, only works if 'Disables Aether music manager' is false");
            this.disable_aether_menu_music = file.booleanEntry("Audio", "Disables Aether menu music", false,
                    "Disables the Aether's menu music in case another mod implements its own, only works if 'Disables Aether music manager' is false");
            this.disable_vanilla_world_preview_menu_music = file.booleanEntry("Audio", "Disables vanilla world preview menu music", false,
                    "Disables the menu music on the vanilla world preview menu, only works if 'Disables Aether music manager' is false");
            this.disable_aether_world_preview_menu_music = file.booleanEntry("Audio", "Disables Aether world preview menu music", false,
                    "Disables the menu music on the Aether world preview menu, only works if 'Disables Aether music manager' is false");

            this.enable_server_button = file.booleanEntry("Miscellaneous", "Enables server button", false,
                    "Enables a direct join button for the official server");
        }
    }
}
