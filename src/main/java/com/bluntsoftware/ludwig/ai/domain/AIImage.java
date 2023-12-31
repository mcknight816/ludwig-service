package com.bluntsoftware.ludwig.ai.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class AIImage {
    String prompt;
    int n = 1;
    String size = "1024x1024";
}
