package io.github.binarybaggins.tavernkeeper.features.guilds;

import java.util.Optional;

/**
 * Repository interface for managing guilds.
 */
public interface GuildRepository {

    /**
     * Registers a new guild with the given ID.
     *
     * @param guildId the ID of the guild to register
     */
    void register(String guildId);

    /**
     * Finds a registered guild by its ID.
     *
     * @param guildId the ID of the guild to find
     * @return an {@link Optional} containing the registered guild if found, or
     *         empty if not found
     */
    Optional<RegisteredGuild> findById(String guildId);
}
