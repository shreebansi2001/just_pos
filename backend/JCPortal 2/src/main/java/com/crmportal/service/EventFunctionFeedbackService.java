package com.crmportal.service;

import java.util.List;

import javax.validation.Valid;

import com.crmportal.request.dto.EventFunctionFeedbackRequest;
import com.crmportal.response.dto.EventFunctionFeedbackResponse;

public interface EventFunctionFeedbackService {

	EventFunctionFeedbackResponse addOrUpdate(EventFunctionFeedbackRequest request);

	EventFunctionFeedbackResponse get(Long id);

	List<EventFunctionFeedbackResponse> getAll(String name, String mobileno, Long eventId,
			Long eventFunctionId, Long userId, Long memberId);

	Boolean deleteById(Long feedbackId);

}