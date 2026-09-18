package com.crmportal.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.crmportal.request.dto.EventFoodTestingGenerateLinkRequestDto;
import com.crmportal.request.dto.EventFoodTestingGenerateLinkResponseDto;
import com.crmportal.request.dto.EventFoodTestingRequestDto;
import com.crmportal.request.dto.VerifyLinkRequestDto;
import com.crmportal.response.dto.EventFoodTestingFinalMenuResponseDto;
import com.crmportal.response.dto.EventFoodTestingResponseDto;
import com.crmportal.response.dto.EventFunctionTesterResponseDto;

@Service
public interface EventFoodTestingService {

	EventFoodTestingResponseDto addOrUpdateFoodTestingMenu(EventFoodTestingRequestDto request);

	EventFoodTestingFinalMenuResponseDto getMenuByTesterId(VerifyLinkRequestDto request);

	EventFoodTestingGenerateLinkResponseDto generateLink(EventFoodTestingGenerateLinkRequestDto request);

	List<EventFunctionTesterResponseDto> getAllEventFunctionTester(Long eventId, Long eventFunctionId);

}
