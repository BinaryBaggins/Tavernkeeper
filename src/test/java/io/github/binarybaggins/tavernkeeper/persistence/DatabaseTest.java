
package io.github.binarybaggins.tavernkeeper.persistence;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseTest {

    @TempDir
    Path tempDir;

    @Test
    void shouldCreateDatabase() throws IOException {
        Path path = tempDir.resolve("data/tavernkeeper.db");

        Database.initialize(path);

        assertTrue(Files.isRegularFile(path));
    }

    @Test
    void shouldApplyMigrations() throws IOException {
        var jdbi = Database.initialize(
                tempDir.resolve("test.db"));

        boolean exists = jdbi.withHandle(handle -> handle.createQuery("""
                SELECT EXISTS (
                    SELECT 1
                    FROM sqlite_master
                    WHERE type = 'table'
                      AND name = 'guilds'
                )
                """)
                .mapTo(boolean.class)
                .one());

        assertTrue(exists);
    }

    @Test
    void shouldEnableForeignKeys() throws IOException {
        var jdbi = Database.initialize(
                tempDir.resolve("test.db"));

        int enabled = jdbi.withHandle(handle -> handle.createQuery("PRAGMA foreign_keys")
                .mapTo(int.class)
                .one());

        assertEquals(1, enabled);
    }

    @Test
    void shouldEnableWalMode() throws IOException {
        var jdbi = Database.initialize(
                tempDir.resolve("test.db"));

        String mode = jdbi.withHandle(handle -> handle.createQuery("PRAGMA journal_mode")
                .mapTo(String.class)
                .one());

        assertEquals("wal", mode);
    }

    @Test
    void shouldAllowRepeatedInitialization() throws IOException {
        Path path = tempDir.resolve("test.db");

        Database.initialize(path);

        assertDoesNotThrow(() -> Database.initialize(path));
    }
}
