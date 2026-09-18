package com.crmportal.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crmportal.entity.SalesInvoiceEntity;
import com.crmportal.enums.EntryType;
import com.crmportal.repository.SalesInvoiceRepository;
import com.crmportal.request.dto.EventInvoicePaymentResponseDto;
import com.crmportal.response.dto.AllExpensesResponseDto;
import com.crmportal.response.dto.AllInvoiceResponseDto;
import com.crmportal.response.dto.EventInvoiceResponseDto;
import com.crmportal.response.dto.ExpenseResponseDto;
import com.crmportal.response.dto.ExpenseTypeAllData;
import com.crmportal.response.dto.LocalDateAmount;
import com.crmportal.response.dto.MemberAllExpensesResponseDto;
import com.crmportal.response.dto.MonthWiseDataDto;
import com.crmportal.response.dto.OfficeExpenseResponseDto;
import com.crmportal.response.dto.PaymentDashboardDto;
import com.crmportal.response.dto.ReceiptDashboardDto;
import com.crmportal.response.dto.SuperAdminInvoiceResponseDto;
import com.crmportal.service.EventInvoiceService;
import com.crmportal.service.ExpenseService;
import com.crmportal.service.InvoiceService;
import com.crmportal.service.ReceivablePayableService;

@Service
public class ReceivablePayableServiceImpl implements ReceivablePayableService {

	@Autowired
	InvoiceService invoiceService;
	
	@Autowired
	ExpenseService expenseService;

	@Autowired
	EventInvoiceService eventInvoiceService;
	
	@Autowired
	SalesInvoiceRepository salesInvoiceRepository;
	
	@Override
	public Map<String, Object> getByMonthWise(EntryType entryType, String startDate, String endDate, Long userId) {

	    Map<String, Object> response = new HashMap<>();

	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	    
	    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");

	    if (entryType == EntryType.RECEIPT) {

	    	List<MonthWiseDataDto> monthList = new ArrayList<>();

	        BigDecimal totalAmount = BigDecimal.ZERO;
	        BigDecimal totalPaid = BigDecimal.ZERO;
	        BigDecimal totalPending = BigDecimal.ZERO;

	        long totalInvoiceCount = 0;
	        long totalPaidCount = 0;
	        long totalPendingCount = 0;
	        
	        Map<YearMonth, MonthWiseDataDto> monthMap = new HashMap<>();

	        if(userId == 1) {
	        	 AllInvoiceResponseDto data =
	        	            invoiceService.getAllAdminInvoice(startDate, endDate, -1L, -1L);

	        	    List<SuperAdminInvoiceResponseDto> invoices = data.getInvoices();
	        	    
	        	    for (SuperAdminInvoiceResponseDto inv : invoices) {
	        	    	
	        	        if (inv == null || inv.getInvoiceDate() == null) continue;

	        	        YearMonth ym = YearMonth.from(LocalDate.parse(inv.getInvoiceDate(), formatter));

	        	        MonthWiseDataDto dto = monthMap.computeIfAbsent(ym, k -> {
	        	            MonthWiseDataDto m = new MonthWiseDataDto();
	        	            m.setMonth(formatMonth(k));
	        	            m.setTotalAmount(BigDecimal.ZERO);
	        	            m.setPaidAmount(BigDecimal.ZERO);
	        	            m.setPendingAmount(BigDecimal.ZERO);
	        	            return m;
	        	        });

	        	        BigDecimal invTotal = nvl(inv.getTotalAmount());
	        	        BigDecimal invPaid = nvl(inv.getTotalPaidAmount());
	        	        BigDecimal invPending = nvl(inv.getTotalUnpaidAmount());

	        	        dto.setTotalAmount(dto.getTotalAmount().add(invTotal));
	        	        dto.setPaidAmount(dto.getPaidAmount().add(invPaid));
	        	        dto.setPendingAmount(dto.getPendingAmount().add(invPending));
	        	        
	        	        dto.setStatus(
        	    	            dto.getPendingAmount().compareTo(BigDecimal.ZERO) > 0
        	    	                    ? "PENDING"
        	    	                    : "PAID"
        	    	    );

	        	        totalAmount = totalAmount.add(invTotal);
	        	        totalPaid = totalPaid.add(invPaid);
	        	        totalPending = totalPending.add(invPending);

	        	        totalInvoiceCount++;

	        	        if (invPending.compareTo(BigDecimal.ZERO) > 0) {
	        	            totalPendingCount++;
	        	        } else {
	        	            totalPaidCount++;
	        	        }
	        	    }
	        }else {
	        	List<EventInvoiceResponseDto> invoices =
	                    eventInvoiceService.getEventInvoiceByUserIdAndDateWise(userId, startDate, endDate,null,null);

	            Set<Long> eventIds = new HashSet<>();
	            for (EventInvoiceResponseDto inv : invoices) {
	                if (inv != null && inv.getEvent() != null) {
	                    eventIds.add(inv.getEvent().getId());
	                }
	            }
	            
	            Map<Long, BigDecimal> paidAmountMap = new HashMap<>();

	            if (!eventIds.isEmpty()) {
	                List<SalesInvoiceEntity> salesList =
	                        salesInvoiceRepository.findByUserIdAndEventIdInAndIsDeleteFalse(userId, new ArrayList<>(eventIds));

	                for (SalesInvoiceEntity s : salesList) {
	                    if (s == null || s.getEvent() == null) continue;

	                    Long eventId = s.getEvent().getId();

	                    BigDecimal paid = s.getTotalAmount() != null
	                            ? s.getTotalAmount()
	                            : BigDecimal.ZERO;

	                    paidAmountMap.merge(eventId, paid, BigDecimal::add);
	                }
	            }

	            for (EventInvoiceResponseDto inv : invoices) {

	                if (inv == null || inv.getCreatedAt() == null) continue;

	                YearMonth ym = YearMonth.from(LocalDate.parse(inv.getCreatedAt(), formatter));

	                MonthWiseDataDto dto = monthMap.computeIfAbsent(ym, k -> {
	                    MonthWiseDataDto m = new MonthWiseDataDto();
	                    m.setMonth(formatMonth(k));
	                    m.setTotalAmount(BigDecimal.ZERO);
	                    m.setPaidAmount(BigDecimal.ZERO);
	                    m.setPendingAmount(BigDecimal.ZERO);
	                    return m;
	                });

	                BigDecimal total = nvl(inv.getTotalAmount());

	                BigDecimal paid = paidAmountMap.getOrDefault(
	                        inv.getEvent().getId(),
	                        BigDecimal.ZERO
	                );

	                BigDecimal pending = total.subtract(paid);

	                dto.setTotalAmount(dto.getTotalAmount().add(total));
	                dto.setPaidAmount(dto.getPaidAmount().add(paid));
	                dto.setPendingAmount(dto.getPendingAmount().add(pending));

	                dto.setStatus(
    	    	            dto.getPendingAmount().compareTo(BigDecimal.ZERO) > 0
    	    	                    ? "PENDING"
    	    	                    : "PAID"
    	    	    );
	                
	                totalAmount = totalAmount.add(total);
	                totalPaid = totalPaid.add(paid);
	                totalPending = totalPending.add(pending);

	                totalInvoiceCount++;

	                if (pending.compareTo(BigDecimal.ZERO) < 0) {
	                    totalPendingCount++;
	                } else {
	                    totalPaidCount++;
	                }
	            }
	        }

	        monthList.addAll(monthMap.values());
	        
	        monthList.sort(Comparator.comparing(MonthWiseDataDto::getMonth));

	        ReceiptDashboardDto dto = new ReceiptDashboardDto();
	        dto.setTotalInvoiceCount(totalInvoiceCount);
	        dto.setTotalInvoiceAmount(totalAmount);
	        dto.setTotalPaidInvoiceCount(totalPaidCount);
	        dto.setTotalPaidInvoiceAmount(totalPaid);
	        dto.setTotalPendingInvoiceCount(totalPendingCount);
	        dto.setTotalPendingInvoiceAmount(totalPending);
	        dto.setTotalPendingAmount(totalPending);
	        dto.setMonths(monthList);

	        response.put("Receipt", dto);

	    } else {

	    	AllExpensesResponseDto data =
	    	        expenseService.getAllExpenses(userId, startDate, endDate, -1l);

	    	List<LocalDateAmount> allExpenses = new ArrayList<>();

	    	if (data.getTripExpenses() != null) {

	    	    for (ExpenseResponseDto trip : data.getTripExpenses()) {
	    	        allExpenses.add(new LocalDateAmount(
	    	        		parseDate(trip.getFromDate(), formatter),
	    	                nvl(trip.getTotalAmount()),
	    	                nvl(trip.getPayoutAmount()),
	    	                nvl(trip.getRemaingAmount())
	    	        ));
	    	    }
	    	}

	    	if (data.getExpenses() != null) {
	    		for (ExpenseTypeAllData type : data.getExpenses()) {
	    	        if (type.getExpenses() != null) {
	    	            for (OfficeExpenseResponseDto office : type.getExpenses()) {
	    	                allExpenses.add(new LocalDateAmount(
	    	                        parseDate(office.getExpenseDate(), formatter),
	    	                        nvl(office.getExpenseAmount()),
	    	                        nvl(office.getPayoutAmount()),
	    	                        nvl(office.getRemaingAmount())
	    	                ));
	    	            }
	    	        }
	    	    }
	    	}

	    	Map<YearMonth, List<LocalDateAmount>> grouped =
	    	        allExpenses.stream()
	    	                .filter(e -> e.getDate() != null)
	    	                .collect(Collectors.groupingBy(
	    	                        e -> YearMonth.from(e.getDate()),
	    	                        TreeMap::new,
	    	                        Collectors.toList()
	    	                ));

	    	List<MonthWiseDataDto> monthList = new ArrayList<>();

	    	BigDecimal total = BigDecimal.ZERO;
	    	BigDecimal paid = BigDecimal.ZERO;
	    	BigDecimal unpaid = BigDecimal.ZERO;

	    	for (Map.Entry<YearMonth, List<LocalDateAmount>> entry : grouped.entrySet()) {

	    	    BigDecimal monthTotal = BigDecimal.ZERO;
	    	    BigDecimal monthPaid = BigDecimal.ZERO;
	    	    BigDecimal monthPending = BigDecimal.ZERO;

	    	    for (LocalDateAmount item : entry.getValue()) {

	    	        monthTotal = monthTotal.add(nvl(item.getTotal()));
	    	        monthPaid = monthPaid.add(nvl(item.getPaid()));
	    	        monthPending = monthPending.add(nvl(item.getPending()));
	    	    }

	    	    MonthWiseDataDto monthDto = new MonthWiseDataDto();

	    	    monthDto.setMonth(formatMonth(entry.getKey()));
	    	    monthDto.setTotalAmount(monthTotal);
	    	    monthDto.setPaidAmount(monthPaid);
	    	    monthDto.setPendingAmount(monthPending);

	    	    monthDto.setStatus(
	    	            monthPending.compareTo(BigDecimal.ZERO) > 0
	    	                    ? "PENDING"
	    	                    : "PAID"
	    	    );

	    	    monthList.add(monthDto);

	    	    total = total.add(monthTotal);
	    	    paid = paid.add(monthPaid);
	    	    unpaid = unpaid.add(monthPending);
	    	}

	    	PaymentDashboardDto dto = new PaymentDashboardDto();

	    	dto.setTotalAmount(total);
	    	dto.setPaidAmount(paid);
	    	dto.setUnpaidAmount(unpaid);
	    	dto.setMonths(monthList);

	    	response.put("Payment", dto);
	    }

	    return response;
	}
	
	private BigDecimal nvl(BigDecimal val) {
	    return val != null ? val : BigDecimal.ZERO;
	}

	private LocalDate parseDate(String date, DateTimeFormatter formatter) {
	    return date != null ? LocalDate.parse(date, formatter) : LocalDate.now();
	}

	private String formatMonth(YearMonth ym) {
	    return ym.getMonth().name() + " " + ym.getYear(); // e.g. APRIL 2026
	}
}
