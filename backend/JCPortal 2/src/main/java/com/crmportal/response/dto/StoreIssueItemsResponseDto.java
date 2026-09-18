package com.crmportal.response.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StoreIssueItemsResponseDto {

	private Long storeIssueId;
	private String poCode;
	private String poDate;
	private String remarks;
	private String voucherNo;
	
	private Long stockTypeId;
	private String stockTypeName;
	
	private String status;
	
	private Long crId;
	private String crCode;
	
	private Long kitchenTypeId;
	private String kitchenType;
	
	private Long storeIssueDetailId;
	private Long rawItemId;
	private String rawItemName;
	private BigDecimal qty;
	private Long unitId;
	private String unitName;
	private BigDecimal avgRate;
	private BigDecimal totalAmount;
}