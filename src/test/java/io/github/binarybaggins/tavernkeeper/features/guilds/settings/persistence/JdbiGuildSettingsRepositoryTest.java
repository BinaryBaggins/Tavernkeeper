package io.github.binarybaggins.tavernkeeper.features.guilds.settings.persistence;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Locale;

import org.jdbi.v3.core.statement.UnableToExecuteStatementException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import io.github.binarybaggins.tavernkeeper.features.guilds.persistence.JdbiGuildRepository;
import io.github.binarybaggins.tavernkeeper.features.guilds.settings.GuildSettings;
import io.github.binarybaggins.tavernkeeper.features.guilds.settings.GuildSettingsRepository;
import io.github.binarybaggins.tavernkeeper.persistence.Database;

import static org.junit.jupiter.api.Assertions.*;

class JdbiGuildSettingsRepositoryTest {

    private static final String GUILD_ID = "123456789012345678";
    private static final String OTHER_GUILD_ID = "987654321098765432";

    @TempDir
    Path tempDir;

    private GuildSettingsRepository repository;

    @BeforeEach
    void setUp() throws IOException {
        var jdbi = Database.initialize(
                tempDir.resolve("test.db"));

        var guildRepository = new JdbiGuildRepository(jdbi);

        guildRepository.register(GUILD_ID);
        guildRepository.register(OTHER_GUILD_ID);

        repository = new JdbiGuildSettingsRepository(jdbi);
    }

    @Test
    void shouldReturnEmptyForMissingSettings() {
        assertTrue(repository.findByGuildId(GUILD_ID).isEmpty());
    }

    @Test
    void shouldSaveGuildSettings() {
        var settings = new GuildSettings(GUILD_ID, Locale.GERMAN);

        repository.save(settings);

        assertEquals(
                settings,
                repository.findByGuildId(GUILD_ID).orElseThrow());
    }

    @Test
    void shouldUpdateExistingSettings() {
        repository.save(new GuildSettings(GUILD_ID, Locale.ENGLISH));

        var updated = new GuildSettings(GUILD_ID, Locale.GERMAN);

        repository.save(updated);

        assertEquals(
                updated,
                repository.findByGuildId(GUILD_ID).orElseThrow());
    }

    @Test
    void shouldPreserveRegionalLocale() {
        var settings = new GuildSettings(
                GUILD_ID,
                Locale.forLanguageTag("de-CH"));

        repository.save(settings);

        assertEquals(
                settings,
                repository.findByGuildId(GUILD_ID).orElseThrow());
    }

    @Test
    void shouldStoreGuildSettingsIndependently() {
        var first = new GuildSettings(GUILD_ID, Locale.GERMAN);
        var second = new GuildSettings(OTHER_GUILD_ID, Locale.ENGLISH);

        repository.save(first);
        repository.save(second);

        assertEquals(
                first,
                repository.findByGuildId(GUILD_ID).orElseThrow());

        assertEquals(
                second,
                repository.findByGuildId(OTHER_GUILD_ID).orElseThrow());
    }

    @Test
    void shouldRejectSettingsForUnregisteredGuild() {
        var settings = new GuildSettings(
                "111111111111111111",
                Locale.ENGLISH);

        assertThrows(
                UnableToExecuteStatementException.class,
                () -> repository.save(settings));
    }
}
