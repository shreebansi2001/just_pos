package com.crmportal.request.dto;

import java.util.List;

import com.crmportal.response.dto.PipelineCloseStageResponseDto;
import com.crmportal.response.dto.PipelineOpenStageResponseDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PipelineRequestDto {

	private Long id;
	
	private String pipelineName;
	
	private List<PipelineOpenStageRequestDto> openStages;
	
	private List<PipelineCloseStageRequestDto> closeStages;
	
	private Long userId;
}
