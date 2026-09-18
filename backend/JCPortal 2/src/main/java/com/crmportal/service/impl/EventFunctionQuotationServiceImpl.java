package com.crmportal.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.Valid;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.crmportal.controller.EventDishCostingController;
import com.crmportal.entity.BankDetailsEntity;
import com.crmportal.entity.CashAccountEntity;
import com.crmportal.entity.DecorePreparationDetailsEntity;
import com.crmportal.entity.DecorePreparationEntity;
import com.crmportal.entity.EventFunctionMasterEntity;
import com.crmportal.entity.EventFunctionQuotationEntity;
import com.crmportal.entity.EventFunctionQuotationItemEntity;
import com.crmportal.entity.EventFunctionQuotationPaymentEntity;
import com.crmportal.entity.EventInvoiceEntity;
import com.crmportal.entity.EventMasterEntity;
import com.crmportal.entity.EventQuotationSecurityDepositEntity;
import com.crmportal.entity.EventRemarkMasterEntity;
import com.crmportal.entity.ExtraChargesHeadingEntity;
import com.crmportal.entity.ExtraChargesRowEntity;
import com.crmportal.entity.MenuPreparationDetailsEntity;
import com.crmportal.entity.MenuPreparationEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.entity.VendorPaymentEntity;
import com.crmportal.mapper.EventFunctionMasterMapper;
import com.crmportal.mapper.EventMasterMapper;
import com.crmportal.repository.BankDetailsRepository;
import com.crmportal.repository.BanquetHallShiftBookingRepository;
import com.crmportal.repository.CashAccountRepository;
import com.crmportal.repository.DecorePreparationDetailsRepository;
import com.crmportal.repository.DecorePreparationRepository;
import com.crmportal.repository.EventFunctionMasterRepository;
import com.crmportal.repository.EventFunctionQuotationItemRepository;
import com.crmportal.repository.EventFunctionQuotationPaymentRepository;
import com.crmportal.repository.EventFunctionQuotationRepository;
import com.crmportal.repository.EventInvoiceFunctionItemRepository;
import com.crmportal.repository.EventInvoicePaymentRepository;
import com.crmportal.repository.EventInvoiceRepository;
import com.crmportal.repository.EventMasterRepository;
import com.crmportal.repository.EventQuotationSecurityDepositRepository;
import com.crmportal.repository.EventRemarkMasterRepository;
import com.crmportal.repository.ExtraChargesHeadingRepository;
import com.crmportal.repository.ExtraChargesRowRepository;
import com.crmportal.repository.MenuPreparationDetailsRepository;
import com.crmportal.repository.MenuPreparationRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.repository.UserUpgradedModuleRepository;
import com.crmportal.repository.VendorPaymentRepository;
import com.crmportal.request.dto.EventFunctionQuotationItemsRequestDto;
import com.crmportal.request.dto.EventFunctionQuotationPaymentRequestDto;
import com.crmportal.request.dto.EventFunctionQuotationPaymentResponseDto;
import com.crmportal.request.dto.EventFunctionQuotationRequestDto;
import com.crmportal.response.dto.BanquetHallShiftInfoDto;
import com.crmportal.response.dto.DefaultExtraQuotationFunctionResponseDto;
import com.crmportal.response.dto.EventFunctionQuotationItemsResponseDto;
import com.crmportal.response.dto.EventFunctionQuotationResponseDto;
import com.crmportal.response.dto.EventMasterResponseDto;
import com.crmportal.response.dto.EventQuotationSecurityDepositResponseDto;
import com.crmportal.response.dto.QuotationResponseDto;
import com.crmportal.service.CommonService;
import com.crmportal.service.DefaultExtraQuotationFunctionService;
import com.crmportal.service.EventFunctionQuotationService;

@Service
public class EventFunctionQuotationServiceImpl implements EventFunctionQuotationService {

	private final EventDishCostingController eventDishCostingController;

	@Autowired
	EventMasterRepository eventMasterRepository;

	@Autowired
	EventFunctionQuotationRepository eventFunctionQuotationRepository;

	@Autowired
	EventFunctionQuotationItemRepository eventFunctionQuotationItemRepository;

	@Autowired
	CommonService commonService;

	@Autowired
	EventMasterMapper eventMasterMapper;

	@Autowired
	UserMasterRepository userMasterRepository;

	@Autowired
	EventFunctionMasterRepository eventFunctionMasterRepository;

	@Autowired
	EventFunctionQuotationPaymentRepository eventFunctionQuotationPaymentRepository;

	@Autowired
	EventFunctionMasterMapper eventFunctionMasterMapper;

	@Autowired
	EventInvoiceRepository eventInvoiceRepository;

	@Autowired
	EventInvoiceServiceImpl eventInvoiceServiceImpl;

	@Autowired
	EventInvoicePaymentRepository eventInvoicePaymentRepository;

	@Autowired
	EventInvoiceFunctionItemRepository eventInvoiceFunctionItemRepository;

	@Autowired
	private EventFunctionQuotationSyncHelper quotationSyncHelper;

	@Autowired
	DefaultExtraQuotationFunctionService defaultExtraQuotationFunctionService;

	@Autowired
	MenuPreparationRepository menuPreparationRepository;

	@Autowired
	MenuPreparationDetailsRepository menuPreparationDetailsRepository;

	@Autowired
	EventRemarkMasterRepository eventRemarkMasterRepository;

	@Autowired
	VendorPaymentRepository vendorPaymentRepository;

	@Autowired
	Environment environment;

	@Autowired
	ExtraChargesHeadingRepository extraChargesHeadingRepository;

	@Autowired
	ExtraChargesRowRepository extraChargesRowRepository;

	@Autowired
	UserUpgradedModuleRepository userUpgradedModuleRepository;

	@Autowired
	BankDetailsRepository bankDetailsRepository;

	@Autowired
	CashAccountRepository cashAccountRepository;

	@Autowired
	DecorePreparationRepository decorePreparationRepository;

	@Autowired
	DecorePreparationDetailsRepository decorePreparationDetailsRepository;

	@Autowired
	EventQuotationSecurityDepositRepository eventQuotationSecurityDepositRepository;

	@Autowired
	EventQuotationSecurityDepositServiceImpl eventQuotationSecurityDepositServiceImpl;

	@Autowired
	BanquetHallShiftBookingRepository banquetHallShiftBookingRepository;

	private static final DateTimeFormatter FUNCTION_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a",
			Locale.ENGLISH);

	private static final DateTimeFormatter CREATE_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a",
			Locale.ENGLISH);

	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	EventFunctionQuotationServiceImpl(EventDishCostingController eventDishCostingController) {
		this.eventDishCostingController = eventDishCostingController;
	}

	@Override
	@Transactional
	public EventFunctionQuotationResponseDto addOrUpdateEventFunctoinQuotation(
			@Valid EventFunctionQuotationRequestDto request, long id) {

		EventMasterEntity eventMaster = eventMasterRepository.findByIdAndIsDeleteFalse(request.getEventId())
				.orElseThrow(() -> new RuntimeException(
						"Event Master with this eventId not found: " + request.getEventId()));

		UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(request.getUserId())
				.orElseThrow(() -> new RuntimeException("User not found with id: " + request.getUserId()));

		EventFunctionQuotationEntity quotationEntity;
		if (id == -1) {
			quotationEntity = new EventFunctionQuotationEntity();
			String quotationNo = generateQuotationNo(request.getUserId());
			quotationEntity.setQuotationCode(quotationNo);
		} else {
			quotationEntity = eventFunctionQuotationRepository.findByIdAndIsDeleteFalse(id);
			if (quotationEntity == null) {
				throw new RuntimeException("EventFunctionQuotation with id " + id + " not found");
			}
			quotationEntity.setUpdatedAt(commonService.getCurrentDateTime());
		}

		quotationEntity.setEvent(eventMaster);
		quotationEntity.setUser(user);
		quotationEntity.setTotalAmount(request.getTotalAmount());
		quotationEntity.setCashPayment(request.getCashPayment() != null ? request.getCashPayment() : BigDecimal.ZERO);
		quotationEntity
				.setChequePayment(request.getChequePayment() != null ? request.getChequePayment() : BigDecimal.ZERO);
		quotationEntity.setCgst(request.getCgst());
		quotationEntity.setCgstAmnt(request.getCgstAmnt());
		quotationEntity.setSgst(request.getSgst());
		quotationEntity.setSgstAmnt(request.getSgstAmnt());
		quotationEntity.setIgst(request.getIgst());
		quotationEntity.setIgstAmnt(request.getIgstAmnt());
		quotationEntity.setDiscount(request.getDiscount());
		quotationEntity.setRoundOff(request.getRoundOff());
		quotationEntity.setSubTotal(request.getSubTotal());
		quotationEntity.setIsExtraFunction(request.getIsExtraFunction());
		quotationEntity.setGrandTotal(request.getGrandTotal());
		quotationEntity.setRemainingAmount(request.getRemainingAmount());
		quotationEntity.setBillingname(request.getBillingname());
		quotationEntity.setGstnumber(request.getGstnumber());
		quotationEntity.setIsLocked(request.getIsLocked());
		quotationEntity.setIsDecore(request.getIsDecore());
		quotationEntity.setFoodTax(request.getFoodTax());
		quotationEntity.setFoodTaxAmount(request.getFoodTaxAmount());
		quotationEntity.setFoodTaxTotalAmount(request.getFoodTaxTotalAmount());
		quotationEntity.setServiceTax(request.getServiceTax());
		quotationEntity.setServiceTaxAmount(request.getServiceTaxAmount());
		quotationEntity.setServiceTaxTotalAmount(request.getServiceTaxTotalAmount());
		quotationEntity.setVatTax(request.getVatTax());
		quotationEntity.setVatTaxAmount(request.getVatTaxAmount());
		quotationEntity.setVatTaxTotalAmount(request.getVatTaxTotalAmount());
		quotationEntity.setDiscountPct(request.getDiscountPct());
		quotationEntity.setIsDiscountPercent(request.getIsDiscountPercent());
		if (!request.getDuedate().equals("")) {
			try {
				quotationEntity.setDuedate(LocalDate.parse(request.getDuedate(), formatter));
			} catch (Exception e) {
				quotationEntity.setDuedate(null);
			}
		}

		if (!request.getQuotationdate().equals("")) {
			try {
				quotationEntity.setQuotationdate(LocalDate.parse(request.getQuotationdate(), formatter));
			} catch (Exception e) {
				quotationEntity.setQuotationdate(null);
			}
		}

		quotationEntity.setNotes(request.getNotes());
		quotationEntity.setPoojaRooms(request.getPoojaRooms());
		quotationEntity.setIronService(request.getIronService());
		quotationEntity.setVenueRemark(request.getVenueRemark());
		quotationEntity.setVenueTotal(request.getVenueTotal() != null ? request.getVenueTotal() : BigInteger.ZERO);
		quotationEntity.setTransportation(request.getTransportation());
		quotationEntity = eventFunctionQuotationRepository.save(quotationEntity);

		for (EventFunctionQuotationPaymentRequestDto dto : request.getEventFunctionQuotationPayments()) {

			BankDetailsEntity bank = getBank(dto.getBankId());
			CashAccountEntity cash = getCashAccount(dto.getCashAccountId());

			EventFunctionQuotationPaymentEntity payment = getOrCreatePayment(dto);

			syncVendorPayment(payment, dto, eventMaster, user, bank, cash);

			mapQuotationPayment(payment, dto, quotationEntity, eventMaster, user, bank, cash);

			eventFunctionQuotationPaymentRepository.save(payment);

			updateAccountBalance(bank, cash, BigDecimal.valueOf(dto.getAdvancePayment().doubleValue()),
					BigDecimal.ZERO);
		}

		List<EventFunctionQuotationItemsRequestDto> items = request.getFunctionQuotationItems();
		if (items != null) {
			Map<String, EventFunctionQuotationItemEntity> addonKeysSeenThisRequest = new HashMap<>();

			for (EventFunctionQuotationItemsRequestDto itemDto : items) {
				EventFunctionQuotationItemEntity itemEntity;
				boolean isAddon = Boolean.TRUE.equals(itemDto.getIsAddons());
				String addonKey = isAddon
						? buildAddonDedupKey(itemDto.getEventFunctionId(), itemDto.getMenuCatId(), itemDto.getItemId())
						: null;
				if (itemDto.getId() == null || itemDto.getId() == 0) {
					if (isAddon && addonKeysSeenThisRequest.containsKey(addonKey)) {
						itemEntity = addonKeysSeenThisRequest.get(addonKey);
					} else if (isAddon) {
						Optional<EventFunctionQuotationItemEntity> existing = eventFunctionQuotationItemRepository
								.findFirstByEventFunctionQuotationAndEventFunctionIdAndItemIdAndMenuCatIdAndIsAddonsTrueAndIsDeleteFalse(
										quotationEntity, itemDto.getEventFunctionId(), itemDto.getItemId(),
										itemDto.getMenuCatId());
						itemEntity = existing.orElseGet(EventFunctionQuotationItemEntity::new);
					} else {
						itemEntity = new EventFunctionQuotationItemEntity();
					}
				} else {
					itemEntity = eventFunctionQuotationItemRepository.findByIdAndIsDeleteFalse(itemDto.getId());
					if (itemEntity == null) {
						throw new RuntimeException("Quotation item with id " + itemDto.getId() + " not found");
					}
					itemEntity.setUpdatedAt(commonService.getCurrentDateTime());
				}

				itemEntity.setFunctionName(itemDto.getFunctionName());
				itemEntity.setPax(itemDto.getPax());
				itemEntity.setEventFunctionId(itemDto.getEventFunctionId());
				itemEntity.setExtraPax(itemDto.getExtraPax());
				itemEntity.setIsEventFunction(itemDto.getIsEventFunction());
				itemEntity.setRatePerPlate(
						itemDto.getRatePerPlate() != null ? itemDto.getRatePerPlate() : BigDecimal.ZERO);
				itemEntity
						.setOfferedRate(itemDto.getOfferedRate() != null ? itemDto.getOfferedRate() : BigDecimal.ZERO);
				itemEntity.setAmount(itemDto.getAmount() != null ? itemDto.getAmount() : BigDecimal.ZERO);
				itemEntity.setExtraTax(itemDto.getExtraTax() != null ? itemDto.getExtraTax() : BigDecimal.ZERO);

				itemEntity.setTaxRate(itemDto.getTaxRate() != null ? itemDto.getTaxRate() : BigDecimal.ZERO);
				if (itemDto.getFunctionDate() != null && !itemDto.getFunctionDate().trim().isEmpty()) {
					itemEntity.setFunctionDate(
							LocalDateTime.parse(itemDto.getFunctionDate().trim(), FUNCTION_DATE_FORMATTER));
				} else {
					itemEntity.setFunctionDate(null);
				}
				itemEntity.setEventFunctionQuotation(quotationEntity);
				itemEntity.setDefaultFunctionId(itemDto.getDefaultFunctionId());
				itemEntity.setIsAddons(itemDto.getIsAddons());
				itemEntity.setMenuCatId(itemDto.getMenuCatId());
				itemEntity.setItemId(itemDto.getItemId());
				itemEntity.setOptions(itemDto.getOptions());
				itemEntity.setOptions(itemDto.getOptions());
				itemEntity.setIsLocked(itemDto.getIsLocked());
				itemEntity.setExtraChargesId(itemDto.getExtraChargesId());
				itemEntity.setIsExtraCharges(itemDto.getIsExtraCharges());

				// ── Custom Package
				// ────────────────────────────────────────────────────
				itemEntity.setCustomPackageId(itemDto.getCustomPackageId());
				itemEntity.setCustomPackageName(itemDto.getCustomPackageName());
				itemEntity.setCustomPackagePrice(itemDto.getCustomPackagePrice());
				// ─────────────────────────────────────────────────────────────────────

				eventFunctionQuotationItemRepository.save(itemEntity);
			}
		}

		EventMasterResponseDto responseDto = eventMasterMapper.entityToResponse(eventMaster);

		EventFunctionQuotationResponseDto response = new EventFunctionQuotationResponseDto();
		response.setTotalAmount(quotationEntity.getTotalAmount());
		response.setCashPayment(quotationEntity.getCashPayment());
		response.setChequePayment(quotationEntity.getChequePayment());
		response.setEvent(responseDto);
		response.setCgst(quotationEntity.getCgst());
		response.setCgstAmnt(quotationEntity.getCgstAmnt());
		response.setSgst(quotationEntity.getSgst());
		response.setSgstAmnt(quotationEntity.getSgstAmnt());
		response.setIgst(quotationEntity.getIgst());
		response.setIgstAmnt(quotationEntity.getIgstAmnt());
		response.setDiscount(quotationEntity.getDiscount());
		response.setRoundOff(quotationEntity.getRoundOff());
		response.setGrandTotal(quotationEntity.getGrandTotal());
		response.setRemainingAmount(quotationEntity.getRemainingAmount());
		response.setNotes(quotationEntity.getNotes());
		response.setSubTotal(quotationEntity.getSubTotal());
		response.setQuotationCode(quotationEntity.getQuotationCode());
		response.setBillingname(quotationEntity.getBillingname());
		response.setGstnumber(quotationEntity.getGstnumber());
		response.setIsExtraFunction(quotationEntity.getIsExtraFunction());
		response.setIsLocked(quotationEntity.getIsLocked());
		response.setIsDecore(quotationEntity.getIsDecore());
		response.setTransportation(quotationEntity.getTransportation());
		response.setFoodTax(quotationEntity.getFoodTax());
		response.setFoodTaxAmount(quotationEntity.getFoodTaxAmount());
		response.setFoodTaxTotalAmount(quotationEntity.getFoodTaxTotalAmount());
		response.setServiceTax(quotationEntity.getServiceTax());
		response.setServiceTaxAmount(quotationEntity.getServiceTaxAmount());
		response.setServiceTaxTotalAmount(quotationEntity.getServiceTaxTotalAmount());
		response.setVatTax(quotationEntity.getVatTax());
		response.setVatTaxAmount(quotationEntity.getVatTaxAmount());
		response.setVatTaxTotalAmount(quotationEntity.getVatTaxTotalAmount());
		response.setDiscountPct(quotationEntity.getDiscountPct());
		response.setIsDiscountPercent(quotationEntity.getIsDiscountPercent());
		try {
			response.setDuedate(
					quotationEntity.getDuedate() != null ? quotationEntity.getDuedate().format(formatter) : "");
		} catch (Exception e) {
			response.setDuedate("");
		}
		response.setCreatedAt(formatDateTime(quotationEntity.getCreatedAt(), FUNCTION_DATE_FORMATTER).split(" ")[0]);
		return response;
	}

	private EventFunctionQuotationPaymentEntity getOrCreatePayment(EventFunctionQuotationPaymentRequestDto dto) {

		if (dto.getId() == null || dto.getId() == 0) {
			return new EventFunctionQuotationPaymentEntity();
		}

		EventFunctionQuotationPaymentEntity entity = eventFunctionQuotationPaymentRepository
				.findByIdAndIsDeleteFalse(dto.getId());

		if (entity == null) {
			throw new RuntimeException("Quotation Payment not found");
		}

		entity.setUpdatedAt(commonService.getCurrentDateTime());

		return entity;
	}

	private BankDetailsEntity getBank(Long bankId) {
		if (bankId == null) {
			return null;
		}

		return bankDetailsRepository.findByIdAndIsDeleteFalse(bankId)
				.orElseThrow(() -> new RuntimeException("Bank Details not found with id : " + bankId));
	}

	private CashAccountEntity getCashAccount(Long cashAccountId) {
		if (cashAccountId == null) {
			return null;
		}

		return cashAccountRepository.findByIdAndIsDeleteFalse(cashAccountId)
				.orElseThrow(() -> new RuntimeException("Cash Account not found with id : " + cashAccountId));
	}

	private void syncVendorPayment(EventFunctionQuotationPaymentEntity payment,
			EventFunctionQuotationPaymentRequestDto dto, EventMasterEntity eventMaster, UserMasterEntity user,
			BankDetailsEntity bank, CashAccountEntity cash) {

		if (!isVendorModuleEnabled(user))
			return;

		if (dto.getAdvancePayment() == null || dto.getAdvancePayment().compareTo(BigInteger.ZERO) <= 0)
			return;

		VendorPaymentEntity vendor;

		if (payment.getVendorCode() == null) {

			vendor = new VendorPaymentEntity();
			vendor.setInvoiceCode(commonService.generateVendorInvoiceCode(user.getId()));

			payment.setVendorCode(vendor.getInvoiceCode());

		} else {

			vendor = vendorPaymentRepository.findByInvoiceCodeAndVendorIdAndIsDeleteFalse(payment.getVendorCode(),
					eventMaster.getParty().getId());

			if (vendor == null) {
				vendor = new VendorPaymentEntity();
				vendor.setInvoiceCode(payment.getVendorCode());
			}
		}

		mapVendorPayment(vendor, dto, eventMaster, user, bank, cash);

		vendorPaymentRepository.save(vendor);
	}

	private boolean isVendorModuleEnabled(UserMasterEntity user) {
		return userUpgradedModuleRepository
				.existsByUserIdAndUpgradeModuleIdAndIsActiveTrueAndIsDeleteFalse(user.getId(), 1L);
	}

	private void mapVendorPayment(VendorPaymentEntity vendor, EventFunctionQuotationPaymentRequestDto dto,
			EventMasterEntity eventMaster, UserMasterEntity user, BankDetailsEntity bank, CashAccountEntity cash) {

		vendor.setEventId(eventMaster.getId());
		vendor.setUser(user);
		vendor.setVendorId(eventMaster.getParty().getId());
		vendor.setVendorCat("User");

		vendor.setIsOpb(false);
		vendor.setIsPayable(false);

		vendor.setReceivedAmount(BigDecimal.valueOf(dto.getAdvancePayment().doubleValue()));

		vendor.setPayAmount(BigDecimal.ZERO);
		vendor.setSettlementAmount(BigDecimal.ZERO);

		vendor.setReferenceId("");

		vendor.setRemarks(dto.getAdvancePaymentNotes());

		vendor.setBankId(bank == null ? null : bank.getId());

		vendor.setCashAccountId(cash == null ? null : cash.getId());

		vendor.setPaymentMode(dto.getPaymentMode());

		if (dto.getAdvancePaymentDate() != null) {
			vendor.setPaymentDate(
					LocalDateTime.parse(dto.getAdvancePaymentDate(), FUNCTION_DATE_FORMATTER).toLocalDate());
		}
	}

	private void mapQuotationPayment(EventFunctionQuotationPaymentEntity payment,
			EventFunctionQuotationPaymentRequestDto dto, EventFunctionQuotationEntity quotation,
			EventMasterEntity event, UserMasterEntity user, BankDetailsEntity bank, CashAccountEntity cash) {

		payment.setAdvancePayment(dto.getAdvancePayment());
		payment.setAdvancePaymentNotes(dto.getAdvancePaymentNotes());

		payment.setPaymentMode(dto.getPaymentMode());

		payment.setEvent(event);

		payment.setEventFunctionQuotation(quotation);

		payment.setUser(user);

		payment.setBankId(bank == null ? null : bank.getId());

		payment.setCashAccountId(cash == null ? null : cash.getId());

		if (dto.getAdvancePaymentDate() != null) {
			payment.setAdvancePaymentDate(LocalDateTime.parse(dto.getAdvancePaymentDate(), FUNCTION_DATE_FORMATTER));
		}
	}

	private String buildAddonDedupKey(Long eventFunctionId, Long menuCatId, Long itemId) {
		return "ADDON_" + eventFunctionId + "_" + (menuCatId != null ? menuCatId : "null") + "_"
				+ (itemId != null ? itemId : "null");
	}

	public void updateAccountBalance(BankDetailsEntity bankDetailsEntity, CashAccountEntity cashAccountEntity,
			BigDecimal receivedAmount, BigDecimal payAmount) {

		BigDecimal received = receivedAmount != null ? receivedAmount : BigDecimal.ZERO;

		BigDecimal paid = payAmount != null ? payAmount : BigDecimal.ZERO;

		if (bankDetailsEntity != null) {

			bankDetailsEntity.setCurrentBalance(bankDetailsEntity.getCurrentBalance().add(received).subtract(paid));

			bankDetailsRepository.save(bankDetailsEntity);

		} else if (cashAccountEntity != null) {

			cashAccountEntity.setCurrentBalance(cashAccountEntity.getCurrentBalance().add(received).subtract(paid));

			cashAccountRepository.save(cashAccountEntity);
		}
	}

	private String generateQuotationNo(Long userId) {
		String prefix = "QT-";
		String lastQuotationNo = eventFunctionQuotationRepository.findLastQuotationNo(userId);

		int nextNumber = 1;

		if (lastQuotationNo != null && lastQuotationNo.startsWith(prefix)) {
			try {
				// Extract numeric part → "0000001" → 1
				String numberPart = lastQuotationNo.substring(prefix.length());
				nextNumber = Integer.parseInt(numberPart) + 1;
			} catch (NumberFormatException e) {
				nextNumber = 1; // fallback in case of invalid format
			}
		}

		// Format: QT-0000001
		return String.format("%s%07d", prefix, nextNumber);
	}

	@Override
	@Transactional
	public Boolean deleteByQuotationItemId(Long quotationItemId) {
		EventFunctionQuotationItemEntity eventFunctionQuotationItemEntity = eventFunctionQuotationItemRepository
				.findByIdAndIsDeleteFalse(quotationItemId);
		if (eventFunctionQuotationItemEntity != null) {
			eventFunctionQuotationItemEntity.setIsDelete(true);
			EventFunctionQuotationItemEntity savedItem = eventFunctionQuotationItemRepository
					.save(eventFunctionQuotationItemEntity);

			List<EventFunctionQuotationItemEntity> defaultFunctions = eventFunctionQuotationItemRepository
					.getAllDefaultFunctionsByQuotation(
							eventFunctionQuotationItemEntity.getEventFunctionQuotation().getId());

			if (defaultFunctions.isEmpty()) {
				EventFunctionQuotationEntity quotation = savedItem.getEventFunctionQuotation();
				quotation.setIsExtraFunction(false);
				eventFunctionQuotationRepository.save(quotation);
			}
			return true;
		} else {
			return false;
		}
	}

	@Override
	@Transactional
	public EventFunctionQuotationResponseDto getEventFunctionQuotationByEventId(Long eventId, Integer isCopyToInvoice,
			Boolean isDecore) {

		EventMasterEntity eventMaster = getEventMaster(eventId);

		EventFunctionQuotationEntity quotation = getOrCreateQuotation(eventMaster, isDecore);

		List<EventFunctionMasterEntity> eventFunctions = getEventFunctions(eventId);

		List<EventFunctionQuotationItemEntity> dbItems = syncDeletedFunctions(quotation, eventFunctions);
		List<EventFunctionQuotationItemsResponseDto> items;
		if (!quotation.getIsDecore()) {

			if (quotation.getIsExtraFunction()) {
				dbItems = syncDefaultFunctions(eventMaster, quotation, dbItems);
			}

			items = mapItems(dbItems);

			items = addMissingEventFunctions(items, eventFunctions);

		} else {
			items = mapItems(dbItems);
		}
		Set<String> existingAddonKeys = extractExistingAddonKeys(items, quotation.getIsDecore());
		syncAddonsToQuotation(quotation, eventFunctions, items, existingAddonKeys);

		Set<String> existingExtraChargesKeys = extractExistingExtraChargesKeys(items, quotation.getIsDecore());
		syncExtraChargesToQuotation(quotation, eventFunctions, items, existingExtraChargesKeys);

		List<EventFunctionQuotationItemsResponseDto> finalItems = orderItems(items, eventFunctions);
		DateTimeFormatter formatter = new DateTimeFormatterBuilder().parseCaseInsensitive()
				.appendPattern("dd/MM/yyyy hh:mm a").toFormatter(Locale.ENGLISH);

		finalItems.sort(Comparator
				.comparing(EventFunctionQuotationItemsResponseDto::getIsEventFunction, Comparator.reverseOrder())
				.thenComparing(item -> {
					String date = item.getFunctionDate();
					return date == null || date.trim().isEmpty();
				}).thenComparing(item -> {
					String date = item.getFunctionDate();

					if (date == null || date.trim().isEmpty()) {
						return null;
					}

					return LocalDateTime.parse(date.trim(), formatter);
				}, Comparator.nullsLast(Comparator.naturalOrder())));
		EventFunctionQuotationResponseDto response = buildResponse(eventMaster, quotation, finalItems);
		if (isCopyToInvoice == 1) {

			response = copyQuotationToInvoice(response, eventMaster);
		}
		return response;
	}

	private void syncExtraChargesToQuotation(EventFunctionQuotationEntity quotation,
			List<EventFunctionMasterEntity> eventFunctions, List<EventFunctionQuotationItemsResponseDto> items,
			Set<String> existingExtraChargesKeys) {
		if (Boolean.TRUE.equals(quotation.getIsDecore())) {
			syncAllExtraChargesToQuotation(quotation, eventFunctions, items, existingExtraChargesKeys);
		} else {
			syncAllExtraChargesToQuotation(quotation, eventFunctions, items, existingExtraChargesKeys);
		}
	}

	private void syncAllExtraChargesToQuotation(EventFunctionQuotationEntity quotation,
			List<EventFunctionMasterEntity> eventFunctions, List<EventFunctionQuotationItemsResponseDto> items,
			Set<String> existingExtraChargesKeys) {
		List<ExtraChargesHeadingEntity> chargesHeadingEntities = extraChargesHeadingRepository
				.findAllByEventFunctionInAndIsDeleteFalse(eventFunctions);
		if (chargesHeadingEntities == null || chargesHeadingEntities.isEmpty()) {
			return;
		}

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a");

		Set<String> validAddonKeys = new HashSet<>();

		for (ExtraChargesHeadingEntity prep : chargesHeadingEntities) {

			Long eventFunctionId = prep.getEventFunction().getId();
			String functionDisplayName = prep.getEventFunction().getFunction().getNameEnglish();
			String functionDate = formatDateTime(prep.getEventFunction().getFunctionStartDateTime(), formatter);

			for (ExtraChargesRowEntity detail : prep.getRows().stream()
					.filter(row -> !Boolean.TRUE.equals(row.getIsDelete())).collect(Collectors.toList())) {

				Long extraChargesId = detail.getId();

				String key = generateExtraChargesKey(eventFunctionId, extraChargesId, quotation.getIsDecore());
				validAddonKeys.add(key);

				if (existingExtraChargesKeys.contains(key)) {
					continue;
				}

				EventFunctionQuotationItemsResponseDto dto = new EventFunctionQuotationItemsResponseDto();

				dto.setId(null);
				dto.setEventFunctionId(eventFunctionId);
				dto.setFunctionDate(functionDate);
				dto.setPax(detail.getPersonItem());
				dto.setExtraPax(0);
				dto.setRatePerPlate(detail.getRate());
				dto.setAmount(detail.getTotal());
				dto.setExtraTax(BigDecimal.ZERO);
				dto.setTaxRate(BigDecimal.ZERO);
				dto.setIsAddons(false);
				dto.setIsEventFunction(true);
				dto.setExtraChargesId(extraChargesId);
				dto.setIsExtraCharges(true);
				dto.setDefaultFunctionId(null);
				dto.setFunctionName((prep.getHeadingName() != null ? prep.getHeadingName().replaceAll("</?b>", "") : "")
						+ "-" + detail.getSession() + " (" + functionDisplayName + ")");
				dto.setMenuCatId(null);
				dto.setItemId(null);

				items.add(dto);
				existingExtraChargesKeys.add(key);

				items.removeIf(item -> Boolean.TRUE.equals(item.getIsExtraCharges())
						&& !validAddonKeys.contains(generateExtraChargesKey(item.getEventFunctionId(),
								item.getExtraChargesId(), quotation.getIsDecore())));
			}
		}

	}

	private Set<String> extractExistingExtraChargesKeys(List<EventFunctionQuotationItemsResponseDto> items,
			Boolean isDecore) {
		return items.stream().filter(i -> Boolean.TRUE.equals(i.getIsExtraCharges()))
				.map(i -> generateExtraChargesKey(i.getEventFunctionId(), i.getExtraChargesId(), isDecore))
				.collect(Collectors.toSet());
	}

	private String generateExtraChargesKey(Long eventFunctionId, Long extraChargesId, Boolean isDecore) {
		return (Boolean.TRUE.equals(isDecore) ? "DECOR_" : "MENU_") + eventFunctionId + "_" + extraChargesId;
	}

	private EventFunctionQuotationResponseDto copyQuotationToInvoice(EventFunctionQuotationResponseDto response,
			EventMasterEntity eventMaster) {

		EventInvoiceEntity oldInvoice = eventInvoiceRepository.findByEventAndIsDeleteFalse(eventMaster);
		if (oldInvoice != null) {
			eventInvoicePaymentRepository.deleteAllByEventInvoice(oldInvoice);
			eventInvoiceFunctionItemRepository.deleteAllByEventInvoice(oldInvoice);
			eventInvoiceRepository.delete(oldInvoice);
		}

		response.setId(null);

		if (response.getEventFunctionQuotationPayments() != null) {
			for (EventFunctionQuotationPaymentResponseDto payment : response.getEventFunctionQuotationPayments()) {
				payment.setId(null);
			}
		}

		if (response.getFunctionQuotationItems() != null) {
			for (EventFunctionQuotationItemsResponseDto item : response.getFunctionQuotationItems()) {
				item.setId(null);
			}
		}

		return response;
	}

	private EventMasterEntity getEventMaster(Long eventId) {
		return eventMasterRepository.findByIdAndIsDeleteFalse(eventId)
				.orElseThrow(() -> new RuntimeException("Event not found: " + eventId));
	}

	public EventFunctionQuotationEntity getOrCreateQuotation(EventMasterEntity eventMaster, Boolean isDecore) {

		EventFunctionQuotationEntity entity = eventFunctionQuotationRepository
				.findByEventAndIsDeleteFalseAndIsDecore(eventMaster, isDecore);

		if (entity != null)
			return entity;

		entity = new EventFunctionQuotationEntity();

		entity.setEvent(eventMaster);
		entity.setIsDelete(false);
		entity.setQuotationCode(
				generateQuotationNo(eventMaster.getUser() != null ? eventMaster.getUser().getId() : null));

		entity.setTotalAmount(BigDecimal.ZERO);
		entity.setCgst("");
		entity.setCgstAmnt(BigDecimal.ZERO);
		entity.setSgst("");
		entity.setSgstAmnt(BigDecimal.ZERO);
		entity.setIgst("");
		entity.setIgstAmnt(BigDecimal.ZERO);
		entity.setDiscount(BigDecimal.ZERO);
		entity.setIsExtraFunction(false);
		entity.setRoundOff(BigInteger.ZERO);
		entity.setGrandTotal(BigInteger.ZERO);
		entity.setSubTotal(BigInteger.ZERO);
		entity.setRemainingAmount(BigInteger.ZERO);
		entity.setTransportation(BigInteger.ZERO);
		entity.setBillingname("");
		entity.setGstnumber("");
		entity.setFoodTax("");
		entity.setFoodTaxAmount(BigInteger.ZERO);
		entity.setFoodTaxTotalAmount(BigInteger.ZERO);
		entity.setServiceTax("");
		entity.setServiceTaxAmount(BigInteger.ZERO);
		entity.setServiceTaxTotalAmount(BigInteger.ZERO);
		entity.setVatTax("");
		entity.setVatTaxAmount(BigInteger.ZERO);
		entity.setVatTaxTotalAmount(BigInteger.ZERO);
		entity.setDiscountPct("");
		entity.setIsDiscountPercent(false);
		String odcNotes = eventRemarkMasterRepository
				.findFirstByTypeContainingIgnoreCaseAndUserAndIsDeleteFalse("odc", eventMaster.getUser())
				.map(EventRemarkMasterEntity::getNameEnglish).orElse("");

		String banqNotes = eventRemarkMasterRepository
				.findFirstByTypeContainingIgnoreCaseAndUserAndIsDeleteFalse("banquet", eventMaster.getUser())
				.map(EventRemarkMasterEntity::getNameEnglish).orElse("");
		if (eventMaster.getBanquetHall() != null) {
			entity.setNotes(banqNotes);
		} else {
			entity.setNotes(odcNotes);
		}

		String pooja_rooms = eventRemarkMasterRepository
				.findFirstByTypeContainingIgnoreCaseAndUserAndIsDeleteFalse("pooja rooms", eventMaster.getUser())
				.map(EventRemarkMasterEntity::getNameEnglish).orElse("");

		entity.setPoojaRooms(pooja_rooms);

		String iron_service = eventRemarkMasterRepository
				.findFirstByTypeContainingIgnoreCaseAndUserAndIsDeleteFalse("iron service", eventMaster.getUser())
				.map(EventRemarkMasterEntity::getNameEnglish).orElse("");

		entity.setIronService(iron_service);

		String venue_remark = eventRemarkMasterRepository
				.findFirstByTypeContainingIgnoreCaseAndUserAndIsDeleteFalse("venue remark", eventMaster.getUser())
				.map(EventRemarkMasterEntity::getNameEnglish).orElse("");

		entity.setVenueRemark(venue_remark);

		entity.setVenueTotal(BigInteger.ZERO);
		entity.setIsLocked(false);
		entity.setIsDecore(isDecore);
		return eventFunctionQuotationRepository.save(entity);
	}

	private List<EventFunctionMasterEntity> getEventFunctions(Long eventId) {
		return eventFunctionMasterRepository.findAllByEventIdAndIsDeleteFalseOrderBySortorderAsc(eventId);
	}

	private List<EventFunctionQuotationItemEntity> syncDeletedFunctions(EventFunctionQuotationEntity quotation,
			List<EventFunctionMasterEntity> eventFunctions) {

		System.out.println("quotation:-" + quotation.getId());
		List<EventFunctionQuotationItemEntity> dbItems = eventFunctionQuotationItemRepository
				.findAllByEventFunctionQuotationAndIsDeleteFalse(quotation);

		if (!quotation.getIsDecore()) {
			System.out.println("inn");
			Set<Long> validIds = eventFunctions.stream().map(EventFunctionMasterEntity::getId)
					.collect(Collectors.toSet());

			List<Long> toDelete = dbItems.stream()
					.filter(i -> Boolean.TRUE.equals(i.getIsEventFunction()) && i.getEventFunctionId() != null
							&& !validIds.contains(i.getEventFunctionId()))
					.map(EventFunctionQuotationItemEntity::getId).collect(Collectors.toList());
			if (!toDelete.isEmpty()) {
				eventFunctionQuotationItemRepository.deleteAllByItemIds(toDelete);

				return eventFunctionQuotationItemRepository.findAllByEventFunctionQuotationAndIsDeleteFalse(quotation);
			}
		}

		return dbItems;
	}

	private List<EventFunctionQuotationItemEntity> syncDefaultFunctions(EventMasterEntity eventMaster,
			EventFunctionQuotationEntity quotation, List<EventFunctionQuotationItemEntity> dbItems) {

		List<DefaultExtraQuotationFunctionResponseDto> defaults = defaultExtraQuotationFunctionService
				.getAll(eventMaster.getUser().getId(), true);

		Set<Long> existing = dbItems.stream().filter(i -> Boolean.FALSE.equals(i.getIsEventFunction()))
				.map(EventFunctionQuotationItemEntity::getDefaultFunctionId).filter(Objects::nonNull)
				.collect(Collectors.toSet());

		if (existing.isEmpty()) {
			for (DefaultExtraQuotationFunctionResponseDto def : defaults) {
				EventFunctionQuotationItemEntity item = new EventFunctionQuotationItemEntity();
				item.setEventFunctionId(null);
				item.setEventFunctionQuotation(quotation);
				item.setFunctionName(def.getName());
				item.setIsEventFunction(false);
				item.setDefaultFunctionId(def.getId());
				item.setAmount(def.getPrice());
				item.setOptions("");
				item.setExtraChargesId(null);
				item.setIsExtraCharges(false);
				item.setFunctionDate(eventMaster.getEventStartDateTime());

				dbItems.add(item);
			}
		}

		return dbItems;
	}

	private List<EventFunctionQuotationItemsResponseDto> mapItems(List<EventFunctionQuotationItemEntity> dbItems) {

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a");

		List<EventFunctionQuotationItemsResponseDto> items = new ArrayList<>();

		for (EventFunctionQuotationItemEntity item : dbItems) {
			EventFunctionQuotationItemsResponseDto dto = new EventFunctionQuotationItemsResponseDto();

			dto.setId(item.getId());
			dto.setFunctionName(item.getFunctionName());
			dto.setFunctionDate(formatDateTime(item.getFunctionDate(), formatter));
			dto.setPax(item.getPax());
			dto.setExtraPax(item.getExtraPax());

			dto.setEventFunctionId(item.getEventFunctionId());
			dto.setRatePerPlate(item.getRatePerPlate());
			dto.setOfferedRate(item.getOfferedRate());
			dto.setAmount(EventFunctionMenuAllocationServiceImpl.removeDecimal(item.getAmount()));
			dto.setExtraTax(item.getExtraTax());
			dto.setTaxRate(item.getTaxRate());

			dto.setIsEventFunction(item.getIsEventFunction());
			dto.setDefaultFunctionId(item.getDefaultFunctionId());

			dto.setIsAddons(item.getIsAddons());
			dto.setMenuCatId(item.getMenuCatId());
			dto.setItemId(item.getItemId());
			dto.setOptions(item.getOptions());
			dto.setIsLocked(item.getIsLocked());
			dto.setExtraChargesId(item.getExtraChargesId());
			dto.setIsExtraCharges(item.getIsExtraCharges());
			// ── Custom Package from menu preparation ──────────────────────────
			dto.setCustomPackageId(item.getCustomPackageId());
			dto.setCustomPackageName(item.getCustomPackageName());
			dto.setCustomPackagePrice(item.getCustomPackagePrice());
			// ─────────────────────────────────────────────────────────────────

			items.add(dto);
		}

		return items;
	}

	private List<EventFunctionQuotationItemsResponseDto> addMissingEventFunctions(
			List<EventFunctionQuotationItemsResponseDto> items, List<EventFunctionMasterEntity> eventFunctions) {

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a");

		Set<Long> existing = items.stream().filter(i -> i.getEventFunctionId() != null && i.getExtraChargesId() == null)
				.map(EventFunctionQuotationItemsResponseDto::getEventFunctionId).collect(Collectors.toSet());

		for (EventFunctionMasterEntity ef : eventFunctions) {

			if (!existing.contains(ef.getId())) {

				EventFunctionQuotationItemsResponseDto dto = new EventFunctionQuotationItemsResponseDto();

				dto.setId(null);
				dto.setFunctionName(ef.getFunction().getNameEnglish());

				if (ef.getFunctionStartDateTime() != null) {
					dto.setFunctionDate(formatter.format(ef.getFunctionStartDateTime()));
				}

				dto.setPax(ef.getPax());
				dto.setExtraPax(0);
				dto.setEventFunctionId(ef.getId());

				BigDecimal rate = menuPreparationRepository.getPackageRate(ef.getId());

				if (rate != null && rate.compareTo(BigDecimal.ZERO) > 0) {
					dto.setRatePerPlate(rate);
				} else {
					dto.setRatePerPlate(BigDecimal.valueOf(ef.getRate()));
				}
				dto.setOfferedRate(dto.getRatePerPlate());
				dto.setAmount(dto.getPax() == null || dto.getRatePerPlate() == null ? BigDecimal.ZERO
						: new BigDecimal(dto.getPax()).multiply(dto.getRatePerPlate()));
				dto.setExtraTax(BigDecimal.ZERO);
				dto.setTaxRate(BigDecimal.ZERO);

				dto.setIsEventFunction(true);
				dto.setDefaultFunctionId(null);

				dto.setIsAddons(false);
				dto.setMenuCatId(null);
				dto.setItemId(null);
				dto.setIsLocked(false);
				dto.setIsExtraCharges(false);
				dto.setExtraChargesId(null);
				try {
					if (ef.getCustomPackage() != null) {
						dto.setCustomPackageId(ef.getCustomPackage().getId());
						dto.setCustomPackageName(ef.getCustomPackage().getNameEnglish());
						dto.setCustomPackagePrice(ef.getCustomPackage().getPrice());
					}
				} catch (Exception e) {
					System.out.println("Package lookup failed for function " + ef.getId() + ": " + e.getMessage());
				}

				items.add(dto);
			}
		}

		return items;
	}

	private Set<String> extractExistingAddonKeys(List<EventFunctionQuotationItemsResponseDto> items, Boolean isDecore) {

		return items.stream().filter(i -> Boolean.TRUE.equals(i.getIsAddons()))
				.map(i -> Boolean.TRUE.equals(isDecore)
						? generateDecoreAddonKey(i.getEventFunctionId(), i.getMenuCatId(), i.getItemId())
						: generateAddonKey(i.getEventFunctionId(), i.getMenuCatId(), i.getItemId()))
				.collect(Collectors.toSet());
	}

	private String generateAddonKey(Long eventFunctionId, Long menuCatId, Long itemId) {
		return "MENU_" + eventFunctionId + "_" + (menuCatId != null ? menuCatId : "null") + "_"
				+ (itemId != null ? itemId : "null");
	}

	private void syncAddonsToQuotation(EventFunctionQuotationEntity quotation,
			List<EventFunctionMasterEntity> eventFunctions, List<EventFunctionQuotationItemsResponseDto> items,
			Set<String> existingAddonKeys) {

		if (Boolean.TRUE.equals(quotation.getIsDecore())) {
			syncDecoreAddonsToQuotation(quotation, eventFunctions, items, existingAddonKeys);
		} else {
			syncMenuAddonsToQuotation(quotation, eventFunctions, items, existingAddonKeys);
		}
	}

	private void syncMenuAddonsToQuotation(EventFunctionQuotationEntity quotation,
			List<EventFunctionMasterEntity> eventFunctions, List<EventFunctionQuotationItemsResponseDto> items,
			Set<String> existingAddonKeys) {

		List<MenuPreparationEntity> preparations = menuPreparationRepository
				.findByEventFunctionInAndIsDeleteFalse(eventFunctions);

		if (preparations == null || preparations.isEmpty()) {
			return;
		}

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a");

		Set<String> validAddonKeys = new HashSet<>();

		for (MenuPreparationEntity prep : preparations) {

			Long eventFunctionId = prep.getEventFunction().getId();
			String functionDisplayName = prep.getEventFunction().getFunction().getNameEnglish();
			String functionDate = formatDateTime(prep.getEventFunction().getFunctionStartDateTime(), formatter);

			List<MenuPreparationDetailsEntity> details = menuPreparationDetailsRepository
					.findAllByMenuPreparation(prep);

			for (MenuPreparationDetailsEntity detail : details) {

				if (!Boolean.TRUE.equals(detail.getIsMenuCatAddons())
						&& !Boolean.TRUE.equals(detail.getIsItemAddons())) {
					continue;
				}

				Long menuCatId = Boolean.TRUE.equals(detail.getIsMenuCatAddons()) ? detail.getMenuCategory().getId()
						: null;

				Long itemId = Boolean.TRUE.equals(detail.getIsItemAddons()) ? detail.getMenuItem().getId() : null;

				String key = generateAddonKey(eventFunctionId, menuCatId, itemId);
				validAddonKeys.add(key);

				if (existingAddonKeys.contains(key)) {
					continue;
				}

				EventFunctionQuotationItemsResponseDto dto = new EventFunctionQuotationItemsResponseDto();
				dto.setId(null);
				dto.setEventFunctionId(eventFunctionId);
				dto.setFunctionDate(functionDate);
				dto.setPax(prep.getPax());
				dto.setExtraPax(0);
				dto.setRatePerPlate(detail.getItemPrice());
				dto.setAmount(calculateDecoreAmount(dto.getPax(), dto.getRatePerPlate()));
				dto.setExtraTax(BigDecimal.ZERO);
				dto.setTaxRate(BigDecimal.ZERO);
				dto.setIsAddons(true);
				dto.setIsEventFunction(true);
				dto.setExtraChargesId(null);
				dto.setIsExtraCharges(false);
				if (menuCatId != null) {
					dto.setFunctionName(detail.getMenuCategoryName() + " (" + functionDisplayName + ")");
					dto.setMenuCatId(menuCatId);
				} else {
					dto.setFunctionName(detail.getMenuItemName() + " (" + functionDisplayName + ")");
					dto.setItemId(itemId);
				}

				items.add(dto);
				existingAddonKeys.add(key);
			}
		}

		items.removeIf(item -> Boolean.TRUE.equals(item.getIsAddons()) && !validAddonKeys
				.contains(generateAddonKey(item.getEventFunctionId(), item.getMenuCatId(), item.getItemId())));
	}

	private void syncDecoreAddonsToQuotation(EventFunctionQuotationEntity quotation,
			List<EventFunctionMasterEntity> eventFunctions, List<EventFunctionQuotationItemsResponseDto> items,
			Set<String> existingAddonKeys) {

		List<DecorePreparationEntity> preparations = decorePreparationRepository
				.findByEventFunctionInAndIsDeleteFalse(eventFunctions);

		if (preparations == null || preparations.isEmpty()) {
			return;
		}

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a");

		Set<String> validAddonKeys = new HashSet<>();

		for (DecorePreparationEntity prep : preparations) {

			Long eventFunctionId = prep.getEventFunction().getId();
			String functionDisplayName = prep.getEventFunction().getFunction().getNameEnglish();
			String functionDate = formatDateTime(prep.getEventFunction().getFunctionStartDateTime(), formatter);

			List<DecorePreparationDetailsEntity> details = decorePreparationDetailsRepository
					.findAllByDecorePreparation(prep);

			for (DecorePreparationDetailsEntity detail : details) {

				boolean isCatAddon = Boolean.TRUE.equals(detail.getIsDecoreCatAddons());
				boolean isItemAddon = Boolean.TRUE.equals(detail.getIsDecoreItemAddons());

				if (!isCatAddon && !isItemAddon) {
					continue;
				}

				Long decoreMainCategoryId = isCatAddon && detail.getDecoreMainCategory() != null
						? detail.getDecoreMainCategory().getId()
						: null;

				Long decoreItemId = isItemAddon && detail.getDecoreItem() != null ? detail.getDecoreItem().getId()
						: null;

				String key = generateDecoreAddonKey(eventFunctionId, decoreMainCategoryId, decoreItemId);
				validAddonKeys.add(key);

				if (existingAddonKeys.contains(key)) {
					continue;
				}

				EventFunctionQuotationItemsResponseDto dto = new EventFunctionQuotationItemsResponseDto();

				dto.setId(null);
				dto.setEventFunctionId(eventFunctionId);
				dto.setFunctionDate(functionDate);
				dto.setPax(detail.getItemQty());
				dto.setExtraPax(0);
				dto.setRatePerPlate(detail.getDecoreItemPrice());
				dto.setAmount(calculateDecoreAmount(detail.getItemQty(), detail.getDecoreItemPrice()));
				dto.setExtraTax(BigDecimal.ZERO);
				dto.setTaxRate(BigDecimal.ZERO);
				dto.setIsAddons(true);
				dto.setIsEventFunction(true);
				dto.setDefaultFunctionId(null);

				if (isCatAddon) {
					dto.setFunctionName(detail.getDecoreCategoryName() + " (" + functionDisplayName + ")");
					dto.setMenuCatId(decoreMainCategoryId);
					dto.setItemId(null);
				} else {
					dto.setFunctionName(detail.getDecoreItemName() + " (" + functionDisplayName + ")");
					dto.setMenuCatId(null);
					dto.setItemId(decoreItemId);
				}

				items.add(dto);
				existingAddonKeys.add(key);
			}
		}

		items.removeIf(item -> Boolean.TRUE.equals(item.getIsAddons()) && item.getEventFunctionId() != null
				&& !validAddonKeys.contains(
						generateDecoreAddonKey(item.getEventFunctionId(), item.getMenuCatId(), item.getItemId())));
	}

	private BigDecimal calculateDecoreAmount(Integer itemQty, BigDecimal itemPrice) {
		if (itemQty == null || itemPrice == null) {
			return null;
		}
		return BigDecimal.valueOf(itemQty).multiply(itemPrice);
	}

	private String generateDecoreAddonKey(Long eventFunctionId, Long decoreMainCategoryId, Long decoreItemId) {
		return "DECORE_" + eventFunctionId + "_" + (decoreMainCategoryId != null ? decoreMainCategoryId : "null") + "_"
				+ (decoreItemId != null ? decoreItemId : "null");
	}

	private List<EventFunctionQuotationItemsResponseDto> orderItems(List<EventFunctionQuotationItemsResponseDto> items,
			List<EventFunctionMasterEntity> eventFunctions) {

		Map<Long, EventFunctionQuotationItemsResponseDto> eventMap = new HashMap<>();
		Map<Long, List<EventFunctionQuotationItemsResponseDto>> addonMap = new HashMap<>();
		Map<Long, List<EventFunctionQuotationItemsResponseDto>> extrahargesMap = new HashMap<>();
		List<EventFunctionQuotationItemsResponseDto> extra = new ArrayList<>();

		for (EventFunctionQuotationItemsResponseDto item : items) {

			if (Boolean.TRUE.equals(item.getIsAddons())) {
				addonMap.computeIfAbsent(item.getEventFunctionId(), k -> new ArrayList<>()).add(item);

			} else if (Boolean.FALSE.equals(item.getIsEventFunction())) {
				extra.add(item);

			} else if (Boolean.TRUE.equals(item.getIsExtraCharges())) {
				extrahargesMap.computeIfAbsent(item.getEventFunctionId(), k -> new ArrayList<>()).add(item);
			} else {
				eventMap.put(item.getEventFunctionId(), item);
			}
		}

		List<EventFunctionQuotationItemsResponseDto> result = new ArrayList<>();

		for (EventFunctionMasterEntity ef : eventFunctions) {

			Long id = ef.getId();
			EventFunctionQuotationItemsResponseDto event = eventMap.get(id);

			if (event != null) {
				result.add(event);
			}

			List<EventFunctionQuotationItemsResponseDto> addons = addonMap.get(id);
			if (addons != null) {
				result.addAll(addons);
			}

			List<EventFunctionQuotationItemsResponseDto> extraCharges = extrahargesMap.get(id);

			if (extraCharges != null) {
				result.addAll(extraCharges);
			}
		}

		eventFunctionMasterRepository.saveAll(eventFunctions);

		result.addAll(extra);

		return result;
	}

	private EventFunctionQuotationResponseDto buildResponse(EventMasterEntity eventMaster,
			EventFunctionQuotationEntity quotation, List<EventFunctionQuotationItemsResponseDto> items) {

		EventFunctionQuotationResponseDto response = new EventFunctionQuotationResponseDto();

		EventMasterResponseDto eventDto = eventMasterMapper.entityToResponse(eventMaster);

		response.setEvent(eventDto);
		response.setId(quotation.getId());
		System.out.println("4. Size:- " + items.size());
		response.setFunctionQuotationItems(items);

		response.setTotalAmount(EventFunctionMenuAllocationServiceImpl.removeDecimal(quotation.getTotalAmount()));

		response.setCashPayment(quotation.getCashPayment());
		response.setChequePayment(quotation.getChequePayment());

		response.setCgst(quotation.getCgst());
		response.setCgstAmnt(EventFunctionMenuAllocationServiceImpl.removeDecimal(quotation.getCgstAmnt()));

		response.setSgst(quotation.getSgst());
		response.setSgstAmnt(EventFunctionMenuAllocationServiceImpl.removeDecimal(quotation.getSgstAmnt()));

		response.setIgst(quotation.getIgst());
		response.setIgstAmnt(EventFunctionMenuAllocationServiceImpl.removeDecimal(quotation.getIgstAmnt()));

		response.setDiscount(EventFunctionMenuAllocationServiceImpl.removeDecimal(quotation.getDiscount()));

		response.setIsExtraFunction(quotation.getIsExtraFunction());
		response.setRoundOff(quotation.getRoundOff());
		response.setGrandTotal(quotation.getGrandTotal());
		response.setRemainingAmount(quotation.getRemainingAmount());
		response.setSubTotal(quotation.getSubTotal());

		response.setQuotationCode(quotation.getQuotationCode());
		response.setBillingname(quotation.getBillingname());
		response.setGstnumber(quotation.getGstnumber());
		// response.setNotes(quotation.getNotes());
		response.setTransportation(quotation.getTransportation());

		response.setFoodTax(quotation.getFoodTax());
		response.setFoodTaxAmount(quotation.getFoodTaxAmount());
		response.setFoodTaxTotalAmount(quotation.getFoodTaxTotalAmount());
		response.setServiceTax(quotation.getServiceTax());
		response.setServiceTaxAmount(quotation.getServiceTaxAmount());
		response.setServiceTaxTotalAmount(quotation.getServiceTaxTotalAmount());
		response.setVatTax(quotation.getVatTax());
		response.setVatTaxAmount(quotation.getVatTaxAmount());
		response.setVatTaxTotalAmount(quotation.getVatTaxTotalAmount());
		response.setDiscountPct(quotation.getDiscountPct());
		String notes = quotation.getNotes();

		if (notes == null || notes.trim().isEmpty()) {
			notes = eventRemarkMasterRepository
					.findFirstByTypeContainingIgnoreCaseAndUserAndIsDeleteFalse("remark", eventMaster.getUser())
					.map(EventRemarkMasterEntity::getNameEnglish).orElse("");
		}

		response.setNotes(notes);

		String pooja_rooms = quotation.getPoojaRooms();

		if (pooja_rooms == null || pooja_rooms.trim().isEmpty()) {
			pooja_rooms = eventRemarkMasterRepository
					.findFirstByTypeContainingIgnoreCaseAndUserAndIsDeleteFalse("pooja rooms", eventMaster.getUser())
					.map(EventRemarkMasterEntity::getNameEnglish).orElse("");
		}

		response.setPoojaRooms(pooja_rooms);
		response.setIsDecore(quotation.getIsDecore());
		String iron_service = quotation.getIronService();

		if (iron_service == null || iron_service.trim().isEmpty()) {
			iron_service = eventRemarkMasterRepository
					.findFirstByTypeContainingIgnoreCaseAndUserAndIsDeleteFalse("iron service", eventMaster.getUser())
					.map(EventRemarkMasterEntity::getNameEnglish).orElse("");
		}

		response.setIronService(iron_service);

		String venue_remark = quotation.getVenueRemark();

		if (venue_remark == null || venue_remark.trim().isEmpty()) {
			venue_remark = eventRemarkMasterRepository
					.findFirstByTypeContainingIgnoreCaseAndUserAndIsDeleteFalse("venue remark", eventMaster.getUser())
					.map(EventRemarkMasterEntity::getNameEnglish).orElse("");
		}

		response.setVenueRemark(venue_remark);

		response.setVenueTotal(quotation.getVenueTotal() != null ? quotation.getVenueTotal() : BigInteger.ZERO);

		try {
			response.setDuedate(quotation.getDuedate() != null ? quotation.getDuedate().format(formatter) : "");

			response.setQuotationdate(
					quotation.getQuotationdate() != null ? quotation.getQuotationdate().format(formatter) : "");

		} catch (Exception e) {
			response.setDuedate("");
			response.setQuotationdate("");
		}

		response.setCreatedAt(formatDateTime(quotation.getCreatedAt(), FUNCTION_DATE_FORMATTER).split(" ")[0]);

		response.setIsLocked(quotation.getIsLocked());

		// Payments
		List<EventFunctionQuotationPaymentResponseDto> payments = new ArrayList<>();

		eventFunctionQuotationPaymentRepository.findAllByEventFunctionQuotationAndIsDeleteFalse(quotation)
				.forEach(payment -> {

					EventFunctionQuotationPaymentResponseDto dto = new EventFunctionQuotationPaymentResponseDto();

					dto.setId(payment.getId());
					dto.setAdvancePaymentDate(formatDateTime(payment.getAdvancePaymentDate(), FUNCTION_DATE_FORMATTER));
					dto.setAdvancePayment(payment.getAdvancePayment());
					dto.setAdvancePaymentNotes(payment.getAdvancePaymentNotes());
					dto.setBankId(payment.getBankId());
					dto.setCashAccountId(payment.getCashAccountId());
					dto.setPaymentMode(payment.getPaymentMode());
					dto.setVendorCode(payment.getVendorCode());
					payments.add(dto);
				});

		response.setEventFunctionQuotationPayments(payments);

		// Security Deposit
		List<EventQuotationSecurityDepositEntity> securityDepositesEntities = eventQuotationSecurityDepositRepository
				.findAllByEventIdAndIsDeleteFalse(eventMaster.getId());

		List<EventQuotationSecurityDepositResponseDto> securityDeposite = securityDepositesEntities.stream()
				.map(entity -> eventQuotationSecurityDepositServiceImpl.entityToResponse(entity))
				.collect(Collectors.toList());

		response.setEventQuotationSecurityDeposit(securityDeposite);

		return response;
	}

	private String formatDateTime(LocalDateTime dateTime, DateTimeFormatter formatter) {
		return dateTime != null ? dateTime.format(formatter).toUpperCase() : "";
	}

	@Override
	public List<EventFunctionQuotationResponseDto> getEventFunctionQuotationByUserId(Long userid, Boolean isDecore) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a");

		List<EventFunctionQuotationResponseDto> eventFunctionQuotationResponseDto = new ArrayList<>();

		UserMasterEntity userMaster = userMasterRepository.findByIdAndIsDeleteFalse(userid)
				.orElseThrow(() -> new RuntimeException("User Master with this userId not found: " + userid));

		List<EventFunctionQuotationEntity> allQuotations = eventFunctionQuotationRepository
				.findAllByUserAndIsDeleteFalseAndIsDecore(userMaster, isDecore);

		Map<Long, EventFunctionQuotationEntity> latestQuotationsByParty = new HashMap<>();

		for (EventFunctionQuotationEntity quotation : allQuotations) {
			if (quotation.getEvent() != null && quotation.getEvent().getParty() != null) {
				Long partyId = quotation.getEvent().getParty().getId();
				EventFunctionQuotationEntity existing = latestQuotationsByParty.get(partyId);

				if (existing == null) {
					latestQuotationsByParty.put(partyId, quotation);
				} else {
					LocalDateTime existingDate = existing.getUpdatedAt() != null ? existing.getUpdatedAt()
							: existing.getCreatedAt();
					LocalDateTime currentDate = quotation.getUpdatedAt() != null ? quotation.getUpdatedAt()
							: quotation.getCreatedAt();

					if (currentDate.isAfter(existingDate)) {
						latestQuotationsByParty.put(partyId, quotation);
					}
				}
			}
		}

		for (EventFunctionQuotationEntity quotation : latestQuotationsByParty.values()) {

			EventMasterResponseDto eventDto = eventMasterMapper.entityToResponse(quotation.getEvent());

			List<EventFunctionQuotationPaymentResponseDto> paymentResponseDtos = new ArrayList<>();

			eventFunctionQuotationPaymentRepository.findAllByEventFunctionQuotationAndIsDeleteFalse(quotation)
					.forEach(payment -> {
						EventFunctionQuotationPaymentResponseDto eventFunctionQuotationPaymentResponseDto = new EventFunctionQuotationPaymentResponseDto();
						eventFunctionQuotationPaymentResponseDto.setId(payment.getId());
						eventFunctionQuotationPaymentResponseDto.setAdvancePaymentDate(
								formatDateTime(payment.getAdvancePaymentDate(), FUNCTION_DATE_FORMATTER));
						eventFunctionQuotationPaymentResponseDto.setAdvancePayment(payment.getAdvancePayment());
						eventFunctionQuotationPaymentResponseDto
								.setAdvancePaymentNotes(payment.getAdvancePaymentNotes());
						eventFunctionQuotationPaymentResponseDto.setBankId(payment.getBankId());
						eventFunctionQuotationPaymentResponseDto.setCashAccountId(payment.getCashAccountId());
						eventFunctionQuotationPaymentResponseDto.setPaymentMode(payment.getPaymentMode());
						paymentResponseDtos.add(eventFunctionQuotationPaymentResponseDto);
					});

			List<EventFunctionQuotationItemsResponseDto> items = new ArrayList<>();
			List<EventFunctionQuotationItemEntity> itemEntities = eventFunctionQuotationItemRepository
					.findAllByEventFunctionQuotationAndIsDeleteFalse(quotation);

			for (EventFunctionQuotationItemEntity item : itemEntities) {
				EventFunctionQuotationItemsResponseDto dto = new EventFunctionQuotationItemsResponseDto();
				dto.setId(item.getId());
				dto.setFunctionName(item.getFunctionName());
				dto.setFunctionDate(formatDateTime(item.getFunctionDate(), dateTimeFormatter));
				dto.setPax(item.getPax());
				dto.setEventFunctionId(item.getEventFunctionId());
				dto.setExtraPax(item.getExtraPax());
				dto.setRatePerPlate(item.getRatePerPlate());
				dto.setAmount(item.getAmount());
				dto.setExtraTax(item.getExtraTax());
				dto.setTaxRate(item.getTaxRate());
				dto.setIsEventFunction(item.getIsEventFunction());
				dto.setDefaultFunctionId(item.getDefaultFunctionId());
				dto.setIsAddons(item.getIsAddons());
				dto.setMenuCatId(item.getMenuCatId());
				dto.setItemId(item.getItemId());
				dto.setIsLocked(item.getIsLocked());
				dto.setIsExtraCharges(false);
				dto.setExtraChargesId(null);
				items.add(dto);
			}

			EventFunctionQuotationResponseDto response = new EventFunctionQuotationResponseDto();
			response.setEvent(eventDto);
			response.setId(quotation.getId());
			response.setFunctionQuotationItems(items);
			response.setTotalAmount(quotation.getTotalAmount());
			response.setCashPayment(quotation.getCashPayment());
			response.setChequePayment(quotation.getChequePayment());
			response.setCgst(quotation.getCgst());
			response.setCgstAmnt(quotation.getCgstAmnt());
			response.setSgst(quotation.getSgst());
			response.setSgstAmnt(quotation.getSgstAmnt());
			response.setIgst(quotation.getIgst());
			response.setIgstAmnt(quotation.getIgstAmnt());
			response.setDiscount(quotation.getDiscount());
			response.setRoundOff(quotation.getRoundOff());
			response.setGrandTotal(quotation.getGrandTotal());
			response.setSubTotal(quotation.getSubTotal());
			response.setRemainingAmount(quotation.getRemainingAmount());
			response.setNotes(quotation.getNotes());
			response.setQuotationCode(quotation.getQuotationCode());
			response.setQuotationCode(quotation.getQuotationCode());
			response.setBillingname(quotation.getBillingname());
			response.setGstnumber(quotation.getGstnumber());
			response.setIsLocked(quotation.getIsLocked());
			response.setTransportation(quotation.getTransportation());
			response.setFoodTax(quotation.getFoodTax());
			response.setFoodTaxAmount(quotation.getFoodTaxAmount());
			response.setFoodTaxTotalAmount(quotation.getFoodTaxTotalAmount());
			response.setServiceTax(quotation.getServiceTax());
			response.setServiceTaxAmount(quotation.getServiceTaxAmount());
			response.setServiceTaxTotalAmount(quotation.getServiceTaxTotalAmount());
			response.setVatTax(quotation.getVatTax());
			response.setVatTaxAmount(quotation.getVatTaxAmount());
			response.setVatTaxTotalAmount(quotation.getVatTaxTotalAmount());
			response.setDiscountPct(quotation.getDiscountPct());
			response.setIsDiscountPercent(quotation.getIsDiscountPercent());
			try {
				response.setDuedate(quotation.getDuedate() != null ? quotation.getDuedate().format(formatter) : "");
				response.setQuotationdate(
						quotation.getQuotationdate() != null ? quotation.getQuotationdate().format(formatter) : "");
			} catch (Exception e) {
				response.setDuedate("");
				response.setQuotationdate("");
			}
			response.setEventFunctionQuotationPayments(paymentResponseDtos);
			response.setCreatedAt(formatDateTime(quotation.getCreatedAt(), FUNCTION_DATE_FORMATTER).split(" ")[0]);
			eventFunctionQuotationResponseDto.add(response);
		}

		Collections.sort(eventFunctionQuotationResponseDto, new Comparator<EventFunctionQuotationResponseDto>() {
			@Override
			public int compare(EventFunctionQuotationResponseDto a, EventFunctionQuotationResponseDto b) {
				EventFunctionQuotationEntity qa = latestQuotationsByParty.get(a.getEvent().getParty().getId());
				EventFunctionQuotationEntity qb = latestQuotationsByParty.get(b.getEvent().getParty().getId());

				LocalDateTime da = qa.getUpdatedAt() != null ? qa.getUpdatedAt() : qa.getCreatedAt();
				LocalDateTime db = qb.getUpdatedAt() != null ? qb.getUpdatedAt() : qb.getCreatedAt();

				return db.compareTo(da); // descending
			}
		});

		return eventFunctionQuotationResponseDto;
	}

	@Override
	public List<EventFunctionQuotationResponseDto> getEventFunctionQuotationByUserIdAndDateWise(Long userid,
			String startDate, String endDate, Boolean isVenue, Long id, Boolean isDecore) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a");

		List<EventFunctionQuotationResponseDto> eventFunctionQuotationResponseDto = new ArrayList<>();

		// Fetch user
		UserMasterEntity userMaster = userMasterRepository.findByIdAndIsDeleteFalse(userid)
				.orElseThrow(() -> new RuntimeException("User Master with this userId not found: " + userid));

		List<EventFunctionQuotationEntity> allQuotations = new ArrayList<>();
		if ((startDate == null || startDate.trim().isEmpty()) && (endDate == null || endDate.trim().isEmpty())) {

			if (isVenue == null) {

				allQuotations = eventFunctionQuotationRepository
						.findAllByUserAndIsDeleteFalseAndEvent_StatusAndIsDecore(userMaster, 1, isDecore);

			} else if (Boolean.TRUE.equals(isVenue)) {

				allQuotations = eventFunctionQuotationRepository
						.findAllByUserAndIsDeleteFalseAndEvent_StatusAndEvent_Venue_IdAndIsDecore(userMaster, 1, id,
								isDecore);

			} else {

				allQuotations = eventFunctionQuotationRepository
						.findAllByUserAndIsDeleteFalseAndEvent_StatusAndEvent_BanquetHall_IdAndIsDecore(userMaster, 1,
								id, isDecore);
			}

		} else {

			DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			LocalDateTime start = LocalDate.parse(startDate, dateFormatter).atStartOfDay();
			LocalDateTime end = LocalDate.parse(endDate, dateFormatter).atTime(23, 59, 59);

			if (isVenue == null) {

				allQuotations = eventFunctionQuotationRepository
						.findAllByUserAndIsDeleteFalseAndEvent_StatusAndEvent_EventStartDateTimeBetweenAndIsDecore(
								userMaster, 1, start, end, isDecore);

			} else if (Boolean.TRUE.equals(isVenue)) {

				allQuotations = eventFunctionQuotationRepository
						.findAllByUserAndIsDeleteFalseAndEvent_StatusAndEvent_EventStartDateTimeBetweenAndEvent_Venue_IdAndIsDecore(
								userMaster, 1, start, end, id, isDecore);

			} else {

				allQuotations = eventFunctionQuotationRepository
						.findAllByUserAndIsDeleteFalseAndEvent_StatusAndEvent_EventStartDateTimeBetweenAndEvent_BanquetHall_IdAndIsDecore(
								userMaster, 1, start, end, id, isDecore);
			}
		}

		BigInteger overallTotalAmnt = BigInteger.ZERO;
		BigInteger overAllReceivableAmnt = BigInteger.ZERO;
		BigInteger overAllRemainingAmnt = BigInteger.ZERO;
		for (EventFunctionQuotationEntity quotation : allQuotations) {
			overallTotalAmnt = overallTotalAmnt
					.add(quotation.getGrandTotal() != null ? quotation.getGrandTotal() : BigInteger.ZERO);
			overAllReceivableAmnt = overAllReceivableAmnt.add(
					quotation.getTotalAmount() != null ? quotation.getTotalAmount().toBigInteger() : BigInteger.ZERO);
			overAllRemainingAmnt = overAllRemainingAmnt
					.add(quotation.getRemainingAmount() != null ? quotation.getRemainingAmount() : BigInteger.ZERO);
		}

		allQuotations.sort(Comparator.comparing(q -> q.getEvent().getEventStartDateTime(),
				Comparator.nullsLast(Comparator.naturalOrder())));

		for (EventFunctionQuotationEntity quotation : allQuotations) {

			EventMasterResponseDto eventDto = eventMasterMapper.entityToResponse(quotation.getEvent());

			List<EventFunctionQuotationPaymentResponseDto> paymentResponseDtos = new ArrayList<>();

			eventFunctionQuotationPaymentRepository.findAllByEventFunctionQuotationAndIsDeleteFalse(quotation)
					.forEach(payment -> {
						EventFunctionQuotationPaymentResponseDto eventFunctionQuotationPaymentResponseDto = new EventFunctionQuotationPaymentResponseDto();
						eventFunctionQuotationPaymentResponseDto.setId(payment.getId());
						eventFunctionQuotationPaymentResponseDto.setAdvancePaymentDate(
								formatDateTime(payment.getAdvancePaymentDate(), FUNCTION_DATE_FORMATTER));
						eventFunctionQuotationPaymentResponseDto.setAdvancePayment(payment.getAdvancePayment());
						eventFunctionQuotationPaymentResponseDto
								.setAdvancePaymentNotes(payment.getAdvancePaymentNotes());
						eventFunctionQuotationPaymentResponseDto.setBankId(payment.getBankId());
						eventFunctionQuotationPaymentResponseDto.setCashAccountId(payment.getCashAccountId());
						eventFunctionQuotationPaymentResponseDto.setPaymentMode(payment.getPaymentMode());
						paymentResponseDtos.add(eventFunctionQuotationPaymentResponseDto);
					});

			// Quotation items
			List<EventFunctionQuotationItemsResponseDto> items = new ArrayList<>();
			List<EventFunctionQuotationItemEntity> itemEntities = eventFunctionQuotationItemRepository
					.findAllByEventFunctionQuotationAndIsDeleteFalse(quotation);

			for (EventFunctionQuotationItemEntity item : itemEntities) {
				EventFunctionQuotationItemsResponseDto dto = new EventFunctionQuotationItemsResponseDto();
				dto.setId(item.getId());
				dto.setFunctionName(item.getFunctionName());
				dto.setFunctionDate(formatDateTime(item.getFunctionDate(), dateTimeFormatter));
				dto.setPax(item.getPax());
				dto.setExtraPax(item.getExtraPax());
				dto.setRatePerPlate(item.getRatePerPlate());
				dto.setAmount(item.getAmount());
				dto.setExtraTax(item.getExtraTax());
				dto.setTaxRate(item.getTaxRate());
				dto.setEventFunctionId(item.getEventFunctionId());
				dto.setIsEventFunction(item.getIsEventFunction());
				dto.setDefaultFunctionId(item.getDefaultFunctionId());
				dto.setIsAddons(item.getIsAddons());
				dto.setMenuCatId(item.getMenuCatId());
				dto.setItemId(item.getItemId());
				dto.setIsLocked(item.getIsLocked());
				dto.setIsExtraCharges(false);
				dto.setExtraChargesId(null);
				items.add(dto);
			}

			EventFunctionQuotationResponseDto response = new EventFunctionQuotationResponseDto();
			response.setEvent(eventDto);
			response.setId(quotation.getId());
			response.setFunctionQuotationItems(items);
			response.setTotalAmount(quotation.getTotalAmount());
			response.setCashPayment(quotation.getCashPayment());
			response.setChequePayment(quotation.getChequePayment());
			response.setCgst(quotation.getCgst());
			response.setCgstAmnt(quotation.getCgstAmnt());
			response.setSgst(quotation.getSgst());
			response.setSgstAmnt(quotation.getSgstAmnt());
			response.setIgst(quotation.getIgst());
			response.setIgstAmnt(quotation.getIgstAmnt());
			response.setDiscount(quotation.getDiscount());
			response.setRoundOff(quotation.getRoundOff());
			response.setGrandTotal(quotation.getGrandTotal());
			response.setRemainingAmount(quotation.getRemainingAmount());
			response.setQuotationCode(quotation.getQuotationCode());
			response.setNotes(quotation.getNotes());
			response.setSubTotal(quotation.getSubTotal());
			response.setQuotationCode(quotation.getQuotationCode());
			response.setBillingname(quotation.getBillingname());
			response.setGstnumber(quotation.getGstnumber());
			response.setIsLocked(quotation.getIsLocked());
			response.setTransportation(quotation.getTransportation());
			response.setFoodTax(quotation.getFoodTax());
			response.setFoodTaxAmount(quotation.getFoodTaxAmount());
			response.setFoodTaxTotalAmount(quotation.getFoodTaxTotalAmount());
			response.setServiceTax(quotation.getServiceTax());
			response.setServiceTaxAmount(quotation.getServiceTaxAmount());
			response.setServiceTaxTotalAmount(quotation.getServiceTaxTotalAmount());
			response.setVatTax(quotation.getVatTax());
			response.setVatTaxAmount(quotation.getVatTaxAmount());
			response.setVatTaxTotalAmount(quotation.getVatTaxTotalAmount());
			response.setDiscountPct(quotation.getDiscountPct());
			response.setIsDiscountPercent(quotation.getIsDiscountPercent());
			try {
				response.setDuedate(quotation.getDuedate() != null ? quotation.getDuedate().format(formatter) : "");
				response.setQuotationdate(
						quotation.getQuotationdate() != null ? quotation.getQuotationdate().format(formatter) : "");
			} catch (Exception e) {
				response.setDuedate("");
				response.setQuotationdate("");
			}

			response.setEventFunctionQuotationPayments(paymentResponseDtos);
			response.setCreatedAt(formatDateTime(quotation.getCreatedAt(), FUNCTION_DATE_FORMATTER).split(" ")[0]);

			response.setOverallTotalAmnt(overallTotalAmnt);
			response.setOverAllReceivableAmnt(overAllReceivableAmnt);
			response.setOverAllRemainingAmnt(overAllRemainingAmnt);
			eventFunctionQuotationResponseDto.add(response);
		}

		return eventFunctionQuotationResponseDto;
	}

	public QuotationResponseDto getEventData(Long eventId, Long userid, int lang, Integer isInvoice, Boolean isDecore) {

		QuotationResponseDto dto = new QuotationResponseDto();

		Object result;

		if (isInvoice == 1) {
			result = eventFunctionQuotationPaymentRepository.getEventDataInvoice(eventId, userid, lang);
		} else {
			result = eventFunctionQuotationPaymentRepository.getEventData(eventId, userid, lang, isDecore);
		}

		if (result == null) {
			return null;
		}

		Object[] cmpData = (Object[]) result;

		int index = 0;

		dto.setQuotationId(commonService.getLong(cmpData[index++]));
		dto.setPartyId(commonService.getLong(cmpData[index++]));
		dto.setEventId(commonService.getLong(cmpData[index++]));
		dto.setEventNo(commonService.getString(cmpData[index++]));
		dto.setPartyName(commonService.getString(cmpData[index++]));
		dto.setPartyAddress(commonService.getString(cmpData[index++]));
		dto.setPartyEmail(commonService.getString(cmpData[index++]));
		dto.setPartyMobile(commonService.getString(cmpData[index++]));
		dto.setPartyGst(commonService.getString(cmpData[index++]));
		dto.setPan(commonService.getString(cmpData[index++]));
		dto.setQuotationDueDate(commonService.getString(cmpData[index++]));
		dto.setQuotationCode(commonService.getString(cmpData[index++]));
		dto.setEventDate(commonService.getString(cmpData[index++]));
		dto.setSubTotle(commonService.getDouble(cmpData[index++]));
		dto.setCashPayment(commonService.getDouble(cmpData[index++]));
		dto.setChequePayment(commonService.getDouble(cmpData[index++]));
		dto.setCgst(commonService.getString(cmpData[index++]));
		dto.setCgstAmnt(commonService.getDouble(cmpData[index++]));
		dto.setSgst(commonService.getString(cmpData[index++]));
		dto.setSgstAmnt(commonService.getDouble(cmpData[index++]));
		dto.setIgst(commonService.getString(cmpData[index++]));
		dto.setIgstAmnt(commonService.getDouble(cmpData[index++]));
		dto.setDiscount(commonService.getDouble(cmpData[index++]));
		dto.setAdvancePayment(commonService.getDouble(cmpData[index++]));
		dto.setRemainingAmount(commonService.getDouble(cmpData[index++]));
		dto.setGrandTotal(commonService.getDouble(cmpData[index++]));

		dto.setCompanyName(commonService.getString(cmpData[index++]));
		dto.setCountryCode(commonService.getString(cmpData[index++]));
		dto.setOfficeNo(commonService.getString(cmpData[index++]));
		dto.setCompanyEmail(commonService.getString(cmpData[index++]));
		dto.setCompanyAddress(commonService.getString(cmpData[index++]));
		dto.setCompanyLogo(commonService.getString(cmpData[index++]));

		dto.setAccountHolderName(commonService.getString(cmpData[index++]));
		dto.setAccountNo(commonService.getString(cmpData[index++]));
		dto.setBankName(commonService.getString(cmpData[index++]));
		dto.setIfscCode(commonService.getString(cmpData[index++]));
		dto.setUpiId(commonService.getString(cmpData[index++]));
		dto.setBranchName(commonService.getString(cmpData[index++]));
		dto.setQrCodePath(commonService.getString(cmpData[index++]));

		dto.setNotes(commonService.getString(cmpData[index++]));
		dto.setInquiryDate(commonService.getString(cmpData[index++]));

		Long banquetHallId = commonService.getLong(cmpData[index++]);
		dto.setBanquet_hall_id(banquetHallId != null ? banquetHallId : 0);

		dto.setIronService(commonService.getString(cmpData[index++]));
		dto.setPoojaRoom(commonService.getString(cmpData[index++]));
		dto.setVenueRemark(commonService.getString(cmpData[index++]));
		dto.setVenueTotal(commonService.getInteger(cmpData[index++]));
		dto.setIsQuotationLocked(commonService.getBoolean(cmpData[index++]));

		dto.setEventName(commonService.getString(cmpData[index++]));

		String venue = commonService.getString(cmpData[index++]);
		String banquetVenue = commonService.getString(cmpData[index++]);

		if (banquetHallId != null && banquetHallId != 0) {
			dto.setVenueName(banquetVenue != null ? banquetVenue : "");
		} else {
			dto.setVenueName(venue != null ? venue : "");
		}

		dto.setCmpGstNumber(commonService.getString(cmpData[index++]));

		Object generatedDateTime = cmpData[index++];

		if (generatedDateTime instanceof Timestamp) {
			dto.setGeneratedDateTime(((Timestamp) generatedDateTime).toLocalDateTime());
		} else if (generatedDateTime instanceof LocalDateTime) {
			dto.setGeneratedDateTime((LocalDateTime) generatedDateTime);
		}

		// Invoice only
		if (isInvoice == 1) {
			dto.setShipAddress(commonService.getString(cmpData[index++]));
		}

		dto.setCmpPanNumber(commonService.getString(cmpData[index++]));

		// Always consume transportation column to keep indexes aligned
		Integer transportation = commonService.getInteger(cmpData[index++]);

		if (isInvoice != 1) {
			dto.setTransportation(transportation);
		}

		dto.setFoodTax(commonService.getString(cmpData[index++]));
		dto.setFoodTaxAmount(commonService.getInteger(cmpData[index++]));
		dto.setFoodTaxTotalAmount(commonService.getInteger(cmpData[index++]));

		dto.setServiceTax(commonService.getString(cmpData[index++]));
		dto.setServiceTaxAmount(commonService.getInteger(cmpData[index++]));
		dto.setServiceTaxTotalAmount(commonService.getInteger(cmpData[index++]));

		dto.setVatTax(commonService.getString(cmpData[index++]));
		dto.setVatTaxAmount(commonService.getInteger(cmpData[index++]));
		dto.setVatTaxTotalAmount(commonService.getInteger(cmpData[index++]));

		dto.setFssaiNumber(commonService.getString(cmpData[index++]));
		dto.setHsnNumber(commonService.getString(cmpData[index++]));
		dto.setCinNumber(commonService.getString(cmpData[index++]));
		dto.setFdaLincense(commonService.getString(cmpData[index++]));

		dto.setDiscountPercent(commonService.getString(cmpData[index++]));

		dto.setPrefix(commonService.getString(cmpData[index++]));
		
		dto.setEventVenue(commonService.getString(cmpData[index++]));
		
		return dto;
	}

	public List<QuotationResponseDto> getFunctionData(Long quotationId) {
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		List<QuotationResponseDto> responseDtos = new ArrayList<>();

		List<Object[]> result = eventFunctionQuotationPaymentRepository.getFunData(quotationId);
		for (Object[] row : result) {
			QuotationResponseDto dto = new QuotationResponseDto();
			int index = 0;
			dto.setFunctionName(commonService.getString(row[index++]));
			dto.setFunctionPax(commonService.getInteger(row[index++]));
			dto.setFunctionExtraPax(commonService.getInteger(row[index++]));
			dto.setRate(commonService.getDouble(row[index++]));
			dto.setAmount(commonService.getDouble(row[index++]));
			dto.setExtraTax(commonService.getBigDecimal(row[index++]));
			dto.setTaxRate(commonService.getBigDecimal(row[index++]));

			Object functionDate = row[index++];
			if (functionDate != null) {
				dto.setFunctionDate(convertToLocalDate(functionDate, dateFormatter));
			}
			dto.setOptions(commonService.getString(row[index++]));
			if (functionDate != null) {
				dto.setFunctionTime(convertToTime(functionDate.toString()));
			}
			dto.setIs_event_function(commonService.getBoolean(row[index++]));
			dto.setEventFunctionId(commonService.getLong(row[index++]));
			dto.setIsItemLocked(commonService.getBoolean(row[index++]));

			dto.setIsAddons(commonService.getBoolean(row[index++]));
			dto.setOfferedRate(commonService.getDouble(row[index++]));
			dto.setDefaultFunctionId(commonService.getLong(row[index++]));

			System.out.println("dto.getEventFunctionId():- " + dto.getEventFunctionId());
			if (dto.getEventFunctionId() != null && dto.getEventFunctionId() != 0) {
				String functionVenue = "";
				EventFunctionMasterEntity functionMasterEntity = eventFunctionMasterRepository
						.findByIdAndIsDeleteFalse(dto.getEventFunctionId())
						.orElseThrow(() -> new RuntimeException("Event Function Not Found"));

				List<BanquetHallShiftInfoDto> banquets = banquetHallShiftBookingRepository.findBanquetByEventFunctionId(
						functionMasterEntity.getEvent().getId(), functionMasterEntity.getId());

				if (!banquets.isEmpty()) {
					functionVenue = banquets.stream().map(BanquetHallShiftInfoDto::getBanquetHallName)
							.collect(Collectors.joining(", "));
				} else {
					functionVenue = functionMasterEntity.getFunction_venue();

				}
				dto.setVenueName(functionVenue);
			}

			responseDtos.add(dto);
		}

		return responseDtos;
	}

	public List<QuotationResponseDto> getFunctionData1(Long quotationId) {
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		List<QuotationResponseDto> responseDtos = new ArrayList<>();

		List<Object[]> result = eventFunctionQuotationPaymentRepository.getFunData1(quotationId);

		for (Object[] row : result) {
			QuotationResponseDto dto = new QuotationResponseDto();
			int index = 0;
			dto.setFunctionName(commonService.getString(row[index++]));
			dto.setFunctionPax(commonService.getInteger(row[index++]));
			dto.setFunctionExtraPax(commonService.getInteger(row[index++]));
			dto.setRate(commonService.getDouble(row[index++]));
			dto.setAmount(commonService.getDouble(row[index++]));
			dto.setExtraTax(commonService.getBigDecimal(row[index++]));
			dto.setTaxRate(commonService.getBigDecimal(row[index++]));

			Object functionDate = row[index++];
			if (functionDate != null) {
				dto.setFunctionDate(convertToLocalDate(functionDate, dateFormatter));
			}
			dto.setOptions(commonService.getString(row[index++]));
			if (functionDate != null) {
				dto.setFunctionTime(convertToTime(functionDate.toString()));
			}
			dto.setIs_event_function(commonService.getBoolean(row[index++]));
			dto.setEventFunctionId(commonService.getLong(row[index++]));
			dto.setIsItemLocked(commonService.getBoolean(row[index++]));

			dto.setIsAddons(commonService.getBoolean(row[index++]));

			responseDtos.add(dto);
		}

		return responseDtos;
	}

	public List<QuotationResponseDto> getFunctionDataInvoice(Long invoiceId) {
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		List<QuotationResponseDto> responseDtos = new ArrayList<>();

		List<Object[]> result = eventFunctionQuotationPaymentRepository.getFunDataInvoice(invoiceId);

		for (Object[] row : result) {
			QuotationResponseDto dto = new QuotationResponseDto();
			int index = 0;
			dto.setFunctionName(commonService.getString(row[index++]));
			dto.setFunctionPax(commonService.getInteger(row[index++]));
			dto.setRate(commonService.getDouble(row[index++]));
			dto.setAmount(commonService.getDouble(row[index++]));
			dto.setFunctionExtraPax(commonService.getInteger(row[index++]));
			Object functionDate = row[index++];
			if (functionDate != null) {
				dto.setFunctionDate(convertToLocalDate(functionDate, dateFormatter));
			}
			dto.setIs_event_function(commonService.getBoolean(row[index++]));
			dto.setOfferedRate(commonService.getDouble(row[index++]));
			responseDtos.add(dto);
		}

		return responseDtos;
	}

	private String convertToLocalDate(Object dateObj, DateTimeFormatter dateFormat) {

		if (dateObj == null) {
			return null;
		}

		if (dateObj instanceof java.sql.Date) {
			return ((java.sql.Date) dateObj).toLocalDate().format(dateFormat);
		}

		if (dateObj instanceof java.sql.Timestamp) {
			return ((java.sql.Timestamp) dateObj).toLocalDateTime().toLocalDate().format(dateFormat);
		}

		if (dateObj instanceof LocalDate) {
			return ((LocalDate) dateObj).format(dateFormat);
		}

		throw new IllegalArgumentException("Unsupported date type: " + dateObj.getClass().getName());
	}

	private String convertToTime(String dateTimeStr) {
		DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");

		DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("hh:mm a");

		return LocalDateTime.parse(dateTimeStr, inputFormatter).format(outputFormatter);
	}

	@Override
	public List<EventFunctionQuotationItemsResponseDto> getDefaultExtraFunction(Long quotationId, Boolean isOn) {

		EventFunctionQuotationEntity quotationEntity = eventFunctionQuotationRepository
				.findByIdAndIsDeleteFalse(quotationId);
		if (quotationEntity == null) {
			throw new RuntimeException("EventFunctionQuotation with id " + quotationId + " not found");
		}
		if (isOn) {
			List<EventFunctionQuotationItemEntity> dbItems = syncDefaultFunctions(quotationEntity.getEvent(),
					quotationEntity, new ArrayList<>());

			List<EventFunctionQuotationItemsResponseDto> items = mapItems(dbItems);
			return items;
		} else {
			eventFunctionQuotationItemRepository.deleteExtraFunction(quotationEntity.getId());
			return Collections.emptyList();
		}
	}

	@Override
	public Boolean lockQuotation(Long quotationId, Boolean isLock) {
		EventFunctionQuotationEntity quotationEntity = eventFunctionQuotationRepository
				.findByIdAndIsDeleteFalse(quotationId);

		List<EventFunctionQuotationItemEntity> quotationItems = eventFunctionQuotationItemRepository
				.findAllByEventFunctionQuotationAndIsDeleteFalse(quotationEntity);

		quotationEntity.setIsLocked(isLock);
		quotationEntity.setUpdatedAt(LocalDateTime.now());
		eventFunctionQuotationRepository.save(quotationEntity);

		quotationItems.stream().forEach(item -> item.setIsLocked(isLock));

		eventFunctionQuotationItemRepository.saveAll(quotationItems);

		return true;
	}

	@Override
	public String quotationExcel(Long userId, String startDate, String endDate, HttpServletRequest request,
			Boolean isVenue, Long id, Boolean isDecore) {

		try {

			UserMasterEntity userMaster = userMasterRepository.findByIdAndIsDeleteFalse(userId)
					.orElseThrow(() -> new RuntimeException("User not found"));

			List<EventFunctionQuotationEntity> allQuotations;

			if ((startDate == null || startDate.trim().isEmpty()) && (endDate == null || endDate.trim().isEmpty())) {

				if (isVenue == null) {

					allQuotations = eventFunctionQuotationRepository
							.findAllByUserAndIsDeleteFalseAndEvent_StatusAndIsDecore(userMaster, 1, isDecore);

				} else if (Boolean.TRUE.equals(isVenue)) {

					allQuotations = eventFunctionQuotationRepository
							.findAllByUserAndIsDeleteFalseAndEvent_StatusAndEvent_Venue_IdAndIsDecore(userMaster, 1, id,
									isDecore);

				} else {

					allQuotations = eventFunctionQuotationRepository
							.findAllByUserAndIsDeleteFalseAndEvent_StatusAndEvent_BanquetHall_IdAndIsDecore(userMaster,
									1, id, isDecore);
				}

			} else {

				DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				LocalDateTime start = LocalDate.parse(startDate, dateFormatter).atStartOfDay();
				LocalDateTime end = LocalDate.parse(endDate, dateFormatter).atTime(23, 59, 59);

				if (isVenue == null) {

					allQuotations = eventFunctionQuotationRepository
							.findAllByUserAndIsDeleteFalseAndEvent_StatusAndEvent_EventStartDateTimeBetweenAndIsDecore(
									userMaster, 1, start, end, isDecore);

				} else if (Boolean.TRUE.equals(isVenue)) {

					allQuotations = eventFunctionQuotationRepository
							.findAllByUserAndIsDeleteFalseAndEvent_StatusAndEvent_EventStartDateTimeBetweenAndEvent_Venue_IdAndIsDecore(
									userMaster, 1, start, end, id, isDecore);

				} else {

					allQuotations = eventFunctionQuotationRepository
							.findAllByUserAndIsDeleteFalseAndEvent_StatusAndEvent_EventStartDateTimeBetweenAndEvent_BanquetHall_IdAndIsDecore(
									userMaster, 1, start, end, id, isDecore);
				}
			}

			XSSFWorkbook workbook = new XSSFWorkbook();
			XSSFSheet sheet = workbook.createSheet("Quotation Report");

			int rowNum = 0;

			// Title
			Row titleRow = sheet.createRow(rowNum++);
			titleRow.createCell(0).setCellValue("Quotation Report");
			sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 7));

			// Summary Row Header
			Row summaryHeader = sheet.createRow(rowNum++);
			summaryHeader.createCell(0).setCellValue("Total Outstanding Receivable");
			summaryHeader.createCell(3).setCellValue("Total Remaining");
			summaryHeader.createCell(6).setCellValue("Total Amount");

			BigInteger overallTotalAmnt = BigInteger.ZERO;
			BigInteger overAllReceivableAmnt = BigInteger.ZERO;
			BigInteger overAllRemainingAmnt = BigInteger.ZERO;

			for (EventFunctionQuotationEntity quotation : allQuotations) {

				overallTotalAmnt = overallTotalAmnt
						.add(quotation.getGrandTotal() != null ? quotation.getGrandTotal() : BigInteger.ZERO);
				overAllReceivableAmnt = overAllReceivableAmnt
						.add(quotation.getTotalAmount() != null ? quotation.getTotalAmount().toBigInteger()
								: BigInteger.ZERO);
				overAllRemainingAmnt = overAllRemainingAmnt
						.add(quotation.getRemainingAmount() != null ? quotation.getRemainingAmount() : BigInteger.ZERO);
			}

			// Summary Values
			Row summaryValue = sheet.createRow(rowNum++);
			summaryValue.createCell(0).setCellValue(overallTotalAmnt.toString());
			summaryValue.createCell(3).setCellValue(overAllRemainingAmnt.toString());
			summaryValue.createCell(6).setCellValue(overAllReceivableAmnt.toString());

			rowNum++; // Empty row

			// Table Header
			Row headerRow = sheet.createRow(rowNum++);

			String[] headers = { "Sr No#", "Customer Name", "Event Name", "Event Date", "Quotation Date", "Total Paid",
					"Balance Due", "Total Amount" };

			for (int i = 0; i < headers.length; i++) {
				headerRow.createCell(i).setCellValue(headers[i]);
			}

			int srNo = 1;

			DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");

			for (EventFunctionQuotationEntity quotation : allQuotations) {
				boolean allZero = quotation.getTotalAmount() != null && quotation.getRemainingAmount() != null
						&& quotation.getGrandTotal() != null
						&& quotation.getTotalAmount().compareTo(BigDecimal.ZERO) == 0
						&& quotation.getRemainingAmount().compareTo(BigInteger.ZERO) == 0
						&& quotation.getGrandTotal().compareTo(BigInteger.ZERO) == 0;

				Row row = sheet.createRow(rowNum++);

				row.createCell(0).setCellValue(srNo++);

				row.createCell(1)
						.setCellValue(quotation.getEvent().getParty().getNameEnglish() != null
								? quotation.getEvent().getParty().getNameEnglish()
								: "");

				row.createCell(2)
						.setCellValue(quotation.getEvent().getEventType().getNameEnglish() != null
								? quotation.getEvent().getEventType().getNameEnglish()
								: "");

				row.createCell(3)
						.setCellValue(quotation.getEvent().getEventStartDateTime() != null
								? quotation.getEvent().getEventStartDateTime().format(dateFormat)
								: "");

				row.createCell(4).setCellValue(
						quotation.getCreatedAt() != null ? quotation.getCreatedAt().format(dateFormat) : "");

				row.createCell(5).setCellValue(
						quotation.getTotalAmount() != null ? quotation.getTotalAmount().doubleValue() : 0);

				row.createCell(6).setCellValue(
						quotation.getRemainingAmount() != null ? quotation.getRemainingAmount().doubleValue() : 0);

				row.createCell(7)
						.setCellValue(quotation.getGrandTotal() != null ? quotation.getGrandTotal().doubleValue() : 0);
			}

			// Auto Size
			for (int i = 0; i < headers.length; i++) {
				sheet.autoSizeColumn(i);
			}

			// Save File
			String rootPath = request.getSession().getServletContext().getRealPath("/");
			String folderName = String.valueOf(userId);

			File dir = new File(rootPath + "resources/tempDownload/" + folderName + "/");

			if (!dir.exists()) {
				dir.mkdirs();
			}

			String fileName = "quotation-report-" + System.currentTimeMillis() + ".xlsx";

			File file = new File(dir, fileName);

			try (FileOutputStream fos = new FileOutputStream(file)) {
				workbook.write(fos);
			}

			workbook.close();

			return environment.getProperty("ws_image_path") + "/api/download/excel/" + folderName + "/" + fileName;

		} catch (Exception e) {
			throw new RuntimeException("Error generating quotation excel", e);
		}
	}

	@Override
	public Boolean deleteQuotationPayment(Long id) {

		EventFunctionQuotationPaymentEntity entity = eventFunctionQuotationPaymentRepository
				.findByIdAndIsDeleteFalse(id);
		if (entity != null) {
			entity.setIsDelete(true);
			eventFunctionQuotationPaymentRepository.save(entity);
			return true;
		}
		return false;
	}
}
