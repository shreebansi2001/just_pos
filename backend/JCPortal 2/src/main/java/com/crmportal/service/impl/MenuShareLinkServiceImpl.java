package com.crmportal.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.crmportal.entity.CustomPackageEntity;
import com.crmportal.entity.EventFunctionMasterEntity;
import com.crmportal.entity.EventMasterEntity;
import com.crmportal.entity.MenuPreparationEntity;
import com.crmportal.entity.MenuShareLinkEntity;
import com.crmportal.entity.UserBasicDetailsMasterEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.repository.CustomPackageRepository;
import com.crmportal.repository.EventFunctionMasterRepository;
import com.crmportal.repository.EventMasterRepository;
import com.crmportal.repository.MenuPreparationRepository;
import com.crmportal.repository.MenuShareLinkRepository;
import com.crmportal.repository.UserBasicDetailsMasterRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.request.dto.GenerateLinkRequestDto;
import com.crmportal.request.dto.VerifyLinkRequestDto;
import com.crmportal.response.dto.EventFunctionMenuPreparationResponseDto;
import com.crmportal.response.dto.MenuPreparationItemResponseDto;
import com.crmportal.response.dto.MenuPreparationResponseDto;
import com.crmportal.response.dto.MenuPreparationSelectedItemDetailsResponseDto;
import com.crmportal.response.dto.MenuShareDataResponseDto;
import com.crmportal.response.dto.MenuShareLinkMenuPreparationDto;
import com.crmportal.response.dto.MenuShareLinkResponseDto;
import com.crmportal.response.dto.MenuSharedMenuItemsResponseDto;
import com.crmportal.service.MenuShareLinkService;
import com.crmportal.utility.DateMapper;

@Service
@Transactional
public class MenuShareLinkServiceImpl implements MenuShareLinkService {

	@Autowired
	private MenuShareLinkRepository shareLinkRepository;

	@Autowired
	private MenuPreparationRepository menuPreparationRepository;

	@Autowired
	private MenuPreparationServiceImpl menuPreparationServiceImpl;

	@Autowired
	EventFunctionMasterRepository eventFunctionMasterRepository;

	@Autowired
	private Environment environment;

	@Autowired
	private EventMasterRepository eventMasterRepository;

	@Autowired
	UserBasicDetailsMasterRepository basicDetailsMasterRepository;

	@Autowired
	UserMasterRepository userMasterRepository;

	@Autowired
	CustomPackageRepository customPackageRepository;

	@Autowired
	DateMapper dateMapper;

	private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	private final DateTimeFormatter displayFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

	@Override
	public MenuShareLinkResponseDto generateLink(GenerateLinkRequestDto request) {

		EventMasterEntity eventMasterEntity = eventMasterRepository.findByIdAndIsDeleteFalse(request.getEventId())
				.orElseThrow(() -> new RuntimeException("Event Not Found"));

		EventFunctionMasterEntity eventFunctionMasterEntity = eventFunctionMasterRepository
				.findByIdAndIsDeleteFalse(request.getEventFunctionId())
				.orElseThrow(() -> new RuntimeException("Event Function Not Found"));

		CustomPackageEntity packageEntity = null;
		if (request.getPackageId() != null) {
			packageEntity = customPackageRepository.findByIdAndIsDeleteFalse(request.getPackageId());
		}

		UserMasterEntity userMasterEntity = userMasterRepository.findByIdAndIsDeleteFalse(request.getUserId())
				.orElseThrow(() -> new RuntimeException("User Not Found"));
		StringBuilder partycode = new StringBuilder("M");
		UserBasicDetailsMasterEntity basicDetailsMasterEntity = basicDetailsMasterRepository
				.findByUser(userMasterEntity);

		String[] compnayDetail = basicDetailsMasterEntity.getCompanyName().split(" ");

		for (String ch : compnayDetail) {
			partycode.append(ch.charAt(0));
		}
		partycode.append(request.getUserId());
		partycode.append(eventMasterEntity.getEventNo());

		LocalDateTime expiryDate = LocalDateTime.now().plusHours(24);

		shareLinkRepository.findByEventFunctionIdAndUserIdAndEventIdAndIsDeleteFalse(request.getEventFunctionId(),
				request.getUserId(), request.getEventId()).ifPresent(old -> {
					old.setIsDelete(true);
					shareLinkRepository.save(old);
				});

		// Generate encrypted token
		String rawToken = request.getEventId() + request.getEventFunctionId() + ":" + request.getUserId() + ":"
				+ UUID.randomUUID().toString();

		String token = Base64.getUrlEncoder().withoutPadding().encodeToString(rawToken.getBytes());

		// Save
		MenuShareLinkEntity entity = new MenuShareLinkEntity();
		entity.setToken(token);
		entity.setAccessCode(partycode.toString());
		entity.setEventFunctionId(request.getEventFunctionId());
		entity.setUserId(request.getUserId());
		entity.setEventId(request.getEventId());
		entity.setPackageId(packageEntity == null ? null : packageEntity.getId());
		entity.setExpiryDate(expiryDate);
		entity.setIsActive(true);
		entity.setIsDelete(false);
		entity = shareLinkRepository.save(entity);

		// Build share URL
		String baseUrl = "https://app.justcatering.in";
		String shareUrl = baseUrl + "/menu-share/verify?token=" + token;

		return buildResponse(entity, shareUrl, userMasterEntity, eventMasterEntity, eventFunctionMasterEntity,packageEntity);
	}

	@Override
	public MenuShareDataResponseDto verifyAndGetData(VerifyLinkRequestDto request) {

		// 1. Find link by token
		MenuShareLinkEntity link = shareLinkRepository.findByTokenAndIsDeleteFalse(request.getToken())
				.orElseThrow(() -> new RuntimeException("Invalid or expired link"));

		// 2. Check active
		if (!Boolean.TRUE.equals(link.getIsActive())) {
			throw new RuntimeException("This link has been deactivated");
		}

		// 3. Check expiry
		if (LocalDateTime.now().isAfter(link.getExpiryDate())) {
			throw new RuntimeException("This link has expired on " + link.getExpiryDate().format(displayFmt));
		}

		// 4. Verify access code
		if (!link.getAccessCode().equals(request.getAccessCode())) {
			throw new RuntimeException("Invalid access code");
		}

		EventMasterEntity event = eventMasterRepository.findByIdAndIsDeleteFalse(link.getEventId())
				.orElseThrow(() -> new RuntimeException("Event  Not Found"));

		EventFunctionMasterEntity eventFunctionMasterEntity = eventFunctionMasterRepository
				.findByIdAndIsDeleteFalse(link.getEventFunctionId())
				.orElseThrow(() -> new RuntimeException("Event Function Not Found"));

		CustomPackageEntity packageEntity = null;
		if (link.getPackageId() != null) {
			packageEntity = customPackageRepository.findByIdAndIsDeleteFalse(link.getPackageId());
		}

		MenuPreparationEntity menuPreparationEntity = menuPreparationRepository
				.findByEventFunctionAndIsDeleteFalse(eventFunctionMasterEntity);

		List<MenuPreparationSelectedItemDetailsResponseDto> selectedItems = menuPreparationServiceImpl
				.loadSelectedItems(eventFunctionMasterEntity.getId(), menuPreparationEntity, eventFunctionMasterEntity);

		EventFunctionMenuPreparationResponseDto eventFunctionMenuPreparationResponseDto = new EventFunctionMenuPreparationResponseDto();

		eventFunctionMenuPreparationResponseDto.setEventId(event.getId());
		eventFunctionMenuPreparationResponseDto.setFuncId(eventFunctionMasterEntity.getFunction().getId());
		eventFunctionMenuPreparationResponseDto.setFunction_venue(eventFunctionMasterEntity.getFunction_venue());
		eventFunctionMenuPreparationResponseDto.setFunctionStartDateTime(
				dateMapper.dateTimeToString(eventFunctionMasterEntity.getFunctionStartDateTime()));
		eventFunctionMenuPreparationResponseDto.setFunctionEndDateTime(
				dateMapper.dateTimeToString(eventFunctionMasterEntity.getFunctionEndDateTime()));
		eventFunctionMenuPreparationResponseDto.setId(eventFunctionMasterEntity.getId());
		eventFunctionMenuPreparationResponseDto
				.setNameEnglish(eventFunctionMasterEntity.getFunction().getNameEnglish());
		eventFunctionMenuPreparationResponseDto.setNameHindi(eventFunctionMasterEntity.getFunction().getNameHindi());
		eventFunctionMenuPreparationResponseDto
				.setNameGujarati(eventFunctionMasterEntity.getFunction().getNameGujarati());
		eventFunctionMenuPreparationResponseDto.setNotesEnglish(eventFunctionMasterEntity.getNotesEnglish());
		eventFunctionMenuPreparationResponseDto.setNotesHindi(eventFunctionMasterEntity.getNotesHindi());
		eventFunctionMenuPreparationResponseDto.setNotesGujarati(eventFunctionMasterEntity.getNotesGujarati());
		eventFunctionMenuPreparationResponseDto.setPax(eventFunctionMasterEntity.getPax());
		eventFunctionMenuPreparationResponseDto.setRate(eventFunctionMasterEntity.getRate());

		MenuShareLinkMenuPreparationDto menuPrepResponse = buildMenuPreparationResponse(
				eventFunctionMenuPreparationResponseDto, menuPreparationEntity, packageEntity);

		List<MenuSharedMenuItemsResponseDto> allItems = convertToItemDtoList(
				menuPreparationRepository.getAllMenuPreparationSharItems(eventFunctionMasterEntity.getId(),
						event.getUser().getId(), packageEntity.getId()));
		MenuShareDataResponseDto response = new MenuShareDataResponseDto();
		response.setEventFunctionId(link.getEventFunctionId());
		response.setUserId(link.getUserId());
		response.setEventId(link.getEventId());
		response.setExpiryDate(link.getExpiryDate().format(displayFmt));
		response.setSelectedMenuPreparationItems(selectedItems);
		response.setMenuPreparation(menuPrepResponse);
		response.setMenuPreparationItems(allItems);
		return response;
	}

	private List<MenuSharedMenuItemsResponseDto> convertToItemDtoList(List<Object[]> rows) {

		Map<Long, MenuSharedMenuItemsResponseDto> categoryMap = new LinkedHashMap<Long, MenuSharedMenuItemsResponseDto>();

		for (Object[] row : rows) {

			Long categoryId = row[5] != null ? ((Number) row[5]).longValue() : null;

			MenuSharedMenuItemsResponseDto categoryDto = categoryMap.get(categoryId);

			if (categoryDto == null) {

				categoryDto = new MenuSharedMenuItemsResponseDto();

				categoryDto.setCategoryId(categoryId);

				categoryDto.setNameEnglish(row[1] != null ? row[1].toString() : null);

				categoryDto.setNameHindi(row[11] != null ? row[11].toString() : null);

				categoryDto.setNameGujarati(row[12] != null ? row[12].toString() : null);

				categoryDto.setItems(new ArrayList<MenuPreparationItemResponseDto>());

				categoryMap.put(categoryId, categoryDto);
			}

			MenuPreparationItemResponseDto itemDto = new MenuPreparationItemResponseDto();

			itemDto.setMenuItemName(row[0] != null ? row[0].toString() : null);

			itemDto.setMenuCategoryName(row[1] != null ? row[1].toString() : null);

			itemDto.setItemPrice(row[2] != null ? (BigDecimal) row[2] : BigDecimal.ZERO);

			itemDto.setItemSlogan(row[3] != null ? row[3].toString() : null);

			itemDto.setCategorySlogan(row[4] != null ? row[4].toString() : null);

			itemDto.setMenuCategoryId(categoryId);

			itemDto.setMenuItemId(row[6] != null ? ((Number) row[6]).longValue() : null);

			itemDto.setImagePath(row[7] != null ? environment.getProperty("app.image.url") + row[7].toString() : null);

			itemDto.setIsSelected(row[8] != null ? ((Number) row[8]).intValue() == 1 : false);

			itemDto.setItemSortOrder(row[9] != null ? ((Number) row[9]).intValue() : null);

			itemDto.setMenuSortOrder(row[10] != null ? ((Number) row[10]).intValue() : null);

			itemDto.setMenuCategoryNameHindi(row[11] != null ? row[11].toString() : null);

			itemDto.setMenuCategoryNameGujarati(row[12] != null ? row[12].toString() : null);

			itemDto.setMenuItemNameHindi(row[13] != null ? row[13].toString() : null);

			itemDto.setMenuItemNameGujarati(row[14] != null ? row[14].toString() : null);

			itemDto.setInstructionEnglish(row[15] != null ? row[15].toString() : null);

			itemDto.setInstructionGujarati(row[16] != null ? row[16].toString() : null);

			itemDto.setInstructionHindi(row[17] != null ? row[17].toString() : null);

			categoryDto.getItems().add(itemDto);
		}

		return new ArrayList<MenuSharedMenuItemsResponseDto>(categoryMap.values());
	}

	private MenuShareLinkMenuPreparationDto buildMenuPreparationResponse(
			EventFunctionMenuPreparationResponseDto eventFunctionDto, MenuPreparationEntity entity,
			CustomPackageEntity packageEntity) {
		MenuShareLinkMenuPreparationDto response = new MenuShareLinkMenuPreparationDto();
		response.setEventFunction(eventFunctionDto);

		if (entity == null) {
			response.setId(null);
			response.setDefaultPrice(BigDecimal.ZERO);
			response.setPax(0);
			response.setSortorder(0);
			response.setIsPackage(packageEntity != null ? true : false);
			response.setPackageId(packageEntity != null ? packageEntity.getId() : null);
			response.setPackageName(packageEntity != null ? packageEntity.getNameEnglish() : "");
			response.setPackagePrice(packageEntity != null ? packageEntity.getPrice() : BigDecimal.ZERO);
			response.setPrice(BigDecimal.ZERO);
			return response;
		}

		response.setId(entity.getId());
		response.setDefaultPrice(entity.getDefaultPrice());
		response.setPax(entity.getPax());
		response.setSortorder(entity.getSortorder());
		response.setPrice(entity.getPrice());

		if (Boolean.TRUE.equals(entity.getIsPackage())) {
			response.setPackageId(entity.getCustomPackage().getId());
		}

		response.setPackageName(entity.getPackageName());
		response.setIsPackage(entity.getIsPackage());
		response.setPackagePrice(entity.getPackagePrice());

		return response;
	}

	// ── Helper ────────────────────────────────────────────────────────────────
	private MenuShareLinkResponseDto buildResponse(MenuShareLinkEntity entity, String shareUrl,
			UserMasterEntity userMasterEntity, EventMasterEntity eventMasterEntity,
			EventFunctionMasterEntity eventFunctionMasterEntity, CustomPackageEntity packageEntity) {

		MenuShareLinkResponseDto dto = new MenuShareLinkResponseDto();
		dto.setId(entity.getId());
		dto.setToken(entity.getToken());
		dto.setShareUrl(shareUrl);
		dto.setAccessCode(entity.getAccessCode());
		dto.setExpiryDate(entity.getExpiryDate().format(displayFmt));
		dto.setEventFunctionId(entity.getEventFunctionId());
		dto.setUserId(entity.getUserId());
		dto.setIsActive(entity.getIsActive());
		dto.setUserMobileNo(userMasterEntity.getContactNo());
		dto.setEventId(entity.getEventId());
		dto.setPackageId(entity.getPackageId());
		dto.setUserName(userMasterEntity.getFirstName() + " " + userMasterEntity.getLastName());
		dto.setEventName(eventMasterEntity.getEventType().getNameEnglish());
		dto.setFunctionName(eventFunctionMasterEntity.getFunction().getNameEnglish());
		dto.setFunctionDate(dateMapper.dateTimeToString(eventFunctionMasterEntity.getFunctionStartDateTime())
				+ " To " + dateMapper.dateTimeToString(eventFunctionMasterEntity.getFunctionEndDateTime()));
		dto.setEventNo(eventMasterEntity.getEventNo());
		dto.setGuestName(eventMasterEntity.getParty().getNameEnglish());
		dto.setGuestMobileNo(eventMasterEntity.getMobileno());
		dto.setPackageName(packageEntity == null ? "NA" : packageEntity.getNameEnglish());
		return dto;
	}
}