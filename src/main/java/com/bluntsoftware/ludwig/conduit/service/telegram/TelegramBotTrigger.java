package com.bluntsoftware.ludwig.conduit.service.telegram;

import com.bluntsoftware.ludwig.conduit.config.telegram.domain.TelegramConfig;
import com.bluntsoftware.ludwig.conduit.service.Trigger;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.api.objects.Update;
import java.util.function.Consumer;

@Slf4j
public class TelegramBotTrigger implements Trigger<Update> {

    private final TelegramConfig config;
    private final Consumer<Update> callback;
    private final TelegramBotService telegramBotService;

    public TelegramBotTrigger(TelegramBotService telegramBotService,TelegramConfig config, Consumer<Update> callback) {
        this.config = config;
        this.callback = callback;
        this.telegramBotService = telegramBotService;
    }

    @Override
    public void trigger(Update update) {
        this.callback.accept(update);
    }

    @Override
    public void start() {
        try {
            TelegramBot bot = telegramBotService.getBot(config);
            bot.addTrigger(this);
            log.info("Registered Telegram bot: {}", config.getUsername());
        } catch (TelegramApiException e) {
            log.error("Failed to initialize Telegram bot: {}", config.getUsername(), e);
        }
    }

    @Override
    public void stop() {
        try {
            TelegramBot bot = telegramBotService.getBot(config);
            bot.triggers.remove(this);
            telegramBotService.removeBot(config);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
        log.info("Shutting down Telegram bot: {}", config.getUsername());
    }
}
