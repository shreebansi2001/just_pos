package com.crmportal.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmportal.entity.AccountEntryEntity;
import com.crmportal.entity.InvoiceEntity;
import com.crmportal.entity.VendorPaymentEntity;
import com.crmportal.repository.AccountEntryRepository;
import com.crmportal.repository.InvoiceEntityRepository;
import com.crmportal.repository.InvoicePaymentHistoryRepository;
import com.crmportal.repository.SalesInvoiceRepository;
import com.crmportal.repository.VendorPaymentRepository;
import com.crmportal.response.dto.AllExpensesResponseDto;
import com.crmportal.response.dto.IncomeListResponseDto;
import com.crmportal.response.dto.ProfitAndLossResponseDto;
import com.crmportal.service.ExpenseService;
import com.crmportal.service.IncomeService;
import com.crmportal.service.ProfitAndLossService;

@Service
public class ProfitAndLossServiceImpl implements ProfitAndLossService {

	@Autowired
	InvoiceEntityRepository invoiceRepository;
	
	@Autowired
	ExpenseService expenseService;
	
	@Autowired
	InvoicePaymentHistoryRepository invoicePaymentHistoryRepository;
	
	@Autowired
	AccountEntryRepository accountEntryRepository;
	
	@Autowired
	IncomeService incomeService;
	
	@Autowired
	VendorPaymentRepository vendorPaymentRepository;
	
	@Autowired
	SalesInvoiceRepository salesInvoiceRepository;
	
	@Override
	public ProfitAndLossResponseDto getProfitAndLossData(String startDate, String endDate, Long userId) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		LocalDate start = startDate != null ? LocalDate.parse(startDate, formatter) : null;
		LocalDate end = endDate != null ? LocalDate.parse(endDate, formatter) : null;

		LocalDateTime stDate = (startDate != null && !startDate.isEmpty())
				? LocalDate.parse(startDate, formatter).atStartOfDay()
				: null;

		LocalDateTime eDate = (endDate != null && !endDate.isEmpty())
				? LocalDate.parse(endDate, formatter).atTime(23, 59, 59)
				: null;
		
		ProfitAndLossResponseDto response = new ProfitAndLossResponseDto();
		
		BigDecimal totalInvoicePaidAmount = BigDecimal.ZERO;
		BigDecimal totalAccountEntryAmount = BigDecimal.ZERO;
		
		if(userId == 1) {
			totalInvoicePaidAmount = Optional.ofNullable(invoicePaymentHistoryRepository.getTotalPaidAmount(stDate, eDate, null, null))
					.orElse(BigDecimal.ZERO);
			
			List<AccountEntryEntity> accountEntries = accountEntryRepository.findAllByUserIdAndDateBetweenAndIsDeleteFalse(userId, start, end);
			totalAccountEntryAmount = accountEntries.stream().filter(Objects::nonNull).map(AccountEntryEntity::getAmount)
					.reduce(BigDecimal.ZERO, BigDecimal::add);
		}else {
			totalInvoicePaidAmount = Optional.ofNullable(salesInvoiceRepository.getTotalPaidAmount(stDate, eDate, userId))
					.orElse(BigDecimal.ZERO);
			
			List<VendorPaymentEntity> vendorPayments = vendorPaymentRepository.getReceivedPaymentList(userId, start, end);
			totalAccountEntryAmount = vendorPayments.stream().filter(Objects::nonNull).map(VendorPaymentEntity::getReceivedAmount)
					.reduce(BigDecimal.ZERO, BigDecimal::add);
		}

		BigDecimal totalRevenue = totalInvoicePaidAmount.add(totalAccountEntryAmount);
		response.setTotalRevenue(totalRevenue);
		
//		List<IncomeListResponseDto> list = invoicePaymentHistoryRepository.getIncomeList(start, end, null, null);
		
		List<Object[]> rows = accountEntryRepository.getAllIncomeData(null, null, null, null, null, startDate, endDate, userId);
		List<IncomeListResponseDto> list = incomeService.mapToIncomeResponseDto(rows);
		
		AllExpensesResponseDto dto = expenseService.getAllExpenses(userId, startDate, endDate, -1l);
		
		BigDecimal totalExpense = dto.getTotalExpenses();
		BigDecimal netProfit = totalRevenue.subtract(totalExpense);
		
		BigDecimal profitMargin = BigDecimal.ZERO;
		if (totalRevenue != null && totalRevenue.compareTo(BigDecimal.ZERO) > 0) {
		    profitMargin = netProfit
		            .divide(totalRevenue, 4, RoundingMode.HALF_UP)
		            .multiply(BigDecimal.valueOf(100));
		}
		
		response.setPayments(list);
		response.setTotalExpence(totalExpense);
		response.setNetProfit(netProfit);
		response.setProfitMargin(profitMargin);
		
		return response;
	}
}
