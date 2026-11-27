
package io.github.binarybaggins.tavernkeeper.config;

public record BotConfig(String token) {

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
