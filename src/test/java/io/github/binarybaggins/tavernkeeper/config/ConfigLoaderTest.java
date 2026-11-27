package io.github.binarybaggins.tavernkeeper.config;

import java.nio.file.Path;
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
                                NullPointerException.class,
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

        @Test
        void shouldUseDefaultDatabasePath() {
                var environment = Map.of(
                                "DISCORD_TOKEN", "test-token");

                var config = ConfigLoader.load(environment::get);

                assertEquals(
                                Path.of("data/tavernkeeper.db"),
                                config.databasePath());
        }

        @Test
        void shouldLoadCustomDatabasePath() {
                var environment = Map.of(
                                "DISCORD_TOKEN", "test-token",
                                "DATABASE_PATH", "custom/bot.db");

                var config = ConfigLoader.load(environment::get);

                assertEquals(
                                Path.of("custom/bot.db"),
                                config.databasePath());
        }
}
