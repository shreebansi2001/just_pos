package com.crmportal.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.crmportal.response.dto.LeadSourceResponseDto;

@Service
public interface LeadSourceService {

	LeadSourceResponseDto addOrUpdateLeadSource(String sourceName, Long sourceId, Long userId);

	List<LeadSourceResponseDto> getAllLeadSource(Long userId);

	Boolean deleteLeadSourceById(Long leadSourceId);

	LeadSourceResponseDto getLeadSourceById(Long leadSourceId);
}
