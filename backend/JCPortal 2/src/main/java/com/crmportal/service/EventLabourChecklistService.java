package com.crmportal.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.crmportal.request.dto.EventLabourCheckListMainRequestDto;
import com.crmportal.request.dto.EventLabourCheckListRequestDto;
import com.crmportal.response.dto.EventLabourCheckListResponseDto;

@Service
public interface EventLabourChecklistService {

	Boolean addOrUpdate(EventLabourCheckListMainRequestDto request, Long userId);

	List<EventLabourCheckListResponseDto> getAllChecklist(Long eventId, Long eventFunctionId,Long userId);

}
