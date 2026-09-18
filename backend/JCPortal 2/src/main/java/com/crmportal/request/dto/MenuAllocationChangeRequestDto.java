package com.crmportal.request.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuAllocationChangeRequestDto {
	
	private Long menuItemId;
	private String allocationType;
	private BigDecimal basePrice;
	private Long counterNo;
	private Long helperNo;
	private BigDecimal pricePerLabour;
	private BigDecimal pricePerHelper;
	private BigDecimal totalPrice;
	private Long quantityPer100Person;
	private Boolean selectChefLabourAgency;
	private Boolean selectOutsideAgency;
	private Boolean selectInsideAgency;
	private Long partyId;
	private Long unitId;
	private Long userId;
	private Long contactCategoryId;
	private String number;
	private String remarks;
}
