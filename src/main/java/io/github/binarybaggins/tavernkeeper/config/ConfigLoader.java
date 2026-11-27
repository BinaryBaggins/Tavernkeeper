package io.github.binarybaggins.tavernkeeper.config;

import java.util.function.Function;
import java.nio.file.Path;

/**
 * Utility class for loading bot configuration from the environment.
 */
public final class ConfigLoader {

    private static final String DEFAULT_DATABASE_PATH = "data/tavernkeeper.db";

    private ConfigLoader() {
    }

    /**
     * Loads the bot configuration from the environment.
     *
     * @return the loaded bot configuration
     */
    public static BotConfig load() {
        return load(System::getenv);
    }

    /**
     * Loads the bot configuration from the specified environment function.
     *
     * @param environment a function that provides environment variables
     * @return the loaded bot configuration
     */
    static BotConfig load(Function<String, String> environment) {
        String token = environment.apply("DISCORD_TOKEN");
        String databasePath = environment.apply("DATABASE_PATH");

        if (databasePath == null || databasePath.isBlank()) {
            databasePath = DEFAULT_DATABASE_PATH;
        }

        return new BotConfig(token, Path.of(databasePath));
    }
}
