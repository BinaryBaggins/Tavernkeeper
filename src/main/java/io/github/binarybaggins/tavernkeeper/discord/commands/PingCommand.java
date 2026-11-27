package io.github.binarybaggins.tavernkeeper.discord.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

/**
 * A slash command that responds with the Discord gateway latency.
 */
public final class PingCommand implements SlashCommand {

    /**
     * Defines the "ping" slash command.
     *
     * @return the slash command data for the "ping" command
     */
    @Override
    public SlashCommandData definition() {
        return Commands.slash(
                "ping",
                "Displays the Discord gateway latency.");
    }

    /**
     * Executes the "ping" slash command.
     *
     * @param event the slash command interaction event
     */
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        long latency = event.getJDA().getGatewayPing();

        event.reply("Pong! Gateway latency: " + latency + " ms")
                .setEphemeral(true)
                .queue();
    }
}
