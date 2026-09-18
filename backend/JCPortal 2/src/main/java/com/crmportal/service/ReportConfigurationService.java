package com.crmportal.service;

import java.util.List;

import com.crmportal.request.dto.ReportConfigurationRequestDto;
import com.crmportal.response.dto.ReportConfigurationResponseDto;

public interface ReportConfigurationService {

	ReportConfigurationResponseDto addOrUpdateReportConfiguration(ReportConfigurationRequestDto request);

	Boolean deleteReportConfiguration(Long id);

	List<ReportConfigurationResponseDto> getAllReportConfiguration(Long mappingId, Long moduleId, Integer isExtraCharges);

	ReportConfigurationResponseDto getReportConfigurationById(Long id, Integer isExtraCharges);
	
}
