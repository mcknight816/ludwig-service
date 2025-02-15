package com.bluntsoftware.ludwig.conduit.service.ai.domain;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class AICompletionRequest {
    @Builder.Default
    String model = "gpt-4o-mini";
    @Singular
    List<AIMessage> messages;
    @Builder.Default
    boolean store = false;
    @Builder.Default
    int temperature = 0;
    @Builder.Default
    int max_tokens = 1024;
    @Builder.Default
    double top_p = 1.0;
    @Builder.Default
    double frequency_penalty = 0.0;
    @Builder.Default
    double presence_penalty = 0.0;
}
