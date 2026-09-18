package com.crmportal.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmportal.entity.EventFoodTestingDetailEntity;
import com.crmportal.entity.EventFoodTestingEntity;
import com.crmportal.entity.EventFoodTestingLinkEntity;
import com.crmportal.entity.EventFunctionMasterEntity;
import com.crmportal.entity.EventMasterEntity;
import com.crmportal.entity.MenuPreparationEntity;
import com.crmportal.entity.MenuShareLinkEntity;
import com.crmportal.entity.TesterMasterEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.repository.EventFoodTestingDetailsRepository;
import com.crmportal.repository.EventFoodTestingLinkRepository;
import com.crmportal.repository.EventFoodTestingRepository;
import com.crmportal.repository.EventFunctionMasterRepository;
import com.crmportal.repository.EventMasterRepository;
import com.crmportal.repository.MenuCategoryMasterRepository;
import com.crmportal.repository.MenuItemMasterRepository;
import com.crmportal.repository.MenuPreparationRepository;
import com.crmportal.repository.TesterMasterRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.request.dto.EventFoodTestingGenerateLinkRequestDto;
import com.crmportal.request.dto.EventFoodTestingGenerateLinkResponseDto;
import com.crmportal.request.dto.EventFoodTestingMenuDetailsRequestDto;
import com.crmportal.request.dto.EventFoodTestingMenuRequestDto;
import com.crmportal.request.dto.EventFoodTestingRequestDto;
import com.crmportal.request.dto.VerifyLinkRequestDto;
import com.crmportal.response.dto.EventFoodMeuDetailsResponseDto;
import com.crmportal.response.dto.EventFoodTestingFinalMenuResponseDto;
import com.crmportal.response.dto.EventFoodTestingMenuResponseDto;
import com.crmportal.response.dto.EventFoodTestingResponseDto;
import com.crmportal.response.dto.EventFunctionTesterResponseDto;
import com.crmportal.response.dto.MenuPreparationSelectedItemDetailsResponseDto;
import com.crmportal.service.EventFoodTestingService;
import com.crmportal.utility.DateMapper;

@Service
public class EventFoodTestingServiceImpl implements EventFoodTestingService {

	@Autowired
	EventMasterRepository eventMasterRepository;

	@Autowired
	EventFunctionMasterRepository eventFunctionMasterRepository;

	@Autowired
	TesterMasterRepository testerMasterRepository;

	@Autowired
	EventFoodTestingRepository eventFoodTestingRepository;

	@Autowired
	MenuCategoryMasterRepository menuCategoryMasterRepository;

	@Autowired
	MenuItemMasterRepository menuItemMasterRepository;

	@Autowired
	EventFoodTestingDetailsRepository eventFoodTestingDetailsRepository;

	@Autowired
	MenuPreparationRepository menuPreparationRepository;

	@Autowired
	MenuPreparationServiceImpl menuPreparationServiceImpl;

	@Autowired
	UserMasterRepository userMasterRepository;

	@Autowired
	EventFoodTestingLinkRepository eventFoodTestingLinkRepository;

	@Autowired
	DateMapper dateMapper;

	private final DateTimeFormatter displayFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

	@Override
	@Transactional
	public EventFoodTestingResponseDto addOrUpdateFoodTestingMenu(EventFoodTestingRequestDto request) {

		eventMasterRepository.findByIdAndIsDeleteFalse(request.getEventId())
				.orElseThrow(() -> new RuntimeException("Event not found with id : " + request.getEventId()));

		eventFunctionMasterRepository.findByIdAndIsDeleteFalse(request.getEventFunctionId()).orElseThrow(
				() -> new RuntimeException("Event function not found with id : " + request.getEventFunctionId()));

		testerMasterRepository.findByIdAndIsDeleteFalse(request.getTesterId())
				.orElseThrow(() -> new RuntimeException("Tester not found with id : " + request.getTesterId()));

		userMasterRepository.findByIdAndIsDeleteFalse(request.getUserId())
				.orElseThrow(() -> new RuntimeException("User not found with id : " + request.getUserId()));

		EventFoodTestingEntity foodTesting;

		if (request.getId() != null && request.getId() > 0) {

			foodTesting = eventFoodTestingRepository.findById(request.getId()).orElseThrow(
					() -> new RuntimeException("Food testing record not found with id : " + request.getId()));

			foodTesting.setEventId(request.getEventId());
			foodTesting.setEventFunctionId(request.getEventFunctionId());
			foodTesting.setTesterId(request.getTesterId());
			foodTesting.setUserId(request.getUserId());
			foodTesting.setUpdatedAt(LocalDateTime.now());

			foodTesting = eventFoodTestingRepository.save(foodTesting);

			// Delete old details
			eventFoodTestingDetailsRepository.deleteByEventFoodTesting_Id(foodTesting.getId());

		} else {

			foodTesting = new EventFoodTestingEntity();
			foodTesting.setEventId(request.getEventId());
			foodTesting.setEventFunctionId(request.getEventFunctionId());
			foodTesting.setTesterId(request.getTesterId());
			foodTesting.setUserId(request.getUserId());
			foodTesting.setUpdatedAt(LocalDateTime.now());

			foodTesting = eventFoodTestingRepository.save(foodTesting);
		}

		foodTesting = eventFoodTestingRepository.save(foodTesting);

		// Delete old details
		eventFoodTestingDetailsRepository.deleteByEventFoodTesting_Id(foodTesting.getId());

		List<EventFoodTestingDetailEntity> detailEntities = new ArrayList<>();
		List<EventFoodTestingMenuResponseDto> responseCategories = new ArrayList<>();

		if (request.getSelectedCat() != null) {

			for (EventFoodTestingMenuRequestDto categoryDto : request.getSelectedCat()) {

				List<EventFoodMeuDetailsResponseDto> responseItems = new ArrayList<>();

				if (categoryDto.getSelectedMenuDetails() != null) {

					for (EventFoodTestingMenuDetailsRequestDto itemDto : categoryDto.getSelectedMenuDetails()) {

						EventFoodTestingDetailEntity detail = new EventFoodTestingDetailEntity();

						detail.setEventFoodTesting(foodTesting);

						// Category
						detail.setMenuCatId(categoryDto.getMenuCatId());
						detail.setMenuCatNameEnglish(categoryDto.getMenuCatNameEnglish());
						detail.setMenuCatNameHindi(categoryDto.getMenuCatNameHindi());
						detail.setMenuCatNameGujarati(categoryDto.getMenuCatNameGujarati());

						detail.setCatNotesEnglish(categoryDto.getCatNotesEnglish());
						detail.setCatNotesHindi(categoryDto.getCatNotesHindi());
						detail.setCatNotesGujarati(categoryDto.getCatNotesGujarati());

						detail.setCatSortOrder(categoryDto.getCatSortOrder());

						// Item
						detail.setMenuItemId(itemDto.getMenuItemId());
						detail.setMenuItemNameEnglish(itemDto.getMenuItemNameEnglish());
						detail.setMenuItemNameHindi(itemDto.getMenuItemNameHindi());
						detail.setMenuItemNameGujarati(itemDto.getMenuItemNameGujarati());

						detail.setItemNotesEnglish(itemDto.getItemNotesEnglish());
						detail.setItemNotesHindi(itemDto.getItemNotesHindi());
						detail.setItemNotesGujarati(itemDto.getItemNotesGujarati());

						detail.setItemSlogan(itemDto.getItemSlogan());

						detail.setItemSortOrder(itemDto.getItemSortOrder());

						detail.setClientNotes(itemDto.getClientNotes());
						detail.setClientNotesHindi(itemDto.getClientNotesHindi());
						detail.setClientNotesGujarati(itemDto.getClientNotesGujarati());
						detail.setReview(itemDto.getReview());

						detail.setUpdatedAt(LocalDateTime.now());
						detail.setIsDelete(false);

						detailEntities.add(detail);

						responseItems.add(new EventFoodMeuDetailsResponseDto(detail.getId(), itemDto.getItemSortOrder(),
								itemDto.getMenuItemId(), itemDto.getMenuItemNameEnglish(),
								itemDto.getMenuItemNameHindi(), itemDto.getMenuItemNameGujarati(),
								itemDto.getItemSlogan(), itemDto.getItemNotesEnglish(), itemDto.getItemNotesHindi(),
								itemDto.getItemNotesGujarati(), itemDto.getClientNotes(),
								itemDto.getClientNotesHindi(), itemDto.getClientNotesGujarati(),
								itemDto.getReview(), null,
								null, false));
					}
				}

				responseCategories.add(new EventFoodTestingMenuResponseDto(categoryDto.getMenuCatId(),
						categoryDto.getMenuCatNameEnglish(), categoryDto.getCatSortOrder(),
						categoryDto.getMenuCatNameHindi(), categoryDto.getMenuCatNameGujarati(),
						categoryDto.getCatNotesEnglish(), categoryDto.getCatNotesHindi(),
						categoryDto.getCatNotesGujarati(), responseItems));
			}
		}

		if (!detailEntities.isEmpty()) {
			eventFoodTestingDetailsRepository.saveAll(detailEntities);
		}

		EventFoodTestingResponseDto response = new EventFoodTestingResponseDto();
		response.setEventId(foodTesting.getEventId());
		response.setEventFunctionId(foodTesting.getEventFunctionId());
		response.setTesterId(foodTesting.getTesterId());
		response.setSelectedCat(responseCategories);

		return response;
	}

	@Override
	public EventFoodTestingFinalMenuResponseDto getMenuByTesterId(VerifyLinkRequestDto request) {

		EventFoodTestingLinkEntity link = eventFoodTestingLinkRepository.findByTokenAndIsDeleteFalse(request.getToken())
				.orElseThrow(() -> new RuntimeException("Invalid or expired link"));

		if (!Boolean.TRUE.equals(link.getIsActive())) {
			throw new RuntimeException("This link has been deactivated");
		}

		if (LocalDateTime.now().isAfter(link.getExpiryDate())) {
			throw new RuntimeException("This link has expired on " + link.getExpiryDate().format(displayFmt));
		}

		if (!link.getAccessCode().equals(request.getAccessCode())) {
			throw new RuntimeException("Invalid access code");
		}

		eventMasterRepository.findByIdAndIsDeleteFalse(link.getEventId())
				.orElseThrow(() -> new RuntimeException("Event not found with id : " + link.getEventId()));

		EventFunctionMasterEntity eventFunction = eventFunctionMasterRepository
				.findByIdAndIsDeleteFalse(link.getEventFunctionId()).orElseThrow(
						() -> new RuntimeException("Event function not found with id : " + link.getEventFunctionId()));

		TesterMasterEntity tester = testerMasterRepository.findByIdAndIsDeleteFalse(link.getTesterId())
				.orElseThrow(() -> new RuntimeException("Tester not found with id : " + link.getTesterId()));

		MenuPreparationEntity menuPreparationEntity = menuPreparationRepository
				.findByEventFunctionAndIsDeleteFalse(eventFunction);

		List<MenuPreparationSelectedItemDetailsResponseDto> eventMenuPreparation = menuPreparationServiceImpl
				.loadSelectedItems(eventFunction.getId(), menuPreparationEntity, eventFunction);

		List<EventFoodTestingResponseDto> selectedMenuPreparation = new ArrayList<>();

		Optional<EventFoodTestingEntity> foodTestingOpt = eventFoodTestingRepository
				.findByEventIdAndEventFunctionIdAndTesterId(link.getEventId(), link.getEventFunctionId(),
						link.getTesterId());

		if (foodTestingOpt.isPresent()) {

			EventFoodTestingEntity foodTesting = foodTestingOpt.get();

			List<EventFoodTestingDetailEntity> details = eventFoodTestingDetailsRepository
					.findByEventFoodTestingIdAndIsDeleteFalse(foodTesting.getId());

			Map<Long, List<EventFoodTestingDetailEntity>> grouped = details.stream()
					.collect(Collectors.groupingBy(EventFoodTestingDetailEntity::getMenuCatId));

			List<EventFoodTestingMenuResponseDto> categories = new ArrayList<>();

			for (List<EventFoodTestingDetailEntity> categoryDetails : grouped.values()) {

				EventFoodTestingDetailEntity first = categoryDetails.get(0);

				List<EventFoodMeuDetailsResponseDto> items = categoryDetails.stream()
						.sorted(Comparator.comparing(EventFoodTestingDetailEntity::getItemSortOrder,
								Comparator.nullsLast(Integer::compareTo)))
						.map(detail -> new EventFoodMeuDetailsResponseDto(detail.getId(), detail.getItemSortOrder(),
								detail.getMenuItemId(), detail.getMenuItemNameEnglish(), detail.getMenuItemNameHindi(),
								detail.getMenuItemNameGujarati(), detail.getItemSlogan(), detail.getItemNotesEnglish(),
								detail.getItemNotesHindi(), detail.getItemNotesGujarati(), detail.getClientNotes(),
								detail.getClientNotesHindi(), detail.getClientNotesGujarati(),
								detail.getReview(),
								detail.getCreatedAt() != null ? detail.getCreatedAt().toString() : null,
								detail.getUpdatedAt() != null ? detail.getUpdatedAt().toString() : null,
								detail.getIsDelete()))
						.collect(Collectors.toList());

				categories.add(new EventFoodTestingMenuResponseDto(first.getMenuCatId(), first.getMenuCatNameEnglish(),
						first.getCatSortOrder(), first.getMenuCatNameHindi(), first.getMenuCatNameGujarati(),
						first.getCatNotesEnglish(), first.getCatNotesHindi(), first.getCatNotesGujarati(), items));
			}

			categories.sort(Comparator.comparing(EventFoodTestingMenuResponseDto::getMenuSortOrder,
					Comparator.nullsLast(Integer::compareTo)));

			selectedMenuPreparation.add(new EventFoodTestingResponseDto(foodTesting.getId(), foodTesting.getEventId(),
					foodTesting.getEventFunctionId(), foodTesting.getTesterId(), categories));
		}

		EventFoodTestingFinalMenuResponseDto response = new EventFoodTestingFinalMenuResponseDto();

		response.setMenuPreparationItems(eventMenuPreparation);
		response.setSelectedMenuPreparation(selectedMenuPreparation);
		response.setEventId(link.getEventId());
		response.setEventFunctionId(link.getEventFunctionId());
		response.setTesterId(link.getTesterId());
		response.setUserId(tester.getUserId());

		return response;
	}

	@Override
	@Transactional
	public EventFoodTestingGenerateLinkResponseDto generateLink(EventFoodTestingGenerateLinkRequestDto request) {
		EventMasterEntity eventMasterEntity = eventMasterRepository.findByIdAndIsDeleteFalse(request.getEventId())
				.orElseThrow(() -> new RuntimeException("Event Not Found"));

		EventFunctionMasterEntity eventFunctionMasterEntity = eventFunctionMasterRepository
				.findByIdAndIsDeleteFalse(request.getEventFunctionId())
				.orElseThrow(() -> new RuntimeException("Event Function Not Found"));

		UserMasterEntity userMasterEntity = userMasterRepository.findByIdAndIsDeleteFalse(request.getUserId())
				.orElseThrow(() -> new RuntimeException("User Not Found"));

		TesterMasterEntity testerMasterEntity = testerMasterRepository.findByIdAndIsDeleteFalse(request.getTesterId())
				.orElseThrow(() -> new RuntimeException("Tester not found"));

		StringBuilder partycode = new StringBuilder("T");

		partycode.append(request.getTesterId());
		partycode.append(eventMasterEntity.getEventNo());

		LocalDateTime expiryDate = LocalDateTime.now().plusHours(24);

		// Generate encrypted token
		String rawToken = request.getEventId() + request.getEventFunctionId() + ":" + request.getUserId() + ":"
				+ UUID.randomUUID().toString();

		String token = Base64.getUrlEncoder().withoutPadding().encodeToString(rawToken.getBytes());
		
		// Build share URL
		String baseUrl = "https://app.justcatering.in";
		String shareUrl = baseUrl + "/menu-review?token=" + token;

		Optional<EventFoodTestingLinkEntity> op = eventFoodTestingLinkRepository
				.findByEventFunctionIdAndTesterIdAndUserIdAndEventIdAndIsDeleteFalse(request.getEventFunctionId(),
						request.getTesterId(), request.getUserId(), request.getEventId());
		
		if(op.isPresent()) {
			EventFoodTestingLinkEntity dbLinkEntity = op.get();
			return buildResponse(dbLinkEntity, baseUrl + "/menu-review?token=" + dbLinkEntity.getToken(), userMasterEntity, eventMasterEntity, eventFunctionMasterEntity,
					testerMasterEntity);
		}

		// Save
		EventFoodTestingLinkEntity entity = new EventFoodTestingLinkEntity();
		entity.setToken(token);
		entity.setAccessCode(partycode.toString());
		entity.setEventFunctionId(request.getEventFunctionId());
		entity.setUserId(request.getUserId());
		entity.setEventId(request.getEventId());
		entity.setExpiryDate(expiryDate);
		entity.setIsActive(true);
		entity.setIsDelete(false);
		entity.setTesterId(request.getTesterId());
		entity.setMembers(request.getMembers() != null ? request.getMembers() : 0);
		entity = eventFoodTestingLinkRepository.save(entity);

		EventFoodTestingEntity eventFoodTesting = new EventFoodTestingEntity();
		eventFoodTesting.setEventId(request.getEventId());
		eventFoodTesting.setEventFunctionId(request.getEventFunctionId());
		eventFoodTesting.setTesterId(request.getTesterId());
		eventFoodTesting.setUserId(request.getUserId());
		eventFoodTestingRepository.save(eventFoodTesting);
		
		return buildResponse(entity, shareUrl, userMasterEntity, eventMasterEntity, eventFunctionMasterEntity,
				testerMasterEntity);
	}

	private EventFoodTestingGenerateLinkResponseDto buildResponse(EventFoodTestingLinkEntity entity, String shareUrl,
			UserMasterEntity userMasterEntity, EventMasterEntity eventMasterEntity,
			EventFunctionMasterEntity eventFunctionMasterEntity, TesterMasterEntity testerMasterEntity) {

		EventFoodTestingGenerateLinkResponseDto dto = new EventFoodTestingGenerateLinkResponseDto();
		dto.setId(entity.getId());
		dto.setToken(entity.getToken());
		dto.setShareUrl(shareUrl);
		dto.setAccessCode(entity.getAccessCode());
		dto.setExpiryDate(entity.getExpiryDate().format(displayFmt));
		dto.setEventFunctionId(entity.getEventFunctionId());
		dto.setUserId(entity.getUserId());
		dto.setIsActive(entity.getIsActive());
		dto.setTesterContactNo(testerMasterEntity.getContactNo());
		dto.setEventId(entity.getEventId());
		dto.setTesterName(testerMasterEntity.getNameEnglish());
		dto.setEventName(eventMasterEntity.getEventType().getNameEnglish());
		dto.setFunctionName(eventFunctionMasterEntity.getFunction().getNameEnglish());
		dto.setFunctionDate(dateMapper.dateTimeToString(eventFunctionMasterEntity.getFunctionStartDateTime()) + " To "
				+ dateMapper.dateTimeToString(eventFunctionMasterEntity.getFunctionEndDateTime()));
		dto.setTesterId(entity.getTesterId());
		dto.setEventNo(eventMasterEntity.getEventNo());
		dto.setMembers(entity.getMembers());
		return dto;
	}
	
	@Override
	public List<EventFunctionTesterResponseDto> getAllEventFunctionTester(Long eventId, Long eventFunctionId) {
	    eventMasterRepository.findByIdAndIsDeleteFalse(eventId)
	            .orElseThrow(() -> new RuntimeException("Event Not Found"));

	    eventFunctionMasterRepository.findByIdAndIsDeleteFalse(eventFunctionId)
	            .orElseThrow(() -> new RuntimeException("Event Function Not Found"));

	    return eventFoodTestingRepository.findTestersByEventAndFunction(eventId, eventFunctionId);
	}
}
