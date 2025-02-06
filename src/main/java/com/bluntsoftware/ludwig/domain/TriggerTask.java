package com.bluntsoftware.ludwig.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    boolean active;
}
