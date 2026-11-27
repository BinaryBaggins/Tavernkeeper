
package io.github.binarybaggins.tavernkeeper.config;

/**
 * Configuration for the Discord bot.
 */
public record BotConfig(String token) {

    /**
     * Constructs a new BotConfig instance.
     *
     * @param token the Discord bot token
     * @throws IllegalArgumentException if the token is null or blank
     */
    public BotConfig {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException(
                    "Discord token must not be empty.");
        }
    }

    @Override
    public String toString() {
        return "BotConfig[token=REDACTED]";
    }
}
