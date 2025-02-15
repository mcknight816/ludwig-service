package com.bluntsoftware.ludwig.conduit.config.telegram;

import com.bluntsoftware.ludwig.conduit.config.ActivityConfigImpl;
import com.bluntsoftware.ludwig.conduit.config.ConfigTestResult;
import com.bluntsoftware.ludwig.conduit.config.telegram.domain.TelegramConfig;
import com.bluntsoftware.ludwig.conduit.service.telegram.TelegramBotService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * The TelegramConfigActivity class extends ActivityConfigImpl and provides functionality for configuring
 * Telegram bots within an application. It utilizes the TelegramBotService to register and validate
 * the provided Telegram configuration.
 *
 * This class is responsible for testing the provided TelegramConfig by validating the token and username.
 * It attempts to register the bot and returns the result of the test.
 */
@Slf4j
@Service
public class TelegramConfigActivity extends ActivityConfigImpl<TelegramConfig> {

    private final TelegramBotService telegramBotService;

    public TelegramConfigActivity(TelegramBotService telegramBotService) {
        this.telegramBotService = telegramBotService;
    }

    @Override
    public ConfigTestResult testConfig(TelegramConfig config) {
        ConfigTestResult ret =  ConfigTestResult.builder()
                .error(true)
                .message("Telegram Failed")
                .hint("Make sure you have a valid token").build();
        try {

            telegramBotService.getBot(config);

            return ConfigTestResult.builder()
                    .error(false)
                    .success(true)
                    .message("Telegram Bot Registered and running to use your bot search for t.me/" + config.getUsername())
                    .build();

        } catch (TelegramApiException e) {
            ret.setHint(e.getMessage());
        }
        return ret;
    }
}
