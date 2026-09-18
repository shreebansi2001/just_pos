package com.crmportal.service;

import java.util.List;

import javax.validation.Valid;

import org.springframework.stereotype.Service;

import com.crmportal.request.dto.LeadStatusRequestDto;
import com.crmportal.request.dto.LeadSubSourceRequestDto;
import com.crmportal.response.dto.LeadStatusResponseDto;

@Service
public interface LeadStatusService {

	LeadStatusResponseDto addOrUpdateLeadStatus(@Valid LeadStatusRequestDto request, Long leadStatusId);

	List<LeadStatusResponseDto> getAllLeadStatus(Long userId);

	Boolean deleteLeadStatusById(Long leadStatusId);

	LeadStatusResponseDto getLeadStatusById(Long leadStatusId);
}
