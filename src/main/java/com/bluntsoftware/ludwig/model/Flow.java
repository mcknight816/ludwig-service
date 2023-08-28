package com.bluntsoftware.ludwig.model;


import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS,property = "@class")
public class Flow {
    @Id
    private String id;
    private String name;
    private Boolean locked = false;
    private List<FlowActivity> activities;
    private List<Connection> connections;
    private List<ConnectionMap> connectionMaps;
}
