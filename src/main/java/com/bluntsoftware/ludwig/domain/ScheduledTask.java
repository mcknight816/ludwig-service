package com.bluntsoftware.ludwig.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScheduledTask {
    String name;
    String tenantId;
    String flowId;
    String flowActivityId;
    String cronExpression;
    boolean active;

}
