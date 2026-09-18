package com.crmportal.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.crmportal.request.dto.EventFollowupRequestDto;
import com.crmportal.response.dto.EventFollowupResponseDto;

@Service
public interface EventFollowUpService {

	EventFollowupResponseDto saveOrUpdate(EventFollowupRequestDto dto);

	EventFollowupResponseDto getById(Long id);

	List<EventFollowupResponseDto> getAll(Long userId, Long managerId, String startDate, String endDate, Boolean isDone,
			Long eventId);

	Boolean deleteById(Long id);
}
