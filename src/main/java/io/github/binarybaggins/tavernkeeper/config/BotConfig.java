
package io.github.binarybaggins.tavernkeeper.config;

import java.nio.file.Path;
import java.util.Objects;

/**
 * Configuration for the Discord bot.
 */
public record BotConfig(String token, Path databasePath) {

    /**
     * Constructs a new BotConfig instance.
     *
     * @param token        the Discord bot token
     * @param databasePath the path to the database
     * @throws NullPointerException     if token or databasePath is null
     * @throws IllegalArgumentException if token is blank
     */
    public BotConfig {
        Objects.requireNonNull(token, "Discord token must not be null");
        Objects.requireNonNull(databasePath, "Database path must not be null");
        if (token.isBlank()) {
            throw new IllegalArgumentException(
                    "Discord token must not be empty.");
        }
    }

    @Override
    public String toString() {
        return "BotConfig[token=REDACTED, databasePath=" + databasePath + "]";
    }
}
