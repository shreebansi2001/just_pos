package com.crmportal.service.impl;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmportal.entity.KitchenAreaMasterEntity;
import com.crmportal.entity.MenuItemMasterEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.mapper.KitchenAreaMasterMapper;
import com.crmportal.repository.KitchenAreaMasterRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.request.dto.KitchenAreaMasterRequestDto;
import com.crmportal.response.dto.KitchenAreaMasterResponseDto;
import com.crmportal.service.CommonService;
import com.crmportal.service.KitchenAreaMasterService;

@Service
public class KitchenAreaMasterServiceImpl implements KitchenAreaMasterService {

    @Autowired
    private KitchenAreaMasterMapper kitchenAreaMasterMapper;

    @Autowired
    private KitchenAreaMasterRepository kitchenAreaMasterRepository;

    @Autowired
    private UserMasterRepository userMasterRepository;

    @Autowired
    private CommonService commonService;

    @Override
    public KitchenAreaMasterResponseDto addOrUpdateKitchenAreaMaster(
            @Valid KitchenAreaMasterRequestDto request, long id) {

        KitchenAreaMasterEntity entity;
        Optional<UserMasterEntity> userOptional =
                userMasterRepository.findByIdAndIsDeleteFalse(request.getUserId());
        if (!userOptional.isPresent()) {
            throw new RuntimeException("User not found with id: " + request.getUserId());
        }
        UserMasterEntity user = userOptional.get();

        Optional<KitchenAreaMasterEntity> existing =
                kitchenAreaMasterRepository.findByNameEnglishAndUserAndIsDeleteFalse(request.getNameEnglish(), user);

        if (existing.isPresent() && (id == -1 || !existing.get().getId().equals(id))) {
            throw new RuntimeException("Kitchen Area with name '" + request.getNameEnglish()
                    + "' already exists for this user.");
        }

        if (id == -1) {
            entity = kitchenAreaMasterMapper.requestToEntity(request);
        } else {
            Optional<KitchenAreaMasterEntity> optional =
                    kitchenAreaMasterRepository.findByIdAndIsDeleteFalse(id);
            if (!optional.isPresent()) {
                throw new RuntimeException("Kitchen Area not found with id: " + id);
            }
            entity = optional.get();
            entity.setUpdatedAt(commonService.getCurrentDateTime());
            kitchenAreaMasterMapper.updateEntityFromRequest(request, entity);
        }

        entity.setUser(user);
        entity = kitchenAreaMasterRepository.save(entity);

        return kitchenAreaMasterMapper.entityToResponse(entity);
    }

    @Override
    public List<KitchenAreaMasterResponseDto> getAllKitchenAreasByUserId(Long userId, String kitchenAreaName) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        Optional<UserMasterEntity> userOpt = userMasterRepository.findByIdAndIsDeleteFalse(userId);
        if (!userOpt.isPresent()) {
            return Collections.emptyList();
        }

        UserMasterEntity user = userOpt.get();
        List<KitchenAreaMasterEntity> entities;

        if (kitchenAreaName == null || kitchenAreaName.trim().isEmpty()) {
            entities = kitchenAreaMasterRepository.findAllByUserAndIsDeleteFalse(user);
        } else {
            entities = kitchenAreaMasterRepository.findByNameEnglishContainingIgnoreCaseAndUserAndIsDeleteFalse(
                    kitchenAreaName, user);
        }

        List<KitchenAreaMasterResponseDto> responseDtos = new ArrayList<>();
        for (KitchenAreaMasterEntity entity : entities) {
            KitchenAreaMasterResponseDto responseDto = kitchenAreaMasterMapper.entityToResponse(entity);
            if (entity.getCreatedAt() != null) {
                responseDto.setCreatedAt(entity.getCreatedAt().format(formatter));
            }
            responseDto.setUserId(entity.getUser().getId());
            responseDtos.add(responseDto);
        }

        return responseDtos;
    }

    @Override
    public KitchenAreaMasterResponseDto getKitchenAreaById(Long id) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        Optional<KitchenAreaMasterEntity> optional = kitchenAreaMasterRepository.findByIdAndIsDeleteFalse(id);
        if (!optional.isPresent()) {
            return null;
        }
        KitchenAreaMasterEntity entity = optional.get();
        KitchenAreaMasterResponseDto responseDto = kitchenAreaMasterMapper.entityToResponse(entity);

        if (entity.getCreatedAt() != null) {
            responseDto.setCreatedAt(entity.getCreatedAt().format(formatter));
        }
        
        responseDto.setUserId(entity.getUser().getId());
        return responseDto;
    }

    @Override
    public Boolean deleteKitchenAreaById(Long id) {
        Optional<KitchenAreaMasterEntity> optional = kitchenAreaMasterRepository.findByIdAndIsDeleteFalse(id);
        if (!optional.isPresent()) {
            return false;
        }
        KitchenAreaMasterEntity entity = optional.get();
        entity.setIsDelete(true);
        kitchenAreaMasterRepository.save(entity);
        return true;
    }

	@Override
	public boolean updateKitchenAreaStatus(Long id, Boolean isActive) {
		 Optional<KitchenAreaMasterEntity> entityOpt = kitchenAreaMasterRepository.findByIdAndIsDeleteFalse(id);
		    if (!entityOpt.isPresent()) {
		        throw new RuntimeException("Kitchen Item not found with id: " + id);
		    }

		    KitchenAreaMasterEntity entity = entityOpt.get();
		    entity.setIsActive(isActive);
		    kitchenAreaMasterRepository.save(entity);
		    return true;
	}
}
	