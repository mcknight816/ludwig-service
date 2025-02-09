package com.bluntsoftware.ludwig.conduit.service.ai.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class AIEmbeddingRequest {
    String input;
    @Builder.Default
    String model = "text-embedding-ada-002";
}
