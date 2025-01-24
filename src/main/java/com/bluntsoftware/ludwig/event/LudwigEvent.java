package com.bluntsoftware.ludwig.event;

import org.springframework.context.ApplicationEvent;

public class LudwigEvent extends ApplicationEvent {

    public LudwigEvent(EventData source) {
        super(source);
    }

    public EventData getEventData() {
        return (EventData) super.getSource();
    }
}
