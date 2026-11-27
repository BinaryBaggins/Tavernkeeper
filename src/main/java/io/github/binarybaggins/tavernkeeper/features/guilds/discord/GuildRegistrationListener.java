
package io.github.binarybaggins.tavernkeeper.features.guilds.discord;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.binarybaggins.tavernkeeper.features.guilds.GuildRepository;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildAvailableEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * A listener for registering guilds with the repository when they become
 * available.
 */
public final class GuildRegistrationListener extends ListenerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(GuildRegistrationListener.class);

    private final GuildRepository repository;

    /**
     * Constructs a new {@link GuildRegistrationListener} with the given
     * {@link GuildRepository}.
     *
     * @param repository the guild repository to use for registration
     */
    public GuildRegistrationListener(GuildRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    /**
     * Handles the {@link GuildJoinEvent} when the bot joins a new guild.
     *
     * @param event the guild join event
     */
    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        registerGuild(event.getGuild());
    }

    /**
     * Handles the {@link GuildAvailableEvent} when a guild becomes available.
     *
     * @param event the guild available event
     */
    @Override
    public void onGuildAvailable(GuildAvailableEvent event) {
        registerGuild(event.getGuild());
    }

    private void registerGuild(Guild guild) {
        try {
            repository.register(guild.getId());

            LOGGER.info("Ensured registration of guild '{}' ({}).",
                    guild.getName(), guild.getId());
        } catch (RuntimeException e) {
            LOGGER.error("Failed to register guild {}.",
                    guild.getId(), e);
        }
    }
}
