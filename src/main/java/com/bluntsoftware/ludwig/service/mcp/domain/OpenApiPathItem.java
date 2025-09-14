package com.bluntsoftware.ludwig.service.mcp.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OpenApiPathItem {
    private OpenApiOperation get;
    private OpenApiOperation post;
    private OpenApiOperation put;
    private OpenApiOperation delete;
    private OpenApiOperation patch;
    private OpenApiOperation head;
    private OpenApiOperation options;
    private OpenApiOperation trace;
}