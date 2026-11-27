package io.github.binarybaggins.tavernkeeper.discord.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GuildCommandChecksTest {

        private SlashCommandInteractionEvent event;
        private Guild guild;
        private Member member;
        private ReplyCallbackAction replyAction;

        @BeforeEach
        void setUp() {
                event = mock(SlashCommandInteractionEvent.class);
                guild = mock(Guild.class);
                member = mock(Member.class);
                replyAction = mock(ReplyCallbackAction.class);

                when(event.reply(anyString())).thenReturn(replyAction);
                when(replyAction.setEphemeral(true)).thenReturn(replyAction);
        }

        @Test
        void shouldAcceptGuildContext() {
                when(event.getGuild()).thenReturn(guild);

                assertSame(
                                guild,
                                GuildCommandChecks.requireGuild(event).orElseThrow());

                verify(event, never()).reply(anyString());
        }

        @Test
        void shouldRejectDirectMessageContext() {
                when(event.getGuild()).thenReturn(null);

                assertTrue(
                                GuildCommandChecks.requireGuild(event).isEmpty());

                verify(event).reply(
                                "This command can only be used in a server.");

                verify(replyAction).setEphemeral(true);
                verify(replyAction).queue();
        }

        @Test
        void shouldAcceptGuildManager() {
                when(event.getGuild()).thenReturn(guild);
                when(event.getMember()).thenReturn(member);
                when(member.hasPermission(Permission.MANAGE_SERVER))
                                .thenReturn(true);

                assertSame(
                                guild,
                                GuildCommandChecks.requireGuildManager(event)
                                                .orElseThrow());

                verify(event, never()).reply(anyString());
        }

        @Test
        void shouldRejectMemberWithoutPermission() {
                when(event.getGuild()).thenReturn(guild);
                when(event.getMember()).thenReturn(member);
                when(member.hasPermission(Permission.MANAGE_SERVER))
                                .thenReturn(false);

                assertTrue(
                                GuildCommandChecks.requireGuildManager(event)
                                                .isEmpty());

                verify(event).reply(
                                "You need the Manage Server permission "
                                                + "to use this command.");

                verify(replyAction).setEphemeral(true);
                verify(replyAction).queue();
        }

        @Test
        void shouldRejectMissingMember() {
                when(event.getGuild()).thenReturn(guild);
                when(event.getMember()).thenReturn(null);

                assertTrue(
                                GuildCommandChecks.requireGuildManager(event)
                                                .isEmpty());

                verify(replyAction).setEphemeral(true);
                verify(replyAction).queue();
        }

        @Test
        void shouldRejectGuildManagerCheckOutsideGuild() {
                when(event.getGuild()).thenReturn(null);

                assertTrue(
                                GuildCommandChecks.requireGuildManager(event)
                                                .isEmpty());

                verify(event, never()).getMember();

                verify(event).reply(
                                "This command can only be used in a server.");

                verify(replyAction).setEphemeral(true);
                verify(replyAction).queue();
        }
}
