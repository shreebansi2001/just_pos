package com.crmportal.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmportal.entity.DecoreMainCategoryItemMasterEntity;
import com.crmportal.entity.DecoreMainCategoryMasterEntity;
import com.crmportal.entity.DecorePackageDetailsEntity;
import com.crmportal.entity.DecorePackageEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.repository.DecoreMainCategoryItemMasterRepository;
import com.crmportal.repository.DecoreMainCategoryMasterRepository;
import com.crmportal.repository.DecorePackageDetailsRepository;
import com.crmportal.repository.DecorePackageRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.request.dto.DecorePackageDetailsRequestDto;
import com.crmportal.request.dto.DecorePackageItemRequestDto;
import com.crmportal.request.dto.DecorePackageRequestDto;
import com.crmportal.response.dto.DecorePackageDetailsResponseDto;
import com.crmportal.response.dto.DecorePackageItemResponseDto;
import com.crmportal.response.dto.DecorePackageResponseDto;
import com.crmportal.service.DecorePackageService;

@Service
public class DecorePackageServiceImpl implements DecorePackageService {

	@Autowired
	private DecorePackageRepository decorePackageRepository;

	@Autowired
	private DecorePackageDetailsRepository decorePackageDetailsRepository;

	@Autowired
	private DecoreMainCategoryMasterRepository decoreMainCategoryRepository;

	@Autowired
	private DecoreMainCategoryItemMasterRepository decoreItemRepository;

	@Autowired
	private UserMasterRepository userMasterRepository;

	@Override
	@Transactional
	public DecorePackageResponseDto addOrUpdateDecorePackage(@Valid DecorePackageRequestDto request) {

		UserMasterEntity user = getUser(request.getUserId());

		DecorePackageEntity entity = (request.getId() == null || request.getId() == 0 || request.getId() == -1)
				? new DecorePackageEntity()
				: getPackageOrThrow(request.getId());

		Optional<DecorePackageEntity> existing = decorePackageRepository
				.findByNameEnglishAndUserAndIsDeleteFalse(request.getNameEnglish(), user);

		if (existing.isPresent() && (request.getId() == null || !existing.get().getId().equals(request.getId()))) {
			throw new RuntimeException("Package name already exists for this user");
		}

		entity.setUser(user);
		entity.setNameEnglish(request.getNameEnglish());
		entity.setNameGujarati(request.getNameGujarati());
		entity.setNameHindi(request.getNameHindi());
		entity.setPrice(request.getPrice());
		entity.setIsActive(true);
		entity.setIsPublished(true);
		if (request.getSequence() != null) {

			shiftSequence(user.getId(), request.getSequence());
			entity.setSequence(request.getSequence());

		} else {

			Integer maxSeq = decorePackageRepository.findMaxSequenceByUserAndIsDeleteFalse(user.getId());

			entity.setSequence(maxSeq == null ? 1 : maxSeq + 1);
		}

		DecorePackageEntity saved = decorePackageRepository.save(entity);

		if (request.getId() != null) {
			decorePackageDetailsRepository.deleteByDecorePackage(saved);
		}

		saveDetails(request, saved, user);

		return mapToResponse(saved);
	}

	private void saveDetails(DecorePackageRequestDto request, DecorePackageEntity saved, UserMasterEntity user) {

		if (request.getDecorePackageDetails() == null)
			return;

		List<DecorePackageDetailsEntity> list = new ArrayList<>();

		for (DecorePackageDetailsRequestDto d : request.getDecorePackageDetails()) {

			DecoreMainCategoryMasterEntity category = decoreMainCategoryRepository.findById(d.getDecoreMainCategoryId())
					.orElseThrow(() -> new RuntimeException("Category not found"));

			if (d.getItems() == null || d.getItems().isEmpty()) {
				DecorePackageDetailsEntity menu = new DecorePackageDetailsEntity();

				menu.setDecorePackage(saved);
				menu.setUser(user);
				menu.setDecoreMainCategory(category);
				menu.setMenuSortOrder(d.getMenuSortOrder());
				menu.setIsActive(true);
				menu.setIsDelete(false);

				list.add(menu);
				continue;
			}

			for (DecorePackageItemRequestDto item : d.getItems()) {

				DecoreMainCategoryItemMasterEntity itemEntity = decoreItemRepository.findById(item.getDecoreItemId())
						.orElseThrow(() -> new RuntimeException("Item not found"));

				DecorePackageDetailsEntity detail = new DecorePackageDetailsEntity();

				detail.setDecorePackage(saved);
				detail.setUser(user);
				detail.setDecoreMainCategory(category);
				detail.setDecoreItem(itemEntity);

				detail.setItemSortOrder(item.getItemSortOrder());
				detail.setPrice(item.getItemPrice());
				detail.setSequence(1);
				detail.setIsActive(true);
				detail.setIsDelete(false);

				list.add(detail);
			}
		}

		decorePackageDetailsRepository.saveAll(list);
	}

	@Override
	public List<DecorePackageResponseDto> getAllByUserId(Long userId, String name, Boolean isActive) {

		UserMasterEntity user = getUser(userId);

		List<DecorePackageEntity> list = decorePackageRepository.findAllByUserAndIsDeleteFalse(user);

		return list.stream().map(this::mapToResponse).collect(Collectors.toList());
	}

	@Override
	public DecorePackageResponseDto getById(Long id) {

		DecorePackageEntity entity = getPackageOrThrow(id);
		return mapToResponse(entity);
	}

	@Override
	@Transactional
	public Boolean deleteById(Long id) {

		DecorePackageEntity entity = getPackageOrThrow(id);

		List<DecorePackageDetailsEntity> details = decorePackageDetailsRepository.findByDecorePackage(entity);

		for (DecorePackageDetailsEntity d : details) {
			d.setIsDelete(true);
		}

		decorePackageDetailsRepository.saveAll(details);

		entity.setIsDelete(true);
		decorePackageRepository.save(entity);

		return true;
	}

	@Override
	public Boolean updateStatus(Long id, Boolean isActive) {

		DecorePackageEntity entity = getPackageOrThrow(id);

		entity.setIsActive(isActive);
		decorePackageRepository.save(entity);

		return true;
	}

	private DecorePackageResponseDto mapToResponse(DecorePackageEntity entity) {

		DecorePackageResponseDto dto = new DecorePackageResponseDto();

		dto.setId(entity.getId());
		dto.setNameEnglish(entity.getNameEnglish());
		dto.setNameGujarati(entity.getNameGujarati());
		dto.setNameHindi(entity.getNameHindi());
		dto.setPrice(entity.getPrice());
		dto.setSequence(entity.getSequence());
		dto.setIsActive(entity.getIsActive());
		dto.setIsPublished(entity.getIsPublished());
		dto.setUserId(entity.getUser().getId());

		List<DecorePackageDetailsEntity> details = decorePackageDetailsRepository
				.findByDecorePackageAndIsDeleteFalse(entity);

		dto.setDecorePackageDetails(mapDetails(details));

		return dto;
	}

	private List<DecorePackageDetailsResponseDto> mapDetails(List<DecorePackageDetailsEntity> list) {

		Map<Long, List<DecorePackageDetailsEntity>> grouped = list.stream()
				.collect(Collectors.groupingBy(d -> d.getDecoreMainCategory().getId()));

		List<DecorePackageDetailsResponseDto> result = new ArrayList<>();

		for (Map.Entry<Long, List<DecorePackageDetailsEntity>> e : grouped.entrySet()) {

			List<DecorePackageDetailsEntity> items = e.getValue();

			DecorePackageDetailsEntity first = items.get(0);

			DecorePackageDetailsResponseDto dto = new DecorePackageDetailsResponseDto();

			dto.setDecoreMainCategoryId(first.getDecoreMainCategory().getId());
			dto.setCategoryName(first.getDecoreMainCategory().getNameEnglish());
			dto.setMenuSortOrder(first.getMenuSortOrder());
			dto.setPrice(first.getPrice());

			List<DecorePackageItemResponseDto> itemDtos = new ArrayList<>();

			for (DecorePackageDetailsEntity i : items) {

				if (i.getDecoreItem() == null)
					continue;

				DecorePackageItemResponseDto itemDto = new DecorePackageItemResponseDto();

				itemDto.setId(i.getId());
				itemDto.setDecoreItemId(i.getDecoreItem().getId());
				itemDto.setItemName(i.getDecoreItem().getNameEnglish());
				itemDto.setItemSortOrder(i.getItemSortOrder());
				itemDto.setItemPrice(i.getPrice());

				itemDtos.add(itemDto);
			}

			dto.setItems(itemDtos);

			result.add(dto);
		}

		return result;
	}

	private UserMasterEntity getUser(Long userId) {
		return userMasterRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
	}

	private DecorePackageEntity getPackageOrThrow(Long id) {
		return decorePackageRepository.findById(id).orElseThrow(() -> new RuntimeException("Package not found"));
	}

	private void shiftSequence(Long userId, Integer sequence) {
		decorePackageRepository.shiftSequencesForUser(userId, sequence);
	}
}