package io.github.binarybaggins.tavernkeeper.features.guilds.activation;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Service for managing guild features.
 */
public final class GuildFeatureService {

    private final GuildFeatureRepository repository;
    private final Set<String> supportedFeatures;

    private final ConcurrentMap<String, Set<String>> cache = new ConcurrentHashMap<>();

    /**
     * Constructs a new GuildFeatureService.
     *
     * @param repository        the repository for persisting guild feature states,
     *                          must not be null
     * @param supportedFeatures the set of supported feature keys, must not be null
     */
    public GuildFeatureService(
            GuildFeatureRepository repository,
            Set<String> supportedFeatures) {

        this.repository = Objects.requireNonNull(repository);
        this.supportedFeatures = Set.copyOf(supportedFeatures);
    }

    /**
     * Returns the set of supported feature keys.
     *
     * @return the set of supported feature keys, never null
     */
    public Set<String> supportedFeatures() {
        return supportedFeatures;
    }

    /**
     * Returns the set of enabled feature keys for the specified guild.
     *
     * @param guildId the ID of the guild, must not be null
     * @return the set of enabled feature keys, never null
     */
    public Set<String> enabledFeatures(String guildId) {
        return cache.computeIfAbsent(
                guildId,
                this::loadEnabledFeatures);
    }

    /**
     * Checks if a specific feature is enabled for the specified guild.
     *
     * @param guildId    the ID of the guild, must not be null
     * @param featureKey the feature key, must be supported
     * @return true if the feature is enabled, false otherwise
     */
    public boolean isEnabled(String guildId, String featureKey) {
        requireSupportedFeature(featureKey);

        return enabledFeatures(guildId).contains(featureKey);
    }

    /**
     * Sets the enabled state of a specific feature for the specified guild.
     *
     * @param guildId    the ID of the guild, must not be null
     * @param featureKey the feature key, must be supported
     * @param enabled    true to enable the feature, false to disable it
     */
    public void setEnabled(
            String guildId,
            String featureKey,
            boolean enabled) {

        requireSupportedFeature(featureKey);

        cache.compute(guildId, (id, current) -> {
            var updated = new HashSet<>(
                    current != null
                            ? current
                            : loadEnabledFeatures(id));

            if (enabled) {
                updated.add(featureKey);
            } else {
                updated.remove(featureKey);
            }

            var snapshot = Set.copyOf(updated);

            repository.setEnabled(id, featureKey, enabled);

            return snapshot;
        });
    }

    private Set<String> loadEnabledFeatures(String guildId) {
        var enabled = new HashSet<>(
                repository.findEnabledFeatures(guildId));

        enabled.retainAll(supportedFeatures);

        return Set.copyOf(enabled);
    }

    private void requireSupportedFeature(String featureKey) {
        Objects.requireNonNull(featureKey);

        if (!supportedFeatures.contains(featureKey)) {
            throw new IllegalArgumentException(
                    "Unsupported feature: " + featureKey);
        }
    }
}
