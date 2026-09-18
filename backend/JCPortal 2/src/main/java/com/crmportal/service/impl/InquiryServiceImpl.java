package com.crmportal.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.management.RuntimeErrorException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.crmportal.controller.InquiryController;
import com.crmportal.entity.InquiryEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.repository.InquiryRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.request.dto.InquiryRequestDto;
import com.crmportal.response.dto.InquiryResponseDto;
import com.crmportal.service.CommonService;
import com.crmportal.service.InquiryService;

@Service
public class InquiryServiceImpl implements InquiryService {

	@Autowired
	InquiryRepository inquiryRepository;
	
	@Autowired
	UserMasterRepository userMasterRepository;
	
	@Autowired
	CommonService commonService;
	
	@Override
	public InquiryResponseDto addOrUpdateInquiry(InquiryRequestDto request) {
		
		UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(request.getUserId())
				.orElseThrow(() -> new RuntimeException("User Not Found"));
		
		InquiryEntity entity = null;

		if(request.getId() != -1) {
			entity = inquiryRepository.findByIdAndIsDeleteFalse(request.getId())
					.orElseThrow(() -> new RuntimeException("Inquiry Not Foune"));
			
			entity = mapToEntity(request, entity, user);
		} else {	
			entity = new InquiryEntity();
			entity = mapToEntity(request, entity, user);
		}

		entity = inquiryRepository.save(entity);
		
		return mapToResponse(entity);
	}
	

	@Override
	public List<InquiryResponseDto> getAllInquiry(Long userId, String startDate, String endDate) {
		
		List<InquiryEntity> entities = null;
		
		if(startDate != null && endDate != null) {			
			LocalDateTime formattedStartDate = commonService.dateFormatted(startDate).atStartOfDay();
			LocalDateTime formattedEndDate = commonService.dateFormatted(endDate).atTime(23, 59, 59);
			
			entities = inquiryRepository.findByUserIdAndIsDeleteFalseAndInquiryDateBetween(userId, formattedStartDate, formattedEndDate);
		} else {
			entities = inquiryRepository.findByUserIdAndIsDeleteFalse(userId);
		}

		
		return entities.stream().map(this::mapToResponse).collect(Collectors.toList());
	}
	

	@Override
	public List<InquiryResponseDto> getInquiryById(Long userId, Long id) {
		
		List<InquiryEntity> entities = inquiryRepository.findByIdAndUserIdAndIsDeleteFalse(id, userId);
		
		return entities.stream().map(this::mapToResponse).collect(Collectors.toList());
	}
	
	public InquiryEntity mapToEntity(InquiryRequestDto dto, InquiryEntity entity, UserMasterEntity user) {
		
		LocalDateTime formatedDate = commonService.dateTimeFormatted(dto.getInquiryDate());
		
		entity.setGuestName(dto.getGuestName());
		entity.setGuestAddress(dto.getGuestAddress());
		entity.setEmailId(dto.getEmailId());
		entity.setFunctionName(dto.getFunction());
		entity.setMobileNo(dto.getMobileNo());
		entity.setReferralSource(dto.getReferralSource());
		entity.setTentativeDate(dto.getTentativeDate());
		entity.setInquiryDate(formatedDate);
		entity.setUser(user);
		
		return entity;
	}
		
	public InquiryResponseDto mapToResponse(InquiryEntity entity) {
		InquiryResponseDto responseDto = new InquiryResponseDto();
		String inquiryDate = commonService.dateTimeFormatted(entity.getInquiryDate());
		
		responseDto.setEmailId(entity.getEmailId());
		responseDto.setFunction(entity.getFunctionName());
		responseDto.setGuestName(entity.getGuestName());
		responseDto.setGuestAddress(entity.getGuestAddress());
		responseDto.setId(entity.getId());
		responseDto.setInquiryDate(inquiryDate);
		responseDto.setMobileNo(entity.getMobileNo());
		responseDto.setReferralSource(entity.getReferralSource());
		responseDto.setTentativeDate(entity.getTentativeDate());
		responseDto.setUserId(entity.getUser().getId());
		
		return responseDto;
	}

	@Override
	public Boolean deleteInquiryById(Long id, Long userId) {
		
		InquiryEntity entity = inquiryRepository.findByIdAndIsDeleteFalse(id)
				.orElseThrow(() -> new RuntimeException("Inquiry Not Foune"));
		
		entity.setIsDelete(true);
		inquiryRepository.save(entity);
		
		return true;
	}

}
