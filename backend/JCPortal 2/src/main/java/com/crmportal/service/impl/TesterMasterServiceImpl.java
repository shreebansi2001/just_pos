package com.crmportal.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmportal.entity.TesterMasterEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.mapper.TesterMasterMapper;
import com.crmportal.repository.TesterMasterRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.request.dto.TesterMasterRequestDto;
import com.crmportal.response.dto.TesterMasterResponseDto;
import com.crmportal.service.TesterMasterService;
import com.crmportal.utility.UserMasterHelper;

@Service
public class TesterMasterServiceImpl implements TesterMasterService {

	@Autowired
	TesterMasterRepository testerMasterRepository;
	
	@Autowired
	UserMasterRepository userMasterRepository;
	
	@Autowired
	TesterMasterMapper testerMasterMapper;
	
	@Autowired
	UserMasterHelper helper;
	
	@Override
	public TesterMasterResponseDto addOrUpdateTesterMaster(TesterMasterRequestDto request) {
		UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(request.getUserId())
				.orElseThrow(() -> new RuntimeException("User not found with id : " + request.getUserId()));

		TesterMasterEntity entity = null;

//		Optional<TesterMasterEntity> op = testerMasterRepository
//				.findByEmailAndUserIdAndIsDeleteFalse(request.getEmail(), request.getUserId());

		Optional<TesterMasterEntity> op1 = testerMasterRepository
				.findByContactNoAndUserIdAndIsDeleteFalse(request.getContactNo(), request.getUserId());

		if (request.getId() == -1l) {
			if (op1.isPresent()) {
				throw new RuntimeException("Contact No. or Email is exist.");
			}
			entity = testerMasterMapper.requestToEntity(request);
		} else {
			entity = testerMasterRepository.findByIdAndIsDeleteFalse(request.getId())
					.orElseThrow(() -> new RuntimeException("Tester Master not found with id : " + request.getId()));

			if (op1.isPresent() && op1.get().getId() != request.getId()) {
				throw new RuntimeException("Contact No. or Email is exist.");
			}

			entity = testerMasterMapper.updateEntity(entity, request);
			entity.setUpdatedAt(LocalDateTime.now());
		}

		entity = testerMasterRepository.save(entity);

		TesterMasterResponseDto response = testerMasterMapper.entityToResponse(entity);

		return response;
	}
	
	@Override
	public List<TesterMasterResponseDto> getAllTesterMaster(Long userId) {
		UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(userId)
				.orElseThrow(() -> new RuntimeException("User not found with id : " + userId));
		
		List<TesterMasterEntity> data = testerMasterRepository.findAllByUserIdAndIsDeleteFalse(userId); 
		
		List<TesterMasterResponseDto> response = testerMasterMapper.entityToResponse(data);
		
		return response;
	}
	
	@Override
	public TesterMasterResponseDto getTesterMasterById(Long id) {
		TesterMasterEntity entity = testerMasterRepository.findByIdAndIsDeleteFalse(id)
				.orElseThrow(() -> new RuntimeException("Tester Master not found with id : " + id)); 
		
		TesterMasterResponseDto response = testerMasterMapper.entityToResponse(entity);
		
		return response;
	}
	
	@Override
	public Boolean deleteTesterMasterById(Long id) {
		TesterMasterEntity entity = testerMasterRepository.findByIdAndIsDeleteFalse(id)
				.orElseThrow(() -> new RuntimeException("Tester Master not found with id : " + id)); 
		
		entity.setIsDelete(true);
		entity.setUpdatedAt(LocalDateTime.now());
		
		testerMasterRepository.save(entity);
		
		return true;
	}
	
}
