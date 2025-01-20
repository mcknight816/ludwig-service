package com.bluntsoftware.ludwig.conduit.service.ai.domain;


import com.fasterxml.jackson.annotation.JsonValue;

public enum OpenAiModel {
    // GPT-4 Aliases
    GPT_4("gpt-4"),
    GPT_4_TURBO("gpt-4-turbo"),
    GPT_4_VISION("gpt-4-vision"),
    GPT_4_MINI("gpt-4o-mini"),

    // O1 Aliases
    O1("o1"),
    O1_PREVIEW("o1-preview"),
    O1_MINI("o1-mini"),

    // GPT-3.5 Alias
    GPT_3_5_TURBO("gpt-3.5-turbo"),

    // Whisper Alias (Speech-to-Text)
    WHISPER("whisper-1"),

    // DALL·E Aliases (Image Generation)
    DALL_E_2("dall-e-2"),
    DALL_E_3("dall-e-3"),

    // Embedding Alias
    EMBEDDING_ADA("text-embedding-ada-002"),

    // Moderation Aliases
    MODERATION_LATEST("text-moderation-latest"),
    MODERATION_STABLE("text-moderation-stable");

    private final String aliasName;

    OpenAiModel(String aliasName) {
        this.aliasName = aliasName;
    }

    @Override
    public String toString() {
        return aliasName;
    }

    public static OpenAiModel fromString(String aliasName) {
        for (OpenAiModel model : OpenAiModel.values()) {
            if (model.aliasName.equalsIgnoreCase(aliasName)) {
                return model;
            }
        }
        throw new IllegalArgumentException("Unknown OpenAI alias model: " + aliasName);
    }

    @JsonValue
    public String getValue() {
        return aliasName;
    }
}