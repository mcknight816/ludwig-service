package com.bluntsoftware.ludwig.conduit.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConfigTestResult {
    @Builder.Default
    boolean success = false;
    @Builder.Default
    boolean error = false;
    @Builder.Default
    boolean warning = false;
    @Builder.Default
    String message = "Test Not Executed";
    @Builder.Default
    String hint = "";
}
