package com.crmportal.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmportal.entity.AccountEntryEntity;
import com.crmportal.entity.InvoiceEntity;
import com.crmportal.entity.UserMasterEntity;
import com.crmportal.entity.VendorPaymentEntity;
import com.crmportal.enums.AccountType;
import com.crmportal.enums.EntryType;
import com.crmportal.enums.PaymentMode;
import com.crmportal.repository.AccountEntryRepository;
import com.crmportal.repository.EventInvoiceRepository;
import com.crmportal.repository.InvoiceEntityRepository;
import com.crmportal.repository.InvoicePaymentHistoryRepository;
import com.crmportal.repository.UserBasicDetailsMasterRepository;
import com.crmportal.repository.UserMasterRepository;
import com.crmportal.repository.VendorPaymentRepository;
import com.crmportal.response.dto.IncomeListResponseDto;
import com.crmportal.response.dto.IncomeResponseDto;
import com.crmportal.response.dto.InvoicePaymentDto;
import com.crmportal.response.dto.InvoicesDetailsResponseDto;
import com.crmportal.service.IncomeService;

@Service
public class IncomeServiceImpl implements IncomeService {

	@Autowired
	InvoicePaymentHistoryRepository invoicePaymentHistoryRepository;

	@Autowired
	InvoiceEntityRepository invoiceRepository;

	@Autowired
	UserBasicDetailsMasterRepository userBasicDetailsMasterRepository;
	
	@Autowired
	AccountEntryRepository accountEntryRepository;

	@Autowired
	UserMasterRepository userMasterRepository;
	
	@Autowired
	EventInvoiceRepository eventInvoiceRepository;
	
	@Autowired
	VendorPaymentRepository vendorPaymentRepository;
	
	@Override
	public IncomeResponseDto getIncome(String startDate, String endDate, AccountType accountType, PaymentMode paymentMode, Long bankAccountId,
			Long cashAccountId, Long typeId, Long userId) {
		UserMasterEntity user = userMasterRepository.findByIdAndIsDeleteFalse(userId)
				.orElseThrow(() -> new RuntimeException("User not found with id : " + userId));
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		LocalDate start = startDate != null ? LocalDate.parse(startDate, formatter) : null;
		LocalDate end = endDate != null ? LocalDate.parse(endDate, formatter) : null;

		LocalDateTime stDate = (startDate != null && !startDate.isEmpty())
				? LocalDate.parse(startDate, formatter).atStartOfDay()
				: null;

		LocalDateTime eDate = (endDate != null && !endDate.isEmpty())
				? LocalDate.parse(endDate, formatter).atTime(23, 59, 59)
				: null;

//		List<IncomeListResponseDto> list = invoicePaymentHistoryRepository.getIncomeList(start, end, paymentMode,
//				bankAccountId);

		List<Object[]> rows = accountEntryRepository.getAllIncomeData(accountType != null ? accountType.name() : null, paymentMode != null ? paymentMode.name() : null,
				cashAccountId, bankAccountId, typeId, startDate, endDate, userId);

		List<IncomeListResponseDto> list = mapToIncomeResponseDto(rows);

		IncomeResponseDto response = new IncomeResponseDto();
		response.setPayments(list);

		Map<PaymentMode, BigDecimal> modeMap = new HashMap<>();

		// Step 1: put 0 for all modes
		for (PaymentMode mode : PaymentMode.values()) {
			modeMap.put(mode, BigDecimal.ZERO);
		}

		List<Object[]> result = accountEntryRepository.getPaymentModeWiseAmount(start, end, userId);

		for (Object[] obj : result) {
			PaymentMode mode = PaymentMode.valueOf(obj[0].toString());
			BigDecimal amount = (BigDecimal) obj[1];
			modeMap.put(mode, amount);
		}

		response.setPaymentModeAmount(modeMap);

		// %%%%%%%%%%%%%%%%%%%%%% Invoices Data %%%%%%%%%%%%%%%%%%%%%%
		BigDecimal totalInvoiceAmountThisMonth = BigDecimal.ZERO;
		BigDecimal totalPaidInvoiceAmountThisMonth = BigDecimal.ZERO;
		long totalInvoiceCreatedThisMonth = 0;

		BigDecimal totalInvoiceAmountPrevAllMonth = BigDecimal.ZERO;
		BigDecimal totalPaidInvoiceAmountPrevAllMonth = BigDecimal.ZERO;
		long totalInvoiceCreatedPrevAllMonth = 0;
		
		List<Object[]> invoicesRows = invoiceRepository.getAllPrevInvoiceDetails(endDate, userId);
		List<InvoicesDetailsResponseDto> invoices = mapToInvoices(invoicesRows);
	
		for (InvoicesDetailsResponseDto invoice : invoices) {
		    if (invoice == null || invoice.getInvoiceDate() == null) {
		        continue;
		    }

		    boolean isCurrentMonth = !invoice.getInvoiceDate().isBefore(start) && !invoice.getInvoiceDate().isAfter(end);
		    boolean isPrevMonth = invoice.getInvoiceDate().isBefore(start);

		    BigDecimal invoiceAmount = invoice.getTotalAmount() != null ? invoice.getTotalAmount() : BigDecimal.ZERO;

		    BigDecimal paidAmount = BigDecimal.ZERO;

		    if (invoice.getPayments() != null) {
		        for (InvoicePaymentDto payment : invoice.getPayments()) {
		            if (payment != null && payment.getAmount() != null) {
		                paidAmount = paidAmount.add(payment.getAmount());
		            }
		        }
		    }

		    if (isCurrentMonth) {
		        totalInvoiceCreatedThisMonth++;
		        totalInvoiceAmountThisMonth = totalInvoiceAmountThisMonth.add(invoiceAmount);
		        totalPaidInvoiceAmountThisMonth = totalPaidInvoiceAmountThisMonth.add(paidAmount);
		    } else if (isPrevMonth) {
		        totalInvoiceCreatedPrevAllMonth++;
		        totalInvoiceAmountPrevAllMonth = totalInvoiceAmountPrevAllMonth.add(invoiceAmount);
		        totalPaidInvoiceAmountPrevAllMonth = totalPaidInvoiceAmountPrevAllMonth.add(paidAmount);
		    }
		}
		
		BigDecimal totalUnpaidInvoiceAmountThisMonth = totalInvoiceAmountThisMonth.subtract(totalPaidInvoiceAmountThisMonth);
		BigDecimal totalUnpaidInvoiceAmountPrevAllMonth = totalInvoiceAmountPrevAllMonth.subtract(totalPaidInvoiceAmountPrevAllMonth);

		response.setTotalInvoiceCreatedThisMonth(totalInvoiceCreatedThisMonth);
		response.setTotalInvoiceAmountThisMonth(totalInvoiceAmountThisMonth);
		response.setTotalInvoicePaidAmountThisMonth(totalPaidInvoiceAmountThisMonth);
		response.setTotalInvoiceUnpaidAmountThisMonth(totalUnpaidInvoiceAmountThisMonth);

		response.setTotalInvoiceCreatedPrevAllMonth(totalInvoiceCreatedPrevAllMonth);
		response.setTotalInvoiceAmountPrevAllMonth(totalInvoiceAmountPrevAllMonth);
		response.setTotalInvoicePaidAmountPrevAllMonth(totalPaidInvoiceAmountPrevAllMonth);
		response.setTotalInvoiceUnpaidAmountPrevAllMonth(totalUnpaidInvoiceAmountPrevAllMonth);

		
		// %%%%%%%%%%%%%%%%%%%%%% Account Entry %%%%%%%%%%%%%%%%%%%%%%
		BigDecimal totalAccountEntryAmount = BigDecimal.ZERO;
		BigDecimal totalCashAmount = BigDecimal.ZERO;
		BigDecimal totalBankAmount = BigDecimal.ZERO;

		long totalCashEntries = 0L;
		long totalBankEntries = 0L;
		long totalEntries = 0L;

		if (userId == 1) {
		    List<AccountEntryEntity> accountEntries = accountEntryRepository.findAllByUserIdAndDateBetweenAndIsDeleteFalse(userId, start, end);

		    totalEntries = accountEntries.size();

		    for (AccountEntryEntity entry : accountEntries) {
		        if (entry.getEntryType() == EntryType.RECEIPT) {
		            BigDecimal amount = entry.getAmount() != null ? entry.getAmount() : BigDecimal.ZERO;
		            totalAccountEntryAmount = totalAccountEntryAmount.add(amount);
		        }

		        if (entry.getAccountType() == AccountType.CASH) {
		            totalCashEntries++;
		            if (entry.getAmount() != null) {
		                totalCashAmount = totalCashAmount.add(entry.getAmount());
		            }
		        }

		        else if (entry.getAccountType() == AccountType.BANK) {
		            totalBankEntries++;
		            if (entry.getAmount() != null) {
		                totalBankAmount = totalBankAmount.add(entry.getAmount());
		            }
		        }
		    }

		} else {
		    List<VendorPaymentEntity> vendorPayments = vendorPaymentRepository.getReceivedPaymentList(userId, start, end);
		  
		    totalEntries = vendorPayments.size();

		    for (VendorPaymentEntity payment : vendorPayments) {
		        BigDecimal amount = payment.getReceivedAmount() != null ? payment.getReceivedAmount() : BigDecimal.ZERO;

		        totalAccountEntryAmount = totalAccountEntryAmount.add(amount);

		        if (payment.getPaymentMode() != null && payment.getPaymentMode().equalsIgnoreCase("CASH")) {
		            totalCashEntries++;
		            totalCashAmount = totalCashAmount.add(amount);
		        } else {
		            totalBankEntries++;
		            totalBankAmount = totalBankAmount.add(amount);
		        }
		    }
		}

		response.setTotalAccountEntry(totalEntries);
		response.setTotalAccountEntryAmount(totalAccountEntryAmount);

		response.setTotalCashAmount(totalCashAmount);
		response.setTotalCashEntry(totalCashEntries);

		response.setTotalBankAmount(totalBankAmount);
		response.setTotalBankEntry(totalBankEntries);
		
		BigDecimal totalRevenue = totalPaidInvoiceAmountThisMonth.add(totalAccountEntryAmount);
		response.setTotalRevenue(totalRevenue);
		
		BigDecimal totalAmount = totalInvoiceAmountThisMonth.add(totalAccountEntryAmount);
		response.setTotalAmount(totalAmount);

		if(userId == 1) {
			Long totalMember = userBasicDetailsMasterRepository.getActiveUserByRoleAndType(2l, "Member", false, true);
			response.setTotalActiveUsers(totalMember);
	
			Long totalNoInvoiceCreated = userBasicDetailsMasterRepository.countUsersWithoutInvoice();
			response.setTotalNoInvoiceCreated(totalNoInvoiceCreated);
		}
		
		return response;
	}
	
	@Override
	public List<IncomeListResponseDto> mapToIncomeResponseDto(List<Object[]> rows) {
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		List<IncomeListResponseDto> response = new ArrayList<>();
		
		IncomeListResponseDto dto = null;
		
		for(Object[] row : rows) {
			dto = new IncomeListResponseDto();
			dto.setClientName(row[0] != null ? (String)row[0] : null);
			dto.setRecordId(row[1] != null ? ((Number)row[1]).longValue() : null);
			dto.setVoucherNo(row[2] != null ? (String)row[2] : null);
			dto.setAmount(row[3] != null ? ((BigDecimal)row[3]) : null);
			dto.setDate(row[4] != null ? formateDate(row[4], dateFormatter) : null);
			String paymentMode = row[5] != null ? row[5].toString().toUpperCase() : null;
			
			if(paymentMode.equalsIgnoreCase("BANK TRANSFER") || paymentMode.equalsIgnoreCase("BANK_TRANSFER")) {
				dto.setPaymentMode(PaymentMode.BANK_TRANSFER);
			}else {
				dto.setPaymentMode(PaymentMode.valueOf(paymentMode));
			}
			
			response.add(dto);
		}
		
		return response;
	}
	
	private List<InvoicesDetailsResponseDto> mapToInvoices(List<Object[]> rows) {
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	    List<InvoicesDetailsResponseDto> response = new ArrayList<>();

	    Map<Long, InvoicesDetailsResponseDto> invoiceMap = new LinkedHashMap<>();

	    for (Object[] row : rows) {
	        Long invoiceId = row[0] != null ? ((Number) row[0]).longValue() : null;
	        InvoicesDetailsResponseDto dto = invoiceMap.get(invoiceId);
	        if (dto == null) {
	            dto = new InvoicesDetailsResponseDto();
	            dto.setInvoiceId(invoiceId);
	            dto.setInvoiceCode(row[1] != null ? row[1].toString() : null);
	            dto.setBillingName(row[2] != null ? row[2].toString() : null);
	            dto.setBillingAddress(row[3] != null ? row[3].toString() : null);
	            dto.setTotalAmount(row[4] != null ? (BigDecimal) row[4] : BigDecimal.ZERO);
	            dto.setCustomerId(row[5] != null ? ((Number) row[5]).longValue() : null);
	            dto.setCustomerName(row[6] != null ? row[6].toString() : null);
	            dto.setInvoiceDate(formateDate(row[14]));
	            dto.setPayments(new ArrayList<>());
	            invoiceMap.put(invoiceId, dto);
	        }

	        if (row[7] != null) {

	            InvoicePaymentDto payment = new InvoicePaymentDto();
	            payment.setInvoicePaymentId(((Number) row[7]).longValue());
	            payment.setAmount(row[8] != null ? (BigDecimal) row[8] : BigDecimal.ZERO);
	            payment.setPaymentDate(formateDate(row[9], dateFormatter));
	            payment.setPaymentMode(row[10] != null ? row[10].toString() : null);
	            payment.setBankAccountId(row[11] != null ? ((Number) row[11]).longValue() : null);
	            payment.setCashTypeId(row[12] != null ? ((Number) row[12]).longValue() : null);
	            payment.setStatus(row[13] != null ? row[13].toString() : null);
	            dto.getPayments().add(payment);
	        }
	    }

	    response.addAll(invoiceMap.values());

	    return response;
	}
	
	private List<InvoicesDetailsResponseDto> mapToAdminInvoices(List<Object[]> rows) {
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	    List<InvoicesDetailsResponseDto> response = new ArrayList<>();

	    Map<Long, InvoicesDetailsResponseDto> invoiceMap = new LinkedHashMap<>();

	    for (Object[] row : rows) {
	        Long invoiceId = row[0] != null ? ((Number) row[0]).longValue() : null;
	        InvoicesDetailsResponseDto dto = invoiceMap.get(invoiceId);
	        if (dto == null) {
	            dto = new InvoicesDetailsResponseDto();
	            dto.setInvoiceId(invoiceId);
	            dto.setInvoiceCode(row[1] != null ? row[1].toString() : null);
	            dto.setBillingName(row[2] != null ? row[2].toString() : null);
	            dto.setBillingAddress(row[3] != null ? row[3].toString() : null);
	            dto.setTotalAmount(row[4] != null ? (BigDecimal) row[4] : BigDecimal.ZERO);
	            dto.setCustomerId(row[5] != null ? ((Number) row[5]).longValue() : null);
	            dto.setCustomerName(row[6] != null ? row[6].toString() : null);
	            dto.setInvoiceDate(formateDate(row[14]));
	            dto.setPayments(new ArrayList<>());
	            invoiceMap.put(invoiceId, dto);
	        }

	        if (row[7] != null) {

	            InvoicePaymentDto payment = new InvoicePaymentDto();
	            payment.setInvoicePaymentId(((Number) row[7]).longValue());
	            payment.setAmount(row[8] != null ? (BigDecimal) row[8] : BigDecimal.ZERO);
	            payment.setPaymentDate(formateDate(row[9], dateFormatter));
	            payment.setPaymentMode(row[10] != null ? row[10].toString() : null);
	            payment.setBankAccountId(row[11] != null ? ((Number) row[11]).longValue() : null);
	            payment.setCashTypeId(row[12] != null ? ((Number) row[12]).longValue() : null);
	            payment.setStatus(row[13] != null ? row[13].toString() : null);
	            dto.getPayments().add(payment);
	        }
	    }

	    response.addAll(invoiceMap.values());

	    return response;
	}
	
	private String formateDate(Object date, DateTimeFormatter formatter) {
        if (date != null) {

            if (date instanceof java.sql.Timestamp) {
                    return ((java.sql.Timestamp) date)
                        .toLocalDateTime()
                        .toLocalDate()
                        .format(formatter);

            } else if (date instanceof java.sql.Date) {
            		return ((java.sql.Date) date)
                        .toLocalDate()
                        .format(formatter);

            } else if (date instanceof LocalDateTime) {
            		return ((LocalDateTime) date)
                        .toLocalDate()
                        .format(formatter);

            } else if (date instanceof LocalDate) {
            		return ((LocalDate) date).format(formatter);
            }
        }
        
        return null;
	}
	
	private LocalDate formateDate(Object date) {
        if (date != null) {

            if (date instanceof java.sql.Timestamp) {
                    return ((java.sql.Timestamp) date)
                        .toLocalDateTime()
                        .toLocalDate();

            } else if (date instanceof java.sql.Date) {
            		return ((java.sql.Date) date)
                        .toLocalDate();

            } else if (date instanceof LocalDateTime) {
            		return ((LocalDateTime) date)
                        .toLocalDate();

            } else if (date instanceof LocalDate) {
            		return ((LocalDate) date);
            }
        }
        
        return null;
	}
}
