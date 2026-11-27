
package io.github.binarybaggins.tavernkeeper.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;

class BotConfigTest {

    @Test
    void shouldAcceptValidToken() {
        var config = new BotConfig("test-token", Path.of("test.db"));

        assertEquals("test-token", config.token());
    }

    @ParameterizedTest
    @ValueSource(strings = { "", " ", "\t", "\n" })
    void shouldRejectBlankToken(String token) {
        assertThrows(
                IllegalArgumentException.class,
                () -> new BotConfig(token, Path.of("test.db")));
    }

    @Test
    void shouldRejectMissingToken() {
        assertThrows(
                NullPointerException.class,
                () -> new BotConfig(null, Path.of("test.db")));
    }

    @Test
    void shouldRedactTokenInStringRepresentation() {
        var config = new BotConfig("test-token", Path.of("test.db"));

        assertFalse(config.toString().contains("test-token"));
        assertEquals("BotConfig[token=REDACTED, databasePath=test.db]", config.toString());
    }
}
