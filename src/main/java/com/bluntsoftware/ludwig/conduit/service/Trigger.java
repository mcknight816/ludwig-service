package com.bluntsoftware.ludwig.conduit.service;

public interface Trigger<T> {
    void trigger(T data);
    void start();
    void stop();
}
