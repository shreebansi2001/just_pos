package com.crmportal.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DatewiseStoreIssueDetailsReportResponseDto {

	private Long rawMaterialId;

    private String rawMaterialName;

    private Long unitId;
    
    private String unitName;

    private double qty;
    
    private float price;
    
    private float total;
}
