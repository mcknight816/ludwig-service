package com.bluntsoftware.ludwig.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ToolSelection {
    private String tool;
    private Map<String, Object> args;

    public ToolSelection() {
        this.tool = "none";
        this.args = new HashMap<>();
    }
}
