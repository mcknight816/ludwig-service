package com.bluntsoftware.ludwig.conduit.activities.output;

import com.bluntsoftware.ludwig.conduit.activities.ActivityImpl;
import com.bluntsoftware.ludwig.conduit.activities.output.domain.TelegramResponse;
import com.bluntsoftware.ludwig.conduit.config.telegram.domain.TelegramConfig;
import com.bluntsoftware.ludwig.conduit.utils.schema.JsonSchema;
import com.bluntsoftware.ludwig.repository.ActivityConfigRepository;
import com.bluntsoftware.ludwig.conduit.service.telegram.TelegramBot;
import com.bluntsoftware.ludwig.conduit.service.telegram.TelegramBotService;

import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
/**
 * TelegramResponseActivity is an implementation of ActivityImpl that integrates with Telegram.
 * This class is responsible for sending a message through a Telegram bot using the given input parameters
 * and external configuration.
 *
 * Dependencies:
 * - ActivityConfigRepository: Retrieves necessary configurations for the activity.
 * - TelegramBotService: Provides access to TelegramBot instances.
 *
 * Functionality:
 * - Processes the provided input map to extract and convert it into a TelegramResponse object.
 * - Retrieves the Telegram configuration based on the provided configuration name.
 * - Sends a text message to a specified chat ID using the configured Telegram bot.
 */
@Service
public class TelegramResponseActivity extends ActivityImpl {

    private final TelegramBotService telegramBotService;

    public TelegramResponseActivity(ActivityConfigRepository activityConfigRepository, TelegramBotService telegramBotService) {
        super(activityConfigRepository);
        this.telegramBotService = telegramBotService;
    }

    @Override
    public Map<String, Object> run(Map<String, Object> input) throws Exception {
        TelegramResponse telegramResponse = convertValue(input, TelegramResponse.class);
        TelegramConfig telegramConfig  = getExternalConfigByName(telegramResponse.getConfig(), TelegramConfig.class);
        TelegramBot bot = telegramBotService.getBot(telegramConfig);
        bot.sendMessage(telegramResponse.getChatId(), telegramResponse.getText());
        return Collections.emptyMap();
    }

    @Override
    public JsonSchema getJsonSchema() {
        return TelegramResponse.builder().build().getJsonSchema();
    }
}
