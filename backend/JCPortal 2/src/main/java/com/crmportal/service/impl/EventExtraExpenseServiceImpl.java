package com.crmportal.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmportal.entity.EventExtraExpenseEntity;
import com.crmportal.entity.EventFunctionMasterEntity;
import com.crmportal.entity.EventMasterEntity;
import com.crmportal.entity.RawMaterialMasterEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.repository.EventExtraExpenseRepository;
import com.crmportal.repository.EventFunctionMasterRepository;
import com.crmportal.repository.EventMasterRepository;
import com.crmportal.repository.RawMaterialMasterRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.request.dto.EventExtraExpenseRequestDto;
import com.crmportal.response.dto.EventExtraExpenseResponseDto;
import com.crmportal.service.CommonService;
import com.crmportal.service.EventExtraExpenseService;

@Service
public class EventExtraExpenseServiceImpl implements EventExtraExpenseService {

	@Autowired
	EventExtraExpenseRepository eventExtraExpenseRepository;

	@Autowired
	UserMasterRepository userMasterRepository;

	@Autowired
	CommonService commonService;
	
	@Autowired
	EventMasterRepository eventMasterRepository;
	
	@Autowired
	EventFunctionMasterRepository eventFunctionMasterRepository;
	
	@Autowired
	RawMaterialMasterRepository rawMaterialMasterRepository;

	@Override
	public EventExtraExpenseResponseDto addOrUpdateExtraService(@Valid EventExtraExpenseRequestDto request,
			Long id) {

		Optional<UserMasterEntity> user = userMasterRepository.findByIdAndIsDeleteFalse(request.getUserId());
		EventExtraExpenseEntity entity;
		
		EventMasterEntity eventMaster = eventMasterRepository.findByIdAndIsDeleteFalse(request.getEventId())
				.orElseThrow(() -> new RuntimeException(
						"Event Master with this eventId not found: " + request.getEventId()));
		
		EventFunctionMasterEntity functionEntity = eventFunctionMasterRepository.findByIdAndIsDeleteFalse(request.getEventFunctionId())
				.orElseThrow(() -> new RuntimeException(
						"Event Function Master not found with id: " + request.getEventFunctionId()));
		
		if (id == -1) {
			entity = new EventExtraExpenseEntity();
			entity.setCreatedAt(commonService.getCurrentDateTime());
		} else {
			Optional<EventExtraExpenseEntity> en = eventExtraExpenseRepository.findById(id);
			if (!en.isPresent()) {
				throw new RuntimeException("Contact Type not found with id: " + id);
			}
			entity = en.get();
			
		}

		entity.setEvent(eventMaster);
		entity.setEventFunction(functionEntity);
		entity.setNameEnglish(request.getNameEnglish());
		entity.setNameHindi(request.getNameHindi());
		entity.setNameGujarati(request.getNameGujarati());
		entity.setPrice(request.getPrice());
		entity.setQty(request.getQty());
		entity.setTotalprice(request.getTotalprice());
		if(user.isPresent()) {
			entity.setUser(user.get());
		}
		entity = eventExtraExpenseRepository.save(entity);
		return getEventExtraExpenseById(entity.getId());
	}

	@Override
	public List<EventExtraExpenseResponseDto> getAllEventExtraByEventId(Long eventId, Long eventFunctionId) {
		
		List<EventExtraExpenseEntity> entities = eventExtraExpenseRepository.findByEvent_IdAndEventFunction_Id(eventId, eventFunctionId);
		if (entities.size() == 0) {
			EventMasterEntity eventMaster = eventMasterRepository.findByIdAndIsDeleteFalse(eventId)
					.orElseThrow(() -> new RuntimeException(
							"Event Master with this eventId not found: " + eventId));
			List<RawMaterialMasterEntity> rawList = rawMaterialMasterRepository.findByUserAndIsGeneralFixTrue(eventMaster.getUser());
			List<EventExtraExpenseResponseDto> responseD = new ArrayList<>();
			for (RawMaterialMasterEntity raw : rawList) {
				responseD.add(entityToResponseFromRawMaterial(raw, eventId, eventFunctionId));
			}
			return responseD;
		}
		List<EventExtraExpenseResponseDto> responseDtos = new ArrayList<>();
		if (entities.isEmpty()) {
			return Collections.emptyList();
		} else {
			for (EventExtraExpenseEntity entity : entities) {
				responseDtos.add(entityToResponse(entity));
			}
			return responseDtos;
		}
	}
	
	private EventExtraExpenseResponseDto entityToResponseFromRawMaterial(RawMaterialMasterEntity mat, Long eventId, Long eventFunctionId) {
		EventExtraExpenseResponseDto responseDto = new EventExtraExpenseResponseDto();
		responseDto.setId(0l);
		responseDto.setEventId(eventId);
		responseDto.setEventFunctionId(eventFunctionId);
		responseDto.setNameEnglish(mat.getNameEnglish());
		responseDto.setNameHindi(mat.getNameHindi());
		responseDto.setNameGujarati(mat.getNameGujarati());
		responseDto.setPrice(mat.getSupplierRate().doubleValue());
		responseDto.setQty(1.0);
		responseDto.setTotalprice(mat.getSupplierRate().doubleValue());
		return responseDto;
	}
	
	private EventExtraExpenseResponseDto entityToResponse(EventExtraExpenseEntity eventExtra) {
		EventExtraExpenseResponseDto responseDto = new EventExtraExpenseResponseDto();
		responseDto.setId(eventExtra.getId());
		responseDto.setEventId(eventExtra.getEvent().getId());
		responseDto.setEventFunctionId(eventExtra.getEventFunction().getId());
		responseDto.setNameEnglish(eventExtra.getNameEnglish());
		responseDto.setNameHindi(eventExtra.getNameHindi());
		responseDto.setNameGujarati(eventExtra.getNameGujarati());
		responseDto.setPrice(eventExtra.getPrice());
		responseDto.setQty(eventExtra.getQty());
		responseDto.setTotalprice(eventExtra.getTotalprice());
		return responseDto;
	}

	@Override
	public EventExtraExpenseResponseDto getEventExtraExpenseById(Long id) {
		Optional<EventExtraExpenseEntity> en = eventExtraExpenseRepository.findById(id);
		
		if (!en.isPresent()) {
			return null;
		} else {
			EventExtraExpenseEntity eventExtra = en.get();
			return entityToResponse(eventExtra);
		}
	}

	@Override
	public Boolean deleteEventExtraById(Long id) {
		Optional<EventExtraExpenseEntity> en = eventExtraExpenseRepository.findById(id);
		if (en.isPresent()) {
			eventExtraExpenseRepository.deleteById(id);
			return true;
		}else {
			return false;
		}
	}
}
