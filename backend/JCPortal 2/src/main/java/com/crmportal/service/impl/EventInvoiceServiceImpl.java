package com.crmportal.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

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

import com.crmportal.entity.BankDetailsEntity;
import com.crmportal.entity.CashAccountEntity;
import com.crmportal.entity.EventFunctionMasterEntity;
import com.crmportal.entity.EventFunctionQuotationEntity;
import com.crmportal.entity.EventInvoiceEntity;
import com.crmportal.entity.EventInvoiceFunctionItemEntity;
import com.crmportal.entity.EventInvoicePaymentEntity;
import com.crmportal.entity.EventMasterEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.entity.VendorPaymentEntity;
import com.crmportal.mapper.EventFunctionMasterMapper;
import com.crmportal.mapper.EventMasterMapper;
import com.crmportal.repository.BankDetailsRepository;
import com.crmportal.repository.CashAccountRepository;
import com.crmportal.repository.EventFunctionMasterRepository;
import com.crmportal.repository.EventFunctionQuotationItemRepository;
import com.crmportal.repository.EventFunctionQuotationPaymentRepository;
import com.crmportal.repository.EventFunctionQuotationRepository;
import com.crmportal.repository.EventInvoiceFunctionItemRepository;
import com.crmportal.repository.EventInvoicePaymentRepository;
import com.crmportal.repository.EventInvoiceRepository;
import com.crmportal.repository.EventMasterRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.repository.UserUpgradedModuleRepository;
import com.crmportal.repository.VendorPaymentRepository;
import com.crmportal.request.dto.EventFunctionQuotationPaymentResponseDto;
import com.crmportal.request.dto.EventInvoiceFunctionItemsRequestDto;
import com.crmportal.request.dto.EventInvoiceFunctionPaymentRequestDto;
import com.crmportal.request.dto.EventInvoicePaymentResponseDto;
import com.crmportal.request.dto.EventInvoiceRequestDto;
import com.crmportal.response.dto.EventFunctionMasterResponseDto;
import com.crmportal.response.dto.EventFunctionQuotationItemsResponseDto;
import com.crmportal.response.dto.EventInvoiceFunctionItemsResponseDto;
import com.crmportal.response.dto.EventInvoiceResponseDto;
import com.crmportal.response.dto.EventMasterResponseDto;
import com.crmportal.response.dto.SalesInvoiceResponseDto;
import com.crmportal.service.CommonService;
import com.crmportal.service.EventInvoiceService;
import com.crmportal.service.SalesInvoiceService;

@Service
public class EventInvoiceServiceImpl implements EventInvoiceService {

	@Autowired
	EventMasterRepository eventMasterRepository;

	@Autowired
	EventInvoiceRepository eventInvoiceRepository;

	@Autowired
	SalesInvoiceService salesInvoiceService;

	@Autowired
	EventInvoiceFunctionItemRepository eventInvoiceFunctionItemRepository;

	@Autowired
	CommonService commonService;

	@Autowired
	EventMasterMapper eventMasterMapper;

	@Autowired
	UserMasterRepository userMasterRepository;

	@Autowired
	EventInvoicePaymentRepository eventInvoicePaymentRepository;

	@Autowired
	EventFunctionQuotationRepository eventFunctionQuotationRepository;

	@Autowired
	EventFunctionQuotationItemRepository eventFunctionQuotationItemRepository;

	@Autowired
	EventFunctionQuotationPaymentRepository eventFunctionQuotationPaymentRepository;

	@Autowired
	EventFunctionMasterRepository eventFunctionMasterRepository;

	@Autowired
	EventFunctionMasterMapper eventFunctionMasterMapper;

	@Autowired
	Environment environment;

	@Autowired
	BankDetailsRepository bankDetailsRepository;

	@Autowired
	CashAccountRepository cashAccountRepository;

	@Autowired
	VendorPaymentRepository vendorPaymentRepository;

	@Autowired
	UserUpgradedModuleRepository userUpgradedModuleRepository;

	private static final DateTimeFormatter FUNCTION_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a",
			Locale.ENGLISH);

	private static final DateTimeFormatter CREATE_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a",
			Locale.ENGLISH);

	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	@Override
	@Transactional
	public EventInvoiceResponseDto addOrUpdateEventInvoice(@Valid EventInvoiceRequestDto request, long id) {

		EventMasterEntity eventMaster = eventMasterRepository.findByIdAndIsDeleteFalse(request.getEventId())
				.orElseThrow(() -> new RuntimeException(
						"Event Master with this eventId not found: " + request.getEventId()));

		UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(request.getUserId())
				.orElseThrow(() -> new RuntimeException("User not found with id: " + request.getUserId()));

		List<EventInvoiceEntity> invList = eventInvoiceRepository.findByEvent_Id(request.getEventId());

		boolean isInvoiceExist = eventInvoiceRepository.existsByEvent(eventMaster);

		EventInvoiceEntity invoiceEntity;
		if (id == -1 && invList.size() == 0) {
			invoiceEntity = new EventInvoiceEntity();
		} else if (id == -1 && invList.size() != 0) {
			if (isInvoiceExist) {

				EventInvoiceEntity oldInvoice = eventInvoiceRepository.findByEventAndIsDeleteFalse(eventMaster);
				eventInvoicePaymentRepository.deleteAllByEventInvoice(oldInvoice);
				eventInvoiceFunctionItemRepository.deleteAllByEventInvoice(oldInvoice);
				eventInvoiceRepository.deleteByEvent(eventMaster);
			}
			invoiceEntity = new EventInvoiceEntity();
		} else {
			invoiceEntity = eventInvoiceRepository.findByIdAndIsDeleteFalse(id);
			if (invoiceEntity == null) {
				throw new RuntimeException("EventFunctionQuotation with id " + id + " not found");
			}
			invoiceEntity.setUpdatedAt(commonService.getCurrentDateTime());
		}

		invoiceEntity.setInvoiceCode(request.getInvoiceCode());
		invoiceEntity.setEvent(eventMaster);
		invoiceEntity.setUser(user);
		invoiceEntity.setTotalAmount(request.getTotalAmount());
		invoiceEntity.setCgst(request.getCgst());
		invoiceEntity.setCgstAmnt(request.getCgstAmnt());
		invoiceEntity.setSgst(request.getSgst());
		invoiceEntity.setSgstAmnt(request.getSgstAmnt());
		invoiceEntity.setIgst(request.getIgst());
		invoiceEntity.setIgstAmnt(request.getIgstAmnt());
		invoiceEntity.setDiscount(request.getDiscount());
		invoiceEntity.setRoundOff(request.getRoundOff());
		invoiceEntity.setSubTotal(request.getSubTotal());
		invoiceEntity.setGrandTotal(request.getGrandTotal());
		invoiceEntity.setRemainingAmount(request.getRemainingAmount());
		invoiceEntity.setBillingname(request.getBillingname());
		invoiceEntity.setBillingaddress(request.getBillingaddress());
		invoiceEntity.setShipname(request.getShipname());
		invoiceEntity.setShipaddress(request.getShipaddress());
		invoiceEntity.setGstnumber(request.getGstnumber());
		invoiceEntity.setChequePayment(request.getChequePayment());
		invoiceEntity.setCashPayment(request.getCashPayment());
		invoiceEntity.setFoodTax(request.getFoodTax());
		invoiceEntity.setFoodTaxAmount(request.getFoodTaxAmount());
		invoiceEntity.setFoodTaxTotalAmount(request.getFoodTaxTotalAmount());
		invoiceEntity.setServiceTax(request.getServiceTax());
		invoiceEntity.setServiceTaxAmount(request.getServiceTaxAmount());
		invoiceEntity.setServiceTaxTotalAmount(request.getServiceTaxTotalAmount());
		invoiceEntity.setVatTax(request.getVatTax());
		invoiceEntity.setVatTaxAmount(request.getVatTaxAmount());
		invoiceEntity.setVatTaxTotalAmount(request.getVatTaxTotalAmount());
		invoiceEntity.setDiscountPct(request.getDiscountPct());
		invoiceEntity.setIsDiscountPercent(request.getIsDiscountPercent());

		if (!request.getDuedate().equals("")) {
			try {
				invoiceEntity.setDuedate(LocalDate.parse(request.getDuedate(), formatter));
			} catch (Exception e) {
				invoiceEntity.setDuedate(null);
				;
			}
		}
		invoiceEntity.setNotes(request.getNotes());
		invoiceEntity = eventInvoiceRepository.save(invoiceEntity);

		List<EventInvoiceFunctionPaymentRequestDto> paymentRequestDtos = request.getEventInvoiceFunctionPayments();

		if (paymentRequestDtos != null) {
			for (EventInvoiceFunctionPaymentRequestDto eventInvPay : paymentRequestDtos) {

				EventInvoicePaymentEntity paymentEntity;

				BankDetailsEntity bankDetailsEntity = null;

				if (eventInvPay.getBankId() != null) {
					bankDetailsEntity = bankDetailsRepository.findByIdAndIsDeleteFalse(eventInvPay.getBankId())
							.orElseThrow(() -> new RuntimeException(
									"Bank Details Not Found with id:- " + eventInvPay.getBankId()));
				}

				CashAccountEntity cashAccountEntity = null;

				if (eventInvPay.getCashAccountId() != null) {
					cashAccountEntity = cashAccountRepository.findByIdAndIsDeleteFalse(eventInvPay.getCashAccountId())
							.orElseThrow(() -> new RuntimeException(
									"Cash Account not found with id : " + eventInvPay.getCashAccountId()));
				}

				BigInteger advancePayment = eventInvPay.getAdvancePayment();

				BigDecimal advancePaymentAmount = advancePayment != null ? new BigDecimal(advancePayment)
						: BigDecimal.ZERO;

				String requestVendorCode = eventInvPay.getVendorCode();

				boolean hasRequestVendorCode = requestVendorCode != null && !requestVendorCode.trim().isEmpty();

				boolean hasAdvancePayment = advancePayment != null && advancePayment.compareTo(BigInteger.ZERO) > 0;

				boolean vendorModuleActive = userUpgradedModuleRepository
						.existsByUserIdAndUpgradeModuleIdAndIsActiveTrueAndIsDeleteFalse(user.getId(), 1L);

				if (eventInvPay.getId() == null || eventInvPay.getId() == 0) {

					paymentEntity = new EventInvoicePaymentEntity();

					if (vendorModuleActive && hasAdvancePayment) {

						// -------------------------------------------------
						if (!hasRequestVendorCode) {

							VendorPaymentEntity vendorPaymentEntity = new VendorPaymentEntity();

							vendorPaymentEntity.setEventId(eventMaster.getId());

							vendorPaymentEntity.setUser(user);

							vendorPaymentEntity.setInvoiceCode(commonService.generateVendorInvoiceCode(user.getId()));

							vendorPaymentEntity.setVendorId(eventMaster.getParty().getId());

							vendorPaymentEntity.setVendorCat("User");

							vendorPaymentEntity.setIsOpb(false);

							vendorPaymentEntity.setIsPayable(false);

							if (eventInvPay.getAdvancePaymentDate() != null
									&& !eventInvPay.getAdvancePaymentDate().trim().isEmpty()) {

								vendorPaymentEntity.setPaymentDate(LocalDateTime
										.parse(eventInvPay.getAdvancePaymentDate().trim(), FUNCTION_DATE_FORMATTER)
										.toLocalDate());
							} else {
								vendorPaymentEntity.setPaymentDate(null);
							}

							vendorPaymentEntity.setReceivedAmount(advancePaymentAmount);

							vendorPaymentEntity.setPayAmount(BigDecimal.ZERO);

							vendorPaymentEntity.setSettlementAmount(BigDecimal.ZERO);

							vendorPaymentEntity.setReferenceId("");

							vendorPaymentEntity.setRemarks(eventInvPay.getAdvancePaymentNotes());

							vendorPaymentEntity.setBankId(bankDetailsEntity != null ? bankDetailsEntity.getId() : null);

							vendorPaymentEntity
									.setCashAccountId(cashAccountEntity != null ? cashAccountEntity.getId() : null);

							vendorPaymentEntity.setPaymentMode(eventInvPay.getPaymentMode());

							vendorPaymentEntity = vendorPaymentRepository.save(vendorPaymentEntity);

							paymentEntity.setVendorCode(vendorPaymentEntity.getInvoiceCode());
						}

						else {

							VendorPaymentEntity vendorPaymentEntity = vendorPaymentRepository
									.findByInvoiceCodeAndVendorIdAndIsDeleteFalse(requestVendorCode.trim(),
											eventMaster.getParty().getId());

							if (vendorPaymentEntity == null) {
								throw new RuntimeException(
										"Vendor Payment not found with invoice code: " + requestVendorCode);
							}

							vendorPaymentEntity.setReceivedAmount(advancePaymentAmount);

							vendorPaymentEntity.setRemarks(eventInvPay.getAdvancePaymentNotes());

							vendorPaymentEntity.setPaymentMode(eventInvPay.getPaymentMode());

							vendorPaymentEntity.setBankId(bankDetailsEntity != null ? bankDetailsEntity.getId() : null);

							vendorPaymentEntity
									.setCashAccountId(cashAccountEntity != null ? cashAccountEntity.getId() : null);

							vendorPaymentRepository.save(vendorPaymentEntity);

							paymentEntity.setVendorCode(requestVendorCode.trim());
						}
					}
				} else {

					paymentEntity = eventInvoicePaymentRepository.findByIdAndIsDeleteFalse(eventInvPay.getId());

					if (paymentEntity == null) {
						throw new RuntimeException("Quotation Payment with id " + eventInvPay.getId() + " not found");
					}

					paymentEntity.setUpdatedAt(commonService.getCurrentDateTime());

					BigInteger oldAdvancePayment = paymentEntity.getAdvancePayment();

					BigInteger newAdvancePayment = eventInvPay.getAdvancePayment();

					boolean advancePaymentChanged = !Objects.equals(oldAdvancePayment, newAdvancePayment);

					String existingVendorCode = paymentEntity.getVendorCode();

					boolean hasExistingVendorCode = existingVendorCode != null && !existingVendorCode.trim().isEmpty();

					if (vendorModuleActive && hasAdvancePayment) {

						if (!hasExistingVendorCode) {

							VendorPaymentEntity vendorPaymentEntity = new VendorPaymentEntity();

							vendorPaymentEntity.setEventId(eventMaster.getId());

							vendorPaymentEntity.setUser(user);

							vendorPaymentEntity.setInvoiceCode(commonService.generateVendorInvoiceCode(user.getId()));

							vendorPaymentEntity.setVendorId(eventMaster.getParty().getId());

							vendorPaymentEntity.setVendorCat("User");

							vendorPaymentEntity.setIsOpb(false);

							vendorPaymentEntity.setIsPayable(false);

							if (eventInvPay.getAdvancePaymentDate() != null
									&& !eventInvPay.getAdvancePaymentDate().trim().isEmpty()) {

								vendorPaymentEntity.setPaymentDate(LocalDateTime
										.parse(eventInvPay.getAdvancePaymentDate().trim(), FUNCTION_DATE_FORMATTER)
										.toLocalDate());
							} else {
								vendorPaymentEntity.setPaymentDate(null);
							}

							vendorPaymentEntity.setReceivedAmount(advancePaymentAmount);

							vendorPaymentEntity.setPayAmount(BigDecimal.ZERO);

							vendorPaymentEntity.setSettlementAmount(BigDecimal.ZERO);

							vendorPaymentEntity.setReferenceId("");

							vendorPaymentEntity.setRemarks(eventInvPay.getAdvancePaymentNotes());

							vendorPaymentEntity.setBankId(bankDetailsEntity != null ? bankDetailsEntity.getId() : null);

							vendorPaymentEntity
									.setCashAccountId(cashAccountEntity != null ? cashAccountEntity.getId() : null);

							vendorPaymentEntity.setPaymentMode(eventInvPay.getPaymentMode());

							vendorPaymentEntity = vendorPaymentRepository.save(vendorPaymentEntity);

							paymentEntity.setVendorCode(vendorPaymentEntity.getInvoiceCode());
						}

						else if (advancePaymentChanged) {

							VendorPaymentEntity vendorPaymentEntity = vendorPaymentRepository
									.findByInvoiceCodeAndVendorIdAndIsDeleteFalse(existingVendorCode.trim(),
											eventMaster.getParty().getId());

							if (vendorPaymentEntity == null) {
								throw new RuntimeException(
										"Vendor Payment not found with invoice code: " + existingVendorCode);
							}

							vendorPaymentEntity.setReceivedAmount(advancePaymentAmount);

							vendorPaymentEntity.setRemarks(eventInvPay.getAdvancePaymentNotes());

							vendorPaymentEntity.setPaymentMode(eventInvPay.getPaymentMode());

							vendorPaymentEntity.setBankId(bankDetailsEntity != null ? bankDetailsEntity.getId() : null);

							vendorPaymentEntity
									.setCashAccountId(cashAccountEntity != null ? cashAccountEntity.getId() : null);

							vendorPaymentRepository.save(vendorPaymentEntity);
						}
					}
				}

				if (eventInvPay.getAdvancePaymentDate() != null
						&& !eventInvPay.getAdvancePaymentDate().trim().isEmpty()) {

					paymentEntity.setAdvancePaymentDate(
							LocalDateTime.parse(eventInvPay.getAdvancePaymentDate().trim(), FUNCTION_DATE_FORMATTER));
				} else {
					paymentEntity.setAdvancePaymentDate(null);
				}

				paymentEntity.setAdvancePayment(eventInvPay.getAdvancePayment());

				paymentEntity.setAdvancePaymentNotes(eventInvPay.getAdvancePaymentNotes());

				paymentEntity.setEvent(eventMaster);

				paymentEntity.setEventInvoice(invoiceEntity);

				paymentEntity.setBankId(bankDetailsEntity != null ? bankDetailsEntity.getId() : null);

				paymentEntity.setCashAccountId(cashAccountEntity != null ? cashAccountEntity.getId() : null);

				paymentEntity.setPaymentMode(eventInvPay.getPaymentMode());

				paymentEntity.setUser(user);

				eventInvoicePaymentRepository.save(paymentEntity);

				updateAccountBalance(bankDetailsEntity, cashAccountEntity, advancePaymentAmount, BigDecimal.ZERO);
			}
		}
		eventInvoiceFunctionItemRepository.deleteAllByEventInvoice(invoiceEntity);
		List<EventInvoiceFunctionItemsRequestDto> items = request.getInvoiceFunctionItems();
		if (items != null) {
			for (EventInvoiceFunctionItemsRequestDto itemDto : items) {
				EventInvoiceFunctionItemEntity itemEntity = new EventInvoiceFunctionItemEntity();

				itemEntity.setFunctionName(itemDto.getFunctionName());
				itemEntity.setPax(itemDto.getPax());
				itemEntity.setExtraPax(itemDto.getExtraPax());
				itemEntity.setIsEventFunction(itemDto.getIsEventFunction());
				itemEntity
						.setOfferedRate(itemDto.getOfferedRate() != null ? itemDto.getOfferedRate() : BigDecimal.ZERO);
				itemEntity.setRatePerPlate(
						itemDto.getRatePerPlate() != null ? itemDto.getRatePerPlate() : BigDecimal.ZERO);
				itemEntity.setAmount(itemDto.getAmount() != null ? itemDto.getAmount() : BigDecimal.ZERO);
				itemEntity.setIsExtraCharges(itemDto.getIsExtraCharges());
				itemEntity.setExtraChargesId(itemDto.getExtraChargesId());
				if (itemDto.getFunctionDate() != null && !itemDto.getFunctionDate().trim().isEmpty()) {
					itemEntity.setFunctionDate(
							LocalDateTime.parse(itemDto.getFunctionDate().trim(), FUNCTION_DATE_FORMATTER));
				} else {
					itemEntity.setFunctionDate(null);
				}
				itemEntity.setExtraTax(itemDto.getExtraTax() != null ? itemDto.getExtraTax() : BigDecimal.ZERO);
				itemEntity.setTaxRate(itemDto.getTaxRate() != null ? itemDto.getTaxRate() : BigDecimal.ZERO);

				itemEntity.setEventInvoice(invoiceEntity);
				eventInvoiceFunctionItemRepository.save(itemEntity);
			}
		}

		EventMasterResponseDto responseDto = eventMasterMapper.entityToResponse(eventMaster);

		EventInvoiceResponseDto response = new EventInvoiceResponseDto();
		response.setTotalAmount(invoiceEntity.getTotalAmount());
		response.setEvent(responseDto);
		response.setCgst(invoiceEntity.getCgst());
		response.setCgstAmnt(invoiceEntity.getCgstAmnt());
		response.setSgst(invoiceEntity.getSgst());
		response.setSgstAmnt(invoiceEntity.getSgstAmnt());
		response.setIgst(invoiceEntity.getIgst());
		response.setIgstAmnt(invoiceEntity.getIgstAmnt());
		response.setDiscount(invoiceEntity.getDiscount());
		response.setRoundOff(invoiceEntity.getRoundOff());
		response.setGrandTotal(invoiceEntity.getGrandTotal());
		response.setRemainingAmount(invoiceEntity.getRemainingAmount());
		response.setNotes(invoiceEntity.getNotes());
		response.setSubTotal(invoiceEntity.getSubTotal());
		response.setInvoiceCode(invoiceEntity.getInvoiceCode());
		response.setBillingname(invoiceEntity.getBillingname());
		response.setBillingaddress(invoiceEntity.getBillingaddress());
		response.setShipname(invoiceEntity.getShipname());
		response.setShipaddress(invoiceEntity.getShipaddress());
		response.setGstnumber(invoiceEntity.getGstnumber());
		response.setChequePayment(invoiceEntity.getChequePayment());
		response.setCashPayment(invoiceEntity.getCashPayment());
		response.setFoodTax(invoiceEntity.getFoodTax());
		response.setFoodTaxAmount(invoiceEntity.getFoodTaxAmount());
		response.setFoodTaxTotalAmount(invoiceEntity.getFoodTaxTotalAmount());
		response.setServiceTax(invoiceEntity.getServiceTax());
		response.setServiceTaxAmount(invoiceEntity.getServiceTaxAmount());
		response.setServiceTaxTotalAmount(invoiceEntity.getServiceTaxTotalAmount());
		response.setVatTax(invoiceEntity.getVatTax());
		response.setVatTaxAmount(invoiceEntity.getVatTaxAmount());
		response.setVatTaxTotalAmount(invoiceEntity.getVatTaxTotalAmount());
		response.setIsDiscountPercent(invoiceEntity.getIsDiscountPercent());
		try {
			response.setDuedate(invoiceEntity.getDuedate() != null ? invoiceEntity.getDuedate().format(formatter) : "");
		} catch (Exception e) {
			response.setDuedate("");
		}
		response.setCreatedAt(formatDateTime(invoiceEntity.getCreatedAt(), FUNCTION_DATE_FORMATTER).split(" ")[0]);
		return response;
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

	public String generateInvoiceNo(Long userId) {
		String prefix = "INV-";
		String lastInvoiceNo = eventInvoiceRepository.findLastInvoiceNo(userId);

		int nextNumber = 1;

		if (lastInvoiceNo != null && lastInvoiceNo.startsWith(prefix)) {
			try {
				// Extract numeric part → "0000001" → 1
				String numberPart = lastInvoiceNo.substring(prefix.length());
				nextNumber = Integer.parseInt(numberPart) + 1;
			} catch (NumberFormatException e) {
				nextNumber = 1; // fallback in case of invalid format
			}
		}

		// Format: QT-0000001
		String newInvoiceCode = "";

		// Keep generating until a unique one is found
		do {
			newInvoiceCode = String.format("%s%07d", prefix, nextNumber);
			nextNumber++;
		} while (eventInvoiceRepository.existsByInvoiceCode(newInvoiceCode));

		return newInvoiceCode;
	}

	@Override
	public Boolean deleteByInvoiceItemId(Long invoiceItemId) {
		EventInvoiceEntity eventInvoiceEntity = eventInvoiceRepository.findByIdAndIsDeleteFalse(invoiceItemId);
		if (eventInvoiceEntity != null) {
			eventInvoiceEntity.setIsDelete(true);
			eventInvoiceRepository.save(eventInvoiceEntity);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public EventInvoiceResponseDto getEventInvoiceByEventId(Long eventId, Boolean isDecore) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a");

		EventMasterEntity eventMaster = eventMasterRepository.findByIdAndIsDeleteFalse(eventId)
				.orElseThrow(() -> new RuntimeException("Event Master with this eventId not found: " + eventId));

		EventInvoiceEntity eventInvoice = eventInvoiceRepository.findByEventAndIsDeleteFalse(eventMaster);

		EventFunctionQuotationEntity eventQuotation = eventFunctionQuotationRepository
				.findByEventAndIsDeleteFalseAndIsDecore(eventMaster, isDecore);

		if (eventInvoice == null) {
			eventInvoice = new EventInvoiceEntity();

			eventInvoice.setEvent(eventMaster);
			eventInvoice.setIsDelete(false);
			eventInvoice.setTotalAmount(BigDecimal.ZERO);
			eventInvoice.setCgst("");
			eventInvoice.setCgstAmnt(BigDecimal.ZERO);
			eventInvoice.setSgst("");
			eventInvoice.setSgstAmnt(BigDecimal.ZERO);
			eventInvoice.setIgst("");
			eventInvoice.setIgstAmnt(BigDecimal.ZERO);
			eventInvoice.setDiscount(BigDecimal.ZERO);
			eventInvoice.setRoundOff(BigInteger.ZERO);
			eventInvoice.setGrandTotal(BigInteger.ZERO);
			eventInvoice.setSubTotal(BigInteger.ZERO);
			eventInvoice.setRemainingAmount(BigInteger.ZERO);
			eventInvoice.setInvoiceCode(null);
			eventInvoice.setBillingname(eventMaster.getParty().getNameEnglish());
			eventInvoice.setBillingaddress(eventMaster.getAddress());
			eventInvoice.setShipname(eventMaster.getParty().getNameEnglish());
			eventInvoice.setShipaddress(eventMaster.getAddress());
			eventInvoice.setGstnumber("");
			eventInvoice.setDuedate(null);
			eventInvoice.setNotes("");
			eventInvoice.setCashPayment(BigDecimal.ZERO);
			eventInvoice.setChequePayment(BigDecimal.ZERO);
			eventInvoice.setFoodTax("");
			eventInvoice.setFoodTaxAmount(BigInteger.ZERO);
			eventInvoice.setFoodTaxTotalAmount(BigInteger.ZERO);
			eventInvoice.setServiceTax("");
			eventInvoice.setServiceTaxAmount(BigInteger.ZERO);
			eventInvoice.setServiceTaxTotalAmount(BigInteger.ZERO);
			eventInvoice.setVatTax("");
			eventInvoice.setVatTaxAmount(BigInteger.ZERO);
			eventInvoice.setVatTaxTotalAmount(BigInteger.ZERO);
			eventInvoice.setDiscountPct("");
			eventInvoice.setIsDiscountPercent(false);
			eventInvoice = eventInvoiceRepository.save(eventInvoice);

		}

		EventMasterResponseDto responseDto = eventMasterMapper.entityToResponse(eventMaster);

		List<EventFunctionMasterEntity> eventFunctions = eventFunctionMasterRepository
				.findAllByEventIdAndIsDeleteFalseOrderBySortorderAsc(eventId);
		List<EventFunctionMasterResponseDto> eventFunction = new ArrayList<>();
		for (EventFunctionMasterEntity eventFunctionMasterEntity : eventFunctions) {
			EventFunctionMasterResponseDto eventFunctionMasterResponseDto = eventFunctionMasterMapper
					.entityToResponse(eventFunctionMasterEntity);
			eventFunction.add(eventFunctionMasterResponseDto);
		}

		eventFunction.forEach(eventFunc -> {
			String start = eventFunc.getFunctionStartDateTime();
			String end = eventFunc.getFunctionEndDateTime();

			if (start != null && !start.isEmpty()) {
				try {
					LocalDateTime dateStartTime = LocalDateTime.parse(start);
					LocalDateTime dateEndTime = LocalDateTime.parse(end);

					eventFunc.setFunctionStartDateTime(dateTimeFormatter.format(dateStartTime));
					eventFunc.setFunctionEndDateTime(dateTimeFormatter.format(dateEndTime));

				} catch (Exception e) {
				}
			}
		});

		responseDto.setEventFunctions(eventFunction);

		List<EventInvoicePaymentResponseDto> paymentResponseDtos = new ArrayList<>();
		List<EventInvoiceFunctionItemsResponseDto> items = new ArrayList<>();
		if (eventInvoice != null) {
			eventInvoicePaymentRepository.findAllByEventInvoiceAndIsDeleteFalse(eventInvoice).forEach(payment -> {
				EventInvoicePaymentResponseDto eventInvoicePaymentResponseDto = new EventInvoicePaymentResponseDto();
				eventInvoicePaymentResponseDto.setId(payment.getId());
				eventInvoicePaymentResponseDto.setAdvancePaymentDate(
						formatDateTime(payment.getAdvancePaymentDate(), FUNCTION_DATE_FORMATTER));
				eventInvoicePaymentResponseDto.setAdvancePayment(payment.getAdvancePayment());
				eventInvoicePaymentResponseDto.setAdvancePaymentNotes(payment.getAdvancePaymentNotes());
				eventInvoicePaymentResponseDto.setBankId(payment.getBankId());
				eventInvoicePaymentResponseDto.setCashAccountId(payment.getCashAccountId());
				eventInvoicePaymentResponseDto.setPaymentMode(payment.getPaymentMode());
				eventInvoicePaymentResponseDto.setVendorCode(payment.getVendorCode());
				paymentResponseDtos.add(eventInvoicePaymentResponseDto);
			});

			eventInvoiceFunctionItemRepository.findAllByEventInvoiceAndIsDeleteFalse(eventInvoice).forEach(item -> {
				EventInvoiceFunctionItemsResponseDto dto = new EventInvoiceFunctionItemsResponseDto();
				dto.setId(item.getId());
				dto.setFunctionName(item.getFunctionName());
				dto.setFunctionDate(formatDateTime(item.getFunctionDate(), dateTimeFormatter));
				dto.setPax(item.getPax());
				dto.setExtraPax(item.getExtraPax());
				dto.setRatePerPlate(item.getRatePerPlate());
				dto.setAmount(item.getAmount());
				dto.setIsEventFunction(item.getIsEventFunction());
				dto.setOfferedRate(item.getOfferedRate());
				dto.setExtraTax(item.getExtraTax());
				dto.setTaxRate(item.getTaxRate());
				dto.setIsExtraCharges(item.getIsExtraCharges());
				dto.setExtraChargesId(item.getExtraChargesId());
				items.add(dto);
			});
		} else if (eventInvoice == null && eventQuotation != null) {
			eventFunctionQuotationPaymentRepository.findAllByEventFunctionQuotationAndIsDeleteFalse(eventQuotation)
					.forEach(payment -> {
						EventInvoicePaymentResponseDto eventFunctionQuotationPaymentResponseDto = new EventInvoicePaymentResponseDto();
						eventFunctionQuotationPaymentResponseDto.setId(0l);
						eventFunctionQuotationPaymentResponseDto.setAdvancePaymentDate(
								formatDateTime(payment.getAdvancePaymentDate(), FUNCTION_DATE_FORMATTER));
						eventFunctionQuotationPaymentResponseDto.setAdvancePayment(payment.getAdvancePayment());
						eventFunctionQuotationPaymentResponseDto
								.setAdvancePaymentNotes(payment.getAdvancePaymentNotes());
						eventFunctionQuotationPaymentResponseDto.setBankId(payment.getBankId());
						eventFunctionQuotationPaymentResponseDto.setCashAccountId(payment.getCashAccountId());
						eventFunctionQuotationPaymentResponseDto.setPaymentMode(payment.getPaymentMode());
						eventFunctionQuotationPaymentResponseDto.setVendorCode(payment.getVendorCode());
						paymentResponseDtos.add(eventFunctionQuotationPaymentResponseDto);
					});

			eventFunctionQuotationItemRepository.findAllByEventFunctionQuotationAndIsDeleteFalse(eventQuotation)
					.forEach(item -> {
						EventInvoiceFunctionItemsResponseDto dto = new EventInvoiceFunctionItemsResponseDto();
						dto.setId(0l);
						dto.setFunctionName(item.getFunctionName());
						dto.setFunctionDate(formatDateTime(item.getFunctionDate(), dateTimeFormatter));
						dto.setPax(item.getPax());
						dto.setExtraPax(item.getExtraPax());
						dto.setRatePerPlate(item.getRatePerPlate());
						dto.setAmount(item.getAmount());
						dto.setIsEventFunction(item.getIsEventFunction());
						dto.setOfferedRate(item.getOfferedRate());
						dto.setExtraTax(item.getExtraTax());
						dto.setTaxRate(item.getTaxRate());
						dto.setIsExtraCharges(item.getIsExtraCharges());
						dto.setExtraChargesId(item.getExtraChargesId());
						items.add(dto);
					});
		}

		EventInvoiceResponseDto response = new EventInvoiceResponseDto();
		response.setEvent(responseDto);
		response.setId(eventInvoice.getId());
		response.setInvoiceFunctionItems(items);
		response.setTotalAmount(eventInvoice.getTotalAmount());
		response.setCgst(eventInvoice.getCgst());
		response.setCgstAmnt(eventInvoice.getCgstAmnt());
		response.setSgst(eventInvoice.getSgst());
		response.setSgstAmnt(eventInvoice.getSgstAmnt());
		response.setIgst(eventInvoice.getIgst());
		response.setIgstAmnt(eventInvoice.getIgstAmnt());
		response.setDiscount(eventInvoice.getDiscount());
		response.setRoundOff(eventInvoice.getRoundOff());
		response.setGrandTotal(eventInvoice.getGrandTotal());
		response.setRemainingAmount(eventInvoice.getRemainingAmount());
		response.setNotes(eventInvoice.getNotes());
		response.setEventInvoicePayments(paymentResponseDtos);
		response.setSubTotal(eventInvoice.getSubTotal());
		response.setInvoiceCode(eventInvoice.getInvoiceCode());
		response.setBillingname(eventInvoice.getBillingname());
		response.setBillingaddress(eventInvoice.getBillingaddress());
		response.setShipname(eventInvoice.getShipname());
		response.setShipaddress(eventInvoice.getShipaddress());
		response.setGstnumber(eventInvoice.getGstnumber());
		response.setCashPayment(eventInvoice.getCashPayment());
		response.setChequePayment(eventInvoice.getChequePayment());
		response.setFoodTax(eventInvoice.getFoodTax());
		response.setFoodTaxAmount(eventInvoice.getFoodTaxAmount());
		response.setFoodTaxTotalAmount(eventInvoice.getFoodTaxTotalAmount());
		response.setServiceTax(eventInvoice.getServiceTax());
		response.setServiceTaxAmount(eventInvoice.getServiceTaxAmount());
		response.setServiceTaxTotalAmount(eventInvoice.getServiceTaxTotalAmount());
		response.setVatTax(eventInvoice.getVatTax());
		response.setVatTaxAmount(eventInvoice.getVatTaxAmount());
		response.setVatTaxTotalAmount(eventInvoice.getVatTaxTotalAmount());
		response.setDiscountPct(eventInvoice.getDiscountPct());
		response.setIsDiscountPercent(eventInvoice.getIsDiscountPercent());
		try {
			response.setDuedate(eventInvoice.getDuedate() != null ? eventInvoice.getDuedate().format(formatter) : "");
		} catch (Exception e) {
			response.setDuedate("");
		}
		response.setCreatedAt(formatDateTime(eventInvoice.getCreatedAt(), FUNCTION_DATE_FORMATTER).split(" ")[0]);

		Map<String, Object> salesinvoiceData = salesInvoiceService.getSalesInvoiceByUserIdAndEventId(eventId);

		List<SalesInvoiceResponseDto> responseDtos = (List<SalesInvoiceResponseDto>) salesinvoiceData.get("data");

		if (responseDtos.isEmpty()) {
			salesinvoiceData.put("due_amount", response.getGrandTotal());
		}

		response.setSalesInvoiceData(salesinvoiceData);

		return response;
	}

	private String formatDateTime(LocalDateTime dateTime, DateTimeFormatter formatter) {
		return dateTime != null ? dateTime.format(formatter).toUpperCase() : "";
	}

	@Override
	public List<EventInvoiceResponseDto> getEventInvoiceByUserId(Long userid, String startDate, String endDate) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a");

		LocalDateTime start = null;
		LocalDateTime end = null;

		if (startDate != null && !startDate.isEmpty()) {
			start = LocalDate.parse(startDate, formatter).atStartOfDay();
		}

		if (endDate != null && !endDate.isEmpty()) {
			end = LocalDate.parse(endDate, formatter).atTime(23, 59, 59);
		}

		List<EventInvoiceResponseDto> eventInvoiceResponseDto = new ArrayList<>();

		// Fetch user
		UserMasterEntity userMaster = userMasterRepository.findByIdAndIsDeleteFalse(userid)
				.orElseThrow(() -> new RuntimeException("User Master with this userId not found: " + userid));

		// Fetch all quotations for this user
		List<EventInvoiceEntity> allInvoices = eventInvoiceRepository.getAllByUserId(userMaster.getId(), start, end);

		// ✅ Group by Party ID manually and keep the latest invoice
		Map<Long, EventInvoiceEntity> latestInvoiceByParty = new HashMap<>();

		for (EventInvoiceEntity invoice : allInvoices) {
			if (invoice.getEvent() != null && invoice.getEvent().getParty() != null) {
				Long partyId = invoice.getEvent().getParty().getId();
				EventInvoiceEntity existing = latestInvoiceByParty.get(partyId);

				if (existing == null) {
					latestInvoiceByParty.put(partyId, invoice);
				} else {
					// Compare dates and keep the latest
					LocalDateTime existingDate = existing.getUpdatedAt() != null ? existing.getUpdatedAt()
							: existing.getCreatedAt();
					LocalDateTime currentDate = invoice.getUpdatedAt() != null ? invoice.getUpdatedAt()
							: invoice.getCreatedAt();

					if (currentDate.isAfter(existingDate)) {
						latestInvoiceByParty.put(partyId, invoice);
					}
				}
			}
		}

		// ✅ Convert to response DTOs
		for (EventInvoiceEntity invoice : latestInvoiceByParty.values()) {

			EventMasterResponseDto eventDto = eventMasterMapper.entityToResponse(invoice.getEvent());

			List<EventInvoicePaymentResponseDto> paymentResponseDtos = new ArrayList<>();

			eventInvoicePaymentRepository.findAllByEventInvoiceAndIsDeleteFalse(invoice).forEach(payment -> {
				EventInvoicePaymentResponseDto eventPaymentResponseDto = new EventInvoicePaymentResponseDto();
				eventPaymentResponseDto.setId(payment.getId());
				eventPaymentResponseDto.setAdvancePaymentDate(
						formatDateTime(payment.getAdvancePaymentDate(), FUNCTION_DATE_FORMATTER));
				eventPaymentResponseDto.setAdvancePayment(payment.getAdvancePayment());
				eventPaymentResponseDto.setAdvancePaymentNotes(payment.getAdvancePaymentNotes());
				eventPaymentResponseDto.setBankId(payment.getBankId());
				eventPaymentResponseDto.setCashAccountId(payment.getCashAccountId());
				eventPaymentResponseDto.setPaymentMode(payment.getPaymentMode());
				eventPaymentResponseDto.setVendorCode(payment.getVendorCode());
				paymentResponseDtos.add(eventPaymentResponseDto);
			});

			// Quotation items
			List<EventInvoiceFunctionItemsResponseDto> items = new ArrayList<>();
			List<EventInvoiceFunctionItemEntity> itemEntities = eventInvoiceFunctionItemRepository
					.findAllByEventInvoiceAndIsDeleteFalse(invoice);

			for (EventInvoiceFunctionItemEntity item : itemEntities) {
				EventInvoiceFunctionItemsResponseDto dto = new EventInvoiceFunctionItemsResponseDto();
				dto.setId(item.getId());
				dto.setFunctionName(item.getFunctionName());
				dto.setFunctionDate(formatDateTime(item.getFunctionDate(), dateTimeFormatter));
				dto.setPax(item.getPax());
				dto.setExtraPax(item.getExtraPax());
				dto.setRatePerPlate(item.getRatePerPlate());
				dto.setAmount(item.getAmount());
				dto.setIsEventFunction(item.getIsEventFunction());
				dto.setOfferedRate(item.getOfferedRate());
				dto.setExtraTax(item.getExtraTax());
				dto.setTaxRate(item.getTaxRate());
				dto.setExtraChargesId(item.getExtraChargesId());
				dto.setIsExtraCharges(item.getIsExtraCharges());
				items.add(dto);
			}

			EventInvoiceResponseDto response = new EventInvoiceResponseDto();
			response.setEvent(eventDto);
			response.setId(invoice.getId());
			response.setInvoiceFunctionItems(items);
			response.setTotalAmount(invoice.getTotalAmount());
			response.setCgst(invoice.getCgst());
			response.setCgstAmnt(invoice.getCgstAmnt());
			response.setSgst(invoice.getSgst());
			response.setSgstAmnt(invoice.getSgstAmnt());
			response.setIgst(invoice.getIgst());
			response.setIgstAmnt(invoice.getIgstAmnt());
			response.setDiscount(invoice.getDiscount());
			response.setRoundOff(invoice.getRoundOff());
			response.setGrandTotal(invoice.getGrandTotal());
			response.setSubTotal(invoice.getSubTotal());
			response.setRemainingAmount(invoice.getRemainingAmount());
			response.setNotes(invoice.getNotes());
			response.setInvoiceCode(invoice.getInvoiceCode());
			response.setBillingname(invoice.getBillingname());
			response.setBillingaddress(invoice.getBillingaddress());
			response.setShipname(invoice.getShipname());
			response.setShipaddress(invoice.getShipaddress());
			response.setGstnumber(invoice.getGstnumber());
			response.setChequePayment(invoice.getChequePayment());
			response.setCashPayment(invoice.getCashPayment());
			response.setFoodTax(invoice.getFoodTax());
			response.setFoodTaxAmount(invoice.getFoodTaxAmount());
			response.setFoodTaxTotalAmount(invoice.getFoodTaxTotalAmount());
			response.setServiceTax(invoice.getServiceTax());
			response.setServiceTaxAmount(invoice.getServiceTaxAmount());
			response.setServiceTaxTotalAmount(invoice.getServiceTaxTotalAmount());
			response.setVatTax(invoice.getVatTax());
			response.setVatTaxAmount(invoice.getVatTaxAmount());
			response.setVatTaxTotalAmount(invoice.getVatTaxTotalAmount());
			response.setIsDiscountPercent(invoice.getIsDiscountPercent());
			try {
				response.setDuedate(invoice.getDuedate() != null ? invoice.getDuedate().format(formatter) : "");
			} catch (Exception e) {
				response.setDuedate("");
			}
			response.setEventInvoicePayments(paymentResponseDtos);
			response.setCreatedAt(formatDateTime(invoice.getCreatedAt(), FUNCTION_DATE_FORMATTER).split(" ")[0]);
			eventInvoiceResponseDto.add(response);
		}

		// ✅ Sort results by latest quotation date (descending)
		Collections.sort(eventInvoiceResponseDto, new Comparator<EventInvoiceResponseDto>() {
			@Override
			public int compare(EventInvoiceResponseDto a, EventInvoiceResponseDto b) {
				EventInvoiceEntity qa = latestInvoiceByParty.get(a.getEvent().getParty().getId());
				EventInvoiceEntity qb = latestInvoiceByParty.get(b.getEvent().getParty().getId());

				LocalDateTime da = qa.getUpdatedAt() != null ? qa.getUpdatedAt() : qa.getCreatedAt();
				LocalDateTime db = qb.getUpdatedAt() != null ? qb.getUpdatedAt() : qb.getCreatedAt();

				return db.compareTo(da); // descending
			}
		});

		return eventInvoiceResponseDto;
	}

	@Override
	public List<EventInvoiceResponseDto> getEventInvoiceByUserIdAndDateWise(Long userid, String startDate,
			String endDate, Boolean isVenue, Long id) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a");

		List<EventInvoiceResponseDto> eventInvoiceList = new ArrayList<>();

		// Fetch user
		UserMasterEntity userMaster = userMasterRepository.findByIdAndIsDeleteFalse(userid)
				.orElseThrow(() -> new RuntimeException("User Master with this userId not found: " + userid));

		List<EventInvoiceEntity> allInvoices = new ArrayList<>();

		if ((startDate == null || startDate.trim().isEmpty()) && (endDate == null || endDate.trim().isEmpty())) {

			if (isVenue == null) {

				// No venue/banquet filter
				allInvoices = eventInvoiceRepository.findAllByUserAndIsDeleteFalseAndEvent_Status(userMaster, 1);

			} else if (Boolean.TRUE.equals(isVenue)) {

				// Venue filter
				allInvoices = eventInvoiceRepository
						.findAllByUserAndIsDeleteFalseAndEvent_StatusAndEvent_Venue_Id(userMaster, 1, id);

			} else {

				// Banquet filter
				allInvoices = eventInvoiceRepository
						.findAllByUserAndIsDeleteFalseAndEvent_StatusAndEvent_BanquetHall_Id(userMaster, 1, id);
			}

		} else {

			DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			LocalDateTime start = LocalDate.parse(startDate, dateFormatter).atStartOfDay();
			LocalDateTime end = LocalDate.parse(endDate, dateFormatter).atTime(23, 59, 59);

			if (isVenue == null) {

				allInvoices = eventInvoiceRepository.findAllByUserAndIsDeleteFalseAndCreatedAtBetween(userMaster, start,
						end);

			} else if (Boolean.TRUE.equals(isVenue)) {

				allInvoices = eventInvoiceRepository
						.findAllByUserAndIsDeleteFalseAndCreatedAtBetweenAndEvent_Venue_Id(userMaster, start, end, id);

			} else {

				allInvoices = eventInvoiceRepository
						.findAllByUserAndIsDeleteFalseAndCreatedAtBetweenAndEvent_BanquetHall_Id(userMaster, start, end,
								id);
			}
		}

		// Group by Party ID manually and keep the latest quotation
		BigInteger overallTotalAmnt = BigInteger.ZERO;
		BigInteger overAllReceivableAmnt = BigInteger.ZERO;
		BigInteger overAllRemainingAmnt = BigInteger.ZERO;
		for (EventInvoiceEntity invoice : allInvoices) {
			overallTotalAmnt = overallTotalAmnt
					.add(invoice.getGrandTotal() != null ? invoice.getGrandTotal() : BigInteger.ZERO);
			overAllReceivableAmnt = overAllReceivableAmnt
					.add(invoice.getTotalAmount() != null ? invoice.getTotalAmount().toBigInteger() : BigInteger.ZERO);
			overAllRemainingAmnt = overAllRemainingAmnt
					.add(invoice.getRemainingAmount() != null ? invoice.getRemainingAmount() : BigInteger.ZERO);
		}

		allInvoices.sort(Comparator.comparing(q -> q.getEvent().getEventStartDateTime(),
				Comparator.nullsLast(Comparator.naturalOrder())));
		// Convert to response DTOs
		for (EventInvoiceEntity invoice : allInvoices) {

			EventMasterResponseDto eventDto = eventMasterMapper.entityToResponse(invoice.getEvent());

			List<EventInvoicePaymentResponseDto> paymentResponseDtos = new ArrayList<>();

			eventInvoicePaymentRepository.findAllByEventInvoiceAndIsDeleteFalse(invoice).forEach(payment -> {
				EventInvoicePaymentResponseDto eventInvoicePayment = new EventInvoicePaymentResponseDto();
				eventInvoicePayment.setId(payment.getId());
				eventInvoicePayment.setAdvancePaymentDate(
						formatDateTime(payment.getAdvancePaymentDate(), FUNCTION_DATE_FORMATTER));
				eventInvoicePayment.setAdvancePayment(payment.getAdvancePayment());
				eventInvoicePayment.setAdvancePaymentNotes(payment.getAdvancePaymentNotes());
				eventInvoicePayment.setBankId(payment.getBankId());
				eventInvoicePayment.setCashAccountId(payment.getCashAccountId());
				eventInvoicePayment.setPaymentMode(payment.getPaymentMode());
				eventInvoicePayment.setVendorCode(payment.getVendorCode());
				paymentResponseDtos.add(eventInvoicePayment);
			});

			// Quotation items
			List<EventInvoiceFunctionItemsResponseDto> items = new ArrayList<>();
			List<EventInvoiceFunctionItemEntity> itemEntities = eventInvoiceFunctionItemRepository
					.findAllByEventInvoiceAndIsDeleteFalse(invoice);

			for (EventInvoiceFunctionItemEntity item : itemEntities) {
				EventInvoiceFunctionItemsResponseDto dto = new EventInvoiceFunctionItemsResponseDto();
				dto.setId(item.getId());
				dto.setFunctionName(item.getFunctionName());
				dto.setFunctionDate(formatDateTime(item.getFunctionDate(), dateTimeFormatter));
				dto.setPax(item.getPax());
				dto.setExtraPax(item.getExtraPax());
				dto.setRatePerPlate(item.getRatePerPlate());
				dto.setAmount(item.getAmount());
				dto.setIsEventFunction(item.getIsEventFunction());
				dto.setOfferedRate(item.getOfferedRate());
				dto.setExtraTax(item.getExtraTax());
				dto.setTaxRate(item.getTaxRate());
				dto.setIsExtraCharges(item.getIsExtraCharges());
				dto.setExtraChargesId(item.getExtraChargesId());
				items.add(dto);
			}

			EventInvoiceResponseDto response = new EventInvoiceResponseDto();
			response.setEvent(eventDto);
			response.setId(invoice.getId());
			response.setInvoiceFunctionItems(items);
			response.setTotalAmount(invoice.getTotalAmount());
			response.setCgst(invoice.getCgst());
			response.setCgstAmnt(invoice.getCgstAmnt());
			response.setSgst(invoice.getSgst());
			response.setSgstAmnt(invoice.getSgstAmnt());
			response.setIgst(invoice.getIgst());
			response.setIgstAmnt(invoice.getIgstAmnt());
			response.setDiscount(invoice.getDiscount());
			response.setRoundOff(invoice.getRoundOff());
			response.setGrandTotal(invoice.getGrandTotal());
			response.setRemainingAmount(invoice.getRemainingAmount());
			response.setInvoiceCode(invoice.getInvoiceCode());
			response.setNotes(invoice.getNotes());
			response.setSubTotal(invoice.getSubTotal());
			response.setBillingname(invoice.getBillingname());
			response.setBillingaddress(invoice.getBillingaddress());
			response.setShipname(invoice.getShipname());
			response.setShipaddress(invoice.getShipaddress());
			response.setGstnumber(invoice.getGstnumber());
			response.setChequePayment(invoice.getChequePayment());
			response.setCashPayment(invoice.getCashPayment());
			response.setFoodTax(invoice.getFoodTax());
			response.setFoodTaxAmount(invoice.getFoodTaxAmount());
			response.setFoodTaxTotalAmount(invoice.getFoodTaxTotalAmount());
			response.setServiceTax(invoice.getServiceTax());
			response.setServiceTaxAmount(invoice.getServiceTaxAmount());
			response.setServiceTaxTotalAmount(invoice.getServiceTaxTotalAmount());
			response.setVatTax(invoice.getVatTax());
			response.setVatTaxAmount(invoice.getVatTaxAmount());
			response.setVatTaxTotalAmount(invoice.getVatTaxTotalAmount());
			response.setIsDiscountPercent(invoice.getIsDiscountPercent());
			try {
				response.setDuedate(invoice.getDuedate() != null ? invoice.getDuedate().format(formatter) : "");
			} catch (Exception e) {
				response.setDuedate("");
			}

			response.setEventInvoicePayments(paymentResponseDtos);
			response.setCreatedAt(formatDateTime(invoice.getCreatedAt(), FUNCTION_DATE_FORMATTER).split(" ")[0]);

			response.setOverallTotalAmnt(overallTotalAmnt);
			response.setOverAllReceivableAmnt(overAllReceivableAmnt);
			response.setOverAllRemainingAmnt(overAllRemainingAmnt);

			Map<String, Object> salesinvoiceData = salesInvoiceService
					.getSalesInvoiceByUserIdAndEventId(invoice.getEvent().getId());

			response.setSalesInvoiceData(salesinvoiceData);

			eventInvoiceList.add(response);
		}

		return eventInvoiceList;
	}

	@Override
	public String invoiceExcel(Long userId, String startDate, String endDate, HttpServletRequest request,
			Boolean isVenue, Long id) {

		try {

			UserMasterEntity userMaster = userMasterRepository.findByIdAndIsDeleteFalse(userId)
					.orElseThrow(() -> new RuntimeException("User not found"));

			List<EventInvoiceEntity> allInvoices;

			if ((startDate == null || startDate.trim().isEmpty()) && (endDate == null || endDate.trim().isEmpty())) {

				if (isVenue == null) {

					// No venue/banquet filter
					allInvoices = eventInvoiceRepository.findAllByUserAndIsDeleteFalseAndEvent_Status(userMaster, 1);

				} else if (Boolean.TRUE.equals(isVenue)) {

					// Venue filter
					allInvoices = eventInvoiceRepository
							.findAllByUserAndIsDeleteFalseAndEvent_StatusAndEvent_Venue_Id(userMaster, 1, id);

				} else {

					// Banquet filter
					allInvoices = eventInvoiceRepository
							.findAllByUserAndIsDeleteFalseAndEvent_StatusAndEvent_BanquetHall_Id(userMaster, 1, id);
				}

			} else {

				DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				LocalDateTime start = LocalDate.parse(startDate, dateFormatter).atStartOfDay();
				LocalDateTime end = LocalDate.parse(endDate, dateFormatter).atTime(23, 59, 59);

				if (isVenue == null) {

					allInvoices = eventInvoiceRepository.findAllByUserAndIsDeleteFalseAndCreatedAtBetween(userMaster,
							start, end);

				} else if (Boolean.TRUE.equals(isVenue)) {

					allInvoices = eventInvoiceRepository
							.findAllByUserAndIsDeleteFalseAndCreatedAtBetweenAndEvent_Venue_Id(userMaster, start, end,
									id);

				} else {

					allInvoices = eventInvoiceRepository
							.findAllByUserAndIsDeleteFalseAndCreatedAtBetweenAndEvent_BanquetHall_Id(userMaster, start,
									end, id);
				}
			}

			XSSFWorkbook workbook = new XSSFWorkbook();
			XSSFSheet sheet = workbook.createSheet("Invoice Report");

			int rowNum = 0;

			// Title
			Row titleRow = sheet.createRow(rowNum++);
			titleRow.createCell(0).setCellValue("Invoice Report");
			sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 7));

			// Summary Header
			Row summaryHeader = sheet.createRow(rowNum++);
			summaryHeader.createCell(0).setCellValue("Total Receivable");
			summaryHeader.createCell(3).setCellValue("Total Remaining");
			summaryHeader.createCell(6).setCellValue("Total Invoice Amount");

			BigInteger totalInvoiceAmount = BigInteger.ZERO;
			BigInteger totalReceivedAmount = BigInteger.ZERO;
			BigInteger totalRemainingAmount = BigInteger.ZERO;

			for (EventInvoiceEntity invoice : allInvoices) {

				totalInvoiceAmount = totalInvoiceAmount
						.add(invoice.getGrandTotal() != null ? invoice.getGrandTotal() : BigInteger.ZERO);

				totalReceivedAmount = totalReceivedAmount.add(
						invoice.getTotalAmount() != null ? invoice.getTotalAmount().toBigInteger() : BigInteger.ZERO);

				totalRemainingAmount = totalRemainingAmount
						.add(invoice.getRemainingAmount() != null ? invoice.getRemainingAmount() : BigInteger.ZERO);
			}

			// Summary Values
			Row summaryValue = sheet.createRow(rowNum++);
			summaryValue.createCell(0).setCellValue(totalReceivedAmount.toString());
			summaryValue.createCell(3).setCellValue(totalRemainingAmount.toString());
			summaryValue.createCell(6).setCellValue(totalInvoiceAmount.toString());

			rowNum++;

			// Table Header
			Row headerRow = sheet.createRow(rowNum++);

			String[] headers = { "Sr No#", "Customer Name", "Event Name", "Event Date", "Invoice Date",
					"Received Amount", "Remaining Amount", "Grand Total" };

			for (int i = 0; i < headers.length; i++) {
				headerRow.createCell(i).setCellValue(headers[i]);
			}

			int srNo = 1;

			DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");

			for (EventInvoiceEntity invoice : allInvoices) {
				boolean allZero = invoice.getTotalAmount() != null && invoice.getRemainingAmount() != null
						&& invoice.getGrandTotal() != null && invoice.getTotalAmount().compareTo(BigDecimal.ZERO) == 0
						&& invoice.getRemainingAmount().compareTo(BigInteger.ZERO) == 0
						&& invoice.getGrandTotal().compareTo(BigInteger.ZERO) == 0;

				if (!allZero) {
					Row row = sheet.createRow(rowNum++);

					row.createCell(0).setCellValue(srNo++);

					row.createCell(1)
							.setCellValue(invoice.getEvent().getParty().getNameEnglish() != null
									? invoice.getEvent().getParty().getNameEnglish()
									: "");

					row.createCell(2)
							.setCellValue(invoice.getEvent().getEventType().getNameEnglish() != null
									? invoice.getEvent().getEventType().getNameEnglish()
									: "");

					row.createCell(3)
							.setCellValue(invoice.getEvent().getEventStartDateTime() != null
									? invoice.getEvent().getEventStartDateTime().format(dateFormat)
									: "");

					row.createCell(4).setCellValue(
							invoice.getCreatedAt() != null ? invoice.getCreatedAt().format(dateFormat) : "");

					row.createCell(5).setCellValue(
							invoice.getTotalAmount() != null ? invoice.getTotalAmount().doubleValue() : 0);

					row.createCell(6).setCellValue(
							invoice.getRemainingAmount() != null ? invoice.getRemainingAmount().doubleValue() : 0);

					row.createCell(7)
							.setCellValue(invoice.getGrandTotal() != null ? invoice.getGrandTotal().doubleValue() : 0);
				}
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

			String fileName = "invoice-report-" + System.currentTimeMillis() + ".xlsx";

			File file = new File(dir, fileName);

			try (FileOutputStream fos = new FileOutputStream(file)) {
				workbook.write(fos);
			}

			workbook.close();

			return environment.getProperty("ws_image_path") + "/api/download/excel/" + folderName + "/" + fileName;

		} catch (Exception e) {
			throw new RuntimeException("Error generating invoice excel", e);
		}
	}

	@Override
	public String getInvoiceCode(Long userId) {
		return generateInvoiceNo(userId);
	}
}
