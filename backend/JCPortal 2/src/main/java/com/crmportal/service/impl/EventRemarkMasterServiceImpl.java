package com.crmportal.service.impl;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmportal.entity.EventRemarkMasterEntity;
import com.crmportal.entity.EventTypeMasterEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.mapper.EventRemarkMasterMapper;
import com.crmportal.repository.EventRemarkMasterRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.request.dto.EventRemarkMasterRequestDto;
import com.crmportal.response.dto.EventRemarkMasterResponseDto;
import com.crmportal.response.dto.EventTypeMasterResponseDto;
import com.crmportal.service.CommonService;
import com.crmportal.service.EventRemarkMasterService;


@Service
public class EventRemarkMasterServiceImpl implements EventRemarkMasterService{

	@Autowired
	EventRemarkMasterMapper eventRemarkMasterMapper;
	
	@Autowired
	EventRemarkMasterRepository eventRemarkMasterRepository;
	
	@Autowired
	UserMasterRepository userMasterRepository;
	
	@Autowired
	CommonService commonService;
	
	@Override
	public EventRemarkMasterResponseDto addOrUpdateEventRemark(EventRemarkMasterRequestDto request, long id) {

		EventRemarkMasterEntity entity;
		Optional<UserMasterEntity> userOptional = userMasterRepository.findByIdAndIsDeleteFalse(request.getUserId());
		if (id == -1) {
            entity = new EventRemarkMasterEntity();
        } else {
        	Optional<EventRemarkMasterEntity> optional = eventRemarkMasterRepository.findByIdAndIsDeleteFalse(id);
	        if (!optional.isPresent()) {
	            throw new RuntimeException("Event Remark not found with id: " + id);
	        }
	        entity = optional.get();
	        entity.setUpdatedAt(commonService.getCurrentDateTime());
	       // eventRemarkMasterMapper.updateEntityFromRequest(request, entity);
        	
        	
        }
		
        entity.setNameEnglish(request.getNameEnglish());
        entity.setNameHindi(request.getNameHindi());
        entity.setNameGujarati(request.getNameGujarati());
        entity.setIsOdc(request.getIsOdc());
        entity.setType(request.getType());
        entity.setUser(userOptional.get());
        entity.setIsActive(request.getIsActive() != null
                ? request.getIsActive() : true);
        entity.setIsDelete(false);

        entity = eventRemarkMasterRepository.save(entity);

	    EventRemarkMasterResponseDto responseDto = eventRemarkMasterMapper.entityToResponse(entity);
	    return responseDto;
    }
	
	
	@Override
	public List<EventRemarkMasterResponseDto> getAllEventRemarks(Long userId, String eventRemarkName) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	    Optional<UserMasterEntity> userOpt = userMasterRepository.findByIdAndIsDeleteFalse(userId);
	    if (!userOpt.isPresent()) {
	        return Collections.emptyList();
	    }

	    UserMasterEntity user = userOpt.get();
	    List<EventRemarkMasterEntity> eventRemarkEntities;

	    if (eventRemarkName == null || eventRemarkName.trim().isEmpty()) {
	    	eventRemarkEntities = eventRemarkMasterRepository.findByUserIdAndIsDeleteFalseAndIsActiveTrue(userId);
	    } else {
	    	eventRemarkEntities = eventRemarkMasterRepository.findByTypeContainingIgnoreCaseAndUserAndIsDeleteFalse(eventRemarkName, user);
	    }

	    List<EventRemarkMasterResponseDto> responseDtos = new ArrayList<>();
	    for (EventRemarkMasterEntity entity : eventRemarkEntities) {
	        EventRemarkMasterResponseDto responseDto = eventRemarkMasterMapper.entityToResponse(entity);

	        if (entity.getCreatedAt() != null) {
	            responseDto.setCreatedAt(entity.getCreatedAt().format(formatter));
	        }
            responseDto.setUserId(entity.getUser().getId());
	        responseDtos.add(responseDto);
	    }

	    return responseDtos;
		
		
	}

	@Override
	public EventRemarkMasterResponseDto getEventRemarkById(Long id) {
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	    Optional<EventRemarkMasterEntity> optional = eventRemarkMasterRepository.findByIdAndIsDeleteFalse(id);

	    if (!optional.isPresent()) {
	        return null;
	    }
	    EventRemarkMasterEntity entity = optional.get();

	    EventRemarkMasterResponseDto responseDto = eventRemarkMasterMapper.entityToResponse(entity);

	    if (entity.getCreatedAt() != null) {
	        responseDto.setCreatedAt(entity.getCreatedAt().format(formatter));
	    }
	    
        responseDto.setUserId(entity.getUser().getId());

	    return responseDto;
	}
	
	@Override
	public Boolean deleteEventRemarkById(Long id) {
	    Optional<EventRemarkMasterEntity> optional = eventRemarkMasterRepository.findByIdAndIsDeleteFalse(id);
	    if (!optional.isPresent()) {
	        return false;
	    }
	    EventRemarkMasterEntity entity = optional.get();
	    entity.setIsDelete(true);
	    eventRemarkMasterRepository.save(entity);
	    return true;
	}
	
	

}
