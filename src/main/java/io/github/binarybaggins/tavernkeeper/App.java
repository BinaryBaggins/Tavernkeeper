package io.github.binarybaggins.tavernkeeper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.binarybaggins.tavernkeeper.config.BotConfig;
import io.github.binarybaggins.tavernkeeper.config.ConfigLoader;

public class App {
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

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
    }
}
