package com.bluntsoftware.ludwig.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.ui.ConcurrentModel;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document
public class ModelJson {
    @Id
    String modelType;
    ConcurrentModel json;
}
