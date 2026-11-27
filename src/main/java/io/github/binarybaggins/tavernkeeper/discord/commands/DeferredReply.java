package io.github.binarybaggins.tavernkeeper.discord.commands;

import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * Utility class for sending deferred replies to slash commands.
 */
public final class DeferredReply {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeferredReply.class);

    private DeferredReply() {
    }

    /**
     * Sends a deferred reply to the given slash command event.
     *
     * @param event    the slash command interaction event
     * @param response a supplier that provides the response message
     */
    public static void send(
            SlashCommandInteractionEvent event,
            Supplier<String> response) {

        event.deferReply(true).queue(hook -> {
            try {
                hook.editOriginal(response.get()).queue();
            } catch (RuntimeException e) {
                LOGGER.error(
                        "Failed to process command /{}.",
                        event.getName(), e);

                hook.editOriginal("An unexpected error occurred.")
                        .queue();
            }
        });
    }
}
