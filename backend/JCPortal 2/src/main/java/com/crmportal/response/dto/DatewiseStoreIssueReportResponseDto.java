package com.crmportal.response.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DatewiseStoreIssueReportResponseDto {

	private BigDecimal totalAmount;
	
    private Long rawMaterialCatId;
    
    private String rawMaterialCatName;
    
	private List<DatewiseStoreIssueDetailsReportResponseDto> details;
    
//    private List<DatewiseStoreIssueDateResponseDto> dates;
}
