package com.crmportal.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmportal.entity.LeadSourceEntity;
import com.crmportal.entity.LeadStatusEntity;
import com.crmportal.entity.LeadStatusTypeEntity;
import com.crmportal.entity.LeadSubSourceEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.mapper.LeadSourceMapper;
import com.crmportal.mapper.LeadStatusMapper;
import com.crmportal.repository.LeadMasterRepository;
import com.crmportal.repository.LeadSourceRepository;
import com.crmportal.repository.LeadStatusRepository;
import com.crmportal.repository.LeadStatusTypeRepository;
import com.crmportal.repository.LeadSubSourceRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.request.dto.LeadStatusRequestDto;
import com.crmportal.request.dto.LeadSubSourceRequestDto;
import com.crmportal.response.dto.LeadStatusResponseDto;
import com.crmportal.response.dto.LeadSubSourceResponseDto;
import com.crmportal.service.LeadStatusService;

import io.swagger.models.Response;

@Service
public class LeadStatusServiceImpl implements LeadStatusService {

	@Autowired
	LeadStatusRepository leadStatusRepository;

	@Autowired
	LeadStatusMapper leadStatusMapper;

	@Autowired
	UserMasterRepository userMasterRepository;
	
	@Autowired
	LeadStatusTypeRepository leadStatusTypeRepository;
	
	@Autowired
	LeadMasterRepository leadMasterRepository;

	@Override
	public LeadStatusResponseDto addOrUpdateLeadStatus(@Valid LeadStatusRequestDto request,
			Long leadstatusId) {

		LeadStatusTypeEntity leadStatusType = leadStatusTypeRepository
				.findByLeadStatusTypeId(request.getLead_status_type_id()).orElseThrow(
						() -> new RuntimeException("Lead source not found with id : " + request.getLead_status_type_id()));

		UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(request.getUserId())
				.orElseThrow(() -> new RuntimeException("User Not Found"));

		LeadStatusEntity entity;
		Optional<LeadStatusEntity> op = leadStatusRepository
				.findByStatusNameContainingIgnoreCaseAndIsDeletedFalseAndUser(request.getStatusName(),user);

		if (leadstatusId == -1) {
			if (op.isPresent()) {
				throw new RuntimeException("Lead status is exist with this name : " + request.getStatusName());
			} else {
				entity = new LeadStatusEntity();
			}
		} else {
			entity = leadStatusRepository.findByLeadStatusIdAndIsDeletedFalse(leadstatusId)
					.orElseThrow(() -> new RuntimeException("Lead status is not found with id : " + leadstatusId));
			if (op.isPresent() && op.get().getLeadStatusId() != entity.getLeadStatusId()) {
				throw new RuntimeException("Lead status is exist with this name : " + request.getStatusName());
			}
			entity.setUpdatedAt(LocalDateTime.now());
		}
		entity.setStatusName(request.getStatusName());
		entity.setColorCode(request.getColorCode());
		entity.setIsActive(request.getIsActive());
		entity.setUser(user);
		entity.setLeadStatus(leadStatusType);
		
		entity = leadStatusRepository.save(entity);

		LeadStatusResponseDto response = leadStatusMapper.entityToResponse(entity);
		response.setUserId(user.getId());
		return response;
	}
	
	@Override
	public List<LeadStatusResponseDto> getAllLeadStatus(Long userId) {

	    UserMasterEntity user = userMasterRepository
	            .findByIdAndIsDeleteFalse(userId)
	            .orElseThrow(() -> new RuntimeException("User Not Found"));

	    return leadStatusRepository
	            .findAllByIsDeletedFalseAndUser(user)
	            .stream()
	            .map(leadStatusMapper::entityToResponse)
	            .peek(response -> response.setUserId(userId))
	            .collect(Collectors.toList());
	}

	@Override
	public Boolean deleteLeadStatusById(Long leadStatusId) {
		LeadStatusEntity entity = leadStatusRepository.findByLeadStatusIdAndIsDeletedFalse(leadStatusId)
				.orElseThrow(() -> new RuntimeException("Lead status is not found with id : " + leadStatusId));
		
		
		/*if(leadMasterRepository.existsByLeadStatusAndIsDeleteFalse(entity)) {
			throw new RuntimeException("Lead  Source already exists in Lead. Please delete it first.");
		}*/
		
		entity.setIsDeleted(true);
		entity = leadStatusRepository.save(entity);

		return true;
	}

	@Override
	public LeadStatusResponseDto getLeadStatusById(Long leadStatusId) {
		LeadStatusEntity entity = leadStatusRepository.findByLeadStatusIdAndIsDeletedFalse(leadStatusId)
				.orElseThrow(() -> new RuntimeException("Lead status is not found with id : " + leadStatusId));
		LeadStatusResponseDto response = leadStatusMapper.entityToResponse(entity);
		response.setUserId(entity.getUser().getId());
		return response;
	}
}
