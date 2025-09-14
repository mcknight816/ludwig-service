package com.bluntsoftware.ludwig.service.mcp.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OpenApiServer {
    private String url;
    private String description;
}