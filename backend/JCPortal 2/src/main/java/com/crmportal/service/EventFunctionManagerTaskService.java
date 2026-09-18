package com.crmportal.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.crmportal.request.dto.EventFunctionManagerTaskMainRequestDto;
import com.crmportal.response.dto.EventFunctionManagerTaskSummaryResponseDto;
import com.crmportal.response.dto.EventFunctionWiseManagerTaskResponseDto;
import com.crmportal.response.dto.ManagerTaskSummaryResponseDto;

@Service
public interface EventFunctionManagerTaskService {

	Boolean addOrUpdate(EventFunctionManagerTaskMainRequestDto request, Long userId);

	EventFunctionManagerTaskSummaryResponseDto getEventFunctionManagerTask(Long eventFunctionId, Long managerId,
			String type);

	ManagerTaskSummaryResponseDto getManagerTaskSummary(Long managerId, Long eventFunctionId);

	List<EventFunctionWiseManagerTaskResponseDto> getAllFunctionWiseManagerTask(Long eventId);

	Boolean deleteManagerTaskImage(Long id);

}
