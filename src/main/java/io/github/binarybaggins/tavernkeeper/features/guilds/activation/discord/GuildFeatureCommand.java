package io.github.binarybaggins.tavernkeeper.features.guilds.activation.discord;

import java.util.Objects;
import java.util.stream.Collectors;

import io.github.binarybaggins.tavernkeeper.discord.commands.DeferredReply;
import io.github.binarybaggins.tavernkeeper.discord.commands.GuildCommandChecks;
import io.github.binarybaggins.tavernkeeper.discord.commands.SlashCommand;
import io.github.binarybaggins.tavernkeeper.features.guilds.activation.GuildFeatureService;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

/**
 * Handles the /features command for managing optional server features.
 */
public final class GuildFeatureCommand implements SlashCommand {

    private final GuildFeatureService service;

    /**
     * Constructs a new GuildFeatureCommand with the specified service.
     *
     * @param service the guild feature service
     */
    public GuildFeatureCommand(GuildFeatureService service) {
        this.service = Objects.requireNonNull(service);
    }

    /**
     * Returns the definition of the /features command.
     *
     * @return the slash command data
     */
    @Override
    public SlashCommandData definition() {
        var command = Commands.slash(
                "features",
                "Manage optional server features");

        command.addSubcommands(
                new SubcommandData(
                        "list",
                        "List available server features"));

        if (!service.supportedFeatures().isEmpty()) {
            command.addSubcommands(
                    new SubcommandData(
                            "enable",
                            "Enable a server feature")
                            .addOptions(createFeatureOption()),
                    new SubcommandData(
                            "disable",
                            "Disable a server feature")
                            .addOptions(createFeatureOption()));
        }

        return command;
    }

    private OptionData createFeatureOption() {
        var option = new OptionData(
                OptionType.STRING,
                "feature",
                "The feature to manage",
                true);

        service.supportedFeatures().stream()
                .sorted()
                .forEach(feature -> option.addChoice(feature, feature));

        return option;
    }

    /**
     * Executes the /features command.
     *
     * @param event the slash command interaction event
     */
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        switch (event.getSubcommandName()) {
            case "list" -> listFeatures(event);
            case "enable" -> setFeatureEnabled(event, true);
            case "disable" -> setFeatureEnabled(event, false);
            case null, default -> event.reply("Unknown features command.")
                    .setEphemeral(true)
                    .queue();
        }
    }

    private void listFeatures(SlashCommandInteractionEvent event) {
        var guild = GuildCommandChecks.requireGuild(event);

        if (guild.isEmpty()) {
            return;
        }

        var supported = service.supportedFeatures();

        if (supported.isEmpty()) {
            event.reply("No optional features are currently available.")
                    .setEphemeral(true)
                    .queue();

            return;
        }

        String guildId = guild.get().getId();

        DeferredReply.send(event, () -> {
            var enabled = service.enabledFeatures(guildId);

            return supported.stream()
                    .sorted()
                    .map(feature -> "- **" + feature + "**: "
                            + (enabled.contains(feature)
                                    ? "Enabled"
                                    : "Disabled"))
                    .collect(Collectors.joining(
                            "\n",
                            "**Server features**\n",
                            ""));
        });
    }

    private void setFeatureEnabled(
            SlashCommandInteractionEvent event,
            boolean enabled) {

        var guild = GuildCommandChecks.requireGuildManager(event);

        if (guild.isEmpty()) {
            return;
        }

        var option = event.getOption("feature");

        if (option == null) {
            event.reply("A feature must be specified.")
                    .setEphemeral(true)
                    .queue();

            return;
        }

        String feature = option.getAsString();

        if (!service.supportedFeatures().contains(feature)) {
            event.reply("This feature is not supported.")
                    .setEphemeral(true)
                    .queue();

            return;
        }

        String guildId = guild.get().getId();

        DeferredReply.send(event, () -> {
            service.setEnabled(guildId, feature, enabled);

            return "Feature **" + feature + "** has been "
                    + (enabled ? "enabled" : "disabled")
                    + ".";
        });
    }
}
