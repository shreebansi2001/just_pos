package com.crmportal.service.impl;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmportal.entity.MealTypeMasterEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.mapper.MealTypeMasterMapper;
import com.crmportal.repository.MealTypeMasterRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.request.dto.MealTypeMasterRequestDto;
import com.crmportal.response.dto.MealTypeMasterResponseDto;
import com.crmportal.service.CommonService;
import com.crmportal.service.MealTypeMasterService;

@Service
public class MealTypeMasterServiceImpl implements MealTypeMasterService{

	@Autowired
	MealTypeMasterMapper mealTypeMasterMapper;
	
	@Autowired
	MealTypeMasterRepository mealTypeMasterRepository;
	
	@Autowired
	UserMasterRepository userMasterRepository;
	
	@Autowired
	CommonService commonService;
	
	@Override
	public MealTypeMasterResponseDto addOrUpdateMealType(@Valid MealTypeMasterRequestDto request, long id) {

	    MealTypeMasterEntity entity;

	    Optional<UserMasterEntity> userOptional = userMasterRepository.findByIdAndIsDeleteFalse(request.getUserId());
	    if (!userOptional.isPresent()) {
	        throw new RuntimeException("User not found with id: " + request.getUserId());
	    }
	    UserMasterEntity user = userOptional.get();

	    Optional<MealTypeMasterEntity> existing = mealTypeMasterRepository
	            .findByNameEnglishAndUserAndIsDeleteFalse(request.getNameEnglish(), user);

	    if (existing.isPresent() && (id == -1 || !existing.get().getId().equals(id))) {
	        throw new RuntimeException(
	                "Meal Type with name '" + request.getNameEnglish() + "' already exists.");
	    }

	    if (id == -1) {
	        entity = mealTypeMasterMapper.requestToEntity(request);
	    } else {
	        Optional<MealTypeMasterEntity> optional = mealTypeMasterRepository.findByIdAndIsDeleteFalse(id);
	        if (!optional.isPresent()) {
	            throw new RuntimeException("Meal Type not found with id: " + id);
	        }
	        entity = optional.get();
	        entity.setUpdatedAt(commonService.getCurrentDateTime());
	        mealTypeMasterMapper.updateEntityFromRequest(request, entity);
	    }

	    entity.setUser(user);
	    entity = mealTypeMasterRepository.save(entity);

	    return mealTypeMasterMapper.entityToResponse(entity);
	}

	@Override
    public List<MealTypeMasterResponseDto> getAllMealTypesByUserId(Long userId, String mealTypeName) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        Optional<UserMasterEntity> userOpt = userMasterRepository.findByIdAndIsDeleteFalse(userId);
        if (!userOpt.isPresent()) {
            return Collections.emptyList();
        }

        UserMasterEntity user = userOpt.get();
        List<MealTypeMasterEntity> mealTypeEntities;

        if (mealTypeName == null || mealTypeName.trim().isEmpty()) {
            mealTypeEntities = mealTypeMasterRepository.findAllByUserAndIsDeleteFalse(user);
        } else {
            mealTypeEntities = mealTypeMasterRepository
                    .findByNameEnglishContainingIgnoreCaseAndUserAndIsDeleteFalse(mealTypeName, user);
        }

        List<MealTypeMasterResponseDto> responseDtos = new ArrayList<>();
        for (MealTypeMasterEntity entity : mealTypeEntities) {
            MealTypeMasterResponseDto responseDto = mealTypeMasterMapper.entityToResponse(entity);

            if (entity.getCreatedAt() != null) {
                responseDto.setCreatedAt(entity.getCreatedAt().format(formatter));
            }
            responseDto.setUserId(userId);
            responseDtos.add(responseDto);
        }

        return responseDtos;
    }

    @Override
    public MealTypeMasterResponseDto getMealTypeById(Long id) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        Optional<MealTypeMasterEntity> entity = mealTypeMasterRepository.findByIdAndIsDeleteFalse(id);
        if (!entity.isPresent()) {
            return null;
        }
        MealTypeMasterEntity mealTypeEntity = entity.get();
        MealTypeMasterResponseDto responseDto = mealTypeMasterMapper.entityToResponse(mealTypeEntity);
        if (mealTypeEntity.getCreatedAt() != null) {
            responseDto.setCreatedAt(mealTypeEntity.getCreatedAt().format(formatter));
        }
        responseDto.setUserId(mealTypeEntity.getUser().getId());
        return responseDto;
    }

    @Override
    public Boolean deleteMealTypeById(Long id) {
        Optional<MealTypeMasterEntity> optional = mealTypeMasterRepository.findByIdAndIsDeleteFalse(id);
        if (!optional.isPresent()) {
            return false;
        }
        MealTypeMasterEntity entity = optional.get();
        entity.setIsDelete(true);
        mealTypeMasterRepository.save(entity);
        return true;
    }
}
