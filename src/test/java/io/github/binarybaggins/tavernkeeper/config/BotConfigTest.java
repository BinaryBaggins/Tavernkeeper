
package io.github.binarybaggins.tavernkeeper.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class BotConfigTest {

    @Test
    void shouldAcceptValidToken() {
        var config = new BotConfig("test-token");

        assertEquals("test-token", config.token());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { " ", "\t", "\n" })
    void shouldRejectMissingToken(String token) {
        assertThrows(
                IllegalArgumentException.class,
                () -> new BotConfig(token));
    }

    @Test
    void shouldRedactTokenInStringRepresentation() {
        var config = new BotConfig("test-token");

        assertFalse(config.toString().contains("test-token"));
        assertEquals("BotConfig[token=REDACTED]", config.toString());
    }
}
