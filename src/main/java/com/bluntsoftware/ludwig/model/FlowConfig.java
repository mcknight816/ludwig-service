package com.bluntsoftware.ludwig.model;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Document("config")
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS,property = "@class")
public class FlowConfig {
    @Id
    String id;
    String name;
    Config config;
}
