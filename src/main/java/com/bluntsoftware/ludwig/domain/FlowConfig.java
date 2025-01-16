package com.bluntsoftware.ludwig.domain;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public <T> T getConfigAs(Class<T> clazz) throws IllegalArgumentException {
        try {
            OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return OBJECT_MAPPER.convertValue(this.config, clazz);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(
                    "Failed to convert config map to " + clazz.getSimpleName(), ex
            );
        }
    }

}
