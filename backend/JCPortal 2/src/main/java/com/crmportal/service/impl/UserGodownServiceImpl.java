package com.crmportal.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.management.RuntimeErrorException;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmportal.entity.UserGodownEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.mapper.UserGodownMapper;
import com.crmportal.repository.UserGodownRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.request.dto.UserGodownRequestDto;
import com.crmportal.response.dto.UserGodownResponseDto;
import com.crmportal.service.UserGodownService;

@Service
public class UserGodownServiceImpl implements UserGodownService {

	@Autowired
	UserGodownRepository userGodownRepository;
	
	@Autowired
	UserGodownMapper userGodownMapper;
	
	@Autowired
	UserMasterRepository userMasterRepository;
	
	@Override
	public UserGodownResponseDto addOrUserGodownMaster(UserGodownRequestDto request) {
		
		UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(request.getUserId())
				.orElseThrow(() -> new RuntimeException("User not found with id : " + request.getUserId()));
		
		UserGodownEntity entity = new UserGodownEntity();
		
		if(request.getId() == -1) {
			entity = userGodownMapper.requestToEntity(request);
			entity.setUser(user);
		} else {
			entity = userGodownMapper.updateEntityFromRequest(entity, request);
			entity.setUser(user);
		}
		
		entity = userGodownRepository.save(entity);
		
		UserGodownResponseDto responseDto = userGodownMapper.entityToResponse(entity);
		responseDto.setUserId(entity.getUser().getId());
		
		return responseDto;
	}

	@Override
	public List<UserGodownResponseDto> getAllGodownMaster(Long userId) {
		
		List<UserGodownEntity> entities = userGodownRepository.findByUserIdAndIsDeleteFalse(userId);
		List<UserGodownResponseDto> responseDtos = new ArrayList<>();
		
		for (UserGodownEntity entity : entities) {
			UserGodownResponseDto dto = userGodownMapper.entityToResponse(entity);
			dto.setUserId(entity.getUser().getId());
			
			responseDtos.add(dto);
		}
		
		return responseDtos;
	}

	@Override
	public UserGodownResponseDto getGodownMasterById(Long id) {
		
		UserGodownEntity entity = userGodownRepository.findByIdAndIsDeleteFalse(id)
				.orElseThrow(() -> new RuntimeException("User Godown not found with id : " + id));
		
		UserGodownResponseDto responseDto = userGodownMapper.entityToResponse(entity);
		responseDto.setUserId(entity.getUser().getId());
		
		return responseDto;
	}

	@Override
	public Boolean deleteGodownMasterById(Long id) {
		UserGodownEntity entity = userGodownRepository.findByIdAndIsDeleteFalse(id)
				.orElseThrow(() -> new RuntimeException("User Godown not found with id : " + id));
		entity.setIsDelete(true);
		userGodownRepository.save(entity);
		return true;
	}

}
