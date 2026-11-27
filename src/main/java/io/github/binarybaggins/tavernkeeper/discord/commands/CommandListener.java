
package io.github.binarybaggins.tavernkeeper.discord.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public final class CommandListener extends ListenerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandListener.class);

    private final CommandRegistry registry;

    public CommandListener(CommandRegistry registry) {
        this.registry = registry;
    }

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
