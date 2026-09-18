package com.crmportal.service.impl;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmportal.entity.RoleMasterEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.mapper.RoleMasterMapper;
import com.crmportal.repository.RoleMasterRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.request.dto.RoleMasterRequestDto;
import com.crmportal.response.dto.RoleMasterResponseDto;
import com.crmportal.service.CommonService;
import com.crmportal.service.RoleMasterService;

@Service
public class RoleMasterServiceImpl implements RoleMasterService {

	@Autowired
	RoleMasterRepository roleMasterRepository;

	@Autowired
	RoleMasterMapper roleMasterMapper;

	@Autowired
	CommonService commonService;

	@Autowired
	UserMasterRepository userMasterRepository;
	
	@Override
	public RoleMasterResponseDto addOrUpdateRoleMaster(@Valid RoleMasterRequestDto request, long id) {
		RoleMasterEntity entity = null;
		 Optional<UserMasterEntity> userOptional = userMasterRepository.findByIdAndIsDeleteFalse(request.getUserId());
		    if (!userOptional.isPresent()) {
		        throw new RuntimeException("User not found with id: " + request.getUserId());
		    }
		    UserMasterEntity user = userOptional.get();
        
		Optional<RoleMasterEntity> existing = roleMasterRepository.findByNameAndUserAndIsDeleteFalse(request.getName(),user);
		if (id == -1) {
			if (existing.isPresent()) {
				throw new RuntimeException("Role with the name '" + request.getName() + "' already exists.");
			}
			entity = roleMasterMapper.requestToEntity(request);
		} else {
			Optional<RoleMasterEntity> roleMaster = roleMasterRepository.findByIdAndIsDeleteFalse(id);
			if (!roleMaster.isPresent()) {
				throw new RuntimeException("Role not found with id: " + id);
			} else {
				entity = roleMaster.get();
				if (!entity.getName().equalsIgnoreCase(request.getName()) && existing.isPresent()) {
					throw new RuntimeException("Role with the name '" + request.getName() + "' already exists.");
				}
				entity.setName(request.getName());
				entity.setUpdatedAt(commonService.getCurrentDateTime());
			}
		}
		
        entity.setUser(user);

		entity = roleMasterRepository.save(entity);
		RoleMasterResponseDto responseDto = roleMasterMapper.entityToResponse(entity);
		return responseDto;

	}

	@Override
	public List<RoleMasterResponseDto> getAllRoleMaster(Long userId, String roleName) {
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	    // validate user
	    Optional<UserMasterEntity> userOpt = userMasterRepository.findByIdAndIsDeleteFalse(userId);
	    if (!userOpt.isPresent()) {
	        return Collections.emptyList();
	    }
	    UserMasterEntity user = userOpt.get();

	    // fetch roles with filters
	    List<RoleMasterEntity> entities;
	    if (roleName == null || roleName.trim().isEmpty()) {
	        entities = roleMasterRepository.findAllByUserAndIsDeleteFalse(user);
	    } else {
	        entities = roleMasterRepository.findByNameContainingIgnoreCaseAndUserAndIsDeleteFalse(roleName, user);
	    }

	    if (entities.isEmpty()) {
	        return Collections.emptyList();
	    }

	    // convert entities to response DTOs
	    List<RoleMasterResponseDto> responseDtos = new ArrayList<>();
	    for (RoleMasterEntity entity : entities) {
	        RoleMasterResponseDto dto = roleMasterMapper.entityToResponse(entity);
	        if (entity.getCreatedAt() != null) {
	            dto.setCreatedAt(entity.getCreatedAt().format(formatter));
	        }
	        dto.setUserId(userId);
	        responseDtos.add(dto);
	    }

	    return responseDtos;
	}

	@Override
	public RoleMasterResponseDto getRoleMasterById(Long id) {
		Optional<RoleMasterEntity> entities = roleMasterRepository.findByIdAndIsDeleteFalse(id);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		if (!entities.isPresent()) {
			return null;
		} else {
			RoleMasterEntity roleMasterEntity = entities.get();
				RoleMasterResponseDto responseDto = roleMasterMapper.entityToResponse(roleMasterEntity);
				responseDto.setCreatedAt(roleMasterEntity.getCreatedAt().format(formatter));
				responseDto.setUserId(roleMasterEntity.getId());
				return responseDto;
		}
	}

	@Override
	public Boolean deleteRoleMasterById(Long id) {
		if(!roleMasterRepository.existsByIdAndIsDeleteFalse(id)) {
			return false;
		}
		Optional<RoleMasterEntity> roleMaster = roleMasterRepository.findByIdAndIsDeleteFalse(id);
		if(!roleMaster.isPresent()) {
			return false;
		}
		
		RoleMasterEntity entity = roleMaster.get();
		entity.setIsDelete(true);
		roleMasterRepository.save(entity);
		return true;
	}

}
