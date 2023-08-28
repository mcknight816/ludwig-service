package com.bluntsoftware.ludwig.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConnectionMap {
    private String src;
    private String tgt;
    ConnectionPath targetPath;
    ConnectionPath sourcePath;
}
