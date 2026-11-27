
package io.github.binarybaggins.tavernkeeper.persistence;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.flywaydb.core.Flyway;
import org.jdbi.v3.core.Jdbi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteDataSource;

/**
 * Initialises and configures the application database.
 */
public final class Database {

    private static final Logger LOGGER = LoggerFactory.getLogger(Database.class);

    private Database() {
    }

    /**
     * Initializes the database at the specified path.
     *
     * @param databasePath the path to the database file
     * @return a Jdbi instance connected to the database
     * @throws IOException if an I/O error occurs
     */
    public static Jdbi initialize(Path databasePath) throws IOException {
        Path path = databasePath.toAbsolutePath().normalize();

        Files.createDirectories(path.getParent());

        LOGGER.info("Initialising database at {}.", path);

        var config = new SQLiteConfig();

        config.enforceForeignKeys(true);
        config.setJournalMode(SQLiteConfig.JournalMode.WAL);
        config.setBusyTimeout(5000);

        var dataSource = new SQLiteDataSource(config);
        dataSource.setUrl("jdbc:sqlite:" + path);

        Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration")
                .load()
                .migrate();

        LOGGER.info("Database initialised successfully.");

        return Jdbi.create(dataSource);
    }
}
