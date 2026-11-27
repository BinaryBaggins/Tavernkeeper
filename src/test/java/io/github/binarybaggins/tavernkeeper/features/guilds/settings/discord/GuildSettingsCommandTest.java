package io.github.binarybaggins.tavernkeeper.features.guilds.settings.discord;

import java.util.Locale;
import java.util.Set;
import java.util.function.Consumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.binarybaggins.tavernkeeper.features.guilds.settings.GuildSettings;
import io.github.binarybaggins.tavernkeeper.features.guilds.settings.GuildSettingsService;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

import static org.mockito.Mockito.*;

class GuildSettingsCommandTest {

    private static final String GUILD_ID = "123456789012345678";

    private GuildSettingsService service;
    private GuildSettingsCommand command;

    private SlashCommandInteractionEvent event;
    private Guild guild;
    private Member member;
    private ReplyCallbackAction replyAction;
    private InteractionHook hook;

    @BeforeEach
    void setUp() {
        service = mock(GuildSettingsService.class);
        command = new GuildSettingsCommand(service);

        event = mock(SlashCommandInteractionEvent.class);
        guild = mock(Guild.class);
        member = mock(Member.class);
        replyAction = mock(ReplyCallbackAction.class);
        hook = mock(InteractionHook.class, RETURNS_DEEP_STUBS);

        when(guild.getId()).thenReturn(GUILD_ID);
        when(event.getGuild()).thenReturn(guild);
        when(event.getMember()).thenReturn(member);

        when(event.reply(anyString())).thenReturn(replyAction);
        when(replyAction.setEphemeral(true)).thenReturn(replyAction);
        when(event.deferReply(true)).thenReturn(replyAction);

        when(service.supportedLocales())
                .thenReturn(Set.of(Locale.ENGLISH));

        doAnswer(invocation -> {
            Consumer<InteractionHook> callback = invocation.getArgument(0);

            callback.accept(hook);

            return null;
        }).when(replyAction).queue(any());
    }

    @Test
    void shouldShowGuildSettings() {
        when(event.getSubcommandName()).thenReturn("show");

        when(service.getSettings(GUILD_ID))
                .thenReturn(GuildSettings.defaults(GUILD_ID));

        command.execute(event);

        var order = inOrder(event, service);

        order.verify(event).deferReply(true);
        order.verify(service).getSettings(GUILD_ID);

        verify(hook).editOriginal(
                "**Server settings**\nLanguage: English");
    }

    @Test
    void shouldRejectShowOutsideGuild() {
        when(event.getSubcommandName()).thenReturn("show");
        when(event.getGuild()).thenReturn(null);

        command.execute(event);

        verify(event).reply(
                "This command can only be used in a server.");

        verify(replyAction).setEphemeral(true);
        verifyNoInteractions(service);
    }

    @Test
    void shouldAllowManagerToChangeLanguage() {
        prepareLanguageChange("en");

        command.execute(event);

        var order = inOrder(event, service);

        order.verify(event).deferReply(true);
        order.verify(service).setLocale(GUILD_ID, Locale.ENGLISH);

        verify(hook).editOriginal(
                "Server language changed to **English**.");
    }

    @Test
    void shouldRejectUnauthorizedLanguageChange() {
        prepareLanguageChange("en");

        when(member.hasPermission(Permission.MANAGE_SERVER))
                .thenReturn(false);

        command.execute(event);

        verify(event).reply(
                "You need the Manage Server permission "
                        + "to use this command.");

        verifyNoInteractions(service);
        verify(event, never()).deferReply(true);
    }

    @Test
    void shouldRejectMissingLanguageOption() {
        prepareLanguageChange("en");

        when(event.getOption("language")).thenReturn(null);

        command.execute(event);

        verify(event).reply("A language must be specified.");

        verifyNoInteractions(service);
        verify(event, never()).deferReply(true);
    }

    @Test
    void shouldRejectUnsupportedLanguage() {
        prepareLanguageChange("ja");

        command.execute(event);

        verify(event).reply("This language is not supported.");

        verify(service, never()).setLocale(anyString(), any());
        verify(event, never()).deferReply(true);
    }

    @Test
    void shouldRejectUnknownSubcommand() {
        when(event.getSubcommandName()).thenReturn("unknown");

        command.execute(event);

        verify(event).reply("Unknown settings command.");

        verifyNoInteractions(service);
    }

    @Test
    void shouldHandleSettingsReadFailure() {
        when(event.getSubcommandName()).thenReturn("show");

        when(service.getSettings(GUILD_ID))
                .thenThrow(new IllegalStateException("Database failure"));

        command.execute(event);

        verify(hook).editOriginal(
                "An unexpected error occurred.");
    }

    @Test
    void shouldHandleSettingsWriteFailure() {
        prepareLanguageChange("en");

        doThrow(new IllegalStateException("Database failure"))
                .when(service)
                .setLocale(GUILD_ID, Locale.ENGLISH);

        command.execute(event);

        verify(hook).editOriginal(
                "An unexpected error occurred.");
    }

    private void prepareLanguageChange(String languageTag) {
        when(event.getSubcommandName()).thenReturn("language");

        when(member.hasPermission(Permission.MANAGE_SERVER))
                .thenReturn(true);

        var option = mock(OptionMapping.class);

        when(option.getAsString()).thenReturn(languageTag);
        when(event.getOption("language")).thenReturn(option);
    }
}
