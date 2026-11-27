
package io.github.binarybaggins.tavernkeeper.discord.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

import static org.mockito.Mockito.*;

class CommandListenerTest {

    private CommandRegistry registry;
    private CommandListener listener;
    private SlashCommandInteractionEvent event;

    @BeforeEach
    void setUp() {
        registry = new CommandRegistry();
        listener = new CommandListener(registry);
        event = mock(SlashCommandInteractionEvent.class);
    }

    @Test
    void shouldDispatchRegisteredCommand() {
        var command = registerCommand("test");

        when(event.getName()).thenReturn("test");

        listener.onSlashCommandInteraction(event);

        verify(command).execute(event);
    }

    @Test
    void shouldReplyToUnknownCommand() {
        when(event.getName()).thenReturn("unknown");

        var reply = mockReply("Unknown command.");

        listener.onSlashCommandInteraction(event);

        verify(event).reply("Unknown command.");
        verify(reply).setEphemeral(true);
        verify(reply).queue();
    }

    @Test
    void shouldHandleCommandExecutionFailure() {
        var command = registerCommand("test");

        when(event.getName()).thenReturn("test");

        doThrow(new IllegalStateException("Test failure"))
                .when(command).execute(event);

        var reply = mockReply("An unexpected error occurred.");

        listener.onSlashCommandInteraction(event);

        verify(command).execute(event);
        verify(reply).setEphemeral(true);
        verify(reply).queue();
    }

    @Test
    void shouldNotReplyAgainWhenAlreadyAcknowledged() {
        var command = registerCommand("test");

        when(event.getName()).thenReturn("test");
        when(event.isAcknowledged()).thenReturn(true);

        doThrow(new IllegalStateException("Test failure"))
                .when(command).execute(event);

        listener.onSlashCommandInteraction(event);

        verify(command).execute(event);
        verify(event, never()).reply(anyString());
    }

    private SlashCommand registerCommand(String name) {
        var command = mock(SlashCommand.class);

        when(command.definition())
                .thenReturn(Commands.slash(name, "Test command."));

        registry.register(command);

        return command;
    }

    private ReplyCallbackAction mockReply(String message) {
        var reply = mock(ReplyCallbackAction.class);

        when(event.reply(message)).thenReturn(reply);
        when(reply.setEphemeral(true)).thenReturn(reply);

        return reply;
    }
}
