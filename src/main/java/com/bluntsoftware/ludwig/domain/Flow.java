package com.bluntsoftware.ludwig.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document
public class Flow {
    @Id
    private String id;
    private String name;
    private String path;
    private Boolean locked = false;
    private List<FlowActivity> activities;
    private List<Connection> connections;
    private List<ConnectionMap> connectionMaps;
}
