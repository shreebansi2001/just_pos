package com.crmportal.request.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DecorePackageItemRequestDto {

	private Long decoreItemId;
	private Integer itemSortOrder;
	private BigDecimal itemPrice;
}