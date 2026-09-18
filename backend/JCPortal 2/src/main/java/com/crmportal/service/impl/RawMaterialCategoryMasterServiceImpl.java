package com.crmportal.service.impl;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmportal.entity.ContactCategoryMasterEntity;
import com.crmportal.entity.CrockeryCutleryEntity;
import com.crmportal.entity.EventMasterEntity;
import com.crmportal.entity.RawMaterialCategoryMasterEntity;
import com.crmportal.entity.RawMaterialCategoryTypeMasterEntity;
import com.crmportal.entity.RawMaterialMasterEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.mapper.RawMaterialCategoryMasterMapper;
import com.crmportal.mapper.UserMasterMapper;
import com.crmportal.repository.CrockeryCutleryRepository;
import com.crmportal.repository.EventMasterRepository;
import com.crmportal.repository.EventRawMaterialRepository;
import com.crmportal.repository.RawMaterialCategoryMasterRepository;
import com.crmportal.repository.RawMaterialCategoryTypeMasterRepository;
import com.crmportal.repository.RawMaterialMasterRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.request.dto.RawMaterialCategoryMasterRequestDto;
import com.crmportal.response.dto.RawMaterialCategoryMasterResponseDto;
import com.crmportal.response.dto.RawMaterialCategoryTypeMasterResponseDto;
import com.crmportal.service.CommonService;
import com.crmportal.service.DatabasePlanningService;
import com.crmportal.service.RawMaterialCategoryMasterService;

@Service
public class RawMaterialCategoryMasterServiceImpl implements RawMaterialCategoryMasterService {

	@Autowired
	UserMasterRepository userMasterRepository;

	@Autowired
	UserMasterMapper userMasterMapper;

	@Autowired
	EventRawMaterialRepository eventRawMaterialRepository;

	@Autowired
	EventRawMaterialServiceImpl eventRawMaterialServiceImpl;

	@Autowired
	RawMaterialCategoryMasterRepository rawMaterialCategoryMasterRepository;

	@Autowired
	RawMaterialCategoryMasterMapper rawMaterialCategoryMasterMapper;

	@Autowired
	RawMaterialCategoryTypeMasterRepository rawMaterialCategoryTypeMasterRepository;

	@Autowired
	CommonService commonService;

	@Autowired
	EventMasterRepository eventMasterRepository;

	@Autowired
	DatabasePlanningService databasePlanningService;

	@Autowired
	CrockeryCutleryRepository crockeryCutleryRepository;

	@Autowired
	RawMaterialMasterRepository rawMaterialMasterRepository;

	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	private static final int CROCKERY_TYPE_ID = 2;

	@Override
	@Transactional
	public RawMaterialCategoryMasterResponseDto addOrUpdateRawMaterialCategory(
			@Valid RawMaterialCategoryMasterRequestDto request, Long id) {

		UserMasterEntity user = getUser(request.getUserId());
		RawMaterialCategoryTypeMasterEntity type = getCategoryType(request.getRawMaterialCatTypeId());

		validateDuplicate(request, user, id);

		RawMaterialCategoryMasterEntity entity = (id == -1) ? createCategory(request, user, type)
				: updateCategory(request, id, user, type);

		entity.setUser(user);
		entity.setRawMaterialCatType(type);

		entity = rawMaterialCategoryMasterRepository.save(entity);

		// 🔥 MAIN LOGIC YOU ASKED
		syncCrockeryData(entity, user, type);

		return rawMaterialCategoryMasterMapper.entityToResponse(entity);
	}

	private void syncCrockeryData(RawMaterialCategoryMasterEntity category, UserMasterEntity user,
			RawMaterialCategoryTypeMasterEntity type) {

		if (!isCrockery(type)) {
// ❌ If not crockery → delete all
			crockeryCutleryRepository.deleteAllByRawMaterialCategory_Id(category.getId());
			return;
		}

// ✅ Get all raw materials of this category
		List<RawMaterialMasterEntity> rawMaterials = rawMaterialMasterRepository
				.findAllByRawMaterialCatAndIsDeleteFalse(category);

		if (rawMaterials.isEmpty())
			return;

		List<CrockeryCutleryEntity> existing = crockeryCutleryRepository
				.findAllByRawMaterialCategoryAndUserAndIsDeleteFalse(category, user);

		Set<Long> existingRawMaterialIds = existing.stream().map(e -> e.getRawMaterial().getId())
				.collect(Collectors.toSet());

		List<CrockeryCutleryEntity> newEntries = rawMaterials.stream()
				.filter(rm -> !existingRawMaterialIds.contains(rm.getId()))
				.map(rm -> mapToCrockeryEntity(rm, user, category)).collect(Collectors.toList());

		if (!newEntries.isEmpty()) {
			crockeryCutleryRepository.saveAll(newEntries);
		}
	}

	private boolean isCrockery(RawMaterialCategoryTypeMasterEntity type) {
		return type != null && type.getId() == CROCKERY_TYPE_ID;
	}

	private CrockeryCutleryEntity mapToCrockeryEntity(RawMaterialMasterEntity rm, UserMasterEntity user,
			RawMaterialCategoryMasterEntity category) {
		CrockeryCutleryEntity entity = new CrockeryCutleryEntity();
		entity.setRawMaterial(rm);
		entity.setUser(user);
		entity.setRawMaterialNameEnglish(rm.getNameEnglish());
		entity.setRawMaterialNameHindi(rm.getNameHindi());
		entity.setRawMaterialNameGujarati(rm.getNameGujarati());
		entity.setRawMaterialCategory(category);
		return entity;
	}

	@Override
	public List<RawMaterialCategoryMasterResponseDto> getAllRawMaterialCategoryByUserId(Long userId,
			Long categoryTypeId, String categoryName, Boolean isActive) {

		UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(userId)
				.orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

		RawMaterialCategoryTypeMasterEntity type = null;
		if (categoryTypeId != null && categoryTypeId != 0) {
			type = rawMaterialCategoryTypeMasterRepository.findByIdAndIsDeleteFalse(categoryTypeId);
			if (type == null) {
				throw new RuntimeException("Raw Material Category Type not found with id: " + categoryTypeId);
			}
		}

		String searchName = (categoryName == null || categoryName.trim().isEmpty()) ? null : categoryName.trim();

		List<RawMaterialCategoryMasterEntity> entities = rawMaterialCategoryMasterRepository.searchByNameOrType(user,
				type, isActive, searchName);

		if (entities.isEmpty()) {
			return Collections.emptyList();
		}

		List<RawMaterialCategoryMasterResponseDto> result = new ArrayList<>();
		entities.forEach(entity -> {
			RawMaterialCategoryMasterResponseDto dto = rawMaterialCategoryMasterMapper.entityToResponse(entity);
			if (entity.getCreatedAt() != null) {
				dto.setCreatedAt(entity.getCreatedAt().format(formatter));
			}
			dto.setUserId(userId);
			result.add(dto);
		});

		return result;
	}

	private UserMasterEntity getUser(Long userId) {
		return userMasterRepository.findByIdAndIsDeleteFalse(userId)
				.orElseThrow(() -> new RuntimeException("User not found"));
	}

	private RawMaterialCategoryTypeMasterEntity getCategoryType(Long id) {
		RawMaterialCategoryTypeMasterEntity type = rawMaterialCategoryTypeMasterRepository.findByIdAndIsDeleteFalse(id);

		if (type == null) {
			throw new RuntimeException("Category type not found");
		}
		return type;
	}

	private void validateDuplicate(RawMaterialCategoryMasterRequestDto request, UserMasterEntity user, Long id) {

		Optional<RawMaterialCategoryMasterEntity> duplicate = rawMaterialCategoryMasterRepository
				.findByNameEnglishAndUserAndIsDeleteFalse(request.getNameEnglish(), user);

		if (duplicate.isPresent() && (id == -1 || !duplicate.get().getId().equals(id))) {
			throw new RuntimeException("Duplicate category name");
		}
	}

	private RawMaterialCategoryMasterEntity createCategory(RawMaterialCategoryMasterRequestDto request,
			UserMasterEntity user, RawMaterialCategoryTypeMasterEntity type) {

		RawMaterialCategoryMasterEntity entity = rawMaterialCategoryMasterMapper.requestToEntity(request);

		entity.setIsActive(true);

		handleSequence(request, user, entity,true);

		entity.setUuid(databasePlanningService.getOrCreateUserUuid(user));

		return entity;
	}

	private RawMaterialCategoryMasterEntity updateCategory(RawMaterialCategoryMasterRequestDto request, Long id,
			UserMasterEntity user, RawMaterialCategoryTypeMasterEntity type) {

		RawMaterialCategoryMasterEntity entity = Optional
				.ofNullable(rawMaterialCategoryMasterRepository.findByIdAndUserAndIsDeleteFalse(id, user))
				.orElseThrow(() -> new RuntimeException("Category not found"));

		handleSequence(request, user, entity,false);

		entity = rawMaterialCategoryMasterMapper.updateEntityFromRequest(request, entity);
		entity.setUpdatedAt(commonService.getCurrentDateTime());

		return entity;
	}

	@Transactional
	private void handleSequence(
	        RawMaterialCategoryMasterRequestDto request,
	        UserMasterEntity user,
	        RawMaterialCategoryMasterEntity entity,
	        boolean isNew) {

	    Integer requestedSequence = request.getSequence();


	    // =====================================================
	    // CASE 1: CREATE NEW RECORD
	    // =====================================================
	    if (isNew) {

	        // Sequence not provided -> Add at last
	        if (requestedSequence == null) {

	            Integer maxSequence =
	                    rawMaterialCategoryMasterRepository
	                            .findMaxSequenceByUserAndIsDeleteFalse(
	                                    user.getId()
	                            );

	            entity.setSequence(
	                    maxSequence == null ? 1 : maxSequence + 1
	            );

	            return;
	        }


	        // Sequence provided -> Shift existing records
	        rawMaterialCategoryMasterRepository.shiftSequencesForInsert(
	                user.getId(),
	                requestedSequence
	        );

	        entity.setSequence(requestedSequence);

	        return;
	    }


	    // =====================================================
	    // CASE 2: UPDATE EXISTING RECORD
	    // =====================================================

	    Integer oldSequence = entity.getSequence();


	    // Sequence not provided -> Keep old sequence
	    if (requestedSequence == null) {
	        return;
	    }


	    // Same sequence -> Do nothing
	    if (oldSequence.equals(requestedSequence)) {
	        return;
	    }


	    // =====================================================
	    // CASE 3: SEQUENCE CHANGED -> SWAP
	    // =====================================================

	    Optional<RawMaterialCategoryMasterEntity> targetOptional =
	            rawMaterialCategoryMasterRepository
	                    .findByUserIdAndSequenceAndIsDeleteFalse(
	                            user.getId(),
	                            requestedSequence
	                    );


	    if (targetOptional.isPresent()) {

	        RawMaterialCategoryMasterEntity targetEntity =
	                targetOptional.get();

	        // Store old sequence
	        Integer tempOldSequence = entity.getSequence();

	        // Swap sequence
	        entity.setSequence(requestedSequence);
	        targetEntity.setSequence(tempOldSequence);

	    } else {

	        // Requested sequence does not exist
	        // Assign directly
	        entity.setSequence(requestedSequence);
	    }
	}

	@Override
	public RawMaterialCategoryMasterResponseDto getRawMaterialCategoryById(Long id) {

		RawMaterialCategoryMasterEntity entity = rawMaterialCategoryMasterRepository.findByIdAndIsDeleteFalse(id)
				.orElseThrow(() -> new RuntimeException("Raw Material Category not found with id: " + id));

		RawMaterialCategoryMasterResponseDto responseDto = rawMaterialCategoryMasterMapper.entityToResponse(entity);
		if (entity.getCreatedAt() != null) {
			responseDto.setCreatedAt(entity.getCreatedAt().format(formatter));
		}
		responseDto.setUserId(entity.getUser().getId());
		return responseDto;
	}

	@Override
	public Boolean deleteRawMaterialCategoryById(Long id) {
		if (rawMaterialCategoryMasterRepository.existsByIdAndIsDeleteFalse(id)) {
			Optional<RawMaterialCategoryMasterEntity> entity = rawMaterialCategoryMasterRepository
					.findByIdAndIsDeleteFalse(id);
			RawMaterialCategoryMasterEntity categoryMasterEntity = entity.get();
			if (rawMaterialMasterRepository.findAllByRawMaterialCatAndIsDeleteFalse(categoryMasterEntity).size() > 0) {

				throw new RuntimeException("Please Delete Raw Material First");
			}
			categoryMasterEntity.setIsDelete(true);
			rawMaterialCategoryMasterRepository.save(categoryMasterEntity);
			crockeryCutleryRepository.deleteAllByRawMaterialCategory_Id(categoryMasterEntity.getId());
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean updateRawMaterialCategoryStatus(Long id, Boolean isActive) {
		if (rawMaterialCategoryMasterRepository.existsByIdAndIsDeleteFalse(id)) {
			Optional<RawMaterialCategoryMasterEntity> entity = rawMaterialCategoryMasterRepository
					.findByIdAndIsDeleteFalse(id);
			RawMaterialCategoryMasterEntity categoryMasterEntity = entity.get();
			categoryMasterEntity.setIsActive(isActive);
			rawMaterialCategoryMasterRepository.save(categoryMasterEntity);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public List<RawMaterialCategoryMasterResponseDto> getRawmaterialCategoryByEventId(Long eventId) {

		EventMasterEntity optEvent = eventMasterRepository.findByIdAndIsDeleteFalse(eventId)
				.orElseThrow(() -> new RuntimeException("Event not found with id: " + eventId));

		List<Object[]> entities = rawMaterialCategoryMasterRepository
				.getRawMaterialCategoryFromRawMaterialByEventIdAndUserId(optEvent.getId());

		if (entities.isEmpty()) {
			System.out.println("Fallback → Allocation");
			entities = rawMaterialCategoryMasterRepository
					.getRawMaterialCategoryFromAllocationByEventId(optEvent.getId());
		}

		if (entities.isEmpty()) {
			System.out.println("Fallback → Event");
			entities = rawMaterialCategoryMasterRepository.getRawMaterialCategoryByEventIdAndUserId(optEvent.getId());
		}

		if (entities.isEmpty()) {
			return Collections.emptyList();
		}
		List<RawMaterialCategoryMasterResponseDto> categoryMasterResponseDtos = new ArrayList<>();
		for (Object[] objects : entities) {
			RawMaterialCategoryMasterResponseDto categoryMasterResponseDto = new RawMaterialCategoryMasterResponseDto(
					Long.valueOf(objects[0].toString()), objects[1] == null ? "" : objects[1].toString(),
					objects[2] == null ? "" : objects[2].toString(), objects[3] == null ? "" : objects[3].toString(),
					objects[4] == null ? null : Integer.valueOf(objects[4].toString()), null, null, null, null, null,
					true);
			eventRawMaterialServiceImpl.saveDefaultIfNotExists(eventId, Long.valueOf(objects[0].toString()));
			categoryMasterResponseDtos.add(categoryMasterResponseDto);
		}
		return categoryMasterResponseDtos;
	}

	@Override
	public List<RawMaterialCategoryMasterResponseDto> getRawmaterialCategoryByTypeId(Long rawMaterialCategoryTypeId,
			Long userId) {

		RawMaterialCategoryTypeMasterEntity categoryTypeMasterEntity = Optional
				.ofNullable(rawMaterialCategoryTypeMasterRepository.findByIdAndIsDeleteFalse(rawMaterialCategoryTypeId))
				.orElseThrow(
						() -> new RuntimeException("Category Type not found with id: " + rawMaterialCategoryTypeId));

		UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(userId)
				.orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

		List<RawMaterialCategoryMasterEntity> entities = rawMaterialCategoryMasterRepository
				.findByRawMaterialCatTypeIdAndUserAndIsDeleteFalse(rawMaterialCategoryTypeId, user);

		if (entities.isEmpty()) {
			return Collections.emptyList();
		}

		List<RawMaterialCategoryMasterResponseDto> categoryMasterResponseDtos = new ArrayList<>();

		for (RawMaterialCategoryMasterEntity entity : entities) {
			RawMaterialCategoryMasterResponseDto categoryMasterResponseDto = rawMaterialCategoryMasterMapper
					.entityToResponse(entity);
			RawMaterialCategoryTypeMasterResponseDto rawMaterialCategoryTypeMasterResponseDto = new RawMaterialCategoryTypeMasterResponseDto();
			rawMaterialCategoryTypeMasterResponseDto.setId(entity.getRawMaterialCatType().getId());
			rawMaterialCategoryTypeMasterResponseDto.setNameEnglish(entity.getRawMaterialCatType().getNameEnglish());
			rawMaterialCategoryTypeMasterResponseDto.setNameHindi(entity.getRawMaterialCatType().getNameHindi());
			rawMaterialCategoryTypeMasterResponseDto.setNameGujarati(entity.getRawMaterialCatType().getNameGujarati());

			categoryMasterResponseDto.setRawMaterialCatType(rawMaterialCategoryTypeMasterResponseDto);
			categoryMasterResponseDtos.add(categoryMasterResponseDto);
		}
		return categoryMasterResponseDtos;
	}

}
