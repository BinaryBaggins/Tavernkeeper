
package io.github.binarybaggins.tavernkeeper.features.guilds.discord;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.binarybaggins.tavernkeeper.features.guilds.GuildRepository;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildAvailableEvent;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GuildRegistrationListenerTest {

    private static final String GUILD_ID = "123456789012345678";

    private GuildRepository repository;
    private GuildRegistrationListener listener;
    private Guild guild;

    @BeforeEach
    void setUp() {
        repository = mock(GuildRepository.class);
        listener = new GuildRegistrationListener(repository);

        guild = mock(Guild.class);

        when(guild.getId()).thenReturn(GUILD_ID);
        when(guild.getName()).thenReturn("Test Guild");
    }

    @Test
    void shouldRegisterGuildOnJoin() {
        var event = mock(GuildJoinEvent.class);

        when(event.getGuild()).thenReturn(guild);

        listener.onGuildJoin(event);

        verify(repository).register(GUILD_ID);
    }

    @Test
    void shouldRegisterGuildWhenAvailable() {
        var event = mock(GuildAvailableEvent.class);

        when(event.getGuild()).thenReturn(guild);

        listener.onGuildAvailable(event);

        verify(repository).register(GUILD_ID);
    }

    @Test
    void shouldHandleRepositoryFailure() {
        var event = mock(GuildJoinEvent.class);

        when(event.getGuild()).thenReturn(guild);

        doThrow(new IllegalStateException("Database failure"))
                .when(repository).register(GUILD_ID);

        assertDoesNotThrow(() -> listener.onGuildJoin(event));

        verify(repository).register(GUILD_ID);
    }
}
