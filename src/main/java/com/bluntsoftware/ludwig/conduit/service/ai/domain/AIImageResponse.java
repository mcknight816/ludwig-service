package com.bluntsoftware.ludwig.conduit.service.ai.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class AIImageResponse {
    Integer created;
    List<AIImageUrl> data;
}
