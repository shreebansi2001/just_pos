package com.crmportal.service;

import java.util.List;

import javax.validation.Valid;

import org.springframework.stereotype.Service;

import com.crmportal.request.dto.EventRemarkMasterRequestDto;
import com.crmportal.response.dto.EventRemarkMasterResponseDto;

@Service
public interface EventRemarkMasterService {

	EventRemarkMasterResponseDto addOrUpdateEventRemark(@Valid EventRemarkMasterRequestDto request, long id);

	List<EventRemarkMasterResponseDto> getAllEventRemarks(Long userId, String eventRemarkName);

	EventRemarkMasterResponseDto getEventRemarkById(Long id);

	Boolean deleteEventRemarkById(Long id);

	

}
