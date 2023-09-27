package com.bluntsoftware.ludwig.dto;

import com.bluntsoftware.ludwig.model.Config;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlowConfigDto {
    String id;
    String name;
    Config config;
}