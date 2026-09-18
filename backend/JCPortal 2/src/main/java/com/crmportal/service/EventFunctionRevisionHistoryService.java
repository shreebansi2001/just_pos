package com.crmportal.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.crmportal.request.dto.EventFunctionRevisionHistoryRequestDto;
import com.crmportal.response.dto.EventFunctionRevisionHistoryResponseDto;

@Service
public interface EventFunctionRevisionHistoryService {

	Boolean addOrUpdate(EventFunctionRevisionHistoryRequestDto request);

	List<EventFunctionRevisionHistoryResponseDto> getAll(Long userId, Long eventId);

	Boolean deleteById(Long id);

}
