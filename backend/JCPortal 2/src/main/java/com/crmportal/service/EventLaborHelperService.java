package com.crmportal.service;

import java.util.List;

import com.crmportal.request.dto.EventLaborHelperRequestDto;
import com.crmportal.response.dto.EventLaborHelperResponseDto;

public interface EventLaborHelperService {

    List<EventLaborHelperResponseDto> saveEventLaborHelpers(EventLaborHelperRequestDto request);

    List<EventLaborHelperResponseDto> getByEventId(Long eventId);

    List<EventLaborHelperResponseDto> getByEventIdAndFunctionIdAndPartyId(Long eventId, Long eventFunctionId, Long partyId);

    boolean deleteByEventIdAndFunctionIdAndPartyId(Long eventId, Long eventFunctionId, Long partyId);

    boolean deleteByEventId(Long eventId);
}