package io.github.binarybaggins.tavernkeeper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.binarybaggins.tavernkeeper.config.BotConfig;
import io.github.binarybaggins.tavernkeeper.config.ConfigLoader;
import io.github.binarybaggins.tavernkeeper.discord.DiscordBot;
import io.github.binarybaggins.tavernkeeper.discord.commands.CommandRegistry;
import io.github.binarybaggins.tavernkeeper.discord.commands.PingCommand;

public class App {
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    private App() {
    }

    public static void main(String[] args) {
        LOGGER.info("Starting Tavernkeeper...");

        final BotConfig config;

        try {
            config = ConfigLoader.load();
        } catch (IllegalArgumentException e) {
            LOGGER.error("Invalid configuration: {}", e.getMessage());
            System.exit(1);
            return;
        }

        LOGGER.info("Configuration loaded successfully.");

        var registry = new CommandRegistry();
        registry.register(new PingCommand());

        final DiscordBot bot;

        try {
            bot = new DiscordBot(config, registry);
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
        }
    }
}
