package com.bluntsoftware.ludwig.conduit.service.telegram;

import com.bluntsoftware.ludwig.conduit.service.Trigger;
import lombok.*;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {

    @Builder.Default
    List<Trigger<Update>> triggers = new ArrayList<>();
    private final String botUsername;
    private final String botToken;
    private final String tenantId;

    void addTrigger(Trigger<Update> trigger){
        if(!triggers.contains(trigger)){
            triggers.add(trigger);
        };
    }

    public void sendMessage(String chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);

        try {
            execute(message); // Sending the message
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        triggers.forEach(trigger -> trigger.trigger(update));
    }
}

