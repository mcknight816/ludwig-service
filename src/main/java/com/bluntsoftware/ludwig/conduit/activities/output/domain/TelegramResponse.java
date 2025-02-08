package com.bluntsoftware.ludwig.conduit.activities.output.domain;

import com.bluntsoftware.ludwig.conduit.config.telegram.domain.TelegramConfig;
import com.bluntsoftware.ludwig.conduit.utils.schema.EntitySchema;
import com.bluntsoftware.ludwig.conduit.utils.schema.JsonSchema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class TelegramResponse implements EntitySchema {
    String chatId;
    String text;
    String config;
    @Override
    public JsonSchema getJsonSchema() {
        JsonSchema ret =  JsonSchema.builder().title("Telegram Request").build();
        ret.addString("text","",true);
        ret.addString("chatId","",true);
        ret.addConfigDomain("config", TelegramConfig.class);
        return ret;
    }
}