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
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.mapper.FunctionMasterMapper;
import com.crmportal.repository.FunctionMasterRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.request.dto.FunctionMasterRequestDto;
import com.crmportal.response.dto.FunctionMasterResponseDto;
import com.crmportal.service.CommonService;
import com.crmportal.service.FunctionMasterService;

@Service
public class FunctionMasterServiceImpl implements FunctionMasterService {

	@Autowired
	UserMasterRepository userMasterRepository;

	@Autowired
	CommonService commonService;

	@Autowired
	FunctionMasterRepository functionMasterRepository;

	@Autowired
	FunctionMasterMapper functionMasterMapper;

	@Override
	public FunctionMasterResponseDto addOrUpdateFunctionMaster(@Valid FunctionMasterRequestDto request, long id) {

		FunctionMasterEntity entity;

		Optional<UserMasterEntity> userOptional = userMasterRepository.findByIdAndIsDeleteFalse(request.getUserId());
		if (!userOptional.isPresent()) {
			throw new RuntimeException("User not found with id: " + request.getUserId());
		}
		UserMasterEntity user = userOptional.get();

		Optional<FunctionMasterEntity> existing = functionMasterRepository
				.findByNameEnglishAndUserAndIsDeleteFalse(request.getNameEnglish(), user);

		if (existing.isPresent() && (id == -1 || !existing.get().getId().equals(id))) {
			throw new RuntimeException(
					"Function with name '" + request.getNameEnglish() + "' already exists.");
		}

		if (id == -1) {
			entity = functionMasterMapper.requestToEntity(request);
		} else {
			Optional<FunctionMasterEntity> optional = functionMasterRepository.findByIdAndIsDeleteFalse(id);
			if (!optional.isPresent()) {
				throw new RuntimeException("Function not found with id: " + id);
			}
			entity = optional.get();
			entity.setUpdatedAt(commonService.getCurrentDateTime());
			functionMasterMapper.updateEntityFromRequest(request, entity);
		}

		entity.setUser(user);
		entity = functionMasterRepository.save(entity);

		return functionMasterMapper.entityToResponse(entity);
	}

	@Override
	public List<FunctionMasterResponseDto> getAllFunctionsByUserId(Long userId, String functionName) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		Optional<UserMasterEntity> userOpt = userMasterRepository.findByIdAndIsDeleteFalse(userId);
		if (!userOpt.isPresent()) {
			return Collections.emptyList();
		}

		UserMasterEntity user = userOpt.get();
		List<FunctionMasterEntity> functionEntities;

		if (functionName == null || functionName.trim().isEmpty()) {
			functionEntities = functionMasterRepository.findAllByUserAndIsDeleteFalse(user);
		} else {
			functionEntities = functionMasterRepository
					.findByNameEnglishContainingIgnoreCaseAndUserAndIsDeleteFalse(functionName, user);
		}

		List<FunctionMasterResponseDto> responseDtos = new ArrayList<>();
		for (FunctionMasterEntity entity : functionEntities) {
			FunctionMasterResponseDto responseDto = functionMasterMapper.entityToResponse(entity);

			if (entity.getCreatedAt() != null) {
				responseDto.setCreatedAt(entity.getCreatedAt().format(formatter));
			}
            responseDto.setUserId(entity.getUser().getId());
			responseDtos.add(responseDto);
		}

		return responseDtos;
	}

	@Override
	public FunctionMasterResponseDto getFunctionsById(Long id) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		Optional<FunctionMasterEntity> entity = functionMasterRepository.findByIdAndIsDeleteFalse(id);
		if (!entity.isPresent()) {
	        return null;
	    }
		FunctionMasterEntity functionEntity = entity.get();
		FunctionMasterResponseDto responseDto = functionMasterMapper.entityToResponse(functionEntity);
		if (functionEntity.getCreatedAt() != null) {
			responseDto.setCreatedAt(functionEntity.getCreatedAt().format(formatter));
		}
        responseDto.setUserId(functionEntity.getUser().getId());
		return responseDto;
	}

	@Override
	public Boolean deleteFunctionById(Long id) {
		  Optional<FunctionMasterEntity> optional = functionMasterRepository.findByIdAndIsDeleteFalse(id);
		    if (!optional.isPresent()) {
		        return false;
		    }
		    FunctionMasterEntity entity = optional.get();
		    entity.setIsDelete(true);
		    functionMasterRepository.save(entity);
		    return true;
	}

}
