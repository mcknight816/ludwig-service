package com.bluntsoftware.ludwig.model;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Field;
import java.util.Map;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FlowActivity {
    @Field("id")
    private String id;
    private Integer x;
    private Integer y;
    private String icon;
    private String category;
    private String name;
    private String description;
    private String context;
    private Boolean hasError;
    private String activityClass;
    Map<String,Object> input;
    Map<String,Object> output;
}