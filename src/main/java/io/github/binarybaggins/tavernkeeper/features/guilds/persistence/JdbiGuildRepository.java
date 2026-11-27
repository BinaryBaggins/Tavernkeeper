
package io.github.binarybaggins.tavernkeeper.features.guilds.persistence;

import java.time.Instant;
import java.util.Optional;
import java.util.Objects;

import org.jdbi.v3.core.Jdbi;

import io.github.binarybaggins.tavernkeeper.features.guilds.RegisteredGuild;
import io.github.binarybaggins.tavernkeeper.features.guilds.GuildRepository;

/**
 * Jdbi implementation of the {@link GuildRepository} interface.
 */
public final class JdbiGuildRepository implements GuildRepository {

    private final Jdbi jdbi;

    /**
     * Constructs a new {@link JdbiGuildRepository} with the given {@link Jdbi}
     * instance.
     *
     * @param jdbi the Jdbi instance to use for database operations
     */
    public JdbiGuildRepository(Jdbi jdbi) {
        this.jdbi = Objects.requireNonNull(jdbi);
    }

    /**
     * Registers a new guild with the given ID.
     *
     * @param guildId the ID of the guild to register
     */
    @Override
    public void register(String guildId) {
        jdbi.useHandle(handle -> handle.createUpdate("""
                INSERT INTO guilds (guild_id)
                VALUES (:guildId)
                ON CONFLICT(guild_id) DO NOTHING
                """)
                .bind("guildId", guildId)
                .execute());
    }

    /**
     * Finds a registered guild by its ID.
     *
     * @param guildId the ID of the guild to find
     * @return an {@link Optional} containing the registered guild if found, or
     *         empty if not found
     */
    @Override
    public Optional<RegisteredGuild> findById(String guildId) {
        return jdbi.withHandle(handle -> handle.createQuery("""
                SELECT guild_id,
                       strftime('%Y-%m-%dT%H:%M:%SZ', created_at)
                           AS created_at
                FROM guilds
                WHERE guild_id = :guildId
                """)
                .bind("guildId", guildId)
                .map((rs, ctx) -> new RegisteredGuild(
                        rs.getString("guild_id"),
                        Instant.parse(rs.getString("created_at"))))
                .findOne());
    }
}
