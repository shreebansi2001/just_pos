package com.crmportal.service.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.crmportal.entity.DecoreMainCategoryItemImagesMasterEntity;
import com.crmportal.entity.DecoreMainCategoryItemMasterEntity;
import com.crmportal.entity.DecoreMainCategoryMasterEntity;
import com.crmportal.entity.DecorePackageEntity;
import com.crmportal.entity.DecorePreparationDetailsEntity;
import com.crmportal.entity.DecorePreparationEntity;
import com.crmportal.entity.EventFunctionDecorItemImagesEntity;
import com.crmportal.entity.EventFunctionMasterEntity;
import com.crmportal.enums.FileType;
import com.crmportal.enums.ModuleName;
import com.crmportal.mapper.DecorePreparationDetailsMapper;
import com.crmportal.mapper.EventFunctionMasterMapper;
import com.crmportal.repository.DecoreMainCategoryItemImagesMasterRepository;
import com.crmportal.repository.DecoreMainCategoryItemMasterRepository;
import com.crmportal.repository.DecoreMainCategoryMasterRepository;
import com.crmportal.repository.DecorePackageRepository;
import com.crmportal.repository.DecorePreparationDetailsRepository;
import com.crmportal.repository.DecorePreparationRepository;
import com.crmportal.repository.EventFunctionDecorItemImagesRepository;
import com.crmportal.repository.EventFunctionMasterRepository;
import com.crmportal.repository.PartyMasterRepository;
import com.crmportal.request.dto.DecorePreparationRequestDto;
import com.crmportal.request.dto.EventFunctionDecorItemImagesRequestDto;
import com.crmportal.response.dto.DecoreCategoryForPreparationResponseDto;
import com.crmportal.response.dto.DecorePreparationCombResponseDto;
import com.crmportal.response.dto.DecorePreparationDetailsResponseDto;
import com.crmportal.response.dto.DecorePreparationItemResponseDto;
import com.crmportal.response.dto.DecorePreparationResponseDto;
import com.crmportal.response.dto.DecorePreparationSelectedItemDetailsResponseDto;
import com.crmportal.response.dto.EventFunctionMasterResponseDto;
import com.crmportal.response.dto.EventFunctionMenuPreparationResponseDto;
import com.crmportal.service.DecorePreparationService;
import com.crmportal.service.UserFileService;

@Service
@Transactional
public class DecorePreparationServiceImpl implements DecorePreparationService {

	@Autowired
	private DecorePreparationRepository decorePreparationRepository;

	@Autowired
	private DecorePreparationDetailsRepository decorePreparationDetailsRepository;

	@Autowired
	private EventFunctionMasterRepository eventFunctionMasterRepository;

	@Autowired
	private DecorePackageRepository decorePackageRepository;

	@Autowired
	private DecoreMainCategoryMasterRepository decoreMainCategoryMasterRepository;

	@Autowired
	EventFunctionMasterMapper eventFunctionMasterMapper;

	@Autowired
	private DecoreMainCategoryItemMasterRepository decoreItemMasterRepository;

	@Autowired
	private DecorePreparationDetailsMapper decorePreparationDetailsMapper;

	@Autowired
	Environment environment;

	@Autowired
	UserFileService userFileService;

	@Autowired
	private DecoreMainCategoryItemImagesMasterRepository decoreMainCategoryItemImagesMasterRepository;

	@Autowired
	EventFunctionDecorItemImagesRepository eventFunctionDecorItemImagesRepository;

	@Autowired
	PartyMasterRepository partyMasterRepository;

	@Override
	@Transactional
	public DecorePreparationResponseDto addOrUpdateDecorePreparation(@Valid DecorePreparationRequestDto request) {

		EventFunctionMasterEntity eventFunction = eventFunctionMasterRepository
				.findByIdAndIsDeleteFalse(request.getEventFunctionId())
				.orElseThrow(() -> new RuntimeException("Event Function not found"));

		DecorePreparationEntity entity;

		if (request.getId() != null && request.getId() != 0) {
			entity = decorePreparationRepository.findByIdAndIsDeleteFalse(request.getId())
					.orElseThrow(() -> new RuntimeException("Decore Preparation not found"));
		} else {
			entity = new DecorePreparationEntity();
			Integer max = decorePreparationRepository.findMaxSortOrder(eventFunction.getId());
			entity.setSortorder(max != null ? max + 1 : 1);
		}

		DecorePackageEntity decorePackage = null;

		if (Boolean.TRUE.equals(request.getIsPackage())) {
			decorePackage = decorePackageRepository.findByIdAndIsDeleteFalse(request.getPackageId());

			entity.setDecorePackage(decorePackage);
			entity.setPackageName(request.getPackageName());
			entity.setPackagePrice(request.getPackagePrice());
		} else {
			entity.setDecorePackage(null);
			entity.setPackageName("");
			entity.setPackagePrice(BigDecimal.ZERO);
		}

		entity.setEventFunction(eventFunction);
		entity.setPax(request.getPax());
		entity.setPrice(request.getPrice());
		entity.setDefaultPrice(request.getDefaultPrice());
		entity.setIsPackage(request.getIsPackage());

		entity = decorePreparationRepository.save(entity);

		decorePreparationDetailsRepository.deleteAllByDecorePreparation(entity);

		List<DecorePreparationSelectedItemDetailsResponseDto> categoryList = new ArrayList<>();

		if (request.getSelectedItemDetails() != null) {

			for (DecorePreparationSelectedItemDetailsResponseDto catDto : request.getSelectedItemDetails()) {

				DecoreMainCategoryMasterEntity category = decoreMainCategoryMasterRepository
						.findByIdAndIsDeleteFalse(catDto.getDecoreCategoryId())
						.orElseThrow(() -> new RuntimeException("Category not found"));

				List<DecorePreparationDetailsResponseDto> itemList = new ArrayList<>();

				if (catDto.getSelectedItems() != null) {

					for (DecorePreparationDetailsResponseDto itemDto : catDto.getSelectedItems()) {

						DecoreMainCategoryItemMasterEntity item = decoreItemMasterRepository
								.findByIdAndIsDeleteFalse(itemDto.getDecoreItemId())
								.orElseThrow(() -> new RuntimeException("Item not found"));

						DecorePreparationDetailsEntity detail = new DecorePreparationDetailsEntity();

						detail.setDecorePreparation(entity);
						detail.setDecoreMainCategory(category);
						detail.setDecoreItem(item);

						detail.setDecoreItemName(itemDto.getDecoreItemName());
						detail.setDecoreItemSortOrder(itemDto.getDecoreItemSortOrder());
						detail.setDecoreItemPrice(itemDto.getDecoreItemPrice());
						detail.setDecoreItemNotes(itemDto.getDecoreItemNotes());
						detail.setDecoreItemSlogan(itemDto.getDecoreItemSlogan());

						detail.setDecoreItemNameHindi(itemDto.getDecoreItemNameHindi());
						detail.setDecoreItemNameGujarati(itemDto.getDecoreItemNameGujarati());

						detail.setDecoreItemNotesHindi(itemDto.getDecoreItemNotesHindi());
						detail.setDecoreItemNotesGujarati(itemDto.getDecoreItemNotesGujarati());

						detail.setItemSpace(itemDto.getItemSpace());

						detail.setSubItem(itemDto.getSubItem());
						detail.setSubItemHindi(itemDto.getSubItemHindi());
						detail.setSubItemGujarati(itemDto.getSubItemGujarati());

						detail.setIsDecoreItemAddons(itemDto.getIsDecoreItemAddons());

						detail.setDecoreCategoryName(catDto.getDecoreCategoryName());
						detail.setDecoreCatSortOrder(catDto.getDecoreCatSortOrder());
						detail.setDecoreCatNotes(catDto.getDecoreCatNotes());

						detail.setDecoreCategoryNameHindi(catDto.getDecoreCategoryNameHindi());
						detail.setDecoreCategoryNameGujarati(catDto.getDecoreCategoryNameGujarati());

						detail.setDecoreCatNotesHindi(catDto.getDecoreCatNotesHindi());
						detail.setDecoreCatNotesGujarati(catDto.getDecoreCatNotesGujarati());

						detail.setStartTime(catDto.getStartTime());

						detail.setAnyItem(catDto.getAnyItem());
						detail.setSubCat(catDto.getSubCat());
						detail.setSubCatHindi(catDto.getSubCatHindi());
						detail.setSubCatGujarati(catDto.getSubCatGujarati());

						detail.setIsDecoreCatAddons(catDto.getIsDecoreCatAddons());

						detail.setBgImgId(catDto.getBgImgId());
						detail.setCatImgId(catDto.getCatImgId());
						detail.setCatSpace(catDto.getCatSpace());
						detail.setItemQty(itemDto.getItemQty());
						detail.setVendorId(itemDto.getVendorId());
						detail = decorePreparationDetailsRepository.save(detail);

						itemList.add(decorePreparationDetailsMapper.entityToResponse(detail));
					}
				}

				DecorePreparationSelectedItemDetailsResponseDto catResponse = new DecorePreparationSelectedItemDetailsResponseDto();

				catResponse.setDecoreCategoryId(category.getId());
				catResponse.setDecoreCategoryName(catDto.getDecoreCategoryName());

				catResponse.setDecoreCatSortOrder(catDto.getDecoreCatSortOrder());
				catResponse.setDecoreCatNotes(catDto.getDecoreCatNotes());

				catResponse.setDecoreCategoryNameHindi(catDto.getDecoreCategoryNameHindi());
				catResponse.setDecoreCategoryNameGujarati(catDto.getDecoreCategoryNameGujarati());

				catResponse.setDecoreCatNotesHindi(catDto.getDecoreCatNotesHindi());
				catResponse.setDecoreCatNotesGujarati(catDto.getDecoreCatNotesGujarati());

				catResponse.setStartTime(catDto.getStartTime());

				catResponse.setCatImgId(catDto.getCatImgId());
				catResponse.setBgImgId(catDto.getBgImgId());
				catResponse.setCatSpace(catDto.getCatSpace());

				catResponse.setAnyItem(catDto.getAnyItem());
				catResponse.setSubCat(catDto.getSubCat());
				catResponse.setSubCatHindi(catDto.getSubCatHindi());
				catResponse.setSubCatGujarati(catDto.getSubCatGujarati());

				catResponse.setIsDecoreCatAddons(catDto.getIsDecoreCatAddons());

				catResponse.setSelectedItems(itemList);

				categoryList.add(catResponse);
			}
		}

		DecorePreparationResponseDto response = new DecorePreparationResponseDto();

		response.setId(entity.getId());
		response.setPax(entity.getPax());
		response.setPrice(entity.getPrice());
		response.setDefaultPrice(entity.getDefaultPrice());
		response.setSortorder(entity.getSortorder());
		response.setIsPackage(entity.getIsPackage());

		response.setPackageId(entity.getDecorePackage() != null ? entity.getDecorePackage().getId() : null);

		response.setPackageName(entity.getPackageName());
		response.setPackagePrice(entity.getPackagePrice());

		response.setSelectedDecorePreparation(categoryList);

		return response;
	}

	@Override
	@Transactional
	public DecorePreparationCombResponseDto getDecorePreparationItems(Integer pageNo, Integer totalRecord,
			Long decoreCategoryId, Long eventFunctionId, String itemName, Long userId) {

		EventFunctionMasterEntity eventFunction = eventFunctionMasterRepository.findById(eventFunctionId)
				.orElseThrow(() -> new RuntimeException("Event Function not found with id: " + eventFunctionId));

		EventFunctionMasterResponseDto eventFunctionDto = eventFunctionMasterMapper.entityToResponse(eventFunction);

		DecorePreparationEntity decorePreparationEntity = decorePreparationRepository
				.findByEventFunctionAndIsDeleteFalse(eventFunction);

		List<DecorePreparationItemResponseDto> allItems = convertToDecoreItemList(decorePreparationRepository
				.getAllDecorePreparationItemsNative(eventFunctionId, userId, itemName, decoreCategoryId));

		PaginationResult<DecorePreparationItemResponseDto> pagination = paginateItems(allItems, pageNo, totalRecord);

		List<DecorePreparationSelectedItemDetailsResponseDto> selectedItems = loadSelectedDecoreItems(eventFunctionId,
				decorePreparationEntity, eventFunction);

		EventFunctionMenuPreparationResponseDto eventFnDto = new EventFunctionMenuPreparationResponseDto();

		eventFnDto.setEventId(eventFunction.getEvent().getId());
		eventFnDto.setFuncId(eventFunctionDto.getFunction().getId());
		eventFnDto.setFunction_venue(eventFunctionDto.getFunction_venue());
		eventFnDto.setFunctionStartDateTime(eventFunctionDto.getFunctionStartDateTime());
		eventFnDto.setFunctionEndDateTime(eventFunctionDto.getFunctionEndDateTime());
		eventFnDto.setId(eventFunctionDto.getId());
		eventFnDto.setNameEnglish(eventFunctionDto.getFunction().getNameEnglish());
		eventFnDto.setNameHindi(eventFunctionDto.getFunction().getNameHindi());
		eventFnDto.setNameGujarati(eventFunctionDto.getFunction().getNameGujarati());
		eventFnDto.setPax(eventFunctionDto.getPax());
		eventFnDto.setRate(eventFunctionDto.getRate());

		if (eventFunction.getCustomPackage() != null) {
			eventFnDto.setCustomPackageId(eventFunction.getCustomPackage().getId());
			eventFnDto.setCustomPackageName(eventFunction.getCustomPackage().getNameEnglish()); // ← nameEnglish
			eventFnDto.setIsPackage(true);
		} else {
			eventFnDto.setCustomPackageId(null);
			eventFnDto.setCustomPackageName(null);
			eventFnDto.setIsPackage(false);
		}
		DecorePreparationResponseDto decorePrep = buildDecorePreparationResponse(eventFnDto, decorePreparationEntity,
				allItems.size(), pagination.totalPage);

		DecorePreparationCombResponseDto response = new DecorePreparationCombResponseDto();

		response.setDecorePreparation(decorePrep);
		response.setDecorePreparationItems(pagination.paginatedList);
		response.setSelectedDecorePreparationItems(selectedItems);

		response.setCustomPackageDetails(Collections.emptyList());

		return response;
	}

	private DecorePreparationResponseDto buildDecorePreparationResponse(
			EventFunctionMenuPreparationResponseDto eventFunctionDto, DecorePreparationEntity entity, long totalItem,
			int totalPage) {

		DecorePreparationResponseDto response = new DecorePreparationResponseDto();

		response.setEventFunction(eventFunctionDto);
		response.setTotalItem(totalItem);
		response.setTotalPage(totalPage);

		if (entity == null) {

			response.setId(null);
			response.setDefaultPrice(BigDecimal.ZERO);
			response.setPax(0);
			response.setSortorder(0);
			response.setPrice(BigDecimal.ZERO);

			if (Boolean.TRUE.equals(eventFunctionDto.getIsPackage()) && eventFunctionDto.getCustomPackageId() != null) {

				response.setIsPackage(true);
				response.setPackageId(eventFunctionDto.getCustomPackageId());
				response.setPackageName(
						eventFunctionDto.getCustomPackageName() != null ? eventFunctionDto.getCustomPackageName() : "");
				response.setPackagePrice(BigDecimal.ZERO);

			} else {
				response.setIsPackage(false);
				response.setPackageId(null);
				response.setPackageName("");
				response.setPackagePrice(BigDecimal.ZERO);
			}

			return response;
		}

		response.setId(entity.getId());
		response.setDefaultPrice(entity.getDefaultPrice());
		response.setPax(entity.getPax());
		response.setSortorder(entity.getSortorder());
		response.setPrice(entity.getPrice());

		if (Boolean.TRUE.equals(entity.getIsPackage()) && entity.getDecorePackage() != null) {

			response.setIsPackage(true);
			response.setPackageId(entity.getDecorePackage().getId());
			response.setPackageName(entity.getPackageName());
			response.setPackagePrice(entity.getPackagePrice());

		} else if (Boolean.TRUE.equals(eventFunctionDto.getIsPackage())
				&& eventFunctionDto.getCustomPackageId() != null) {

			response.setIsPackage(true);
			response.setPackageId(eventFunctionDto.getCustomPackageId());
			response.setPackageName(
					eventFunctionDto.getCustomPackageName() != null ? eventFunctionDto.getCustomPackageName() : "");
			response.setPackagePrice(entity.getPackagePrice() != null ? entity.getPackagePrice() : BigDecimal.ZERO);

		} else {
			response.setIsPackage(entity.getIsPackage());
			response.setPackageId(null);
			response.setPackageName(entity.getPackageName());
			response.setPackagePrice(entity.getPackagePrice());
		}

		return response;
	}

	private static class PaginationResult<T> {
		List<T> paginatedList;
		int totalPage;
	}

	private <T> PaginationResult<T> paginateItems(List<T> items, int pageNo, int totalRecord) {
		PaginationResult<T> result = new PaginationResult<>();

		int totalItem = items.size();
		result.totalPage = (int) Math.ceil((double) totalItem / totalRecord);

		int fromIndex = Math.max(0, (pageNo - 1) * totalRecord);
		int toIndex = Math.min(fromIndex + totalRecord, totalItem);

		result.paginatedList = fromIndex < totalItem ? items.subList(fromIndex, toIndex) : Collections.emptyList();

		return result;
	}

	public List<DecorePreparationSelectedItemDetailsResponseDto> loadSelectedDecoreItems(Long eventFunctionId,
			DecorePreparationEntity decorePreparationEntity, EventFunctionMasterEntity eventFunction) {

		List<DecoreCategoryForPreparationResponseDto> categoryList = decorePreparationDetailsRepository
				.findAllByEventFunctionIdAndIsDeleteFalse(eventFunctionId);

		List<DecorePreparationSelectedItemDetailsResponseDto> result = new ArrayList<>();

		for (DecoreCategoryForPreparationResponseDto cat : categoryList) {

			decoreMainCategoryMasterRepository.findById(cat.getDecoreCategoryId()).ifPresent(category -> {

				List<DecorePreparationDetailsEntity> details = decorePreparationDetailsRepository
						.findAllByDecoreMainCategoryAndDecorePreparationOrderByDecoreCatSortOrderAscDecoreItemSortOrderAsc(
								category, decorePreparationEntity);

				List<DecorePreparationDetailsResponseDto> itemDtos = new ArrayList<>();

				for (DecorePreparationDetailsEntity detail : details) {

					DecorePreparationDetailsResponseDto dto = new DecorePreparationDetailsResponseDto();

					dto.setId(detail.getId());

					dto.setDecoreItemId(detail.getDecoreItem() != null ? detail.getDecoreItem().getId() : null);

					dto.setDecoreItemName(detail.getDecoreItemName());
					dto.setDecoreItemSortOrder(detail.getDecoreItemSortOrder());
					dto.setDecoreItemPrice(detail.getDecoreItemPrice());
					dto.setDecoreItemNotes(detail.getDecoreItemNotes());
					dto.setDecoreItemSlogan(detail.getDecoreItemSlogan());

					dto.setDecoreItemNameHindi(detail.getDecoreItemNameHindi());
					dto.setDecoreItemNameGujarati(detail.getDecoreItemNameGujarati());

					dto.setDecoreItemNotesHindi(detail.getDecoreItemNotesHindi());
					dto.setDecoreItemNotesGujarati(detail.getDecoreItemNotesGujarati());

					dto.setItemSpace(detail.getItemSpace());
					dto.setItemQty(detail.getItemQty());
					dto.setSubItem(detail.getSubItem());
					dto.setSubItemHindi(detail.getSubItemHindi());
					dto.setSubItemGujarati(detail.getSubItemGujarati());

					dto.setVendorId(detail.getVendorId());
					if (detail.getVendorId() != null) {
						dto.setVendorName(partyMasterRepository.findByIdAndIsDeleteFalse(detail.getVendorId()).get()
								.getNameEnglish());
					}

					dto.setIsDecoreItemAddons(
							detail.getIsDecoreItemAddons() != null ? detail.getIsDecoreItemAddons() : false);

					itemDtos.add(dto);
					List<EventFunctionDecorItemImagesEntity> decorItemImagesEntities = eventFunctionDecorItemImagesRepository
							.findByEventIdAndEventFunctionIdAndDecorItemIdAndIsDeleteFalse(
									eventFunction.getEvent().getId(), eventFunctionId, dto.getDecoreItemId());

					List<String> images = decorItemImagesEntities.stream()
							.map(image -> environment.getProperty("app.image.url", "") + image.getImagePath())
							.collect(Collectors.toList());

					dto.setImages(images);
				}

				DecorePreparationSelectedItemDetailsResponseDto dto = new DecorePreparationSelectedItemDetailsResponseDto();

				dto.setDecoreCategoryId(category.getId());
				dto.setDecoreCategoryName(cat.getDecoreCategoryName());

				dto.setDecoreCatSortOrder(cat.getDecoreCatSortOrder());
				dto.setDecoreCatNotes(cat.getDecoreCatNotes());

				dto.setDecoreCategoryNameHindi(cat.getDecoreCategoryNameHindi());
				dto.setDecoreCategoryNameGujarati(cat.getDecoreCategoryNameGujarati());

				dto.setDecoreCatNotesHindi(cat.getDecoreCatNotes());
				dto.setDecoreCatNotesGujarati(cat.getDecoreNotesGujarati());

				dto.setStartTime(cat.getStartTime());

				dto.setCatImgId(cat.getCatImgId());
				dto.setBgImgId(cat.getBgImgId());
				dto.setCatSpace(cat.getCatSpace());

				dto.setSubCat(cat.getSubCat());
				dto.setSubCatHindi(cat.getSubCatHindi());
				dto.setSubCatGujarati(cat.getSubCatGujarati());

				dto.setIsDecoreCatAddons(cat.getIsDecoreCatAddons());
				dto.setAnyItem(cat.getAnyItem());

				dto.setSelectedItems(itemDtos);

				result.add(dto);
			});
		}

		return result;
	}

	private List<DecorePreparationItemResponseDto> convertToDecoreItemList(List<Object[]> rows) {

		String baseImageUrl = environment.getProperty("app.image.url", "");

		return rows.stream().map(row -> {

			DecorePreparationItemResponseDto dto = new DecorePreparationItemResponseDto();

			dto.setDecoreItemName((String) row[0]);
			dto.setDecoreCategoryName((String) row[1]);

			dto.setItemPrice((BigDecimal) row[2]);

			dto.setDecoreItemSlogan((String) row[3]);
			dto.setCategorySlogan((String) row[4]);

			dto.setDecoreCategoryId(((Number) row[5]).longValue());
			dto.setDecoreItemId(((Number) row[6]).longValue());

			List<DecoreMainCategoryItemImagesMasterEntity> imagesMasterEntities = decoreMainCategoryItemImagesMasterRepository
					.findAllByDecoreItem_IdAndIsDeleteFalse(dto.getDecoreItemId());
			List<String> imgs = new ArrayList<>();
			for (DecoreMainCategoryItemImagesMasterEntity img : imagesMasterEntities) {
				imgs.add(environment.getProperty(baseImageUrl) + img);

			}
			dto.setImagePath(imgs);

			dto.setIsSelected(((Number) row[8]).intValue() == 1);

			dto.setItemSortOrder(row[9] != null ? ((Number) row[9]).intValue() : null);

			dto.setDecoreSortOrder(row[10] != null ? ((Number) row[10]).intValue() : null);

			dto.setDecoreCategoryNameHindi((String) row[11]);
			dto.setDecoreCategoryNameGujarati((String) row[12]);

			dto.setDecoreItemNameHindi((String) row[13]);
			dto.setDecoreItemNameGujarati((String) row[14]);

			dto.setInstructionEnglish((String) row[15]);
			dto.setInstructionGujarati((String) row[16]);
			dto.setInstructionHindi((String) row[17]);

			return dto;
		}).collect(Collectors.toList());
	}

	@Override
	public Boolean deleteDecorePreparationItem(Long decorePreparationId, Long decoreCategoryId, Long itemId) {

		DecoreMainCategoryMasterEntity category = decoreMainCategoryMasterRepository
				.findByIdAndIsDeleteFalse(decoreCategoryId)
				.orElseThrow(() -> new RuntimeException("Decore Category not found with id: " + decoreCategoryId));

		DecoreMainCategoryItemMasterEntity item = decoreItemMasterRepository.findByIdAndIsDeleteFalse(itemId)
				.orElseThrow(() -> new RuntimeException("Decore Item not found with id: " + itemId));

		DecorePreparationEntity decorePreparation = decorePreparationRepository
				.findByIdAndIsDeleteFalse(decorePreparationId).orElseThrow(
						() -> new RuntimeException("Decore Preparation not found with id: " + decorePreparationId));

		Optional<DecorePreparationDetailsEntity> existing = decorePreparationDetailsRepository
				.findByDecorePreparationAndDecoreItemAndDecoreMainCategory(decorePreparation, item, category);

		if (!existing.isPresent()) {
			return false;
		}

		decorePreparationDetailsRepository.deleteById(existing.get().getId());

		return true;
	}

	@Transactional
	@Override
	public Boolean copyEventFunctionDecore(Long oldEventFunctionId, Long activeEventFunctionId) {

		EventFunctionMasterEntity oldEventFunction = eventFunctionMasterRepository
				.findByIdAndIsDeleteFalse(oldEventFunctionId)
				.orElseThrow(() -> new RuntimeException("Old Event Function Not Found"));

		DecorePreparationEntity oldDecorePreparation = decorePreparationRepository
				.findByEventFunctionAndIsDeleteFalse(oldEventFunction);

		if (oldDecorePreparation == null
				|| !decorePreparationDetailsRepository.existsByDecorePreparation(oldDecorePreparation)) {
			throw new RuntimeException("Old Event Function Decore Planning Not Exist");
		}

		List<Long> tobeAddedItemIds = new ArrayList<>();

		EventFunctionMasterEntity activeEventFunction = eventFunctionMasterRepository
				.findByIdAndIsDeleteFalse(activeEventFunctionId)
				.orElseThrow(() -> new RuntimeException("Active Event Function Not Found"));

		DecorePreparationEntity activeDecorePreparation = decorePreparationRepository
				.findByEventFunctionAndIsDeleteFalse(activeEventFunction);

		if (activeDecorePreparation != null) {

			decorePreparationDetailsRepository.deleteAllByDecorePreparation(activeDecorePreparation);

		} else {

			activeDecorePreparation = new DecorePreparationEntity();
			activeDecorePreparation.setEventFunction(activeEventFunction);
			activeDecorePreparation.setDecorePackage(oldDecorePreparation.getDecorePackage());
			activeDecorePreparation.setDefaultPrice(oldDecorePreparation.getDefaultPrice());
			activeDecorePreparation.setIsDelete(false);
			activeDecorePreparation.setIsPackage(oldDecorePreparation.getIsPackage());
			activeDecorePreparation.setIsUpdate(true);
			activeDecorePreparation.setPackageName(oldDecorePreparation.getPackageName());
			activeDecorePreparation.setPackagePrice(oldDecorePreparation.getPackagePrice());
			activeDecorePreparation.setPax(activeEventFunction.getPax());
			activeDecorePreparation.setPrice(
					oldDecorePreparation.getPrice() != null ? oldDecorePreparation.getPrice() : BigDecimal.ZERO);

			Integer maxSortOrder = decorePreparationRepository.findMaxSortOrder(activeEventFunction.getId());

			activeDecorePreparation.setSortorder(maxSortOrder != null ? maxSortOrder + 1 : 1);

			activeDecorePreparation = decorePreparationRepository.save(activeDecorePreparation);
		}

		List<DecorePreparationDetailsEntity> oldDetails = decorePreparationDetailsRepository
				.findAllByDecorePreparation(oldDecorePreparation);

		List<DecorePreparationDetailsEntity> newDetails = new ArrayList<>();

		for (DecorePreparationDetailsEntity detail : oldDetails) {

			DecorePreparationDetailsEntity newDetail = new DecorePreparationDetailsEntity();

			newDetail.setDecorePreparation(activeDecorePreparation);

			newDetail.setDecoreItem(detail.getDecoreItem());
			newDetail.setDecoreMainCategory(detail.getDecoreMainCategory());

			newDetail.setDecoreItemName(detail.getDecoreItemName());
			newDetail.setDecoreItemNameHindi(detail.getDecoreItemNameHindi());
			newDetail.setDecoreItemNameGujarati(detail.getDecoreItemNameGujarati());

			newDetail.setDecoreCategoryName(detail.getDecoreCategoryName());
			newDetail.setDecoreCategoryNameHindi(detail.getDecoreCategoryNameHindi());
			newDetail.setDecoreCategoryNameGujarati(detail.getDecoreCategoryNameGujarati());

			newDetail.setDecoreItemPrice(detail.getDecoreItemPrice());
			newDetail.setDecoreItemSlogan(detail.getDecoreItemSlogan());
			newDetail.setDecoreCatSlogan(detail.getDecoreCatSlogan());

			newDetail.setDecoreItemSortOrder(detail.getDecoreItemSortOrder());
			newDetail.setDecoreCatSortOrder(detail.getDecoreCatSortOrder());

			newDetail.setDecoreItemNotes(detail.getDecoreItemNotes());
			newDetail.setDecoreItemNotesHindi(detail.getDecoreItemNotesHindi());
			newDetail.setDecoreItemNotesGujarati(detail.getDecoreItemNotesGujarati());

			newDetail.setDecoreCatNotes(detail.getDecoreCatNotes());
			newDetail.setDecoreCatNotesHindi(detail.getDecoreCatNotesHindi());
			newDetail.setDecoreCatNotesGujarati(detail.getDecoreCatNotesGujarati());

			newDetail.setSubCat(detail.getSubCat());
			newDetail.setSubCatHindi(detail.getSubCatHindi());
			newDetail.setSubCatGujarati(detail.getSubCatGujarati());

			newDetail.setSubItem(detail.getSubItem());
			newDetail.setSubItemHindi(detail.getSubItemHindi());
			newDetail.setSubItemGujarati(detail.getSubItemGujarati());

			newDetail.setIsDecoreCatAddons(detail.getIsDecoreCatAddons());
			newDetail.setIsDecoreItemAddons(detail.getIsDecoreItemAddons());

			newDetail.setCatImgId(detail.getCatImgId());
			newDetail.setBgImgId(detail.getBgImgId());
			newDetail.setCatSpace(detail.getCatSpace());
			newDetail.setItemSpace(detail.getItemSpace());
			newDetail.setAnyItem(detail.getAnyItem());

			tobeAddedItemIds.add(detail.getDecoreItem().getId());
			newDetails.add(newDetail);
		}

		decorePreparationDetailsRepository.saveAll(newDetails);
		return true;
	}

	@Override
	@Transactional
	public List<String> uploadDecorImages(EventFunctionDecorItemImagesRequestDto request) {

		if (request.getImages() == null || request.getImages().isEmpty()) {
			throw new RuntimeException("Please select at least one image.");
		}
		eventFunctionDecorItemImagesRepository.deleteByEventIdAndEventFunctionIdAndDecorItemId(request.getEventId(),
				request.getEventFunctionId(), request.getDecorItemId());

		for (MultipartFile file : request.getImages()) {

			if (file.isEmpty()) {
				continue;
			}

			EventFunctionDecorItemImagesEntity entity = new EventFunctionDecorItemImagesEntity();
			entity.setEventId(request.getEventId());
			entity.setEventFunctionId(request.getEventFunctionId());
			entity.setDecorItemId(request.getDecorItemId());
			entity.setUserId(request.getUserId());

			entity = eventFunctionDecorItemImagesRepository.save(entity);

			try {
				userFileService.storeFile(request.getUserId(),
						ModuleName.EVENTFUNCTIONDECOREMAINCATEGORYITEM.toString(), entity.getId(),
						FileType.IMAGE.toString(), file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		List<EventFunctionDecorItemImagesEntity> decorItemImagesEntities = eventFunctionDecorItemImagesRepository
				.findByEventIdAndEventFunctionIdAndDecorItemIdAndIsDeleteFalse(request.getEventId(),
						request.getEventFunctionId(), request.getDecorItemId());

		List<String> images = decorItemImagesEntities.stream()
				.map(image -> environment.getProperty("app.image.url", "") + image.getImagePath())
				.collect(Collectors.toList());

		return images;
	}
}