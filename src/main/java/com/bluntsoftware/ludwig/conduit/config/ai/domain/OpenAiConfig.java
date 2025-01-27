package com.bluntsoftware.ludwig.conduit.config.ai.domain;

import com.bluntsoftware.ludwig.conduit.utils.schema.EntitySchema;
import com.bluntsoftware.ludwig.conduit.utils.schema.JsonSchema;
import com.bluntsoftware.ludwig.conduit.utils.schema.PropertyFormat;
import com.bluntsoftware.ludwig.conduit.utils.schema.StringProperty;
import com.bluntsoftware.ludwig.conduit.service.ai.domain.OpenAiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OpenAiConfig implements EntitySchema {

    private final static String DEFAULT_QUESTION = "Who was the president of the United States in May of 1845 ?";

    String secret;
    @Builder.Default
    String testQuestion = DEFAULT_QUESTION;
    @Builder.Default
    String model =  OpenAiModel.GPT_4_MINI.toString();
    @Builder.Default
    boolean store = true;
    @Builder.Default
    int temperature = 0;
    @Builder.Default
    int max_tokens = 1024;

    public JsonSchema getJsonSchema() {
        JsonSchema openApiSchema = JsonSchema.builder().title("open-ai").build();
        openApiSchema.addEnum("model","model", Arrays.stream(OpenAiModel.values()).map(OpenAiModel::toString).collect(Collectors.toList()), OpenAiModel.GPT_4_MINI.toString() );
        openApiSchema.addString("secret", StringProperty.builder().format(PropertyFormat.PASSWORD).build());
        openApiSchema.addString("testQuestion",DEFAULT_QUESTION);
        openApiSchema.addNumber("max_tokens",1024);
        openApiSchema.addNumber("temperature",0);
        openApiSchema.addBoolean("store",true);
        return openApiSchema;
    }
}
