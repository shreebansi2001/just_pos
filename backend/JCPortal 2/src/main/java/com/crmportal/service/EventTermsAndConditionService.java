package com.crmportal.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.crmportal.request.dto.EventTermsAndConditionRequestDto;
import com.crmportal.response.dto.EventTermsAndConditionResponseDto;

@Service
public interface EventTermsAndConditionService {

	EventTermsAndConditionResponseDto addOrUpdateTermsAndCondition(EventTermsAndConditionRequestDto request);

	List<EventTermsAndConditionResponseDto> getByEventId(Long eventId);

}
