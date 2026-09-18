package com.crmportal.service.impl;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.crmportal.entity.BanquetHallMasterEntity;
import com.crmportal.entity.BanquetHallShiftBookingEntity;
import com.crmportal.entity.CustomPackageEntity;
import com.crmportal.entity.EventFunctionManagerAssignEntity;
import com.crmportal.entity.EventFunctionMasterEntity;
import com.crmportal.entity.EventFunctionQuotationEntity;
import com.crmportal.entity.EventFunctionStaffAssignmentEntity;
import com.crmportal.entity.EventFunctionStaffTaskEntity;
import com.crmportal.entity.EventFunctionStaffUploadEntity;
import com.crmportal.entity.EventGroundTaskMasterEntity;
import com.crmportal.entity.EventInvoiceEntity;
import com.crmportal.entity.EventLaborEntity;
import com.crmportal.entity.EventMasterEntity;
import com.crmportal.entity.EventRawMaterialEntity;
import com.crmportal.entity.EventRoomMasterEntity;
import com.crmportal.entity.EventTermsAndConditionEntity;
import com.crmportal.entity.EventTermsAndConditionFeaturesEntity;
import com.crmportal.entity.EventTypeMasterEntity;
import com.crmportal.entity.FunctionMasterEntity;
import com.crmportal.entity.MenuAllocationOrdersEntity;
import com.crmportal.entity.MenuItemMasterEntity;
import com.crmportal.entity.MenuPreparationEntity;
import com.crmportal.entity.PartyMasterEntity;
import com.crmportal.entity.TermsAndConditionFeaturesEntity;
import com.crmportal.entity.UnitMasterEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.entity.UserTermsAndConditionEntity;
import com.crmportal.entity.VenueMasterEntity;
import com.crmportal.enums.AllocationType;
import com.crmportal.enums.FileType;
import com.crmportal.enums.ModuleName;
import com.crmportal.mapper.EventFunctionMasterMapper;
import com.crmportal.mapper.EventMasterMapper;
import com.crmportal.repository.BanquetHallMasterRepository;
import com.crmportal.repository.BanquetHallShiftBookingRepository;
import com.crmportal.repository.BanquetShiftMasterRepository;
import com.crmportal.repository.CustomPackageRepository;
import com.crmportal.repository.EventFunctionManagerAssignRepository;
import com.crmportal.repository.EventFunctionMasterRepository;
import com.crmportal.repository.EventFunctionMenuAllocationRepository;
import com.crmportal.repository.EventFunctionQuotationItemRepository;
import com.crmportal.repository.EventFunctionQuotationRepository;
import com.crmportal.repository.EventFunctionStaffAssignmentRepository;
import com.crmportal.repository.EventFunctionStaffTaskRepository;
import com.crmportal.repository.EventFunctionStaffUploadRepository;
import com.crmportal.repository.EventGroundTaskMasterRepository;
import com.crmportal.repository.EventInvoiceRepository;
import com.crmportal.repository.EventLaborRepository;
import com.crmportal.repository.EventMasterRepository;
import com.crmportal.repository.EventRawMaterialFunctionsRepository;
import com.crmportal.repository.EventRawMaterialRepository;
import com.crmportal.repository.EventRoomMasterRepository;
import com.crmportal.repository.EventTermsAndConditionFeaturesRepository;
import com.crmportal.repository.EventTermsAndConditionRepository;
import com.crmportal.repository.EventTypeMasterRepository;
import com.crmportal.repository.EvetFunctionMenuAllocationOrderRepository;
import com.crmportal.repository.FunctionMasterRepository;
import com.crmportal.repository.MealTypeMasterRepository;
import com.crmportal.repository.MenuAllocationItemRawMaterialRepository;
import com.crmportal.repository.MenuItemMasterRepository;
import com.crmportal.repository.MenuPreparationDetailsRepository;
import com.crmportal.repository.MenuPreparationRepository;
import com.crmportal.repository.PartyMasterRepository;
import com.crmportal.repository.RoomMasterRepository;
import com.crmportal.repository.TermsAndConditionFeaturesRepository;
import com.crmportal.repository.UnitMasterRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.repository.UserTermsAndConditionRepository;
import com.crmportal.repository.VenueMasterRepository;
import com.crmportal.request.dto.AssignEventToChildRequestDto;
import com.crmportal.request.dto.BanquetHallShiftRequestDto;
import com.crmportal.request.dto.BanquetShiftBookingRequestDto;
import com.crmportal.request.dto.EventFunctionManagerAssignListRequestDto;
import com.crmportal.request.dto.EventFunctionManagerAssignRequestDto;
import com.crmportal.request.dto.EventFunctionMasterRequestDto;
import com.crmportal.request.dto.EventFunctionStaffImagesRequestDto;
import com.crmportal.request.dto.EventFunctionStaffTaskRequestDto;
import com.crmportal.request.dto.EventFunctionVendorAssignmentRequestDto;
import com.crmportal.request.dto.EventMasterRequestDto;
import com.crmportal.request.dto.EventRemarksRequestDto;
import com.crmportal.request.dto.EventRoomMasterRequestDto;
import com.crmportal.request.dto.EventVendorAssignmentWrapperDto;
import com.crmportal.response.dto.AssignEventToChildResponseDto;
import com.crmportal.response.dto.BanquetHallShiftInfoDto;
import com.crmportal.response.dto.BanquetShiftAvailabilityResponseDto;
import com.crmportal.response.dto.EventByDateResponseDto;
import com.crmportal.response.dto.EventFunctionManagerListResponseDto;
import com.crmportal.response.dto.EventFunctionManagersResponseDto;
import com.crmportal.response.dto.EventFunctionMasterResponseDto;
import com.crmportal.response.dto.EventFunctionStaffImagesResponseDto;
import com.crmportal.response.dto.EventFunctionStaffTaskResponseDto;
import com.crmportal.response.dto.EventFunctionWithManagersResponseDto;
import com.crmportal.response.dto.EventLabourOverviewResponse;
import com.crmportal.response.dto.EventMasterResponseDto;
import com.crmportal.response.dto.EventMenuAllocationChefLabourOverviewResponseDto;
import com.crmportal.response.dto.EventMenuAllocationItemOverviewResponseDto;
import com.crmportal.response.dto.EventMenuAllocationOutSideOverviewResponseDto;
import com.crmportal.response.dto.EventMenuAllocationOverviewResponseDto;
import com.crmportal.response.dto.EventOverViewResponseDto;
import com.crmportal.response.dto.ManagerEventFunctionsResponseDto;
import com.crmportal.response.dto.ManagerEventsResponseDto;
import com.crmportal.service.BanquetShiftService;
import com.crmportal.service.CommonService;
import com.crmportal.service.EventCopyService;
import com.crmportal.service.EventMasterService;
import com.crmportal.service.EventRoomMasterService;
import com.crmportal.service.UserFileService;
import com.crmportal.service.UserTermsAndConditionService;
import com.crmportal.utility.DateMapper;
import com.crmportal.utility.EventMasterSpecification;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

@Service
@Transactional
public class EventMasterServiceImpl implements EventMasterService {

	@Autowired
	EventMasterMapper eventMasterMapper;

	@Autowired
	EventMasterRepository eventMasterRepository;

	@Autowired
	CommonService commonService;

	@Autowired
	UserMasterRepository userMasterRepository;

	@Autowired
	MealTypeMasterRepository mealTypeMasterRepository;

	@Autowired
	PartyMasterRepository partyMasterRepository;

	@Autowired
	EventFunctionMasterMapper eventFunctionMasterMapper;

	@Autowired
	private EventRoomMasterService eventRoomMasterService;

	@Autowired
	private EventRoomMasterRepository eventRoomMasterRepository;

	@Autowired
	private RoomMasterRepository roomMasterRepository;

	@Autowired
	EventFunctionMasterRepository eventFunctionMasterRepository;

	@Autowired
	FunctionMasterRepository functionMasterRepository;

	@Autowired
	EventTypeMasterRepository eventTypeMasterRepository;

	@Autowired
	MenuPreparationRepository menuPreparationRepository;

	@Autowired
	MenuPreparationDetailsRepository menuPreparationDetailsRepository;

	@Autowired
	VenueMasterRepository venueMasterRepository;

	@Autowired
	DateMapper dateMapper;

	@Autowired
	EventInvoiceRepository eventInvoiceRepository;

	@Autowired
	EventFunctionQuotationRepository eventFunctionQuotationRepository;

	@Autowired
	EventFunctionMenuAllocationRepository allocationRepository;

	@Autowired
	MenuAllocationItemRawMaterialRepository menuAllocationItemRawMaterRepository;

	@Autowired
	EvetFunctionMenuAllocationOrderRepository menuAllocationOrderRepository;

	@Autowired
	EventRawMaterialFunctionsRepository eventRawMaterialFunctionsRepository;

	@Autowired
	EventRawMaterialRepository eventRawMaterialRepository;

	@Autowired
	EventRawMaterialHelperService eventrawmaterialhelperservice;

	@Autowired
	EventFunctionQuotationItemRepository eventFunctionQuotationItemRepository;

	@Autowired
	EventFunctionManagerAssignRepository eventFunctionManagerAssignRepository;

	@Autowired
	EventFunctionStaffAssignmentRepository eventFunctionStaffAssignmentRepository;

	@Autowired
	EventGroundTaskMasterRepository eventGroundTaskMasterRepository;

	@Autowired
	EventLaborRepository eventLaborRepository;

	@Autowired
	EventFunctionStaffTaskRepository eventFunctionStaffTaskRepository;

	@Autowired
	UnitMasterRepository unitMasterRepository;

	@Autowired
	EventFunctionStaffUploadRepository eventFunctionStaffUploadRepository;

	@Autowired
	EventCopyService eventCopyService;

	@Autowired
	BanquetHallMasterRepository banquetHallMasterRepository;

	@Autowired
	private BanquetShiftService banquetShiftService;

	@Autowired
	private BanquetHallShiftBookingRepository bookingRepository;

	@Autowired
	private CustomPackageRepository customPackageRepository;

	@Autowired
	private UserTermsAndConditionService userTermsAndConditionService;

	@Autowired
	private EventTermsAndConditionRepository eventTermsAndConditionRepository;

	@Autowired
	private EventTermsAndConditionFeaturesRepository eventTermsAndConditionFeaturesRepository;

	@Autowired
	UserTermsAndConditionRepository userTermsAndConditionRepository;

	@Autowired
	TermsAndConditionFeaturesRepository termsAndConditionFeaturesRepository;

	@Autowired
	private BanquetShiftMasterRepository shiftRepository;

	@Autowired
	BanquetHallShiftBookingRepository banquetHallShiftBookingRepository;

	@Autowired
	MenuItemMasterRepository menuItemRepository;

	private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	@Autowired
	UserFileService userFileService;

	@Autowired
	Environment environment;

	@Override
	@SuppressWarnings("unchecked")
	public EventMasterResponseDto addOrUpdateEventMaster(@Valid EventMasterRequestDto request, long id) {
		UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(request.getUserId())
				.orElseThrow(() -> new RuntimeException("User not found with id : " + request.getUserId()));

		Boolean eventStatusChanged = false;
		EventMasterEntity entity;
		String eventNum;
		if (id == -1) {
			eventNum = commonService.getLastestEventNo(request.getUserId());
			entity = eventMasterMapper.requestToEntity(request);
			entity.setEventNo(eventNum);
			eventStatusChanged = true;
			entity.setIsRMenu(true);
		} else {
			entity = eventMasterRepository.findByIdAndIsDeleteFalse(id)
					.orElseThrow(() -> new RuntimeException("Event Master with this eventId not found: " + id));
			if (entity.getStatus() != request.getStatus()) {
				eventStatusChanged = true;
			}
			entity = eventMasterMapper.updateEntityFromRequest(request, entity);
			entity.setUpdatedAt(commonService.getCurrentDateTime());
		}
		entity.setUser(userMasterRepository.findByIdAndIsDeleteFalse(request.getUserId())
				.orElseThrow(() -> new RuntimeException("User not found with id: " + request.getUserId())));

		UserMasterEntity manager = userMasterRepository.findByIdAndIsDeleteFalse(request.getManagerId())
				.orElseThrow(() -> new RuntimeException("Manager not found with id: " + request.getManagerId()));

		entity.setManager(manager);

		PartyMasterEntity party = partyMasterRepository.findByIdAndIsDeleteFalse(request.getPartyId())
				.orElseThrow(() -> new RuntimeException("Party not found with id: " + request.getPartyId()));

		entity.setParty(party);

		entity.setMealType(mealTypeMasterRepository.findByIdAndIsDeleteFalse(request.getMealTypeId())
				.orElseThrow(() -> new RuntimeException("Meal Type not found with id: " + request.getMealTypeId())));

		entity.setEventType(eventTypeMasterRepository.findByIdAndIsDeleteFalse(request.getEventTypeId())
				.orElseThrow(() -> new RuntimeException("Event Type not found with id: " + request.getEventTypeId())));

		entity.setMenuPreparationStatus("PENDING");

//		entity.setVenue(
//			    request.getVenueId() != null && request.getVenueId() != 0
//			        ? venueMasterRepository.findByIdAndIsDeleteFalse(request.getVenueId())
//			        : null
//			);

		if (request.getVenueId() != null && request.getVenueId() != 0) {
			entity.setVenue(venueMasterRepository.findByIdAndIsDeleteFalse(request.getVenueId()));
		} else {
			entity.setVenue(null);
		}

		// Add banquet hall:
		if (request.getBanquetHallId() != null && request.getBanquetHallId() > 0) {

			BanquetHallMasterEntity hall = banquetHallMasterRepository
					.findByIdAndIsDeleteFalse(request.getBanquetHallId()).orElseThrow(() -> new RuntimeException(
							"Banquet hall not found with id: " + request.getBanquetHallId()));

			entity.setBanquetHall(hall);

			System.out.println("Hall Set Successfully : " + hall.getId() + " - " + hall.getHallName());

		} else {
			entity.setBanquetHall(null);
			System.out.println("Hall Set NULL");
		}
		entity = eventMasterRepository.save(entity);

		// ── Book banquet shift if hall and shift selected ─────────────────────
		if (request.getBanquetHallId() != null && request.getBanquetHallId() > 0 && request.getShiftId() != null
				&& request.getShiftId() > 0 && request.getBookingDate() != null
				&& !request.getBookingDate().isEmpty()) {

			try {

				if (id != -1) {
					banquetShiftService.releaseBookingsByEvent(entity.getId());
				}

				BanquetShiftBookingRequestDto bookingRequest = new BanquetShiftBookingRequestDto();

				bookingRequest.setHallId(request.getBanquetHallId());
				bookingRequest.setShiftId(request.getShiftId());
				bookingRequest.setEventId(entity.getId());
				bookingRequest.setBookingDate(request.getBookingDate());
				bookingRequest.setUserId(request.getUserId());

				banquetShiftService.bookShift(bookingRequest);

			} catch (RuntimeException e) {
				throw new RuntimeException(e.getMessage());
			}
		}
		// ─────────────────────────────────────────────────────────────────────

		if (id != -1) {
			eventRoomMasterRepository.deleteByEventId(entity.getId());
		}

		if (request.getEventRooms() != null && !request.getEventRooms().isEmpty()) {
			List<EventRoomMasterEntity> roomEntities = new ArrayList<>();
			for (EventRoomMasterRequestDto roomDto : request.getEventRooms()) {
				if (roomDto.getRoomId() == null || roomDto.getRoomId() <= 0) {
					continue;
				}

				EventRoomMasterEntity roomEntity = new EventRoomMasterEntity();
				roomEntity.setEvent(entity);
				roomEntity.setRoom(roomMasterRepository.findById(roomDto.getRoomId())
						.orElseThrow(() -> new RuntimeException("Room not found with id : " + roomDto.getRoomId())));
				roomEntity.setBookingdate(roomDto.getBookingdate());
				roomEntity.setBookingcheckoutdate(roomDto.getBookingcheckoutdate());
				roomEntity.setPrice(roomDto.getPrice() != null ? roomDto.getPrice() : 0);
				roomEntity.setQty(roomDto.getQty() != null ? roomDto.getQty() : 1);
				roomEntity.setTotal(roomDto.getTotal() != null ? roomDto.getTotal() : 0);
				roomEntities.add(roomEntity);
			}

			if (!roomEntities.isEmpty()) {
				eventRoomMasterRepository.saveAll(roomEntities);
			}
		}

		if (request.getEventFunction() != null && !request.getEventFunction().isEmpty()) {
			for (EventFunctionMasterRequestDto dto : request.getEventFunction()) {
				EventFunctionMasterEntity functionEntity;
				Boolean isUpdatePax = false;
				Boolean isUpdateRate = false;
				Boolean isPackageUpdate = false;
				Boolean isFunctionDateUpdate = false;
				if (dto.getEventFuncId() == 0) {
					functionEntity = eventFunctionMasterMapper.requestToEntity(dto);
				} else {
					functionEntity = eventFunctionMasterRepository.findByIdAndIsDeleteFalse(dto.getEventFuncId())
							.orElseThrow(() -> new RuntimeException(
									"Event Function Master not found with id: " + dto.getEventFuncId()));
					if (!Objects.equals(functionEntity.getPax(), dto.getPax())) {
						menuAllocationItemRawMaterRepository.deleteRawMaterialsByEventAndEventFunction(entity.getId(),
								functionEntity.getId());
						List<Long> eventrawmaterialid = eventRawMaterialFunctionsRepository
								.findDistinctEventRawMaterialIdsByEventIdAndEventFunctionId(entity.getId(),
										functionEntity.getId());
						Set<Long> setEventRawMaterialId = new HashSet<>(eventrawmaterialid);
						eventRawMaterialFunctionsRepository.deleteByEventIdAndEventFunctionId(entity.getId(),
								functionEntity.getId());
						eventrawmaterialhelperservice.updateQtyFromFunctions(setEventRawMaterialId);
						functionEntity.setIsUpdate(true);
						isUpdatePax = true;
					}

					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a", Locale.ENGLISH);

					LocalDateTime dtoStartDateTime = LocalDateTime.parse(dto.getFunctionStartDateTime(), formatter);

					if (!Objects.equals(functionEntity.getFunctionStartDateTime(), dtoStartDateTime)) {
						isFunctionDateUpdate = true;
					}

					if (isFunctionDateUpdate) {
						eventFunctionQuotationItemRepository.updateFunctionDate(dtoStartDateTime,
								functionEntity.getId());
					}

					if (!Objects.equals(functionEntity.getRate(), dto.getRate())) {
						isUpdateRate = true;
					}
					Long existingPackageId = functionEntity.getCustomPackage() != null
							? functionEntity.getCustomPackage().getId()
							: 0;

					if (!Objects.equals(existingPackageId, dto.getCustomPackageId())) {
						isPackageUpdate = true;
					}
					if (isUpdatePax || isUpdateRate) {
						BigDecimal rate = dto.getRate() != null ? BigDecimal.valueOf(dto.getRate()) : BigDecimal.ZERO;
						eventFunctionQuotationItemRepository.updatePaxAndRate(functionEntity.getId(), isUpdatePax,
								isUpdateRate, dto.getPax(), rate);
					}

					if (isPackageUpdate) {
						if (dto.getCustomPackageId() != null && dto.getCustomPackageId() != 0) {
							CustomPackageEntity packageEntity = customPackageRepository
									.findByIdAndIsDeleteFalse(dto.getCustomPackageId());
							eventFunctionQuotationItemRepository.updatePackage(functionEntity.getId(),
									packageEntity.getId(), packageEntity.getNameEnglish(), packageEntity.getPrice());
							MenuPreparationEntity menuPreparationEntity = menuPreparationRepository
									.findByEventFunction_IdAndIsDeleteFalse(functionEntity.getId());
							if (menuPreparationEntity != null) {
								System.out.println("in event update package");
								menuPreparationEntity.setPackageName(packageEntity.getNameEnglish());
								menuPreparationEntity.setPackagePrice(BigDecimal.valueOf(dto.getRate()));
								menuPreparationRepository.save(menuPreparationEntity);
								menuPreparationDetailsRepository.deleteAllByMenuPreparation(menuPreparationEntity);
							}
						} else {
							eventFunctionQuotationItemRepository.updatePackage(functionEntity.getId(), null, "",
									new BigDecimal(functionEntity.getRate()));
						}

					}

					functionEntity = eventFunctionMasterMapper.updateEntityFromRequest(dto, functionEntity);
					functionEntity.setUpdatedAt(commonService.getCurrentDateTime());
				}
				functionEntity.setSortorder(dto.getSortorder());
				functionEntity.setEvent(entity);
				functionEntity.setFunction(functionMasterRepository.findByIdAndIsDeleteFalse(dto.getFunctionId())
						.orElseThrow(() -> new RuntimeException("Function not found with id: " + dto.getFunctionId())));
				// Set custom package function-wise
				if (dto.getCustomPackageId() != null && dto.getCustomPackageId() > 0) {
					functionEntity.setCustomPackage(
							customPackageRepository.findByIdAndIsDeleteFalse(dto.getCustomPackageId()));
				} else {
					functionEntity.setCustomPackage(null);
				}

				eventFunctionMasterRepository.save(functionEntity);

				// ── START the Book banquet shift function-wise
				// logic─────────────────────────────────
				if (dto.getBanquetHallShifts() != null && !dto.getBanquetHallShifts().isEmpty()) {
					try {
						if (dto.getEventFuncId() != 0) {
							banquetShiftService.releaseBookingsByEventFunction(functionEntity.getId());
						}
						for (BanquetHallShiftRequestDto hallShift : dto.getBanquetHallShifts()) {
							if (hallShift.getBanquetHallId() == null || hallShift.getBanquetHallId() <= 0
									|| hallShift.getShiftId() == null || hallShift.getShiftId() <= 0
									|| hallShift.getBookingDate() == null || hallShift.getBookingDate().isEmpty()) {
								continue;
							}
							BanquetShiftBookingRequestDto fnBooking = new BanquetShiftBookingRequestDto();
							fnBooking.setHallId(hallShift.getBanquetHallId());
							fnBooking.setShiftId(hallShift.getShiftId());
							fnBooking.setEventId(entity.getId());
							fnBooking.setEventFunctionId(functionEntity.getId());
							fnBooking.setBookingDate(hallShift.getBookingDate());
							fnBooking.setUserId(request.getUserId());
							banquetShiftService.bookShiftForFunction(fnBooking);
						}
					} catch (RuntimeException e) {
						throw new RuntimeException("Function shift booking failed: " + e.getMessage());
					}
				}
				// end of functionwise hall save logic
			}
		}

		EventMasterEntity updatedEntity = eventMasterRepository.findByIdAndIsDeleteFalse(entity.getId())
				.orElseThrow(() -> new RuntimeException("Event not found after save!"));

		EventMasterResponseDto responseDto = eventMasterMapper.entityToResponse(updatedEntity);

		// Set banquet hall manually
		if (updatedEntity.getBanquetHall() != null) {

			responseDto.setBanquetHallId(updatedEntity.getBanquetHall().getId());

			responseDto.setBanquetHallName(updatedEntity.getBanquetHall().getHallName());

		} else {

			responseDto.setBanquetHallId(null);
			responseDto.setBanquetHallName("ODC");
		}

		// Set booking details
		try {

			List<BanquetHallShiftBookingEntity> bookings = bookingRepository
					.findEventLevelBookings(updatedEntity.getId());

			if (bookings != null && !bookings.isEmpty()) {

				BanquetHallShiftBookingEntity booking = bookings.get(0);

				// Hall Details
				if (booking.getHall() != null) {

					responseDto.setBanquetHallId(booking.getHall().getId());

					responseDto.setBanquetHallName(booking.getHall().getHallName());
				}
				// Shift Details
				if (booking.getShift() != null) {

					responseDto.setShiftId(booking.getShift().getId());

					responseDto.setShiftName(booking.getShift().getShiftName());

					responseDto.setShiftStartTime(booking.getShift().getStartTime());

					responseDto.setShiftEndTime(booking.getShift().getEndTime());
				}

				// Booking Date
				if (booking.getBookingDate() != null) {

					responseDto
							.setBookingDate(booking.getBookingDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
				}
			}

//			UserTermsAndConditionEntity dto = userTermsAndConditionRepository
//					.findByUserIdAndNameEnglishAndIsDeleteFalseAndIsActiveTrue(request.getUserId(), "Menu Report");
//			
//			EventTermsAndConditionEntity eventTermsAndConditionEntity = new EventTermsAndConditionEntity();
//
//			eventTermsAndConditionEntity.setEventId(entity.getId());
//			eventTermsAndConditionEntity.setUser(user);
//
//			eventTermsAndConditionEntity.setNameEnglish(dto.getNameEnglish());
//			eventTermsAndConditionEntity.setNameHindi(dto.getNameHindi());
//			eventTermsAndConditionEntity.setNameGujarati(dto.getNameGujarati());
//
//			eventTermsAndConditionEntity.setIsDelete(false);
//
//			eventTermsAndConditionEntity = eventTermsAndConditionRepository.save(eventTermsAndConditionEntity);
//
//			List<TermsAndConditionFeaturesEntity> features = termsAndConditionFeaturesRepository
//					.findByUserTermsConditionIdAndIsDeleteFalse(dto.getId());
//
//			// Save Features
//			if (features != null) {
//
//				List<EventTermsAndConditionFeaturesEntity> featureEntities = new ArrayList<>();
//
//				for (TermsAndConditionFeaturesEntity featureDto : features) {
//
//					EventTermsAndConditionFeaturesEntity featureEntity = new EventTermsAndConditionFeaturesEntity();
//
//					featureEntity.setEventTermsConditionId(eventTermsAndConditionEntity.getId());
//
//					featureEntity.setDescription(featureDto.getDescription());
//					featureEntity.setDescriptionHindi(featureDto.getDescriptionHindi());
//					featureEntity.setDescriptionGujarati(featureDto.getDescriptionGujarati());
//
//					featureEntities.add(featureEntity);
//				}
//
//				eventTermsAndConditionFeaturesRepository.saveAll(featureEntities);
//			}

		} catch (Exception e) {

			System.out.println("Booking fetch failed : " + e.getMessage());
		}

		if (entity.getId() == -1) {
			UserTermsAndConditionEntity dto = userTermsAndConditionRepository
					.findByUserIdAndNameEnglishAndIsDeleteFalseAndIsActiveTrue(request.getUserId(), "Menu Report");

			EventTermsAndConditionEntity eventTermsAndConditionEntity = new EventTermsAndConditionEntity();

			eventTermsAndConditionEntity.setEventId(entity.getId());
			eventTermsAndConditionEntity.setUser(user);

			eventTermsAndConditionEntity.setNameEnglish(dto.getNameEnglish());
			eventTermsAndConditionEntity.setNameHindi(dto.getNameHindi());
			eventTermsAndConditionEntity.setNameGujarati(dto.getNameGujarati());

			eventTermsAndConditionEntity.setIsDelete(false);

			eventTermsAndConditionEntity = eventTermsAndConditionRepository.save(eventTermsAndConditionEntity);

			List<TermsAndConditionFeaturesEntity> features = termsAndConditionFeaturesRepository
					.findByUserTermsConditionIdAndIsDeleteFalse(dto.getId());

			// Save Features
			if (features != null) {

				List<EventTermsAndConditionFeaturesEntity> featureEntities = new ArrayList<>();

				for (TermsAndConditionFeaturesEntity featureDto : features) {

					EventTermsAndConditionFeaturesEntity featureEntity = new EventTermsAndConditionFeaturesEntity();

					featureEntity.setEventTermsConditionId(eventTermsAndConditionEntity.getId());

					featureEntity.setDescription(featureDto.getDescription());
					featureEntity.setDescriptionHindi(featureDto.getDescriptionHindi());
					featureEntity.setDescriptionGujarati(featureDto.getDescriptionGujarati());

					featureEntities.add(featureEntity);
				}

				eventTermsAndConditionFeaturesRepository.saveAll(featureEntities);
			}
		}
		System.out.println("eventStatusChanged:- " + eventStatusChanged);
		responseDto.setIsEventStatusChanged(eventStatusChanged);
		responseDto.setCompanyName(user.getUserBasicDetails().getCompanyName());
		responseDto.setCompanyMobileNo(user.getContactNo());
		responseDto.setManagerMobileNo(manager.getContactNo());
		responseDto.setManagerName(manager.getFirstName() + " " + manager.getLastName());
		responseDto.setPartyName(party.getNameEnglish());
		responseDto.setPartyMobileNo(party.getMobileno());
		return responseDto;
	}

	@Override
	public AssignEventToChildResponseDto assignEventsToChildUser(AssignEventToChildRequestDto request) {

		if (request.getChildUserId() == null) {
			throw new RuntimeException("Child User Id is required");
		}

		if (request.getEventIds() == null || request.getEventIds().isEmpty()) {
			throw new RuntimeException("Event Id list is required");
		}

		int copiedCount = 0;

		for (Long eventId : request.getEventIds()) {
			EventMasterEntity newEvent = eventCopyService.copyEvent(eventId, request.getChildUserId(),
					request.getPaxPercentage());
			copiedCount++;
		}

		return new AssignEventToChildResponseDto(true, "Events assigned successfully", copiedCount);

	}

	public void removeMenuItems(EventMasterEntity eventEntity, EventFunctionMasterEntity eventFunctionEntity) {
		try {
			menuAllocationItemRawMaterRepository.deleteRawMaterialsByEventAndEventFunction(eventEntity.getId(),
					eventFunctionEntity.getId());

			menuAllocationOrderRepository.deleteOrdersByEventAndEventFunction(eventEntity.getId(),
					eventFunctionEntity.getId());

			allocationRepository.deleteMenuAllocationsByEventAndEventFunction(eventEntity.getId(),
					eventFunctionEntity.getId());

		} catch (Exception e2) {
			e2.printStackTrace();
		}
	}

	@Override
	public List<EventMasterResponseDto> getAllByUserId(Long userId, String partyName, Boolean isVisible,
			Boolean isChildUser, String month, String year, Integer status) {
		List<EventMasterEntity> entities = new ArrayList<>();
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a");

		// Fetch User
		Optional<UserMasterEntity> userOpt = userMasterRepository.findByIdAndIsDeleteFalse(userId);
		if (!userOpt.isPresent()) {
			return Collections.emptyList();
		}
		UserMasterEntity user = userOpt.get();
		// Fetch Events
		if (Boolean.TRUE.equals(isVisible)) {
			entities = eventMasterRepository.findEventsSorted(userId, partyName, month, year, status);
		} else {
			entities = eventMasterRepository.findTop10Events(userId, partyName, month, year, status);
		}

		if (entities.isEmpty()) {
			return Collections.emptyList();
		}

		List<EventMasterResponseDto> responseDtos = new ArrayList<>();

		for (EventMasterEntity entity : entities) {
			EventMasterResponseDto responseDto = eventMasterMapper.entityToResponse(entity);
			// Format top-level Event dates
			if (entity.getInquiryDate() != null) {
				responseDto.setInquiryDate(entity.getInquiryDate().format(dateFormatter));
			}
			if (entity.getBrideBirthDate() != null) {
				responseDto.setBrideBirthDate(entity.getBrideBirthDate().format(dateFormatter));
			}

			if (entity.getGroomBirthDate() != null) {
				responseDto.setGroomBirthDate(entity.getGroomBirthDate().format(dateFormatter));
			}
			if (entity.getCreatedAt() != null) {
				responseDto.setCreatedAt(entity.getCreatedAt().format(dateFormatter));
			}
			if (entity.getEventStartDateTime() != null) {
				responseDto.setEventStartDateTime(entity.getEventStartDateTime().format(dateTimeFormatter));
			}
			if (entity.getEventEndDateTime() != null) {
				responseDto.setEventEndDateTime(entity.getEventEndDateTime().format(dateTimeFormatter));
			}

			responseDto.setUserId(entity.getUser().getId());
			responseDto.setManagerId(entity.getManager().getId() == null ? null : entity.getManager().getId());

			List<EventFunctionMasterEntity> eventFunctionMasterEntities = eventFunctionMasterRepository
					.findAllByEventIdAndIsDeleteFalseOrderBySortorderAsc(entity.getId());

			List<EventFunctionMasterResponseDto> functionDtos = new ArrayList<>();
			if (eventFunctionMasterEntities != null && !eventFunctionMasterEntities.isEmpty()) {
				for (EventFunctionMasterEntity fnEntity : eventFunctionMasterEntities) {
					if (Boolean.FALSE.equals(fnEntity.getIsDelete())) {
						EventFunctionMasterResponseDto fnDto = eventFunctionMasterMapper.entityToResponse(fnEntity);

						if (fnEntity.getCreatedAt() != null) {
							fnDto.setCreatedAt(fnEntity.getCreatedAt().format(dateFormatter));
						}

						fnDto.setEventId(entity.getId());

						// ── NEW ──────────────────────────────────────────────────────────────────
						List<BanquetHallShiftBookingEntity> fnBookings = bookingRepository
								.findFunctionLevelBookingsAll(fnEntity.getId());

						if (!fnBookings.isEmpty()) {
							List<BanquetHallShiftInfoDto> hallShiftList = new ArrayList<>();
							for (BanquetHallShiftBookingEntity fnBooking : fnBookings) {
								BanquetHallShiftInfoDto info = new BanquetHallShiftInfoDto();
								if (fnBooking.getHall() != null) {
									info.setBanquetHallId(fnBooking.getHall().getId());
									info.setBanquetHallName(fnBooking.getHall().getHallName());
								}
								if (fnBooking.getShift() != null) {
									info.setShiftId(fnBooking.getShift().getId());
									info.setShiftName(fnBooking.getShift().getShiftName());
									info.setShiftStartTime(fnBooking.getShift().getStartTime());
									info.setShiftEndTime(fnBooking.getShift().getEndTime());
								}
								if (fnBooking.getBookingDate() != null) {
									info.setBookingDate(fnBooking.getBookingDate()
											.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
								}
								hallShiftList.add(info);
							}
							fnDto.setBanquetHallShifts(hallShiftList);

							// Backward-compat: keep single fields from first booking
							BanquetHallShiftBookingEntity first = fnBookings.get(0);
							if (first.getHall() != null) {
								fnDto.setBanquetHallId(first.getHall().getId());
								fnDto.setBanquetHallName(first.getHall().getHallName());
							}
							if (first.getShift() != null) {
								fnDto.setShiftId(first.getShift().getId());
								fnDto.setShiftName(first.getShift().getShiftName());
								fnDto.setShiftStartTime(first.getShift().getStartTime());
								fnDto.setShiftEndTime(first.getShift().getEndTime());
							}
							if (first.getBookingDate() != null) {
								fnDto.setBookingDate(
										first.getBookingDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
							}
						}

						functionDtos.add(fnDto);

						if (fnEntity.getCustomPackage() != null) {
							fnDto.setCustomPackageId(fnEntity.getCustomPackage().getId());
							fnDto.setCustomPackageName(fnEntity.getCustomPackage().getNameEnglish());
						} else {
							fnDto.setCustomPackageId(null);
							fnDto.setCustomPackageName(null);
						}
						// ─────────────────────────────────────────────────────────────────────
					}
				}
			}

			// Fetch booking
			List<BanquetHallShiftBookingEntity> eventBookings = bookingRepository.findEventLevelBookings(entity.getId(),
					org.springframework.data.domain.PageRequest.of(0, 1));

			if (!eventBookings.isEmpty()) {
				BanquetHallShiftBookingEntity booking = eventBookings.get(0);

				if (booking.getBookingDate() != null) {
					responseDto.setBookingDate(booking.getBookingDate().format(dateFormatter));
				}
				if (booking.getShift() != null) {
					responseDto.setShiftId(booking.getShift().getId());
					responseDto.setShiftName(booking.getShift().getShiftName());
					responseDto.setShiftStartTime(booking.getShift().getStartTime());
					responseDto.setShiftEndTime(booking.getShift().getEndTime());
				}
				if (booking.getHall() != null) {
					responseDto.setBanquetHallId(booking.getHall().getId());
					responseDto.setBanquetHallName(booking.getHall().getHallName());
				}
			}

			responseDto.setEventFunctions(functionDtos);
			responseDto.setEventRooms(eventRoomMasterService.getByEventId(entity.getId()));
			responseDtos.add(responseDto);
		}

		return filterEventsByChildUser(responseDtos, isChildUser, userId);
	}

	private List<EventMasterResponseDto> filterEventsByChildUser(List<EventMasterResponseDto> responseDtos,
			Boolean isChildUser, Long childUserId) {

		return responseDtos.stream()
				.filter(event -> Boolean.TRUE.equals(isChildUser)
						? event.getChilduserid() != null && event.getChilduserid().equals(childUserId)
						: event.getChilduserid() == null || event.getChilduserid().equals(0L))
				.collect(Collectors.toList());
	}

	@Override
	public EventMasterResponseDto getEventMasterById(Long eventId) {
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a");

		Optional<EventMasterEntity> optEntity = eventMasterRepository.findByIdAndIsDeleteFalse(eventId);
		if (!optEntity.isPresent()) {
			return null;
		}

		// ── entity and responseDto declared FIRST ─────────────────────────────
		EventMasterEntity entity = optEntity.get();
		EventMasterResponseDto responseDto = eventMasterMapper.entityToResponse(entity);

		if (entity.getInquiryDate() != null) {
			responseDto.setInquiryDate(entity.getInquiryDate().format(dateFormatter));
		}
		if (entity.getBrideBirthDate() != null) {
			responseDto.setBrideBirthDate(entity.getBrideBirthDate().format(dateFormatter));
		}
		if (entity.getGroomBirthDate() != null) {
			responseDto.setGroomBirthDate(entity.getGroomBirthDate().format(dateFormatter));
		}
		if (entity.getCreatedAt() != null) {
			responseDto.setCreatedAt(entity.getCreatedAt().format(dateFormatter));
		}
		if (entity.getEventStartDateTime() != null) {
			responseDto.setEventStartDateTime(entity.getEventStartDateTime().format(dateTimeFormatter));
		}
		if (entity.getEventEndDateTime() != null) {
			responseDto.setEventEndDateTime(entity.getEventEndDateTime().format(dateTimeFormatter));
		}

		responseDto.setUserId(entity.getUser().getId());
		responseDto.setManagerId(entity.getManager().getId() == null ? null : entity.getManager().getId());

		// ── Fetch booked shift — AFTER entity and responseDto are ready ───────
		try {

			List<BanquetHallShiftBookingEntity> eventBookings = bookingRepository.findEventLevelBookings(entity.getId(),
					org.springframework.data.domain.PageRequest.of(0, 1));

			if (!eventBookings.isEmpty()) {
				BanquetHallShiftBookingEntity booking = eventBookings.get(0);

				if (booking.getHall() != null) {
					responseDto.setBanquetHallId(booking.getHall().getId());
					responseDto.setBanquetHallName(booking.getHall().getHallName());
				}
				if (booking.getShift() != null) {
					responseDto.setShiftId(booking.getShift().getId());
					responseDto.setShiftName(booking.getShift().getShiftName());
					responseDto.setShiftStartTime(booking.getShift().getStartTime());
					responseDto.setShiftEndTime(booking.getShift().getEndTime());
				}
				if (booking.getBookingDate() != null) {
					responseDto
							.setBookingDate(booking.getBookingDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
				}
			}

		} catch (Exception e) {

			System.out.println("Shift booking fetch failed for event: " + eventId + " → " + e.getMessage());
		}

		// ── Event functions ───────────────────────────────────────────────────
		List<EventFunctionMasterResponseDto> fnDtos = new ArrayList<>();
		List<EventFunctionMasterEntity> eventFunctionMasterEntities = eventFunctionMasterRepository
				.findAllByEventIdAndIsDeleteFalseOrderBySortorderAsc(entity.getId());

		if (eventFunctionMasterEntities != null && !eventFunctionMasterEntities.isEmpty()) {

			for (EventFunctionMasterEntity fnEntity : eventFunctionMasterEntities) {
				if (Boolean.FALSE.equals(fnEntity.getIsDelete())) {

					EventFunctionMasterResponseDto fnDto = eventFunctionMasterMapper.entityToResponse(fnEntity);

					if (fnEntity.getCreatedAt() != null) {
						fnDto.setCreatedAt(fnEntity.getCreatedAt().format(dateFormatter));
					}
					if (fnEntity.getFunctionStartDateTime() != null) {
						fnDto.setFunctionStartDateTime(fnEntity.getFunctionStartDateTime().format(dateTimeFormatter));
					}
					if (fnEntity.getFunctionEndDateTime() != null) {
						fnDto.setFunctionEndDateTime(fnEntity.getFunctionEndDateTime().format(dateTimeFormatter));
					}

					fnDto.setEventId(eventId);

					// ── NEW ──────────────────────────────────────────────────────────────────
					List<BanquetHallShiftBookingEntity> fnBookings = bookingRepository
							.findFunctionLevelBookingsAll(fnEntity.getId());

					if (!fnBookings.isEmpty()) {
						List<BanquetHallShiftInfoDto> hallShiftList = new ArrayList<>();
						for (BanquetHallShiftBookingEntity fnBooking : fnBookings) {
							BanquetHallShiftInfoDto info = new BanquetHallShiftInfoDto();
							if (fnBooking.getHall() != null) {
								info.setBanquetHallId(fnBooking.getHall().getId());
								info.setBanquetHallName(fnBooking.getHall().getHallName());
							}
							if (fnBooking.getShift() != null) {
								info.setShiftId(fnBooking.getShift().getId());
								info.setShiftName(fnBooking.getShift().getShiftName());
								info.setShiftStartTime(fnBooking.getShift().getStartTime());
								info.setShiftEndTime(fnBooking.getShift().getEndTime());
							}
							if (fnBooking.getBookingDate() != null) {
								info.setBookingDate(
										fnBooking.getBookingDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
							}
							hallShiftList.add(info);
						}
						fnDto.setBanquetHallShifts(hallShiftList);

						// Backward-compat: keep single fields from first booking
						BanquetHallShiftBookingEntity first = fnBookings.get(0);
						if (first.getHall() != null) {
							fnDto.setBanquetHallId(first.getHall().getId());
							fnDto.setBanquetHallName(first.getHall().getHallName());
						}
						if (first.getShift() != null) {
							fnDto.setShiftId(first.getShift().getId());
							fnDto.setShiftName(first.getShift().getShiftName());
							fnDto.setShiftStartTime(first.getShift().getStartTime());
							fnDto.setShiftEndTime(first.getShift().getEndTime());
						}
						if (first.getBookingDate() != null) {
							fnDto.setBookingDate(
									first.getBookingDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
						}
					}

					fnDtos.add(fnDto);
					if (fnEntity.getCustomPackage() != null) {
						fnDto.setCustomPackageId(fnEntity.getCustomPackage().getId());
						fnDto.setCustomPackageName(fnEntity.getCustomPackage().getNameEnglish());
					} else {
						fnDto.setCustomPackageId(null);
						fnDto.setCustomPackageName(null);
					}
				}
			}
		}

		responseDto.setEventFunctions(fnDtos);
		responseDto.setEventRooms(eventRoomMasterService.getByEventId(entity.getId()));

		return responseDto;
	}

	@Override
	public boolean deleteEventById(Long eventId) {
		Optional<EventMasterEntity> optEntity = eventMasterRepository.findByIdAndIsDeleteFalse(eventId);
		if (!optEntity.isPresent()) {
			return false;
		}

		System.err.println("innnnn");
		EventMasterEntity eventEntity = optEntity.get();

		eventEntity.setIsDelete(true);
		List<EventFunctionMasterEntity> eventFunctionMasterEntities = eventFunctionMasterRepository
				.findAllByEventIdAndIsDeleteFalseOrderBySortorderAsc(eventId);
		if (!eventFunctionMasterEntities.isEmpty()) {
			if (eventFunctionMasterEntities != null && !eventFunctionMasterEntities.isEmpty()) {
				for (EventFunctionMasterEntity fnEntity : eventFunctionMasterEntities) {
					fnEntity.setIsDelete(true);

					MenuPreparationEntity menuPreparationEntity = menuPreparationRepository
							.findByEventFunctionAndIsDeleteFalse(fnEntity);

					if (menuPreparationEntity != null) {
						menuPreparationDetailsRepository.deleteAllByMenuPreparation(menuPreparationEntity);

						menuPreparationRepository.delete(menuPreparationEntity);
					}

					eventFunctionMasterRepository.save(fnEntity);
				}
			}
		}
		EventFunctionQuotationEntity eventFuncQuotation = eventFunctionQuotationRepository
				.findByEventAndIsDeleteFalse(eventEntity);
		if (eventFuncQuotation != null) {
			eventFuncQuotation.setIsDelete(true);
			eventFunctionQuotationRepository.save(eventFuncQuotation);

		}

		EventInvoiceEntity eventInvoiceEntity = eventInvoiceRepository.findByEventAndIsDeleteFalse(eventEntity);
		if (eventInvoiceEntity != null) {
			eventInvoiceEntity.setIsDelete(true);
			eventInvoiceRepository.save(eventInvoiceEntity);

		}

		if (eventFunctionMasterEntities != null && !eventFunctionMasterEntities.isEmpty()) {
			for (EventFunctionMasterEntity fnEntity : eventFunctionMasterEntities) {
				fnEntity.setIsDelete(true);

				MenuPreparationEntity menuPreparationEntity = menuPreparationRepository
						.findByEventFunctionAndIsDeleteFalse(fnEntity);

				if (menuPreparationEntity != null) {
					menuPreparationDetailsRepository.deleteAllByMenuPreparation(menuPreparationEntity);

					menuPreparationRepository.delete(menuPreparationEntity);
				}

				eventFunctionMasterRepository.save(fnEntity);
			}
		}

		// ── Release banquet shift bookings when event is deleted ──────────────
		banquetShiftService.releaseBookingsByEvent(eventId);

		// ── Release banquet shift booking on event delete ─────────────────────
		List<BanquetHallShiftBookingEntity> bookingsToRelease = bookingRepository.findBookingsByEventId(eventId);
		if (!bookingsToRelease.isEmpty()) {
			bookingsToRelease.forEach(b -> b.setIsDelete(true));
			bookingRepository.saveAll(bookingsToRelease);
			System.out.println("Shift booking released for event: " + eventId);
		}

		eventMasterRepository.save(eventEntity);
		return true;
	}

	@Override
	public EventMasterResponseDto changeEventStatus(Integer status, Long eventId) {

		if (eventId == null) {
			throw new IllegalArgumentException("Event ID must not be null");
		}

		if (status == null) {
			throw new IllegalArgumentException("Status must not be null");
		}

		EventMasterEntity entity = eventMasterRepository.findByIdAndIsDeleteFalse(eventId)
				.orElseThrow(() -> new EntityNotFoundException("Event Master not found with id: " + eventId));

		boolean eventStatusChanged = false;

		if (!Objects.equals(status, entity.getStatus())) {
			entity.setStatus(status);
			eventMasterRepository.save(entity);
			eventStatusChanged = true;
		}

		EventMasterResponseDto responseDto = new EventMasterResponseDto();

		responseDto.setIsEventStatusChanged(eventStatusChanged);

		// User details
		if (entity.getUser() != null) {

			responseDto.setCompanyMobileNo(entity.getUser().getContactNo());
			responseDto.setUserId(entity.getUser().getId());
			if (entity.getUser().getUserBasicDetails() != null) {
				responseDto.setCompanyName(entity.getUser().getUserBasicDetails().getCompanyName());
			}
		}

		// Manager details
		if (entity.getManager() != null) {

			responseDto.setManagerMobileNo(entity.getManager().getContactNo());

			String firstName = entity.getManager().getFirstName();
			String lastName = entity.getManager().getLastName();

			String managerName = "";

			if (firstName != null && !firstName.trim().isEmpty()) {
				managerName = firstName.trim();
			}

			if (lastName != null && !lastName.trim().isEmpty()) {
				if (!managerName.isEmpty()) {
					managerName += " ";
				}
				managerName += lastName.trim();
			}

			responseDto.setManagerName(managerName);
		}

		// Party details
		if (entity.getParty() != null) {

			responseDto.setPartyName(entity.getParty().getNameEnglish());

			responseDto.setPartyMobileNo(entity.getParty().getMobileno());
		}

		return responseDto;

	}

	@Override
	public List<EventMasterResponseDto> getAllByPartyId(Long partyId, String partyName, Boolean isChildUser) {
		List<EventMasterEntity> entities = new ArrayList<>();
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a");

		// Fetch User
		Optional<PartyMasterEntity> partyOptional = partyMasterRepository.findByIdAndIsDeleteFalse(partyId);
		if (!partyOptional.isPresent()) {
			return Collections.emptyList();
		}
		PartyMasterEntity party = partyOptional.get();

		// Fetch Events
		if (partyName == null || partyName.trim().isEmpty()) {
			entities = eventMasterRepository.findAllByPartyAndIsDeleteFalse(party);
		} else {
			entities = eventMasterRepository
					.findByParty_NameEnglishContainingIgnoreCaseAndPartyAndIsDeleteFalse(partyName, party);
		}
		if (entities.isEmpty()) {
			return Collections.emptyList();
		}

		List<EventMasterResponseDto> responseDtos = new ArrayList<>();

		for (EventMasterEntity entity : entities) {
			EventMasterResponseDto responseDto = eventMasterMapper.entityToResponse(entity);

			// Format top-level Event dates
			if (entity.getInquiryDate() != null) {
				responseDto.setInquiryDate(entity.getInquiryDate().format(dateFormatter));
			}
			if (entity.getCreatedAt() != null) {
				responseDto.setCreatedAt(entity.getCreatedAt().format(dateFormatter));
			}
			if (entity.getBrideBirthDate() != null) {
				responseDto.setBrideBirthDate(entity.getBrideBirthDate().format(dateFormatter));
			}

			if (entity.getGroomBirthDate() != null) {
				responseDto.setGroomBirthDate(entity.getGroomBirthDate().format(dateFormatter));
			}
			if (entity.getEventStartDateTime() != null) {
				responseDto.setEventStartDateTime(entity.getEventStartDateTime().format(dateTimeFormatter));
			}
			if (entity.getEventEndDateTime() != null) {
				responseDto.setEventEndDateTime(entity.getEventEndDateTime().format(dateTimeFormatter));
			}

			responseDto.setUserId(entity.getUser().getId());
			responseDto.setManagerId(entity.getManager().getId() == null ? null : entity.getManager().getId());

			List<EventFunctionMasterResponseDto> functionDtos = new ArrayList<>();
			List<EventFunctionMasterEntity> eventFunctionMasterEntities = eventFunctionMasterRepository
					.findAllByEventIdAndIsDeleteFalseOrderBySortorderAsc(entity.getId());
			if (eventFunctionMasterEntities != null && !eventFunctionMasterEntities.isEmpty()) {
				for (EventFunctionMasterEntity fnEntity : eventFunctionMasterEntities) {
					if (Boolean.FALSE.equals(fnEntity.getIsDelete())) {
						EventFunctionMasterResponseDto fnDto = eventFunctionMasterMapper.entityToResponse(fnEntity);

						if (fnEntity.getCreatedAt() != null) {
							fnDto.setCreatedAt(fnEntity.getCreatedAt().format(dateFormatter));
						}

						fnDto.setEventId(entity.getId());
						functionDtos.add(fnDto);
					}
				}
			}
			responseDto.setEventFunctions(functionDtos);
			responseDto.setEventRooms(eventRoomMasterService.getByEventId(entity.getId()));
			responseDtos.add(responseDto);
		}

		return filterEventsByChildUser(responseDtos, isChildUser, party.getUser().getId());
	}

	@Override
	public Map<String, Object> getAllEventByFilter(Long userId, String partyName, String startDate, String endDate,
			String eventDate, Integer eventStatus, Boolean isChildUser) {

		Map<String, Object> result = new HashMap<>();

		Optional<UserMasterEntity> userOpt = userMasterRepository.findByIdAndIsDeleteFalse(userId);
		if (!userOpt.isPresent()) {
			return result;
		}
		UserMasterEntity user = userOpt.get();

		// ------------ Parse Inputs ------------
		LocalDateTime eventDateExact = null;
		if (eventDate != null && !eventDate.trim().isEmpty()) {
			LocalDate d = dateMapper.stringToDate(eventDate);
			eventDateExact = d.atStartOfDay();
		}

		LocalDateTime startDT = null;
		LocalDateTime endDT = null;
		if (startDate != null && !startDate.trim().isEmpty() && endDate != null && !endDate.trim().isEmpty()) {

			startDT = dateMapper.stringToDate(startDate).atStartOfDay();
			endDT = dateMapper.stringToDate(endDate).atStartOfDay();
		}

		// ------------ Today Events (Exact date) ------------
		List<EventMasterEntity> todayEntities = eventMasterRepository
				.findAll(EventMasterSpecification.filter(user, null, eventDateExact, null, null, eventStatus));

		List<EventMasterResponseDto> todayEvents = todayEntities.stream().map(this::setEventResponse)
				.collect(Collectors.toList());

		result.put("TodayEvents", todayEvents);

		// ------------ Upcoming Events (range + filters) ------------
		List<EventMasterEntity> upcomingEntities = eventMasterRepository
				.findAll(EventMasterSpecification.filter(user, partyName, null, startDT, endDT, eventStatus));

		List<EventMasterResponseDto> upcomingDtos = upcomingEntities.stream().map(this::setEventResponse)
				.collect(Collectors.toList());

		result.put("UpCommingEvents", filterEventsByChildUser(upcomingDtos, isChildUser, userId));

		return result;
	}

	private EventMasterResponseDto setEventResponse(EventMasterEntity entity) {

		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a");
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		EventMasterResponseDto responseDto = eventMasterMapper.entityToResponse(entity);

		// Format top-level Event dates
		if (entity.getInquiryDate() != null) {
			responseDto.setInquiryDate(dateMapper.dateToString(entity.getInquiryDate()));
		}
		if (entity.getBrideBirthDate() != null) {
			responseDto.setBrideBirthDate(entity.getBrideBirthDate().format(dateFormatter));
		}

		if (entity.getGroomBirthDate() != null) {
			responseDto.setGroomBirthDate(entity.getGroomBirthDate().format(dateFormatter));
		}
		if (entity.getCreatedAt() != null) {
			responseDto.setCreatedAt(dateMapper.dateTimeToString(entity.getCreatedAt()));
		}
		if (entity.getEventStartDateTime() != null) {
			responseDto.setEventStartDateTime(entity.getEventStartDateTime().format(dateTimeFormatter));
		}
		if (entity.getEventEndDateTime() != null) {
			responseDto.setEventEndDateTime(entity.getEventEndDateTime().format(dateTimeFormatter));
		}

		responseDto.setUserId(entity.getUser().getId());
		responseDto.setManagerId(entity.getManager().getId() == null ? null : entity.getManager().getId());

		List<EventFunctionMasterEntity> eventFunctionMasterEntities = eventFunctionMasterRepository
				.findAllByEventIdAndIsDeleteFalseOrderBySortorderAsc(entity.getId());

		List<EventFunctionMasterResponseDto> functionDtos = new ArrayList<>();
		if (eventFunctionMasterEntities != null && !eventFunctionMasterEntities.isEmpty()) {
			for (EventFunctionMasterEntity fnEntity : eventFunctionMasterEntities) {
				if (Boolean.FALSE.equals(fnEntity.getIsDelete())) {
					EventFunctionMasterResponseDto fnDto = eventFunctionMasterMapper.entityToResponse(fnEntity);

					if (fnEntity.getCreatedAt() != null) {
						fnDto.setCreatedAt(dateMapper.dateTimeToString(fnEntity.getCreatedAt()));
					}

					fnDto.setEventId(entity.getId());
					functionDtos.add(fnDto);
				}
			}
		}
		responseDto.setEventFunctions(functionDtos);
		responseDto.setEventRooms(eventRoomMasterService.getByEventId(entity.getId()));
		return responseDto;
	}

	@Override
	public EventFunctionMasterResponseDto updateEventFunction(EventFunctionMasterRequestDto request, Long id) {

		EventMasterEntity eventEntity = eventMasterRepository.findByIdAndIsDeleteFalse(id)
				.orElseThrow(() -> new RuntimeException("Event Master not found with id: " + id));

		List<EventFunctionMasterResponseDto> responseList = new ArrayList<>();

		if (request == null) {
			return null; // nothing to update
		}
		Boolean isUpdatePax = false;
		EventFunctionMasterRequestDto req = request;

		EventFunctionMasterEntity fnEntity;

		if (req.getEventFuncId() == 0) {
			fnEntity = eventFunctionMasterMapper.requestToEntity(req);
		} else {
			fnEntity = eventFunctionMasterRepository.findByIdAndIsDeleteFalse(req.getEventFuncId()).orElseThrow(
					() -> new RuntimeException("Event Function Master not found with id: " + req.getEventFuncId()));

			if (fnEntity.getPax() != req.getPax()) {
				fnEntity.setIsUpdate(true);
				isUpdatePax = true;
				if (isUpdatePax) {
					BigDecimal rate = req.getRate() != null ? BigDecimal.valueOf(req.getRate()) : BigDecimal.ZERO;
					eventFunctionQuotationItemRepository.updatePaxAndRate(fnEntity.getId(), isUpdatePax, false,
							req.getPax(), rate);
				}

			}
			fnEntity = eventFunctionMasterMapper.updateEntityFromRequest(req, fnEntity);
			fnEntity.setUpdatedAt(commonService.getCurrentDateTime());
		}

		fnEntity.setSortorder(req.getSortorder());
		fnEntity.setEvent(eventEntity);
		fnEntity.setFunction(functionMasterRepository.findByIdAndIsDeleteFalse(req.getFunctionId())
				.orElseThrow(() -> new RuntimeException("Function not found with id: " + req.getFunctionId())));

		fnEntity = eventFunctionMasterRepository.save(fnEntity);

		return eventFunctionMasterMapper.entityToResponse(fnEntity);
	}

	@Override
	@Transactional
	public List<EventFunctionMasterResponseDto> updateAllEventFunction(List<EventFunctionMasterRequestDto> requests,
			Long id) {
		EventMasterEntity eventEntity = eventMasterRepository.findByIdAndIsDeleteFalse(id)
				.orElseThrow(() -> new RuntimeException("Event Master not found with id: " + id));

		if (requests == null || requests.isEmpty()) {
			return Collections.emptyList();
		}

		DateTimeFormatter requestFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a", Locale.ENGLISH);

		DateTimeFormatter responseFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		if (requests != null && !requests.isEmpty()) {
			for (EventFunctionMasterRequestDto dto : requests) {
				EventFunctionMasterEntity functionEntity;
				Boolean isUpdatePax = false;
				Boolean isUpdateRate = false;
				Boolean isPackageUpdate = false;
				Boolean isFunctionDateUpdate = false;
				if (dto.getEventFuncId() == 0) {
					functionEntity = eventFunctionMasterMapper.requestToEntity(dto);
				} else {
					functionEntity = eventFunctionMasterRepository.findByIdAndIsDeleteFalse(dto.getEventFuncId())
							.orElseThrow(() -> new RuntimeException(
									"Event Function Master not found with id: " + dto.getEventFuncId()));
					if (!Objects.equals(functionEntity.getPax(), dto.getPax())) {
						menuAllocationItemRawMaterRepository
								.deleteRawMaterialsByEventAndEventFunction(eventEntity.getId(), functionEntity.getId());
						List<Long> eventrawmaterialid = eventRawMaterialFunctionsRepository
								.findDistinctEventRawMaterialIdsByEventIdAndEventFunctionId(eventEntity.getId(),
										functionEntity.getId());
						Set<Long> setEventRawMaterialId = new HashSet<>(eventrawmaterialid);
						eventRawMaterialFunctionsRepository.deleteByEventIdAndEventFunctionId(eventEntity.getId(),
								functionEntity.getId());
						eventrawmaterialhelperservice.updateQtyFromFunctions(setEventRawMaterialId);
						functionEntity.setIsUpdate(true);
						isUpdatePax = true;
					}

					LocalDateTime dtoStartDateTime = LocalDateTime.parse(dto.getFunctionStartDateTime(),
							requestFormatter);

					if (!Objects.equals(functionEntity.getFunctionStartDateTime(), dtoStartDateTime)) {
						isFunctionDateUpdate = true;
					}

					if (isFunctionDateUpdate) {
						eventFunctionQuotationItemRepository.updateFunctionDate(dtoStartDateTime,
								functionEntity.getId());
					}

					if (!Objects.equals(functionEntity.getRate(), dto.getRate())) {
						isUpdateRate = true;
					}
					Long existingPackageId = functionEntity.getCustomPackage() != null
							? functionEntity.getCustomPackage().getId()
							: 0;

					if (!Objects.equals(existingPackageId, dto.getCustomPackageId())) {
						isPackageUpdate = true;
					}
					if (isUpdatePax || isUpdateRate) {
						BigDecimal rate = dto.getRate() != null ? BigDecimal.valueOf(dto.getRate()) : BigDecimal.ZERO;
						eventFunctionQuotationItemRepository.updatePaxAndRate(functionEntity.getId(), isUpdatePax,
								isUpdateRate, dto.getPax(), rate);
					}

					if (isPackageUpdate) {
						if (dto.getCustomPackageId() != null && dto.getCustomPackageId() != 0) {
							CustomPackageEntity packageEntity = customPackageRepository
									.findByIdAndIsDeleteFalse(dto.getCustomPackageId());
							eventFunctionQuotationItemRepository.updatePackage(functionEntity.getId(),
									packageEntity.getId(), packageEntity.getNameEnglish(), packageEntity.getPrice());

							MenuPreparationEntity menuPreparationEntity = menuPreparationRepository
									.findByEventFunction_IdAndIsDeleteFalse(functionEntity.getId());
							if (menuPreparationEntity != null) {
								menuPreparationEntity.setPackageName(packageEntity.getNameEnglish());
								menuPreparationEntity.setPackagePrice(BigDecimal.valueOf(dto.getRate()));
								menuPreparationRepository.save(menuPreparationEntity);
								menuPreparationDetailsRepository.deleteAllByMenuPreparation(menuPreparationEntity);
							}

						} else {
							eventFunctionQuotationItemRepository.updatePackage(functionEntity.getId(), null, "",
									new BigDecimal(functionEntity.getRate()));
						}

					}

					functionEntity = eventFunctionMasterMapper.updateEntityFromRequest(dto, functionEntity);
					functionEntity.setUpdatedAt(commonService.getCurrentDateTime());
				}
				functionEntity.setSortorder(dto.getSortorder());
				functionEntity.setEvent(eventEntity);
				functionEntity.setFunction(functionMasterRepository.findByIdAndIsDeleteFalse(dto.getFunctionId())
						.orElseThrow(() -> new RuntimeException("Function not found with id: " + dto.getFunctionId())));
				// Set custom package function-wise
				if (dto.getCustomPackageId() != null && dto.getCustomPackageId() > 0) {
					functionEntity.setCustomPackage(
							customPackageRepository.findByIdAndIsDeleteFalse(dto.getCustomPackageId()));
				} else {
					functionEntity.setCustomPackage(null);
				}

				functionEntity = eventFunctionMasterRepository.save(functionEntity);

				// logic─────────────────────────────────
				if (dto.getBanquetHallShifts() != null && !dto.getBanquetHallShifts().isEmpty()) {
					try {
						if (dto.getEventFuncId() != 0) {
							banquetShiftService.releaseBookingsByEventFunction(functionEntity.getId());
						}
						for (BanquetHallShiftRequestDto hallShift : dto.getBanquetHallShifts()) {
							if (hallShift.getBanquetHallId() == null || hallShift.getBanquetHallId() <= 0
									|| hallShift.getShiftId() == null || hallShift.getShiftId() <= 0
									|| hallShift.getBookingDate() == null || hallShift.getBookingDate().isEmpty()) {
								continue;
							}
							BanquetShiftBookingRequestDto fnBooking = new BanquetShiftBookingRequestDto();
							fnBooking.setHallId(hallShift.getBanquetHallId());
							fnBooking.setShiftId(hallShift.getShiftId());
							fnBooking.setEventId(eventEntity.getId());
							fnBooking.setEventFunctionId(functionEntity.getId());
							fnBooking.setBookingDate(hallShift.getBookingDate());
							fnBooking.setUserId(eventEntity.getUser().getId());
							banquetShiftService.bookShiftForFunction(fnBooking);
						}
					} catch (RuntimeException e) {
						throw new RuntimeException("Function shift booking failed: " + e.getMessage());
					}
				}
				// end of functionwise hall save logic
			}
		}
		List<EventFunctionMasterEntity> eventFunctionMasterEntities = eventFunctionMasterRepository
				.findAllByEventIdAndIsDeleteFalseOrderBySortorderAsc(eventEntity.getId());
		List<EventFunctionMasterResponseDto> functionDtos = new ArrayList<>();
		if (eventFunctionMasterEntities != null && !eventFunctionMasterEntities.isEmpty()) {
			for (EventFunctionMasterEntity fnEntity : eventFunctionMasterEntities) {
				if (Boolean.FALSE.equals(fnEntity.getIsDelete())) {
					EventFunctionMasterResponseDto fnDto = eventFunctionMasterMapper.entityToResponse(fnEntity);

					if (fnEntity.getCreatedAt() != null) {
						fnDto.setCreatedAt(fnEntity.getCreatedAt().format(responseFormatter));
					}

					fnDto.setEventId(eventEntity.getId());

					// ── NEW ──────────────────────────────────────────────────────────────────
					List<BanquetHallShiftBookingEntity> fnBookings = bookingRepository
							.findFunctionLevelBookingsAll(fnEntity.getId());

					if (!fnBookings.isEmpty()) {
						List<BanquetHallShiftInfoDto> hallShiftList = new ArrayList<>();
						for (BanquetHallShiftBookingEntity fnBooking : fnBookings) {
							BanquetHallShiftInfoDto info = new BanquetHallShiftInfoDto();
							if (fnBooking.getHall() != null) {
								info.setBanquetHallId(fnBooking.getHall().getId());
								info.setBanquetHallName(fnBooking.getHall().getHallName());
							}
							if (fnBooking.getShift() != null) {
								info.setShiftId(fnBooking.getShift().getId());
								info.setShiftName(fnBooking.getShift().getShiftName());
								info.setShiftStartTime(fnBooking.getShift().getStartTime());
								info.setShiftEndTime(fnBooking.getShift().getEndTime());
							}
							if (fnBooking.getBookingDate() != null) {
								info.setBookingDate(fnBooking.getBookingDate().format(responseFormatter));
							}
							hallShiftList.add(info);
						}
						fnDto.setBanquetHallShifts(hallShiftList);

						// Backward-compat: keep single fields from first booking
						BanquetHallShiftBookingEntity first = fnBookings.get(0);
						if (first.getHall() != null) {
							fnDto.setBanquetHallId(first.getHall().getId());
							fnDto.setBanquetHallName(first.getHall().getHallName());
						}
						if (first.getShift() != null) {
							fnDto.setShiftId(first.getShift().getId());
							fnDto.setShiftName(first.getShift().getShiftName());
							fnDto.setShiftStartTime(first.getShift().getStartTime());
							fnDto.setShiftEndTime(first.getShift().getEndTime());
						}
						if (first.getBookingDate() != null) {
							fnDto.setBookingDate(first.getBookingDate().format(responseFormatter));
						}
					}

					if (fnEntity.getCustomPackage() != null) {
						fnDto.setCustomPackageId(fnEntity.getCustomPackage().getId());
						fnDto.setCustomPackageName(fnEntity.getCustomPackage().getNameEnglish());
					} else {
						fnDto.setCustomPackageId(null);
						fnDto.setCustomPackageName(null);
					}

					functionDtos.add(fnDto);

					// ─────────────────────────────────────────────────────────────────────
				}
			}
		}
		return functionDtos;
	}

	@Override
	public List<EventByDateResponseDto> getEventByDate(String startDate, String endDate, Long userId,
			Boolean isChildUser) {

		List<Object[]> responseDtos;

		responseDtos = eventMasterRepository.getEventByDate(startDate, endDate, userId);

		if (responseDtos.isEmpty() || responseDtos == null) {
			return null;
		}

		return responseDtos.stream()

				.filter(raw -> {
					Long childUserId = raw[8] == null ? 0L : ((Number) raw[8]).longValue();

					return Boolean.TRUE.equals(isChildUser) ? childUserId.equals(userId) : childUserId.equals(0L);
				})

				.map(raw -> new EventByDateResponseDto((String) raw[0], (String) raw[1], (String) raw[2],
						(Integer) raw[3], (String) raw[4], (String) raw[5], (String) raw[6], (String) raw[7]))

				.collect(Collectors.toList());
	}

	@Override
	public String generateDatewiseOrderSummaryPDF(HttpServletRequest re, List<Map<String, Object>> datas,
			String startDate, String endDate) throws Exception {

		String rootPath = re.getSession().getServletContext().getRealPath("/");
		File outputPath = new File(rootPath + "resources/tempDownload/eventReports/");

		if (!outputPath.exists()) {
			if (outputPath.mkdirs()) {
				System.out.println("Directory Created!!!");
			} else {
				System.out.println("Error!!!");
			}
		}

		String sanitizedStartDate = startDate.replace("/", "-");
		String sanitizedEndDate = endDate.replace("/", "-");

		File pdfFile = new File(
				outputPath + "/EventReports_from_" + sanitizedStartDate + "_to_" + sanitizedEndDate + ".pdf");

		PdfWriter writer = new PdfWriter(pdfFile);
		PdfDocument pdfDoc = new PdfDocument(writer);

		// Set page size to A4 landscape for better table fit
		pdfDoc.setDefaultPageSize(PageSize.A4.rotate());

		Document document = new Document(pdfDoc);

		// Set fonts
		PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
		PdfFont normalFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);

		// Add title
		Paragraph title = new Paragraph("DATEWISE ORDER SUMMARY").setFont(boldFont).setFontSize(18)
				.setTextAlignment(TextAlignment.CENTER).setMarginBottom(10);
		document.add(title);

		// Add date range
		Table dateTable = new Table(2);
		dateTable.setWidth(UnitValue.createPercentValue(100));

		Cell fromDateCell = new Cell()
				.add(new Paragraph("From Date : " + startDate).setFont(normalFont).setFontSize(12)).setBorder(null)
				.setTextAlignment(TextAlignment.LEFT);

		Cell toDateCell = new Cell().add(new Paragraph("To Date : " + endDate).setFont(normalFont).setFontSize(12))
				.setBorder(null).setTextAlignment(TextAlignment.RIGHT);

		dateTable.addCell(fromDateCell);
		dateTable.addCell(toDateCell);
		document.add(dateTable);

		document.add(new Paragraph("\n").setMarginBottom(5));

		// Create table with 9 columns
		float[] columnWidths = { 30f, 60f, 80f, 130f, 50f, 90f, 80f, 80f, 90f };
		Table table = new Table(UnitValue.createPointArray(columnWidths));
		table.setWidth(UnitValue.createPercentValue(100));

		// Add table headers
		String[] headers = { "Sr", "Fun Date", "Event Name", "Session Date / Time", "Pax.", "Venue Name", "Guest Name",
				"Mgr Name", "Menu Releasing Status" };

		for (String header : headers) {
			Cell headerCell = new Cell().add(new Paragraph(header).setFont(boldFont).setFontSize(11))
					.setBackgroundColor(ColorConstants.LIGHT_GRAY).setTextAlignment(TextAlignment.CENTER).setPadding(5);
			table.addHeaderCell(headerCell);
		}

		// Add data rows
		int srNo = 1;
		for (Map<String, Object> data : datas) {
			// Sr No
			table.addCell(new Cell().add(new Paragraph(String.valueOf(srNo++)).setFont(normalFont).setFontSize(10))
					.setTextAlignment(TextAlignment.CENTER).setPadding(4));

			// Fun Date
			table.addCell(new Cell()
					.add(new Paragraph(String.valueOf(data.get("function_date"))).setFont(normalFont).setFontSize(10))
					.setTextAlignment(TextAlignment.CENTER).setPadding(4));

			// Event Name
			table.addCell(new Cell()
					.add(new Paragraph(String.valueOf(data.get("event_name"))).setFont(normalFont).setFontSize(10))
					.setPadding(4));

			// Session Date/Time
			table.addCell(new Cell().add(
					new Paragraph(String.valueOf(data.get("session_date_time"))).setFont(normalFont).setFontSize(10))
					.setPadding(4));

			// Pax
			table.addCell(
					new Cell().add(new Paragraph(String.valueOf(data.get("pax"))).setFont(normalFont).setFontSize(10))
							.setTextAlignment(TextAlignment.CENTER).setPadding(4));

			// Venue Name
			table.addCell(new Cell()
					.add(new Paragraph(String.valueOf(data.get("venue_name"))).setFont(normalFont).setFontSize(10))
					.setPadding(4));

			// Guest Name
			table.addCell(new Cell()
					.add(new Paragraph(String.valueOf(data.get("guest_name"))).setFont(normalFont).setFontSize(10))
					.setPadding(4));

			// Mgr Name
			table.addCell(new Cell()
					.add(new Paragraph(String.valueOf(data.get("mgr_name"))).setFont(normalFont).setFontSize(10))
					.setPadding(4));

			// Menu Releasing Status
			table.addCell(new Cell().add(new Paragraph(String.valueOf(data.get("menu_releasing_status")))
					.setFont(normalFont).setFontSize(10)).setTextAlignment(TextAlignment.CENTER).setPadding(4));
		}

		document.add(table);
		document.close();

		// Build return URL
		String scheme = re.getScheme();
		String serverName = re.getServerName();
		int serverPort = re.getServerPort();
		String contextPath = re.getContextPath();

		String fullUrl;
		if ((scheme.equals("http") && serverPort == 80) || (scheme.equals("https") && serverPort == 443)) {
			fullUrl = scheme + "://" + serverName + contextPath + "/api/download/pdf" + "/EventReports_from_"
					+ sanitizedStartDate + "_to_" + sanitizedEndDate + ".pdf";
		} else {
			fullUrl = scheme + "://" + serverName + ":" + serverPort + contextPath + "/api/download/pdf"
					+ "/EventReports_from_" + sanitizedStartDate + "_to_" + sanitizedEndDate + ".pdf";
		}

		return fullUrl;

	}

	@Override
	public List<Long> findEventsByEventDate(LocalDateTime startDate, LocalDateTime endDate) {

		List<Long> eventIds = eventMasterRepository.findByEventStartDateTimeBetween(startDate, endDate);

		return eventIds;
	}

	@Override
	public EventOverViewResponseDto getEventOverview(Long partyId, Long eventId, Long userId) {

		EventMasterEntity eventEntity = eventMasterRepository.findByIdAndIsDeleteFalse(eventId)
				.orElseThrow(() -> new RuntimeException("Event not found after save!"));

		List<EventRawMaterialEntity> eventRawMaterialEntities = eventRawMaterialRepository
				.findAllByEventAndIsDeleteFalse(eventEntity);
		return null;
	}

	@Transactional
	@Override
	public void syncMenuAllocationAndRawMaterial(Long eventId) {
		EventMasterEntity eventEntity = eventMasterRepository.findByIdAndIsDeleteFalse(eventId)
				.orElseThrow(() -> new EntityNotFoundException("Event not found with id: " + eventId));

		// Delete menu allocation data
		menuAllocationItemRawMaterRepository.deleteAllByEvent(eventEntity);
		menuAllocationOrderRepository.deleteAllByEvent(eventEntity.getId());
		allocationRepository.deleteAllByEvent(eventEntity.getId());

		// Delete raw material data
		eventRawMaterialFunctionsRepository.deleteAllByEvent(eventEntity);
		eventRawMaterialRepository.deleteAllByEvent(eventEntity);
	}

	@Transactional
	@Override
	public Boolean addEventFunctionManagerAssign(EventFunctionManagerAssignRequestDto request) {

		if (request == null) {
			throw new IllegalArgumentException("Request must not be null");
		}

		if (request.getEventFunctionManagers() == null || request.getEventFunctionManagers().isEmpty()) {
			throw new IllegalArgumentException("Event function managers are required");
		}

		EventMasterEntity event = eventMasterRepository.findByIdAndIsDeleteFalse(request.getEventId())
				.orElseThrow(new Supplier<RuntimeException>() {
					@Override
					public RuntimeException get() {
						return new RuntimeException("Event not found: " + request.getEventId());
					}
				});

		UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(request.getUserId())
				.orElseThrow(new Supplier<RuntimeException>() {
					@Override
					public RuntimeException get() {
						return new RuntimeException("User not found: " + request.getUserId());
					}
				});

		eventFunctionManagerAssignRepository.deleteAllByEventIdAndUserId(event.getId(), user.getId());

		List<EventFunctionManagerAssignEntity> entities = new ArrayList<EventFunctionManagerAssignEntity>();

		for (EventFunctionManagerAssignListRequestDto dto : request.getEventFunctionManagers()) {

			EventFunctionMasterEntity function = eventFunctionMasterRepository
					.findByIdAndIsDeleteFalse(dto.getEventFunctionId()).orElseThrow(new Supplier<RuntimeException>() {
						@Override
						public RuntimeException get() {
							return new RuntimeException("Function not found: " + dto.getEventFunctionId());
						}
					});

			Set<Long> managerIds = new HashSet<Long>(dto.getManagerId());

			for (Long managerId : managerIds) {
				if (managerId == null) {
					continue;
				}

				EventFunctionManagerAssignEntity entity = new EventFunctionManagerAssignEntity();
				entity.setEventFunctionId(function.getId());
				entity.setEventId(event.getId());
				entity.setManagerId(managerId);
				entity.setUserId(user.getId());
				entity.setCreatedAt(commonService.getCurrentDateTime());

				entities.add(entity);
			}
		}

		if (!entities.isEmpty()) {
			eventFunctionManagerAssignRepository.saveAll(entities);
		}

		return true;
	}

	@Override
	public List<ManagerEventsResponseDto> getAllManagerEvents(Long managerId) {

		List<Object[]> rows = eventMasterRepository.getAllManagerEvents(managerId);

		Map<Long, ManagerEventsResponseDto> eventMap = new LinkedHashMap<>();

		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

		for (Object[] row : rows) {

			EventMasterEntity event = (EventMasterEntity) row[0];
			EventFunctionMasterEntity eventFunction = (EventFunctionMasterEntity) row[1];
			EventTypeMasterEntity eventType = (EventTypeMasterEntity) row[2];
			FunctionMasterEntity function = (FunctionMasterEntity) row[3];
			PartyMasterEntity party = (PartyMasterEntity) row[4];
			VenueMasterEntity venue = (VenueMasterEntity) row[5];

			List<BanquetHallShiftInfoDto> banquetList = banquetHallShiftBookingRepository
					.findBanquetByEventFunctionId(event.getId(), eventFunction.getId());

			String venueEnglish;
			String venueHindi;
			String venueGujarati;

			if (banquetList != null && !banquetList.isEmpty()) {

				String banquetNames = banquetList.stream().map(BanquetHallShiftInfoDto::getBanquetHallName)
						.collect(Collectors.joining(", "));

				venueEnglish = banquetNames;
				venueHindi = banquetNames;
				venueGujarati = banquetNames;

			} else {

				venueEnglish = venue != null ? venue.getNameEnglish() : "";
				venueHindi = venue != null ? venue.getNameHindi() : "";
				venueGujarati = venue != null ? venue.getNameGujarati() : "";
			}

			final String finalVenueEnglish = venueEnglish;
			final String finalVenueHindi = venueHindi;
			final String finalVenueGujarati = venueGujarati;

			ManagerEventsResponseDto eventDto = eventMap.computeIfAbsent(event.getId(),
					id -> new ManagerEventsResponseDto(event.getId(), event.getEventNo(), eventType.getNameEnglish(),
							eventType.getNameHindi(), eventType.getNameGujarati(), party.getNameEnglish(),
							party.getNameHindi(), party.getNameGujarati(), event.getMobileno(),
							event.getEventStartDateTime() != null ? event.getEventStartDateTime().format(dateFormatter)
									: null,
							finalVenueEnglish, finalVenueHindi, finalVenueGujarati, getStatus(event.getStatus()),
							new ArrayList<>()));

			String functionDate = eventFunction.getFunctionStartDateTime() != null
					? eventFunction.getFunctionStartDateTime().format(dateFormatter)
					: null;

			String startTime = eventFunction.getFunctionStartDateTime() != null
					? eventFunction.getFunctionStartDateTime().format(timeFormatter)
					: null;

			String endTime = eventFunction.getFunctionEndDateTime() != null
					? eventFunction.getFunctionEndDateTime().format(timeFormatter)
					: null;

			boolean exists = eventDto.getEventFunctions().stream()
					.anyMatch(f -> f.getEventFunctionId().equals(eventFunction.getId()));

			if (!exists) {
				eventDto.getEventFunctions()
						.add(new ManagerEventFunctionsResponseDto(eventFunction.getId(), function.getNameEnglish(),
								function.getNameHindi(), function.getNameGujarati(), functionDate, startTime, endTime,
								eventFunction.getPax(), eventFunction.getFunction_venue()));
			}
		}

		return new ArrayList<>(eventMap.values());
	}

	private String getStatus(Integer status) {

		if (status == null) {
			return "UNKNOWN";
		}

		switch (status) {
		case 0:
			return "INQUERY";
		case 1:
			return "CONFIRM";
		case 2:
			return "CANCEL";
		case 3:
			return "TENTATIVE";
		default:
			return "UNKNOWN";
		}
	}

	@Override
	@Transactional
	public List<EventMenuAllocationOverviewResponseDto> getEventVendorData(Long eventId, Long eventFunctionId,
			String type) {

		EventMasterEntity eventEntity = eventMasterRepository.findByIdAndIsDeleteFalse(eventId)
				.orElseThrow(() -> new EntityNotFoundException("Event Master not found with id: " + eventId));

		EventFunctionMasterEntity functionEntity = eventFunctionMasterRepository
				.findByIdAndIsDeleteFalse(eventFunctionId).orElseThrow(() -> new EntityNotFoundException(
						"Event Function Master not found with id: " + eventFunctionId));

		AllocationType allocationType = AllocationType.valueOf(type.toUpperCase());

		List<EventFunctionStaffAssignmentEntity> assignments = eventFunctionStaffAssignmentRepository
				.findByEventIdAndEventFunctionIdAndResourceType(eventId, eventFunctionId, allocationType);

		if (assignments == null || assignments.isEmpty()) {

			assignments = syncMissingOldDataToNewTable(eventEntity, functionEntity, allocationType, assignments);
		}

		if (assignments == null || assignments.isEmpty()) {
			return new ArrayList<>();
		}

		List<Long> assignmentIds = assignments.stream().map(EventFunctionStaffAssignmentEntity::getId)
				.filter(Objects::nonNull).collect(Collectors.toList());

		List<Long> vendorIds = assignments.stream().map(EventFunctionStaffAssignmentEntity::getVendorId)
				.filter(Objects::nonNull).distinct().collect(Collectors.toList());

		List<Long> unitIds = assignments.stream().map(EventFunctionStaffAssignmentEntity::getUnitId)
				.filter(Objects::nonNull).distinct().collect(Collectors.toList());

		Map<Long, PartyMasterEntity> partyMap = partyMasterRepository.findAllById(vendorIds).stream()
				.collect(Collectors.toMap(PartyMasterEntity::getId, Function.identity()));

		Map<Long, UnitMasterEntity> unitMap = unitMasterRepository.findAllById(unitIds).stream()
				.collect(Collectors.toMap(UnitMasterEntity::getId, Function.identity()));

		Map<Long, List<EventFunctionStaffImagesResponseDto>> imageMap = eventFunctionStaffUploadRepository
				.findByAssignmentIdIn(assignmentIds).stream()
				.collect(Collectors.groupingBy(EventFunctionStaffUploadEntity::getAssignmentId,
						Collectors.mapping(
								img -> new EventFunctionStaffImagesResponseDto(img.getId(), img.getAssignmentId(),
										environment.getProperty("app.image.url") + img.getFilePath()),
								Collectors.toList())));

		List<EventFunctionStaffTaskEntity> allTasks = eventFunctionStaffTaskRepository
				.findByAssignmentIdInAndIsDeleteFalse(assignmentIds);

		List<Long> taskIds = allTasks.stream().map(EventFunctionStaffTaskEntity::getTaskId).filter(Objects::nonNull)
				.distinct().collect(Collectors.toList());

		Map<Long, EventGroundTaskMasterEntity> taskMasterMap = eventGroundTaskMasterRepository.findAllById(taskIds)
				.stream().collect(Collectors.toMap(EventGroundTaskMasterEntity::getId, Function.identity()));

		Map<Long, List<EventFunctionStaffTaskResponseDto>> taskMap = allTasks.stream().collect(
				Collectors.groupingBy(EventFunctionStaffTaskEntity::getAssignmentId, Collectors.mapping(task -> {

					String taskName = "";
					String taskNameHindi = "";
					String taskNameGujarati = "";

					if (task.getTaskId() != null && taskMasterMap.containsKey(task.getTaskId())) {

						EventGroundTaskMasterEntity master = taskMasterMap.get(task.getTaskId());

						taskName = master.getNameEnglish();

						taskNameHindi = master.getNameHindi();

						taskNameGujarati = master.getNameGujarati();

					} else {

						taskName = task.getTaskName();

						taskNameHindi = task.getTaskNameHindi();

						taskNameGujarati = task.getTaskNameGujarati();
					}

					return new EventFunctionStaffTaskResponseDto(task.getTaskId(), taskName, taskNameHindi,
							taskNameGujarati, task.getIsCompleted(), task.getIsCommonTask(), task.getRemarks());

				}, Collectors.toList())));

		List<Long> menuItemIds = assignments.stream().map(EventFunctionStaffAssignmentEntity::getMenuItemId)
				.filter(Objects::nonNull).distinct().collect(Collectors.toList());

		Map<Long, List<EventFunctionStaffAssignmentEntity>> vendorAssignmentMap = assignments.stream()
				.filter(assignment -> assignment.getVendorId() != null).collect(Collectors.groupingBy(
						EventFunctionStaffAssignmentEntity::getVendorId, LinkedHashMap::new, Collectors.toList()));

		List<EventMenuAllocationOverviewResponseDto> responseList = vendorAssignmentMap.entrySet().stream()
				.map(entry -> {

					Long vendorId = entry.getKey();

					List<EventFunctionStaffAssignmentEntity> vendorAssignments = entry.getValue();

					EventFunctionStaffAssignmentEntity firstAssignment = vendorAssignments.get(0);

					EventMenuAllocationOverviewResponseDto response = new EventMenuAllocationOverviewResponseDto();

					response.setVendorId(vendorId);

					if (partyMap.containsKey(vendorId)) {

						PartyMasterEntity party = partyMap.get(vendorId);

						response.setVendorNameEnglish(party.getNameEnglish());

						response.setVendorNameHindi(party.getNameHindi());

						response.setVendorNameGujarati(party.getNameGujarati());
					}

					response.setEventId(eventId);

					response.setEventFunctionId(eventFunctionId);

					response.setEventNameEnglish(eventEntity.getEventType().getNameEnglish());

					response.setEventNameHindi(eventEntity.getEventType().getNameHindi());

					response.setEventNameGujarati(eventEntity.getEventType().getNameGujarati());

					response.setFunctionNameEnglish(functionEntity.getFunction().getNameEnglish());

					response.setFunctionNameHindi(functionEntity.getFunction().getNameHindi());

					response.setFunctionNameGujarati(functionEntity.getFunction().getNameGujarati());

					response.setFunctionPax(functionEntity.getPax());

					response.setVenue(functionEntity.getFunction_venue());

					response.setReportingTime(firstAssignment.getReportingTime());

					response.setArrivalTime(firstAssignment.getArrivalTime());

					response.setRemarks(firstAssignment.getRemarks());

					response.setStatus(firstAssignment.getStatus());

					response.setIsPresent(firstAssignment.getIsPresent());

					response.setLatitude(firstAssignment.getLatitude());

					response.setLongitude(firstAssignment.getLongitude());

					Map<Long, MenuItemMasterEntity> menuItemMap = menuItemRepository.findAllById(menuItemIds).stream()
							.collect(Collectors.toMap(MenuItemMasterEntity::getId, Function.identity()));

					List<EventMenuAllocationItemOverviewResponseDto> items = vendorAssignments.stream()
							.map(assignment -> buildMenuAllocationItem(assignment, unitMap, menuItemMap))
							.collect(Collectors.toList());

					response.setItems(items);

					List<EventFunctionStaffImagesResponseDto> vendorImages = vendorAssignments.stream()
							.map(EventFunctionStaffAssignmentEntity::getId).filter(Objects::nonNull)
							.flatMap(assignmentId -> imageMap.getOrDefault(assignmentId, new ArrayList<>()).stream())
							.collect(Collectors.toList());

					response.setVendorImages(vendorImages);

					List<EventFunctionStaffTaskResponseDto> vendorTasks = vendorAssignments.stream()
							.map(EventFunctionStaffAssignmentEntity::getId).filter(Objects::nonNull)
							.flatMap(assignmentId -> taskMap.getOrDefault(assignmentId, new ArrayList<>()).stream())
							.collect(Collectors.toList());

					response.setVendorTasks(vendorTasks);

					return response;

				}).collect(Collectors.toList());

		return responseList;
	}

	private EventMenuAllocationItemOverviewResponseDto buildMenuAllocationItem(
			EventFunctionStaffAssignmentEntity assignment, Map<Long, UnitMasterEntity> unitMap,
			Map<Long, MenuItemMasterEntity> menuItemMap) {

		EventMenuAllocationItemOverviewResponseDto item = new EventMenuAllocationItemOverviewResponseDto();

		item.setId(assignment.getId());

		item.setMenuItemId(assignment.getMenuItemId());

		item.setInstructions(assignment.getInstructions());

		item.setInstructionsHindi(assignment.getInstructionsHindi());

		item.setInstructionsGujarati(assignment.getInstructionsGujarati());

		item.setResourceType(assignment.getResourceType());

		UnitMasterEntity unit = null;

		if (assignment.getUnitId() != null) {

			unit = unitMap.get(assignment.getUnitId());
		}

		if (assignment.getResourceType() == AllocationType.CHEF) {

			EventMenuAllocationChefLabourOverviewResponseDto chefDto = new EventMenuAllocationChefLabourOverviewResponseDto();

			chefDto.setStaffCategory(assignment.getStaffCategory());

			chefDto.setLabour(assignment.getLabourQty());

			chefDto.setHelpers(assignment.getHelpersQty());

			chefDto.setWeight(assignment.getWeight());

			chefDto.setUnitId(assignment.getUnitId());

			if (unit != null) {

				chefDto.setUnitNameEnglish(unit.getNameEnglish());

				chefDto.setUnitNameHindi(unit.getNameHindi());

				chefDto.setUnitNameGujarati(unit.getNameGujarati());
			}

			item.setChefLabour(chefDto);
		}

		if (assignment.getResourceType() == AllocationType.OUTSIDE) {

			EventMenuAllocationOutSideOverviewResponseDto outsideDto = new EventMenuAllocationOutSideOverviewResponseDto(

					assignment.getWeight() != null ? assignment.getWeight() : BigDecimal.ZERO,

					assignment.getUnitId(),

					unit != null ? unit.getNameEnglish() : null,

					unit != null ? unit.getNameHindi() : null,

					unit != null ? unit.getNameGujarati() : null);

			item.setOutside(outsideDto);
		}

		if (assignment.getResourceType() == AllocationType.LABOUR) {

			EventLabourOverviewResponse labourDto = new EventLabourOverviewResponse();

			labourDto.setStaffCategory(assignment.getStaffCategory());

			labourDto.setShift(assignment.getShiftName());

			labourDto.setAssignedQty(assignment.getAssignedQty());

			labourDto.setConfirmedQty(assignment.getConfirmedQty());

			item.setLabour(labourDto);
		}

		return item;
	}

	private List<EventFunctionStaffAssignmentEntity> syncMissingOldDataToNewTable(EventMasterEntity event,
			EventFunctionMasterEntity eventFunction, AllocationType allocationType,
			List<EventFunctionStaffAssignmentEntity> existingAssignments) {

		List<EventFunctionStaffAssignmentEntity> newAssignments = new ArrayList<>();

		Set<String> existingKeys = existingAssignments.stream().map(a -> buildUniqueKey(a.getVendorId(),
				a.getResourceType(), a.getStaffCategory(), a.getShiftName(), a.getWeight(), a.getAssignedQty()))
				.collect(Collectors.toSet());

		if (allocationType == AllocationType.CHEF || allocationType == AllocationType.OUTSIDE) {

			List<MenuAllocationOrdersEntity> orderEntities = menuAllocationOrderRepository
					.findByEventAndFunctionAndType(event.getId(), eventFunction.getId(), allocationType.name());

			for (MenuAllocationOrdersEntity order : orderEntities) {

				String key = buildUniqueKey(order.getParty() != null ? order.getParty().getId() : null, allocationType,
						order.getServiceType(), null, order.getQuantity(), null);

				if (existingKeys.contains(key)) {
					continue;
				}

				EventFunctionStaffAssignmentEntity assignment = new EventFunctionStaffAssignmentEntity();

				assignment.setEventId(event.getId());
				assignment.setEventFunctionId(eventFunction.getId());

				if (order.getParty() != null) {
					assignment.setVendorId(order.getParty().getId());
				}

				assignment.setResourceType(allocationType);
				assignment.setIsPresent(false);
				assignment.setReportingTime(order.getReportingTime());
				assignment.setRemarks(order.getRemarks());
				assignment.setLatitude("");
				assignment.setLongitude("");
				assignment.setStatus("PENDING");

				if (allocationType == AllocationType.CHEF) {

					if ("counter_wise".equalsIgnoreCase(order.getServiceType())) {

						assignment.setLabourQty(order.getCounterQuantity());
						assignment.setHelpersQty(order.getHelperQuantity());

					} else {

						assignment.setWeight(order.getQuantity());

						if (order.getUnit() != null) {
							assignment.setUnitId(order.getUnit().getId());
						}
					}

					assignment.setStaffCategory(order.getServiceType());
				}

				if (allocationType == AllocationType.OUTSIDE) {

					assignment.setWeight(order.getQuantity());

					if (order.getUnit() != null) {
						assignment.setUnitId(order.getUnit().getId());
					}
				}

				assignment = eventFunctionStaffAssignmentRepository.save(assignment);

				saveCommonTasks(assignment.getId(), allocationType);

				newAssignments.add(assignment);
			}
		}

		if (allocationType == AllocationType.LABOUR) {

			List<EventLaborEntity> laborEntities = eventLaborRepository.findByEventIdAndFunctionId(event.getId(),
					eventFunction.getId());

			for (EventLaborEntity labor : laborEntities) {

				String key = buildUniqueKey(labor.getContact() != null ? labor.getContact().getId() : null,
						AllocationType.LABOUR,
						labor.getContactCategory() != null ? labor.getContactCategory().getNameEnglish() : null,
						labor.getLaborshift(), null, labor.getQty() != null ? labor.getQty().intValue() : null);

				if (existingKeys.contains(key)) {
					continue;
				}

				EventFunctionStaffAssignmentEntity assignment = new EventFunctionStaffAssignmentEntity();

				assignment.setEventId(event.getId());
				assignment.setEventFunctionId(eventFunction.getId());

				if (labor.getContact() != null) {
					assignment.setVendorId(labor.getContact().getId());
				}

				assignment.setResourceType(AllocationType.LABOUR);

				assignment.setShiftName(labor.getLaborshift());

				if (labor.getQty() != null) {
					assignment.setAssignedQty(labor.getQty().intValue());
				}

				if (labor.getContactCategory() != null) {
					assignment.setStaffCategory(labor.getContactCategory().getNameEnglish());
				}
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
				String formattedTime = labor.getLabordatetime().format(formatter);
				assignment.setReportingTime(formattedTime);
				assignment.setRemarks(labor.getNotesEnglish());

				assignment.setStatus("PENDING");
				assignment.setUserId(event.getUser().getId());
				assignment = eventFunctionStaffAssignmentRepository.save(assignment);

				saveCommonTasks(assignment.getId(), AllocationType.LABOUR);

				newAssignments.add(assignment);
			}
		}

		existingAssignments.addAll(newAssignments);

		return existingAssignments;
	}

	private String buildUniqueKey(Long vendorId, AllocationType type, String category, String shift, Object weight,
			Object qty) {

		return String.valueOf(vendorId) + "_" + String.valueOf(type) + "_" + String.valueOf(category) + "_"
				+ String.valueOf(shift) + "_" + String.valueOf(weight) + "_" + String.valueOf(qty);
	}

	private void saveCommonTasks(Long assignmentId, AllocationType allocationType) {

		List<EventGroundTaskMasterEntity> commonTasks = eventGroundTaskMasterRepository
				.findByResourceTypeAndIsDeleteFalse(allocationType);

		if (commonTasks == null || commonTasks.isEmpty()) {
			return;
		}

		Set<Long> existingTaskIds = eventFunctionStaffTaskRepository.findByAssignmentIdAndIsDeleteFalse(assignmentId)
				.stream().map(EventFunctionStaffTaskEntity::getTaskId).filter(Objects::nonNull)
				.collect(Collectors.toSet());

		List<EventFunctionStaffTaskEntity> taskEntities = commonTasks.stream()

				.filter(task -> !existingTaskIds.contains(task.getId()))

				.map(task -> {

					EventFunctionStaffTaskEntity entity = new EventFunctionStaffTaskEntity();

					entity.setAssignmentId(assignmentId);

					entity.setTaskId(task.getId());

					entity.setTaskName(task.getNameEnglish());

					entity.setTaskNameGujarati(task.getNameGujarati());

					entity.setTaskNameHindi(task.getNameHindi());

					entity.setRemarks("");

					entity.setIsCommonTask(true);

					entity.setIsCompleted(false);
					return entity;

				}).collect(Collectors.toList());

		if (!taskEntities.isEmpty()) {

			eventFunctionStaffTaskRepository.saveAll(taskEntities);
		}
	}

	@Override
	@Transactional
	public Boolean addUpdateEventVendorData(EventVendorAssignmentWrapperDto requests) {

		if (requests == null || requests.getAssignments().isEmpty()) {
			throw new RuntimeException("Event Function Vendor Assign cannot be empty");
		}

		for (EventFunctionVendorAssignmentRequestDto req : requests.getAssignments()) {

			EventFunctionStaffAssignmentEntity entity = getOrCreateEntity(req);

			mapAssignmentData(req, entity);

			EventFunctionStaffAssignmentEntity savedEntity = eventFunctionStaffAssignmentRepository.save(entity);

			saveVendorImages(req, savedEntity);

			saveVendorTasks(req, savedEntity);
		}

		return true;
	}

	private EventFunctionStaffAssignmentEntity getOrCreateEntity(EventFunctionVendorAssignmentRequestDto req) {

		if (req.getId() == null || req.getId() == -1) {

			EventFunctionStaffAssignmentEntity entity = new EventFunctionStaffAssignmentEntity();

			entity.setCreatedBy(req.getManagerId());

			return entity;
		}

		EventFunctionStaffAssignmentEntity entity = eventFunctionStaffAssignmentRepository.findById(req.getId())
				.orElseThrow(() -> new RuntimeException("Event Function Vendor Assigned Data Not Found"));

		entity.setUpdatedAt(commonService.getCurrentDateTime());

		return entity;
	}

	private void mapAssignmentData(EventFunctionVendorAssignmentRequestDto req,
			EventFunctionStaffAssignmentEntity entity) {

		if (req.getLabour() != null) {

			entity.setStaffCategory(req.getLabour().getStaffCategory());

			entity.setAssignedQty(req.getLabour().getAssignedQty());

			entity.setConfirmedQty(req.getLabour().getConfirmedQty());

			entity.setShiftName(req.getLabour().getShift());
		}

		if (req.getChefLabour() != null) {

			entity.setStaffCategory(req.getChefLabour().getStaffCategory());

			entity.setLabourQty(req.getChefLabour().getLabour());

			entity.setHelpersQty(req.getChefLabour().getHelpers());

			entity.setWeight(req.getChefLabour().getWeight());

			entity.setUnitId(req.getChefLabour().getUnitId());
		}

		if (req.getOutside() != null) {

			entity.setWeight(req.getOutside().getWeight());

			entity.setUnitId(req.getOutside().getUnitId());
		}

		entity.setArrivalTime(req.getArrivalTime());

		entity.setEventFunctionId(req.getEventFunctionId());

		entity.setEventId(req.getEventId());

		entity.setIsPresent(req.getIsPresent());

		entity.setRemarks(req.getRemarks());

		entity.setReportingTime(req.getReportingTime());

		entity.setResourceType(AllocationType.fromString(req.getResourceType()));

		entity.setStatus(req.getStatus());

		entity.setUserId(req.getUserId());

		entity.setVendorId(req.getVendorId());

		entity.setLatitude(req.getLatitude());

		entity.setLongitude(req.getLongitude());

		entity.setMenuItemId(req.getMenuItemId());

		entity.setInstructions(req.getInstructions());

		entity.setInstructionsHindi(req.getInstructionsHindi());

		entity.setInstructionsGujarati(req.getInstructionsGujarati());
	}

	private void saveVendorImages(EventFunctionVendorAssignmentRequestDto req,
			EventFunctionStaffAssignmentEntity savedEntity) {

		List<EventFunctionStaffImagesRequestDto> images = req.getVendorImages();

		if (images == null || images.isEmpty()) {
			return;
		}

		for (EventFunctionStaffImagesRequestDto fileDto : images) {

			EventFunctionStaffUploadEntity uploadEntity;
			if (fileDto.getUploadId() == -1 || fileDto.getUploadId() == 0) {
				uploadEntity = new EventFunctionStaffUploadEntity();
			} else {
				uploadEntity = eventFunctionStaffUploadRepository.findById(fileDto.getUploadId())
						.orElseThrow(() -> new RuntimeException("Function Staff Image Not Found"));
			}

			uploadEntity.setAssignmentId(savedEntity.getId());
			uploadEntity.setCreatedAt(commonService.getCurrentDateTime());

			uploadEntity = eventFunctionStaffUploadRepository.save(uploadEntity);

			if (fileDto.getFile() != null && !fileDto.getFile().isEmpty()) {

				try {

					userFileService.storeFile(req.getUserId(), ModuleName.GROUDIMAGE.toString(), uploadEntity.getId(),
							FileType.IMAGE.toString(), fileDto.getFile());

				} catch (IOException e) {

					throw new RuntimeException("Failed to store image", e);
				}
			}
		}
	}

	private void saveVendorTasks(EventFunctionVendorAssignmentRequestDto req,
			EventFunctionStaffAssignmentEntity savedEntity) {

		List<EventFunctionStaffTaskRequestDto> tasks = req.getVendorTasks();

		if (tasks == null || tasks.isEmpty()) {
			return;
		}

		eventFunctionStaffTaskRepository.deleteAllByAssignmentId(savedEntity.getId());

		List<EventFunctionStaffTaskEntity> entities = tasks.stream().map(task -> {

			EventFunctionStaffTaskEntity entity = new EventFunctionStaffTaskEntity();

			entity.setAssignmentId(savedEntity.getId());

			entity.setCompletedAt(dateMapper.stringToDateTime(task.getCompletedDate()));

			entity.setIsCommonTask(task.getIsCommonTask());

			entity.setIsCompleted(task.getIsCompleted());
			entity.setTaskName(task.getTaskName());
			entity.setTaskNameGujarati(task.getTaskNameGujarati());
			entity.setTaskNameHindi(task.getTaskNameHindi());
			entity.setRemarks(task.getRemarks());
			entity.setTaskId(task.getTaskId());
			return entity;

		}).collect(Collectors.toList());

		eventFunctionStaffTaskRepository.saveAll(entities);
	}

	@Override
	public EventFunctionWithManagersResponseDto getAllAssignFunctionByEvent(Long eventId) {

		List<Object[]> datas = eventMasterRepository.getAllAssignFunctionByEvent(eventId);

		if (datas == null || datas.isEmpty()) {
			return null;
		}

		Object[] first = datas.get(0);

		EventFunctionWithManagersResponseDto response = new EventFunctionWithManagersResponseDto();

		response.setEventId(first[0] != null ? ((Number) first[0]).longValue() : null);

		response.setEventNameEnglish((String) first[1]);
		response.setEventNameHindi((String) first[2]);
		response.setEventNameGujarati((String) first[3]);

		response.setPartyNameEnglish((String) first[4]);
		response.setPartyNameHindi((String) first[5]);
		response.setPartyNameGujarati((String) first[6]);

		response.setEventStartDate(first[7] != null ? first[7].toString() : null);

		response.setEventEndDate(first[8] != null ? first[8].toString() : null);

		response.setVenueEnglish((String) first[9]);
		response.setVenueHindi((String) first[10]);
		response.setVenueGujarati((String) first[11]);

		response.setStatus(first[12] != null ? first[12].toString() : null);

		Map<Long, EventFunctionManagerListResponseDto> functionMap = new LinkedHashMap<>();

		for (Object[] row : datas) {

			Long eventFunctionId = row[13] != null ? ((Number) row[13]).longValue() : null;

			if (eventFunctionId == null) {
				continue;
			}

			EventFunctionManagerListResponseDto functionDto = functionMap.get(eventFunctionId);

			if (functionDto == null) {

				functionDto = new EventFunctionManagerListResponseDto();

				functionDto.setEventFunctionId(eventFunctionId);
				functionDto.setFunctionNameEnglish((String) row[14]);
				functionDto.setFunctionNameHindi((String) row[15]);
				functionDto.setFunctionNameGujarati((String) row[16]);

				functionDto.setVenue((String) row[17]);

				functionDto.setFunctionStartDate(row[18] != null ? row[18].toString() : null);

				functionDto.setFunctionEndDate(row[19] != null ? row[19].toString() : null);

				functionDto.setPax(row[20] != null ? ((Number) row[20]).intValue() : null);

				functionDto.setManagers(new ArrayList<>());

				functionMap.put(eventFunctionId, functionDto);
			}

			Long managerId = row[21] != null ? ((Number) row[21]).longValue() : null;

			String managerName = (String) row[22];

			if (managerId != null) {

				EventFunctionManagersResponseDto managerDto = new EventFunctionManagersResponseDto();

				managerDto.setManagerId(managerId);
				managerDto.setManagerName(managerName);

				functionDto.getManagers().add(managerDto);
			}
		}

		response.setEventFunctions(new ArrayList<>(functionMap.values()));

		return response;
	}

	@Override
	public List<EventMasterResponseDto> getEventsByHallAndPassword(String hallName, String password) {

		// Find Hall
		BanquetHallMasterEntity hall = banquetHallMasterRepository.findByHallNameAndIsDeleteFalse(hallName)
				.orElseThrow(() -> new RuntimeException("Hall not found : " + hallName));

		// Verify Password
		if (hall.getPassword() == null || hall.getPassword().isEmpty()) {
			throw new RuntimeException("Password not set for this hall");
		}
		boolean matched = passwordEncoder.matches(password, hall.getPassword());

		if (!matched) {
			throw new RuntimeException("Invalid password");
		}

		// Get Events
		List<EventMasterEntity> events = eventMasterRepository.findByBanquetHallIdAndIsDeleteFalse(hall.getId());

		// Convert Response
		return events.stream().map(eventMasterMapper::entityToResponse).collect(Collectors.toList());
	}

	@Override
	public List<BanquetShiftAvailabilityResponseDto> getShiftsForEventForm(Long hallId, String bookingDate, Long userId,
			Long eventId) {

		// If no hall selected (ODC case), return empty list
		if (hallId == null || hallId == 0) {
			return new ArrayList<>();
		}

		return banquetShiftService.getAvailability(hallId, bookingDate, userId, eventId);
	}

	@Override
	public Boolean updateRemarks(EventRemarksRequestDto request) {
		EventMasterEntity updatedEntity = eventMasterRepository.findByIdAndIsDeleteFalse(request.getEventId())
				.orElseThrow(() -> new RuntimeException("Event not found after save!"));
		updatedEntity.setRemark(request.getNameEnglish());
		updatedEntity.setRemarksGujarati(request.getNameGujarati());
		updatedEntity.setRemarksHindi(request.getNameHindi());
		eventMasterRepository.save(updatedEntity);
		return true;

	}

	@Override
	public Boolean deleteEventVendorTaskImage(Long id) {

		if (eventFunctionStaffUploadRepository.existsById(id)) {
			eventFunctionStaffUploadRepository.deleteById(id);
			return true;
		}
		return false;
	}
}
