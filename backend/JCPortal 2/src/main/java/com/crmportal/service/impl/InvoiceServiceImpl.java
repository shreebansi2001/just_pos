package com.crmportal.service.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.crmportal.entity.BankDetailsEntity;
import com.crmportal.entity.CashAccountEntity;
import com.crmportal.entity.InvoiceEntity;
import com.crmportal.entity.InvoiceItemsEntity;
import com.crmportal.entity.InvoicePaymentHistoryEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.enums.AccountType;
import com.crmportal.enums.EntryType;
import com.crmportal.enums.FileType;
import com.crmportal.enums.ModuleName;
import com.crmportal.enums.PaymentMode;
import com.crmportal.enums.TaxType;
import com.crmportal.mapper.BankDetailsMapper;
import com.crmportal.mapper.InvoiceItemsMapper;
import com.crmportal.mapper.InvoiceMapper;
import com.crmportal.mapper.InvoicePaymentHistoryMapper;
import com.crmportal.mapper.PlanInfomationMapper;
import com.crmportal.repository.BankDetailsRepository;
import com.crmportal.repository.CashAccountRepository;
import com.crmportal.repository.InvoiceEntityRepository;
import com.crmportal.repository.InvoiceItemsRepository;
import com.crmportal.repository.InvoicePaymentHistoryRepository;
import com.crmportal.repository.PaymentInfoRepository;
import com.crmportal.repository.PlanInformationEntityRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.repository.UserPlansHistoryRepository;
import com.crmportal.request.dto.AccountEntryRequestDto;
import com.crmportal.request.dto.InvoicePaymentHistoryRequestDto;
import com.crmportal.request.dto.InvoiceRequestDTO;
import com.crmportal.response.dto.AccountEntryResponseDto;
import com.crmportal.response.dto.AllInvoiceResponseDto;
import com.crmportal.response.dto.BankDetailsResponseDto;
import com.crmportal.response.dto.CurrentMonthPaidInvoicesResponseDto;
import com.crmportal.response.dto.InvoicePaymentHistoryResponseDto;
import com.crmportal.response.dto.InvoiceWisePaymentHistoryResponseDto;
import com.crmportal.response.dto.SuperAdminInvoiceItemsResponseDto;
import com.crmportal.response.dto.SuperAdminInvoiceResponseDto;
import com.crmportal.service.AccountEntryService;
import com.crmportal.service.CommonService;
import com.crmportal.service.InvoiceService;
import com.crmportal.service.UserFileService;

@Service
public class InvoiceServiceImpl implements InvoiceService {

	@Autowired
	private InvoiceEntityRepository invoiceEntityRepository;

	@Autowired
	UserPlansHistoryRepository plansHistoryRepository;

	@Autowired
	UserMasterRepository userMasterRepository;

	@Autowired
	PaymentInfoRepository paymentInfoRepository;

	@Autowired
	InvoiceMapper invoiceMapper;

	@Autowired
	PlanInformationEntityRepository informationEntityRepository;

	@Autowired
	PlanInfomationMapper PlanInfomationMapper;

	@Autowired
	CommonService commonService;

	@Autowired
	UserFileService userFileService;

	@Autowired
	InvoiceItemsRepository invoiceItemsRepository;

	@Autowired
	InvoiceItemsMapper invoiceItemsMapper;

	@Autowired
	Environment environment;

	@Autowired
	InvoicePaymentHistoryRepository invoicePaymentHistoryRepository;

	@Autowired
	InvoicePaymentHistoryMapper invoicePaymentHistoryMapper;

	@Autowired
	BankDetailsRepository bankDetailsRepository;

	@Autowired
	BankDetailsMapper bankDetailsMapper;

	@Autowired
	CashAccountRepository cashAccountRepository;

	@Autowired
	AccountEntryService accountEntryService;

	@Override
	public String generateInvoiceCode() {

		String prefix = "INV";
		String year = String.valueOf(LocalDate.now().getYear());

		String lastInvoiceCode = invoiceEntityRepository.getLastInvoiceNo();

		int nextNumber = 1;

		if (lastInvoiceCode != null && lastInvoiceCode.startsWith(prefix + year)) {
			String numberPart = lastInvoiceCode.substring((prefix + year).length());
			nextNumber = Integer.parseInt(numberPart) + 1;
		}

		return prefix + year + String.format("%05d", nextNumber);
	}

	@Override
	@Transactional
	public SuperAdminInvoiceResponseDto addOrUpdateInvoice(InvoiceRequestDTO request, Long invoiceId)
			throws IOException {
		InvoiceEntity invoiceEntity = null;

		UserMasterEntity customer = userMasterRepository.findByIdAndIsDeleteFalse(request.getCustomerId())
				.orElseThrow(() -> new RuntimeException("Customer not found with id : " + request.getCustomerId()));

		UserMasterEntity salesPerson = userMasterRepository.findByIdAndIsDeleteFalse(request.getSalesPersonId())
				.orElseThrow(
						() -> new RuntimeException("Sales Person not found with id : " + request.getSalesPersonId()));

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		Optional<InvoiceEntity> op = invoiceEntityRepository
				.findByInvoiceCodeAndIsDeleteFalse(request.getInvoiceCode());

		if (invoiceId == -1) {
			if (op.isPresent()) {
				throw new RuntimeException("Invoice code already exist.");
			}
			invoiceEntity = invoiceMapper.requestToEntity(request);
			invoiceEntity.setInvoiceDate(LocalDate.parse(request.getInvoice_date(), formatter));
			invoiceEntity.setDueDate(LocalDate.parse(request.getDue_date(), formatter));
		} else {
			invoiceEntity = invoiceEntityRepository.findByInvoiceIdAndIsDeleteFalse(invoiceId)
					.orElseThrow(() -> new RuntimeException("Invoice not found with id : " + invoiceId));

			if (op.isPresent() && op.get().getInvoiceId() != invoiceEntity.getInvoiceId()) {
				throw new RuntimeException("Invoice code already exist.");
			}

			Optional<InvoicePaymentHistoryEntity> lastPaymentOptional = invoicePaymentHistoryRepository
					.findLatestByInvoiceId(invoiceEntity.getInvoiceId());
			
			InvoicePaymentHistoryEntity lastPayment = null;
			BigDecimal totalPaidAmount = BigDecimal.ZERO;
			
			if(lastPaymentOptional.isPresent()) {
				lastPayment = lastPaymentOptional.get();
				totalPaidAmount = invoicePaymentHistoryRepository.getTotalPaidAmountByInvoice(invoiceEntity);
				BigDecimal totalAmount = request.getTotalAmount();
				BigDecimal remainingAmount = totalAmount.subtract(totalPaidAmount);
				
				lastPayment.setDueAmount(remainingAmount);
				
				if(lastPayment.getDueAmount().compareTo(BigDecimal.ZERO) == 0) {
					lastPayment.setStatus("confirm");
				}else if(lastPayment.getDueAmount().compareTo(BigDecimal.ZERO) > 0) {
					lastPayment.setStatus("pending");
				}else {
					lastPayment.setStatus("advanced");
				}
				
				invoicePaymentHistoryRepository.save(lastPayment);
			}
			
			invoiceEntity.setCustomerId(customer.getId());
			invoiceEntity.setBillingAddress(request.getBillingAddress());
			invoiceEntity.setShippingAddress(request.getShippingAddress());
			invoiceEntity.setGstNumber(request.getGstNumber());
			invoiceEntity.setInvoiceCode(request.getInvoiceCode());
			invoiceEntity.setInvoiceDate(LocalDate.parse(request.getInvoice_date(), formatter));
			invoiceEntity.setTerms(request.getTerms());
			invoiceEntity.setDueDate(LocalDate.parse(request.getDue_date(), formatter));
			invoiceEntity.setSalesPersonId(salesPerson.getId());
			invoiceEntity.setCustomerNotes(request.getCustomerNotes());
			invoiceEntity.setSubTotal(request.getSubTotal());
			invoiceEntity.setDiscountPer(request.getDiscountPer());
			invoiceEntity.setDiscountAmount(request.getDiscountAmount());
			invoiceEntity.setTaxType(request.getTaxType());
			invoiceEntity.setGstPercent(request.getGstPercent());
			invoiceEntity.setGstAmount(request.getGstAmount());
			invoiceEntity.setAdjust_amount(request.getAdjust_amount());
			invoiceEntity.setTotalAmount(request.getTotalAmount());
			invoiceEntity.setTnc(request.getTnc());
			invoiceEntity.setUpdatedAt(commonService.getCurrentDateTime());
		}

		InvoiceEntity savedInvoice = invoiceEntityRepository.save(invoiceEntity);

		if (invoiceId != -1) {
			List<InvoiceItemsEntity> existingItems = invoiceItemsRepository.findByInvoiceAndIsDeleteFalse(savedInvoice);

			Set<Long> requestItemIds = request.getInvoiceItems().stream().filter(i -> i.getInvoiceItemId() != -1)
					.map(i -> i.getInvoiceItemId()).collect(Collectors.toSet());

			List<InvoiceItemsEntity> itemsToDelete = existingItems.stream()
					.filter(item -> !requestItemIds.contains(item.getInvoiceItemId())).collect(Collectors.toList());

			// SOFT DELETE
			itemsToDelete.forEach(item -> {
				item.setIsDelete(true);
				item.setUpdatedAt(LocalDateTime.now());
			});

			invoiceItemsRepository.saveAll(itemsToDelete);
		}

		// Save invoice items
		List<InvoiceItemsEntity> itemsEntities = request.getInvoiceItems().stream().map(item -> {
			InvoiceItemsEntity itemEntity;
			if (item.getInvoiceItemId() == -1) {
				itemEntity = invoiceItemsMapper.requestToEntity(item);
				itemEntity.setInvoice(savedInvoice);
			} else {
				itemEntity = invoiceItemsRepository.findById(item.getInvoiceItemId()).orElseThrow(
						() -> new RuntimeException("Invoice Item not found with id : " + item.getInvoiceItemId()));
				itemEntity.setPlanHistoryId(item.getPlanHistoryId());
				itemEntity.setItemName(item.getItemName());
				itemEntity.setQty(item.getQty());
				itemEntity.setRate(item.getRate());
				itemEntity.setAmount(item.getAmount());
				itemEntity.setDescription(item.getDescription());
				itemEntity.setHsnCode(item.getHsnCode());
				itemEntity.setTaxPercent(item.getTaxPercent());
				itemEntity.setTaxAmount(item.getTaxAmount());
				itemEntity.setUpdatedAt(LocalDateTime.now());
			}
			itemEntity = invoiceItemsRepository.save(itemEntity);

			return itemEntity;
		}).collect(Collectors.toList());

		invoiceItemsRepository.saveAll(itemsEntities);

		if (request.getDoc() != null && !request.getDoc().isEmpty()) {
			userFileService.storeFile(1l, ModuleName.SUPERADMIN_INVOICE.toString(), savedInvoice.getInvoiceId(),
					FileType.DOC.toString(), request.getDoc());
		}

		// Prepare and return response DTO
		SuperAdminInvoiceResponseDto response = invoiceMapper.toResponse(invoiceEntity);
		response.setItems(
				itemsEntities.stream().map(invoiceItemsMapper::entityToResponse).collect(Collectors.toList()));

		return response;
	}

	@Override
	public AllInvoiceResponseDto getAllAdminInvoice(String startDate, String endDate, Long planId, Long customerId) {

		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

		LocalDateTime stDate = (startDate != null && !startDate.isEmpty())
				? LocalDate.parse(startDate, dateFormatter).atStartOfDay()
				: null;

		LocalDateTime eDate = (endDate != null && !endDate.isEmpty())
				? LocalDate.parse(endDate, dateFormatter).atTime(23, 59, 59)
				: null;
		
		LocalDate sDate = (startDate != null && !startDate.isEmpty())
				? LocalDate.parse(startDate, dateFormatter)
				: null;
		
		LocalDate edDate = (endDate != null && !endDate.isEmpty())
				? LocalDate.parse(endDate, dateFormatter)
				: null;


		long totalPaidInvoices = 0;
		long totalUnPaidInvoices = 0;
		long totalPendingInvoices = 0;
		
		/* %%%%%%%%%% This Month Data %%%%%%%%%% */
		List<Object[]> results = invoiceEntityRepository.getAllInvoiceFullData(stDate, eDate, planId, customerId);
		List<SuperAdminInvoiceResponseDto> allInvoices = mapInvoiceFullData(results, dateFormatter, dateTimeFormatter);
		
		Long totalInvoiceThisMonth = (long) allInvoices.size();
		BigDecimal totalAmount = BigDecimal.ZERO;
		String baseUrl = environment.getProperty("app.image.url");
		for (SuperAdminInvoiceResponseDto dto : allInvoices) {
			if (dto.getDocPath() != null) {
				dto.setDocPath(baseUrl + dto.getDocPath());
			}
			
//			if (dto.getStatus().equalsIgnoreCase("pending")) {
//				totalPendingInvoices++;
//		    } else if (dto.getStatus().equalsIgnoreCase("confirm") || dto.getStatus().equalsIgnoreCase("advanced")) {
//		    	totalPaidInvoices++;
//		    } else {
//		    	totalUnPaidInvoices++;
//		    }
			
			totalAmount = dto.getTotalAmount() != null ? totalAmount.add(dto.getTotalAmount()) : BigDecimal.ZERO;
		}

		BigDecimal totalPaidAmountThisMonth = allInvoices.stream().flatMap(invoice -> invoice.getPayments().stream())
				.filter(payment -> payment.getPayment_date() != null)
		        .filter(payment -> {
		            if(stDate != null && eDate != null) {
			        	LocalDate paymentDate = LocalDate.parse(payment.getPayment_date(), dateFormatter);
			            return !paymentDate.isBefore(stDate.toLocalDate())
			                    && !paymentDate.isAfter(eDate.toLocalDate());
		            }else {
		            	return true;
		            }
		        })
				.map(InvoicePaymentHistoryResponseDto::getAmount).filter(Objects::nonNull)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		BigDecimal totalUnPaidAmountThisMonth = totalAmount.subtract(totalPaidAmountThisMonth);
		

		/* %%%%%%%%%% Prev All Month Data %%%%%%%%%% */
		Long totalUnpaidInvoiceCountPrevAllMonth = 0L;
		BigDecimal totalUnpaidInvoiceAmountPrevAllMonth = BigDecimal.ZERO;
		BigDecimal totalPaidAmountPrevAllMonth = BigDecimal.ZERO;
		BigDecimal totalRemainingAmountPrevAllMonth = BigDecimal.ZERO;

		Object[] prevAllMonthData = invoicePaymentHistoryRepository.getPrevAllMonthData(startDate, endDate).get(0);
		if (prevAllMonthData != null) {

		    totalUnpaidInvoiceCountPrevAllMonth =
		            prevAllMonthData[0] != null
		                    ? ((Number) prevAllMonthData[0]).longValue()
		                    : 0L;

		    totalUnpaidInvoiceAmountPrevAllMonth =
		            prevAllMonthData[1] != null
		                    ? BigDecimal.valueOf(((Number) prevAllMonthData[1]).doubleValue())
		                    : BigDecimal.ZERO;

		    totalPaidAmountPrevAllMonth =
		            prevAllMonthData[2] != null
		                    ? BigDecimal.valueOf(((Number) prevAllMonthData[2]).doubleValue())
		                    : BigDecimal.ZERO;

		    totalRemainingAmountPrevAllMonth =
		            prevAllMonthData[3] != null
		                    ? BigDecimal.valueOf(((Number) prevAllMonthData[3]).doubleValue())
		                    : BigDecimal.ZERO;
		}
		
		List<CurrentMonthPaidInvoicesResponseDto> paidInvoiceThisMonth = invoiceEntityRepository.getPaidInvoicesWithAmount(sDate, edDate);

		Long totalInvoices = totalInvoiceThisMonth + totalUnpaidInvoiceCountPrevAllMonth;
		BigDecimal totalPaidAmount = totalPaidAmountThisMonth.add(totalPaidAmountPrevAllMonth);
		BigDecimal totalUnpaidAmount = totalAmount.add(totalUnpaidInvoiceAmountPrevAllMonth);
		BigDecimal totalRemainingAmount = totalUnpaidAmount.subtract(totalPaidAmount);
		
//		Long totalUnpaidInvoices = totalUnPaidInvoices + totalUnpaidInvoiceCountPrevAllMonth;
		
		List<Object[]> data = invoicePaymentHistoryRepository.getTotalData(sDate);
		BigDecimal prevAllMonthInvoiceAmount = BigDecimal.ZERO;
		BigDecimal prevAllMonthInvoicePaidAmount = BigDecimal.ZERO;
		BigDecimal prevAllMonthInvoiceUnpaidAmount = BigDecimal.ZERO;
	
		if(data != null && !data.isEmpty()) {
			Object[] prevAllData = data.get(0);
			
			
			if (prevAllData != null) {
	
				prevAllMonthInvoiceAmount =
						prevAllData[0] != null
			                    ? BigDecimal.valueOf(((Number) prevAllMonthData[1]).doubleValue())
			                    : BigDecimal.ZERO;
	
				prevAllMonthInvoicePaidAmount =
						prevAllData[0] != null
			                    ? BigDecimal.valueOf(((Number) prevAllMonthData[2]).doubleValue())
			                    : BigDecimal.ZERO;
	
				prevAllMonthInvoiceUnpaidAmount =
						prevAllData[0] != null
			                    ? BigDecimal.valueOf(((Number) prevAllMonthData[3]).doubleValue())
			                    : BigDecimal.ZERO;
			}
		}
		
		AllInvoiceResponseDto response = new AllInvoiceResponseDto();
		response.setInvoices(allInvoices);
		response.setTotalInvoicesThisMonth(totalInvoiceThisMonth);
		response.setTotalAmountThisMonth(totalAmount);
		response.setTotalPaidAmountThisMonth(totalPaidAmountThisMonth);
		response.setTotalUnPaidAmountThisMonth(totalUnPaidAmountThisMonth);
//		response.setTotalPaidInvoiceCountThisMonth(totalPaidInvoices);
//		response.setTotalUnpaidInvoiceCountThisMonth(totalUnPaidInvoices);
//		response.setTotalPendingInvoiceCountThisMonth(totalPendingInvoices);
		response.setTotalPaidInvoiceThisMonth(
				paidInvoiceThisMonth.stream()
						.map(invoiceDto -> {
							SuperAdminInvoiceResponseDto dto =
									invoiceMapper.toResponse(invoiceDto.getInvoice());

							dto.setTotalPaidAmount(invoiceDto.getPaidAmount());

							return dto;
						})
						.collect(Collectors.toList()));
		response.setTotalUnpaidInvoiceCountPrevAllMonth(totalUnpaidInvoiceCountPrevAllMonth);
		response.setTotalUnpaidInvoiceAmountPrevAllMonth(totalUnpaidInvoiceAmountPrevAllMonth);
		response.setTotalPaidAmountPrevAllMonth(totalPaidAmountPrevAllMonth);
		response.setTotalRemainingAmountPrevAllMonth(totalRemainingAmountPrevAllMonth);
//		response.setTotalUnpaidInvoiceCount(totalUnpaidInvoices);
		response.setTotalInvoiceCount(totalInvoices);
		response.setTotalInvoiceAmount(totalUnpaidAmount);
		response.setTotalPaidAmount(totalPaidAmount);
		response.setTotalUnpaidAmount(totalRemainingAmount);
		
		return response;
	}

	@Override
	public SuperAdminInvoiceResponseDto getAdminInvoiceById(Long invoiceId) {
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

		InvoiceEntity invoice = invoiceEntityRepository.findByInvoiceIdAndIsDeleteFalse(invoiceId)
				.orElseThrow(() -> new RuntimeException("Invoice not found with id: " + invoiceId));

		SuperAdminInvoiceResponseDto response = invoiceMapper.toResponse(invoice);

		UserMasterEntity customer = userMasterRepository.findById(invoice.getCustomerId())
				.orElseThrow(() -> new RuntimeException("Customer not found with id : " + invoice.getCustomerId()));

		UserMasterEntity salesPerson = userMasterRepository.findById(invoice.getSalesPersonId()).orElseThrow(
				() -> new RuntimeException("Sales Person not found with id : " + invoice.getSalesPersonId()));

		String status = invoicePaymentHistoryRepository.getInvoiceStatus(invoiceId).stream().findFirst()
				.orElse("pending");

		List<InvoiceItemsEntity> items = invoiceItemsRepository.findByInvoiceAndIsDeleteFalse(invoice);

		List<InvoicePaymentHistoryEntity> payments = invoicePaymentHistoryRepository
				.findAllByInvoiceAndIsDeleteFalse(invoice);

		BigDecimal totalPaidAmount = payments.stream().map(InvoicePaymentHistoryEntity::getAmount)
				.filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
		BigDecimal totalUnpaidAmount = invoice.getTotalAmount().subtract(totalPaidAmount);

		response.setCustomerName(customer.getFirstName() + " " + customer.getLastName());
		response.setSalesPersonName(salesPerson.getFirstName() + " " + salesPerson.getLastName());
		response.setItems(items.stream().map(invoiceItemsMapper::entityToResponse).collect(Collectors.toList()));
		response.setInvoiceDate(
				invoice.getInvoiceDate() != null ? invoice.getInvoiceDate().format(dateFormatter) : null);
		response.setDueDate(invoice.getDueDate() != null ? invoice.getDueDate().format(dateFormatter) : null);
		response.setCreatedAt(invoice.getCreatedAt() != null ? invoice.getCreatedAt().format(dateTimeFormatter) : null);
		response.setUpdatedAt(invoice.getUpdatedAt() != null ? invoice.getUpdatedAt().format(dateTimeFormatter) : null);
		response.setPayments(
				payments.stream().map(invoicePaymentHistoryMapper::entityToResponse).collect(Collectors.toList()));
		response.setTotalPaidAmount(totalPaidAmount);
		response.setTotalUnpaidAmount(totalUnpaidAmount);

		response.setStatus(status);

		return response;
	}

	@Override
	public Boolean deleteInvoiceById(Long invoiceId) {
		InvoiceEntity invoiceEntity = invoiceEntityRepository.findByInvoiceIdAndIsDeleteFalse(invoiceId)
				.orElseThrow(() -> new RuntimeException("Invoice not found with id : " + invoiceId));

		invoiceEntity.setIsDelete(true);

		invoiceEntityRepository.save(invoiceEntity);

		return true;
	}

	@Override
	@Transactional
	public InvoicePaymentHistoryResponseDto recordPayment(InvoicePaymentHistoryRequestDto request,
			Long invoicePaymentHistoryId) {

		InvoiceEntity invoice = invoiceEntityRepository.findByInvoiceIdAndIsDeleteFalse(request.getInvoiceId())
				.orElseThrow(() -> new RuntimeException("Invoice not found with id : " + request.getInvoiceId()));

		CashAccountEntity cashAccountEntity = null;
		BankDetailsEntity bankDetailEntity = null;
		BigDecimal newBalance = BigDecimal.ZERO;

		if (AccountType.CASH == request.getAccountType()) {
			cashAccountEntity = cashAccountRepository.findByIdAndIsDeleteFalse(request.getCashTypeId()).orElseThrow(
					() -> new RuntimeException("Cash account not found with id : " + request.getCashTypeId()));
		} else {
			bankDetailEntity = bankDetailsRepository.findByIdAndIsDeleteFalse(request.getBankAccountId()).orElseThrow(
					() -> new RuntimeException("Bank account not found with id : " + request.getBankAccountId()));
		}

		InvoicePaymentHistoryEntity entity;
		BigDecimal oldAmount = BigDecimal.ZERO;

		if (invoicePaymentHistoryId == -1) {
			entity = invoicePaymentHistoryMapper.requestToEntity(request);
			entity.setInvoice(invoice);

			newBalance = AccountType.CASH == request.getAccountType()
					? cashAccountEntity.getCurrentBalance().add(request.getAmount())
					: bankDetailEntity.getCurrentBalance().add(request.getAmount());
		} else {
			entity = invoicePaymentHistoryRepository.findByInvoicePaymentIdAndIsDeleteFalse(invoicePaymentHistoryId)
					.orElseThrow(() -> new RuntimeException(
							"Invoice payment not found with id : " + invoicePaymentHistoryId));

			oldAmount = entity.getAmount();

			entity.setTransactionId(request.getTransactionId());
			entity.setChequeNo(request.getChequeNo());
			entity.setAmount(request.getAmount());
			entity.setDueAmount(request.getDueAmount());
			entity.setPaymentMode(request.getPaymentMode());

			newBalance = AccountType.CASH == request.getAccountType()
					? cashAccountEntity.getCurrentBalance().subtract(oldAmount).add(request.getAmount())
					: bankDetailEntity.getCurrentBalance().subtract(oldAmount).add(request.getAmount());
		}

		if (AccountType.CASH == request.getAccountType()) {
			entity.setCashType(cashAccountEntity);
			entity.setBankAccount(null);
			cashAccountEntity.setCurrentBalance(newBalance);
			cashAccountRepository.save(cashAccountEntity);
		} else {
			entity.setBankAccount(bankDetailEntity);
			entity.setCashType(null);
			bankDetailEntity.setCurrentBalance(newBalance);
			bankDetailsRepository.save(bankDetailEntity);
		}

		entity.setPaymentDate(LocalDate.parse(request.getPayment_date(), DateTimeFormatter.ofPattern("dd/MM/yyyy")));

		BigDecimal totalPaidAmount = invoicePaymentHistoryRepository.getTotalPaidAmountByInvoice(invoice);

		totalPaidAmount = totalPaidAmount.subtract(oldAmount);
		totalPaidAmount = totalPaidAmount.add(request.getAmount());

		if (invoice.getTotalAmount().compareTo(totalPaidAmount) == 0) {
			entity.setStatus("confirm");
		} else if (invoice.getTotalAmount().compareTo(totalPaidAmount) > 0) {
			entity.setStatus("pending");
		} else {
//			throw new RuntimeException("Paid amount exceeds invoice total.");
			entity.setStatus("advanced");
		}

		entity = invoicePaymentHistoryRepository.save(entity);

		InvoicePaymentHistoryResponseDto response = invoicePaymentHistoryMapper.entityToResponse(entity);
		response.setInvoiceId(invoice.getInvoiceId());
		response.setInvoiceCode(invoice.getInvoiceCode());
		response.setPayment_date(entity.getPaymentDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

		return response;
	}

	@Override
	public InvoiceWisePaymentHistoryResponseDto getPaymentHistoryByInvoiceId(Long invoiceId) {
		InvoiceWisePaymentHistoryResponseDto response = new InvoiceWisePaymentHistoryResponseDto();

		InvoiceEntity invoice = invoiceEntityRepository.findByInvoiceIdAndIsDeleteFalse(invoiceId)
				.orElseThrow(() -> new RuntimeException("Invoice not found with id : " + invoiceId));

		List<InvoicePaymentHistoryEntity> paymentHistory = invoicePaymentHistoryRepository
				.findAllByInvoiceAndIsDeleteFalse(invoice);

		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a");

		List<InvoicePaymentHistoryResponseDto> payments = paymentHistory.stream().map(payment -> {
			InvoicePaymentHistoryResponseDto res = new InvoicePaymentHistoryResponseDto();
			res.setInvoicePaymentHistoryId(payment.getInvoicePaymentId());
			res.setInvoiceId(invoice.getInvoiceId());
			res.setInvoiceCode(invoice.getInvoiceCode());
			res.setBankDetails(bankDetailsMapper.entityToResponse(payment.getBankAccount()));
			res.setTransactionId(payment.getTransactionId());
			res.setPayment_date(payment.getPaymentDate().format(dateFormat));
			res.setChequeNo(payment.getChequeNo());
			res.setAmount(payment.getAmount());
			res.setDueAmount(payment.getDueAmount());
			res.setPaymentMode(payment.getPaymentMode());
			res.setStatus(payment.getStatus());
			res.setIsDelete(payment.getIsDelete());
			res.setCreatedAt(payment.getCreatedAt().format(dateTimeFormat));
			res.setUpdatedAt(payment.getUpdatedAt() != null ? payment.getUpdatedAt().format(dateTimeFormat) : null);

			return res;
		}).collect(Collectors.toList());

		BigDecimal totalAmount = invoice.getTotalAmount();
		BigDecimal paidAmount = paymentHistory.stream().map(InvoicePaymentHistoryEntity::getAmount)
				.filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
		BigDecimal unPaidAmount = totalAmount.subtract(paidAmount);

		response.setPayments(payments);
		response.setTotalAmount(totalAmount);
		response.setTotalPaidAmount(paidAmount);
		response.setTotalUnPaidAmount(unPaidAmount);

		return response;
	}

	@Override
	public InvoicePaymentHistoryResponseDto getPaymentDetailByInvoicePaymentHistoryId(Long invoicePaymentHistoryId) {
		InvoicePaymentHistoryEntity payment = invoicePaymentHistoryRepository
				.findByInvoicePaymentIdAndIsDeleteFalse(invoicePaymentHistoryId).orElseThrow(
						() -> new RuntimeException("Invoice payment not found with id : " + invoicePaymentHistoryId));

		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a");

		InvoicePaymentHistoryResponseDto res = new InvoicePaymentHistoryResponseDto();
		res.setInvoicePaymentHistoryId(payment.getInvoicePaymentId());
		res.setInvoiceId(payment.getInvoice().getInvoiceId());
		res.setInvoiceCode(payment.getInvoice().getInvoiceCode());
		res.setBankDetails(bankDetailsMapper.entityToResponse(payment.getBankAccount()));
		res.setTransactionId(payment.getTransactionId());
		res.setPayment_date(payment.getPaymentDate().format(dateFormat));
		res.setChequeNo(payment.getChequeNo());
		res.setAmount(payment.getAmount());
		res.setDueAmount(payment.getDueAmount());
		res.setPaymentMode(payment.getPaymentMode());
		res.setStatus(payment.getStatus());
		res.setIsDelete(payment.getIsDelete());
		res.setCreatedAt(payment.getCreatedAt().format(dateTimeFormat));
		res.setUpdatedAt(payment.getUpdatedAt() != null ? payment.getUpdatedAt().format(dateTimeFormat) : null);

		return res;
	}

	@Override
	@Transactional
	public Boolean deleteInvoicePaymentDetailsByHistoryId(Long invoicePaymentHistoryId, Long invoiceId) {

	    InvoicePaymentHistoryEntity invoicePayment = invoicePaymentHistoryRepository
	            .findByInvoicePaymentIdAndIsDeleteFalse(invoicePaymentHistoryId)
	            .orElseThrow(() -> new RuntimeException(
	                    "Payment not found with id : " + invoicePaymentHistoryId));

	    InvoiceEntity invoice = invoiceEntityRepository
	            .findByInvoiceIdAndIsDeleteFalse(invoiceId)
	            .orElseThrow(() -> new RuntimeException(
	                    "Invoice not found with id : " + invoiceId));

	    List<InvoicePaymentHistoryEntity> payments = invoicePaymentHistoryRepository
	            .findAllByInvoice_InvoiceIdAndIsDeleteFalseOrderByPaymentDateAsc(invoiceId);

	    BigDecimal cumulativePaid = payments.stream()
	            .filter(p -> p.getPaymentDate().isBefore(invoicePayment.getPaymentDate())
	                    && !p.getInvoicePaymentId().equals(invoicePaymentHistoryId))
	            .map(InvoicePaymentHistoryEntity::getAmount)
	            .reduce(BigDecimal.ZERO, BigDecimal::add);

	    final BigDecimal[] runningCumulative = {cumulativePaid}; // effectively final for lambda

	    List<InvoicePaymentHistoryEntity> toBeSaved = payments.stream()
	            .filter(p -> p.getInvoicePaymentId().equals(invoicePaymentHistoryId)  // deleted payment
	                    || !p.getPaymentDate().isBefore(invoicePayment.getPaymentDate())) // payments after deleted date
	            .map(p -> {
	                if (p.getInvoicePaymentId().equals(invoicePaymentHistoryId)) {
	                    // Soft delete
	                    p.setIsDelete(true);
	                    p.setUpdatedAt(LocalDateTime.now());
	                } else {
	                    // Recalculate
	                    runningCumulative[0] = runningCumulative[0].add(p.getAmount());
	                    BigDecimal remainingDue = invoice.getTotalAmount().subtract(runningCumulative[0]);
	                    int cmp = remainingDue.compareTo(BigDecimal.ZERO);

	                    p.setDueAmount(remainingDue.abs());
	                    p.setStatus(cmp > 0 ? "PENDING" : cmp == 0 ? "PAID" : "ADVANCED");
	                    p.setUpdatedAt(LocalDateTime.now());
	                }
	                return p;
	            })
	            .collect(Collectors.toList());

	    invoicePaymentHistoryRepository.saveAll(toBeSaved);

	    return true;
	}

	private List<SuperAdminInvoiceResponseDto> mapInvoiceFullData(List<Object[]> results,
			DateTimeFormatter dateFormatter, DateTimeFormatter dateTimeFormatter) {

		Map<Long, SuperAdminInvoiceResponseDto> map = new LinkedHashMap<>();
		Map<Long, Set<Long>> itemTracker = new HashMap<>();
		Map<Long, Set<Long>> paymentTracker = new HashMap<>();

		for (Object[] row : results) {

			Long invoiceId = ((Number) row[0]).longValue();

			SuperAdminInvoiceResponseDto dto = map.get(invoiceId);

			if (dto == null) {

				dto = new SuperAdminInvoiceResponseDto();

				dto.setInvoiceId(invoiceId);
				dto.setCustomerId(row[1] != null ? ((Number) row[1]).longValue() : null);
				dto.setCustomerName((String) row[2]);
				dto.setSalesPersonId(row[3] != null ? ((Number) row[3]).longValue() : null);
				dto.setSalesPersonName((String) row[4]);
				dto.setBillingName((String) row[5]);
				dto.setBillingAddress((String) row[6]);
				dto.setShippingAddress((String) row[7]);
				dto.setGstNumber((String) row[8]);
				dto.setInvoiceCode((String) row[9]);

				dto.setInvoiceDate(toLocalDate(row[10]).format(dateFormatter));

				dto.setTerms((String) row[11]);

				dto.setDueDate(toLocalDate(row[12]).format(dateFormatter));

				dto.setCustomerNotes((String) row[13]);

				dto.setSubTotal((BigDecimal) row[14]);
				dto.setDiscountPer((BigDecimal) row[15]);
				dto.setDiscountAmount((BigDecimal) row[16]);

				dto.setTaxType(row[17] != null ? TaxType.valueOf((String) row[17]) : null);

				dto.setGstPercent((BigDecimal) row[18]);
				dto.setGstAmount((BigDecimal) row[19]);
				dto.setAdjust_amount((BigDecimal) row[20]);
				dto.setTotalAmount((BigDecimal) row[21]);
				dto.setTnc((String) row[22]);
				dto.setDocPath((String) row[23]);

				dto.setCreatedAt(
						row[24] != null ? ((java.sql.Timestamp) row[24]).toLocalDateTime().format(dateTimeFormatter)
								: null);

				dto.setUpdatedAt(
						row[25] != null ? ((java.sql.Timestamp) row[25]).toLocalDateTime().format(dateTimeFormatter)
								: null);

				dto.setIsDelete(row[26] != null ? (Boolean) row[26] : null);

				dto.setItems(new ArrayList<>());
				dto.setPayments(new ArrayList<>());

				map.put(invoiceId, dto);
				itemTracker.put(invoiceId, new HashSet<>());
				paymentTracker.put(invoiceId, new HashSet<>());
			}

			// ---------- ITEM MAPPING ----------
			if (row[27] != null) {

				Long itemId = ((Number) row[27]).longValue();
				Set<Long> itemIds = itemTracker.get(invoiceId);

				if (!itemIds.contains(itemId)) {

					SuperAdminInvoiceItemsResponseDto itemDto = new SuperAdminInvoiceItemsResponseDto();

					itemDto.setInvoiceItemId(itemId);
					itemDto.setPlanHistoryId(row[28] != null ? ((Number) row[28]).longValue() : null);
					itemDto.setItemName((String) row[29]);
					itemDto.setQty((BigDecimal) row[30]);
					itemDto.setRate((BigDecimal) row[31]);
					itemDto.setAmount((BigDecimal) row[32]);
					itemDto.setDescription((String) row[33]);

					itemDto.setCreatedAt(row[34] != null ? ((java.sql.Timestamp) row[34]).toLocalDateTime() : null);

					itemDto.setHsnCode((String) row[36]);
					itemDto.setTaxPercent((BigDecimal) row[37]);
					itemDto.setTaxAmount((BigDecimal) row[58]);

					dto.getItems().add(itemDto);
					itemIds.add(itemId);
				}
			}

			// ---------- PAYMENT MAPPING ----------
			if (row[38] != null) {

				Long paymentId = ((Number) row[38]).longValue();
				Set<Long> paymentIds = paymentTracker.get(invoiceId);

				if (!paymentIds.contains(paymentId)) {

					InvoicePaymentHistoryResponseDto paymentDto = new InvoicePaymentHistoryResponseDto();

					paymentDto.setInvoicePaymentHistoryId(paymentId);
					paymentDto.setTransactionId((String) row[39]);

					paymentDto.setPayment_date(toLocalDate(row[40]).format(dateFormatter));

					paymentDto.setChequeNo((String) row[41]);
					paymentDto.setAmount((BigDecimal) row[42]);
					paymentDto.setDueAmount((BigDecimal) row[43]);

					paymentDto.setPaymentMode(row[44] != null ? PaymentMode.valueOf((String) row[44]) : null);

					paymentDto.setStatus((String) row[45]);
					paymentDto.setIsDelete(row[46] != null ? (Boolean) row[46] : null);

					paymentDto.setCreatedAt(
							row[47] != null ? ((Timestamp) row[47]).toLocalDateTime().format(dateTimeFormatter) : null);

					paymentDto.setUpdatedAt(
							row[48] != null ? ((Timestamp) row[48]).toLocalDateTime().format(dateTimeFormatter) : null);

					BankDetailsResponseDto bankDetails = new BankDetailsResponseDto();
					bankDetails.setId(row[49] != null ? ((Number) row[49]).longValue() : null);
					bankDetails.setBankName(row[50] != null ? (String) row[50] : null);
					bankDetails.setBranchName(row[51] != null ? (String) row[51] : null);
					bankDetails.setAccountHolderName(row[52] != null ? (String) row[52] : null);
					bankDetails.setAccountNo(row[53] != null ? (String) row[53] : null);
					bankDetails.setIfscCode(row[54] != null ? (String) row[54] : null);
					bankDetails.setIsPrimary(row[55] != null ? (Boolean) row[55] : null);
					bankDetails.setUpiId(row[56] != null ? (String) row[56] : null);
					bankDetails.setUserId(row[57] != null ? ((Number) row[57]).longValue() : null);

					paymentDto.setInvoiceId(invoiceId);
					paymentDto.setInvoiceCode((String) row[9]);
					paymentDto.setBankDetails(bankDetails);

					dto.getPayments().add(paymentDto);

					paymentIds.add(paymentId);

					dto.setStatus((String) row[45]);
					dto.setTotalPaidAmount(dto.getTotalPaidAmount().add(paymentDto.getAmount()));
				}
			} else {
				dto.setStatus("pending");
				dto.setTotalPaidAmount(BigDecimal.ZERO);
			}

			dto.setTotalUnpaidAmount(dto.getTotalAmount().subtract(dto.getTotalPaidAmount()));
		}

		return new ArrayList<>(map.values());
	}

	private LocalDate toLocalDate(Object obj) {
		if (obj == null)
			return null;

		if (obj instanceof java.sql.Date)
			return ((java.sql.Date) obj).toLocalDate();

		if (obj instanceof java.sql.Timestamp)
			return ((java.sql.Timestamp) obj).toLocalDateTime().toLocalDate();

		return null;
	}

	private AccountEntryRequestDto prepareAccountEntry(Long userId, String voucherNo, EntryType entryType,
			PaymentMode paymentMode, Long cashTypeId, Long bankAccountId, String date, String partyName, BigDecimal amount,
			String notes, Long invoiceId, String referenceNo) {
		AccountEntryRequestDto request = new AccountEntryRequestDto();

		request.setUserId(userId);
		request.setVoucherNo(voucherNo);
		request.setEntryType(entryType);
		request.setPaymentMode(paymentMode);
		request.setCashTypeId(cashTypeId);
		request.setBankAccountId(bankAccountId);
		request.setDate(date);
		request.setAccountContactName(partyName);
		request.setAmount(amount);
		request.setNotes(notes);
		request.setInvoiceId(invoiceId);
		request.setReferenceNo(referenceNo);

		return request;
	}
	
}
