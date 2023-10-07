package com.bluntsoftware.ludwig.ai.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class AICompletion {
    String model = "text-davinci-003";
    String prompt;
    int temperature = 0;
    int max_tokens = 1024;
    double top_p = 1.0;;
    double frequency_penalty = 0.0;
    double presence_penalty = 0.0;
}
