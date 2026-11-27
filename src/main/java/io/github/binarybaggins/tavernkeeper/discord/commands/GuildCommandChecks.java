
package io.github.binarybaggins.tavernkeeper.discord.commands;

import java.util.Optional;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * Utility class for checking guild-related conditions in Discord commands.
 */
public final class GuildCommandChecks {

    private GuildCommandChecks() {
    }

    /**
     * Ensures that the command is executed within a guild.
     *
     * @param event the slash command interaction event
     * @return an Optional containing the guild if present, otherwise empty
     */
    public static Optional<Guild> requireGuild(
            SlashCommandInteractionEvent event) {

        var guild = event.getGuild();

        if (guild == null) {
            event.reply("This command can only be used in a server.")
                    .setEphemeral(true)
                    .queue();

            return Optional.empty();
        }

        return Optional.of(guild);
    }

    /**
     * Ensures that the command is executed within a guild and that the user has the
     * Manage Server permission.
     *
     * @param event the slash command interaction event
     * @return an Optional containing the guild if the user has the required
     *         permission, otherwise empty
     */
    public static Optional<Guild> requireGuildManager(
            SlashCommandInteractionEvent event) {

        var guild = requireGuild(event);

        if (guild.isEmpty()) {
            return Optional.empty();
        }

        var member = event.getMember();

        if (member == null
                || !member.hasPermission(Permission.MANAGE_SERVER)) {

            event.reply("You need the Manage Server permission "
                    + "to use this command.")
                    .setEphemeral(true)
                    .queue();

            return Optional.empty();
        }

        return guild;
    }
}
