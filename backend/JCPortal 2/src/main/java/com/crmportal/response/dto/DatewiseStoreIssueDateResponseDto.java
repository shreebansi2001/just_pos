package com.crmportal.response.dto;

import java.time.LocalDate;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DatewiseStoreIssueDateResponseDto {

	private LocalDate issueDate;

//    private List<DatewiseStoreIssueDetailsReportResponseDto> details;
	
	private List<DatewiseStoreIssueReportResponseDto> categories;
	
}
