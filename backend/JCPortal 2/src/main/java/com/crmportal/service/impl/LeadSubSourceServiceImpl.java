package com.crmportal.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmportal.entity.LeadSourceEntity;
import com.crmportal.entity.LeadSubSourceEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.mapper.LeadSourceMapper;
import com.crmportal.mapper.LeadSubSourceMapper;
import com.crmportal.repository.LeadMasterRepository;
import com.crmportal.repository.LeadSourceRepository;
import com.crmportal.repository.LeadSubSourceRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.request.dto.LeadSubSourceRequestDto;
import com.crmportal.response.dto.LeadSourceAllDataResponseDto;
import com.crmportal.response.dto.LeadSubSourceResponseDto;
import com.crmportal.response.dto.LeadSubSourceResponseDto2;
import com.crmportal.service.LeadSubSourceService;

@Service
public class LeadSubSourceServiceImpl implements LeadSubSourceService {

	@Autowired
	LeadSubSourceRepository leadSubSourceRepository;

	@Autowired
	LeadSourceRepository leadSourceRepository;

	@Autowired
	LeadSubSourceMapper leadSubSourceMapper;

	@Autowired
	LeadSourceMapper leadSourceMapper;

	@Autowired
	UserMasterRepository userMasterRepository;

	@Autowired
	LeadMasterRepository leadMasterRepository;

	@Override
	public LeadSubSourceResponseDto addOrUpdateLeadSubSource(@Valid LeadSubSourceRequestDto request,
			Long leadSubSourceId) {

		LeadSourceEntity leadSource = leadSourceRepository
				.findByLeadSourceIdAndIsDeletedFalse(request.getLeadSourceId()).orElseThrow(
						() -> new RuntimeException("Lead source not found with id : " + request.getLeadSourceId()));

		UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(request.getUserId())
				.orElseThrow(() -> new RuntimeException("User Not Found"));

		LeadSubSourceEntity entity;
		Optional<LeadSubSourceEntity> op = leadSubSourceRepository
				.findByNameContainingIgnoreCaseAndIsDeleteFalse(request.getName());

		if (leadSubSourceId == -1) {
			if (op.isPresent()) {
				throw new RuntimeException("Lead subsource exist with this name : " + request.getName());
			} else {
				entity = new LeadSubSourceEntity();
			}
		} else {
			entity = leadSubSourceRepository.findByLeadSubSourceIdAndIsDeleteFalse(leadSubSourceId)
					.orElseThrow(() -> new RuntimeException("Lead subsource not found with id : " + leadSubSourceId));

			if (op.isPresent() && op.get().getLeadSubSourceId() != entity.getLeadSubSourceId()) {
				throw new RuntimeException("Lead subsource exist with this name : " + request.getName());
			}
			entity.setUpdatedAt(LocalDateTime.now());
		}

		entity.setUser(user);
		entity.setName(request.getName());
		entity.setDateTime(request.getDateTime() != null
				? LocalDateTime.parse(request.getDateTime(), DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
				: null);
		entity.setDescription(request.getDescription());
		entity.setLeadSource(leadSource);

		entity = leadSubSourceRepository.save(entity);

		LeadSubSourceResponseDto response = leadSubSourceMapper.entityToResponse(entity);
		response.setUserId(user.getId());
		return response;
	}

	@Override
	public List<LeadSubSourceResponseDto> getAllLeadSubSource(Long userId) {
		UserMasterEntity userMasterEntity = userMasterRepository.findByIdAndIsDeleteFalse(userId)
				.orElseThrow(() ->  new RuntimeException( "User not Found"));
		List<LeadSubSourceEntity> subSources = leadSubSourceRepository.findAllByIsDeleteFalseAndUser(userMasterEntity);
		List<LeadSubSourceResponseDto> response = leadSubSourceMapper.entityToResponse(subSources);
		response.stream().forEach(res -> {
			res.setUserId(userId);
		});
		return response;
	}

	@Override
	public Boolean deleteLeadSubSourceById(Long leadSubSourceId) {
		LeadSubSourceEntity entity = leadSubSourceRepository.findByLeadSubSourceIdAndIsDeleteFalse(leadSubSourceId)
				.orElseThrow(() -> new RuntimeException("Lead subsource not found with id : " + leadSubSourceId));

		/*
		 * if (leadMasterRepository.existsByLeadSubSourceAndIsDeleteFalse(entity)) {
		 * throw new
		 * RuntimeException("Lead Sub Source already exists in Lead. Please delete it first."
		 * ); }
		 */
		entity.setIsDelete(true);
		entity = leadSubSourceRepository.save(entity);
		return true;
	}

	@Override
	public LeadSubSourceResponseDto getLeadSubSourceById(Long subSourceId) {
		LeadSubSourceEntity entity = leadSubSourceRepository.findByLeadSubSourceIdAndIsDeleteFalse(subSourceId)
				.orElseThrow(() -> new RuntimeException("Lead subsource not found with id : " + subSourceId));
		LeadSubSourceResponseDto response = leadSubSourceMapper.entityToResponse(entity);
		response.setUserId(entity.getUser().getId());
		return response;
	}

	@Override
	public LeadSourceAllDataResponseDto getAllByLeadSourceId(Long leadSourceId) {
		LeadSourceEntity source = leadSourceRepository.findByLeadSourceIdAndIsDeletedFalse(leadSourceId)
				.orElseThrow(() -> new RuntimeException("Lead source not found with id : " + leadSourceId));

		List<LeadSubSourceEntity> subSources = leadSubSourceRepository.findAllByLeadSourceAndIsDeleteFalse(source);

		LeadSourceAllDataResponseDto response = new LeadSourceAllDataResponseDto();
		response.setSource(leadSourceMapper.entityToResponse(source));
		response.setSubSources(leadSubSourceMapper.toResponse(subSources));

		return response;
	}

}
