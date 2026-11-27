
package io.github.binarybaggins.tavernkeeper.discord.commands;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PingCommandTest {

    @Test
    void shouldProvideCorrectCommandName() {
        var definition = new PingCommand().definition();

        assertEquals("ping", definition.getName());
    }

    @Test
    void shouldProvideCommandDescription() {
        var definition = new PingCommand().definition();

        assertFalse(definition.getDescription().isBlank());
    }

    @Test
    void shouldNotRequireArguments() {
        var definition = new PingCommand().definition();

        assertTrue(definition.getOptions().isEmpty());
    }
}
