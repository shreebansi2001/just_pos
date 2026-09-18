package com.crmportal.response.dto;

import java.util.List;

import com.crmportal.request.dto.PipelineRequestDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PipelineResponseDto {

	private Long id;
	
	private String pipelineName;
	
	private List<PipelineOpenStageResponseDto> openStages;
	
	private List<PipelineCloseStageResponseDto> closeStages;
	
	private Long userId;
	
	private String createdAt;
}
