package com.bluntsoftware.ludwig.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlowConfigDto {
    String id;
    String name;
    String configClass;
    Map<String,Object> config;
}