
package io.github.binarybaggins.tavernkeeper.discord.commands;

import org.junit.jupiter.api.Test;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PingCommandTest {

    @Test
    void shouldProvideCorrectCommandName() {
        var definition = new PingCommand().definition();

        assertEquals("ping", definition.getName());
    }

    @Test
    void shouldProvideCommandDescription() {
        var definition = new PingCommand().definition();

        assertFalse(definition.getDescription().isBlank());
    }

    @Test
    void shouldNotRequireArguments() {
        var definition = new PingCommand().definition();

        assertTrue(definition.getOptions().isEmpty());
    }

    @Test
    void shouldReplyWithGatewayLatency() {
        var event = mock(SlashCommandInteractionEvent.class);
        var jda = mock(JDA.class);
        var reply = mock(ReplyCallbackAction.class);

        when(event.getJDA()).thenReturn(jda);
        when(jda.getGatewayPing()).thenReturn(42L);

        when(event.reply("Pong! Gateway latency: 42 ms"))
                .thenReturn(reply);

        when(reply.setEphemeral(true)).thenReturn(reply);

        new PingCommand().execute(event);

        verify(event).reply("Pong! Gateway latency: 42 ms");
        verify(reply).setEphemeral(true);
        verify(reply).queue();
    }
}
