package com.crmportal.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmportal.entity.EventMasterEntity;
import com.crmportal.entity.EventTermsAndConditionEntity;
import com.crmportal.entity.EventTermsAndConditionFeaturesEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.repository.EventMasterRepository;
import com.crmportal.repository.EventTermsAndConditionFeaturesRepository;
import com.crmportal.repository.EventTermsAndConditionRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.request.dto.EventTermsAndConditionFeaturesRequestDto;
import com.crmportal.request.dto.EventTermsAndConditionRequestDto;
import com.crmportal.response.dto.EventTermsAndConditionResponseDto;
import com.crmportal.service.EventTermsAndConditionService;

@Service
public class EventTermsAndConditionServiceImpl implements EventTermsAndConditionService {

	@Autowired
	EventTermsAndConditionRepository eventTermsAndConditionRepository;

	@Autowired
	EventTermsAndConditionFeaturesRepository eventTermsAndConditionFeaturesRepository;

	@Autowired
	UserMasterRepository userMasterRepository;

	@Autowired
	EventMasterRepository eventMasterRepository;

	@Override
	@Transactional
	public EventTermsAndConditionResponseDto addOrUpdateTermsAndCondition(EventTermsAndConditionRequestDto request) {
		EventMasterEntity eventMaster = eventMasterRepository.findByIdAndIsDeleteFalse(request.getEventId())
				.orElseThrow(() -> new RuntimeException("Event not found with id : " + request.getEventId()));

		UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(request.getUserId())
				.orElseThrow(() -> new RuntimeException("User not found with id : " + request.getUserId()));

		EventTermsAndConditionEntity entity;

		if (request.getId() != -1) {

			entity = eventTermsAndConditionRepository.findById(request.getId())
					.orElseThrow(() -> new RuntimeException("Terms & Condition not found"));

			entity.setNameEnglish(request.getNameEnglish());
			entity.setNameHindi(request.getNameHindi());
			entity.setNameGujarati(request.getNameGujarati());
			entity.setUpdatedAt(LocalDateTime.now());

		} else {

			entity = new EventTermsAndConditionEntity();
			entity.setNameEnglish(request.getNameEnglish());
			entity.setNameHindi(request.getNameHindi());
			entity.setNameGujarati(request.getNameGujarati());
			entity.setEventId(request.getEventId());

			entity.setUser(user);
			entity.setCreatedAt(LocalDateTime.now());
			entity.setIsDelete(false);
		}

		entity = eventTermsAndConditionRepository.save(entity);

		if (request.getId() != null) {
			eventTermsAndConditionFeaturesRepository.deleteByEventTermsConditionId(entity.getId());
		}

		if (request.getFeatures() != null && !request.getFeatures().isEmpty()) {

			List<EventTermsAndConditionFeaturesEntity> featureEntities = new ArrayList<>();

			for (EventTermsAndConditionFeaturesRequestDto featureDto : request.getFeatures()) {

				EventTermsAndConditionFeaturesEntity featureEntity = new EventTermsAndConditionFeaturesEntity();

				featureEntity.setEventTermsConditionId(entity.getId());
				featureEntity.setDescription(featureDto.getDescription());
				featureEntity.setDescriptionHindi(featureDto.getDescriptionHindi());
				featureEntity.setDescriptionGujarati(featureDto.getDescriptionGujarati());

				featureEntities.add(featureEntity);
			}

			eventTermsAndConditionFeaturesRepository.saveAll(featureEntities);
		}

		// Prepare Response
		EventTermsAndConditionResponseDto response = new EventTermsAndConditionResponseDto();

		response.setId(entity.getId());
		response.setNameEnglish(entity.getNameEnglish());
		response.setNameHindi(entity.getNameHindi());
		response.setNameGujarati(entity.getNameGujarati());
		response.setEventId(entity.getEventId());
		response.setUserId(entity.getUser().getId());
		response.setCreatedAt(entity.getCreatedAt());
		response.setUpdatedAt(entity.getUpdatedAt());
		response.setIsDelete(entity.getIsDelete());
		response.setFeatures(request.getFeatures());

		return response;
	}

	@Override
	public List<EventTermsAndConditionResponseDto> getByEventId(Long eventId) {

		List<EventTermsAndConditionEntity> entities = eventTermsAndConditionRepository
				.findByEventIdAndIsDeleteFalse(eventId);

		List<EventTermsAndConditionResponseDto> responseList = new ArrayList<>();

		for (EventTermsAndConditionEntity entity : entities) {

			EventTermsAndConditionResponseDto response = new EventTermsAndConditionResponseDto();

			response.setId(entity.getId());
			response.setNameEnglish(entity.getNameEnglish());
			response.setNameHindi(entity.getNameHindi());
			response.setNameGujarati(entity.getNameGujarati());
			response.setEventId(entity.getEventId());
			response.setUserId(entity.getUser().getId());
			response.setCreatedAt(entity.getCreatedAt());
			response.setUpdatedAt(entity.getUpdatedAt());
			response.setIsDelete(entity.getIsDelete());

			List<EventTermsAndConditionFeaturesEntity> featureEntities = eventTermsAndConditionFeaturesRepository
					.findByEventTermsConditionIdAndIsDeleteFalse(entity.getId());

			List<EventTermsAndConditionFeaturesRequestDto> features = new ArrayList<>();

			for (EventTermsAndConditionFeaturesEntity featureEntity : featureEntities) {
				EventTermsAndConditionFeaturesRequestDto featureDto = new EventTermsAndConditionFeaturesRequestDto();

				featureDto.setEventTermsConditionId(featureEntity.getEventTermsConditionId());
				featureDto.setDescription(featureEntity.getDescription());
				featureDto.setDescriptionHindi(featureEntity.getDescriptionHindi());
				featureDto.setDescriptionGujarati(featureEntity.getDescriptionGujarati());

				features.add(featureDto);
			}

			response.setFeatures(features);

			responseList.add(response);
		}

		return responseList;
	}
	
}
