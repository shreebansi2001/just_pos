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
import com.crmportal.entity.MenuCategoryMasterEntity;
import com.crmportal.entity.MenuItemMasterEntity;
import com.crmportal.entity.PartyMasterEntity;
import com.crmportal.entity.StateMasterEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.mapper.ContactCategoryMasterMapper;
import com.crmportal.repository.ContactCategoryMasterRepository;
import com.crmportal.repository.ContactTypeMasterRepository;
import com.crmportal.repository.PartyMasterRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.request.dto.ContactCategoryMasterRequestDto;
import com.crmportal.response.dto.ContactCategoryMasterResponseDto;
import com.crmportal.service.CommonService;
import com.crmportal.service.ContactCategoryMasterService;

@Service
public class ContactCategoryMasterServiceImpl implements ContactCategoryMasterService {

	@Autowired
	ContactCategoryMasterMapper categoryMasterMapper;

	@Autowired
	ContactCategoryMasterRepository categoryMasterRepository;

	@Autowired
	CommonService commonService;

	@Autowired
	ContactTypeMasterRepository contactTypeMasterRepository;

	@Autowired
	UserMasterRepository userMasterRepository;
	
	@Autowired
	PartyMasterRepository partyMasterRepository;

	@Override
	public ContactCategoryMasterResponseDto addOrUpdateContactCategoryMaster(
			@Valid ContactCategoryMasterRequestDto request, long id) {

		ContactCategoryMasterEntity entity = null;

		Optional<UserMasterEntity> userOptional = userMasterRepository.findByIdAndIsDeleteFalse(request.getUserId());
		if (!userOptional.isPresent()) {
			throw new RuntimeException("User not found with id: " + request.getUserId());
		}
		UserMasterEntity user = userOptional.get();

		ContactTypeMasterEntity contactTypeOptional = contactTypeMasterRepository
				.findByIdAndIsDeleteFalse(request.getContcatTypeId());
		if (contactTypeOptional == null) {
			throw new RuntimeException("Contact Type not found with id: " + request.getContcatTypeId());
		}

		Optional<ContactCategoryMasterEntity> existingName = categoryMasterRepository
				.findByNameEnglishAndUserAndIsDeleteFalse(request.getNameEnglish(), user);
		if (id == -1) {
			if (existingName.isPresent()) {
				throw new RuntimeException(
						"Contact Category with the name '" + request.getNameEnglish() + "' already exists.");
			}
			entity = categoryMasterMapper.requestToEntity(request);
			if (request.getSequence() != null) {
				categoryMasterRepository.shiftSequencesForUser(user.getId(), request.getSequence());
				entity.setSequence(request.getSequence());
			} else {
				Integer lastSeq = categoryMasterRepository.findMaxSequenceByUserAndIsDeleteFalse(user.getId());
				entity.setSequence(lastSeq == null ? 1 : lastSeq + 1);
			}
		} else {
			Optional<ContactCategoryMasterEntity> contactCategory = categoryMasterRepository
					.findByIdAndIsDeleteFalse(id);
			if (!contactCategory.isPresent()) {
				throw new RuntimeException("Contact Category not found with id: " + id);
			}

			entity = contactCategory.get();
			if (!entity.getNameEnglish().equalsIgnoreCase(request.getNameEnglish()) && existingName.isPresent()) {
				throw new RuntimeException(
						"Contact Category with the name '" + request.getNameEnglish() + "' already exists.");
			}
			if (request.getSequence() != null) {
				List<ContactCategoryMasterEntity> conflicts = categoryMasterRepository
						.findByUserAndSequenceAndIsDeleteFalse(user, request.getSequence());

				if (!conflicts.isEmpty() && !conflicts.get(0).getId().equals(entity.getId())) {
					categoryMasterRepository.shiftSequencesForUser(user.getId(), request.getSequence());
				}
				entity.setSequence(request.getSequence());
			}
			entity.setNameEnglish(request.getNameEnglish());
			entity.setNameHindi(request.getNameHindi());
			entity.setNameGujarati(request.getNameGujarati());
			entity.setUpdatedAt(commonService.getCurrentDateTime());
		}
		entity.setUser(userOptional.get());
		entity.setContactType(contactTypeOptional);
		entity = categoryMasterRepository.save(entity);
		ContactCategoryMasterResponseDto categoryMasterResponseDto = categoryMasterMapper.entityToResponse(entity);
		return categoryMasterResponseDto;
	}

	@Override
	public List<ContactCategoryMasterResponseDto> getAllContactCategory(String categoryName, Long userId) {
		List<ContactCategoryMasterEntity> entities = new ArrayList<>();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		Optional<UserMasterEntity> userOpt = userMasterRepository.findByIdAndIsDeleteFalse(userId);
		if (!userOpt.isPresent()) {
			return Collections.emptyList();
		}

		UserMasterEntity user = userOpt.get();

		if (categoryName == null || categoryName.trim().isEmpty()) {
			entities = categoryMasterRepository.findAllByUserAndIsDeleteFalse(user);
		} else {
			entities = categoryMasterRepository
					.findByNameEnglishContainingIgnoreCaseAndUserAndIsDeleteFalse(categoryName, user);
		}
		if (entities.isEmpty()) {
			return Collections.emptyList();
		} else {
			List<ContactCategoryMasterResponseDto> categoryMasterResponseDtos = new ArrayList<>();
			for (ContactCategoryMasterEntity entity : entities) {
				ContactCategoryMasterResponseDto responseDto = categoryMasterMapper.entityToResponse(entity);
				responseDto.setCreatedAt(entity.getCreatedAt().format(formatter));
				responseDto.setUserId(userId);
				categoryMasterResponseDtos.add(responseDto);
			}
			return categoryMasterResponseDtos;
		}
	}

	@Override
	public ContactCategoryMasterResponseDto getContactCategoryById(Long id) {
		Optional<ContactCategoryMasterEntity> optional = categoryMasterRepository.findByIdAndIsDeleteFalse(id);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		if (optional.isPresent()) {
			ContactCategoryMasterEntity entity = optional.get();
			ContactCategoryMasterResponseDto responseDto = categoryMasterMapper.entityToResponse(entity);
			responseDto.setCreatedAt(entity.getCreatedAt().format(formatter));
			return responseDto;
		} else {
			return null;
		}
	}

	@Override
	public Boolean deleteContactCategoryById(Long id) {

		if (!categoryMasterRepository.existsByIdAndIsDeleteFalse(id)) {
			return false;
		}
		Optional<ContactCategoryMasterEntity> contactOptional = categoryMasterRepository.findByIdAndIsDeleteFalse(id);
		if (!contactOptional.isPresent()) {
			return false;
		}
		

		ContactCategoryMasterEntity categoryMasterEntity = contactOptional.get();
		if(partyMasterRepository.existsByContactAndIsDeleteFalse(categoryMasterEntity)) {
			throw new RuntimeException("Category already exists in Customer/Vendor. Please delete it first");
		}
		categoryMasterEntity.setIsDelete(true);
		categoryMasterRepository.save(categoryMasterEntity);
		return true;
	}

	@Override
	public List<ContactCategoryMasterResponseDto> getContactCategoryWithSearch(String categoryName) {
		if (categoryName == null || categoryName.trim().isEmpty()) {
			return Collections.emptyList();
		}

		List<ContactCategoryMasterEntity> entities = categoryMasterRepository
				.findByNameEnglishContainingIgnoreCaseAndIsDeleteFalse(categoryName);

		if (entities.isEmpty()) {
			return Collections.emptyList();
		}

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		List<ContactCategoryMasterResponseDto> responseDtos = new ArrayList<>();

		for (ContactCategoryMasterEntity entity : entities) {
			ContactCategoryMasterResponseDto dto = categoryMasterMapper.entityToResponse(entity);

			if (entity.getCreatedAt() != null) {
				dto.setCreatedAt(entity.getCreatedAt().format(formatter));
			}

			responseDtos.add(dto);
		}

		return responseDtos;
	}

	@Override
	public boolean updateContcatCategoryStatus(Long id, Boolean isActive) {
		Optional<ContactCategoryMasterEntity> entityOpt = categoryMasterRepository.findByIdAndIsDeleteFalse(id);
		if (!entityOpt.isPresent()) {
			throw new RuntimeException("Contact Category not found with id: " + id);
		}

		ContactCategoryMasterEntity entity = entityOpt.get();
		entity.setIsActive(isActive);
		categoryMasterRepository.save(entity);
		return true;
	}

	@Override
	public List<ContactCategoryMasterResponseDto> getAllContactCategoryByCatAndUserId(String categoryName, Long userId,
			Long conCatId) {
		List<ContactCategoryMasterEntity> entities = new ArrayList<>();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		Optional<UserMasterEntity> userOpt = userMasterRepository.findByIdAndIsDeleteFalse(userId);
		if (!userOpt.isPresent()) {
			return Collections.emptyList();
		}
		
		ContactTypeMasterEntity contactType = contactTypeMasterRepository.findByIdAndIsDeleteFalse(conCatId);
		if(contactType == null) {
			throw new RuntimeException("Contact Type not found with id: " + conCatId);
		}
		UserMasterEntity user = userOpt.get();

		if (categoryName == null || categoryName.trim().isEmpty()) {
			entities = categoryMasterRepository.findAllByUserAndContactTypeAndIsDeleteFalse(user,contactType);
		} else {
			entities = categoryMasterRepository
					.findByNameEnglishContainingIgnoreCaseAndUserAndContactTypeAndIsDeleteFalse(categoryName, user,contactType);
		}
		if (entities.isEmpty()) {
			return Collections.emptyList();
		} else {
			List<ContactCategoryMasterResponseDto> categoryMasterResponseDtos = new ArrayList<>();
			for (ContactCategoryMasterEntity entity : entities) {
				ContactCategoryMasterResponseDto responseDto = categoryMasterMapper.entityToResponse(entity);
				responseDto.setCreatedAt(entity.getCreatedAt().format(formatter));
				responseDto.setUserId(userId);
				categoryMasterResponseDtos.add(responseDto);
			}
			return categoryMasterResponseDtos;
		}
	}
	
	

}
