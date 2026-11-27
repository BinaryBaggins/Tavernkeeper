
package io.github.binarybaggins.tavernkeeper.discord.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public final class PingCommand implements SlashCommand {

    @Override
    public SlashCommandData definition() {
        return Commands.slash(
                "ping",
                "Displays the Discord gateway latency.");
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        long latency = event.getJDA().getGatewayPing();

        event.reply("Pong! Gateway latency: " + latency + " ms")
                .setEphemeral(true)
                .queue();
    }
}
