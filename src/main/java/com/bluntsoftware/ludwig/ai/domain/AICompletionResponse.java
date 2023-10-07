package com.bluntsoftware.ludwig.ai.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document
public class AICompletionResponse {

	@Id
	private String id;
	private String object;
	private Integer created;
	private String model;
	private List<AIChoice> choices;
	private AIUsage usage;
}
