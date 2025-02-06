package com.bluntsoftware.ludwig.conduit.service;

public interface Trigger<T> {
    /**
     * Starts the trigger using the provided configuration.
     */
    void start();

    /**
     * Stops the trigger and cleans up any resources.
     */
    void stop();
}
