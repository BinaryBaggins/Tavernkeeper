package io.github.binarybaggins.tavernkeeper.features.guilds.activation.persistence;

import java.util.Objects;
import java.util.Set;

import org.jdbi.v3.core.Jdbi;

import io.github.binarybaggins.tavernkeeper.features.guilds.activation.GuildFeatureRepository;

/**
 * JDBI-based implementation of {@link GuildFeatureRepository}.
 */
public final class JdbiGuildFeatureRepository
        implements GuildFeatureRepository {

    private final Jdbi jdbi;

    /**
     * Constructs a new JdbiGuildFeatureRepository with the specified Jdbi instance.
     *
     * @param jdbi the Jdbi instance, must not be null
     */
    public JdbiGuildFeatureRepository(Jdbi jdbi) {
        this.jdbi = Objects.requireNonNull(jdbi);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> findEnabledFeatures(String guildId) {
        return jdbi.withHandle(handle -> Set.copyOf(
                handle.createQuery("""
                        SELECT feature_key
                        FROM guild_features
                        WHERE guild_id = :guildId
                          AND enabled = 1
                        """)
                        .bind("guildId", guildId)
                        .mapTo(String.class)
                        .list()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEnabled(
            String guildId,
            String featureKey,
            boolean enabled) {

        jdbi.useHandle(handle -> handle.createUpdate("""
                INSERT INTO guild_features (
                    guild_id,
                    feature_key,
                    enabled
                )
                VALUES (
                    :guildId,
                    :featureKey,
                    :enabled
                )
                ON CONFLICT(guild_id, feature_key)
                DO UPDATE SET
                    enabled = excluded.enabled
                """)
                .bind("guildId", guildId)
                .bind("featureKey", featureKey)
                .bind("enabled", enabled ? 1 : 0)
                .execute());
    }
}
