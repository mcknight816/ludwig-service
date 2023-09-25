package com.bluntsoftware.ludwig.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.HashMap;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfigDto extends HashMap<String,Object> {
    String id;
    String configClass;
}