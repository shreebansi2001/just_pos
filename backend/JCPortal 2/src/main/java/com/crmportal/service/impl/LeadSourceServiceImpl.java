package com.crmportal.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmportal.entity.LeadSourceEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.mapper.LeadSourceMapper;
import com.crmportal.repository.LeadMasterRepository;
import com.crmportal.repository.LeadSourceRepository;
import com.crmportal.repository.LeadSubSourceRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.response.dto.LeadSourceResponseDto;
import com.crmportal.service.LeadSourceService;

import io.swagger.models.Response;

@Service
public class LeadSourceServiceImpl implements LeadSourceService {

	@Autowired
	LeadSourceRepository leadSourceRepository;

	@Autowired
	LeadSourceMapper leadSourceMapper;

	@Autowired
	UserMasterRepository userMasterRepository;

	@Autowired
	LeadSubSourceRepository leadSubSourceRepository;
	
	@Autowired
	LeadMasterRepository leadMasterRepository;
	
	@Override
	public LeadSourceResponseDto addOrUpdateLeadSource(String sourceName, Long sourceId, Long userId) {
		LeadSourceEntity entity;
		
		UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(userId)
				.orElseThrow(() -> new RuntimeException("User Not Found"));
		
		Optional<LeadSourceEntity> op = leadSourceRepository.findBySourceNameContainingIgnoreCaseAndIsDeletedFalseAndUser(sourceName,user);
		if (sourceId == -1) {
			if (op.isPresent()) {
				throw new RuntimeException("Lead source is exist with this name : " + sourceName);
			} else {
				entity = new LeadSourceEntity();
			}
		} else {
			entity = leadSourceRepository.findByLeadSourceIdAndIsDeletedFalse(sourceId)
					.orElseThrow(() -> new RuntimeException("Lead source is not found with id : " + sourceId));

			if (op.isPresent() && op.get().getLeadSourceId() != entity.getLeadSourceId()) {
				throw new RuntimeException("Lead source is exist with this name : " + sourceName);
			}
			entity.setUpdatedAt(LocalDateTime.now());
		}

		entity.setUser(user);
		entity.setSourceName(sourceName);
		entity = leadSourceRepository.save(entity);

		LeadSourceResponseDto response = leadSourceMapper.entityToResponse(entity);
		response.setUserId(userId);
		return response;
	}

	@Override
	public List<LeadSourceResponseDto> getAllLeadSource(Long userId) {

	    UserMasterEntity user = userMasterRepository
	            .findByIdAndIsDeleteFalse(userId)
	            .orElseThrow(() -> new RuntimeException("User Not Found"));

	    return leadSourceRepository
	            .findAllByIsDeletedFalseAndUser(user)
	            .stream()
	            .map(leadSourceMapper::entityToResponse)
	            .peek(response -> response.setUserId(userId))
	            .collect(Collectors.toList());
	}

	@Override
	public Boolean deleteLeadSourceById(Long leadSourceId) {
		LeadSourceEntity entity = leadSourceRepository.findByLeadSourceIdAndIsDeletedFalse(leadSourceId)
				.orElseThrow(() -> new RuntimeException("Lead source is not found with id : " + leadSourceId));
		if(leadSubSourceRepository.existsByLeadSourceAndIsDeleteFalse(entity)) {
			 throw new RuntimeException("Lead Source already exists in Lead Sub Source. Please delete it first.");
		}
		
		if(leadMasterRepository.existsByLeadSourceAndIsDeleteFalse(entity)) {
			throw new RuntimeException("Lead  Source already exists in Lead. Please delete it first.");
		}
		
		entity.setIsDeleted(true);
		entity = leadSourceRepository.save(entity);

		return true;
	}

	@Override
	public LeadSourceResponseDto getLeadSourceById(Long leadSourceId) {
		LeadSourceEntity entity = leadSourceRepository.findByLeadSourceIdAndIsDeletedFalse(leadSourceId)
				.orElseThrow(() -> new RuntimeException("Lead source is not found with id : " + leadSourceId));
		LeadSourceResponseDto response = leadSourceMapper.entityToResponse(entity);
		response.setUserId(entity.getUser().getId());
		return response;
	}
}
