
package io.github.binarybaggins.tavernkeeper.discord;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.binarybaggins.tavernkeeper.config.BotConfig;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

public final class DiscordBot implements AutoCloseable {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiscordBot.class);

    private final JDA jda;

    public DiscordBot(BotConfig config) {
        this.jda = JDABuilder.createLight(config.token())
                .setEnableShutdownHook(false)
                .build();
    }

    public void awaitReady() throws InterruptedException {
        jda.awaitReady();

        LOGGER.info("Connected to Discord as {}.",
                jda.getSelfUser().getName());
    }

    public void awaitShutdown() throws InterruptedException {
        jda.awaitShutdown();
    }

    @Override
    public void close() {
        LOGGER.info("Shutting down Discord connection...");

        jda.shutdown();

        try {
            if (!jda.awaitShutdown(Duration.ofSeconds(10))) {
                LOGGER.warn("Graceful shutdown timed out.");
                jda.shutdownNow();
            }
        } catch (InterruptedException e) {
            jda.shutdownNow();
            Thread.currentThread().interrupt();
        }

        LOGGER.info("Discord shutdown procedure completed.");
    }
}
