package com.crmportal.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.mail.Multipart;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.crmportal.entity.DecoreMainCategoryItemImagesMasterEntity;
import com.crmportal.entity.DecoreMainCategoryItemMasterEntity;
import com.crmportal.entity.DecoreMainCategoryMasterEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.enums.FileType;
import com.crmportal.enums.ModuleName;
import com.crmportal.mapper.DecoreMainCategoryItemMasterMapper;
import com.crmportal.repository.DecoreMainCategoryItemImagesMasterRepository;
import com.crmportal.repository.DecoreMainCategoryItemMasterRepository;
import com.crmportal.repository.DecoreMainCategoryMasterRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.request.dto.DecoreCatItemImagesRequestDto;
import com.crmportal.request.dto.DecoreMainCategoryItemMasterRequestDto;
import com.crmportal.response.dto.DecoreItemImageResponseDto;
import com.crmportal.response.dto.DecoreMainCategoryItemMasterResponseDto;
import com.crmportal.service.CommonService;
import com.crmportal.service.DatabasePlanningService;
import com.crmportal.service.DecoreMainCategoryItemMasterService;
import com.crmportal.service.UserFileService;

@Service
@Transactional
public class DecoreMainCategoryItemMasterServiceImpl implements DecoreMainCategoryItemMasterService {

	@Autowired
	private DecoreMainCategoryItemMasterRepository repository;

	@Autowired
	private UserMasterRepository userMasterRepository;

	@Autowired
	private DecoreMainCategoryMasterRepository categoryRepository;

	@Autowired
	private DecoreMainCategoryItemMasterMapper mapper;

	@Autowired
	private Environment environment;

	@Autowired
	private CommonService commonService;

	@Autowired
	UserFileService userFileService;
	@Autowired
	DatabasePlanningService databasePlanningService;
	@Autowired
	private DecoreMainCategoryItemImagesMasterRepository decoreMainCategoryItemImagesMasterRepository;

	@Override
	@Transactional
	public DecoreMainCategoryItemMasterResponseDto saveDecoreItem(DecoreMainCategoryItemMasterRequestDto request) {

		UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(request.getUserId())
				.orElseThrow(() -> new RuntimeException("User not found with id : " + request.getUserId()));

		DecoreMainCategoryMasterEntity category = categoryRepository
				.findByIdAndIsDeleteFalse(request.getDecoreMainCategoryId()).orElseThrow(() -> new RuntimeException(
						"Decore Category not found with id : " + request.getDecoreMainCategoryId()));

		boolean isCreate = request.getId() == null || request.getId() <= 0;

		validateDuplicateItem(request, user, request.getId());

		DecoreMainCategoryItemMasterEntity entity;

		if (isCreate) {

			entity = new DecoreMainCategoryItemMasterEntity();

			entity.setCreatedAt(commonService.getCurrentDateTime());

			if (request.getSequence() != null) {

				repository.shiftSequencesForCategory(category.getId(), request.getSequence());

				entity.setSequence(request.getSequence());

			} else {

				Integer maxSequence = repository.findMaxSequenceByCategory(category.getId());

				entity.setSequence(maxSequence == null ? 1 : maxSequence + 1);
			}
			entity.setUuid(databasePlanningService.getOrCreateUserUuid(user));
		} else {

			entity = repository.findByIdAndIsDeleteFalse(request.getId())
					.orElseThrow(() -> new RuntimeException("Decore Item not found with id : " + request.getId()));

			Integer oldSequence = entity.getSequence();
			Integer newSequence = request.getSequence();

			if (newSequence != null && !newSequence.equals(oldSequence)) {

				Optional<DecoreMainCategoryItemMasterEntity> conflict = repository
						.findByDecoreMainCategoryAndSequenceAndIsDeleteFalse(category, newSequence);

				if (conflict.isPresent() && !conflict.get().getId().equals(entity.getId())) {

					repository.shiftSequencesForCategory(category.getId(), newSequence);
				}

				entity.setSequence(newSequence);
			}

			entity.setUpdatedAt(commonService.getCurrentDateTime());
		}

		entity.setNameEnglish(request.getNameEnglish());
		entity.setNameGujarati(request.getNameGujarati());
		entity.setNameHindi(request.getNameHindi());

		entity.setInstructionEnglish(request.getInstructionEnglish());
		entity.setInstructionGujarati(request.getInstructionGujarati());
		entity.setInstructionHindi(request.getInstructionHindi());

		entity.setSlogan(request.getSlogan());
		entity.setPrice(request.getPrice() == null ? BigDecimal.ZERO : request.getPrice());

		entity.setUrl(request.getUrl());
		entity.setRemarks(request.getRemarks());

		entity.setUser(user);
		entity.setDecoreMainCategory(category);

		DecoreMainCategoryItemMasterEntity saved = repository.save(entity);
		if (request.getImagesPath() != null && !request.getImagesPath().isEmpty()) {
			for (DecoreCatItemImagesRequestDto file : request.getImagesPath()) {
				try {
					DecoreMainCategoryItemImagesMasterEntity imagesMasterEntity = new DecoreMainCategoryItemImagesMasterEntity();
					imagesMasterEntity.setIsDelete(false);
					imagesMasterEntity.setDecoreItem(saved);
					imagesMasterEntity.setUuid(saved.getUuid());
					imagesMasterEntity.setUser(user);
					imagesMasterEntity.setIsActive(true);
					imagesMasterEntity.setCreatedAt(commonService.getCurrentDateTime());
					imagesMasterEntity = decoreMainCategoryItemImagesMasterRepository.save(imagesMasterEntity);
					userFileService.storeFile(user.getId(), ModuleName.DECOREMAINCATEGORYITEM.toString(),
							imagesMasterEntity.getId(), FileType.IMAGE.toString(), file.getImagePath());

				} catch (Exception e) {

					throw new RuntimeException("Failed to store category image", e);
				}
			}
		}
		return buildResponse(saved);
	}

	@Override
	public Page<DecoreMainCategoryItemMasterResponseDto> getAllDecoreItems(Long userId, String itemName,
			Long categoryId, Boolean isActive, Pageable pageable) {

		Page<DecoreMainCategoryItemMasterEntity> page = repository.search(userId, itemName, categoryId, isActive,
				pageable);

		return page.map(this::buildResponse);
	}

	@Override
	public DecoreMainCategoryItemMasterResponseDto getDecoreItemById(Long id) {

		DecoreMainCategoryItemMasterEntity entity = repository.findByIdAndIsDeleteFalse(id)
				.orElseThrow(() -> new RuntimeException("Decore Item not found with id : " + id));

		return buildResponse(entity);
	}

	@Override
	public Boolean deleteDecoreItemById(Long id) {

		DecoreMainCategoryItemMasterEntity entity = repository.findByIdAndIsDeleteFalse(id)
				.orElseThrow(() -> new RuntimeException("Decore Item not found with id : " + id));

		entity.setIsDelete(true);

		repository.save(entity);

		return true;
	}

	@Override
	public boolean updateDecoreItemStatus(Long id, Boolean isActive) {

		DecoreMainCategoryItemMasterEntity entity = repository.findByIdAndIsDeleteFalse(id)
				.orElseThrow(() -> new RuntimeException("Decore Item not found with id : " + id));

		entity.setIsActive(isActive);

		repository.save(entity);

		return true;
	}

	private DecoreMainCategoryItemMasterResponseDto buildResponse(DecoreMainCategoryItemMasterEntity entity) {

		DecoreMainCategoryItemMasterResponseDto dto = mapper.entityToResponse(entity);

		dto.setUserId(entity.getUser().getId());

		dto.setDecoreMainCategoryId(entity.getDecoreMainCategory().getId());

		dto.setDecoreMainCategoryName(entity.getDecoreMainCategory().getNameEnglish());

		dto.setCreatedAt(commonService.dateTimeFormatted(entity.getCreatedAt()));

		dto.setImages(getImages(entity.getId()));

		return dto;
	}

	private List<DecoreItemImageResponseDto> getImages(Long itemId) {

		return decoreMainCategoryItemImagesMasterRepository.findAllByDecoreItem_IdAndIsDeleteFalse(itemId).stream()
				.map(this::mapImage).collect(Collectors.toList());
	}

	private DecoreItemImageResponseDto mapImage(DecoreMainCategoryItemImagesMasterEntity file) {

		DecoreItemImageResponseDto dto = new DecoreItemImageResponseDto();

		dto.setId(file.getId());

		dto.setImagePath(environment.getProperty("app.image.url") + file.getImagePath());

		return dto;
	}

	private void validateDuplicateItem(DecoreMainCategoryItemMasterRequestDto request, UserMasterEntity user, Long id) {

		List<DecoreMainCategoryItemMasterEntity> existing = repository
				.findByNameEnglishAndUserAndIsDeleteFalse(request.getNameEnglish(), user);

		if (!existing.isEmpty() && (id == null || !existing.get(0).getId().equals(id))) {

			throw new RuntimeException("Decore Item '" + request.getNameEnglish() + "' already exists.");
		}
	}
}