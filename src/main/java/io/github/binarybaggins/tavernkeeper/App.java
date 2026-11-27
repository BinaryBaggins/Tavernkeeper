package io.github.binarybaggins.tavernkeeper;

import java.io.IOException;

import org.jdbi.v3.core.Jdbi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.binarybaggins.tavernkeeper.config.BotConfig;
import io.github.binarybaggins.tavernkeeper.config.ConfigLoader;
import io.github.binarybaggins.tavernkeeper.discord.DiscordBot;
import io.github.binarybaggins.tavernkeeper.discord.commands.CommandRegistry;
import io.github.binarybaggins.tavernkeeper.discord.commands.PingCommand;
import io.github.binarybaggins.tavernkeeper.features.guilds.GuildRepository;
import io.github.binarybaggins.tavernkeeper.features.guilds.persistence.JdbiGuildRepository;
import io.github.binarybaggins.tavernkeeper.persistence.Database;

public final class App {
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    private App() {
    }

    public static void main(String[] args) {
        LOGGER.info("Starting Tavernkeeper...");

        final BotConfig config;

        try {
            config = ConfigLoader.load();
        } catch (IllegalArgumentException | NullPointerException e) {
            LOGGER.error("Invalid configuration: {}", e.getMessage());
            System.exit(1);
            return;
        }

        LOGGER.info("Configuration loaded successfully.");

        final Jdbi jdbi;

        try {
            jdbi = Database.initialize(config.databasePath());
        } catch (IOException | RuntimeException e) {
            LOGGER.error("Failed to initialize database.", e);
            System.exit(1);
            return;
        }

        GuildRepository guildRepository = new JdbiGuildRepository(jdbi);

        var registry = new CommandRegistry();
        registry.register(new PingCommand());

        final DiscordBot bot;

        try {
            bot = new DiscordBot(config, registry, guildRepository);
        } catch (RuntimeException e) {
            LOGGER.error("Failed to initialize Discord bot: {}", e.getClass().getSimpleName());
            System.exit(1);
            return;
        }

        Runtime.getRuntime().addShutdownHook(new Thread(bot::close, "tavernkeeper-shutdown"));

        try {
            bot.awaitReady();
            LOGGER.info("Discord bot is ready.");
            bot.awaitShutdown();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.exit(1);
        } catch (RuntimeException e) {
            LOGGER.error("Application encountered an error.", e);
            System.exit(1);
        }
    }
}
