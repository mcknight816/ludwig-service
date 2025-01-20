package com.bluntsoftware.ludwig.conduit.service.ai.domain;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class AICompletionRequest {
    String model = "gpt-4o-mini";
    @Singular
    List<AIMessage> messages;
    boolean store;
    int temperature = 0;
    int max_tokens = 1024;
    double top_p = 1.0;;
    double frequency_penalty = 0.0;
    double presence_penalty = 0.0;
}
