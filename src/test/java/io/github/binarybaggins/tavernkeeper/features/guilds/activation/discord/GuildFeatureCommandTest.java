package io.github.binarybaggins.tavernkeeper.features.guilds.activation.discord;

import java.util.Set;
import java.util.function.Consumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import io.github.binarybaggins.tavernkeeper.features.guilds.activation.GuildFeatureService;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GuildFeatureCommandTest {

    private static final String GUILD_ID = "123456789012345678";

    private static final String PROGRESSION = "progression";
    private static final String ECONOMY = "economy";

    private GuildFeatureService service;
    private GuildFeatureCommand command;

    private SlashCommandInteractionEvent event;
    private Guild guild;
    private Member member;
    private ReplyCallbackAction replyAction;
    private InteractionHook hook;

    @BeforeEach
    void setUp() {
        service = mock(GuildFeatureService.class);
        command = new GuildFeatureCommand(service);

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

        when(service.supportedFeatures())
                .thenReturn(Set.of(PROGRESSION, ECONOMY));

        doAnswer(invocation -> {
            Consumer<InteractionHook> callback = invocation.getArgument(0);

            callback.accept(hook);

            return null;
        }).when(replyAction).queue(any());
    }

    @Test
    void shouldOnlyRegisterListWhenNoFeaturesAreSupported() {
        when(service.supportedFeatures()).thenReturn(Set.of());

        var definition = command.definition();

        assertEquals("features", definition.getName());

        assertEquals(
                Set.of("list"),
                Set.copyOf(definition.getSubcommands().stream()
                        .map(SubcommandData::getName)
                        .toList()));
    }

    @Test
    void shouldRegisterManagementSubcommandsWhenFeaturesAreSupported() {
        var definition = command.definition();

        assertEquals(
                Set.of("list", "enable", "disable"),
                Set.copyOf(definition.getSubcommands().stream()
                        .map(SubcommandData::getName)
                        .toList()));

        for (var subcommand : definition.getSubcommands()) {
            if (subcommand.getName().equals("list")) {
                assertTrue(subcommand.getOptions().isEmpty());
                continue;
            }

            var option = subcommand.getOptions().getFirst();

            assertEquals("feature", option.getName());
            assertTrue(option.isRequired());

            assertEquals(
                    Set.of(PROGRESSION, ECONOMY),
                    Set.copyOf(option.getChoices().stream()
                            .map(choice -> choice.getAsString())
                            .toList()));
        }
    }

    @Test
    void shouldReportWhenNoFeaturesAreAvailable() {
        when(event.getSubcommandName()).thenReturn("list");
        when(service.supportedFeatures()).thenReturn(Set.of());

        command.execute(event);

        verify(event).reply(
                "No optional features are currently available.");

        verify(replyAction).setEphemeral(true);
        verify(service, never()).enabledFeatures(anyString());
        verify(event, never()).deferReply(true);
    }

    @Test
    void shouldListFeatureActivationStates() {
        when(event.getSubcommandName()).thenReturn("list");

        when(service.enabledFeatures(GUILD_ID))
                .thenReturn(Set.of(PROGRESSION));

        command.execute(event);

        var order = inOrder(event, service);

        order.verify(event).deferReply(true);
        order.verify(service).enabledFeatures(GUILD_ID);

        verify(hook).editOriginal(
                "**Server features**\n"
                        + "- **economy**: Disabled\n"
                        + "- **progression**: Enabled");
    }

    @Test
    void shouldRejectListOutsideGuild() {
        when(event.getSubcommandName()).thenReturn("list");
        when(event.getGuild()).thenReturn(null);

        command.execute(event);

        verify(event).reply(
                "This command can only be used in a server.");

        verifyNoInteractions(service);
    }

    @Test
    void shouldEnableFeature() {
        prepareFeatureChange("enable", PROGRESSION);

        command.execute(event);

        var order = inOrder(event, service);

        order.verify(event).deferReply(true);
        order.verify(service)
                .setEnabled(GUILD_ID, PROGRESSION, true);

        verify(hook).editOriginal(
                "Feature **progression** has been enabled.");
    }

    @Test
    void shouldDisableFeature() {
        prepareFeatureChange("disable", PROGRESSION);

        command.execute(event);

        verify(service)
                .setEnabled(GUILD_ID, PROGRESSION, false);

        verify(hook).editOriginal(
                "Feature **progression** has been disabled.");
    }

    @ParameterizedTest
    @ValueSource(strings = { "enable", "disable" })
    void shouldRejectUnauthorizedFeatureChanges(String subcommand) {
        prepareFeatureChange(subcommand, PROGRESSION);

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
    void shouldRejectMissingFeatureOption() {
        prepareFeatureChange("enable", PROGRESSION);

        when(event.getOption("feature")).thenReturn(null);

        command.execute(event);

        verify(event).reply("A feature must be specified.");

        verifyNoInteractions(service);
        verify(event, never()).deferReply(true);
    }

    @Test
    void shouldRejectUnsupportedFeature() {
        prepareFeatureChange("enable", "unknown");

        command.execute(event);

        verify(event).reply("This feature is not supported.");

        verify(service, never())
                .setEnabled(anyString(), anyString(), anyBoolean());

        verify(event, never()).deferReply(true);
    }

    @Test
    void shouldRejectUnknownSubcommand() {
        when(event.getSubcommandName()).thenReturn("unknown");

        command.execute(event);

        verify(event).reply("Unknown features command.");

        verifyNoInteractions(service);
    }

    @Test
    void shouldHandleFeatureReadFailure() {
        when(event.getSubcommandName()).thenReturn("list");

        when(service.enabledFeatures(GUILD_ID))
                .thenThrow(new IllegalStateException("Database failure"));

        command.execute(event);

        verify(hook).editOriginal(
                "An unexpected error occurred.");
    }

    @Test
    void shouldHandleFeatureWriteFailure() {
        prepareFeatureChange("enable", PROGRESSION);

        doThrow(new IllegalStateException("Database failure"))
                .when(service)
                .setEnabled(GUILD_ID, PROGRESSION, true);

        command.execute(event);

        verify(hook).editOriginal(
                "An unexpected error occurred.");
    }

    private void prepareFeatureChange(
            String subcommand,
            String featureKey) {

        when(event.getSubcommandName()).thenReturn(subcommand);

        when(member.hasPermission(Permission.MANAGE_SERVER))
                .thenReturn(true);

        var option = mock(OptionMapping.class);

        when(option.getAsString()).thenReturn(featureKey);
        when(event.getOption("feature")).thenReturn(option);
    }
}
