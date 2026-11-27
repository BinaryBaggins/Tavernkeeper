package io.github.binarybaggins.tavernkeeper.config;

import java.util.Map;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConfigLoaderTest {

    @Test
    void shouldLoadTokenFromEnvironment() {
        var environment = Map.of(
                "DISCORD_TOKEN", "test-token");

        var config = ConfigLoader.load(environment::get);

        assertEquals("test-token", config.token());
    }

    @Test
    void shouldRejectMissingEnvironmentVariable() {
        assertThrows(
                IllegalArgumentException.class,
                () -> ConfigLoader.load(key -> null));
    }

    @Test
    void shouldRejectBlankEnvironmentVariable() {
        var environment = Map.of(
                "DISCORD_TOKEN", " ");

        assertThrows(
                IllegalArgumentException.class,
                () -> ConfigLoader.load(environment::get));
    }
}
