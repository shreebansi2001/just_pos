package com.crmportal.response.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuPreparationItemForMenuAllocationResponseDto {

	
	private String menuItemName;
	private String menuItemNameHindi;
	private String menuItemNameGujarati;
	private BigDecimal totalPrice;
	private String typeName;
	private Boolean isFromNewTable;
	private Long menuItemId;
}
