package io.github.binarybaggins.tavernkeeper.features.guilds.activation;

import java.util.Set;

/**
 * Repository for managing guild feature activation.
 */
public interface GuildFeatureRepository {

    /**
     * Finds the set of enabled features for the specified guild.
     *
     * @param guildId the ID of the guild, must not be null or blank
     * @return the set of enabled feature keys, never null
     */
    Set<String> findEnabledFeatures(String guildId);

    /**
     * Sets the enabled state for a specific feature in the specified guild.
     *
     * @param guildId    the ID of the guild, must not be null or blank
     * @param featureKey the key of the feature, must not be null or blank
     * @param enabled    true to enable the feature, false to disable it
     */
    void setEnabled(String guildId, String featureKey, boolean enabled);
}
