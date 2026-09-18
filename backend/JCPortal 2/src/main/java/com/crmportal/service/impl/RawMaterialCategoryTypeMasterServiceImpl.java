package com.crmportal.service.impl;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmportal.entity.RawMaterialCategoryTypeMasterEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.mapper.RawMaterialCategoryTypeMasterMapper;
import com.crmportal.repository.RawMaterialCategoryTypeMasterRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.request.dto.RawMaterialCategoryTypeMasterRequestDto;
import com.crmportal.response.dto.RawMaterialCategoryTypeMasterResponseDto;
import com.crmportal.service.CommonService;
import com.crmportal.service.RawMaterialCategoryTypeMasterService;

@Service
public class RawMaterialCategoryTypeMasterServiceImpl implements RawMaterialCategoryTypeMasterService {

    @Autowired
    RawMaterialCategoryTypeMasterMapper rawMaterialCategoryTypeMasterMapper;

    @Autowired
    RawMaterialCategoryTypeMasterRepository rawMaterialCategoryTypeMasterRepository;

    @Autowired
    UserMasterRepository userMasterRepository;

    @Autowired
    CommonService commonService;

    @Override
    public RawMaterialCategoryTypeMasterResponseDto addOrUpdateRawMaterialCategoryType(
            @Valid RawMaterialCategoryTypeMasterRequestDto request, long id) {

        UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + request.getUserId()));

        Optional<RawMaterialCategoryTypeMasterEntity> duplicate =
                rawMaterialCategoryTypeMasterRepository.findByNameEnglishAndUserAndIsDeleteFalse(request.getNameEnglish(), user);

        if (duplicate.isPresent() && (id == -1 || !duplicate.get().getId().equals(id))) {
            throw new RuntimeException("Raw Material Category Type already exists with name: " + request.getNameEnglish());
        }

        RawMaterialCategoryTypeMasterEntity entity;
        if (id == -1) {
            entity = rawMaterialCategoryTypeMasterMapper.requestToEntity(request);
            entity.setIsActive(true);
        } else {
            entity = rawMaterialCategoryTypeMasterRepository.findByIdAndUserAndIsDeleteFalse(id, user);
            if (entity == null) {
                throw new RuntimeException("Raw Material Category Type not found with id: " + id);
            }
            entity = rawMaterialCategoryTypeMasterMapper.updateEntityFromRequest(request, entity);
            entity.setUpdatedAt(commonService.getCurrentDateTime());
        }

        entity.setUser(user);
        entity = rawMaterialCategoryTypeMasterRepository.save(entity);

        return rawMaterialCategoryTypeMasterMapper.entityToResponse(entity);
    }

    @Override
    public List<RawMaterialCategoryTypeMasterResponseDto> getAllRawMaterialCategoryTypeByUserId(
            Long userId, String categoryTypeName, Boolean isActive) {

        UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        List<RawMaterialCategoryTypeMasterEntity> entities = new ArrayList<>();

        if (categoryTypeName == null || categoryTypeName.trim().isEmpty()) {
            entities = (isActive == null)
                    ? rawMaterialCategoryTypeMasterRepository.findAllByUserAndIsDeleteFalse(user)
                    : rawMaterialCategoryTypeMasterRepository.findAllByUserAndIsDeleteFalseAndIsActive(user, isActive);
        } else {
            entities = (isActive == null)
                    ? rawMaterialCategoryTypeMasterRepository.findByNameEnglishContainingIgnoreCaseAndUserAndIsDeleteFalse(categoryTypeName, user)
                    : rawMaterialCategoryTypeMasterRepository.findByNameEnglishContainingIgnoreCaseAndUserAndIsDeleteFalseAndIsActive(categoryTypeName, user, isActive);
        }

        if (entities.isEmpty()) {
            return Collections.emptyList();
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        List<RawMaterialCategoryTypeMasterResponseDto> responseDtos = new ArrayList<>();

        for (RawMaterialCategoryTypeMasterEntity entity : entities) {
            RawMaterialCategoryTypeMasterResponseDto dto = rawMaterialCategoryTypeMasterMapper.entityToResponse(entity);
            if (entity.getCreatedAt() != null) {
                dto.setCreatedAt(entity.getCreatedAt().format(formatter));
            }
            dto.setUserId(userId);
            responseDtos.add(dto);
        }

        return responseDtos;
    }

    @Override
    public RawMaterialCategoryTypeMasterResponseDto getRawMaterialCategoryTypeById(Long id) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        RawMaterialCategoryTypeMasterEntity entity =
                rawMaterialCategoryTypeMasterRepository.findByIdAndIsDeleteFalse(id);

        if (entity == null) {
            return null;
        }

        RawMaterialCategoryTypeMasterResponseDto dto = rawMaterialCategoryTypeMasterMapper.entityToResponse(entity);
        if (entity.getCreatedAt() != null) {
            dto.setCreatedAt(entity.getCreatedAt().format(formatter));
        }
        dto.setUserId(entity.getUser().getId());
        return dto;
    }

    @Override
    public Boolean deleteRawMaterialCategoryTypeById(Long id) {
        if (rawMaterialCategoryTypeMasterRepository.existsByIdAndIsDeleteFalse(id)) {
            RawMaterialCategoryTypeMasterEntity entity =
                    rawMaterialCategoryTypeMasterRepository.findByIdAndIsDeleteFalse(id);
            entity.setIsDelete(true);
            rawMaterialCategoryTypeMasterRepository.save(entity);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean updateRawMaterialCategoryTypeStatus(Long id, Boolean isActive) {
        if (rawMaterialCategoryTypeMasterRepository.existsByIdAndIsDeleteFalse(id)) {
            RawMaterialCategoryTypeMasterEntity entity =
                    rawMaterialCategoryTypeMasterRepository.findByIdAndIsDeleteFalse(id);
            entity.setIsActive(isActive);
            rawMaterialCategoryTypeMasterRepository.save(entity);
            return true;
        } else {
            return false;
        }
    }
}
