package com.bluntsoftware.ludwig.model;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Document("config")
public class FlowConfig {
    @Id
    String id;
    String name;
    String configClass;
    Map<String,Object> config;
}
