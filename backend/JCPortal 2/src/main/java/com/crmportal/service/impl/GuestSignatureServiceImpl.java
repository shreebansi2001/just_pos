package com.crmportal.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.crmportal.entity.GuestSignatureEntity;
import com.crmportal.repository.GuestSignatureRepository;
import com.crmportal.request.dto.GuestSignatureRequestDto;
import com.crmportal.response.dto.GuestSignatureResponseDto;
import com.crmportal.service.GuestSignatureService;

@Service
public class GuestSignatureServiceImpl implements GuestSignatureService {

	@Autowired
	GuestSignatureRepository guestSignatureRepository;
	
	@Override
	@Transactional
	public List<GuestSignatureResponseDto> addOrUpdateGuestSignature(@Valid List<GuestSignatureRequestDto> request,
			Long eventId, Long eventFunctionId) {
		List<GuestSignatureResponseDto> responseList = new ArrayList<>();

		// Get existing data
		List<GuestSignatureEntity> existingList;

		if (eventFunctionId != null && eventFunctionId == -1) {
			existingList = guestSignatureRepository.findByEventIdAndIsDeleteFalse(eventId);
		} else {
			existingList = guestSignatureRepository.findByEventIdAndEventFunctionIdAndIsDeleteFalse(eventId,
					eventFunctionId);
		}

		List<GuestSignatureEntity> toDeleted = new ArrayList<>();
		
		if (request == null || request.isEmpty()) {
			existingList.stream().forEach(data -> data.setIsDelete(true));
			toDeleted.addAll(existingList);
		}else {
			// IDs which are present in request
			Set<Long> requestIds = request.stream().filter(req -> req.getId() != null && req.getId() != -1)
					.map(GuestSignatureRequestDto::getId).collect(Collectors.toSet());
	
			// Delete records which are not present in request
			for (GuestSignatureEntity existing : existingList) {
	
				if (!requestIds.contains(existing.getId())) {
					existing.setIsDelete(true);
					existing.setUpdatedAt(LocalDateTime.now());
					
					toDeleted.add(existing);
				}
			}
		}
		guestSignatureRepository.saveAll(toDeleted);

		// Insert / Update
		for (GuestSignatureRequestDto req : request) {
			GuestSignatureEntity entity;

			if (req.getId() != null && req.getId() != -1) {
				entity = guestSignatureRepository.findByIdAndIsDeleteFalse(req.getId())
						.orElseThrow(() -> new RuntimeException("Guest signature not found."));

				entity.setUpdatedAt(LocalDateTime.now());

			} else {
				entity = new GuestSignatureEntity();
			}

			entity.setEventId(eventId);
			entity.setEventFunctionId(eventFunctionId);
			entity.setParticulars(req.getParticulars());
			entity.setPersons(req.getPersons());
			entity.setExtra(req.getExtra() != null ? req.getExtra() : 0);
			entity.setUserId(req.getUserId());

			entity = guestSignatureRepository.save(entity);

			GuestSignatureResponseDto response = new GuestSignatureResponseDto();

			response.setId(entity.getId());
			response.setEventId(entity.getEventId());
			response.setEventFunctionId(entity.getEventFunctionId());
			response.setParticulars(entity.getParticulars());
			response.setPersons(entity.getPersons());
			response.setExtra(entity.getExtra());
			response.setUserId(entity.getUserId());

			responseList.add(response);
		}

		return responseList;
	}
	
	@Override
	public List<GuestSignatureResponseDto> getAllByEventAndEventFunction(Long eventId, Long eventFunctionId) {
		List<GuestSignatureEntity> entities;

		if (eventFunctionId != null && eventFunctionId == -1) {
			entities = guestSignatureRepository.findByEventIdAndIsDeleteFalse(eventId);
		} else {
			entities = guestSignatureRepository.findByEventIdAndEventFunctionIdAndIsDeleteFalse(eventId,
					eventFunctionId);
		}

		return entities.stream().map(entity -> {
			GuestSignatureResponseDto response = new GuestSignatureResponseDto();

			response.setId(entity.getId());
			response.setEventId(entity.getEventId());
			response.setEventFunctionId(entity.getEventFunctionId());
			response.setParticulars(entity.getParticulars());
			response.setPersons(entity.getPersons());
			response.setExtra(entity.getExtra());

			return response;
		}).collect(Collectors.toList());
	}
}
