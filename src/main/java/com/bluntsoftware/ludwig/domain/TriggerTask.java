package com.bluntsoftware.ludwig.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TriggerTask {
    String name;
    String appId;
    String tenantId;
    String activityClassId;
    String flowId;
    String flowActivityId;
    Map<String,Object> input;
    boolean active;
}
