package com.crmportal.service.impl;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.crmportal.entity.DecoreMainCategoryItemImagesMasterEntity;
import com.crmportal.entity.DecoreMainCategoryMasterEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.enums.FileType;
import com.crmportal.enums.ModuleName;
import com.crmportal.mapper.DecoreMainCategoryMasterMapper;
import com.crmportal.repository.DecoreMainCategoryMasterRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.request.dto.DecoreMainCategoryMasterRequestDto;
import com.crmportal.response.dto.DecoreMainCategoryMasterResponseDto;
import com.crmportal.service.CommonService;
import com.crmportal.service.DatabasePlanningService;
import com.crmportal.service.DecoreMainCategoryMasterService;
import com.crmportal.service.UserFileService;

@Service
public class DecoreMainCategoryMasterServiceImpl implements DecoreMainCategoryMasterService {

	@Autowired
	private UserMasterRepository userMasterRepository;

	@Autowired
	private DecoreMainCategoryMasterRepository decoreMainCategoryMasterRepository;

	@Autowired
	private DecoreMainCategoryMasterMapper decoreMainCategoryMasterMapper;

	@Autowired
	private CommonService commonService;

	@Autowired
	private DatabasePlanningService databasePlanningService;

	@Autowired
	private UserFileService userFileService;

	@Autowired
	private Environment environment;

	@Override
	@Transactional
	public DecoreMainCategoryMasterResponseDto addOrUpdateDecoreMainCategory(
			@Valid DecoreMainCategoryMasterRequestDto request, long id,MultipartFile file) {

		boolean isCreate = (id <= 0);

		UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(request.getUserId())
				.orElseThrow(() -> new RuntimeException("User not found with id : " + request.getUserId()));

		Optional<DecoreMainCategoryMasterEntity> existingCategory = decoreMainCategoryMasterRepository
				.findByUserAndNameEnglishIgnoreCaseAndIsDeleteFalse(user, request.getNameEnglish());

		if (existingCategory.isPresent() && (isCreate || !existingCategory.get().getId().equals(id))) {

			throw new RuntimeException("Category already exists with name : " + request.getNameEnglish());
		}

		DecoreMainCategoryMasterEntity entity;

		if (isCreate) {

			entity = decoreMainCategoryMasterMapper.requestToEntity(request);

			entity.setUser(user);
			entity.setUuid(databasePlanningService.getOrCreateUserUuid(user));
			entity.setPrice(request.getPrice() == null ? BigDecimal.ZERO : request.getPrice());

			if (request.getSequence() != null) {

				decoreMainCategoryMasterRepository.shiftSequencesForUser(user.getId(), request.getSequence());

				entity.setSequence(request.getSequence());

			} else {

				Integer maxSequence = decoreMainCategoryMasterRepository
						.findMaxSequenceByUserAndIsDeleteFalse(user.getId());

				entity.setSequence(maxSequence == null ? 1 : maxSequence + 1);
			}

		} else {

			entity = decoreMainCategoryMasterRepository.findByIdAndUserAndIsDeleteFalse(id, user)
					.orElseThrow(() -> new RuntimeException("Category not found with id : " + id));

			Integer oldSequence = entity.getSequence();
			Integer newSequence = request.getSequence();

			decoreMainCategoryMasterMapper.updateEntityFromRequest(request, entity);

			entity.setPrice(request.getPrice() == null ? BigDecimal.ZERO : request.getPrice());

			if (newSequence != null && !newSequence.equals(oldSequence)) {

				Optional<DecoreMainCategoryMasterEntity> conflict = decoreMainCategoryMasterRepository
						.findByUserAndSequenceAndIsDeleteFalse(user, newSequence);

				if (conflict.isPresent() && !conflict.get().getId().equals(entity.getId())) {

					decoreMainCategoryMasterRepository.shiftSequencesForUser(user.getId(), newSequence);
				}

				entity.setSequence(newSequence);
			}

			entity.setUpdatedAt(commonService.getCurrentDateTime());
		}

		DecoreMainCategoryMasterEntity saved = decoreMainCategoryMasterRepository.save(entity);

		if (file != null && !file.isEmpty()) {

			try {

				userFileService.storeFile(user.getId(), ModuleName.DECOREMAINCATEGORY.toString(), saved.getId(),
						FileType.IMAGE.toString(), file);

			} catch (Exception e) {

				throw new RuntimeException("Failed to store category image", e);
			}
		}

		return decoreMainCategoryMasterMapper.entityToResponse(saved);
	}

	@Override
	public List<DecoreMainCategoryMasterResponseDto> getAllDecoreMainCategoryByUserId(Long userId, String categoryName,
			Boolean isActive) {

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		Optional<UserMasterEntity> userOpt = userMasterRepository.findByIdAndIsDeleteFalse(userId);

		if (!userOpt.isPresent()) {
			return Collections.emptyList();
		}

		UserMasterEntity user = userOpt.get();

		List<DecoreMainCategoryMasterEntity> entities;

		if (categoryName == null || categoryName.trim().isEmpty()) {

			if (isActive == null) {
				entities = decoreMainCategoryMasterRepository.findAllByUserAndIsDeleteFalseOrderBySequenceAsc(user);
			} else {
				entities = decoreMainCategoryMasterRepository
						.findAllByUserAndIsDeleteFalseAndIsActiveOrderBySequenceAsc(user, isActive);
			}

		} else {

			if (isActive == null) {
				entities = decoreMainCategoryMasterRepository
						.findByNameEnglishContainingIgnoreCaseAndUserAndIsDeleteFalseOrderBySequenceAsc(categoryName,
								user);
			} else {
				entities = decoreMainCategoryMasterRepository
						.findByNameEnglishContainingIgnoreCaseAndUserAndIsDeleteFalseAndIsActiveOrderBySequenceAsc(
								categoryName, user, isActive);
			}
		}

		List<DecoreMainCategoryMasterResponseDto> response = new ArrayList<>();

		for (DecoreMainCategoryMasterEntity entity : entities) {

			DecoreMainCategoryMasterResponseDto dto = decoreMainCategoryMasterMapper.entityToResponse(entity);
			dto.setImagePath(environment.getProperty("app.image.url") + entity.getImagePath());
			dto.setUserId(userId);

			if (entity.getCreatedAt() != null) {
				dto.setCreatedAt(entity.getCreatedAt().format(formatter));
			}

			response.add(dto);
		}

		return response;
	}

	@Override
	public DecoreMainCategoryMasterResponseDto getDecoreMainCategoryById(Long id) {

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		Optional<DecoreMainCategoryMasterEntity> entityOpt = decoreMainCategoryMasterRepository
				.findByIdAndIsDeleteFalse(id);

		if (!entityOpt.isPresent()) {
			return null;
		}

		DecoreMainCategoryMasterEntity entity = entityOpt.get();

		DecoreMainCategoryMasterResponseDto dto = decoreMainCategoryMasterMapper.entityToResponse(entity);
		dto.setImagePath(environment.getProperty("app.image.url") + entity.getImagePath());
		dto.setUserId(entity.getUser().getId());

		if (entity.getCreatedAt() != null) {
			dto.setCreatedAt(entity.getCreatedAt().format(formatter));
		}

		return dto;
	}

	@Override
	public Boolean deleteDecoreMainCategoryById(Long id) {

		DecoreMainCategoryMasterEntity entity = decoreMainCategoryMasterRepository.findByIdAndIsDeleteFalse(id)
				.orElseThrow(() -> new RuntimeException("Category not found"));

		entity.setIsDelete(true);

		decoreMainCategoryMasterRepository.save(entity);

		return true;
	}

	@Override
	public boolean updateDecoreMainCategoryStatus(Long id, Boolean isActive) {

		DecoreMainCategoryMasterEntity entity = decoreMainCategoryMasterRepository.findByIdAndIsDeleteFalse(id)
				.orElseThrow(() -> new RuntimeException("Category not found"));

		entity.setIsActive(isActive);

		decoreMainCategoryMasterRepository.save(entity);

		return true;
	}

}
