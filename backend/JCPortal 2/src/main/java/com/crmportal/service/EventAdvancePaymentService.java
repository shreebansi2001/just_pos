package com.crmportal.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.crmportal.request.dto.EventAdvancePaymentRequestDto;
import com.crmportal.response.dto.EventAdvancePaymentResponseDto;

@Service
public interface EventAdvancePaymentService {

	EventAdvancePaymentResponseDto addOrUpdateEventAdvancePayment(EventAdvancePaymentRequestDto request);

	List<EventAdvancePaymentResponseDto> getAllEventAdvancePaymentList(Long eventId);

	EventAdvancePaymentResponseDto getEventAdvancePaymentById(Long id);

	Boolean deleteEventAdvancePaymentById(Long id);

	String generateReport(Long advancePaymentId, Long userId, Long eventId, HttpServletRequest re, Boolean isTermsCond, Long eventFunctionId);

}
