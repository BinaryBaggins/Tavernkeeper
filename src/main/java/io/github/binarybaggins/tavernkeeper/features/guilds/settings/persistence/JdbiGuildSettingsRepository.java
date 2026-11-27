package io.github.binarybaggins.tavernkeeper.features.guilds.settings.persistence;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import org.jdbi.v3.core.Jdbi;

import io.github.binarybaggins.tavernkeeper.features.guilds.settings.GuildSettings;
import io.github.binarybaggins.tavernkeeper.features.guilds.settings.GuildSettingsRepository;

/**
 * Jdbi-based implementation of the GuildSettingsRepository.
 */
public final class JdbiGuildSettingsRepository
        implements GuildSettingsRepository {

    private final Jdbi jdbi;

    /**
     * Constructs a new JdbiGuildSettingsRepository with the given Jdbi instance.
     *
     * @param jdbi the Jdbi instance, must not be null
     */
    public JdbiGuildSettingsRepository(Jdbi jdbi) {
        this.jdbi = Objects.requireNonNull(jdbi);
    }

    /**
     * Finds the settings for a guild by its ID.
     *
     * @param guildId the ID of the guild, must not be null or blank
     * @return an Optional containing the GuildSettings if found, or empty if not
     *         found
     */
    @Override
    public Optional<GuildSettings> findByGuildId(String guildId) {
        return jdbi.withHandle(handle -> handle.createQuery("""
                SELECT guild_id, locale
                FROM guild_settings
                WHERE guild_id = :guildId
                """)
                .bind("guildId", guildId)
                .map((rs, ctx) -> new GuildSettings(
                        rs.getString("guild_id"),
                        Locale.forLanguageTag(rs.getString("locale"))))
                .findOne());
    }

    /**
     * Saves the given GuildSettings.
     *
     * @param settings the GuildSettings to save, must not be null
     */
    @Override
    public void save(GuildSettings settings) {
        jdbi.useHandle(handle -> handle.createUpdate("""
                INSERT INTO guild_settings (guild_id, locale)
                VALUES (:guildId, :locale)
                ON CONFLICT(guild_id) DO UPDATE SET
                    locale = excluded.locale
                """)
                .bind("guildId", settings.guildId())
                .bind("locale", settings.locale().toLanguageTag())
                .execute());
    }
}
