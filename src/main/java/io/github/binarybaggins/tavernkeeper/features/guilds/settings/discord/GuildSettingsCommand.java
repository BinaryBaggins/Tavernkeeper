package io.github.binarybaggins.tavernkeeper.features.guilds.settings.discord;

import java.util.Comparator;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.binarybaggins.tavernkeeper.discord.commands.GuildCommandChecks;
import io.github.binarybaggins.tavernkeeper.discord.commands.SlashCommand;
import io.github.binarybaggins.tavernkeeper.features.guilds.settings.GuildSettingsService;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

/**
 * Handles the /settings command for managing guild settings.
 */
public final class GuildSettingsCommand implements SlashCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(GuildSettingsCommand.class);

    private final GuildSettingsService service;

    /**
     * Constructs a new GuildSettingsCommand with the specified service.
     *
     * @param service the guild settings service
     */
    public GuildSettingsCommand(GuildSettingsService service) {
        this.service = Objects.requireNonNull(service);
    }

    /**
     * Returns the definition of the /settings command.
     *
     * @return the slash command data
     */
    @Override
    public SlashCommandData definition() {
        var languageOption = new OptionData(
                OptionType.STRING,
                "language",
                "The preferred server language",
                true);

        service.supportedLocales().stream()
                .sorted(Comparator.comparing(Locale::toLanguageTag))
                .forEach(locale -> languageOption.addChoice(
                        locale.getDisplayName(Locale.ENGLISH),
                        locale.toLanguageTag()));

        return Commands.slash("settings", "Manage server settings")
                .addSubcommands(
                        new SubcommandData(
                                "show",
                                "Display current server settings"),
                        new SubcommandData(
                                "language",
                                "Change the server language")
                                .addOptions(languageOption));
    }

    /**
     * Executes the /settings command based on the subcommand.
     *
     * @param event the slash command interaction event
     */
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        switch (event.getSubcommandName()) {
            case "show" -> showSettings(event);
            case "language" -> setLanguage(event);
            case null, default -> event.reply("Unknown settings command.")
                    .setEphemeral(true)
                    .queue();
        }
    }

    private void showSettings(SlashCommandInteractionEvent event) {
        var guild = GuildCommandChecks.requireGuild(event);

        if (guild.isEmpty()) {
            return;
        }

        String guildId = guild.get().getId();

        replyDeferred(event, () -> {
            var settings = service.getSettings(guildId);

            return "**Server settings**\nLanguage: "
                    + settings.locale().getDisplayName(Locale.ENGLISH);
        });
    }

    private void setLanguage(SlashCommandInteractionEvent event) {
        var guild = GuildCommandChecks.requireGuildManager(event);

        if (guild.isEmpty()) {
            return;
        }

        var option = event.getOption("language");

        if (option == null) {
            event.reply("A language must be specified.")
                    .setEphemeral(true)
                    .queue();

            return;
        }

        var locale = Locale.forLanguageTag(option.getAsString());

        if (!service.supportedLocales().contains(locale)) {
            event.reply("This language is not supported.")
                    .setEphemeral(true)
                    .queue();

            return;
        }

        String guildId = guild.get().getId();

        replyDeferred(event, () -> {
            service.setLocale(guildId, locale);

            return "Server language changed to **"
                    + locale.getDisplayName(Locale.ENGLISH)
                    + "**.";
        });
    }

    /**
     * Replies to the interaction with a deferred response.
     *
     * @param event    the slash command interaction event
     * @param response a supplier that provides the response message
     */
    private void replyDeferred(
            SlashCommandInteractionEvent event,
            Supplier<String> response) {

        event.deferReply(true).queue(hook -> {
            try {
                hook.editOriginal(response.get()).queue();
            } catch (RuntimeException e) {
                LOGGER.error("Failed to process guild settings command.", e);

                hook.editOriginal("An unexpected error occurred.")
                        .queue();
            }
        });
    }
}
