package io.github.binarybaggins.tavernkeeper.features.guilds;

import java.time.Instant;

/**
 * Represents a guild that has been registered.
 *
 * @param guildId   the ID of the guild
 * @param createdAt the timestamp when the guild was registered
 */
public record RegisteredGuild(String guildId,
        Instant createdAt) {

}
