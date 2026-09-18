package com.crmportal.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.crmportal.request.dto.PipelineRequestDto;
import com.crmportal.response.dto.EmployeeLeadPerformanceForReportResponseDto;
import com.crmportal.response.dto.PipelineResponseDto;

@Service
public interface PipelineService {

	PipelineResponseDto createUpdatePipeLine(PipelineRequestDto request);

	Boolean deletePipeLine(Long pipelineId);

	List<PipelineResponseDto> getAllPipeLine(Long userId);

	Map<String, Object> getStageByPipelineId(Long pipelineId, Long userId);

	Map<String, Object> getStageLeadDataByPipelineId(Long pipelineId, Long memberId, Long userId);

	Map<String, Object> getStageLeadDataByPipelineIdAndStage(Long pipelineId, Long userId, String stage, Long memberId);

	Map<String, Object> getPerformance(Long userId, String startDate, String endDate, Long memberId);

	Map<String, Object> getEmployeePerformance(Long userId, String startDate, String endDate, Long pipelineId,Long memberId);

	List<EmployeeLeadPerformanceForReportResponseDto> getEmployeePerformanceForReport(Long employeeId, String startDate,
			String endDate, Long pipelineId, Long userId);

	Boolean deleteStage(Long stageId, String stageType);

}
