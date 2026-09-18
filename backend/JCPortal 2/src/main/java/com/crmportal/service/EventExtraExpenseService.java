package com.crmportal.service;

import java.util.List;

import javax.validation.Valid;

import org.springframework.stereotype.Service;

import com.crmportal.request.dto.ContactTypeMasterRequestDto;
import com.crmportal.request.dto.EventExtraExpenseRequestDto;
import com.crmportal.response.dto.ContactTypeMasterResponseDto;
import com.crmportal.response.dto.EventExtraExpenseResponseDto;

@Service
public interface EventExtraExpenseService {

	EventExtraExpenseResponseDto addOrUpdateExtraService(@Valid EventExtraExpenseRequestDto request, Long valueOf);

	List<EventExtraExpenseResponseDto> getAllEventExtraByEventId(Long eventId, Long eventFunctionId);

	EventExtraExpenseResponseDto getEventExtraExpenseById(Long id);

	Boolean deleteEventExtraById(Long id);

}
