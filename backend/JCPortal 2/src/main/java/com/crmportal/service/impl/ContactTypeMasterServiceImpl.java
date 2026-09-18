package com.crmportal.service.impl;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmportal.entity.ContactCategoryMasterEntity;
import com.crmportal.entity.ContactTypeMasterEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.mapper.ContactTypeMasterMapper;
import com.crmportal.repository.ContactTypeMasterRepository;
import com.crmportal.repository.UserBasicDetailsMasterRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.request.dto.ContactTypeMasterRequestDto;
import com.crmportal.response.dto.ContactTypeMasterResponseDto;
import com.crmportal.service.CommonService;
import com.crmportal.service.ContactTypeMasterService;

@Service
public class ContactTypeMasterServiceImpl implements ContactTypeMasterService {

	@Autowired
	ContactTypeMasterMapper contactTypeMasterMapper;

	@Autowired
	ContactTypeMasterRepository contactTypeMasterRepository;

	@Autowired
	UserMasterRepository userMasterRepository;

	@Autowired
	CommonService commonService;

	@Override
	public ContactTypeMasterResponseDto addOrUpdateContactTypeMaster(@Valid ContactTypeMasterRequestDto request,
			Long id) {

		UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(request.getUserId())
				.orElseThrow(() -> new RuntimeException("User not found with id: " + request.getUserId()));

		Optional<ContactTypeMasterEntity> duplicate = contactTypeMasterRepository
				.findByNameEnglishAndUserAndIsDeleteFalse(request.getNameEnglish(), user);
		if (duplicate.isPresent() && (id == -1 || !duplicate.get().getId().equals(id))) {
			throw new RuntimeException("Contact type already exists with name: " + request.getNameEnglish());
		}
		ContactTypeMasterEntity entity;
		if (id == -1) {
			entity = contactTypeMasterMapper.requestToEntity(request);
			entity.setIsActive(true);
			entity.setCreatedAt(commonService.getCurrentDateTime());
		} else {
			entity = contactTypeMasterRepository.findByIdAndUserAndIsDeleteFalse(id, user);
			if (entity == null) {
				throw new RuntimeException("Contact Type not found with id: " + id);
			}
			entity = contactTypeMasterMapper.updateEntityFromRequest(request, entity);
			entity.setUpdatedAt(commonService.getCurrentDateTime());
		}

		entity.setUser(user);
		entity = contactTypeMasterRepository.save(entity);
		return contactTypeMasterMapper.entityToResponse(entity);
	}

	@Override
	public List<ContactTypeMasterResponseDto> getAllContactTypeByUserId(Long userId,String contactTypeName, Boolean isActive) {
		UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(userId)
				.orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
		
		List<ContactTypeMasterEntity> entities = new ArrayList<>();
		   if (contactTypeName == null || contactTypeName.trim().isEmpty()) {
		        entities = (isActive == null)
		                ? contactTypeMasterRepository.findAllByUserAndIsDeleteFalse(user)
		                : contactTypeMasterRepository.findAllByUserAndIsDeleteFalseAndIsActive(user, isActive);
		    } else {
		        entities = (isActive == null)
		                ? contactTypeMasterRepository.findByNameEnglishContainingIgnoreCaseAndUserAndIsDeleteFalse(contactTypeName, user)
		                : contactTypeMasterRepository.findByNameEnglishContainingIgnoreCaseAndUserAndIsDeleteFalseAndIsActive(contactTypeName, user, isActive);
		    }
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		List<ContactTypeMasterResponseDto> responseDtos = new ArrayList<>();
		if (entities.isEmpty()) {
			return Collections.emptyList();
		} else {
			for (ContactTypeMasterEntity contactTypeMasterEntity : entities) {
				ContactTypeMasterResponseDto responseDto = contactTypeMasterMapper
						.entityToResponse(contactTypeMasterEntity);
				responseDto.setCreatedAt(contactTypeMasterEntity.getCreatedAt().format(formatter));
				responseDtos.add(responseDto);
			}
			return responseDtos;
		}
	}

	@Override
	public ContactTypeMasterResponseDto getContactTypeById(Long id) {

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		ContactTypeMasterEntity contactTypeMasterEntity = contactTypeMasterRepository.findByIdAndIsDeleteFalse(id);
		if (contactTypeMasterEntity == null) {
			return null;
		} else {

			ContactTypeMasterResponseDto responseDto = contactTypeMasterMapper
					.entityToResponse(contactTypeMasterEntity);
			responseDto.setCreatedAt(contactTypeMasterEntity.getCreatedAt().format(formatter));
			return responseDto;
		}
	}

	@Override
	public Boolean deleteContactTypeById(Long id) {
		if(contactTypeMasterRepository.existsByIdAndIsDeleteFalse(id)) {
			ContactTypeMasterEntity entity = contactTypeMasterRepository.findByIdAndIsDeleteFalse(id);
			entity.setIsDelete(true);
			contactTypeMasterRepository.save(entity);
			return true;
		}else {
			return false;
		}
	}

	@Override
	public boolean updateContcatTypeStatus(Long id, Boolean isActive) {
		if(contactTypeMasterRepository.existsByIdAndIsDeleteFalse(id)) {
			ContactTypeMasterEntity entity = contactTypeMasterRepository.findByIdAndIsDeleteFalse(id);
			entity.setIsActive(isActive);
			contactTypeMasterRepository.save(entity);
			return true;
		}else {
			return false;
		}
	}

}
