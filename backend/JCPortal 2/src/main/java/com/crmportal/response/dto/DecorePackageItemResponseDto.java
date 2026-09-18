package com.crmportal.response.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DecorePackageItemResponseDto {

	private Long id;
	private Long decoreItemId;
	private String itemName;
	private Integer itemSortOrder;
	private BigDecimal itemPrice;
}