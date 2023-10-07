package com.bluntsoftware.ludwig.ai.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AIChoice {

	private String text;
	private Integer index;
	private String finish_reason;
}
