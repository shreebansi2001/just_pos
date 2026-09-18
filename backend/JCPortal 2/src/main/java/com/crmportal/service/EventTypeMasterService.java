package com.crmportal.service;

import java.util.List;

import javax.validation.Valid;

import org.springframework.stereotype.Service;

import com.crmportal.request.dto.EventTypeMasterRequestDto;
import com.crmportal.response.dto.EventTypeMasterResponseDto;

@Service
public interface EventTypeMasterService {

	EventTypeMasterResponseDto addOrUpdateEventType(@Valid EventTypeMasterRequestDto request, long id);

	List<EventTypeMasterResponseDto> getAllEventTypesByUserId(Long userId, String eventTypeName);

	List<EventTypeMasterResponseDto> getAllEventTypes();

	EventTypeMasterResponseDto getEventTypeById(Long id);

	Boolean deleteEventTypeById(Long id);

	List<EventTypeMasterResponseDto> getEventTypeWithSearch(String eventTypeName, Long userId);

}
