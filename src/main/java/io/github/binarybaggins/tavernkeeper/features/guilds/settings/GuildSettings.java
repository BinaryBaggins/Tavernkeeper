package io.github.binarybaggins.tavernkeeper.features.guilds.settings;

import java.util.Locale;
import java.util.Objects;

/**
 * Represents the settings for a guild.
 */
public record GuildSettings(
        String guildId,
        Locale locale) {

    /**
     * Constructs a new GuildSettings instance, validating the inputs.
     *
     * @param guildId the ID of the guild, must not be null or blank
     * @param locale  the locale for the guild, must not be null
     */
    public GuildSettings {
        Objects.requireNonNull(guildId);
        Objects.requireNonNull(locale);

        if (guildId.isBlank()) {
            throw new IllegalArgumentException(
                    "Guild ID must not be blank.");
        }
    }

    /**
     * Returns the default GuildSettings for the given guild ID.
     *
     * @param guildId the ID of the guild, must not be null or blank
     * @return a GuildSettings instance with default values
     */
    public static GuildSettings defaults(String guildId) {
        return new GuildSettings(guildId, Locale.ENGLISH);
    }
}