package io.github.binarybaggins.tavernkeeper.discord.commands;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.JDA;

/**
 * A registry for managing slash commands.
 */
public final class CommandRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandRegistry.class);

    private final Map<String, SlashCommand> commands = new HashMap<>();

    /**
     * Registers a new slash command.
     *
     * @param command the command to register
     * @throws IllegalArgumentException if a command with the same name is already
     *                                  registered
     */
    public void register(SlashCommand command) {
        String name = command.definition().getName();

        if (commands.putIfAbsent(name, command) != null) {
            throw new IllegalArgumentException(
                    "Duplicate command: " + name);
        }

        LOGGER.debug("Registered command: /{}", name);
    }

    /**
     * Finds a registered slash command by its name.
     *
     * @param name the name of the command
     * @return an {@link Optional} containing the command if found, or empty if not
     *         found
     */
    public Optional<SlashCommand> find(String name) {
        return Optional.ofNullable(commands.get(name));
    }

    /**
     * Synchronizes the registered slash commands with the given JDA instance.
     *
     * @param jda the JDA instance to synchronize commands with
     */
    public void synchronize(JDA jda) {
        var definitions = commands.values().stream()
                .map(SlashCommand::definition)
                .toList();

        jda.updateCommands()
                .addCommands(definitions)
                .queue(
                        registered -> LOGGER.info(
                                "Synchronized {} slash commands.",
                                registered.size()),
                        error -> LOGGER.error(
                                "Command synchronization failed.",
                                error));
    }
}
