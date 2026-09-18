package com.crmportal.response.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuAllocationAgencyResponseDto {

	private Long contactId;
	private String contactName;
	private String contactNameHindi;
	private String contactNameGujarati;
	private Integer totalPax;
	private BigDecimal totalPrice;
	
	//Chef Labour
	private String type;
	private Integer TotalCounterQty;
	private Integer TotalCounterPrice;
	private Integer TotalHelperQty;
	private Integer TotalHeplerPrice;
	
	//inside
	private String number;
	private String remarks;
	
	//OutSide
	private Integer totalQty;
	
	private BigDecimal totalShiftTransPrice;
	private List<MenuAllocationItemsResponseDto> allocationItems;
	
}
