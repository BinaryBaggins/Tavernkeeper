package io.github.binarybaggins.tavernkeeper.config;

import java.util.function.Function;

/**
 * Utility class for loading bot configuration from the environment.
 */
public final class ConfigLoader {

    private ConfigLoader() {
    }

    /**
     * Loads the bot configuration from the environment.
     *
     * @return the loaded bot configuration
     */
    public static BotConfig load() {
        return new BotConfig(
                System.getenv("DISCORD_TOKEN"));
    }

    /**
     * Loads the bot configuration from the specified environment function.
     *
     * @param environment a function that provides environment variables
     * @return the loaded bot configuration
     */
    static BotConfig load(Function<String, String> environment) {
        return new BotConfig(
                environment.apply("DISCORD_TOKEN"));
    }
}
