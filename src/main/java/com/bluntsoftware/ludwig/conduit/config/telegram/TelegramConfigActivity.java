package com.bluntsoftware.ludwig.conduit.config.telegram;

import com.bluntsoftware.ludwig.conduit.config.ActivityConfigImpl;
import com.bluntsoftware.ludwig.conduit.config.ConfigTestResult;
import com.bluntsoftware.ludwig.conduit.config.telegram.domain.TelegramConfig;
import com.bluntsoftware.ludwig.conduit.service.telegram.TelegramBot;
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

    @Override
    public ConfigTestResult testConfig(TelegramConfig config) {
        ConfigTestResult ret =  ConfigTestResult.builder()
                .error(true)
                .message("Telegram Failed")
                .hint("Make sure you have a valid token").build();
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            TelegramBot bot = TelegramBot.builder()
                    .botToken(config.getToken())
                    .botUsername(config.getUsername())
                    .tenantId(TenantResolver.resolve())
                    .build();

            BotSession session = botsApi.registerBot(bot);
            log.info("Telegram Bot Registered and running {}", session.toString());

            return ConfigTestResult.builder()
                    .error(false)
                    .success(true)
                    .message("Telegram Bot Registered and running to use your bot search for t.me/" + config.getUsername())
                    .hint(session.toString()).build();

        } catch (TelegramApiException e) {
            ret.setHint(e.getMessage());
        }
        return ret;
    }
}
