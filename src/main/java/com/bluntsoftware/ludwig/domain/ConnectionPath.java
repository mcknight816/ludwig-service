package com.bluntsoftware.ludwig.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConnectionPath {
    public enum FieldType{
        input,output
    }
    private String flowActivityId;
    private FieldType fieldType;
    private String path;
}
