package com.crmportal.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmportal.entity.UserRightsModuleEntity;
import com.crmportal.repository.ModuleRightsRepository;
import com.crmportal.request.dto.UserModuleRightsRequestDto;
import com.crmportal.response.dto.UserModuleRightsResponseDto;
import com.crmportal.service.UserModuleRightsService;
import com.crmportal.utility.DateMapper;

@Service
public class UserModuleRightsServiceImpl implements UserModuleRightsService {

	@Autowired
	ModuleRightsRepository moduleRightsRepository;

	@Autowired
	DateMapper dateMapper;

	@Override
	public UserModuleRightsResponseDto addOrUpdateModuleRights(@Valid UserModuleRightsRequestDto request, Long id) {

		Optional<UserRightsModuleEntity> existingName = moduleRightsRepository
				.findByNameAndIsDeleteFalse(request.getName());
		UserRightsModuleEntity entity = null;
		if (id == -1) {
			if (existingName.isPresent()) {
				throw new RuntimeException("Module with the name '" + request.getName() + "' already exists.");
			}
			entity = new UserRightsModuleEntity();
		} else {
			Optional<UserRightsModuleEntity> userRightsModule = moduleRightsRepository.findByIdAndIsDeleteFalse(id);
			if (!userRightsModule.isPresent()) {
				throw new RuntimeException("Module Name Not Found With id " + id);
			}

			entity = userRightsModule.get();
			if (!entity.getName().equalsIgnoreCase(request.getName()) && existingName.isPresent()) {
				throw new RuntimeException("Module with the name '" + request.getName() + "' already exists.");
			}

		}

		entity.setName(request.getName());
		entity.setIsAdminModule(request.getIsAdminModule());
		entity = moduleRightsRepository.save(entity);
		return setUserModuleRightsResponse(entity);
	}

	@Override
	public List<UserModuleRightsResponseDto> getAllModuleRights() {

		List<UserModuleRightsResponseDto> dtos = new ArrayList<>();
		List<UserRightsModuleEntity> entities = moduleRightsRepository.findAllByIsDeleteFalse();
		if (entities.isEmpty()) {
			return Collections.emptyList();
		}
		for (UserRightsModuleEntity userRightsModuleEntity : entities) {
			dtos.add(setUserModuleRightsResponse(userRightsModuleEntity));
		}
		return dtos;
	}

	@Override
	public UserModuleRightsResponseDto getModuleRightsById(Long id) {

		UserRightsModuleEntity userRightsModuleEntity = moduleRightsRepository.findByIdAndIsDeleteFalse(id)
				.orElseThrow(() -> new RuntimeException("Module not found"));
		if (userRightsModuleEntity == null) {
			return null;
		}
		return setUserModuleRightsResponse(userRightsModuleEntity);
	}

	private UserModuleRightsResponseDto setUserModuleRightsResponse(UserRightsModuleEntity entity) {
		UserModuleRightsResponseDto responseDto = new UserModuleRightsResponseDto();
		responseDto.setId(entity.getId());
		responseDto.setIsActive(entity.getIsActive());
		responseDto.setIsDelete(entity.getIsDelete());
		responseDto.setName(entity.getName());
		responseDto.setIsAdminModule(entity.getIsAdminModule());
		responseDto.setCreatedAt(dateMapper.dateTimeToString(entity.getCreatedAt()));
		return responseDto;
	}

	@Override
	public Boolean deleteModuleRightsById(Long id) {
		Boolean isSuccess = false;

		if (moduleRightsRepository.existsByIdAndIsDeleteFalse(id)) {

			Optional<UserRightsModuleEntity> entity = moduleRightsRepository.findByIdAndIsDeleteFalse(id);
			UserRightsModuleEntity moduleEntity = entity.get();
			moduleEntity.setIsDelete(true);
			moduleRightsRepository.save(moduleEntity);
			isSuccess = true;
		}

		return isSuccess;
	}

}
