
package io.github.binarybaggins.tavernkeeper.discord.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommandRegistryTest {

    private CommandRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new CommandRegistry();
    }

    @Test
    void shouldRegisterCommand() {
        var command = new PingCommand();

        registry.register(command);

        assertSame(command, registry.find("ping").orElseThrow());
    }

    @Test
    void shouldReturnEmptyForUnknownCommand() {
        assertTrue(registry.find("unknown").isEmpty());
    }

    @Test
    void shouldRejectDuplicateCommand() {
        registry.register(new PingCommand());

        assertThrows(
                IllegalArgumentException.class,
                () -> registry.register(new PingCommand()));
    }

    @Test
    void shouldPreserveOriginalCommandAfterDuplicateRegistration() {
        var original = new PingCommand();

        registry.register(original);

        assertThrows(
                IllegalArgumentException.class,
                () -> registry.register(new PingCommand()));

        assertSame(original, registry.find("ping").orElseThrow());
    }
}
