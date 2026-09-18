package com.crmportal.service;

import java.util.List;

import javax.validation.Valid;

import org.springframework.stereotype.Service;

import com.crmportal.request.dto.EventLaborDetailsRequestForAppDto;
import com.crmportal.request.dto.EventLaborRequestDto;
import com.crmportal.response.dto.EventLaborResponseDto;

@Service
public interface EventLaborService {
	 EventLaborResponseDto getEventLabor(Long eventId, Long eventFunctionId);
	 EventLaborResponseDto saveUpdateEventLabour(EventLaborRequestDto req);
	 EventLaborResponseDto getEventLaborBySupplier(Long eventId, Long eventFunctionId, Long partyId);
	EventLaborResponseDto saveUpdateEventLabourForApp(@Valid List<EventLaborDetailsRequestForAppDto> request);
	Boolean deleteEventLaborById(Long id);
}
