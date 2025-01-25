package com.bluntsoftware.ludwig.conduit.config.telegram.domain;

import com.bluntsoftware.ludwig.conduit.utils.schema.EntitySchema;
import com.bluntsoftware.ludwig.conduit.utils.schema.JsonSchema;
import com.bluntsoftware.ludwig.conduit.utils.schema.PropertyFormat;
import com.bluntsoftware.ludwig.conduit.utils.schema.StringProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TelegramConfig implements EntitySchema {

    String testMessage;
    String token;
    String username;

    public static JsonSchema getSchema() {
        JsonSchema telegramSchema = JsonSchema.builder().title("telegram").build();

        telegramSchema.addString("testMessage","Hello , is there anyone out there ?");
        telegramSchema.addString("token", StringProperty.builder().defaultValue("YOUR_TELEGRAM_TOKEN").format(PropertyFormat.PASSWORD).build());
        telegramSchema.addString("username", "YOUR_USERNAME");
        return telegramSchema;
    }
}
