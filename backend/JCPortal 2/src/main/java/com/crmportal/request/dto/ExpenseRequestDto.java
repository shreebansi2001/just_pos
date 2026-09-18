package com.crmportal.request.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseRequestDto {

	private Long id;
	
	private String title;
	
	private Long fromCityId;

	private Long toCityId;
	
	private String fromDate;
	
	private String toDate;
	
	private String dueDate;
	
	private String paidDate;
	
	private String expenseType;
	
	private String remark;
	
	private BigDecimal totalAmount;

	private Long accountContactId;
	
	private Long userId;

	private Long adminId;
	
	private List<ExpenseDetailRequestDto> detailRequestDtos;
}
