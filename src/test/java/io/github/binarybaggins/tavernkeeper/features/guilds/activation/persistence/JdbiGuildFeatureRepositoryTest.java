package io.github.binarybaggins.tavernkeeper.features.guilds.activation.persistence;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;

import org.jdbi.v3.core.statement.UnableToExecuteStatementException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import io.github.binarybaggins.tavernkeeper.features.guilds.activation.GuildFeatureRepository;
import io.github.binarybaggins.tavernkeeper.features.guilds.persistence.JdbiGuildRepository;
import io.github.binarybaggins.tavernkeeper.persistence.Database;

import static org.junit.jupiter.api.Assertions.*;

class JdbiGuildFeatureRepositoryTest {

    private static final String GUILD_ID = "123456789012345678";
    private static final String OTHER_GUILD_ID = "987654321098765432";

    private static final String PROGRESSION = "progression";
    private static final String ECONOMY = "economy";

    @TempDir
    Path tempDir;

    private GuildFeatureRepository repository;

    @BeforeEach
    void setUp() throws IOException {
        var jdbi = Database.initialize(
                tempDir.resolve("test.db"));

        var guildRepository = new JdbiGuildRepository(jdbi);

        guildRepository.register(GUILD_ID);
        guildRepository.register(OTHER_GUILD_ID);

        repository = new JdbiGuildFeatureRepository(jdbi);
    }

    @Test
    void shouldReturnEmptySetByDefault() {
        assertTrue(
                repository.findEnabledFeatures(GUILD_ID).isEmpty());
    }

    @Test
    void shouldEnableFeature() {
        repository.setEnabled(GUILD_ID, PROGRESSION, true);

        assertEquals(
                Set.of(PROGRESSION),
                repository.findEnabledFeatures(GUILD_ID));
    }

    @Test
    void shouldDisableFeature() {
        repository.setEnabled(GUILD_ID, PROGRESSION, true);
        repository.setEnabled(GUILD_ID, PROGRESSION, false);

        assertTrue(
                repository.findEnabledFeatures(GUILD_ID).isEmpty());
    }

    @Test
    void shouldAllowRepeatedActivation() {
        repository.setEnabled(GUILD_ID, PROGRESSION, true);
        repository.setEnabled(GUILD_ID, PROGRESSION, true);

        assertEquals(
                Set.of(PROGRESSION),
                repository.findEnabledFeatures(GUILD_ID));
    }

    @Test
    void shouldAllowRepeatedDeactivation() {
        repository.setEnabled(GUILD_ID, PROGRESSION, false);
        repository.setEnabled(GUILD_ID, PROGRESSION, false);

        assertTrue(
                repository.findEnabledFeatures(GUILD_ID).isEmpty());
    }

    @Test
    void shouldManageFeaturesIndependently() {
        repository.setEnabled(GUILD_ID, PROGRESSION, true);
        repository.setEnabled(GUILD_ID, ECONOMY, true);

        assertEquals(
                Set.of(PROGRESSION, ECONOMY),
                repository.findEnabledFeatures(GUILD_ID));

        repository.setEnabled(GUILD_ID, PROGRESSION, false);

        assertEquals(
                Set.of(ECONOMY),
                repository.findEnabledFeatures(GUILD_ID));
    }

    @Test
    void shouldManageGuildsIndependently() {
        repository.setEnabled(GUILD_ID, PROGRESSION, true);
        repository.setEnabled(OTHER_GUILD_ID, ECONOMY, true);

        assertEquals(
                Set.of(PROGRESSION),
                repository.findEnabledFeatures(GUILD_ID));

        assertEquals(
                Set.of(ECONOMY),
                repository.findEnabledFeatures(OTHER_GUILD_ID));
    }

    @Test
    void shouldReturnImmutableSet() {
        repository.setEnabled(GUILD_ID, PROGRESSION, true);

        var enabled = repository.findEnabledFeatures(GUILD_ID);

        assertThrows(
                UnsupportedOperationException.class,
                () -> enabled.add(ECONOMY));
    }

    @Test
    void shouldRejectUnregisteredGuild() {
        assertThrows(
                UnableToExecuteStatementException.class,
                () -> repository.setEnabled(
                        "111111111111111111",
                        PROGRESSION,
                        true));
    }
}
