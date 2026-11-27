package io.github.binarybaggins.tavernkeeper.discord.commands;

import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public interface SlashCommand {

    SlashCommandData definition();

    void execute(SlashCommandInteractionEvent event);

}
