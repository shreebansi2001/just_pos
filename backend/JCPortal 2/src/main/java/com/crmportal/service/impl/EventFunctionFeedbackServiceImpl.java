package com.crmportal.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmportal.entity.EventFunctionFeedbackEntity;
import com.crmportal.repository.EventFunctionFeedbackRepository;
import com.crmportal.request.dto.EventFunctionFeedbackRequest;
import com.crmportal.response.dto.EventFunctionFeedbackResponse;
import com.crmportal.service.CommonService;
import com.crmportal.service.EventFunctionFeedbackService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventFunctionFeedbackServiceImpl implements EventFunctionFeedbackService {

	@Autowired
	private final EventFunctionFeedbackRepository repository;

	@Autowired
	CommonService commonService;

	@Override
	public EventFunctionFeedbackResponse addOrUpdate(EventFunctionFeedbackRequest request) {

		EventFunctionFeedbackEntity entity;

		if (request.getId() != null && request.getId() != -1 && request.getId() != 0) {

			entity = repository.findById(request.getId())
					.orElseThrow(() -> new RuntimeException("Feedback not found with id: " + request.getId()));
			entity.setUpdatedAt(commonService.getCurrentDateTime());
		} else {

			entity = new EventFunctionFeedbackEntity();
			entity.setCreatedAt(commonService.getCurrentDateTime());
		}

		BeanUtils.copyProperties(request, entity);

		EventFunctionFeedbackEntity saved = repository.save(entity);

		return convertToResponse(saved);
	}

	@Override
	public Boolean deleteById(Long id) {

		EventFunctionFeedbackEntity entity = repository.findById(id)
				.orElseThrow(() -> new RuntimeException("Feedback not found with id: " + id));

		repository.delete(entity);
		return true;
	}

	@Override
	public EventFunctionFeedbackResponse get(Long id) {

		EventFunctionFeedbackEntity entity = repository.findById(id)
				.orElseThrow(() -> new RuntimeException("Feedback not found with id: " + id));

		return convertToResponse(entity);
	}

	@Override
	public List<EventFunctionFeedbackResponse> getAll(String name, String mobileno, Long eventId, Long eventFunctionId,
			Long userId, Long memberId) {

		List<EventFunctionFeedbackEntity> entities = repository.getAllWithFilters(name, mobileno, eventId,
				eventFunctionId, userId, memberId);

		List<EventFunctionFeedbackResponse> responseList = new ArrayList<>();

		for (EventFunctionFeedbackEntity entity : entities) {
			responseList.add(convertToResponse(entity));
		}

		return responseList;
	}

	private EventFunctionFeedbackResponse convertToResponse(EventFunctionFeedbackEntity entity) {

		EventFunctionFeedbackResponse response = new EventFunctionFeedbackResponse();

		BeanUtils.copyProperties(entity, response);

		return response;
	}
}