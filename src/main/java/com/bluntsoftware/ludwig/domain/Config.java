package com.bluntsoftware.ludwig.domain;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.*;
import java.util.HashMap;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS,property = "@class")
public class Config extends HashMap<String,Object> {
    String configClass;
}
