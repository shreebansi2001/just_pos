package com.crmportal.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmportal.entity.UserAmcEntity;
import com.crmportal.mapper.UserAmcMapper;
import com.crmportal.repository.UserAmcRepository;
import com.crmportal.response.dto.UserAmcResponseDto;
import com.crmportal.service.UserAmcService;

@Service
public class UserAmcServiceImpl implements UserAmcService {

	@Autowired
	UserAmcRepository userAmcRepository;
	
	@Autowired
	UserAmcMapper userAmcMapper;
	
	@Override
	public Boolean deleteUserAmcById(Long id) {
		Boolean isSuccess = false;
		UserAmcEntity userAmcEntity = userAmcRepository.findByIdAndIsDeleteFalse(id);
		if(userAmcEntity == null) {
			return isSuccess;
		}
		userAmcEntity.setIsDelete(true);
		userAmcRepository.save(userAmcEntity);
		isSuccess = true;
		
		return isSuccess;
	}

	@Override
	public List<UserAmcResponseDto> getAllUserAmc() {
		List<UserAmcResponseDto> responseDtos = new ArrayList<>();
		List<UserAmcEntity> allUserAmc = userAmcRepository.findByIsDeleteFalse();
		
		if(allUserAmc == null || allUserAmc.isEmpty()) {
			return null;
		}
		
		for (UserAmcEntity userAmcEntity : allUserAmc) {
			UserAmcResponseDto responseDto = userAmcMapper.entityToResponse(userAmcEntity);
			responseDtos.add(responseDto);
		}
		
		return responseDtos;
	}

}
