package com.bluntsoftware.ludwig.domain;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Entity {
    String name;
    @Singular
    List<Variable> variables;
}
