package com.bluntsoftware.ludwig.conduit.service;

import com.bluntsoftware.ludwig.domain.TriggerTask;

public interface Trigger<T> {
    void trigger(T data);
    void start();
    void stop();
    Boolean triggerTaskChanged(TriggerTask task);

}
