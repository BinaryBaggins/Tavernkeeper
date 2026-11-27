package io.github.binarybaggins.tavernkeeper.discord.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * A listener for handling slash command interactions.
 */
public final class CommandListener extends ListenerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandListener.class);

    private final CommandRegistry registry;

    /**
     * Constructs a new {@link CommandListener} with the given
     * {@link CommandRegistry}.
     *
     * @param registry the command registry to use for finding commands
     */
    public CommandListener(CommandRegistry registry) {
        this.registry = registry;
    }

    /**
     * Handles a slash command interaction event.
     *
     * @param event the slash command interaction event
     */
    @Override
    public void onSlashCommandInteraction(
            SlashCommandInteractionEvent event) {
        registry.find(event.getName()).ifPresentOrElse(
                command -> execute(command, event),
                () -> event.reply("Unknown command.")
                        .setEphemeral(true)
                        .queue());
    }

    private void execute(
            SlashCommand command,
            SlashCommandInteractionEvent event) {
        try {
            command.execute(event);
        } catch (RuntimeException e) {
            LOGGER.error(
                    "Failed to execute command /{}",
                    event.getName(),
                    e);

            if (!event.isAcknowledged()) {
                event.reply("An unexpected error occurred.")
                        .setEphemeral(true)
                        .queue();
            }
        }
    }
}
