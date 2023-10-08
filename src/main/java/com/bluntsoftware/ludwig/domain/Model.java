package com.bluntsoftware.ludwig.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document
public class Model {
    @Id
    String id;
    String name;
    String owner;
    String description;
    String packageName;
    Instant createDate;
    Instant updateDate;
    @Singular
    List<Entity> entities;
}
