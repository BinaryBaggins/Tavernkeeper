
package io.github.binarybaggins.tavernkeeper.features.guilds.persistence;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import io.github.binarybaggins.tavernkeeper.features.guilds.GuildRepository;
import io.github.binarybaggins.tavernkeeper.persistence.Database;

import static org.junit.jupiter.api.Assertions.*;

class JdbiGuildRepositoryTest {

    private static final String GUILD_ID = "123456789012345678";
    private static final String OTHER_GUILD_ID = "987654321098765432";

    @TempDir
    Path tempDir;

    private GuildRepository repository;

    @BeforeEach
    void setUp() throws IOException {
        var jdbi = Database.initialize(
                tempDir.resolve("test.db"));

        repository = new JdbiGuildRepository(jdbi);
    }

    @Test
    void shouldRegisterGuild() {
        repository.register(GUILD_ID);

        var guild = repository.findById(GUILD_ID).orElseThrow();

        assertEquals(GUILD_ID, guild.guildId());
        assertNotNull(guild.createdAt());
    }

    @Test
    void shouldReturnEmptyForUnknownGuild() {
        assertTrue(repository.findById(GUILD_ID).isEmpty());
    }

    @Test
    void shouldAllowRepeatedRegistration() {
        repository.register(GUILD_ID);

        var original = repository.findById(GUILD_ID).orElseThrow();

        assertDoesNotThrow(() -> repository.register(GUILD_ID));

        assertEquals(
                original,
                repository.findById(GUILD_ID).orElseThrow());
    }

    @Test
    void shouldStoreGuildsIndependently() {
        repository.register(GUILD_ID);
        repository.register(OTHER_GUILD_ID);

        assertEquals(
                GUILD_ID,
                repository.findById(GUILD_ID).orElseThrow().guildId());

        assertEquals(
                OTHER_GUILD_ID,
                repository.findById(OTHER_GUILD_ID).orElseThrow().guildId());
    }
}
