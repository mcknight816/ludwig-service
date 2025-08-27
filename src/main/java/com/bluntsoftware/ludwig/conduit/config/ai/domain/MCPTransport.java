package com.bluntsoftware.ludwig.conduit.config.ai.domain;

import com.fasterxml.jackson.annotation.JsonValue;

public enum MCPTransport {

    STDIO("stdio");

    private final String name;

    MCPTransport(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public static MCPTransport fromString(String name) {
        for (MCPTransport transport : MCPTransport.values()) {
            if (transport.name.equalsIgnoreCase(name)) {
                return transport;
            }
        }
        throw new IllegalArgumentException("Unknown Mcp Transport: " + name);
    }

    @JsonValue
    public String getValue() {
        return name;
    }

}
