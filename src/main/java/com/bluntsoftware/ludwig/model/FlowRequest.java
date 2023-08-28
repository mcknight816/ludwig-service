package com.bluntsoftware.ludwig.model;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Document("flow_request")
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS,property = "@class")
public class FlowRequest extends HashMap<String,Object> {
    @Id
    String _id;
}
