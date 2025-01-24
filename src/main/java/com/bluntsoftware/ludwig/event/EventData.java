package com.bluntsoftware.ludwig.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventData {
    EventAction eventAction;
    EventSubject eventSubject;
    String tenantId;
    String subjectId;
    String eventMessage;
}
