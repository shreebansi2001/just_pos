package com.crmportal.request.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CostingReportAgencyResponseDto {

	private String functionName;
	private String itemName;
	private Long partyId;
	private String agencyName;
	private String chefLabourPrice;
	private BigDecimal outsourcePrice;
	private BigDecimal labourPrice;
	private Boolean isChefLabour;
	private Boolean isOutsource;
	private String quantity;
	private String unitName;
	private BigDecimal totalChefLabourPrice;
	private BigDecimal totalOutsourcePrice;
	private BigDecimal totalLabourPrice;
	private BigDecimal transRate;
}
