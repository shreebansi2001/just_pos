package com.crmportal.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.crmportal.request.dto.EventFunctionFilterRequestDto;
import com.crmportal.request.dto.EventFunctionMasterRequestDto;
import com.crmportal.response.dto.AllEventFunctionMasterResponseDto;
import com.crmportal.response.dto.EventFunctionFilterResponseDto;
import com.crmportal.response.dto.EventFunctionMasterResponseDto;

@Service
public interface EventFunctionMasterService {

	List<EventFunctionMasterResponseDto> getAllEventFunctionByEventId(Long eventId);

	List<EventFunctionMasterResponseDto> getEventFunctionById(Long eventFunctionId);

	Boolean deleteEventFunctionById(Long id);

	List<EventFunctionMasterResponseDto> getAllEventFunctionByEventId(List<Long> allEventsIds);

	Page<AllEventFunctionMasterResponseDto> getAllEventFunction(int page, int size,String name,Long userId);
	
	List<EventFunctionFilterResponseDto> getFilteredEventFunctions(
            EventFunctionFilterRequestDto request);

}
