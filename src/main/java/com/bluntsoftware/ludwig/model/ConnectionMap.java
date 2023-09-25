package com.bluntsoftware.ludwig.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConnectionMap {
    private Connection parent;
    private String src;
    private String tgt;
    @JsonIgnore
    ConnectionPath targetPath;
    @JsonIgnore
    ConnectionPath sourcePath;
}
