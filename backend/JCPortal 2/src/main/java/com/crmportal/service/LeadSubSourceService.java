package com.crmportal.service;

import java.util.List;

import javax.validation.Valid;

import org.springframework.stereotype.Service;

import com.crmportal.request.dto.LeadSubSourceRequestDto;
import com.crmportal.response.dto.LeadSourceAllDataResponseDto;
import com.crmportal.response.dto.LeadSubSourceResponseDto;

@Service
public interface LeadSubSourceService {

	LeadSubSourceResponseDto addOrUpdateLeadSubSource(@Valid LeadSubSourceRequestDto request, Long leadSubSourceId);

	List<LeadSubSourceResponseDto> getAllLeadSubSource(Long userId);

	Boolean deleteLeadSubSourceById(Long leadSourceId);

	LeadSubSourceResponseDto getLeadSubSourceById(Long subSourceId);

	LeadSourceAllDataResponseDto getAllByLeadSourceId(Long leadSourceId);

}
