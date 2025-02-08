package com.bluntsoftware.ludwig.conduit.config.telegram;

import com.bluntsoftware.ludwig.conduit.config.ActivityConfigImpl;
import com.bluntsoftware.ludwig.conduit.config.ConfigTestResult;
import com.bluntsoftware.ludwig.conduit.config.telegram.domain.TelegramConfig;
import com.bluntsoftware.ludwig.service.telegram.TelegramBot;
import com.bluntsoftware.ludwig.service.telegram.TelegramBotService;
import com.bluntsoftware.ludwig.tenant.TenantResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.BotSession;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
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
