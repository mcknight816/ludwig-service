package com.bluntsoftware.ludwig.conduit.service.telegram;

import com.bluntsoftware.ludwig.conduit.config.telegram.domain.TelegramConfig;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.BotSession;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TelegramBotService {

    Map<String,TelegramBot> bots = new ConcurrentHashMap<>();
    Map<String, BotSession> botSessions = new ConcurrentHashMap<>();
    TelegramBotsApi telegramBotsApi;

    TelegramBotService() throws TelegramApiException {
        this.telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
    }

    public void removeBot(TelegramConfig telegramConfig){
        if(bots.containsKey(telegramConfig.getUsername()) && botSessions.containsKey(telegramConfig.getUsername())){
            botSessions.get(telegramConfig.getUsername()).stop();
            bots.remove(telegramConfig.getUsername());
        }
    }

    public TelegramBot getBot(TelegramConfig telegramConfig) throws TelegramApiException {
        if(!bots.containsKey(telegramConfig.getUsername())){
            TelegramBot bot = TelegramBot.builder()
                    .botToken(telegramConfig.getToken())
                    .botUsername(telegramConfig.getUsername())
                    .tenantId(telegramConfig.getTenantId())
                    .build();
            bots.put(telegramConfig.getUsername(),bot);
            botSessions.put(telegramConfig.getUsername(),telegramBotsApi.registerBot(bot));
        }
        return  bots.get(telegramConfig.getUsername());
    }
}
