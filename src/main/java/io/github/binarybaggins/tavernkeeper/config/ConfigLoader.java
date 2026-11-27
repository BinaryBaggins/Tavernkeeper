package io.github.binarybaggins.tavernkeeper.config;

public final class ConfigLoader {

    private ConfigLoader() {
    }

    public static BotConfig load() {
        return new BotConfig(
                System.getenv("DISCORD_TOKEN"));
    }
}
