package com.crmportal.response.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuAllocationItemsResponseDto {

	private Long itemId;
	private String itemName;
	private String itemNameHindi;
	private String itemNameGujarati;
	private Integer pax;
	private String notes;
	
	private String remarks;
	private String remarksHindi;
	private String remarksGujarati;
	private String serviceType;
	
	// Outside
	private Long unitId;
	private String unitName;
	private String unitNameHindi;
	private String unitNameGujarati;
	private Integer price;
	private Integer qty;

	//	Chef Labour
	private Integer counterQty;
	private Integer counterPrice;
	private Integer helperQty;
	private Integer helperPrice;
	
	// inside
	private String number;
	
	private String place;
	
	private Long categoryId;
	private String categoryName;
	private String categoryNameHindi;
	private String categoryNameGujarati;
	
}
