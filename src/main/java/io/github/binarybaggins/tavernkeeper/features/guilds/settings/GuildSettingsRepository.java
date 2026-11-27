package io.github.binarybaggins.tavernkeeper.features.guilds.settings;

import java.util.Optional;

/**
 * Repository interface for managing GuildSettings.
 */
public interface GuildSettingsRepository {

    /**
     * Finds the settings for a guild by its ID.
     *
     * @param guildId the ID of the guild, must not be null or blank
     * @return an Optional containing the GuildSettings if found, or empty if not
     *         found
     */
    Optional<GuildSettings> findByGuildId(String guildId);

    /**
     * Saves the given GuildSettings.
     *
     * @param settings the GuildSettings to save, must not be null
     */
    void save(GuildSettings settings);
}