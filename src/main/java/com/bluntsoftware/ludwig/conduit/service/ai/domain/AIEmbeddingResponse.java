package com.bluntsoftware.ludwig.conduit.service.ai.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class AIEmbeddingResponse {
    List<AIEmbeddingData> data;
    String model;
    String object;
    Map<String,Object> usage;
}
