
package io.github.binarybaggins.tavernkeeper.discord;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.binarybaggins.tavernkeeper.config.BotConfig;
import io.github.binarybaggins.tavernkeeper.discord.commands.CommandListener;
import io.github.binarybaggins.tavernkeeper.discord.commands.CommandRegistry;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

/**
 * A Discord bot that manages the connection to the Discord API and handles
 * slash commands.
 */
public final class DiscordBot implements AutoCloseable {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiscordBot.class);

    private final CommandRegistry registry;
    private final JDA jda;

    /**
     * Creates a new Discord bot instance.
     *
     * @param config   the bot configuration
     * @param registry the command registry
     */
    public DiscordBot(BotConfig config, CommandRegistry registry) {
        this.registry = registry;
        this.jda = JDABuilder.createLight(config.token())
                .setEnableShutdownHook(false)
                .addEventListeners(new CommandListener(registry))
                .build();
    }

    /**
     * Waits for the bot to be fully connected and ready.
     *
     * @throws InterruptedException if the current thread is interrupted while
     *                              waiting
     */
    public void awaitReady() throws InterruptedException {
        jda.awaitReady();

        LOGGER.info("Connected to Discord as {}.",
                jda.getSelfUser().getName());

        registry.synchronize(jda);
    }

    /**
     * Waits for the bot to be fully shut down.
     *
     * @throws InterruptedException if the current thread is interrupted while
     *                              waiting
     */
    public void awaitShutdown() throws InterruptedException {
        jda.awaitShutdown();
    }

    /**
     * Closes the Discord bot, initiating a graceful shutdown.
     */
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
