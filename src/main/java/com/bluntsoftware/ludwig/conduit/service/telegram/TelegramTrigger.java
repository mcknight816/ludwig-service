package com.bluntsoftware.ludwig.conduit.service.telegram;

import com.bluntsoftware.ludwig.conduit.config.telegram.domain.TelegramConfig;
import com.bluntsoftware.ludwig.conduit.service.Trigger;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Slf4j
public class TelegramTrigger implements Trigger<TelegramConfig> {

    private final TelegramConfig config;
    private TelegramBotsApi telegramBotsApi;
    private TelegramBot bot;

    public TelegramTrigger(TelegramConfig config) {
        this.config = config;
    }

    @Override
    public void start() {
        try {
            // Initialize the TelegramBotsApi with the default session
            telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            bot = new TelegramBot("",config.getToken(), config.getUsername());
            telegramBotsApi.registerBot(bot);
            log.info("Registered Telegram bot: {}", config.getUsername());
        } catch (TelegramApiException e) {
            log.error("Failed to initialize Telegram bot: {}", config.getUsername(), e);
        }
    }

    @Override
    public void stop() {
        // TelegramBotsApi does not provide an unregister method.
        // If you implement custom shutdown logic, do it here.
        log.info("Shutting down Telegram bot: {}", config.getUsername());
    }
}
