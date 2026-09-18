package com.crmportal.response.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.crmportal.entity.TripExpensePayoutHistoryEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseResponseDto {
	private Long id;

	private String title;

	private Long fromCityId;

	private Long toCityId;

	private String fromDate;

	private String toDate;

	private String dueDate;

	private String paidDate;

	private BigDecimal totalAmount;
	
	private BigDecimal payoutAmount;

	private BigDecimal remaingAmount;

	private String expenseType;
	
	private String remark;

	private Long accountContactId;
	
	private String accountContactName;
	
	private Long userId;
	
	private String status;
	
	private List<ExpenseDetailResponseDto> detailRequestDtos;
	
	private List<TripExpensePayoutHistoryResponseDto> payoutHistory; 
}
