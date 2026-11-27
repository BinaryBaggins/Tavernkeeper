
package io.github.binarybaggins.tavernkeeper.features.guilds.settings;

import java.util.Locale;
import java.util.Objects;
import java.util.Set;

/**
 * Service for managing guild settings.
 */
public final class GuildSettingsService {

    private static final Set<Locale> SUPPORTED_LOCALES = Set.of(Locale.ENGLISH);

    private final GuildSettingsRepository repository;

    /**
     * Constructs a new GuildSettingsService with the given repository.
     *
     * @param repository the GuildSettingsRepository, must not be null
     */
    public GuildSettingsService(GuildSettingsRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    /**
     * Retrieves the settings for the specified guild.
     *
     * @param guildId the ID of the guild, must not be null or blank
     * @return the GuildSettings for the guild, never null
     */
    public GuildSettings getSettings(String guildId) {
        return repository.findByGuildId(guildId)
                .orElseGet(() -> GuildSettings.defaults(guildId));
    }

    /**
     * Sets the locale for the specified guild.
     *
     * @param guildId the ID of the guild, must not be null or blank
     * @param locale  the locale to set, must not be null and must be supported
     * @throws IllegalArgumentException if the locale is not supported
     */
    public void setLocale(String guildId, Locale locale) {
        Objects.requireNonNull(locale);

        if (!SUPPORTED_LOCALES.contains(locale)) {
            throw new IllegalArgumentException(
                    "Unsupported locale: " + locale.toLanguageTag());
        }

        repository.save(new GuildSettings(guildId, locale));
    }

    /**
     * Returns the set of supported locales.
     *
     * @return the set of supported locales, never null
     */
    public Set<Locale> supportedLocales() {
        return SUPPORTED_LOCALES;
    }
}
