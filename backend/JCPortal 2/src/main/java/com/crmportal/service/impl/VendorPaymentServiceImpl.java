package com.crmportal.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.crmportal.entity.AccountContactEntity;
import com.crmportal.entity.BankDetailsEntity;
import com.crmportal.entity.CashAccountEntity;
import com.crmportal.entity.EventMasterEntity;
import com.crmportal.entity.PartyMasterEntity;
import com.crmportal.entity.UserBasicDetailsMasterEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.entity.VendorPaymentEntity;
import com.crmportal.repository.AccountContactRepository;
import com.crmportal.repository.AccountEntryRepository;
import com.crmportal.repository.BankDetailsRepository;
import com.crmportal.repository.CashAccountRepository;
import com.crmportal.repository.EventLaborRepository;
import com.crmportal.repository.EventMasterRepository;
import com.crmportal.repository.EvetFunctionMenuAllocationOrderRepository;
import com.crmportal.repository.PartyMasterRepository;
import com.crmportal.repository.PurchaseOrderRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.repository.VendorPaymentRepository;
import com.crmportal.request.dto.UpdateVendorPaymentRequestDto;
import com.crmportal.request.dto.VendorKey;
import com.crmportal.request.dto.VendorPaymentEventRequestDto;
import com.crmportal.request.dto.VendorPaymentRequestDto;
import com.crmportal.response.dto.AccountLadgerPartyResponseDto;
import com.crmportal.response.dto.AccountLadgerResponseDto;
import com.crmportal.response.dto.AccountLedgerFinalResponseDto;
import com.crmportal.response.dto.AccountLedgerSummaryDto;
import com.crmportal.response.dto.AllVendorsPaymentResponseDto;
import com.crmportal.response.dto.VendorPartyWiseEventResponseDto;
import com.crmportal.response.dto.VendorPartyWiseFinalEventResponseDto;
import com.crmportal.response.dto.VendorPaymentInvoiceResponseDto;
import com.crmportal.response.dto.VendorPaymentResponseDto;
import com.crmportal.service.CommonService;
import com.crmportal.service.VendorPaymentService;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;

@Service
public class VendorPaymentServiceImpl implements VendorPaymentService {

	@Autowired
	VendorPaymentRepository vendorPaymentRepository;

	@Autowired
	BankDetailsRepository bankDetailsRepository;

	@Autowired
	CommonService commonService;

	@Autowired
	EventMasterRepository eventMasterRepository;

	@Autowired
	PartyMasterRepository partyMasterRepository;

	@Autowired
	UserMasterRepository userMasterRepository;

	@Autowired
	EvetFunctionMenuAllocationOrderRepository allocationOrderRepository;

	@Autowired
	EventLaborRepository eventLaborRepository;

	@Autowired
	PurchaseOrderRepository purchaseOrderRepository;

	@Autowired
	CashAccountRepository cashAccountRepository;

	@Autowired
	AccountContactRepository accountContactRepository;

	@Autowired
	Environment environment;

	@Autowired
	AccountEntryRepository accountEntryRepository;

	@Autowired
	MenuPreparationServiceImpl menuPreparationServiceImpl;

	@Autowired
	RawMaterialReportServiceImpl rawMaterialReportServiceImpl;

	@Override
	public VendorPaymentResponseDto getAllVendorPaymentByEventId(Long eventId, Boolean isLabour) {
		List<AllVendorsPaymentResponseDto> vendors = new ArrayList<>();
		if (!isLabour) {
			vendors = vendorPaymentRepository.getVendorPaymentsByEvent(eventId);
		} else {
			vendors = vendorPaymentRepository.getLabourVendorPaymentsByEvent(eventId);
		}

		BigDecimal totalAmt = vendors.stream().map(AllVendorsPaymentResponseDto::getTotalAmt).reduce(BigDecimal.ZERO,
				BigDecimal::add);

		BigDecimal paidAmt = vendors.stream().map(AllVendorsPaymentResponseDto::getPaidAmt).reduce(BigDecimal.ZERO,
				BigDecimal::add);

		BigDecimal pendingAmt = vendors.stream().map(AllVendorsPaymentResponseDto::getPendingAmt)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		String eventName = eventMasterRepository.getEventNameByEventId(eventId);

		VendorPaymentResponseDto response = new VendorPaymentResponseDto();
		response.setTotalAmt(totalAmt);
		response.setPaidAmt(paidAmt);
		response.setPendingAmt(pendingAmt);
		response.setVendorsPayment(vendors);
		response.setEventId(eventId);
		response.setEventName(eventName);
		return response;
	}

	@Override
	public Map<String, Object> getVendorPaymentByEventIdAndVendorId(Long eventId, Long vendorId, Long userId,
			Boolean isPayable, String vName) {

		UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(userId)
				.orElseThrow(() -> new RuntimeException("User not found"));

		List<VendorPaymentEntity> entities;

		boolean validEventId = eventId != null && eventId != -1;
		boolean validVendorId = vendorId != null && vendorId != -1;

		if (validEventId && validVendorId) {

			entities = vendorPaymentRepository.findAllByEventIdAndVendorIdAndIsDeleteFalse(eventId, vendorId);

		} else if (validVendorId) {

			entities = vendorPaymentRepository.findAllByVendorIdAndIsDeleteFalse(vendorId);

		} else if (validEventId) {

			entities = vendorPaymentRepository.findAllByEventIdAndIsDeleteFalse(eventId);

		} else {

			entities = vendorPaymentRepository.findAllByUserAndIsDeleteFalse(user);
		}


		if (entities == null || entities.isEmpty()) {

			Map<String, Object> response = new HashMap<String, Object>();

			response.put("payments", Collections.<VendorPaymentInvoiceResponseDto>emptyList());

			response.put("totalPayable", BigDecimal.ZERO);
			response.put("remainingPayable", BigDecimal.ZERO);
			response.put("totalReceived", BigDecimal.ZERO);
			response.put("remainingBalanced", BigDecimal.ZERO);

			return response;
		}

		Set<Long> bankIds = entities.stream().filter(Objects::nonNull)
				.filter(entity -> entity.getPaymentMode() != null
						&& !"CASH".equalsIgnoreCase(entity.getPaymentMode().trim()))
				.map(VendorPaymentEntity::getBankId).filter(Objects::nonNull).collect(Collectors.toSet());


		Set<Long> cashIds = entities.stream().filter(Objects::nonNull)
				.filter(entity -> "CASH"
						.equalsIgnoreCase(Optional.ofNullable(entity.getPaymentMode()).orElse("").trim()))
				.map(VendorPaymentEntity::getCashAccountId).filter(Objects::nonNull).collect(Collectors.toSet());


		Map<VendorKey, List<VendorPaymentEntity>> groupedVendors = entities.stream().filter(Objects::nonNull)
				.filter(entity -> entity.getVendorId() != null)
				.collect(Collectors.groupingBy(entity -> new VendorKey(entity.getVendorId(), entity.getVendorCat())));


		Set<Long> partyVendorIds = groupedVendors.keySet().stream()
				.filter(key -> !"AccountContact".equalsIgnoreCase(key.getVendorCat())).map(VendorKey::getVendorId)
				.filter(Objects::nonNull).collect(Collectors.toSet());


		Set<Long> accountContactIds = groupedVendors.keySet().stream()
				.filter(key -> "AccountContact".equalsIgnoreCase(key.getVendorCat())).map(VendorKey::getVendorId)
				.filter(Objects::nonNull).collect(Collectors.toSet());


		Map<Long, String> bankMap = new HashMap<Long, String>();

		if (!bankIds.isEmpty()) {

			bankMap = bankDetailsRepository.findAllByIdInAndIsDeleteFalse(bankIds).stream().filter(Objects::nonNull)
					.filter(bank -> bank.getId() != null)
					.collect(Collectors.toMap(BankDetailsEntity::getId,
							bank -> Optional.ofNullable(bank.getBankName()).orElse("-"),
							(existing, replacement) -> existing));
		}


		Map<Long, String> cashMap = new HashMap<Long, String>();

		if (!cashIds.isEmpty()) {

			cashMap = cashAccountRepository.findAllByIdInAndIsDeleteFalse(cashIds).stream().filter(Objects::nonNull)
					.filter(cash -> cash.getId() != null)
					.collect(Collectors.toMap(CashAccountEntity::getId,
							cash -> Optional.ofNullable(cash.getAccountName()).orElse("-"),
							(existing, replacement) -> existing));
		}


		Map<Long, String> partyVendorMap = new HashMap<Long, String>();

		if (!partyVendorIds.isEmpty()) {

			partyVendorMap = partyMasterRepository.findAllByIdInAndIsDeleteFalse(partyVendorIds).stream()
					.filter(Objects::nonNull).filter(party -> party.getId() != null)
					.collect(Collectors.toMap(PartyMasterEntity::getId,
							party -> Optional.ofNullable(party.getNameEnglish()).orElse("-"),
							(existing, replacement) -> existing));
		}

		Map<Long, String> accountContactMap = new HashMap<Long, String>();

		if (!accountContactIds.isEmpty()) {

			accountContactMap = accountContactRepository.findAllByIdInAndIsDeleteFalse(accountContactIds).stream()
					.filter(Objects::nonNull).filter(contact -> contact.getId() != null)
					.collect(Collectors.toMap(AccountContactEntity::getId,
							contact -> Optional.ofNullable(contact.getName()).orElse("-"),
							(existing, replacement) -> existing));
		}

		Map<VendorKey, String> vendorMap = new HashMap<VendorKey, String>();

		for (VendorKey key : groupedVendors.keySet()) {

			String vendorName;

			if ("AccountContact".equalsIgnoreCase(key.getVendorCat())) {

				vendorName = accountContactMap.get(key.getVendorId());

			} else {

				vendorName = partyVendorMap.get(key.getVendorId());
			}

			if (vendorName != null) {
				vendorMap.put(key, vendorName);
			}
		}

		Set<Long> eventIds = entities.stream().filter(Objects::nonNull).map(VendorPaymentEntity::getEventId)
				.filter(Objects::nonNull).collect(Collectors.toSet());


		Map<Long, String> eventMap = new HashMap<Long, String>();

		for (Long id : eventIds) {

			String eventName = eventMasterRepository.getEventNameByEventId(id);

			if (eventName != null) {
				eventMap.put(id, eventName);
			}
		}


		BigDecimal totalPayable = BigDecimal.ZERO;
		BigDecimal totalReceived = BigDecimal.ZERO;

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		List<VendorPaymentInvoiceResponseDto> dtos = new ArrayList<VendorPaymentInvoiceResponseDto>();

		String vendorNameFilter = vName != null ? vName.trim() : "";


		for (VendorPaymentEntity entity : entities) {

			if (entity == null) {
				continue;
			}

			VendorKey key = new VendorKey(entity.getVendorId(), entity.getVendorCat());

			String vendorName = vendorMap.get(key);

			if (!vendorNameFilter.isEmpty()) {

				if (vendorName == null
						|| !vendorName.toLowerCase(Locale.ROOT).contains(vendorNameFilter.toLowerCase(Locale.ROOT))) {

					continue;
				}
			}

			if (isPayable != null && !Objects.equals(entity.getIsPayable(), isPayable)) {

				continue;
			}

			BigDecimal payAmount = Optional.ofNullable(entity.getPayAmount()).orElse(BigDecimal.ZERO);

			BigDecimal receivedAmount = Optional.ofNullable(entity.getReceivedAmount()).orElse(BigDecimal.ZERO);

			if (Boolean.TRUE.equals(entity.getIsPayable())) {

				totalPayable = totalPayable.add(payAmount);

			} else if (Boolean.FALSE.equals(entity.getIsPayable())) {

				totalReceived = totalReceived.add(receivedAmount);
			}

			VendorPaymentInvoiceResponseDto dto = new VendorPaymentInvoiceResponseDto();

			dto.setId(entity.getId());

			dto.setDate(entity.getPaymentDate() != null ? entity.getPaymentDate().format(formatter) : null);

			dto.setInvoiceCode(entity.getInvoiceCode());

			dto.setPayMode(entity.getPaymentMode());

			dto.setReferenceId(entity.getReferenceId());

			dto.setBankId(entity.getBankId());

			dto.setBankName(bankMap.getOrDefault(entity.getBankId(), "-"));

			dto.setVendorName(vendorName);

			dto.setPayAmount(payAmount);

			dto.setReceivedAmount(receivedAmount);

			dto.setRemarks(entity.getRemarks());

			dto.setVendorId(entity.getVendorId());

			dto.setIsPayable(entity.getIsPayable());

			dto.setSettlementAmount(entity.getSettlementAmount());

			dto.setEventId(entity.getEventId());

			dto.setIsOpb(entity.getIsOpb());

			dto.setEventName(eventMap.getOrDefault(entity.getEventId(), "-"));

			dto.setCashId(entity.getCashAccountId());

			dto.setCashName(cashMap.getOrDefault(entity.getCashAccountId(), "-"));

			dtos.add(dto);
		}


		BigDecimal totalAmount = BigDecimal.ZERO;

		BigDecimal remainingPayable = BigDecimal.ZERO;

		BigDecimal remainingBalanced = BigDecimal.ZERO;

		if (Boolean.TRUE.equals(isPayable)) {

			totalAmount = Optional.ofNullable(vendorPaymentRepository.getTotalPayableAmountByUser(userId))
					.orElse(BigDecimal.ZERO);

			remainingPayable = totalAmount.subtract(totalPayable);

		} else if (Boolean.FALSE.equals(isPayable)) {

			totalAmount = Optional.ofNullable(vendorPaymentRepository.getTotalReceiveAmountByUser(userId))
					 .map(BigDecimal::new)
				        .orElse(BigDecimal.ZERO);

			remainingBalanced = totalAmount.subtract(totalReceived);
		}


		Map<String, Object> response = new HashMap<String, Object>();

		System.out.println("Total Amount:- " + totalAmount);
		
		System.out.println("TotalPayable:- " + totalPayable);
		System.out.println("RemainingPayable:- " + remainingPayable);
		
		System.out.println("TotalReceived:- " + totalReceived);
		System.out.println("RemainingBalanced:- " + remainingBalanced);
		
		
		response.put("payments", dtos);
		response.put("totalPayable", totalPayable);
		response.put("remainingPayable", remainingPayable);
		response.put("totalReceived", totalReceived);
		response.put("remainingBalanced", remainingBalanced);
		

		return response;

	}

	@Transactional
	@Override
	public Boolean addOrUpdateVendorPaymentInvoice(VendorPaymentRequestDto request) {

		UserMasterEntity userMasterEntity = userMasterRepository.findByIdAndIsDeleteFalse(request.getUserId())
				.orElseThrow(() -> new RuntimeException("User Not found With id:-" + request.getUserId()));

		BankDetailsEntity bankDetailsEntity = null;
		if (request.getBankId() != null) {
			bankDetailsEntity = bankDetailsRepository.findByIdAndIsDeleteFalse(request.getBankId())
					.orElseThrow(() -> new RuntimeException("Bank Details Not Found with id:- " + request.getBankId()));
		}

		CashAccountEntity cashAccountEntity = null;
		if (request.getCashId() != null) {
			cashAccountEntity = cashAccountRepository.findByIdAndIsDeleteFalse(request.getCashId())
					.orElseThrow(() -> new RuntimeException("Cash Account not found with id : " + request.getCashId()));
		}

		PartyMasterEntity partyMasterEntity = null;
		AccountContactEntity accountContactEntity = null;
		if (request.getVendorCat().equalsIgnoreCase("AccountContact")) {
			accountContactEntity = accountContactRepository.findByIdAndIsDeleteFalse(request.getVendorId()).orElseThrow(
					() -> new RuntimeException("Account contact Not Found with id:- " + request.getVendorId()));

		} else {
			partyMasterEntity = partyMasterRepository.findByIdAndIsDeleteFalse(request.getVendorId())
					.orElseThrow(() -> new RuntimeException("Vendor Not Found with id:- " + request.getVendorId()));
		}

		String vendorCode = commonService.generateVendorInvoiceCode(request.getUserId());

		// EVENT PAYMENT
		if (request.getVendorEventsPayment() != null && !request.getVendorEventsPayment().isEmpty()) {

			for (VendorPaymentEventRequestDto eventDto : request.getVendorEventsPayment()) {

				VendorPaymentEntity entity = new VendorPaymentEntity();

				entity.setInvoiceCode(vendorCode);

				EventMasterEntity eventMasterEntity = null;

				if (eventDto.getEventId() != null && eventDto.getEventId() != 0 && eventDto.getEventId() != -1) {
					eventMasterEntity = eventMasterRepository.findByIdAndIsDeleteFalse(eventDto.getEventId())
							.orElseThrow(() -> new RuntimeException(
									"Event Master Not Found with id:- " + eventDto.getEventId()));
				}

				entity.setEventId(eventMasterEntity != null ? eventMasterEntity.getId() : null);

				entity.setReceivedAmount(eventDto.getReceivedAmount());
				entity.setSettlementAmount(eventDto.getSettlementAmount());
				entity.setPayAmount(eventDto.getPayAmount());

				setCommonFields(entity, request, userMasterEntity, bankDetailsEntity, partyMasterEntity,
						cashAccountEntity, accountContactEntity);

				vendorPaymentRepository.save(entity);
				if (accountContactEntity != null) {
					BigDecimal currentBalance = accountContactEntity.getCurrentBalance()
							.subtract(eventDto.getReceivedAmount()).subtract(eventDto.getPayAmount());
					accountContactEntity.setCurrentBalance(currentBalance);

					accountContactRepository.save(accountContactEntity);
				}
			}

			updateAccountBalance(bankDetailsEntity, cashAccountEntity, request.getTotalReceivedAmount(),
					request.getTotalPayAmount());

			return true;
		}

		// OPB / PURCHASE / NORMAL PAYMENT
		else {

			VendorPaymentEntity entity = new VendorPaymentEntity();

			entity.setInvoiceCode(vendorCode);
			entity.setReceivedAmount(request.getTotalReceivedAmount());
			entity.setSettlementAmount(request.getTotalSettlementAmount());
			entity.setPayAmount(request.getTotalPayAmount());
			entity.setEventId(null);

			setCommonFields(entity, request, userMasterEntity, bankDetailsEntity, partyMasterEntity, cashAccountEntity,
					accountContactEntity);

			/*
			 * 
			 * if (Boolean.TRUE.equals(request.getIsOpb()) &&
			 * !Boolean.TRUE.equals(request.getIsPurchase())) {
			 * 
			 * partyMasterEntity.setOpb( partyMasterEntity.getOpb().subtract(
			 * request.getTotalPayAmount() .add(request.getTotalSettlementAmount())));
			 * 
			 * partyMasterRepository.save(partyMasterEntity); }
			 */

			updateAccountBalance(bankDetailsEntity, cashAccountEntity, request.getTotalReceivedAmount(),
					request.getTotalPayAmount());

			vendorPaymentRepository.save(entity);

			return true;
		}
	}

	private void updateAccountBalance(BankDetailsEntity bankDetailsEntity, CashAccountEntity cashAccountEntity,
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

	private void setCommonFields(VendorPaymentEntity entity, VendorPaymentRequestDto request, UserMasterEntity user,
			BankDetailsEntity bank, PartyMasterEntity vendor, CashAccountEntity cashAccountEntity,
			AccountContactEntity accountContactEntity) {

		entity.setBankId(bank == null ? null : bank.getId());
		entity.setCashAccountId(cashAccountEntity == null ? null : cashAccountEntity.getId());
		entity.setPaymentDate(commonService.dateFormatted(request.getPaymentDate()));
		entity.setPaymentMode(request.getPaymentMode());
		entity.setReferenceId(request.getReferenceId());
		entity.setUser(user);
		entity.setVendorCat(request.getVendorCat());
		entity.setRemarks(request.getRemarks());
		entity.setIsPayable(request.getIsPayable());
		entity.setIsOpb(request.getIsOpb());
		if (request.getVendorCat().equalsIgnoreCase("AccountContact")) {
			entity.setVendorId(accountContactEntity.getId());
		} else {
			entity.setVendorId(vendor.getId());
		}
	}

	@Override
	public Boolean deleteEventVendorPaymentInvoiceById(Long id) {

		Boolean isSuccess = false;

		if (vendorPaymentRepository.existsByIdAndIsDeleteFalse(id)) {
			VendorPaymentEntity entity = vendorPaymentRepository.findByIdAndIsDeleteFalse(id);
			if (entity.getIsOpb()) {
				PartyMasterEntity partyMasterEntity = partyMasterRepository
						.findByIdAndIsDeleteFalse(entity.getVendorId())
						.orElseThrow(() -> new RuntimeException("Vendor Not Found"));
				partyMasterEntity.setOpb(
						partyMasterEntity.getOpb().add(entity.getPayAmount().add(entity.getSettlementAmount())));
				partyMasterRepository.save(partyMasterEntity);
			}

			BankDetailsEntity bankDetailsEntity = null;
			if (entity.getBankId() != null) {
				bankDetailsEntity = bankDetailsRepository.findByIdAndIsDeleteFalse(entity.getBankId()).orElseThrow(
						() -> new RuntimeException("Bank Details Not Found with id:- " + entity.getBankId()));
			}

			CashAccountEntity cashAccountEntity = null;
			if (entity.getCashAccountId() != null) {
				cashAccountEntity = cashAccountRepository.findByIdAndIsDeleteFalse(entity.getCashAccountId())
						.orElseThrow(() -> new RuntimeException(
								"Cash Account not found with id : " + entity.getCashAccountId()));
			}

			if (bankDetailsEntity != null) {
				bankDetailsEntity.setCurrentBalance(bankDetailsEntity.getCurrentBalance()
						.subtract(entity.getReceivedAmount()).add(entity.getPayAmount()));
				bankDetailsRepository.save(bankDetailsEntity);
			} else {
				cashAccountEntity.setCurrentBalance(cashAccountEntity.getCurrentBalance()
						.subtract(entity.getReceivedAmount()).add(entity.getPayAmount()));
				cashAccountRepository.save(cashAccountEntity);
			}
			entity.setIsDelete(true);
			vendorPaymentRepository.save(entity);
			isSuccess = true;
		}

		return isSuccess;
	}

	@Override
	public List<AccountLadgerPartyResponseDto> getAllParty(Long userid, Boolean isBookingParty, Boolean isAllStatus) {

		userMasterRepository.findByIdAndIsDeleteFalse(userid)
				.orElseThrow(() -> new RuntimeException("User Not found With id:-" + userid));

		List<Object[]> bookingPartyList = Boolean.FALSE.equals(isBookingParty) ? Collections.emptyList()
				: eventMasterRepository.findAllConfirmPartyByUser(userid, isAllStatus);

		List<Object[]> chefPartyList = Boolean.TRUE.equals(isBookingParty) ? Collections.emptyList()
				: allocationOrderRepository.findAllChefPartyByUser(userid, isAllStatus);
		System.out.println("2");
		List<Object[]> outsidePartyList = Boolean.TRUE.equals(isBookingParty) ? Collections.emptyList()
				: allocationOrderRepository.findAllOutsidePartyByUser(userid, isAllStatus);

		List<Object[]> labourPartyList = Boolean.TRUE.equals(isBookingParty) ? Collections.emptyList()
				: eventLaborRepository.findAllLabourPartyByUser(userid, isAllStatus);
		System.out.println("3");
		List<Object[]> purchasePartyList = Boolean.TRUE.equals(isBookingParty) ? Collections.emptyList()
				: purchaseOrderRepository.findAllPurchasePartyByUser(userid);

		List<Object[]> usersList = userMasterRepository.findAllByUser(userid);

		Stream<AccountLadgerPartyResponseDto> partyStream = Stream
				.of(bookingPartyList.stream().map(obj -> mapToPartyDto(obj, "User")),
						chefPartyList.stream().map(obj -> mapToPartyDto(obj, "Chef")),
						outsidePartyList.stream().map(obj -> mapToPartyDto(obj, "Outside")),
						labourPartyList.stream().map(obj -> mapToPartyDto(obj, "Labour")),
						purchasePartyList.stream().map(obj -> mapToPartyDto(obj, "Purchase")),
						usersList.stream().map(obj -> mapToPartyDto(obj, "AccountContact")))
				.flatMap(s -> s);

		Map<Long, AccountLadgerPartyResponseDto> uniqueParties = partyStream.collect(Collectors
				.toMap(dto -> dto.getPartyId(), dto -> dto, (existing, duplicate) -> existing, LinkedHashMap::new));

		return uniqueParties.values().stream().sorted(Comparator.comparing(AccountLadgerPartyResponseDto::getPartyId))
				.collect(Collectors.toList());
	}

	private AccountLadgerPartyResponseDto mapToPartyDto(Object[] obj, String type) {

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		String date3 = null;
		String date4 = null;
		String date5 = null;
		if (obj[3] != null) {
			LocalDateTime dt = convertToLocalDateTime(obj[3]);
			date3 = dt != null ? dt.format(formatter) : null;
		}

		if (obj[4] != null) {
			LocalDateTime dt = convertToLocalDateTime(obj[4]);
			date4 = dt != null ? dt.format(formatter) : null;
		}
		if (obj[5] != null) {
			LocalDate dt = convertToLocalDate(obj[5]);
			date5 = dt != null ? dt.format(formatter) : null;
		}
		return new AccountLadgerPartyResponseDto(((Number) obj[0]).longValue(), (String) obj[1], (String) obj[2], date3,
				date4, type, date5, (BigDecimal) obj[6]);
	}

	private LocalDate convertToLocalDate(Object obj) {
		if (obj instanceof LocalDate) {
			return (LocalDate) obj;
		} else if (obj instanceof java.sql.Date) {
			return ((java.sql.Date) obj).toLocalDate();
		}
		return null;
	}

	private LocalDateTime convertToLocalDateTime(Object obj) {
		if (obj instanceof LocalDateTime) {
			return (LocalDateTime) obj;
		} else if (obj instanceof java.sql.Timestamp) {
			return ((java.sql.Timestamp) obj).toLocalDateTime();
		}
		return null;
	}

	@Override
	public VendorPartyWiseFinalEventResponseDto getAllEventByParty(Long partyId, Long userId) {

		List<VendorPartyWiseEventResponseDto> events = vendorPaymentRepository.getAllEventByParty(partyId, userId)
				.stream()
				.map(obj -> new VendorPartyWiseEventResponseDto(obj[0] != null ? ((Number) obj[0]).longValue() : null,
						obj[1] != null ? (String) obj[1] : null,
						obj[2] != null ? BigDecimal.valueOf(((Number) obj[2]).doubleValue()) : BigDecimal.ZERO,
						obj[3] != null ? (String) obj[3] : ""))
				.collect(Collectors.toList());

		BigDecimal totalAmount = events.stream().map(VendorPartyWiseEventResponseDto::getRemaingAmnt)
				.filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);

		return new VendorPartyWiseFinalEventResponseDto(totalAmount, events);
	}

	@Override
	public AccountLedgerFinalResponseDto getAccountLadger(Long partyId, String startDate, String endDate, Long userId,
			String vendorCat, String type) {

		if ((startDate == null || startDate.trim().isEmpty()) && (endDate == null || endDate.trim().isEmpty())) {
			throw new IllegalArgumentException("Start date and End date cannot be empty");
		}

		List<AccountLadgerResponseDto> list = new ArrayList<>();

		DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		List<Object[]> rows = null;

		PartyMasterEntity party = null;
		AccountContactEntity accountContactEntity = null;

		BigDecimal totalCredit = BigDecimal.ZERO;
		BigDecimal totalDebit = BigDecimal.ZERO;
		BigDecimal balance = BigDecimal.ZERO;

		// Opening Balance Row
		AccountLadgerResponseDto openingRow = new AccountLadgerResponseDto();

		if (vendorCat != null) {
			if (!vendorCat.equalsIgnoreCase("AccountContact")) {
				rows = vendorPaymentRepository.getGroupedLedger(partyId, userId, startDate, endDate, type);
				party = partyMasterRepository.getOpeningParty(partyId, userId,
						LocalDate.parse(startDate, inputFormatter));

				balance = calculateFinalOpeningBalance(party, partyId, userId, startDate, type);

				openingRow.setInvoiceNo("OPB");
				openingRow.setAccountName("Opening Balance");
				openingRow.setDate(startDate);
				openingRow.setType("OPB/PREV");
				openingRow.setSettlementAmnt(BigDecimal.ZERO);

				BigDecimal opbBalance = balance;
				if (party != null && "CR".equalsIgnoreCase(party.getType())) {
					openingRow.setCreditCrAmnt(balance);
					openingRow.setDebitDrAmnt(BigDecimal.ZERO);
					totalCredit = totalCredit.add(balance);
					opbBalance = opbBalance.negate();
				} else {
					openingRow.setCreditCrAmnt(BigDecimal.ZERO);
					openingRow.setDebitDrAmnt(balance);
					totalDebit = totalDebit.add(balance);
				}

				openingRow.setTotalAmnt(opbBalance);
				openingRow.setRemarks("");

				list.add(openingRow);
			} else {
				LocalDate stDate = LocalDate.parse(startDate, inputFormatter);
				LocalDate eDate = LocalDate.parse(endDate, inputFormatter);
				rows = accountEntryRepository.getAccountLedgerByAccountContact(partyId, stDate.toString(),
						eDate.toString(), userId);

				if (rows != null && !rows.isEmpty()) {
					Object[] firstRow = rows.get(0);

					totalDebit = firstRow[5] != null ? new BigDecimal(firstRow[5].toString()) : BigDecimal.ZERO;

					totalCredit = firstRow[4] != null ? new BigDecimal(firstRow[4].toString()) : BigDecimal.ZERO;
				}
			}
		} else {
			throw new RuntimeException("Vendor category is mandatory.");
		}

		// Transaction Rows
		for (Object[] row : rows) {

			String date = toStr(row[0]);
			String invoiceNo = toStr(row[1]);
			String accountName = toStr(row[2]);
			String txnType = toStr(row[3]);

			BigDecimal credit = toBigDecimal(row[4]);
			BigDecimal debit = toBigDecimal(row[5]);

			String remarks = toStr(row[6]);

			BigDecimal cdBalance = debit.subtract(credit);
			// Running Balance
			balance = balance.add(cdBalance);

			totalCredit = totalCredit.add(credit);
			totalDebit = totalDebit.add(debit);
			AccountLadgerResponseDto dto = new AccountLadgerResponseDto();

			dto.setInvoiceNo(invoiceNo);
			dto.setAccountName(accountName);
			dto.setType(txnType);

			String formattedDate = "";
			if (date != null && !date.isEmpty()) {
				LocalDate localDate = LocalDate.parse(date);
				formattedDate = localDate.format(inputFormatter);
			}

			dto.setDate(formattedDate);
			dto.setSettlementAmnt(BigDecimal.ZERO);
			dto.setCreditCrAmnt(credit);
			dto.setDebitDrAmnt(debit);

			// Running balance after this transaction
			dto.setTotalAmnt(totalDebit.subtract(totalCredit));

			dto.setRemarks(remarks);

			list.add(dto);
		}

		AccountLedgerSummaryDto summary = new AccountLedgerSummaryDto();
		summary.setTotalCredit(totalCredit);
		summary.setTotalDebit(totalDebit);
		summary.setTotalBalance(totalDebit.subtract(totalCredit));

		AccountLedgerFinalResponseDto response = new AccountLedgerFinalResponseDto();
		response.setLedgerList(list);
		response.setSummary(summary);

		return response;
	}

	private BigDecimal calculateFinalOpeningBalance(PartyMasterEntity party, Long partyId, Long userId,
			String startDate, String type) {

		BigDecimal opb = BigDecimal.ZERO;
		LocalDate opbDate = null;

		if (party == null) {
			return BigDecimal.ZERO;
		}
		if (party.getOpb() != null) {
			opb = party.getOpb();
		}
		opbDate = party.getOpbDate();

		DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		LocalDate startLocalDate = LocalDate.parse(startDate, inputFormatter);

		if (opbDate == null || opbDate.isEqual(startLocalDate)) {
			return opb;
		}

		if (opbDate.isAfter(startLocalDate)) {
			return opb;
		}

		String fromDate = opbDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		String toDate = startLocalDate.minusDays(1).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

		List<Object[]> movement = vendorPaymentRepository.getGroupedLedger(partyId, userId, fromDate, toDate, type);

		BigDecimal totalCredit = BigDecimal.ZERO;
		BigDecimal totalDebit = BigDecimal.ZERO;

		if (movement != null && !movement.isEmpty()) {
			for (Object[] row : movement) {
				if (row[4] != null) {
					totalCredit = totalCredit.add(new BigDecimal(row[4].toString()));
				}
				if (row[5] != null) {
					totalDebit = totalDebit.add(new BigDecimal(row[5].toString()));
				}
			}
		}
		BigDecimal balance = opb;
		if (totalCredit.compareTo(BigDecimal.ZERO) > 0) {
			balance = balance.subtract(totalCredit);
		} else {
			balance = balance.add(totalDebit);
		}
		return balance;
	}

	private String toStr(Object obj) {
		if (obj == null) {
			return "";
		}
		if (obj instanceof byte[]) {
			return new String((byte[]) obj);
		}
		return obj.toString();
	}

	private BigDecimal toBigDecimal(Object obj) {
		if (obj == null) {
			return BigDecimal.ZERO;
		}

		if (obj instanceof BigDecimal) {
			return (BigDecimal) obj;
		}

		if (obj instanceof BigInteger) {
			return new BigDecimal((BigInteger) obj);
		}

		if (obj instanceof Integer || obj instanceof Long || obj instanceof Short) {
			return BigDecimal.valueOf(((Number) obj).longValue());
		}

		if (obj instanceof Float || obj instanceof Double) {
			return BigDecimal.valueOf(((Number) obj).doubleValue());
		}

		if (obj instanceof byte[]) {
			String value = new String((byte[]) obj).trim();
			return value.isEmpty() ? BigDecimal.ZERO : new BigDecimal(value);
		}

		String value = obj.toString().trim();
		return value.isEmpty() ? BigDecimal.ZERO : new BigDecimal(value);
	}

	@Override
	public Boolean updateVendorPaymentInvoice(UpdateVendorPaymentRequestDto request) {
		BankDetailsEntity bankDetailsEntity = null;
		CashAccountEntity cashAccountEntity = null;
		UserMasterEntity userMasterEntity = userMasterRepository.findByIdAndIsDeleteFalse(request.getUserId())
				.orElseThrow(() -> new RuntimeException("User Not found With id:-" + request.getUserId()));
		if (request.getBankId() != null) {
			bankDetailsEntity = bankDetailsRepository.findByIdAndIsDeleteFalse(request.getBankId())
					.orElseThrow(() -> new RuntimeException("Bank Details Not Found with id:- " + request.getBankId()));
		}

		if (request.getCashId() != null) {
			cashAccountEntity = cashAccountRepository.findByIdAndIsDeleteFalse(request.getCashId())
					.orElseThrow(() -> new RuntimeException("Cash Account not found with id : " + request.getCashId()));
		}

		PartyMasterEntity partyMasterEntity = partyMasterRepository.findByIdAndIsDeleteFalse(request.getVendorId())
				.orElseThrow(() -> new RuntimeException("Vendor Not Found with id:- " + request.getVendorId()));

		VendorPaymentEntity entity = vendorPaymentRepository.findByIdAndIsDeleteFalse(request.getId());
		if (entity == null) {
			throw new RuntimeException("Vendor Payment Not Found With Id:-" + request.getId());
		}
		entity.setUpdatedAt(commonService.getCurrentDateTime());
		EventMasterEntity eventMasterEntity = null;
//		if (request.getIsOpb()) {
//			partyMasterEntity.setOpb(
//					partyMasterEntity.getOpb().subtract((request.getPayAmount().add(request.getSettlementAmount()))));
//			partyMasterRepository.save(partyMasterEntity);
//		}
		if (request.getEventId() != null) {
			eventMasterEntity = eventMasterRepository.findByIdAndIsDeleteFalse(request.getEventId()).orElseThrow(
					() -> new RuntimeException("Event Master Not Found with id:- " + request.getEventId()));
		}

		entity.setBankId(bankDetailsEntity == null ? null : bankDetailsEntity.getId());
		entity.setCashAccountId(cashAccountEntity == null ? null : cashAccountEntity.getId());
		entity.setEventId(eventMasterEntity == null ? null : eventMasterEntity.getId());
		entity.setPaymentDate(commonService.dateFormatted(request.getPaymentDate()));
		entity.setPaymentMode(request.getPaymentMode());
		entity.setReferenceId(request.getReferenceId());
		entity.setUser(userMasterEntity);
		entity.setVendorCat(request.getVendorCat());
		entity.setVendorId(partyMasterEntity == null ? null : partyMasterEntity.getId());
		entity.setRemarks(request.getRemarks());
		entity.setIsPayable(request.getIsPayable());
		entity.setIsOpb(request.getIsOpb());
		entity.setSettlementAmount(request.getSettlementAmount());
		entity.setReceivedAmount(request.getReceivedAmount());
		entity.setPayAmount(request.getPayAmount());
		BigDecimal oldAmount = BigDecimal.ZERO;
		if (bankDetailsEntity != null) {
			oldAmount = bankDetailsEntity.getCurrentBalance().subtract(entity.getReceivedAmount())
					.add(entity.getPayAmount());
			bankDetailsEntity
					.setCurrentBalance(oldAmount.add(entity.getReceivedAmount()).subtract(entity.getPayAmount()));
			bankDetailsRepository.save(bankDetailsEntity);
		} else {
			oldAmount = cashAccountEntity.getCurrentBalance().subtract(entity.getReceivedAmount())
					.add(entity.getPayAmount());
			cashAccountEntity
					.setCurrentBalance(oldAmount.add(entity.getReceivedAmount()).subtract(entity.getPayAmount()));
			cashAccountRepository.save(cashAccountEntity);
		}

		VendorPaymentEntity savedEntity = vendorPaymentRepository.save(entity);

		if (savedEntity == null) {
			return false;
		}
		return true;
	}

	@Override
	public String accountLedgerPdf(Long partyId, String startDate, String endDate, Long userId, String vendorCat,
			HttpServletRequest request, String type) {

		try {

			AccountLedgerFinalResponseDto data = getAccountLadger(partyId, startDate, endDate, userId, vendorCat, type);
			String partyName = "";
			if (vendorCat.equalsIgnoreCase("AccountContact")) {
				AccountContactEntity accountContactEntity = accountContactRepository.findByIdAndIsDeleteFalse(partyId)
						.orElseThrow(() -> new RuntimeException("Account Contact Not Found"));
				partyName = accountContactEntity.getName();
			} else {
				PartyMasterEntity partyMasterEntity = partyMasterRepository.findByIdAndIsDeleteFalse(partyId)
						.orElseThrow(null);
				partyName = partyMasterEntity.getNameEnglish();
			}
			byte[] pdfBytes = generateAccountLedgerPdf(data, startDate, endDate, partyName);

			String rootPath = request.getSession().getServletContext().getRealPath("/");
			UserMasterEntity userMasterEntity = userMasterRepository.findByIdAndIsDeleteFalse(userId).orElseThrow(null);
			String folderName = String.valueOf(userMasterEntity.getEmail());

			File dir = new File(rootPath + "resources/tempDownload/" + folderName + "/");

			if (!dir.exists()) {
				dir.mkdirs();
			}

			String fileName = replaceSpaceWithUnderscore(partyName) + "_ACCOUNT_LEDGER_" + System.currentTimeMillis()
					+ ".pdf";

			File pdfFile = new File(dir, fileName);

			Files.write(pdfFile.toPath(), pdfBytes);

			return environment.getProperty("ws_image_path") + "/api/download/pdf/" + folderName + "/" + fileName;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Unable to generate Account Ledger PDF");
		}
	}

	private byte[] generateAccountLedgerPdf(AccountLedgerFinalResponseDto response, String startDate, String endDate,
			String partyName) throws Exception {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		PdfWriter writer = new PdfWriter(baos);

		PdfDocument pdfDoc = new PdfDocument(writer);

		Document document = new Document(pdfDoc, PageSize.A4.rotate());

		PdfFont normalFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);

		PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);

		Color primaryColor = new DeviceRgb(98, 72, 156);

		Color summaryBg = new DeviceRgb(242, 245, 250);

		Paragraph title = new Paragraph(partyName + " " + "ACCOUNT LEDGER").setFont(boldFont).setFontSize(18)
				.setFontColor(primaryColor).setTextAlignment(TextAlignment.CENTER);

		document.add(title);

		document.add(new Paragraph("Period : " + startDate + " To " + endDate).setFont(normalFont).setFontSize(10)
				.setTextAlignment(TextAlignment.CENTER));

		document.add(new Paragraph("\n"));

		AccountLedgerSummaryDto summary = response.getSummary();

		Table summaryTable = new Table(3);

		summaryTable.setWidth(UnitValue.createPercentValue(100));

		summaryTable.addCell(createSummaryCell("CR (Credit) Amount", summary.getTotalCredit(), summaryBg));

		summaryTable.addCell(createSummaryCell("DR (Debit) Amount", summary.getTotalDebit(), summaryBg));

		summaryTable.addCell(createSummaryCell("Total Balance", summary.getTotalBalance(), summaryBg));

		document.add(summaryTable);

		document.add(new Paragraph("\n"));

		Table table = new Table(new float[] { 1, 2, 4, 2, 1.5f, 2, 4, 2, 2, 2 });

		table.setWidth(UnitValue.createPercentValue(100));

		addHeader(table, "SR");
		addHeader(table, "Invoice No");
		addHeader(table, "Account Name");
		addHeader(table, "Date");
		addHeader(table, "Type");
		addHeader(table, "Settlement");
		addHeader(table, "Remarks");
		addHeader(table, "CR");
		addHeader(table, "DR");
		addHeader(table, "Balance");

		int srNo = 1;

		for (AccountLadgerResponseDto dto : response.getLedgerList()) {

			Color rowColor = srNo % 2 == 0 ? new DeviceRgb(250, 250, 250) : ColorConstants.WHITE;

			table.addCell(createCell(String.valueOf(srNo++), rowColor));

			table.addCell(createCell(dto.getInvoiceNo(), rowColor));

			table.addCell(createCell(dto.getAccountName(), rowColor));

			table.addCell(createCell(dto.getDate(), rowColor));

			table.addCell(createTypeCell(dto.getType()));

			table.addCell(createCell(amount(dto.getSettlementAmnt()), rowColor));

			table.addCell(createCell(dto.getRemarks(), rowColor));

			table.addCell(createCell(amount(dto.getCreditCrAmnt()), rowColor));

			table.addCell(createCell(amount(dto.getDebitDrAmnt()), rowColor));

			table.addCell(createCell(amount(dto.getTotalAmnt()), rowColor));
		}

		document.add(table);

		document.close();

		return baos.toByteArray();
	}

	private void addHeader(Table table, String title) throws IOException {

		PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);

		table.addHeaderCell(

				new Cell()

						.add(new Paragraph(title).setFont(boldFont).setFontColor(ColorConstants.WHITE))

						.setBackgroundColor(new DeviceRgb(0, 91, 168))

						.setTextAlignment(TextAlignment.CENTER)

						.setPadding(8));
	}

	private Cell createCell(String value, Color bgColor) {

		return new Cell()

				.add(new Paragraph(value == null ? "" : value).setFontSize(9))

				.setBackgroundColor(bgColor)

				.setPadding(5)

				.setTextAlignment(TextAlignment.CENTER);
	}

	private Cell createTypeCell(String type) {

		Cell cell = new Cell();

		cell.setTextAlignment(TextAlignment.CENTER);

		cell.add(new Paragraph(type == null ? "" : type).setFontSize(9));

		return cell;
	}

	private Cell createSummaryCell(String title, BigDecimal amount, Color bgColor) throws IOException {

		PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);

		Paragraph heading = new Paragraph(title).setFontSize(10);

		Paragraph value = new Paragraph("₹ " + amount(amount)).setFont(boldFont).setFontSize(14);

		return new Cell()

				.add(heading)

				.add(value)

				.setBackgroundColor(bgColor)

				.setPadding(10)

				.setBorder(new SolidBorder(new DeviceRgb(220, 220, 220), 1));
	}

	private String amount(BigDecimal amount) {

		if (amount == null) {
			return "0.00";
		}

		DecimalFormat formatter = new DecimalFormat("#,##0.00");

		return formatter.format(amount);
	}

	@Override
	public String accountLedgerExcel(Long partyId, String startDate, String endDate, Long userId, String vendorCat,
			HttpServletRequest request, String type) {

		try {

			AccountLedgerFinalResponseDto data = getAccountLadger(partyId, startDate, endDate, userId, vendorCat, type);

			String partyName = "";
			if (vendorCat.equalsIgnoreCase("AccountContact")) {
				AccountContactEntity accountContactEntity = accountContactRepository.findByIdAndIsDeleteFalse(partyId)
						.orElseThrow(() -> new RuntimeException("Account Contact Not Found"));
				partyName = accountContactEntity.getName();
			} else {
				PartyMasterEntity partyMasterEntity = partyMasterRepository.findByIdAndIsDeleteFalse(partyId)
						.orElseThrow(null);
				partyName = partyMasterEntity.getNameEnglish();
			}

			XSSFWorkbook workbook = new XSSFWorkbook();

			XSSFSheet sheet = workbook.createSheet(partyName.toUpperCase() + " " + "ACCOUNT LEDGER");

			XSSFCellStyle titleStyle = workbook.createCellStyle();
			titleStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(98, 72, 156), null));
			titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			titleStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);

			XSSFFont titleFont = workbook.createFont();
			titleFont.setBold(true);
			titleFont.setFontHeightInPoints((short) 16);
			titleFont.setColor(IndexedColors.WHITE.getIndex());
			titleStyle.setFont(titleFont);

			XSSFCellStyle summaryStyle = workbook.createCellStyle();
			summaryStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(232, 240, 254), null));
			summaryStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			summaryStyle.setBorderBottom(BorderStyle.THIN);
			summaryStyle.setBorderTop(BorderStyle.THIN);
			summaryStyle.setBorderLeft(BorderStyle.THIN);
			summaryStyle.setBorderRight(BorderStyle.THIN);

			XSSFFont summaryFont = workbook.createFont();
			summaryFont.setBold(true);
			summaryStyle.setFont(summaryFont);

			XSSFCellStyle headerStyle = workbook.createCellStyle();
			headerStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(98, 72, 156), null));
			headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			headerStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
			headerStyle.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);

			headerStyle.setBorderBottom(BorderStyle.THIN);
			headerStyle.setBorderTop(BorderStyle.THIN);
			headerStyle.setBorderLeft(BorderStyle.THIN);
			headerStyle.setBorderRight(BorderStyle.THIN);

			XSSFFont headerFont = workbook.createFont();
			headerFont.setBold(true);
			headerFont.setColor(IndexedColors.WHITE.getIndex());
			headerStyle.setFont(headerFont);

			XSSFCellStyle dataStyle = workbook.createCellStyle();
			dataStyle.setBorderBottom(BorderStyle.THIN);
			dataStyle.setBorderTop(BorderStyle.THIN);
			dataStyle.setBorderLeft(BorderStyle.THIN);
			dataStyle.setBorderRight(BorderStyle.THIN);

			XSSFCellStyle alternateStyle = workbook.createCellStyle();
			alternateStyle.cloneStyleFrom(dataStyle);
			alternateStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(245, 245, 245), null));
			alternateStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

			XSSFCellStyle amountStyle = workbook.createCellStyle();
			amountStyle.cloneStyleFrom(dataStyle);
			amountStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.RIGHT);

			DataFormat format = workbook.createDataFormat();
			amountStyle.setDataFormat(format.getFormat("#,##0.00"));

			XSSFCellStyle alternateAmountStyle = workbook.createCellStyle();
			alternateAmountStyle.cloneStyleFrom(amountStyle);
			alternateAmountStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(245, 245, 245), null));
			alternateAmountStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

			int rowNum = 0;

			Row titleRow = sheet.createRow(rowNum++);
			org.apache.poi.ss.usermodel.Cell titleCell = titleRow.createCell(0);

			titleCell.setCellValue(partyName.toUpperCase() + " " + "ACCOUNT LEDGER");
			titleCell.setCellStyle(titleStyle);

			sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 9));

			Row dateRow = sheet.createRow(rowNum++);
			dateRow.createCell(0).setCellValue("Period : " + startDate + " To " + endDate);

			rowNum++;

			AccountLedgerSummaryDto summary = data.getSummary();

			Row summaryHeader = sheet.createRow(rowNum++);

			org.apache.poi.ss.usermodel.Cell sh1 = summaryHeader.createCell(0);
			sh1.setCellValue("Total Credit");
			sh1.setCellStyle(summaryStyle);

			org.apache.poi.ss.usermodel.Cell sh2 = summaryHeader.createCell(3);
			sh2.setCellValue("Total Debit");
			sh2.setCellStyle(summaryStyle);

			org.apache.poi.ss.usermodel.Cell sh3 = summaryHeader.createCell(6);
			sh3.setCellValue("Total Balance");
			sh3.setCellStyle(summaryStyle);

			Row summaryValue = sheet.createRow(rowNum++);

			org.apache.poi.ss.usermodel.Cell sv1 = summaryValue.createCell(0);
			sv1.setCellValue(summary.getTotalCredit() != null ? summary.getTotalCredit().doubleValue() : 0);
			sv1.setCellStyle(summaryStyle);

			org.apache.poi.ss.usermodel.Cell sv2 = summaryValue.createCell(3);
			sv2.setCellValue(summary.getTotalDebit() != null ? summary.getTotalDebit().doubleValue() : 0);
			sv2.setCellStyle(summaryStyle);

			org.apache.poi.ss.usermodel.Cell sv3 = summaryValue.createCell(6);
			sv3.setCellValue(summary.getTotalBalance() != null ? summary.getTotalBalance().doubleValue() : 0);
			sv3.setCellStyle(summaryStyle);

			rowNum++;

			Row headerRow = sheet.createRow(rowNum++);

			String[] headers = { "SR NO", "INVOICE NO", "ACCOUNT NAME", "DATE", "TYPE", "SETTLEMENT AMOUNT", "REMARKS",
					"CR (CREDIT)", "DR (DEBIT)", "TOTAL BALANCE" };

			for (int i = 0; i < headers.length; i++) {

				org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
				cell.setCellValue(headers[i]);
				cell.setCellStyle(headerStyle);
			}

			int srNo = 1;

			for (AccountLadgerResponseDto dto : data.getLedgerList()) {

				Row row = sheet.createRow(rowNum++);

				boolean even = srNo % 2 == 0;

				XSSFCellStyle rowStyle = even ? alternateStyle : dataStyle;

				XSSFCellStyle rowAmountStyle = even ? alternateAmountStyle : amountStyle;

				createCell(row, 0, String.valueOf(srNo++), rowStyle);
				createCell(row, 1, dto.getInvoiceNo(), rowStyle);
				createCell(row, 2, dto.getAccountName(), rowStyle);
				createCell(row, 3, dto.getDate(), rowStyle);
				createCell(row, 4, dto.getType(), rowStyle);

				createAmountCell(row, 5, dto.getSettlementAmnt(), rowAmountStyle);

				createCell(row, 6, dto.getRemarks(), rowStyle);

				createAmountCell(row, 7, dto.getCreditCrAmnt(), rowAmountStyle);

				createAmountCell(row, 8, dto.getDebitDrAmnt(), rowAmountStyle);

				createAmountCell(row, 9, dto.getTotalAmnt(), rowAmountStyle);
			}

			sheet.createFreezePane(0, rowNum > 8 ? 7 : 0);

			sheet.setAutoFilter(new CellRangeAddress(6, rowNum - 1, 0, 9));

			sheet.setColumnWidth(0, 3500);
			sheet.setColumnWidth(1, 5000);
			sheet.setColumnWidth(2, 12000);
			sheet.setColumnWidth(3, 4500);
			sheet.setColumnWidth(4, 3500);
			sheet.setColumnWidth(5, 6000);
			sheet.setColumnWidth(6, 12000);
			sheet.setColumnWidth(7, 6000);
			sheet.setColumnWidth(8, 6000);
			sheet.setColumnWidth(9, 7000);

			String rootPath = request.getSession().getServletContext().getRealPath("/");

			UserMasterEntity userMasterEntity = userMasterRepository.findByIdAndIsDeleteFalse(userId).orElseThrow(null);
			String folderName = String.valueOf(userMasterEntity.getEmail());

			File dir = new File(rootPath + "resources/tempDownload/" + folderName + "/");

			if (!dir.exists()) {
				dir.mkdirs();
			}

			String fileName = replaceSpaceWithUnderscore(partyName.toUpperCase()) + "_ACCOUNT_LEDGER_"
					+ System.currentTimeMillis() + ".xlsx";

			File file = new File(dir, fileName);

			try (FileOutputStream fos = new FileOutputStream(file)) {
				workbook.write(fos);
			}

			workbook.close();

			return environment.getProperty("ws_image_path") + "/api/download/excel/" + folderName + "/" + fileName;

		} catch (Exception e) {
			throw new RuntimeException("Error generating account ledger excel", e);
		}
	}

	private void createCell(Row row, int column, String value, CellStyle style) {

		org.apache.poi.ss.usermodel.Cell cell = row.createCell(column);
		cell.setCellValue(value == null ? "" : value);
		cell.setCellStyle(style);
	}

	private void createAmountCell(Row row, int column, BigDecimal value, CellStyle style) {

		org.apache.poi.ss.usermodel.Cell cell = row.createCell(column);

		cell.setCellValue(value != null ? value.doubleValue() : 0.00);

		cell.setCellStyle(style);
	}

	private String replaceSpaceWithUnderscore(String value) {

		if (value == null || value.trim().isEmpty()) {
			return "";
		}

		return value.trim().replaceAll("\\s+", "_");
	}

	@SuppressWarnings("unchecked")
	@Override
	public String generateVendorPaymentReport(Integer isCompanyDetails, Boolean isPayable, HttpServletRequest re,
			Long userId) {
		try {
			PdfFont basicFont = null;
			PdfFont boldFont = null;
			menuPreparationServiceImpl.loadLicense();

			// English
			System.out.println("Loading English font...");
			basicFont = menuPreparationServiceImpl.loadFont("/fonts/arial-regular.ttf");
			boldFont = menuPreparationServiceImpl.loadFont("/fonts/arial-bold.ttf");
			System.out.println("English font loaded successfully");

			UserBasicDetailsMasterEntity cmpData = rawMaterialReportServiceImpl.getCompanyData(userId);

			if (cmpData == null) {
				return "Company data not found.";
			}
			Map<String, Object> vendorResponse = getVendorPaymentByEventIdAndVendorId(-1l, -1l, userId, isPayable,
					null);

			if (vendorResponse == null) {
				return "Data not found.";
			}

			Date now = new Date();
			String rootPath = re.getSession().getServletContext().getRealPath("/");
			String datetimeforfile = new SimpleDateFormat("yyyyMMddhhmmss").format(now);
			File outputPath = new File(rootPath + "resources/tempDownload/vendorpayment/");

			if (!outputPath.exists()) {
				if (outputPath.mkdirs()) {
					System.out.println("Directory Created!!!");
				} else {
					System.out.println("Error!!!");
				}
			}
			File pdfFile = new File(outputPath + "/" + datetimeforfile + ".pdf");

			PdfWriter writer = new PdfWriter(pdfFile);
			PdfDocument pdfDocument = new PdfDocument(writer);
			Document document = new Document(pdfDocument, pdfDocument.getDefaultPageSize(), false);
			document.setMargins(20, 20, 35, 20);

			Color blackColor = new DeviceRgb(0, 0, 0);

			String cmpName = cmpData.getCompanyName() != null ? cmpData.getCompanyName() : "";
			String cmpEmail = cmpData.getCompanyEmail() != null ? cmpData.getCompanyEmail() : "";
			String cmpMobileNo = cmpData.getOfficeNo() != null ? cmpData.getOfficeNo() : "";
			String cmpAddress = cmpData.getAddress() != null ? cmpData.getAddress() : "";

			if (isCompanyDetails == 1) {
				Table cmpTable = new Table(UnitValue.createPercentArray(new float[] { 30f, 70f }));
				cmpTable.setWidth(UnitValue.createPercentValue(100f));
				cmpTable.setBorder(Border.NO_BORDER);
				cmpTable.setMarginTop(10f);

				ImageData logoData = null;
				logoData = menuPreparationServiceImpl
						.loadImageFromResource(environment.getProperty("app.image.url") + cmpData.getUser().getLogo());
//				logoData = menuPreparationServiceImpl.loadImageFromResource("/flipbook/pages/logo.png");

				Image logo = new Image(logoData);

				// Resize & align
				logo.scaleToFit(100, 100);
				logo.setAutoScale(false);
				logo.setHorizontalAlignment(HorizontalAlignment.CENTER);

				Cell cell = new Cell(6, 1).add(logo).setVerticalAlignment(VerticalAlignment.MIDDLE)
						.setBorder(Border.NO_BORDER).setPadding(3f);

				cmpTable.addCell(cell);

				cell = new Cell(1, 5)
						.add(new Paragraph(cmpName.toUpperCase()).setFont(basicFont).setFontSize(12)
								.setFontColor(blackColor).setTextAlignment(TextAlignment.LEFT).simulateBold())
						.setPaddingLeft(10f).setPaddingBottom(0).setPaddingTop(0).setBorder(Border.NO_BORDER);
				cmpTable.addCell(cell);

				cell = new Cell(1, 5)
						.add(new Paragraph(cmpAddress).setFont(basicFont).setFontSize(10).setFontColor(blackColor)
								.setTextAlignment(TextAlignment.LEFT))
						.setPaddingLeft(10f).setPaddingBottom(0).setPaddingTop(0).setBorder(Border.NO_BORDER);
				cmpTable.addCell(cell);

				cell = new Cell(1, 5)
						.add(new Paragraph().add(new Text("Mobile No : ").simulateBold()).add(new Text(cmpMobileNo))
								.setFont(basicFont).setFontSize(10).setFontColor(blackColor)
								.setTextAlignment(TextAlignment.LEFT))
						.setPaddingBottom(0).setPaddingTop(0).setBorder(Border.NO_BORDER).setPaddingLeft(10f);
				cmpTable.addCell(cell);

				cell = new Cell(1, 5)
						.add(new Paragraph().add(new Text("Email : ").simulateBold()).add(new Text(cmpEmail))
								.setFont(basicFont).setFontSize(10).setFontColor(blackColor)
								.setTextAlignment(TextAlignment.LEFT))
						.setPaddingBottom(0).setPaddingTop(0).setBorder(Border.NO_BORDER).setPaddingLeft(10f);
				cmpTable.addCell(cell);

				cmpTable.addCell(new Cell(1, 5).setBorder(Border.NO_BORDER).setHeight(5f));
				cmpTable.addCell(new Cell(1, 5).setBorder(Border.NO_BORDER).setHeight(5f));

				document.add(cmpTable);
			}

			List<VendorPaymentInvoiceResponseDto> payments = (List<VendorPaymentInvoiceResponseDto>) vendorResponse
					.get("payments");

			Table transactionTable = new Table(
					UnitValue.createPercentArray(new float[] { 14f, 14f, 28f, 17f, 15f, 15f }));
			transactionTable.setFixedLayout();
			transactionTable.setWidth(UnitValue.createPercentValue(100f));
			transactionTable.setMarginTop(10f);

			Cell cell = addTableCell("DATE", boldFont, blackColor, TextAlignment.CENTER, VerticalAlignment.MIDDLE, 12f,
					5f);
			transactionTable.addCell(cell);

			cell = addTableCell("VOUCHER NO", boldFont, blackColor, TextAlignment.CENTER, VerticalAlignment.MIDDLE, 12f,
					5f);
			transactionTable.addCell(cell);

			cell = addTableCell("ACCOUNT NAME", boldFont, blackColor, TextAlignment.CENTER, VerticalAlignment.MIDDLE,
					12f, 5f);
			transactionTable.addCell(cell);

			cell = addTableCell("BANK/CASH AC", boldFont, blackColor, TextAlignment.CENTER, VerticalAlignment.MIDDLE,
					12f, 5f);
			transactionTable.addCell(cell);

			cell = addTableCell("PAYMENT MODE", boldFont, blackColor, TextAlignment.CENTER, VerticalAlignment.MIDDLE,
					12f, 5f);
			transactionTable.addCell(cell);

			cell = addTableCell("TOTAL", boldFont, blackColor, TextAlignment.CENTER, VerticalAlignment.MIDDLE, 12f, 5f);
			transactionTable.addCell(cell);

			for (VendorPaymentInvoiceResponseDto entry : payments) {
				String val = "";
				cell = addTableCell(entry.getDate(), basicFont, blackColor, TextAlignment.CENTER,
						VerticalAlignment.MIDDLE, 12f, 5f).setKeepTogether(true);
				transactionTable.addCell(cell);

				cell = addTableCell(entry.getInvoiceCode(), basicFont, blackColor, TextAlignment.CENTER,
						VerticalAlignment.MIDDLE, 12f, 5f).setKeepTogether(true);
				transactionTable.addCell(cell);

				cell = addTableCell(entry.getVendorName(), basicFont, blackColor, TextAlignment.CENTER,
						VerticalAlignment.MIDDLE, 12f, 5f).setKeepTogether(true);
				transactionTable.addCell(cell);

				String cashBankName = "";
				if (entry.getPayMode().equalsIgnoreCase("CASH")) {
					cashBankName = entry.getCashName();
				} else {
					cashBankName = entry.getBankName();
				}
				cell = addTableCell(cashBankName, basicFont, blackColor, TextAlignment.CENTER, VerticalAlignment.MIDDLE,
						12f, 5f).setKeepTogether(true);
				transactionTable.addCell(cell);

				cell = addTableCell(entry.getPayMode().toUpperCase(), basicFont, blackColor, TextAlignment.CENTER,
						VerticalAlignment.MIDDLE, 12f, 5f).setKeepTogether(true);
				transactionTable.addCell(cell);

				BigDecimal amount = BigDecimal.ZERO;
				if (isPayable) {
					amount = entry.getPayAmount() != null ? entry.getPayAmount() : BigDecimal.ZERO;
				} else {
					amount = entry.getPayAmount() != null ? entry.getReceivedAmount() : BigDecimal.ZERO;
				}
				cell = addTableCell(amount.toString(), basicFont, blackColor, TextAlignment.CENTER,
						VerticalAlignment.MIDDLE, 12f, 5f);
				transactionTable.addCell(cell);
			}

			document.add(transactionTable);

			/* ================= PAGE NUMBERS ================= */
			int totalPages = pdfDocument.getNumberOfPages();

			for (int i = 1; i <= totalPages; i++) {
				PdfPage page = pdfDocument.getPage(i);
				Canvas canvas = new Canvas(new PdfCanvas(page), page.getPageSize());

				canvas.showTextAligned("Page " + i + " of " + totalPages, page.getPageSize().getWidth() / 2, 22,
						TextAlignment.CENTER);

				canvas.close();
			}

			document.close();

			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/vendorpayment" + "/"
					+ datetimeforfile + ".pdf";
			return fullUrl;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to generate Event Menu Report", e);
		}
	}

	private Cell addTableCell(String label, PdfFont font, Color fontColor, TextAlignment textAlignment,
			VerticalAlignment verticalAlignment, float fontSize, float padding) {
		return new Cell()
				.add(new Paragraph(label == null ? "" : label).setFont(font).setFontSize(fontSize)
						.setFontColor(fontColor).setFixedLeading(fontSize))
				.setPadding(padding).setVerticalAlignment(verticalAlignment).setTextAlignment(textAlignment);
	}

	@Override
	public String generatePaymentReceipt(Integer isCompanyDetails, HttpServletRequest request, Long vendorPayId,
			Long userId) {

		PdfWriter writer = null;
		PdfDocument pdfDocument = null;
		Document document = null;

		try {
			PdfFont basicFont = menuPreparationServiceImpl.loadFont("/fonts/arial-regular.ttf");
			menuPreparationServiceImpl.loadLicense();

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

			UserBasicDetailsMasterEntity cmpData = rawMaterialReportServiceImpl.getCompanyData(userId);
			if (cmpData == null) {
				return "Company data not found.";
			}

			VendorPaymentEntity entity = vendorPaymentRepository.findByIdAndIsDeleteFalse(vendorPayId);
			if (entity == null) {
				return "Payment record not found.";
			}

			BankDetailsEntity bankDetailsEntity = null;
			if (entity.getBankId() != null) {
				bankDetailsEntity = bankDetailsRepository.findByIdAndIsDeleteFalse(entity.getBankId()).orElseThrow(
						() -> new RuntimeException("Bank Details Not Found with id:- " + entity.getBankId()));
			}

			CashAccountEntity cashAccountEntity = null;
			if (entity.getCashAccountId() != null) {
				cashAccountEntity = cashAccountRepository.findByIdAndIsDeleteFalse(entity.getCashAccountId())
						.orElseThrow(() -> new RuntimeException(
								"Cash Account not found with id : " + entity.getCashAccountId()));
			}

			PartyMasterEntity partyMasterEntity = null;
			AccountContactEntity accountContactEntity = null;
			String vendorCat = entity.getVendorCat();

			if (vendorCat != null && vendorCat.equalsIgnoreCase("AccountContact")) {
				accountContactEntity = accountContactRepository.findByIdAndIsDeleteFalse(entity.getVendorId())
						.orElseThrow(() -> new RuntimeException(
								"Account contact Not Found with id:- " + entity.getVendorId()));
			} else {
				partyMasterEntity = partyMasterRepository.findByIdAndIsDeleteFalse(entity.getVendorId())
						.orElseThrow(() -> new RuntimeException("Vendor Not Found with id:- " + entity.getVendorId()));
			}

			String vendorName = accountContactEntity != null ? accountContactEntity.getName()
					: (partyMasterEntity != null ? partyMasterEntity.getNameEnglish() : "");

			BigDecimal payAmount = Optional.ofNullable(entity.getPayAmount()).orElse(BigDecimal.ZERO);
			BigDecimal receivedAmount = Optional.ofNullable(entity.getReceivedAmount()).orElse(BigDecimal.ZERO);

			VendorPaymentInvoiceResponseDto dto = new VendorPaymentInvoiceResponseDto();
			dto.setId(entity.getId());
			dto.setDate(entity.getPaymentDate() != null ? entity.getPaymentDate().format(formatter) : null);
			dto.setInvoiceCode(entity.getInvoiceCode());
			dto.setPayMode(entity.getPaymentMode());
			dto.setReferenceId(entity.getReferenceId());
			dto.setBankId(entity.getBankId());
			dto.setBankName(bankDetailsEntity != null ? bankDetailsEntity.getBankName() : null);
			dto.setVendorName(vendorName);
			dto.setPayAmount(payAmount);
			dto.setReceivedAmount(receivedAmount);
			dto.setRemarks(entity.getRemarks());
			dto.setVendorId(entity.getVendorId());
			dto.setIsPayable(entity.getIsPayable());
			dto.setSettlementAmount(entity.getSettlementAmount());
			dto.setEventId(entity.getEventId());
			dto.setIsOpb(entity.getIsOpb());
			dto.setEventName(eventMasterRepository.getEventNameByEventId(entity.getEventId()));
			dto.setCashId(entity.getCashAccountId());
			dto.setCashName(cashAccountEntity != null ? cashAccountEntity.getAccountName() : null);

			Date now = new Date();
			String rootPath = request.getSession().getServletContext().getRealPath("/");
			String datetimeforfile = new SimpleDateFormat("yyyyMMddhhmmss").format(now);
			File outputPath = new File(rootPath + "resources/tempDownload/vendorpayment/");

			if (!outputPath.exists() && !outputPath.mkdirs()) {
				return "Failed to create output directory.";
			}
			boolean isBankPayment = dto.getBankId() != null;

			String receiptName = isBankPayment ? "Bank_Receipt" : "Cash_Receipt";

			File pdfFile = new File(outputPath, receiptName + "_" + datetimeforfile + ".pdf");

			writer = new PdfWriter(pdfFile);
			pdfDocument = new PdfDocument(writer);
			document = new Document(pdfDocument, pdfDocument.getDefaultPageSize(), false);
			document.setMargins(20, 20, 35, 20);

			Color blackColor = new DeviceRgb(0, 0, 0);

			String cmpName = safe(cmpData.getCompanyName());
			String cmpEmail = safe(cmpData.getCompanyEmail());
			String cmpMobileNo = safe(cmpData.getOfficeNo());
			String cmpAddress = safe(cmpData.getAddress());

			if (isCompanyDetails != null && isCompanyDetails == 1) {
				Table cmpTable = new Table(UnitValue.createPercentArray(new float[] { 30f, 70f }));
				cmpTable.setWidth(UnitValue.createPercentValue(100f));
				cmpTable.setBorder(Border.NO_BORDER);
				cmpTable.setMarginTop(10f);

				ImageData logoData = menuPreparationServiceImpl
						.loadImageFromResource(environment.getProperty("app.image.url") + cmpData.getUser().getLogo());

				Image logo = new Image(logoData);
				logo.scaleToFit(100, 100);
				logo.setAutoScale(false);
				logo.setHorizontalAlignment(HorizontalAlignment.CENTER);

				Cell cell = new Cell(6, 1).add(logo).setVerticalAlignment(VerticalAlignment.MIDDLE)
						.setBorder(Border.NO_BORDER).setPadding(3f);
				cmpTable.addCell(cell);

				cell = new Cell(1, 5)
						.add(new Paragraph(cmpName.toUpperCase()).setFont(basicFont).setFontSize(12)
								.setFontColor(blackColor).setTextAlignment(TextAlignment.LEFT).simulateBold())
						.setPaddingLeft(10f).setPaddingBottom(0).setPaddingTop(0).setBorder(Border.NO_BORDER);
				cmpTable.addCell(cell);

				cell = new Cell(1, 5)
						.add(new Paragraph(cmpAddress).setFont(basicFont).setFontSize(10).setFontColor(blackColor)
								.setTextAlignment(TextAlignment.LEFT))
						.setPaddingLeft(10f).setPaddingBottom(0).setPaddingTop(0).setBorder(Border.NO_BORDER);
				cmpTable.addCell(cell);

				cell = new Cell(1, 5)
						.add(new Paragraph().add(new Text("Mobile No : ").simulateBold()).add(new Text(cmpMobileNo))
								.setFont(basicFont).setFontSize(10).setFontColor(blackColor)
								.setTextAlignment(TextAlignment.LEFT))
						.setPaddingBottom(0).setPaddingTop(0).setBorder(Border.NO_BORDER).setPaddingLeft(10f);
				cmpTable.addCell(cell);

				cell = new Cell(1, 5)
						.add(new Paragraph().add(new Text("Email : ").simulateBold()).add(new Text(cmpEmail))
								.setFont(basicFont).setFontSize(10).setFontColor(blackColor)
								.setTextAlignment(TextAlignment.LEFT))
						.setPaddingBottom(0).setPaddingTop(0).setBorder(Border.NO_BORDER).setPaddingLeft(10f);
				cmpTable.addCell(cell);

				cmpTable.addCell(new Cell(1, 5).setBorder(Border.NO_BORDER).setHeight(5f));
				cmpTable.addCell(new Cell(1, 5).setBorder(Border.NO_BORDER).setHeight(5f));

				document.add(cmpTable);
			}

			String receiptTitle = isBankPayment ? "Bank Receipt" : "Cash Receipt";
			document.add(new Paragraph(receiptTitle).setFont(basicFont).setFontSize(18).setFontColor(blackColor)
					.simulateBold().setTextAlignment(TextAlignment.CENTER).setMarginTop(15f).setMarginBottom(10f));

			String voucherNo = safe(dto.getInvoiceCode());
			String paymentDateStr = safe(dto.getDate());

			Table headerInfoTable = new Table(UnitValue.createPercentArray(new float[] { 50f, 50f }));
			headerInfoTable.setWidth(UnitValue.createPercentValue(100f));
			headerInfoTable.setBorder(Border.NO_BORDER);
			headerInfoTable.setMarginBottom(5f);

			headerInfoTable.addCell(new Cell()
					.add(new Paragraph().add(new Text("Voucher No. : ").simulateBold()).add(new Text(voucherNo)))
					.setFont(basicFont).setFontSize(10).setFontColor(blackColor).setTextAlignment(TextAlignment.LEFT)
					.setBorder(Border.NO_BORDER));

			headerInfoTable.addCell(new Cell()
					.add(new Paragraph().add(new Text("Date : ").simulateBold()).add(new Text(paymentDateStr)))
					.setFont(basicFont).setFontSize(10).setFontColor(blackColor).setTextAlignment(TextAlignment.RIGHT)
					.setBorder(Border.NO_BORDER));

			document.add(headerInfoTable);

			if (isBankPayment) {
				String accountNo = bankDetailsEntity.getAccountNo();
				String last4 = (accountNo != null && accountNo.length() >= 4)
						? accountNo.substring(accountNo.length() - 4)
						: safe(accountNo);

				String bankName = bankDetailsEntity != null
						? safe(bankDetailsEntity.getBankName()) + (last4 != null ? " " + last4 : "")
						: "";

				document.add(new Paragraph().add(new Text("Bank Name : ").simulateBold()).add(new Text(bankName))
						.setFont(basicFont).setFontSize(10).setFontColor(blackColor)
						.setTextAlignment(TextAlignment.LEFT).setMarginBottom(8f));
			}

			Table itemTable;
			if (isBankPayment) {
				itemTable = new Table(UnitValue.createPercentArray(new float[] { 6f, 20f, 16f, 14f, 32f, 12f }));
			} else {
				itemTable = new Table(UnitValue.createPercentArray(new float[] { 8f, 30f, 46f, 16f }));
			}
			itemTable.setWidth(UnitValue.createPercentValue(100f));
			itemTable.setMarginTop(10f);

			itemTable.addHeaderCell(headerCell("No.", basicFont, blackColor));
			itemTable.addHeaderCell(headerCell("A/C Name", basicFont, blackColor));
			if (isBankPayment) {
				itemTable.addHeaderCell(headerCell("Trans No", basicFont, blackColor));
				itemTable.addHeaderCell(headerCell("Trans Date", basicFont, blackColor));
			}
			itemTable.addHeaderCell(headerCell("Particular", basicFont, blackColor));
			itemTable.addHeaderCell(headerCell("Amount", basicFont, blackColor));

			itemTable.addCell(dataCell("1", TextAlignment.CENTER, basicFont, blackColor));
			itemTable.addCell(dataCell(dto.getVendorName(), TextAlignment.LEFT, basicFont, blackColor));
			if (isBankPayment) {
				itemTable.addCell(dataCell(safe(dto.getReferenceId()), TextAlignment.CENTER, basicFont, blackColor));
				itemTable.addCell(dataCell(paymentDateStr, TextAlignment.CENTER, basicFont, blackColor));
			}
			itemTable.addCell(dataCell(safe(dto.getRemarks()), TextAlignment.LEFT, basicFont, blackColor));
			itemTable.addCell(
					dataCell(dto.getReceivedAmount().toPlainString(), TextAlignment.RIGHT, basicFont, blackColor));

			int mergeSpan = isBankPayment ? 5 : 3;
			itemTable.addCell(new Cell(1, mergeSpan).add(new Paragraph("Total :").simulateBold())
					.setTextAlignment(TextAlignment.RIGHT).setFont(basicFont).setFontSize(10).setFontColor(blackColor)
					.setBorder(new SolidBorder(0.5f)));

			itemTable.addCell(new Cell().add(new Paragraph(dto.getReceivedAmount().toPlainString()).simulateBold())
					.setTextAlignment(TextAlignment.RIGHT).setFont(basicFont).setFontSize(10).setFontColor(blackColor)
					.setBorder(new SolidBorder(0.5f)));

			document.add(itemTable);

			document.add(new Paragraph("Page 1 of 1").setFont(basicFont).setFontSize(9).setFontColor(blackColor)
					.setTextAlignment(TextAlignment.CENTER).setMarginTop(10f));

			document.close();

			String fullUrl = environment.getProperty("ws_image_path") + "/api/download/pdf/vendorpayment" + "/"
					+ receiptName + "_" + datetimeforfile + ".pdf";
			return fullUrl;

		} catch (Exception e) {
			System.out.println("Error generating payment receipt: " + e.getMessage());
			e.printStackTrace();
			return "Failed to generate receipt: " + e.getMessage();
		} finally {
			if (document != null && !document.getPdfDocument().isClosed()) {
				document.close();
			}
		}
	}

	private Cell headerCell(String text, PdfFont font, Color color) {
		return new Cell().add(new Paragraph(text).simulateBold()).setFont(font).setFontSize(10).setFontColor(color)
				.setTextAlignment(TextAlignment.CENTER).setBackgroundColor(new DeviceRgb(240, 240, 240))
				.setBorder(new SolidBorder(0.5f)).setPadding(4f);
	}

	private Cell dataCell(String text, TextAlignment alignment, PdfFont font, Color color) {
		return new Cell().add(new Paragraph(text == null ? "" : text)).setFont(font).setFontSize(10).setFontColor(color)
				.setTextAlignment(alignment).setBorder(new SolidBorder(0.5f)).setPadding(4f);
	}

	private String safe(String value) {
		return value != null ? value : "";
	}
}
