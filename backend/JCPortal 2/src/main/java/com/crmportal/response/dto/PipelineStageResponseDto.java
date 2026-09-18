package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PipelineStageResponseDto {

	private String stageType;
	
	private Long pipelineId;
	
	private Long stageId;
	
	private String stageName;
}
