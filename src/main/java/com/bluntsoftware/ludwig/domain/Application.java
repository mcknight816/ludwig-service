package com.bluntsoftware.ludwig.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document
public class Application {
    @Id
    private String id;
    private String name;
    private String description;
    private String path;
    private Instant created;
    private Instant modified;
    private String jwkUri;
    private List<Flow> flows;
}
