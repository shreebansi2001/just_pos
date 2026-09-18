package com.crmportal.service.impl;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmportal.entity.EventTypeMasterEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.mapper.EventTypeMasterMapper;
import com.crmportal.repository.EventTypeMasterRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.request.dto.EventTypeMasterRequestDto;
import com.crmportal.response.dto.EventTypeMasterResponseDto;
import com.crmportal.service.CommonService;
import com.crmportal.service.EventTypeMasterService;

@Service
public class EventTypeMasterServiceImpl implements EventTypeMasterService{

	@Autowired
	EventTypeMasterMapper eventTypeMasterMapper;
	
	@Autowired
	EventTypeMasterRepository eventTypeMasterRepository;
	
	@Autowired
	UserMasterRepository userMasterRepository;
	
	@Autowired
	CommonService commonService;
	
	@Override
	public EventTypeMasterResponseDto addOrUpdateEventType(@Valid EventTypeMasterRequestDto request, long id) {

	    EventTypeMasterEntity entity;
	    Optional<UserMasterEntity> userOptional = userMasterRepository.findByIdAndIsDeleteFalse(request.getUserId());
	    if (!userOptional.isPresent()) {
	        throw new RuntimeException("User not found with id: " + request.getUserId());
	    }
	    UserMasterEntity user = userOptional.get();

	    Optional<EventTypeMasterEntity> existing =
	            eventTypeMasterRepository.findByNameEnglishAndUserAndIsDeleteFalse(request.getNameEnglish(), user);

	    if (existing.isPresent() && (id == -1 || !existing.get().getId().equals(id))) {
	        throw new RuntimeException("Event Type with name '" + request.getNameEnglish() + "' already exists for this user.");
	    }
	    
	    if (id == -1) {
	        entity = eventTypeMasterMapper.requestToEntity(request);
	    } else {
	        Optional<EventTypeMasterEntity> optional = eventTypeMasterRepository.findByIdAndIsDeleteFalse(id);
	        if (!optional.isPresent()) {
	            throw new RuntimeException("Event Type not found with id: " + id);
	        }
	        entity = optional.get();
	        entity.setUpdatedAt(commonService.getCurrentDateTime());
	        eventTypeMasterMapper.updateEntityFromRequest(request, entity);
	    }

	    
	    entity.setUser(userOptional.get());

	    entity = eventTypeMasterRepository.save(entity);

	    EventTypeMasterResponseDto responseDto = eventTypeMasterMapper.entityToResponse(entity);
	    return responseDto;
	}
	
	@Override
	public List<EventTypeMasterResponseDto> getAllEventTypes() {
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	    List<EventTypeMasterEntity> eventTypeEntities = eventTypeMasterRepository.findAllByIsDeleteFalse();
	    List<EventTypeMasterResponseDto> responseDtos = new ArrayList<>();

	    if (eventTypeEntities.isEmpty()) {
	        return Collections.emptyList();
	    } else {
	        for (EventTypeMasterEntity entity : eventTypeEntities) {
	            EventTypeMasterResponseDto responseDto = eventTypeMasterMapper.entityToResponse(entity);

	            if (entity.getCreatedAt() != null) {
	                responseDto.setCreatedAt(entity.getCreatedAt().format(formatter));
	            }
	            responseDto.setUserId(entity.getUser().getId());
	            responseDtos.add(responseDto);
	        }
	        return responseDtos;
	    }
	}

	@Override
	public EventTypeMasterResponseDto getEventTypeById(Long id) {
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	    Optional<EventTypeMasterEntity> optional = eventTypeMasterRepository.findByIdAndIsDeleteFalse(id);

	    if (!optional.isPresent()) {
	        return null;
	    }
	    EventTypeMasterEntity entity = optional.get();

	    EventTypeMasterResponseDto responseDto = eventTypeMasterMapper.entityToResponse(entity);

	    if (entity.getCreatedAt() != null) {
	        responseDto.setCreatedAt(entity.getCreatedAt().format(formatter));
	    }
	    
        responseDto.setUserId(entity.getUser().getId());

	    return responseDto;
	}

	@Override
	public List<EventTypeMasterResponseDto> getAllEventTypesByUserId(Long userId, String eventTypeName) {
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	    Optional<UserMasterEntity> userOpt = userMasterRepository.findByIdAndIsDeleteFalse(userId);
	    if (!userOpt.isPresent()) {
	        return Collections.emptyList();
	    }

	    UserMasterEntity user = userOpt.get();
	    List<EventTypeMasterEntity> eventTypeEntities;

	    if (eventTypeName == null || eventTypeName.trim().isEmpty()) {
	        eventTypeEntities = eventTypeMasterRepository.findAllByUserAndIsDeleteFalse(user);
	    } else {
	        eventTypeEntities = eventTypeMasterRepository.findByNameEnglishContainingIgnoreCaseAndUserAndIsDeleteFalse(eventTypeName, user);
	    }

	    List<EventTypeMasterResponseDto> responseDtos = new ArrayList<>();
	    for (EventTypeMasterEntity entity : eventTypeEntities) {
	        EventTypeMasterResponseDto responseDto = eventTypeMasterMapper.entityToResponse(entity);

	        if (entity.getCreatedAt() != null) {
	            responseDto.setCreatedAt(entity.getCreatedAt().format(formatter));
	        }
            responseDto.setUserId(entity.getUser().getId());
	        responseDtos.add(responseDto);
	    }

	    return responseDtos;
	}

	
	@Override
	public Boolean deleteEventTypeById(Long id) {
	    Optional<EventTypeMasterEntity> optional = eventTypeMasterRepository.findByIdAndIsDeleteFalse(id);
	    if (!optional.isPresent()) {
	        return false;
	    }
	    EventTypeMasterEntity entity = optional.get();
	    entity.setIsDelete(true);
	    eventTypeMasterRepository.save(entity);
	    return true;
	}
	
	@Override
	public List<EventTypeMasterResponseDto> getEventTypeWithSearch(String eventTypeName,Long userId) {
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	    if (eventTypeName == null || eventTypeName.trim().equals("")) {
	        return Collections.emptyList();
	    }
	    Optional<UserMasterEntity> userOpt = userMasterRepository.findByIdAndIsDeleteFalse(userId);
	    if (!userOpt.isPresent()) {
	        return Collections.emptyList();
	    }

	    UserMasterEntity user = userOpt.get();
	    List<EventTypeMasterEntity> eventTypeEntities =
	            eventTypeMasterRepository.findByNameEnglishContainingIgnoreCaseAndUserAndIsDeleteFalse(eventTypeName,user);

	    List<EventTypeMasterResponseDto> eventTypeResponseDtos = new ArrayList<>();

	    if (eventTypeEntities.isEmpty()) {
	        return Collections.emptyList();
	    } else {
	        for (EventTypeMasterEntity eventTypeEntity : eventTypeEntities) {
	            EventTypeMasterResponseDto responseDto =
	                    eventTypeMasterMapper.entityToResponse(eventTypeEntity);

	            if (eventTypeEntity.getCreatedAt() != null) {
	                responseDto.setCreatedAt(eventTypeEntity.getCreatedAt().format(formatter));
	            }

	            responseDto.setUserId(eventTypeEntity.getUser().getId());
	            eventTypeResponseDtos.add(responseDto);
	        }
	        return eventTypeResponseDtos;
	    }
	}

}
