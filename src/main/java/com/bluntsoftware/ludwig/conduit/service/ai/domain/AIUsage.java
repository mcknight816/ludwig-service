package com.bluntsoftware.ludwig.conduit.service.ai.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AIUsage {

	private Integer prompt_tokens;
	private Integer completion_tokens;
	private Integer total_tokens;
}
