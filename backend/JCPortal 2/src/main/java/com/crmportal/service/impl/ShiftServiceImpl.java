package com.crmportal.service.impl;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.crmportal.entity.EventTypeMasterEntity;
import com.crmportal.entity.FunctionMasterEntity;
import com.crmportal.entity.ShiftEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.mapper.FunctionMasterMapper;
import com.crmportal.repository.FunctionMasterRepository;
import com.crmportal.repository.ShiftRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.request.dto.FunctionMasterRequestDto;
import com.crmportal.request.dto.ShiftRequestDto;
import com.crmportal.response.dto.FunctionMasterResponseDto;
import com.crmportal.response.dto.ShiftResponseDto;
import com.crmportal.service.CommonService;
import com.crmportal.service.FunctionMasterService;
import com.crmportal.service.ShiftService;

@Service
public class ShiftServiceImpl implements ShiftService {

	@Autowired
	UserMasterRepository userMasterRepository;

	@Autowired
	CommonService commonService;

	@Autowired
	ShiftRepository shiftRepository;


	@Override
	public ShiftResponseDto addOrUpdateShift(@Valid ShiftRequestDto request, long id) {

		ShiftEntity entity;

		Optional<UserMasterEntity> userOptional = userMasterRepository.findByIdAndIsDeleteFalse(request.getUserId());
		if (!userOptional.isPresent()) {
			throw new RuntimeException("User not found with id: " + request.getUserId());
		}
		UserMasterEntity user = userOptional.get();

		Optional<ShiftEntity> existing = shiftRepository
				.findByNameEnglishAndUserAndIsDeleteFalse(request.getNameEnglish(), user);

		if (existing.isPresent() && (id == -1 || !existing.get().getId().equals(id))) {
			throw new RuntimeException(
					"Function with name '" + request.getNameEnglish() + "' already exists.");
		}

		if (id == -1) {
			entity = new ShiftEntity();
		} else {
			Optional<ShiftEntity> optional = shiftRepository.findByIdAndIsDeleteFalse(id);
			if (!optional.isPresent()) {
				throw new RuntimeException("Function not found with id: " + id);
			}
			entity = optional.get();
			entity.setUpdatedAt(commonService.getCurrentDateTime());
		}

		entity.setUser(user);
		entity.setNameEnglish(request.getNameEnglish());
		entity.setNameGujarati(request.getNameGujarati());
		entity.setNameHindi(request.getNameHindi());
		entity.setShifttime(request.getShifttime());
		entity.setPrice(request.getPrice());
		entity = shiftRepository.save(entity);

		return toDto(entity);
	}
	
	public static ShiftResponseDto toDto(ShiftEntity entity) {
	    return new ShiftResponseDto(
	        entity.getId(),
	        entity.getNameEnglish(),
	        entity.getNameHindi(),
	        entity.getNameGujarati(),
	        entity.getShifttime(),
	        entity.getCreatedAt() != null ? entity.getCreatedAt().toString() : null,
	        		entity.getPrice(),
	        		entity.getUser().getId()
	    );
	}

	@Override
	public List<ShiftResponseDto> getAllShiftByUserId(Long userId, String functionName) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		Optional<UserMasterEntity> userOpt = userMasterRepository.findByIdAndIsDeleteFalse(userId);
		if (!userOpt.isPresent()) {
			return Collections.emptyList();
		}

		UserMasterEntity user = userOpt.get();
		List<ShiftEntity> shiftEntities;

		if (functionName == null || functionName.trim().isEmpty()) {
			shiftEntities = shiftRepository.findAllByUserAndIsDeleteFalse(user);
		} else {
			shiftEntities = shiftRepository.findByNameEnglishContainingIgnoreCaseAndUserAndIsDeleteFalse(functionName, user);
		}

		List<ShiftResponseDto> responseDtos = new ArrayList<>();
		for (ShiftEntity entity : shiftEntities) {
			ShiftResponseDto responseDto = toDto(entity);

			if (entity.getCreatedAt() != null) {
				responseDto.setCreatedAt(entity.getCreatedAt().format(formatter));
			}
			responseDtos.add(responseDto);
		}

		return responseDtos;
	}

	@Override
	public ShiftResponseDto getShiftById(Long id) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		Optional<ShiftEntity> entity = shiftRepository.findByIdAndIsDeleteFalse(id);
		if (!entity.isPresent()) {
	        return null;
	    }
		ShiftEntity functionEntity = entity.get();
		ShiftResponseDto responseDto = toDto(functionEntity);
		if (functionEntity.getCreatedAt() != null) {
			responseDto.setCreatedAt(functionEntity.getCreatedAt().format(formatter));
		}
		return responseDto;
	}

	@Override
	public Boolean deleteShiftById(Long id) {
		  Optional<ShiftEntity> optional = shiftRepository.findByIdAndIsDeleteFalse(id);
		    if (!optional.isPresent()) {
		        return false;
		    }
		    ShiftEntity entity = optional.get();
		    entity.setIsDelete(true);
		    shiftRepository.save(entity);
		    return true;
	}

}
