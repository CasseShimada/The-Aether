package com.aetherteam.aether.accessories;

public final class Accessories {
    private static final Config CONFIG = new Config();

    private Accessories() {
    }

    public static Config config() {
        return CONFIG;
    }

    public static final class Config {
        public final ClientOptions clientOptions = new ClientOptions();
    }

    public static final class ClientOptions {
        public boolean showCosmeticAccessories() {
            return true;
        }
    }
}
