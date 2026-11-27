
package io.github.binarybaggins.tavernkeeper.features.guilds.activation;

import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GuildFeatureServiceTest {

    private static final String GUILD_ID = "123456789012345678";
    private static final String OTHER_GUILD_ID = "987654321098765432";

    private static final String PROGRESSION = "progression";
    private static final String ECONOMY = "economy";
    private static final String UNKNOWN = "unknown";

    private GuildFeatureRepository repository;
    private GuildFeatureService service;

    @BeforeEach
    void setUp() {
        repository = mock(GuildFeatureRepository.class);

        service = new GuildFeatureService(
                repository,
                Set.of(PROGRESSION, ECONOMY));
    }

    @Test
    void shouldLoadFeaturesOnlyOnce() {
        when(repository.findEnabledFeatures(GUILD_ID))
                .thenReturn(Set.of(PROGRESSION));

        assertTrue(service.isEnabled(GUILD_ID, PROGRESSION));
        assertTrue(service.isEnabled(GUILD_ID, PROGRESSION));
        assertFalse(service.isEnabled(GUILD_ID, ECONOMY));

        verify(repository, times(1))
                .findEnabledFeatures(GUILD_ID);
    }

    @Test
    void shouldCacheGuildsIndependently() {
        when(repository.findEnabledFeatures(GUILD_ID))
                .thenReturn(Set.of(PROGRESSION));

        when(repository.findEnabledFeatures(OTHER_GUILD_ID))
                .thenReturn(Set.of(ECONOMY));

        assertEquals(
                Set.of(PROGRESSION),
                service.enabledFeatures(GUILD_ID));

        assertEquals(
                Set.of(ECONOMY),
                service.enabledFeatures(OTHER_GUILD_ID));
    }

    @Test
    void shouldIgnoreUnsupportedPersistedFeatures() {
        when(repository.findEnabledFeatures(GUILD_ID))
                .thenReturn(Set.of(PROGRESSION, UNKNOWN));

        assertEquals(
                Set.of(PROGRESSION),
                service.enabledFeatures(GUILD_ID));
    }

    @Test
    void shouldUpdateCacheAfterSuccessfulWrite() {
        when(repository.findEnabledFeatures(GUILD_ID))
                .thenReturn(Set.of());

        service.enabledFeatures(GUILD_ID);

        service.setEnabled(GUILD_ID, PROGRESSION, true);

        assertTrue(service.isEnabled(GUILD_ID, PROGRESSION));

        service.setEnabled(GUILD_ID, PROGRESSION, false);

        assertFalse(service.isEnabled(GUILD_ID, PROGRESSION));

        verify(repository)
                .setEnabled(GUILD_ID, PROGRESSION, true);

        verify(repository)
                .setEnabled(GUILD_ID, PROGRESSION, false);

        verify(repository, times(1))
                .findEnabledFeatures(GUILD_ID);
    }

    @Test
    void shouldLoadExistingFeaturesBeforeFirstWrite() {
        when(repository.findEnabledFeatures(GUILD_ID))
                .thenReturn(Set.of(PROGRESSION));

        service.setEnabled(GUILD_ID, ECONOMY, true);

        assertEquals(
                Set.of(PROGRESSION, ECONOMY),
                service.enabledFeatures(GUILD_ID));
    }

    @Test
    void shouldPreserveCacheWhenWriteFails() {
        when(repository.findEnabledFeatures(GUILD_ID))
                .thenReturn(Set.of(PROGRESSION));

        service.enabledFeatures(GUILD_ID);

        doThrow(new IllegalStateException("Database failure"))
                .when(repository)
                .setEnabled(GUILD_ID, PROGRESSION, false);

        assertThrows(
                IllegalStateException.class,
                () -> service.setEnabled(
                        GUILD_ID, PROGRESSION, false));

        assertTrue(service.isEnabled(GUILD_ID, PROGRESSION));
    }

    @Test
    void shouldNotPopulateCacheWhenInitialWriteFails() {
        when(repository.findEnabledFeatures(GUILD_ID))
                .thenReturn(Set.of());

        doThrow(new IllegalStateException("Database failure"))
                .when(repository)
                .setEnabled(GUILD_ID, PROGRESSION, true);

        assertThrows(
                IllegalStateException.class,
                () -> service.setEnabled(
                        GUILD_ID, PROGRESSION, true));

        assertFalse(service.isEnabled(GUILD_ID, PROGRESSION));

        verify(repository, times(2))
                .findEnabledFeatures(GUILD_ID);
    }

    @Test
    void shouldRejectUnsupportedFeatures() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.isEnabled(GUILD_ID, UNKNOWN));

        assertThrows(
                IllegalArgumentException.class,
                () -> service.setEnabled(GUILD_ID, UNKNOWN, true));

        verifyNoInteractions(repository);
    }

    @Test
    void shouldReturnImmutableFeatureSets() {
        when(repository.findEnabledFeatures(GUILD_ID))
                .thenReturn(Set.of(PROGRESSION));

        assertThrows(
                UnsupportedOperationException.class,
                () -> service.enabledFeatures(GUILD_ID)
                        .add(ECONOMY));

        assertThrows(
                UnsupportedOperationException.class,
                () -> service.supportedFeatures()
                        .add(UNKNOWN));
    }

    @Test
    void shouldPreserveConcurrentUpdates() throws Exception {
        when(repository.findEnabledFeatures(GUILD_ID))
                .thenReturn(Set.of());

        var executor = Executors.newFixedThreadPool(2);
        var start = new CountDownLatch(1);

        try {
            var first = executor.submit(() -> {
                start.await();
                service.setEnabled(GUILD_ID, PROGRESSION, true);
                return null;
            });

            var second = executor.submit(() -> {
                start.await();
                service.setEnabled(GUILD_ID, ECONOMY, true);
                return null;
            });

            start.countDown();

            first.get(5, TimeUnit.SECONDS);
            second.get(5, TimeUnit.SECONDS);

            assertEquals(
                    Set.of(PROGRESSION, ECONOMY),
                    service.enabledFeatures(GUILD_ID));

            verify(repository, times(1))
                    .findEnabledFeatures(GUILD_ID);

            verify(repository)
                    .setEnabled(GUILD_ID, PROGRESSION, true);

            verify(repository)
                    .setEnabled(GUILD_ID, ECONOMY, true);
        } finally {
            executor.shutdownNow();
        }
    }
}
