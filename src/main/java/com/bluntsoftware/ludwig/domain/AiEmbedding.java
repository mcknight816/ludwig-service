package com.bluntsoftware.ludwig.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AiEmbedding {
    @Id
    String id;
    String category;
    String userId;
    String text;
    String description;
    List<Double> vector;
    boolean processed;
}

