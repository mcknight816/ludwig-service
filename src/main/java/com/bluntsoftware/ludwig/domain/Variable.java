package com.bluntsoftware.ludwig.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Variable {
    String name;
    String type;
    Boolean list;
    Boolean primary;
    Boolean ignore;
    Boolean notNull;
    Integer length;
}
