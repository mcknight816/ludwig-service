package com.bluntsoftware.ludwig.dto;

import com.bluntsoftware.ludwig.domain.Flow;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationDto {
    String id;
    String name;
    String description;
    String path;
    String jwkUri;
    List<Flow> flows;
}
