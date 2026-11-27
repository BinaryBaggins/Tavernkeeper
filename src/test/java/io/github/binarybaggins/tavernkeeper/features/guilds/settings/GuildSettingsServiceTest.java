
package io.github.binarybaggins.tavernkeeper.features.guilds.settings;

import java.util.Locale;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GuildSettingsServiceTest {

    private static final String GUILD_ID = "123456789012345678";

    private GuildSettingsRepository repository;
    private GuildSettingsService service;

    @BeforeEach
    void setUp() {
        repository = mock(GuildSettingsRepository.class);
        service = new GuildSettingsService(repository);
    }

    @Test
    void shouldReturnDefaultSettingsWhenMissing() {
        when(repository.findByGuildId(GUILD_ID))
                .thenReturn(Optional.empty());

        assertEquals(
                GuildSettings.defaults(GUILD_ID),
                service.getSettings(GUILD_ID));

        verify(repository, never()).save(any());
    }

    @Test
    void shouldReturnPersistedSettings() {
        var settings = new GuildSettings(GUILD_ID, Locale.ENGLISH);

        when(repository.findByGuildId(GUILD_ID))
                .thenReturn(Optional.of(settings));

        assertSame(settings, service.getSettings(GUILD_ID));
    }

    @Test
    void shouldSaveSupportedLocale() {
        service.setLocale(GUILD_ID, Locale.ENGLISH);

        verify(repository).save(
                new GuildSettings(GUILD_ID, Locale.ENGLISH));
    }

    @Test
    void shouldRejectUnsupportedLocale() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.setLocale(GUILD_ID, Locale.JAPANESE));

        verifyNoInteractions(repository);
    }

    @Test
    void shouldRejectNullLocale() {
        assertThrows(
                NullPointerException.class,
                () -> service.setLocale(GUILD_ID, null));

        verifyNoInteractions(repository);
    }

    @Test
    void shouldExposeSupportedLocales() {
        assertTrue(service.supportedLocales().contains(Locale.ENGLISH));
        assertFalse(service.supportedLocales().contains(Locale.JAPANESE));
    }
}
